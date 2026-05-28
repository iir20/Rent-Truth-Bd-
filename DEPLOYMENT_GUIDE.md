# Rent Truth BD — National Scale deployment Guide 🛰️

This document outlines the system configuration to launch the Rent Truth BD smart housing portal on enterprise-grade cloud environments in Bangladesh.

## 🏢 Platform Topology
```
           [ Client Apps (Android Native) / Web Hubs ]
                              │
                    SSL / HTTPS / WSS Ports
                              ▼
                     [ Nginx Reverse Proxy ]
                              │
                    Load Balanced (PM2 Pools)
                     ┌────────┴────────┐
                     ▼                 ▼
             [ Node Service 1 ] [ Node Service 2 ]
                     └────────┬────────┘
                              │
                     Secure VPC Network
                              ▼
                [ App Database Cluster (Room/SQLite) ]
```

## 📋 Pre-Deployment Audit
Ensure that the following environment criteria are met before staging the release build:
1. **Domain SSL Mapping**: Direct `*.renttruthbd.gov` routing securely points to standard DNS nameservers.
2. **Ports Assignment**: Ensure ports `443` (HTTPS/WSS) and `80` (HTTP auto-redirect) are globally open in the firewall.
3. **Storage Vaults**: Secure document scans are isolated into object storage bins with read-only validation.

---

## 🛠️ Step-by-Step Installation

### Step 1: Clone and Environment Isolation
Authenticate with the server node and clone the release tree:
```bash
git clone https://github.com/aistudio/rent-truth-bd-platform.git /var/www/renttruth
cd /var/www/renttruth
cp .env.example .env
nano .env # Set your secure production credentials
```

### Step 2: Container Compilation
Build and spin up the isolated app microservices:
```bash
docker-compose up -d --build
```

### Step 3: Nginx Gateway Setup
Copy and link your cloud server gateway values:
```bash
sudo cp NGINX_CONFIG.md /etc/nginx/sites-available/renttruth.conf
sudo ln -s /etc/nginx/sites-available/renttruth.conf /etc/nginx/sites-enabled/
sudo nginx -t && sudo systemctl restart nginx
```

---

## ⚡ Scaling Checklist for Dhaka Hubs

When leasing volumes scale in high-density sectors (Mirpur, Dhanmondi, Uttara, Bashundhara, Gulshan):
- **Enable WebSockets Clustering**: Hook custom state relays into Redis channels to replicate websocket connections seamlessly across multi-thread nodes.
- **Set Up Multi-Core Forking**: Utilize PM2 with `pm2 start dist/server.js -i max` to utilize multiple CPU cores under peak rental lease periods.
- **Configure Memory Constraints**: Prevent host server exhaustion by configuring Node.js heap memory limits with `--max-old-space-size=2048`.
