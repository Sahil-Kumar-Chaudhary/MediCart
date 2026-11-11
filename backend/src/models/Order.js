const mongoose = require('mongoose');

const orderItemSnapshotSchema = new mongoose.Schema(
  {
    productId: { type: String, required: true },
    name: { type: String, required: true },
    genericName: { type: String, required: true },
    unitPricePaise: { type: Number, required: true },
    quantity: { type: Number, required: true, min: 1, max: 5 },
    totalPricePaise: { type: Number, required: true }
  },
  { _id: false }
);

const deliveryAddressSnapshotSchema = new mongoose.Schema(
  {
    recipientName: { type: String, required: true },
    phoneNumber: { type: String, required: true },
    houseNumber: { type: String, required: true },
    locality: { type: String, required: true },
    city: { type: String, required: true },
    state: { type: String, required: true },
    pinCode: { type: String, required: true },
    addressType: { type: String, default: 'HOME' }
  },
  { _id: false }
);

const orderSchema = new mongoose.Schema(
  {
    orderId: {
      type: String,
      required: true,
      unique: true,
      trim: true,
      index: true
    },
    userId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User',
      required: true,
      index: true
    },
    items: {
      type: [orderItemSnapshotSchema],
      required: true,
      validate: [val => val.length > 0, 'Order must contain at least one product item']
    },
    deliveryAddress: {
      type: deliveryAddressSnapshotSchema,
      required: true
    },
    subtotalPaise: {
      type: Number,
      required: true,
      min: 0
    },
    deliveryFeePaise: {
      type: Number,
      required: true,
      default: 3000 // ₹30.00 standard delivery
    },
    packagingFeePaise: {
      type: Number,
      required: true,
      default: 1400 // ₹14.00 standard packaging
    },
    totalAmountPaise: {
      type: Number,
      required: true,
      min: 0
    },
    paymentMethod: {
      type: String,
      enum: ['UPI_DEMO', 'CASH_ON_DELIVERY', 'CARD_DEMO'],
      required: true
    },
    paymentStatus: {
      type: String,
      enum: ['PENDING_SIMULATED', 'CONFIRMED_SIMULATED', 'FAILED_SIMULATED'],
      default: 'CONFIRMED_SIMULATED'
    },
    orderStatus: {
      type: String,
      enum: ['PROCESSING', 'PACKED', 'SHIPPED', 'DELIVERED', 'CANCELLED'],
      default: 'PROCESSING'
    },
    idempotencyKey: {
      type: String,
      trim: true,
      index: { unique: true, sparse: true }
    }
  },
  {
    timestamps: true
  }
);

module.exports = mongoose.model('Order', orderSchema);
