const mongoose = require('mongoose');
const env = require('./env');

const connectDB = async (customUri) => {
  const uri = customUri || env.MONGODB_URI;
  try {
    const conn = await mongoose.connect(uri, {
      autoIndex: true
    });
    if (!env.isTest) {
      console.log(`[Database] MongoDB Connected: ${conn.connection.host}/${conn.connection.name}`);
    }
    return conn;
  } catch (error) {
    console.error(`[Database Error] Connection failed: ${error.message}`);
    if (!env.isTest) {
      process.exit(1);
    }
    throw error;
  }
};

const disconnectDB = async () => {
  await mongoose.disconnect();
};

module.exports = { connectDB, disconnectDB };
