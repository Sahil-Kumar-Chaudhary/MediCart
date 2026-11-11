const productService = require('../services/productService');
const { successResponse, errorResponse } = require('../utils/responseEnvelope');

const handleGetCategories = async (req, res, next) => {
  try {
    const categories = await productService.getCategories();
    return successResponse(res, 200, 'Categories retrieved successfully', categories);
  } catch (error) {
    next(error);
  }
};

const handleGetProducts = async (req, res, next) => {
  try {
    const result = await productService.getProducts(req.query);
    return successResponse(res, 200, 'Products retrieved successfully', result);
  } catch (error) {
    next(error);
  }
};

const handleGetProductById = async (req, res, next) => {
  try {
    const { id } = req.params;
    const product = await productService.getProductById(id);
    if (!product) {
      return errorResponse(res, 404, `Product with ID '${id}' not found`, 'PRODUCT_NOT_FOUND');
    }
    return successResponse(res, 200, 'Product details retrieved successfully', product);
  } catch (error) {
    next(error);
  }
};

module.exports = {
  handleGetCategories,
  handleGetProducts,
  handleGetProductById
};
