const mongoose = require('mongoose');
const env = require('./env');

let embeddedMongod = null;
let retryTimer = null;

const seedInitialDataSafely = async () => {
  try {
    const Product = require('../models/Product');
    const count = await Product.countDocuments();
    if (count === 0) {
      const { PRODUCTS_DATA } = require('../scripts/seed');
      await Product.insertMany(PRODUCTS_DATA);
      console.log(`[Database] Initialized fresh database with ${PRODUCTS_DATA.length} products.`);
    }

    const User = require('../models/User');
    const Address = require('../models/Address');
    let user = await User.findOne({ phoneNumber: env.DEMO_PHONE_NUMBER });
    if (!user) {
      user = await User.create({ name: 'Sahil Kumar', phoneNumber: env.DEMO_PHONE_NUMBER });
      await Address.create({
        userId: user._id,
        recipientName: 'Sahil Kumar',
        phoneNumber: env.DEMO_PHONE_NUMBER,
        houseNumber: 'Flat 402, Block B, Shanti Vihar',
        locality: '12th Main Road, HAL 2nd Stage',
        city: 'Bengaluru',
        state: 'Karnataka',
        pinCode: '560038',
        addressType: 'HOME',
        isDefault: true
      });
      console.log(`[Database] Initialized default demo account for ${env.DEMO_PHONE_NUMBER}.`);
    }
  } catch (seedError) {
    console.error('[Database Warning] Auto-seeding encounter warning:', seedError.message);
  }
};

const connectDB = async (customUri) => {
  const uri = customUri || env.MONGODB_URI;

  if (env.isProd && (!process.env.MONGODB_URI || uri.includes('127.0.0.1') || uri.includes('localhost'))) {
    console.warn('[Database Warning] Running in production but MONGODB_URI is not set to a MongoDB Atlas cluster.');
    console.warn('[Database Warning] Please configure MONGODB_URI in Render dashboard: mongodb+srv://<user>:<password>@<cluster>.mongodb.net/medicart?retryWrites=true&w=majority');
  }

  try {
    const conn = await mongoose.connect(uri, {
      autoIndex: true,
      serverSelectionTimeoutMS: 10000
    });
    if (!env.isTest) {
      console.log(`[Database] MongoDB Connected: ${conn.connection.host}/${conn.connection.name}`);
      await seedInitialDataSafely();
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
        await seedInitialDataSafely();
        return conn;
      } catch (memErr) {
        console.error('[Database Error] Failed to start embedded Mongo server:', memErr.message);
      }
    }

    console.error(`[Database Error] Connection failed: ${error.message}`);

    // In production or live server, do not exit immediately before retry
    // Schedule reconnection attempt so transient network or DNS delays recover
    if (!env.isTest && !customUri) {
      console.log('[Database] Retrying MongoDB connection in 5 seconds...');
      if (!retryTimer) {
        retryTimer = setTimeout(() => {
          retryTimer = null;
          connectDB(customUri).catch(retryErr => {
            console.error('[Database Error] Retry connection attempt failed:', retryErr.message);
          });
        }, 5000);
      }
    }

    if (env.isTest) {
      throw error;
    }
    return null;
  }
};

const disconnectDB = async () => {
  if (retryTimer) {
    clearTimeout(retryTimer);
    retryTimer = null;
  }
  await mongoose.disconnect();
  if (embeddedMongod) {
    await embeddedMongod.stop();
  }
};

module.exports = { connectDB, disconnectDB };
