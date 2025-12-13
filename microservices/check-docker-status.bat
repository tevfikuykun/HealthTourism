@echo off
echo ==========================================
echo Docker Container Status
echo ==========================================
echo.

cd /d "%~dp0"

echo Running containers:
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

echo.
echo All containers (including stopped):
docker ps -a --format "table {{.Names}}\t{{.Status}}" | findstr /i "mysql redis rabbitmq zipkin"

echo.
echo ==========================================
echo To start containers: docker-compose up -d
echo To view logs: docker-compose logs
echo To stop: docker-compose down
echo ==========================================
pause

