# 🔧 Apache Camel Integration Guide

## Overview

Apache Camel is the "Swiss Army Knife" of integration. It provides Enterprise Integration Patterns (EIP) for connecting different systems, protocols, and formats.

## Why Camel?

### 1. Protocol Bridging
- **FTP → Kafka**: Hospital sends files via FTP, Camel converts to Kafka messages
- **SOAP → REST**: Airline uses SOAP, Camel converts to REST/JSON
- **HL7 → JSON**: Medical systems use HL7, Camel converts to JSON
- **HTTP → MQTT**: Webhooks to IoT messaging

### 2. Enterprise Integration Patterns (EIP)
- **Splitter**: Split bulk messages into individual messages
- **Filter**: Filter messages based on conditions
- **Content-Based Router**: Route messages to different destinations
- **Aggregator**: Combine multiple messages into one
- **Transformer**: Transform message formats (JSON ↔ XML ↔ HL7)

### 3. Format Transformation
- JSON → XML (for legacy systems)
- XML → JSON (for modern APIs)
- HL7 → JSON (for FHIR adapter)
- CSV → JSON (for data imports)

## Implemented Routes

### 1. Hospital FTP Route
**Purpose**: Bridge FTP to Kafka

```java
from("ftp://hospital-ftp-server:21/reports")
    .unmarshal().json()
    .to("kafka:medical-reports")
```

**Use Case**: Hospital sends medical reports via FTP, system processes via Kafka

---

### 2. Airline SOAP Route
**Purpose**: Bridge SOAP to REST/Kafka

```java
from("cxf:bean:airlineSoapEndpoint")
    .unmarshal().soapjaxb()
    .marshal().json()
    .to("kafka:flight-bookings")
```

**Use Case**: Airline booking system uses SOAP, convert to internal format

---

### 3. Payment Gateway Route
**Purpose**: Handle payment webhooks

```java
from("rest:post:/api/integration/payment/webhook")
    .filter(simple("${body[status]} == 'SUCCESS'"))
    .to("kafka:payment-events")
```

**Use Case**: Payment gateway sends webhooks, filter and process

---

### 4. FHIR/HL7 Route
**Purpose**: Convert HL7 to JSON

```java
from("netty4:tcp://0.0.0.0:8888")
    .unmarshal().hl7()
    .process(convertHL7ToJSON)
    .to("kafka:hl7-messages")
```

**Use Case**: Hospital sends HL7 messages, convert to JSON for processing

---

### 5. Message Splitter Route
**Purpose**: Split bulk imports

```java
from("kafka:bulk-reservations")
    .split().jsonpath("$[*]")
    .to("kafka:reservations")
```

**Use Case**: Bulk reservation import, split into individual messages

---

### 6. Message Filter Route
**Purpose**: Filter high-priority notifications

```java
from("kafka:notifications")
    .filter(simple("${body[priority]} == 'HIGH'"))
    .to("kafka:high-priority-notifications")
```

**Use Case**: Route high-priority notifications to urgent queue

---

### 7. Content-Based Router Route
**Purpose**: Route based on document type

```java
from("kafka:medical-documents")
    .choice()
        .when(simple("${body[documentType]} == 'LAB_RESULT'"))
            .to("kafka:lab-results")
        .when(simple("${body[documentType]} == 'RADIOLOGY'"))
            .to("kafka:radiology-reports")
```

**Use Case**: Route medical documents to appropriate services

---

### 8. Message Transformer Route
**Purpose**: Transform formats

```java
from("kafka:json-messages")
    .marshal().jacksonxml()
    .to("kafka:xml-messages")
```

**Use Case**: Convert JSON to XML for legacy systems

---

### 9. Aggregator Route
**Purpose**: Aggregate patient data fragments

```java
from("kafka:patient-data-fragments")
    .aggregate(header("patientId"), new GroupedExchangeAggregationStrategy())
    .completionSize(3)
    .to("kafka:complete-patient-data")
```

**Use Case**: Combine patient data from multiple sources

---

## API Endpoints

### Get All Routes
```bash
GET /api/integration/routes

Response:
{
  "totalRoutes": 9,
  "routes": {
    "hospital-ftp-to-kafka": {
      "status": "Started",
      "uptime": "2h 30m"
    }
  }
}
```

### Trigger Route Manually
```bash
POST /api/integration/trigger/{routeId}
Content-Type: application/json

{
  "data": "..."
}
```

### Start/Stop Route
```bash
POST /api/integration/route/{routeId}/start
POST /api/integration/route/{routeId}/stop
```

## Benefits

| Feature | Benefit |
|---------|---------|
| **Protocol Bridging** | Connect any system, any protocol |
| **EIP Patterns** | Reusable integration patterns |
| **Format Transformation** | Convert between any formats |
| **Error Handling** | Built-in retry, dead letter queue |
| **Monitoring** | Camel metrics and tracing |
| **Code Reduction** | 90% less code vs. manual integration |

## Integration Scenarios

### Scenario 1: Hospital Integration
**Problem**: Hospital uses FTP for file transfer
**Solution**: Camel FTP route → Kafka
**Result**: Seamless integration, no code changes needed

### Scenario 2: Airline Integration
**Problem**: Airline uses SOAP, we use REST
**Solution**: Camel SOAP route → JSON → Kafka
**Result**: Protocol abstraction, easy to maintain

### Scenario 3: Payment Gateway
**Problem**: Multiple payment gateways, different formats
**Solution**: Camel routes for each gateway
**Result**: Unified internal format, easy to add new gateways

### Scenario 4: HL7/FHIR
**Problem**: Medical systems use HL7, we use JSON
**Solution**: Camel HL7 route → JSON transformation
**Result**: FHIR adapter simplified

## Monitoring

Camel provides built-in monitoring:
- Route status (started/stopped)
- Message throughput
- Error rates
- Processing times

Access via:
- JMX
- Camel metrics endpoint
- Integration with Prometheus/Grafana

---

## 🎯 Result

**"Integration Swiss Army Knife"**

- Connect any system, any protocol
- Reusable integration patterns
- 90% less code
- Easy to maintain and extend



