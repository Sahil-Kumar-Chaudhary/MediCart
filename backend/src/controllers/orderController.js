const orderService = require('../services/orderService');
const { successResponse } = require('../utils/responseEnvelope');

const handleCreateOrder = async (req, res, next) => {
  try {
    const { order, isDuplicate } = await orderService.createOrder(req.user._id, req.body);
    const statusCode = isDuplicate ? 200 : 201;
    const message = isDuplicate
      ? 'Order already submitted (Idempotent response)'
      : 'Order request submitted and confirmed by backend';

    return successResponse(res, statusCode, message, order);
  } catch (error) {
    next(error);
  }
};

const handleGetUserOrders = async (req, res, next) => {
  try {
    const orders = await orderService.getUserOrders(req.user._id);
    return successResponse(res, 200, 'Orders retrieved successfully', orders);
  } catch (error) {
    next(error);
  }
};

const handleGetOrderById = async (req, res, next) => {
  try {
    const { id } = req.params;
    const order = await orderService.getOrderById(req.user._id, id);
    return successResponse(res, 200, 'Order details retrieved successfully', order);
  } catch (error) {
    next(error);
  }
};

module.exports = {
  handleCreateOrder,
  handleGetUserOrders,
  handleGetOrderById
};
