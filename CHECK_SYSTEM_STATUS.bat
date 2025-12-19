@echo off
chcp 65001 >nul
echo.
echo ╔══════════════════════════════════════════════════════════════╗
echo ║     Health Tourism Platform - System Status Check            ║
echo ╚══════════════════════════════════════════════════════════════╝
echo.

cd /d %~dp0

echo ┌─────────────────────────────────────────────────────────────┐
echo │ DOCKER CONTAINERS                                           │
echo └─────────────────────────────────────────────────────────────┘
docker-compose ps

echo.
echo ┌─────────────────────────────────────────────────────────────┐
echo │ SERVICE HEALTH CHECKS                                       │
echo └─────────────────────────────────────────────────────────────┘

REM Check Eureka
echo | set /p="Eureka Server (8761):    "
curl -s -o nul -w "%%{http_code}" http://localhost:8761/actuator/health 2>nul || echo OFFLINE
echo.

REM Check API Gateway
echo | set /p="API Gateway (8080):      "
curl -s -o nul -w "%%{http_code}" http://localhost:8080/actuator/health 2>nul || echo OFFLINE
echo.

REM Check Auth Service
echo | set /p="Auth Service (8023):     "
curl -s -o nul -w "%%{http_code}" http://localhost:8023/actuator/health 2>nul || echo OFFLINE
echo.

REM Check Redis
echo | set /p="Redis (6379):            "
docker exec healthtourism-redis redis-cli ping 2>nul || echo OFFLINE

REM Check Kafka
echo | set /p="Kafka (9092):            "
docker exec healthtourism-kafka kafka-broker-api-versions --bootstrap-server localhost:9092 >nul 2>&1 && echo PONG || echo OFFLINE

echo.
echo ┌─────────────────────────────────────────────────────────────┐
echo │ QUICK LINKS                                                 │
echo └─────────────────────────────────────────────────────────────┘
echo   1. Eureka Dashboard:  http://localhost:8761
echo   2. API Gateway:       http://localhost:8080
echo   3. RabbitMQ Console:  http://localhost:15672
echo   4. Elasticsearch:     http://localhost:9200
echo.
pause



