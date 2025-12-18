# 🏗️ Health Tourism Platform - Architecture Map

## System Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENT LAYER                             │
├─────────────────────────────────────────────────────────────────┤
│  Web App (React)          │  Mobile App (Future)                │
│  - Super-App (/super-app)  │  - React Native/Flutter            │
│  - 50+ Pages               │  - Push Notifications              │
└──────────────┬──────────────────────────────┬───────────────────┘
               │                              │
               └──────────────┬───────────────┘
                              │
┌─────────────────────────────▼──────────────────────────────────┐
│                    API GATEWAY LAYER                            │
│  Port: 8080                                                     │
│  - Spring Cloud Gateway                                         │
│  - 88+ Routes                                                   │
│  - Rate Limiting                                                │
│  - CORS Configuration                                           │
│  - Timeout Management (IoT: 60s, VR: 120s)                     │
└──────────────┬──────────────────────────────────────────────────┘
               │
               ├─────────────────────────────────────────────┐
               │                                             │
┌──────────────▼──────────────┐    ┌─────────────────────────▼────┐
│   SERVICE DISCOVERY         │    │   CONFIG SERVER              │
│   Eureka Server             │    │   Port: 8888                 │
│   Port: 8761                │    │   - Centralized Config      │
│   (3 replicas for HA)       │    │   - Git-based                │
└─────────────────────────────┘    └─────────────────────────────┘
               │
               │
┌──────────────▼───────────────────────────────────────────────────┐
│                    CORE SERVICES LAYER                           │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │   Auth       │  │   User       │  │  Hospital    │          │
│  │   Port: 8001 │  │   Port: 8001 │  │  Port: 8002  │          │
│  └──────────────┘  └──────────────┘  └──────────────┘          │
│                                                                  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │   Doctor     │  │ Accommodation│  │   Flight     │          │
│  │   Port: 8003 │  │   Port: 8004 │  │  Port: 8005  │          │
│  └──────────────┘  └──────────────┘  └──────────────┘          │
│                                                                  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │ Car Rental   │  │   Transfer   │  │   Package    │          │
│  │   Port: 8006 │  │   Port: 8007 │  │  Port: 8008  │          │
│  └──────────────┘  └──────────────┘  └──────────────┘          │
│                                                                  │
│  ┌──────────────┐                                               │
│  │ Reservation  │  (With Resilience4j Circuit Breaker)        │
│  │   Port: 8009 │                                               │
│  └──────────────┘                                               │
└──────────────────────────────────────────────────────────────────┘
               │
               │
┌──────────────▼───────────────────────────────────────────────────┐
│              ADVANCED & AI SERVICES LAYER                        │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────────────┐  ┌──────────────────┐                    │
│  │ Cost Predictor   │  │ Risk Scoring     │                    │
│  │ Port: 8033       │  │ Port: 8036       │                    │
│  │ (AI + Hidden     │  │ (AI Explainability)                   │
│  │  Costs Analysis) │  │                  │                    │
│  └──────────────────┘  └──────────────────┘                    │
│                                                                  │
│  ┌──────────────────┐  ┌──────────────────┐                    │
│  │ Health Wallet    │  │ AI Companion     │                    │
│  │ Port: 8037       │  │ Port: 8035       │                    │
│  │ (QR + Privacy    │  │ (RAG-based)      │                    │
│  │  Shield)         │  │                  │                    │
│  └──────────────────┘  └──────────────────┘                    │
│                                                                  │
│  ┌──────────────────┐  ┌──────────────────┐                    │
│  │ IoT Monitoring   │  │ Virtual Tour     │                    │
│  │ Port: 8032       │  │ Port: 8031       │                    │
│  │ (Post-Op)        │  │ (AR/VR)         │                    │
│  └──────────────────┘  └──────────────────┘                    │
│                                                                  │
│  ┌──────────────────┐  ┌──────────────────┐                    │
│  │ Legal Ledger     │  │ Privacy          │                    │
│  │ Port: 8034       │  │ Port: 8038       │                    │
│  │ (Blockchain)     │  │ (GDPR/HIPAA)     │                    │
│  └──────────────────┘  └──────────────────┘                    │
│                                                                  │
│  ┌──────────────────┐                                           │
│  │ Chaos Engineering│  (Fail-Safe Mode)                        │
│  │ Port: 8039       │                                           │
│  └──────────────────┘                                           │
└──────────────────────────────────────────────────────────────────┘
               │
               │
┌──────────────▼───────────────────────────────────────────────────┐
│              INFRASTRUCTURE SERVICES LAYER                        │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │ Blockchain   │  │ Gamification │  │ Translation  │          │
│  │ Port: 8030   │  │ Port: 8017   │  │ Port: 8016   │          │
│  │ (SHA-256 +   │  │ (Health      │  │ (Live <500ms)│          │
│  │  IPFS)        │  │  Tokens)     │  │              │          │
│  └──────────────┘  └──────────────┘  └──────────────┘          │
│                                                                  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │ Notification │  │ Chat         │  │ File Storage │          │
│  │ Port: 8011   │  │ Port: 8010   │  │ Port: 8013   │          │
│  └──────────────┘  └──────────────┘  └──────────────┘          │
└──────────────────────────────────────────────────────────────────┘
               │
               │
┌──────────────▼───────────────────────────────────────────────────┐
│                    DATA LAYER                                     │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │ MySQL Main    │  │ MySQL Core   │  │ MySQL Adv    │          │
│  │ Port: 3306   │  │ Services     │  │ Services     │          │
│  │ (20+ DBs)     │  │ (Ports       │  │ (Ports       │          │
│  │               │  │  3307-3315)  │  │  3341-3348)  │          │
│  └──────────────┘  └──────────────┘  └──────────────┘          │
│                                                                  │
│  ┌──────────────┐                                               │
│  │ IPFS Storage  │  (Off-chain data storage)                    │
│  │ (Pinata/      │                                               │
│  │  Infura)      │                                               │
│  └──────────────┘                                               │
└──────────────────────────────────────────────────────────────────┘
               │
               │
┌──────────────▼───────────────────────────────────────────────────┐
│              MONITORING & OBSERVABILITY LAYER                    │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │ Prometheus    │  │ Grafana      │  │ Zipkin       │          │
│  │ Port: 9090    │  │ Port: 3001   │  │ Port: 9411   │          │
│  │ (Metrics)     │  │ (Dashboards) │  │ (Tracing)    │          │
│  └──────────────┘  └──────────────┘  └──────────────┘          │
│                                                                  │
│  ┌──────────────┐                                               │
│  │ Actuator      │  (Health, Metrics, Prometheus endpoints)     │
│  │ (All Services)│                                               │
│  └──────────────┘                                               │
└──────────────────────────────────────────────────────────────────┘
```

## Data Flow Examples

### 1. Reservation Flow (with Circuit Breaker)
```
User → API Gateway → Reservation Service
                    ↓
              Doctor Service (Circuit Breaker)
              Accommodation Service (Circuit Breaker)
              Transfer Service (Circuit Breaker)
                    ↓
              Price Calculation (with Fallbacks)
                    ↓
              Database (Reservation saved)
```

### 2. AI Health Companion Flow (RAG)
```
User Question → API Gateway → AI Health Companion Service
                              ↓
                        Retrieve Medical Context:
                        - IPFS Documents (via Blockchain)
                        - IoT Data (real-time)
                        - Recovery Score
                              ↓
                        RAG Context Enrichment
                              ↓
                        Generate AI Response
                              ↓
                        Return Contextual Answer
```

### 3. Health Wallet QR Access Flow (Privacy Shield)
```
QR Code Scan → API Gateway → Health Wallet Service
                              ↓
                        Create Temporary Token (1 hour)
                        Generate Pre-signed URLs for IPFS
                              ↓
                        Return Wallet Data + URLs
                              ↓
                        Token expires after 1 hour
                        (or revoked by patient)
```

### 4. Cost Prediction Flow (Hidden Costs Analysis)
```
Request → API Gateway → Cost Predictor Service
                        ↓
                  Retrieve Medical Report (IPFS)
                  Analyze Historical Reservations
                  Calculate Hidden Costs Trend
                  Apply Trend Multiplier
                        ↓
                  Return Prediction (±5% accuracy)
```

## Resilience Patterns

### Circuit Breaker Pattern
```
Normal → Closed State (requests pass through)
  ↓
High Error Rate → Open State (requests fail fast)
  ↓
After Wait Duration → Half-Open (test requests)
  ↓
Success → Closed State (recovered)
```

### Fail-Safe Pattern
```
Request → Privacy Service Check
  ↓
Available → Normal Authorization
  ↓
Unavailable → Fail-Safe Mode (Deny All)
```

## Security Layers

```
┌─────────────────────────────────────┐
│  API Gateway (Rate Limiting)       │
├─────────────────────────────────────┤
│  JWT Authentication (RBAC)         │
├─────────────────────────────────────┤
│  Privacy Compliance (GDPR/HIPAA)    │
├─────────────────────────────────────┤
│  IPFS Encryption (User-specific)    │
├─────────────────────────────────────┤
│  Blockchain (Immutable Records)     │
└─────────────────────────────────────┘
```

## Technology Stack Summary

### Backend
- **Framework**: Spring Boot 4.0
- **Cloud**: Spring Cloud (Gateway, Eureka, Config)
- **Resilience**: Resilience4j (Circuit Breaker, Retry)
- **Tracing**: Micrometer Tracing + Zipkin
- **Security**: Spring Security + JWT
- **Validation**: Jakarta Validation

### Frontend
- **Framework**: React
- **State**: Redux Toolkit
- **Data Fetching**: React Query
- **UI**: Material-UI
- **Routing**: React Router DOM

### Infrastructure
- **Containerization**: Docker
- **Orchestration**: Kubernetes (ready)
- **Monitoring**: Prometheus + Grafana
- **Service Discovery**: Eureka
- **Config Management**: Spring Cloud Config

### Advanced Technologies
- **Blockchain**: SHA-256 + IPFS
- **AI/ML**: RAG (Retrieval-Augmented Generation)
- **IoT**: Real-time monitoring
- **AR/VR**: WebRTC-based tours
- **Translation**: Speech-to-Text APIs

## Service Communication Patterns

### Synchronous (REST)
- Service-to-Service via RestTemplate
- Through API Gateway
- With Circuit Breaker protection

### Asynchronous (Future)
- RabbitMQ/Kafka for events
- Event-driven architecture
- Saga pattern for distributed transactions

## Scalability Strategy

### Horizontal Scaling
- Kubernetes HPA (Horizontal Pod Autoscaler)
- Auto-scaling based on CPU/Memory
- Load balancing via Service mesh

### Vertical Scaling
- Resource limits per service
- Database connection pooling
- Caching strategies (Redis)

## High Availability Strategy

### Service Replication
- API Gateway: 3-10 replicas
- Eureka: 3 replicas (quorum)
- Core Services: 2-3 replicas
- Advanced Services: 2 replicas

### Database Strategy
- Master-Slave replication (future)
- Connection pooling
- Read replicas for scaling

## Disaster Recovery

### Backup Strategy
- Database backups (daily)
- IPFS data redundancy
- Blockchain immutable records

### Recovery Procedures
- Fail-safe modes
- Circuit Breaker fallbacks
- Service health monitoring

---

**Architecture Version**: 4.0.0
**Last Updated**: 2024-01-15
