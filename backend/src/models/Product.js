const mongoose = require('mongoose');

const productSchema = new mongoose.Schema(
  {
    productId: {
      type: String,
      required: true,
      unique: true,
      trim: true,
      index: true
    },
    name: {
      type: String,
      required: true,
      trim: true,
      index: true
    },
    genericName: {
      type: String,
      required: true,
      trim: true,
      index: true
    },
    description: {
      type: String,
      trim: true,
      default: ''
    },
    category: {
      type: String,
      required: true,
      trim: true,
      index: true
    },
    pricePaise: {
      type: Number,
      required: true,
      min: 0
    },
    mrpPaise: {
      type: Number,
      required: true,
      min: 0
    },
    packSize: {
      type: String,
      required: true,
      trim: true
    },
    stockCount: {
      type: Number,
      required: true,
      default: 0,
      min: 0
    },
    isAvailable: {
      type: Boolean,
      default: true
    },
    isExpired: {
      type: Boolean,
      default: false
    },
    expiryDate: {
      type: String,
      required: true,
      trim: true
    },
    imageUrl: {
      type: String,
      trim: true,
      default: ''
    }
  },
  {
    timestamps: { createdAt: true, updatedAt: false }
  }
);

// Compound text/search index for fast product lookup
productSchema.index({ name: 'text', genericName: 'text' });

module.exports = mongoose.model('Product', productSchema);
