module.exports = {
  apps: [
    {
      name: 'renttruth-api-service',
      script: './dist/index.js',
      instances: 'max', // Enable clustering to run in multi-core VPS nodes
      exec_mode: 'cluster',
      autorestart: true,
      watch: false,
      max_memory_restart: '1G',
      env_production: {
        NODE_ENV: 'production',
        PORT: 5000,
        DATABASE_URL: 'postgresql://renttruth_user:renttruth_secure_pass@db:5432/renttruth_db',
        REDIS_URL: 'redis://cache:6379'
      }
    }
  ]
};
