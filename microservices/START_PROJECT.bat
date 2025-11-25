@echo off
echo ============================================
echo Health Tourism Microservices - Starting...
echo ============================================
echo.

echo [1/3] Starting Docker containers...
cd microservices
docker-compose up -d
timeout /t 5 /nobreak >nul
echo Docker containers started!
echo.

echo [2/3] Building common modules...
call mvn clean install -DskipTests -f common-exception-handler\pom.xml
echo.

echo [3/3] Starting Eureka Server...
start "Eureka Server" cmd /k "cd eureka-server && mvn spring-boot:run"
timeout /t 10 /nobreak >nul
echo Eureka Server starting...
echo.

echo ============================================
echo Services can be started manually:
echo ============================================
echo 1. API Gateway
echo 2. Auth Service
echo 3. User Service
echo 4. Hospital Service
echo 5. ... (other services)
echo.
echo Or use: start-services.bat
echo.
echo Access:
echo - Eureka: http://localhost:8761
echo - API Gateway: http://localhost:8080
echo ============================================
pause

