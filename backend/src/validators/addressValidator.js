const { z } = require('zod');

const createAddressSchema = z.object({
  recipientName: z.string().trim().min(2, 'Recipient name is required'),
  phoneNumber: z.string().trim().regex(/^\d{10}$/, 'Valid 10-digit mobile number is required'),
  houseNumber: z.string().trim().min(1, 'House/Flat number is required'),
  locality: z.string().trim().min(2, 'Street/Locality is required'),
  city: z.string().trim().min(2, 'City is required'),
  state: z.string().trim().min(2, 'State is required'),
  pinCode: z.string().trim().regex(/^[1-9][0-9]{5}$/, 'Valid 6-digit Indian PIN code is required'),
  addressType: z.enum(['HOME', 'WORK', 'OTHER']).default('HOME'),
  isDefault: z.boolean().optional()
});

const updateAddressSchema = createAddressSchema.partial();

module.exports = { createAddressSchema, updateAddressSchema };
