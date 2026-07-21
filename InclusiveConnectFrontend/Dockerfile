# ─────────────────────────────────────────────────────────────────────
#  Stage 1: Build
#  Node 20 Alpine — installs deps and compiles production bundle
# ─────────────────────────────────────────────────────────────────────
FROM node:20-alpine AS builder

WORKDIR /app

# Copy manifests first — layer cache for npm install
COPY package.json package-lock.json ./
RUN npm ci --prefer-offline

# Copy source and build production bundle
COPY . .
RUN npm run build -- --configuration production

# ─────────────────────────────────────────────────────────────────────
#  Stage 2: Serve
#  Lightweight Nginx Alpine — no Node runtime in final image
# ─────────────────────────────────────────────────────────────────────
FROM nginx:1.27-alpine AS runtime

# Remove default Nginx content
RUN rm -rf /usr/share/nginx/html/*

# Copy compiled Angular app from build stage
COPY --from=builder /app/dist/inclusive-connect-frontend/browser /usr/share/nginx/html

# Copy custom Nginx configuration
COPY nginx.conf /etc/nginx/nginx.conf

# Nginx runs on port 80
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD wget -qO- http://localhost:80/ || exit 1

CMD ["nginx", "-g", "daemon off;"]
