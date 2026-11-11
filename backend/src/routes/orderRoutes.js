const express = require('express');
const router = express.Router();
const orderController = require('../controllers/orderController');
const { requireAuth } = require('../middleware/authMiddleware');
const validate = require('../middleware/validate');
const { createOrderSchema } = require('../validators/orderValidator');

// All order routes require authentication
router.use(requireAuth);

router.post('/', validate(createOrderSchema), orderController.handleCreateOrder);
router.get('/', orderController.handleGetUserOrders);
router.get('/:id', orderController.handleGetOrderById);

module.exports = router;
