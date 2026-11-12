const mongoose = require('mongoose');

const otpSchema = new mongoose.Schema({
  phoneNumber: {
    type: String,
    required: true,
    index: true
  },
  otpHash: {
    type: String,
    required: true
  },
  purpose: {
    type: String,
    enum: ['LOGIN', 'REGISTRATION', 'PASSWORD_RESET'],
    default: 'LOGIN',
    index: true
  },
  metadata: {
    type: mongoose.Schema.Types.Mixed,
    default: null
  },
  expiresAt: {
    type: Date,
    required: true,
    index: { expireAfterSeconds: 0 }
  },
  attempts: {
    type: Number,
    default: 0
  }
});

// Compound index to ensure uniqueness per phone + purpose
otpSchema.index({ phoneNumber: 1, purpose: 1 });

module.exports = mongoose.model('Otp', otpSchema);
