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

echo "Buildování backendu..."
cd battery-management-system
./gradlew shadowJar
cd ..

echo "Spouštění backendu..."
docker run -d \
  --name battery_backend \
  --network battery_net \
  --privileged \
  -v /dev:/dev \
  -v $(pwd)/battery-management-system/build/libs/battery-management-system-all.jar:/app.jar \
  -p 8080:8080 \
  openjdk:17-slim \
  sh -c "java -jar /app.jar --port=8080 --db=jdbc:postgresql://battery_postgres:5432/battery"

echo "Čekání na spuštění backendu..."
sleep 10

echo "Spouštění frontendu..."
docker run -d \
  --name battery_frontend_dev \
  --network battery_net \
  -v $(pwd)/web:/app \
  -p 3000:5173 \
  -w /app \
  node:18-alpine \
  sh -c "npm install && npm run dev"

echo "Aplikace běží na http://localhost:3000"
