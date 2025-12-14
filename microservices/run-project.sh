#!/bin/bash

echo "========================================"
echo "Health Tourism - Project Startup"
echo "========================================"
echo ""

echo "[1/4] Starting Docker containers..."
docker-compose up -d
sleep 5

echo "[2/4] Starting Eureka Server..."
cd eureka-server && mvn spring-boot:run > ../logs/eureka.log 2>&1 &
cd ..
sleep 15

echo "[3/4] Starting API Gateway..."
cd api-gateway && mvn spring-boot:run > ../logs/gateway.log 2>&1 &
cd ..
sleep 10

echo "[4/4] Starting Frontend..."
cd frontend && npm run dev > ../logs/frontend.log 2>&1 &
cd ..

echo ""
echo "========================================"
echo "Project Started Successfully!"
echo "========================================"
echo ""
echo "Access Points:"
echo "- Eureka Dashboard: http://localhost:8761"
echo "- API Gateway: http://localhost:8080"
echo "- Frontend: http://localhost:3000"
echo ""
echo "Note: Other services can be started manually using start-services.sh"
echo ""

