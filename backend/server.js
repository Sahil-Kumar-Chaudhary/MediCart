const app = require('./app');
const { connectDB } = require('./src/config/db');
const env = require('./src/config/env');

const startServer = async () => {
  try {
    await connectDB();
    const server = app.listen(env.PORT, () => {
      console.log(`[MediCart Server] REST API listening on port ${env.PORT} in ${env.NODE_ENV} mode`);
      console.log(`[MediCart Server] Health Check: http://localhost:${env.PORT}/health`);
    });

    // Graceful Shutdown
    const shutdown = async (signal) => {
      console.log(`\n[MediCart Server] Received ${signal}. Closing HTTP server and database connections...`);
      server.close(async () => {
        const { disconnectDB } = require('./src/config/db');
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
