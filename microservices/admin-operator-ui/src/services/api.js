import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_GATEWAY_URL || 'http://localhost:8080';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add auth token to requests
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('adminToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const api = {
  reservationService: {
    getAll: () => apiClient.get('/api/reservations'),
    updateStatus: (id, status) => apiClient.put(`/api/reservations/${id}/status?status=${status}`),
  },
  
  apiKeyService: {
    getAllApiKeys: () => apiClient.get('/api/api-keys'),
    generateApiKey: (data) => apiClient.post('/api/api-keys/generate', data),
    updateStatus: (id, active) => apiClient.put(`/api/api-keys/${id}/status?active=${active}`),
    getUsageStats: (id) => apiClient.get(`/api/api-keys/${id}/usage`),
  },
  
  securityAuditService: {
    getAuditLogs: () => apiClient.get('/api/security/audit-logs'),
    getSuspiciousActivity: () => apiClient.get('/api/security/suspicious-activity'),
  },
};

export default api;
