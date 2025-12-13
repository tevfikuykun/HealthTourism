@echo off
echo Starting Docker containers...
echo This may take 2-3 minutes for the first time...
echo.

cd /d "%~dp0"

docker-compose up -d

echo.
echo Waiting for containers to be ready...
timeout /t 30 /nobreak >nul

echo.
echo Checking container status...
docker ps --format "table {{.Names}}\t{{.Status}}"

echo.
echo Docker containers started!
echo.
echo To view logs: docker-compose logs -f
echo To stop: docker-compose down
echo.
pause

