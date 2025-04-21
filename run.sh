#!/bin/bash
set -e

echo "Vytváření Docker sítě..."
docker network create battery_net 2>/dev/null || true

echo "Spouštění PostgreSQL kontejneru..."
docker run -d \
  --name battery_postgres \
  --network battery_net \
  -e POSTGRES_USER=admin \
  -e POSTGRES_PASSWORD=admin \
  -v postgres_data:/var/lib/postgresql/data \
  -p 5432:5432 \
  postgres:17-alpine

echo "Čekání na spuštění PostgreSQL..."
sleep 5

echo "Vytváření databáze..."
docker exec -it battery_postgres psql -U admin -d postgres -c "CREATE DATABASE battery;"

echo "Spouštění backendu..."
docker run -d \
  --name battery_backend \
  --network battery_net \
  -v $(pwd)/battery-management-system/build/libs/battery-management-system-all.jar:/app.jar \
  openjdk:17-slim \
  sh -c "java -jar /app.jar --port=8080 --db=jdbc:postgresql://battery_postgres:5432/battery"

echo "Čekání na spuštění backendu..."
sleep 10

echo "Buildování Vite frontendu..."
cd web
npm install
npm run build
cd ..

echo "Spouštění Nginx s Vite výstupem..."
docker run -d \
  --name battery_frontend_nginx \
  --network battery_net \
  -v $(pwd)/web/dist:/usr/share/nginx/html:ro \
  -v $(pwd)/web/nginx.conf:/etc/nginx/nginx.conf:ro \
  -p 3000:80 \
  nginx:alpine

echo "Aplikace běží na http://localhost:3000"