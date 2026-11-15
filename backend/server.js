const app = require('./app');
const { connectDB, disconnectDB } = require('./src/config/db');
const env = require('./src/config/env');

const startServer = async () => {
  try {
    // 1. Bind to PORT and 0.0.0.0 immediately so Render health checks and proxy routing succeed
    const server = app.listen(env.PORT, '0.0.0.0', () => {
      console.log(`[MediCart Server] REST API listening on 0.0.0.0:${env.PORT} in ${env.NODE_ENV} mode`);
      console.log(`[MediCart Server] Health Check: http://localhost:${env.PORT}/health`);
    });

    // 2. Initialize database connection asynchronously without crashing process on transient errors
    connectDB().catch((err) => {
      console.error('[MediCart Server] Initial database connection attempt reported error:', err.message);
    });

    // Graceful Shutdown
    const shutdown = async (signal) => {
      console.log(`\n[MediCart Server] Received ${signal}. Closing HTTP server and database connections...`);
      server.close(async () => {
        await disconnectDB();
        console.log('[MediCart Server] Process exited safely.');
        process.exit(0);
      });
    };

    process.on('SIGINT', () => shutdown('SIGINT'));
    process.on('SIGTERM', () => shutdown('SIGTERM'));
  } catch (error) {
    console.error('[MediCart Server] Startup failed:', error);
    process.exit(1);
  }
};

startServer();
