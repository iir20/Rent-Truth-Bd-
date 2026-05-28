# Rent Truth BD — NGINX Gateway Configuration 🛡️

Use this production-hardened site configuration file to set up secure reverse proxies, enforce TLS 1.3 encryption, and route real-time WebSocket traffic.

## 📁 Configuration File (`/etc/nginx/sites-available/renttruth.conf`)

```nginx
# Rate limiting rules to prevent scraper abuse and DDoS on National NID APIs
limit_req_zone $binary_remote_addr zone=api_limit_shared:10m rate=15r/s;

server {
    listen 80;
    listen [::]:80;
    server_name www.renttruthbd.gov renttruthbd.gov;

    # Force secure HTTP redirect to TLS 1.3
    return 301 https://$host$request_uri;
}

server {
    listen 443 ssl http2;
    listen [::]:443 ssl http2;
    server_name www.renttruthbd.gov renttruthbd.gov;

    # Secure SSL Certificates
    ssl_certificate /etc/letsencrypt/live/renttruthbd.gov/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/renttruthbd.gov/privkey.pem;

    # TLS 1.3 Security Standards
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers 'ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256:ECDHE-ECDSA-AES256-GCM-SHA384:ECDHE-RSA-AES256-GCM-SHA384:DHE-RSA-AES128-GCM-SHA256:DHE-RSA-AES256-GCM-SHA384';
    ssl_prefer_server_ciphers on;
    ssl_session_cache shared:SSL:10m;
    ssl_session_timeout 1d;

    # Standard security headers
    add_header X-Frame-Options "DENY";
    add_header X-Content-Type-Options "nosniff";
    add_header X-XSS-Protection "1; mode=block";
    add_header Content-Security-Policy "default-src 'self' http: https: data: blob: 'unsafe-inline'";

    # Static assets and upload vaults (compress with Gzip)
    location /uploads/ {
        alias /var/www/renttruth/uploads/;
        expires 30d;
        add_header Cache-Control "public, no-transform";
    }

    # Rent Truth Native API Routing
    location /api/ {
        limit_req zone=api_limit_shared burst=20 nodelay;
        proxy_pass http://localhost:3000;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_cache_bypass $http_upgrade;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # Real-time WebSocket Gateway Streaming
    location /socket.io/ {
        proxy_pass http://localhost:3000;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "Upgrade";
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        
        # Support long keepalive durations for unstable rural connections
        proxy_read_timeout 86400s;
        proxy_send_timeout 86400s;
    }
}
```
