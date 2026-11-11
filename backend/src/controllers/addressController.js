const addressService = require('../services/addressService');
const { successResponse } = require('../utils/responseEnvelope');

const handleGetAddresses = async (req, res, next) => {
  try {
    const addresses = await addressService.getUserAddresses(req.user._id);
    return successResponse(res, 200, 'Addresses retrieved successfully', addresses);
  } catch (error) {
    next(error);
  }
};

const handleCreateAddress = async (req, res, next) => {
  try {
    const address = await addressService.createAddress(req.user._id, req.body);
    return successResponse(res, 201, 'Address added successfully', address);
  } catch (error) {
    next(error);
  }
};

const handleUpdateAddress = async (req, res, next) => {
  try {
    const { id } = req.params;
    const updated = await addressService.updateAddress(req.user._id, id, req.body);
    return successResponse(res, 200, 'Address updated successfully', updated);
  } catch (error) {
    next(error);
  }
};

const handleDeleteAddress = async (req, res, next) => {
  try {
    const { id } = req.params;
    await addressService.deleteAddress(req.user._id, id);
    return successResponse(res, 200, 'Address deleted successfully', { addressId: id });
  } catch (error) {
    next(error);
  }
};

module.exports = {
  handleGetAddresses,
  handleCreateAddress,
  handleUpdateAddress,
  handleDeleteAddress
};
