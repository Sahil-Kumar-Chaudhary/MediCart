const { z } = require('zod');

const orderItemInputSchema = z.object({
  productId: z.string().trim().min(1, 'Product ID is required'),
  quantity: z
    .number()
    .int('Quantity must be an integer')
    .min(1, 'Minimum quantity is 1')
    .max(5, 'Maximum limit is 5 units per order')
});

const createOrderSchema = z.object({
  addressId: z.string().trim().min(1, 'Delivery address ID is required'),
  items: z
    .array(orderItemInputSchema)
    .min(1, 'Cart cannot be empty. At least one item is required.'),
  paymentMethod: z.enum(['UPI_DEMO', 'CASH_ON_DELIVERY', 'CARD_DEMO']),
  idempotencyKey: z.string().trim().min(1).optional()
});

module.exports = { createOrderSchema };
