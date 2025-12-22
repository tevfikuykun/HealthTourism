# Kubernetes Deployment Guide

## Overview

Bu klasör, Health Tourism platformunu Kubernetes'e deploy etmek için gerekli tüm konfigürasyon dosyalarını içerir.

> Not: Repo içinde ayrıca `kubernetes/istio/` altında daha güncel (Istio sidecar + daha kapsamlı) manifest seti bulunur. CI/CD deploy workflow şu an `kubernetes/istio/` yolunu kullanacak şekilde güncellenmiştir.

## Prerequisites

- Kubernetes cluster (v1.24+)
- kubectl configured
- Docker images built and pushed to registry
- Ingress controller (NGINX) installed
- cert-manager (optional, for SSL)

## Deployment Steps

### 1. Create Namespace
```bash
kubectl apply -f namespace.yaml
```

### 2. Create ConfigMap and Secrets
```bash
# Update secrets.yaml with your actual secrets first!
kubectl apply -f secrets.yaml
kubectl apply -f configmap.yaml
```

### 3. Deploy Infrastructure
```bash
# Eureka Server (Service Discovery)
kubectl apply -f eureka-server-deployment.yaml

# MySQL Database
kubectl apply -f mysql-deployment.yaml

# Wait for Eureka to be ready
kubectl wait --for=condition=ready pod -l app=eureka-server -n health-tourism --timeout=300s
```

### 4. Deploy Microservices

#### Option A: Deploy All Services (Using Template)
```bash
# Generate deployment files for each service
./generate-deployments.sh

# Deploy all services
kubectl apply -f deployments/
```

#### Option B: Deploy Individual Services
```bash
# Example: Deploy API Gateway
kubectl apply -f api-gateway-deployment.yaml

# Deploy other services similarly
```

### 5. Deploy API Gateway
```bash
kubectl apply -f api-gateway-deployment.yaml
```

### 6. Setup Ingress (Optional)
```bash
kubectl apply -f ingress.yaml
```

### 7. Setup Auto-Scaling
```bash
kubectl apply -f hpa-api-gateway.yaml
```

## Service List

### Core Services
- api-gateway (3 replicas, HPA enabled)
- eureka-server (3 replicas)
- reservation-service (2 replicas)
- hospital-service (2 replicas)
- doctor-service (2 replicas)

### Advanced Services
- cost-predictor-service (2 replicas)
- patient-risk-scoring-service (2 replicas)
- health-wallet-service (2 replicas)
- ai-health-companion-service (2 replicas)
- iot-monitoring-service (2 replicas)
- privacy-compliance-service (2 replicas)
- chaos-engineering-service (2 replicas)

## High Availability Setup

### Recommended Replicas
- **API Gateway**: 3-10 (with HPA)
- **Eureka Server**: 3 (for quorum)
- **Core Services**: 2-3 replicas each
- **Advanced Services**: 2 replicas each

### Resource Limits
- **API Gateway**: 512Mi-1Gi memory, 500m-1000m CPU
- **Core Services**: 256Mi-512Mi memory, 250m-500m CPU
- **Advanced Services**: 256Mi-512Mi memory, 250m-500m CPU

## Monitoring

### Prometheus Scraping
All services expose metrics at `/actuator/prometheus`

### Health Checks
- Liveness: `/actuator/health`
- Readiness: `/actuator/health`

## Zero-Downtime Deployment

### Rolling Update Strategy
```yaml
strategy:
  type: RollingUpdate
  rollingUpdate:
    maxSurge: 1
    maxUnavailable: 0
```

### Blue-Green Deployment (Optional)
Use ArgoCD or similar tools for blue-green deployments.

## Cloud Provider Specific

### AWS (EKS)
```bash
# Create EKS cluster
eksctl create cluster --name health-tourism --region us-east-1

# Deploy
kubectl apply -f .
```

### Google Cloud (GKE)
```bash
# Create GKE cluster
gcloud container clusters create health-tourism --zone us-central1-a

# Deploy
kubectl apply -f .
```

### Azure (AKS)
```bash
# Create AKS cluster
az aks create --resource-group health-tourism --name health-tourism-cluster

# Deploy
kubectl apply -f .
```

## Troubleshooting

### Check Pod Status
```bash
kubectl get pods -n health-tourism
```

### Check Service Status
```bash
kubectl get svc -n health-tourism
```

### View Logs
```bash
kubectl logs -f deployment/api-gateway -n health-tourism
```

### Describe Pod
```bash
kubectl describe pod <pod-name> -n health-tourism
```

## Production Checklist

- [ ] Secrets updated with production values
- [ ] Resource limits configured
- [ ] HPA configured for critical services
- [ ] Ingress configured with SSL
- [ ] Monitoring setup (Prometheus/Grafana)
- [ ] Backup strategy for databases
- [ ] Disaster recovery plan
- [ ] Load testing completed
