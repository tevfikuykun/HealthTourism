@echo off
echo ==========================================
echo Running Load Tests
echo ==========================================

if "%BASE_URL%"=="" set BASE_URL=http://localhost:8080

cd /d "%~dp0"

echo Running Load Test with JMeter...
if exist "C:\Program Files\Apache JMeter\bin\jmeter.bat" (
    "C:\Program Files\Apache JMeter\bin\jmeter.bat" -n -t jmeter\comprehensive-load-test.jmx -l results\load-test-results.jtl -e -o results\html-report
    echo Load test completed! Results saved in results\ directory.
) else (
    echo JMeter is not installed. Please install from https://jmeter.apache.org/download_jmeter.cgi
    echo Or use k6 for load testing: k6 run k6\load-test.js
)

