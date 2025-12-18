#!/bin/bash

# Istio Chaos Engineering Commands
# Test system resilience with single commands!

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

NAMESPACE="healthtourism"

echo -e "${BLUE}╔══════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║     Istio Chaos Engineering - Health Tourism Platform        ║${NC}"
echo -e "${BLUE}╚══════════════════════════════════════════════════════════════╝${NC}"

# Function: Enable chaos for a service
enable_chaos() {
    local service=$1
    local type=$2
    local value=$3
    
    echo -e "${YELLOW}Enabling $type chaos for $service...${NC}"
    
    case $type in
        "delay")
            kubectl apply -f - <<EOF
apiVersion: networking.istio.io/v1beta1
kind: VirtualService
metadata:
  name: ${service}-chaos
  namespace: $NAMESPACE
  labels:
    chaos: enabled
spec:
  hosts:
    - $service
  http:
    - fault:
        delay:
          percentage:
            value: ${value:-10}
          fixedDelay: 5s
      route:
        - destination:
            host: $service
EOF
            ;;
        "abort")
            kubectl apply -f - <<EOF
apiVersion: networking.istio.io/v1beta1
kind: VirtualService
metadata:
  name: ${service}-chaos
  namespace: $NAMESPACE
  labels:
    chaos: enabled
spec:
  hosts:
    - $service
  http:
    - fault:
        abort:
          percentage:
            value: ${value:-20}
          httpStatus: 503
      route:
        - destination:
            host: $service
EOF
            ;;
    esac
    
    echo -e "${GREEN}✅ Chaos enabled for $service${NC}"
}

# Function: Disable chaos for a service
disable_chaos() {
    local service=$1
    echo -e "${YELLOW}Disabling chaos for $service...${NC}"
    kubectl delete virtualservice ${service}-chaos -n $NAMESPACE --ignore-not-found
    echo -e "${GREEN}✅ Chaos disabled for $service${NC}"
}

# Function: Disable all chaos
disable_all_chaos() {
    echo -e "${YELLOW}Disabling all chaos experiments...${NC}"
    kubectl delete virtualservice -l chaos=enabled -n $NAMESPACE --ignore-not-found
    echo -e "${GREEN}✅ All chaos disabled${NC}"
}

# Function: View service mesh status
view_mesh_status() {
    echo -e "${BLUE}=== Service Mesh Status ===${NC}"
    
    echo -e "\n${YELLOW}Services:${NC}"
    kubectl get services -n $NAMESPACE
    
    echo -e "\n${YELLOW}Virtual Services:${NC}"
    kubectl get virtualservices -n $NAMESPACE
    
    echo -e "\n${YELLOW}Destination Rules:${NC}"
    kubectl get destinationrules -n $NAMESPACE
    
    echo -e "\n${YELLOW}Active Chaos:${NC}"
    kubectl get virtualservices -l chaos=enabled -n $NAMESPACE
}

# Function: Open Kiali dashboard
open_kiali() {
    echo -e "${YELLOW}Opening Kiali dashboard...${NC}"
    istioctl dashboard kiali &
    echo -e "${GREEN}Kiali available at http://localhost:20001${NC}"
}

# Function: Open Jaeger dashboard
open_jaeger() {
    echo -e "${YELLOW}Opening Jaeger dashboard...${NC}"
    kubectl port-forward -n observability svc/jaeger-query 16686:16686 &
    echo -e "${GREEN}Jaeger available at http://localhost:16686${NC}"
}

# Function: Run chaos experiment
run_experiment() {
    local experiment=$1
    
    case $experiment in
        "auth-slowdown")
            echo -e "${YELLOW}Experiment: Auth Service Slowdown${NC}"
            echo "Injecting 5s delay to 30% of auth requests..."
            enable_chaos "auth-service" "delay" 30
            echo "Monitor at Kiali: http://localhost:20001"
            echo "Run: curl -w '@curl-format.txt' http://localhost:8080/api/auth/login"
            ;;
        "cascade-failure")
            echo -e "${YELLOW}Experiment: Cascade Failure Test${NC}"
            echo "Injecting failures to user-service..."
            enable_chaos "user-service" "abort" 50
            echo "Watch how dependent services handle the failure"
            ;;
        "network-partition")
            echo -e "${YELLOW}Experiment: Network Partition${NC}"
            echo "Simulating blockchain service isolation..."
            enable_chaos "blockchain-service" "abort" 100
            echo "Test: Transactions should queue and retry"
            ;;
        "gradual-degradation")
            echo -e "${YELLOW}Experiment: Gradual Degradation${NC}"
            for pct in 10 30 50 70; do
                echo "Injecting ${pct}% delay..."
                enable_chaos "ai-health-companion-service" "delay" $pct
                sleep 30
            done
            ;;
        *)
            echo -e "${RED}Unknown experiment: $experiment${NC}"
            echo "Available: auth-slowdown, cascade-failure, network-partition, gradual-degradation"
            ;;
    esac
}

# Main menu
case "${1:-help}" in
    "enable")
        enable_chaos "$2" "$3" "$4"
        ;;
    "disable")
        disable_chaos "$2"
        ;;
    "disable-all")
        disable_all_chaos
        ;;
    "status")
        view_mesh_status
        ;;
    "kiali")
        open_kiali
        ;;
    "jaeger")
        open_jaeger
        ;;
    "experiment")
        run_experiment "$2"
        ;;
    "help"|*)
        echo -e "\n${GREEN}Usage:${NC}"
        echo "  $0 enable <service> <delay|abort> [percentage]"
        echo "  $0 disable <service>"
        echo "  $0 disable-all"
        echo "  $0 status"
        echo "  $0 kiali"
        echo "  $0 jaeger"
        echo "  $0 experiment <name>"
        echo ""
        echo -e "${GREEN}Experiments:${NC}"
        echo "  auth-slowdown      - Inject delays to auth service"
        echo "  cascade-failure    - Test cascade failure handling"
        echo "  network-partition  - Simulate network partition"
        echo "  gradual-degradation - Gradually increase delays"
        echo ""
        echo -e "${GREEN}Examples:${NC}"
        echo "  $0 enable auth-service delay 20"
        echo "  $0 enable user-service abort 30"
        echo "  $0 experiment cascade-failure"
        echo "  $0 disable-all"
        ;;
esac

