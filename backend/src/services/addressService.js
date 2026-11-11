const Address = require('../models/Address');

const getUserAddresses = async (userId) => {
  return Address.find({ userId }).sort({ isDefault: -1, createdAt: -1 }).lean();
};

const createAddress = async (userId, data) => {
  if (data.isDefault) {
    await Address.updateMany({ userId }, { isDefault: false });
  }

  // If first address, make it default
  const count = await Address.countDocuments({ userId });
  const isDefault = data.isDefault !== undefined ? data.isDefault : count === 0;

  const address = await Address.create({
    ...data,
    userId,
    isDefault
  });
  return address;
};

const updateAddress = async (userId, addressId, data) => {
  const address = await Address.findById(addressId);
  if (!address) {
    const error = new Error('Address not found');
    error.statusCode = 404;
    error.errorCode = 'ADDRESS_NOT_FOUND';
    throw error;
  }

  if (address.userId.toString() !== userId.toString()) {
    const error = new Error('You do not have permission to modify this address');
    error.statusCode = 403;
    error.errorCode = 'FORBIDDEN';
    throw error;
  }

  if (data.isDefault) {
    await Address.updateMany({ userId, _id: { $ne: addressId } }, { isDefault: false });
  }

  Object.assign(address, data);
  await address.save();
  return address;
};

const deleteAddress = async (userId, addressId) => {
  const address = await Address.findById(addressId);
  if (!address) {
    const error = new Error('Address not found');
    error.statusCode = 404;
    error.errorCode = 'ADDRESS_NOT_FOUND';
    throw error;
  }

  if (address.userId.toString() !== userId.toString()) {
    const error = new Error('You do not have permission to delete this address');
    error.statusCode = 403;
    error.errorCode = 'FORBIDDEN';
    throw error;
  }

  await Address.deleteOne({ _id: addressId });
  return { deleted: true };
};

module.exports = {
  getUserAddresses,
  createAddress,
  updateAddress,
  deleteAddress
};
