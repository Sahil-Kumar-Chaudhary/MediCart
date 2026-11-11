const crypto = require('crypto');

/**
 * Generates official MediCart Order ID format: #MC-2026-XXXX
 */
const generateOrderId = () => {
  const randomNum = Math.floor(1000 + Math.random() * 9000);
  const suffix = crypto.randomBytes(2).toString('hex').toUpperCase();
  return `#MC-2026-${randomNum}`;
};

module.exports = { generateOrderId };
