@echo off
echo ========================================
echo Health Tourism - Project Startup
echo ========================================
echo.

echo [1/4] Starting Docker containers...
docker-compose up -d
timeout /t 5

echo [2/4] Starting Eureka Server...
start "Eureka Server" cmd /k "cd eureka-server && mvn spring-boot:run"
timeout /t 15

echo [3/4] Starting API Gateway...
start "API Gateway" cmd /k "cd api-gateway && mvn spring-boot:run"
timeout /t 10

echo [4/4] Starting Frontend...
start "Frontend" cmd /k "cd frontend && npm run dev"

echo.
echo ========================================
echo Project Started Successfully!
echo ========================================
echo.
echo Access Points:
echo - Eureka Dashboard: http://localhost:8761
echo - API Gateway: http://localhost:8080
echo - Frontend: http://localhost:3000
echo.
echo Note: Other services can be started manually using start-services.bat
echo.
pause

