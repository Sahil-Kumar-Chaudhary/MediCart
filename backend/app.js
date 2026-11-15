const express = require('express');
const helmet = require('helmet');
const cors = require('cors');
const { notFoundHandler, errorHandler } = require('./src/middleware/errorHandler');
const { apiRateLimiter } = require('./src/middleware/rateLimiter');

const authRoutes = require('./src/routes/authRoutes');
const productRoutes = require('./src/routes/productRoutes');
const categoryRoutes = require('./src/routes/categoryRoutes');
const addressRoutes = require('./src/routes/addressRoutes');
const orderRoutes = require('./src/routes/orderRoutes');

const app = express();

// Enable reverse proxy trust for Render / Cloud hosting
app.set('trust proxy', 1);

// Security Headers & CORS
app.use(helmet());
app.use(cors());

// Global API rate limiting
app.use('/api', apiRateLimiter);

// Body Parsers
app.use(express.json({ limit: '10kb' }));
app.use(express.urlencoded({ extended: true, limit: '10kb' }));

// Health Check Endpoint
app.get('/health', (req, res) => {
  const mongoose = require('mongoose');
  const dbStates = ['disconnected', 'connected', 'connecting', 'disconnecting'];
  const dbStatus = dbStates[mongoose.connection.readyState] || 'unknown';

  res.status(200).json({
    status: 'healthy',
    service: 'medicart-api',
    database: dbStatus,
    timestamp: new Date().toISOString()
  });
});

// API Routes
app.use('/api/auth', authRoutes);
app.use('/api/products', productRoutes);
app.use('/api/categories', categoryRoutes);
app.use('/api/addresses', addressRoutes);
app.use('/api/orders', orderRoutes);

// 404 Not Found & Error Handlers
app.use(notFoundHandler);
app.use(errorHandler);

module.exports = app;
