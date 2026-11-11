const express = require('express');
const helmet = require('helmet');
const cors = require('cors');
const { notFoundHandler, errorHandler } = require('./src/middleware/errorHandler');

const authRoutes = require('./src/routes/authRoutes');
const productRoutes = require('./src/routes/productRoutes');
const categoryRoutes = require('./src/routes/categoryRoutes');
const addressRoutes = require('./src/routes/addressRoutes');
const orderRoutes = require('./src/routes/orderRoutes');

const app = express();

// Security Headers & CORS
app.use(helmet());
app.use(cors());

// Body Parsers
app.use(express.json({ limit: '10kb' }));
app.use(express.urlencoded({ extended: true, limit: '10kb' }));

// Health Check Endpoint
app.get('/health', (req, res) => {
  res.status(200).json({
    status: 'healthy',
    service: 'medicart-api',
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
