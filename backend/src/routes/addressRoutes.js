const express = require('express');
const router = express.Router();
const addressController = require('../controllers/addressController');
const { requireAuth } = require('../middleware/authMiddleware');
const validate = require('../middleware/validate');
const { createAddressSchema, updateAddressSchema } = require('../validators/addressValidator');

// All address routes require authentication
router.use(requireAuth);

router.get('/', addressController.handleGetAddresses);
router.post('/', validate(createAddressSchema), addressController.handleCreateAddress);
router.put('/:id', validate(updateAddressSchema), addressController.handleUpdateAddress);
router.delete('/:id', addressController.handleDeleteAddress);

module.exports = router;
