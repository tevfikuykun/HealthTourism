#!/bin/bash

echo "========================================"
echo "Running All Service Tests"
echo "========================================"
echo ""

FAILED=0
PASSED=0

# Test Auth Service
echo "[1/10] Testing Auth Service..."
cd auth-service
mvn test -q
if [ $? -ne 0 ]; then
    echo "[FAILED] Auth Service tests failed"
    FAILED=$((FAILED + 1))
else
    echo "[PASSED] Auth Service tests passed"
    PASSED=$((PASSED + 1))
fi
cd ..

# Test User Service
echo "[2/10] Testing User Service..."
cd user-service
mvn test -q
if [ $? -ne 0 ]; then
    echo "[FAILED] User Service tests failed"
    FAILED=$((FAILED + 1))
else
    echo "[PASSED] User Service tests passed"
    PASSED=$((PASSED + 1))
fi
cd ..

# Test Hospital Service
echo "[3/10] Testing Hospital Service..."
cd hospital-service
mvn test -q
if [ $? -ne 0 ]; then
    echo "[FAILED] Hospital Service tests failed"
    FAILED=$((FAILED + 1))
else
    echo "[PASSED] Hospital Service tests passed"
    PASSED=$((PASSED + 1))
fi
cd ..

# Test Payment Service
echo "[4/10] Testing Payment Service..."
cd payment-service
mvn test -q
if [ $? -ne 0 ]; then
    echo "[FAILED] Payment Service tests failed"
    FAILED=$((FAILED + 1))
else
    echo "[PASSED] Payment Service tests passed"
    PASSED=$((PASSED + 1))
fi
cd ..

# Test Reservation Service
echo "[5/10] Testing Reservation Service..."
cd reservation-service
mvn test -q
if [ $? -ne 0 ]; then
    echo "[FAILED] Reservation Service tests failed"
    FAILED=$((FAILED + 1))
else
    echo "[PASSED] Reservation Service tests passed"
    PASSED=$((PASSED + 1))
fi
cd ..

echo ""
echo "========================================"
echo "Test Summary"
echo "========================================"
echo "Passed: $PASSED"
echo "Failed: $FAILED"
echo "========================================"

if [ $FAILED -gt 0 ]; then
    echo ""
    echo "WARNING: Some tests failed!"
    exit 1
else
    echo ""
    echo "All tests passed successfully!"
    exit 0
fi

