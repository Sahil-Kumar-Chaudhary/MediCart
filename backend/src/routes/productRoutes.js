const express = require('express');
const router = express.Router();
const productController = require('../controllers/productController');

router.get('/', productController.handleGetProducts);
router.get('/:id', productController.handleGetProductById);

module.exports = router;
