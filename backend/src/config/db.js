const mongoose = require('mongoose');
const env = require('./env');

let embeddedMongod = null;

const connectDB = async (customUri) => {
  const uri = customUri || env.MONGODB_URI;
  try {
    const conn = await mongoose.connect(uri, {
      autoIndex: true,
      serverSelectionTimeoutMS: 2000
    });
    if (!env.isTest) {
      console.log(`[Database] MongoDB Connected: ${conn.connection.host}/${conn.connection.name}`);
    }
    return conn;
  } catch (error) {
    if (env.isDev && !customUri) {
      console.log(`[Database] Standard MongoDB connection failed (${error.message}). Starting embedded MongoMemoryServer for local dev...`);
      try {
        const { MongoMemoryServer } = require('mongodb-memory-server');
        embeddedMongod = await MongoMemoryServer.create();
        const memUri = embeddedMongod.getUri();
        const conn = await mongoose.connect(memUri, { autoIndex: true });
        console.log(`[Database] Embedded MongoDB Server running at: ${memUri}`);

        // Seed development catalogue
        const Product = require('../models/Product');
        const count = await Product.countDocuments();
        if (count === 0) {
          const { PRODUCTS_DATA } = require('../scripts/seed');
          await Product.insertMany(PRODUCTS_DATA);
          console.log(`[Database] Successfully auto-seeded ${PRODUCTS_DATA.length} products.`);
        }

        // Seed default sample user & address
        const User = require('../models/User');
        const Address = require('../models/Address');
        let user = await User.findOne({ phoneNumber: '9876543210' });
        if (!user) {
          user = await User.create({ name: 'Sahil Kumar', phoneNumber: '9876543210' });
          await Address.create({
            userId: user._id,
            recipientName: 'Sahil Kumar',
            phoneNumber: '9876543210',
            houseNumber: 'Flat 402, Block B, Shanti Vihar',
            locality: '12th Main Road, HAL 2nd Stage',
            city: 'Bengaluru',
            state: 'Karnataka',
            pinCode: '560038',
            addressType: 'HOME',
            isDefault: true
          });
          console.log('[Database] Seeded default user and address for instant app connectivity.');
        }

        return conn;
      } catch (memErr) {
        console.error('[Database Error] Failed to start embedded Mongo server:', memErr.message);
      }
    }

    console.error(`[Database Error] Connection failed: ${error.message}`);
    if (!env.isTest) {
      process.exit(1);
    }
    throw error;
  }
};

const disconnectDB = async () => {
  await mongoose.disconnect();
  if (embeddedMongod) {
    await embeddedMongod.stop();
  }
};

module.exports = { connectDB, disconnectDB };
