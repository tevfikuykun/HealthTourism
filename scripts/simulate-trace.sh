#!/bin/bash

# Trace Simulation Script
# Simulates "Hospital FTP -> AI Processing" flow and shows trace in Zipkin

echo "🔍 Starting Trace Simulation..."
echo ""

# Check if Zipkin is running
if ! curl -s http://localhost:9411 > /dev/null; then
    echo "❌ Zipkin is not running. Please start it first:"
    echo "   docker-compose up -d zipkin"
    exit 1
fi

# Check if Integration Test Service is running
if ! curl -s http://localhost:8093/actuator/health > /dev/null; then
    echo "❌ Integration Test Service is not running. Please start it first:"
    echo "   cd microservices/integration-test-service && mvn spring-boot:run"
    exit 1
fi

echo "✅ Services are running"
echo ""
echo "🚀 Triggering trace simulation..."
echo ""

# Trigger trace
RESPONSE=$(curl -s -X POST http://localhost:8093/api/trace-simulation/hospital-ftp-to-ai)

# Extract trace ID
TRACE_ID=$(echo $RESPONSE | grep -o '"traceId":"[^"]*' | cut -d'"' -f4)

if [ -z "$TRACE_ID" ]; then
    echo "❌ Failed to get trace ID"
    echo "Response: $RESPONSE"
    exit 1
fi

echo "✅ Trace created successfully!"
echo ""
echo "📊 Trace Details:"
echo "$RESPONSE" | jq .
echo ""
echo "🔍 View trace in Zipkin:"
echo "   http://localhost:9411/zipkin/traces/$TRACE_ID"
echo ""
echo "   Or search for: hospital-ftp-to-ai-flow"
echo ""

