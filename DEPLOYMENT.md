# Rent Truth BD — Production Deployment Manual 🚀

This document details the configuration and scripts to deploy the Rent Truth BD platform securely on enterprise virtual private servers (VPS), cloud runtimes, or dedicated infrastructure.

---

## 🐋 Docker & containerization Strategy

Rent Truth BD uses a multi-stage Docker setup to run its backend services and dashboard containers under isolated, light images.

### 1. `docker-compose.yml`
Save this in your project root to handle automated orchestrations of the server, MongoDB Atlas caching proxies, and SSL certificates generation:

```yaml
version: '3.8'

services:
  rent-truth-api:
    image: rent-truth-bd/backend:latest
    build:
      context: .
      dockerfile: Dockerfile
    container_name: rent_truth_backend_server
    restart: unless-stopped
    ports:
      - "8080:8080"
    environment:
      - NODE_ENV=production
      - MONGO_URI=mongodb+srv://adminUser:secureDbPass@rent-cluster.mongodb.net/production?retryWrites=true
      - JWT_SECRET=bK4sh-SECURE-99s2ksj-77s12jh-88a8as9
      - GEMINI_API_KEY=AIzaSyD-YourRealAndSecureGeminiApiKeyInjected
      - PORT=8080
    volumes:
      - secure-storage:/app/uploads
    logging:
      driver: "json-file"
      options:
        max-size: "100m"
        max-file: "3"

  nginx-proxy:
    image: nginx:alpine
    container_name: rent_truth_nginx_proxy
    restart: always
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf:ro
      - /etc/letsencrypt:/etc/letsencrypt:ro
    depends_on:
      - rent-truth-api

volumes:
  secure-storage:
    driver: local
```

### 2. Nginx Reverse Proxy Config (`nginx.conf`)
Put this inside your hosting server to enable secure HTTPS, Gzip compression, WebSockets proxying (for real-time RentSync), and automated DDoS rate limits:

```nginx
user nginx;
worker_processes auto;
error_log /var/log/nginx/error.log warn;
pid /var/run/nginx.pid;

events {
    worker_connections 1024;
}

http {
    include /etc/nginx/mime.types;
    default_type application/octet-stream;
    
    # DDoS Shield & Rate Limits
    limit_req_zone $binary_remote_addr zone=api_limit:10m rate=15r/s;

    # Gzip Optimization
    gzip on;
    gzip_types text/plain text/css application/json application/javascript text/xml;
    gzip_min_length 1000;

    upstream api_servers {
        server rent-truth-api:8080;
    }

    server {
        listen 80;
        server_name api.renttruthbd.com;
        return 301 https://$host$request_uri;
    }

    server {
        listen 443 ssl http2;
        server_name api.renttruthbd.com;

        ssl_certificate /etc/letsencrypt/live/api.renttruthbd.com/fullchain.pem;
        ssl_certificate_key /etc/letsencrypt/live/api.renttruthbd.com/privkey.pem;
        ssl_protocols TLSv1.2 TLSv1.3;
        ssl_ciphers HIGH:!aNULL:!MD5;

        # WebSockets Sync Proxying for RentSync
        location /sync/ {
            proxy_pass http://api_servers;
            proxy_http_version 1.1;
            proxy_set_header Upgrade $http_upgrade;
            proxy_set_header Connection "upgrade";
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
        }

        # Main REST API Gateway
        location / {
            limit_req zone=api_limit burst=30 nodelay;
            proxy_pass http://api_servers;
            proxy_set_header Host $host;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        }
    }
}
```

---

## ⚙️ Process Management (Non-Docker Native VPS with PM2)

If deploying directly onto a raw Ubuntu VPS without Docker, PM2 is the recommended node task manager.

### 1. PM2 Application Declaration (`ecosystem.config.js`)
```javascript
module.exports = {
  apps: [
    {
      name: "rent-truth-bd-api",
      script: "./dist/server.js",
      instances: "max",
      exec_mode: "cluster",
      autorestart: true,
      watch: false,
      max_memory_restart: "1G",
      env_production: {
        NODE_ENV: "production",
        PORT: 8080,
        MONGO_URI: "mongodb+srv://adminUser:secureDbPass@rent-cluster.mongodb.net/production"
      }
    }
  ]
};
```

### 2. Startup Command
```bash
# Install PM2 Globally
sudo npm install -g pm2

# Start cluster
pm2 start ecosystem.config.js --env production

# Ensure restart on system reboot
pm2 startup systemd
pm2 save
```

---

## 📈 High Traffic Scaling & CDN Integrations

1. **Static Files & Documents Storage**: Direct file attachments (like scanned PDF/JPG NID Cards, Lease Contracts) must be offloaded to secure encrypted S3 buckets paired with **Cloudflare CDN** caching rules. 
2. **Database Clustering**: Use MongoDB Atlas's local cluster configuration with `M10-M30` tiers to allow instant horizontal replication scaling if traffic in high-density Dhaka hubs (Mirpur, Dhanmondi, Uttara) spikes during seasonal rental search months.

---

## 💾 automated backups Strategy

Execute this simple daily cron script (`/etc/cron.daily/rent_db_backup.sh`) to stream database dumps directly to offsite S3-compatible cloud objects:

```bash
#!/bin/bash
BACKUP_DATE=$(date +"%Y-%m-%d_%H-%M-%S")
BACKUP_DIR="/tmp/db_backups"
S3_BUCKET_NAME="s3://renttruthbd-cold-ledger-backups"

mkdir -p $BACKUP_DIR
mongodump --uri="mongodb+srv://adminUser:secureDbPass..." --out=$BACKUP_DIR/$BACKUP_DATE

tar -czf $BACKUP_DIR/rent_backup_$BACKUP_DATE.tar.gz $BACKUP_DIR/$BACKUP_DATE
aws s3 cp $BACKUP_DIR/rent_backup_$BACKUP_DATE.tar.gz $S3_BUCKET_NAME/rent_backup_$BACKUP_DATE.tar.gz

rm -rf $BACKUP_DIR
```

---

## 🚨 System Monitoring & Alerting

To maintain 99.9% uptime, configure Prometheus and Grafana on your hosting nodes or hook the API to monitoring interfaces such as Datadog/Sentry:
- **Core KPIs to watch**:
  1. API Response Latency (Optimal: `< 150ms`).
  2. Database Connection counts.
  3. Concurrent WebSocket links count.
  4. OCR processing exceptions (fuzzy files upload rejections).
  5. API Rate-limit blockades count (Anti-DDoS telemetry logs).
