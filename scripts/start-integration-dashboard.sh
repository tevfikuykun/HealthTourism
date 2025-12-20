#!/bin/bash

echo "🔌 Starting Integration Dashboard..."
echo ""

cd microservices/integration-dashboard

echo "Building project..."
mvn clean install -DskipTests

echo ""
echo "Starting Integration Dashboard on port 3002..."
echo "Open http://localhost:3002 in your browser"
echo ""

mvn spring-boot:run

