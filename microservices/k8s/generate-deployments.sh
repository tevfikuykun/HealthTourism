#!/bin/bash

# Generate Kubernetes deployment files for all microservices
# Usage: ./generate-deployments.sh

SERVICES=(
    "reservation-service:8009"
    "hospital-service:8002"
    "doctor-service:8003"
    "accommodation-service:8004"
    "flight-service:8005"
    "car-rental-service:8006"
    "transfer-service:8007"
    "package-service:8008"
    "cost-predictor-service:8033"
    "patient-risk-scoring-service:8036"
    "health-wallet-service:8037"
    "ai-health-companion-service:8035"
    "iot-monitoring-service:8032"
    "virtual-tour-service:8031"
    "legal-ledger-service:8034"
    "privacy-compliance-service:8038"
    "chaos-engineering-service:8039"
    "blockchain-service:8030"
    "gamification-service:8017"
    "translation-service:8016"
)

mkdir -p deployments

for service_info in "${SERVICES[@]}"; do
    IFS=':' read -r service_name port <<< "$service_info"
    
    # Generate deployment file from template
    sed "s/SERVICE_NAME/$service_name/g; s/PORT/$port/g" microservice-template.yaml > "deployments/${service_name}-deployment.yaml"
    
    echo "Generated: deployments/${service_name}-deployment.yaml"
done

echo "All deployment files generated in deployments/ directory"
