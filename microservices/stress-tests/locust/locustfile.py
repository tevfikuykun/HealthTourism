"""
Locust stress test for Health Tourism microservices
Tests Circuit Breaker thresholds and Gateway timeout limits
"""

from locust import HttpUser, task, between
import json
import random

class HealthTourismUser(HttpUser):
    wait_time = between(1, 3)  # Wait 1-3 seconds between requests
    
    def on_start(self):
        """Called when a simulated user starts"""
        self.user_id = random.randint(1, 1000)
        self.hospital_id = random.randint(1, 10)
        self.doctor_id = random.randint(1, 50)
    
    @task(3)
    def get_hospitals(self):
        """Test GET /api/hospitals - Should handle high load"""
        self.client.get("/api/hospitals", name="Get Hospitals")
    
    @task(2)
    def get_doctors(self):
        """Test GET /api/doctors - Standard endpoint"""
        self.client.get(f"/api/doctors/hospital/{self.hospital_id}", name="Get Doctors by Hospital")
    
    @task(1)
    def iot_monitoring(self):
        """Test GET /api/iot-monitoring - High latency endpoint (60s timeout)"""
        self.client.get(
            f"/api/iot-monitoring/user/{self.user_id}",
            name="IoT Monitoring (High Latency)",
            timeout=60
        )
    
    @task(1)
    def cost_predictor(self):
        """Test POST /api/cost-predictor/predict - AI processing endpoint"""
        payload = {
            "userId": self.user_id,
            "hospitalId": self.hospital_id,
            "doctorId": self.doctor_id,
            "procedureType": "SURGERY",
            "medicalReportHash": "test-hash",
            "medicalReportReference": "ipfs://test-cid"
        }
        self.client.post(
            "/api/cost-predictor/predict",
            json=payload,
            name="Cost Predictor (AI Processing)"
        )
    
    @task(1)
    def patient_risk_scoring(self):
        """Test POST /api/patient-risk-scoring/calculate - Risk scoring endpoint"""
        payload = {
            "userId": self.user_id,
            "reservationId": random.randint(1, 100)
        }
        self.client.post(
            "/api/patient-risk-scoring/calculate",
            json=payload,
            name="Patient Risk Scoring"
        )
    
    @task(1)
    def health_wallet_access(self):
        """Test GET /api/health-wallet/user/{userId} - Wallet access"""
        self.client.get(
            f"/api/health-wallet/user/{self.user_id}",
            name="Health Wallet Access"
        )
    
    @task(1)
    def virtual_tour(self):
        """Test GET /api/virtual-tours - VR content (120s timeout)"""
        self.client.get(
            "/api/virtual-tours",
            name="Virtual Tour (VR Content)",
            timeout=120
        )

# Run with: locust -f locustfile.py --host=http://localhost:8080 --users=100 --spawn-rate=10
