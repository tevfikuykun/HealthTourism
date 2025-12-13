#!/bin/bash

# Generate test coverage reports for all services

set -e

echo "Generating test coverage reports..."

BASE_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$BASE_DIR"

# Run tests with coverage for each service
SERVICES=(
    "auth-service"
    "user-service"
    "hospital-service"
    "doctor-service"
    "payment-service"
)

for service in "${SERVICES[@]}"; do
    service_dir="microservices/$service"
    
    if [ ! -d "$service_dir" ]; then
        continue
    fi
    
    echo "Generating coverage for $service..."
    cd "$service_dir"
    
    mvn clean test jacoco:report
    
    if [ -d "target/site/jacoco" ]; then
        echo "✓ Coverage report generated for $service"
        echo "  Location: $service_dir/target/site/jacoco/index.html"
    fi
    
    cd "$BASE_DIR"
done

# Frontend coverage
echo "Generating coverage for frontend..."
cd microservices/frontend

if [ -f "package.json" ]; then
    npm test -- --coverage
    echo "✓ Coverage report generated for frontend"
    echo "  Location: microservices/frontend/coverage/index.html"
fi

cd "$BASE_DIR"

echo "All coverage reports generated!"

