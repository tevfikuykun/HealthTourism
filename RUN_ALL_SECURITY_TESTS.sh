#!/bin/bash

echo "=========================================="
echo "Running All Security Tests"
echo "=========================================="

cd "$(dirname "$0")"

echo ""
echo "[1/4] Running Penetration Tests..."
./security-tests/run-penetration-tests.sh

echo ""
echo "[2/4] Running Security Headers Check..."
./security-tests/security-headers-check.sh

echo ""
echo "[3/4] Running OWASP Dependency Check..."
./security-tests/owasp-dependency-check.sh

echo ""
echo "[4/4] Running Load Tests..."
echo "Note: Load tests may take a while. Press Ctrl+C to skip."
sleep 5
./load-tests/run-load-tests.sh

echo ""
echo "=========================================="
echo "All Security Tests Completed!"
echo "=========================================="
echo "Check the results in:"
echo "- security-tests/owasp-reports/"
echo "- load-tests/results/"
echo "=========================================="

