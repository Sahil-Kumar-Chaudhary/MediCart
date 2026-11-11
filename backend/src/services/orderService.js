const Order = require('../models/Order');
const Product = require('../models/Product');
const Address = require('../models/Address');
const { generateOrderId } = require('../utils/orderIdGenerator');

const STANDARD_DELIVERY_FEE_PAISE = 3000;  // ₹30.00
const STANDARD_PACKAGING_FEE_PAISE = 1400; // ₹14.00

const createOrder = async (userId, orderData) => {
  const { addressId, items, paymentMethod, idempotencyKey } = orderData;

  // 1. Idempotency Check
  if (idempotencyKey && idempotencyKey.trim()) {
    const existingOrder = await Order.findOne({ idempotencyKey: idempotencyKey.trim() }).lean();
    if (existingOrder) {
      return { order: existingOrder, isDuplicate: true };
    }
  }

  // 2. Validate Cart is not empty
  if (!items || items.length === 0) {
    const error = new Error('Cannot submit an empty cart. Please add items to order.');
    error.statusCode = 400;
    error.errorCode = 'EMPTY_CART';
    throw error;
  }

  // 3. Verify Delivery Address belongs to User
  const address = await Address.findOne({ _id: addressId, userId });
  if (!address) {
    const error = new Error('Delivery address not found or does not belong to the user.');
    error.statusCode = 400;
    error.errorCode = 'INVALID_DELIVERY_ADDRESS';
    throw error;
  }

  // 4. Validate Each Item and Check Inventory
  const productIds = items.map(i => i.productId);
  const products = await Product.find({ productId: { $in: productIds } });
  const productMap = new Map(products.map(p => [p.productId, p]));

  const itemSnapshots = [];
  let subtotalPaise = 0;

  for (const item of items) {
    const product = productMap.get(item.productId);
    if (!product) {
      const error = new Error(`Product '${item.productId}' not found in pharmacy catalogue.`);
      error.statusCode = 404;
      error.errorCode = 'PRODUCT_NOT_FOUND';
      throw error;
    }

    // Check quantity limit
    if (item.quantity < 1 || item.quantity > 5) {
      const error = new Error(`Invalid quantity ${item.quantity} for '${product.name}'. Allowed range is 1 to 5 packs.`);
      error.statusCode = 400;
      error.errorCode = 'INVALID_QUANTITY';
      throw error;
    }

    // Check expiry
    if (product.isExpired) {
      const error = new Error(`Item '${product.name}' is from an expired batch and cannot be dispensed.`);
      error.statusCode = 400;
      error.errorCode = 'EXPIRED_PRODUCT';
      throw error;
    }

    // Check stock availability
    if (!product.isAvailable || product.stockCount < item.quantity) {
      const error = new Error(`Insufficient stock for '${product.name}'. Available: ${product.stockCount}, Requested: ${item.quantity}.`);
      error.statusCode = 400;
      error.errorCode = 'INSUFFICIENT_STOCK';
      throw error;
    }

    // Server-calculated pricing (Never trust client prices)
    const unitPricePaise = product.pricePaise;
    const itemTotalPaise = unitPricePaise * item.quantity;
    subtotalPaise += itemTotalPaise;

    itemSnapshots.push({
      productId: product.productId,
      name: product.name,
      genericName: product.genericName,
      unitPricePaise,
      quantity: item.quantity,
      totalPricePaise: itemTotalPaise
    });
  }

  // 5. Calculate Final Fees
  const deliveryFeePaise = STANDARD_DELIVERY_FEE_PAISE;
  const packagingFeePaise = STANDARD_PACKAGING_FEE_PAISE;
  const totalAmountPaise = subtotalPaise + deliveryFeePaise + packagingFeePaise;

  // 6. Address Snapshot
  const addressSnapshot = {
    recipientName: address.recipientName,
    phoneNumber: address.phoneNumber,
    houseNumber: address.houseNumber,
    locality: address.locality,
    city: address.city,
    state: address.state,
    pinCode: address.pinCode,
    addressType: address.addressType
  };

  // 7. Atomic Inventory Decrement
  // Decrement stock for all items atomically
  const decrementedProductIds = [];
  try {
    for (const item of items) {
      const updatedProduct = await Product.findOneAndUpdate(
        {
          productId: item.productId,
          stockCount: { $gte: item.quantity },
          isAvailable: true,
          isExpired: false
        },
        {
          $inc: { stockCount: -item.quantity }
        },
        { new: true }
      );

      if (!updatedProduct) {
        throw new Error(`Concurrent checkout conflict: insufficient stock for product ${item.productId}`);
      }

      decrementedProductIds.push({ productId: item.productId, quantity: item.quantity });

      // If stock reached 0, mark as not available
      if (updatedProduct.stockCount === 0) {
        await Product.updateOne({ productId: item.productId }, { isAvailable: false });
      }
    }

    // 8. Create Order Document
    const orderId = generateOrderId();
    const order = await Order.create({
      orderId,
      userId,
      items: itemSnapshots,
      deliveryAddress: addressSnapshot,
      subtotalPaise,
      deliveryFeePaise,
      packagingFeePaise,
      totalAmountPaise,
      paymentMethod,
      paymentStatus: 'CONFIRMED_SIMULATED',
      orderStatus: 'PROCESSING',
      idempotencyKey: idempotencyKey ? idempotencyKey.trim() : undefined
    });

    return { order, isDuplicate: false };
  } catch (error) {
    // Rollback stock decrements if order creation fails midway
    for (const dec of decrementedProductIds) {
      await Product.updateOne(
        { productId: dec.productId },
        { $inc: { stockCount: dec.quantity }, isAvailable: true }
      );
    }

    // If a concurrent request created the order with this idempotencyKey, return that existing order
    if (error.code === 11000 && idempotencyKey) {
      const existingOrder = await Order.findOne({ idempotencyKey: idempotencyKey.trim() });
      if (existingOrder) {
        return { order: existingOrder, isDuplicate: true };
      }
    }

    error.statusCode = error.statusCode || 400;
    error.errorCode = error.errorCode || 'ORDER_CREATION_FAILED';
    throw error;
  }
};

const getUserOrders = async (userId) => {
  return Order.find({ userId }).sort({ createdAt: -1 }).lean();
};

const getOrderById = async (userId, orderRefOrId) => {
  let order = await Order.findOne({ orderId: orderRefOrId }).lean();
  if (!order && orderRefOrId.match(/^[0-9a-fA-F]{24}$/)) {
    order = await Order.findById(orderRefOrId).lean();
  }

  if (!order) {
    const error = new Error(`Order '${orderRefOrId}' not found.`);
    error.statusCode = 404;
    error.errorCode = 'ORDER_NOT_FOUND';
    throw error;
  }

  // Enforce ownership
  if (order.userId.toString() !== userId.toString()) {
    const error = new Error('You do not have permission to view this order.');
    error.statusCode = 403;
    error.errorCode = 'FORBIDDEN';
    throw error;
  }

  return order;
};

module.exports = {
  createOrder,
  getUserOrders,
  getOrderById
};
