#!/bin/bash

echo "=========================================="
echo "Running Load Tests"
echo "=========================================="

BASE_URL=${BASE_URL:-"http://localhost:8080"}

# Check if k6 is installed
if ! command -v k6 &> /dev/null; then
    echo "k6 is not installed. Installing..."
    # Install k6 (adjust for your OS)
    # For Linux:
    # sudo gpg -k
    # sudo gpg --no-default-keyring --keyring /usr/share/keyrings/k6-archive-keyring.gpg --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys C5AD17C747E3415A3642D57D77C6C491D6AC1D9B
    # echo "deb [signed-by=/usr/share/keyrings/k6-archive-keyring.gpg] https://dl.k6.io/deb stable main" | sudo tee /etc/apt/sources.list.d/k6.list
    # sudo apt-get update
    # sudo apt-get install k6
    echo "Please install k6 from https://k6.io/docs/getting-started/installation/"
    exit 1
fi

cd "$(dirname "$0")"

echo "Running Load Test..."
k6 run --out json=results/load-test-results.json k6/load-test.js

echo "Running Stress Test..."
k6 run --out json=results/stress-test-results.json k6/stress-test.js

echo "Running Spike Test..."
k6 run --out json=results/spike-test-results.json k6/spike-test.js

echo "Running Endurance Test (this will take 40 minutes)..."
k6 run --out json=results/endurance-test-results.json k6/endurance-test.js

echo "Load tests completed! Results saved in results/ directory."

