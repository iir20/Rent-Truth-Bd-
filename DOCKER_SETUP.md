# Rent Truth BD — Docker Setup & Orchestration 🐋

This file lists the orchestration instructions to containerize and spin up the multi-tenant Dhaka Smart Rental Platform in secure, repeatable cloud nodes.

## 📦 Service Architecture
The platform is packaged into 3 distinct container layers:
- **`rt-app`**: The Node.js application server.
- **`rt-redis`**: Key-value data cache to stream Real-time WebSockets and retry queues.
- **`rt-nginx`**: Configured reverse-proxy handling HTTPS encryption and routing.

---

## 📄 `Dockerfile` (Production Target)
```dockerfile
# Use official Node.js Alpine base image for minimal footprint
FROM node:18-alpine AS builder

WORKDIR /usr/src/app

COPY package*.json ./
RUN npm ci

COPY . .
RUN npm run build && npm prune --production

FROM node:18-alpine AS runner
WORKDIR /usr/src/app

COPY --from=builder /usr/src/app/dist ./dist
COPY --from=builder /usr/src/app/node_modules ./node_modules
COPY --from=builder /usr/src/app/package*.json ./

ENV NODE_ENV=production
ENV PORT=3000

EXPOSE 3000

CMD ["node", "dist/server.js"]
```

---

## 📄 `docker-compose.yml` (Orchestration Setup)
```yaml
version: '3.8'

services:
  rt-app:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: renttruth_app_container
    restart: always
    environment:
      - NODE_ENV=production
      - DB_PATH=/var/lib/renttruth/rent_db.sqlite
      - BASE_URL=https://renttruthbd.gov
    volumes:
      - renttruth_data:/var/lib/renttruth
    ports:
      - "3000:3000"
    depends_on:
      - rt-redis

  rt-redis:
    image: redis:7-alpine
    container_name: renttruth_redis_container
    restart: always
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data

volumes:
  renttruth_data:
  redis_data:
```

---

## 🚀 Running Container Clusters
To spin up all backend node services inside isolated Docker containers:
```bash
# Build images and run daemonized
docker-compose up -d --build

# Verify all services are healthy and running:
docker-compose ps

# Show logs in real-time:
docker-compose logs -f rt-app
```
