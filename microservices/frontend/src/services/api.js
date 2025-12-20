import axios from 'axios';
import { handleApiError } from '../utils/errorHandler';
import { toast } from 'react-toastify';

// Use proxy in development, full URL in production
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 
  (import.meta.env.DEV ? '/api' : 'http://localhost:8080/api');

// Toast throttling - prevent duplicate error messages
const errorToastCache = new Map();
const TOAST_THROTTLE_TIME = 5000; // 5 seconds
const MAX_TOAST_CACHE_SIZE = 50;

// Clean old cache entries periodically
setInterval(() => {
  const now = Date.now();
  for (const [key, timestamp] of errorToastCache.entries()) {
    if (now - timestamp > TOAST_THROTTLE_TIME) {
      errorToastCache.delete(key);
    }
  }
  // Prevent memory leak
  if (errorToastCache.size > MAX_TOAST_CACHE_SIZE) {
    const firstKey = errorToastCache.keys().next().value;
    errorToastCache.delete(firstKey);
  }
}, TOAST_THROTTLE_TIME);

// Helper function to show throttled toast
const showThrottledToast = (message, errorCode, config = {}) => {
  const cacheKey = `${errorCode}_${message}`;
  const lastShown = errorToastCache.get(cacheKey);
  const now = Date.now();
  
  // Use toastId from config if provided to prevent duplicates
  const toastId = config.toastId || cacheKey;
  
  // Check if this toast is already active (prevents duplicate toasts)
  try {
    if (toast.isActive && toast.isActive(toastId)) {
      return; // Don't show duplicate toast
    }
  } catch (e) {
    // toast.isActive might not be available in all versions
  }
  
  // Only show toast if not shown recently OR if toastId is provided (unique toast)
  if (!lastShown || (now - lastShown) > TOAST_THROTTLE_TIME || config.toastId) {
    errorToastCache.set(cacheKey, now);
    
    // For network errors, show as warning instead of error (less intrusive)
    if (errorCode === 'NETWORK_ERROR') {
      toast.warning(message, {
        autoClose: 3000,
        hideProgressBar: false,
        toastId, // Use toastId to prevent duplicates
        ...config
      });
    } else {
      toast.error(message, {
        autoClose: 4000,
        hideProgressBar: false,
        toastId, // Use toastId to prevent duplicates
        ...config
      });
    }
  }
};

// Track if backend is reachable
let isBackendReachable = true;
let lastBackendCheck = 0;
const BACKEND_CHECK_INTERVAL = 30000; // 30 seconds

// 1. DÜZELTME: 'api' değişkeninin başına 'export' eklendi (currency.js hatası için)
export const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 30000,
});

// Request interceptor - Auth token ekleme
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    // Add flag to suppress toast for specific requests
    if (config.suppressErrorToast) {
      config._suppressToast = true;
    }
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor - Global hata yönetimi
api.interceptors.response.use(
  (response) => {
    // Backend is reachable
    isBackendReachable = true;
    lastBackendCheck = Date.now();
    return response;
  },
  (error) => {
    const appError = handleApiError(error);
    const config = error.config || {};
    
    // Check if toast should be suppressed
    // Suppress toast for auth endpoints (register/login) - they handle errors themselves
    const isAuthEndpoint = config.url?.includes('/auth/register') || config.url?.includes('/auth/login');
    
    if (config._suppressToast || config.suppressErrorToast || isAuthEndpoint) {
      return Promise.reject(appError);
    }
    
    // Update backend reachability
    if (appError.code === 'NETWORK_ERROR') {
      const now = Date.now();
      if (now - lastBackendCheck > BACKEND_CHECK_INTERVAL) {
        isBackendReachable = false;
        lastBackendCheck = now;
      }
    }
    
    if (appError.statusCode === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
      return Promise.reject(appError);
    }
    
    // Only show toast for non-401 errors
    // Network errors are throttled and shown as warnings
    if (appError.statusCode !== 401) {
      // For network errors, only show if backend was previously reachable
      // This prevents spam when backend is completely down
      if (appError.code === 'NETWORK_ERROR') {
        if (isBackendReachable || Date.now() - lastBackendCheck > BACKEND_CHECK_INTERVAL) {
          showThrottledToast(
            'Connection issue. Some features may be unavailable.',
            appError.code,
            { toastId: 'network-error' } // Same toastId prevents duplicates
          );
        }
      } else {
        // For other errors, show normally but throttled
        // Use a unique toastId based on error code and status to prevent duplicates
        // Also include a timestamp component to allow same error after throttle period
        const toastId = `error-${appError.code}-${appError.statusCode || 'unknown'}-${Math.floor(Date.now() / TOAST_THROTTLE_TIME)}`;
        showThrottledToast(appError.message, appError.code, {
          toastId: toastId,
          autoClose: appError.statusCode === 500 ? 6000 : 4000 // Longer for server errors
        });
      }
    }
    
    return Promise.reject(appError);
  }
);

// --- API SERVICES ---

export const authService = {
  register: (data, config = {}) => {
    const requestConfig = {
      ...config,
      suppressErrorToast: true,
      _suppressToast: true, // Also suppress in interceptor
    };
    return api.post('/auth/register', data, requestConfig);
  },
  login: (data, config = {}) => {
    const requestConfig = {
      ...config,
      suppressErrorToast: true,
      _suppressToast: true, // Also suppress in interceptor
    };
    return api.post('/auth/login', data, requestConfig);
  },
  logout: (refreshToken) => api.post('/auth/logout', { refreshToken }),
  refreshToken: (refreshToken) => api.post('/auth/refresh', { refreshToken }),
  validateToken: (token) => api.post('/auth/validate', { token }),
  verifyEmail: (token) => api.post('/auth/verify-email', null, { params: { token } }),
  resendVerification: (email) => api.post('/auth/resend-verification', { email }),
  forgotPassword: (email) => api.post('/auth/forgot-password', { email }),
  resetPassword: (token, newPassword) => api.post('/auth/reset-password', { token, newPassword }),
  socialLogin: (data) => api.post('/auth/social/login', data),
};

export const userService = {
  getAll: () => api.get('/users'),
  getById: (id) => api.get(`/users/${id}`),
  create: (user) => api.post('/users', user),
  update: (id, user) => api.put(`/users/${id}`, user),
  delete: (id) => api.delete(`/users/${id}`),
  getProfile: () => api.get('/users/profile'),
  updateProfile: (data) => api.put('/users/profile', data),
};

export const hospitalService = {
  getAll: (params) => api.get('/hospitals', { params }),
  getById: (id) => api.get(`/hospitals/${id}`),
  getBySlug: (slug) => api.get(`/hospitals/slug/${slug}`),
  search: (query) => api.get('/hospitals/search', { params: { q: query } }),
};

export const doctorService = {
  getAll: (params) => api.get('/doctors', { params }),
  getById: (id) => api.get(`/doctors/${id}`),
  getByHospital: (hospitalId) => api.get(`/doctors/hospital/${hospitalId}`),
  getBySpecialization: (specialization) => api.get(`/doctors/specialization/${specialization}`),
  getTopRatedByHospital: (hospitalId) => api.get(`/doctors/hospital/${hospitalId}/top-rated`),
  create: (doctor) => api.post('/doctors', doctor),
  uploadImage: (id, formData) => api.post(`/doctors/${id}/upload-image`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }),
};

// 2. DÜZELTME: VideoConsultation.jsx hatası için eksik servis eklendi
export const videoConsultationService = {
  getAll: () => api.get('/video-consultations'),
  getById: (id) => api.get(`/video-consultations/${id}`),
  create: (data) => api.post('/video-consultations', data),
  updateStatus: (id, status) => api.put(`/video-consultations/${id}/status`, null, { params: { status } }),
  getByUser: (userId) => api.get(`/video-consultations/user/${userId}`),
  getByDoctor: (doctorId) => api.get(`/video-consultations/doctor/${doctorId}`),
  generateRoom: (id) => api.post(`/video-consultations/${id}/generate-room`),
};

export const accommodationService = {
  getAll: (params) => api.get('/accommodations', { params }),
  getById: (id) => api.get(`/accommodations/${id}`),
  getByHospital: (hospitalId) => api.get(`/accommodations/hospital/${hospitalId}`),
  getNearHospital: (hospitalId) => api.get(`/accommodations/hospital/${hospitalId}/near`),
  getByPrice: (maxPrice) => api.get('/accommodations/price', { params: { maxPrice } }),
  create: (accommodation) => api.post('/accommodations', accommodation),
};

export const flightService = {
  getAll: () => api.get('/flights'),
  search: (departureCity, arrivalCity) => api.get('/flights/search', { params: { departureCity, arrivalCity } }),
  getByClass: (flightClass) => api.get(`/flights/class/${flightClass}`),
  getByPrice: (maxPrice) => api.get('/flights/price', { params: { maxPrice } }),
  getById: (id) => api.get(`/flights/${id}`),
};

export const carRentalService = {
  getAll: () => api.get('/car-rentals'),
  getByType: (carType) => api.get(`/car-rentals/type/${carType}`),
  getByPrice: (maxPrice) => api.get('/car-rentals/price', { params: { maxPrice } }),
  getById: (id) => api.get(`/car-rentals/${id}`),
};

export const transferService = {
  getAll: () => api.get('/transfers'),
  getByType: (serviceType) => api.get(`/transfers/type/${serviceType}`),
  getByPrice: (maxPrice) => api.get('/transfers/price', { params: { maxPrice } }),
  getById: (id) => api.get(`/transfers/${id}`),
};

export const packageService = {
  getAll: (params) => api.get('/packages', { params }),
  getById: (id) => api.get(`/packages/${id}`),
  getBySlug: (slug) => api.get(`/packages/slug/${slug}`),
  search: (query) => api.get('/packages/search', { params: { q: query } }),
};

export const paymentService = {
  processPayment: (payment) => api.post('/payments', payment),
  getByUser: (userId) => api.get(`/payments/user/${userId}`),
  getById: (id) => api.get(`/payments/${id}`),
};

export const reservationService = {
  create: (reservation) => api.post('/reservations', reservation),
  getAll: (params) => api.get('/reservations', { params }),
  getById: (id) => api.get(`/reservations/${id}`),
  update: (id, reservation) => api.put(`/reservations/${id}`, reservation),
  cancel: (id) => api.delete(`/reservations/${id}`),
};

export const quoteService = {
  create: (quote) => api.post('/quotes', quote),
  getById: (id) => api.get(`/quotes/${id}`),
  getByUser: (userId) => api.get(`/quotes/user/${userId}`),
  accept: (id) => api.post(`/quotes/${id}/accept`),
  reject: (id, reason) => api.post(`/quotes/${id}/reject`, { reason }),
};

export const crmService = {
  createLead: (lead) => api.post('/crm/leads', lead),
  getLeads: () => api.get('/crm/leads'),
  updateStatus: (id, status) => api.put(`/crm/leads/${id}/status`, { status }),
};

export const fileStorageService = {
  upload: (formData, config) => api.post('/files/upload', formData, { ...config, headers: { 'Content-Type': 'multipart/form-data' } }),
  getById: (id) => api.get(`/files/${id}`),
  delete: (id) => api.delete(`/files/${id}`),
};

export const currencyService = {
  getRates: (baseCurrency = 'TRY') => api.get(`/currency/rates/${baseCurrency}`),
  convert: (amount, from, to) => api.get('/currency/convert', { params: { amount, from, to } }),
};

export const blockchainService = {
  verify: (hash) => api.get(`/blockchain/verify/${hash}`),
  getRecords: () => api.get('/blockchain/records'),
};

export const notificationService = {
  getAll: (params) => api.get('/notifications', { params }),
  getById: (id) => api.get(`/notifications/${id}`),
  getByUser: (userId) => api.get(`/notifications/user/${userId || 'me'}`),
  markAsRead: (id) => api.put(`/notifications/${id}/read`),
  markAllAsRead: (userId) => api.put(`/notifications/user/${userId || 'me'}/read-all`),
  delete: (id) => api.delete(`/notifications/${id}`),
  create: (notification) => api.post('/notifications', notification),
};

export const chatService = {
  getConversations: (userId) => api.get(`/chat/conversations/user/${userId}`),
  getMessages: (conversationId) => api.get(`/chat/conversations/${conversationId}/messages`),
  createConversation: (data) => api.post('/chat/conversations', data),
  sendMessage: (data) => api.post('/chat/messages', data),
  markAsRead: (messageId) => api.put(`/chat/messages/${messageId}/read`),
  deleteConversation: (conversationId) => api.delete(`/chat/conversations/${conversationId}`),
};

export const favoriteService = {
  getAll: (userId) => api.get(`/favorites/user/${userId}`),
  getByType: (userId, itemType) => api.get(`/favorites/user/${userId}/type/${itemType}`),
  add: (userId, itemType, itemId) => api.post('/favorites', { userId, itemType, itemId }),
  remove: (userId, itemType, itemId) => api.delete(`/favorites/user/${userId}/type/${itemType}/item/${itemId}`),
  isFavorite: (userId, itemType, itemId) => api.get(`/favorites/user/${userId}/type/${itemType}/item/${itemId}/check`),
  check: (userId, itemType, itemId) => api.get(`/favorites/user/${userId}/type/${itemType}/item/${itemId}/check`),
};

export const reviewService = {
  getAll: (params) => api.get('/reviews', { params }),
  getById: (id) => api.get(`/reviews/${id}`),
  getByService: (serviceId, serviceType) => api.get(`/reviews/service/${serviceType}/${serviceId}`),
  create: (review) => api.post('/reviews', review),
  update: (id, review) => api.put(`/reviews/${id}`, review),
  delete: (id) => api.delete(`/reviews/${id}`),
};

export const contactService = {
  send: (data) => api.post('/contact', data),
  getAll: (params) => api.get('/contact', { params }),
  getById: (id) => api.get(`/contact/${id}`),
};

export const healthRecordsService = {
  getAll: () => api.get('/health-records'),
  getById: (id) => api.get(`/health-records/${id}`),
  getByUser: (userId) => api.get(`/health-records/user/${userId}`),
  create: (record) => api.post('/health-records', record),
  update: (id, record) => api.put(`/health-records/${id}`, record),
  delete: (id) => api.delete(`/health-records/${id}`),
};

export const analyticsService = {
  getRevenue: (params) => api.get('/analytics/revenue', { params }),
  getUsers: (params) => api.get('/analytics/users', { params }),
  getReservations: (params) => api.get('/analytics/reservations', { params }),
  getServices: (params) => api.get('/analytics/services', { params }),
  getDashboard: () => api.get('/analytics/dashboard'),
};

export const adminService = {
  getUsers: (params) => api.get('/admin/users', { params }),
  getUserById: (id) => api.get(`/admin/users/${id}`),
  updateUser: (id, user) => api.put(`/admin/users/${id}`, user),
  deleteUser: (id) => api.delete(`/admin/users/${id}`),
  getStats: () => api.get('/admin/stats'),
  getReports: (params) => api.get('/admin/reports', { params }),
};

// Smart Insurance Service (Blockchain-backed)
export const smartInsuranceService = {
  createPolicy: (policyData) => api.post('/insurance/smart/policy', policyData),
  getPoliciesByUser: (userId) => api.get(`/insurance/smart/policy/user/${userId}`),
  getPolicyByReservation: (reservationId) => api.get(`/insurance/smart/policy/reservation/${reservationId}`),
  verifyPolicy: (policyId) => api.post(`/insurance/smart/policy/${policyId}/verify`),
  claimInsurance: (policyId, claimData) => api.post(`/insurance/smart/policy/${policyId}/claim`, claimData),
};

// Virtual Tour Service (AR/VR)
export const virtualTourService = {
  getAll: () => api.get('/virtual-tours'),
  getByType: (tourType) => api.get(`/virtual-tours/type/${tourType}`),
  getByEntity: (entityId, tourType) => api.get(`/virtual-tours/entity/${entityId}`, { params: { tourType } }),
  getById: (id) => api.get(`/virtual-tours/${id}`),
  getTourByEntity: (entityId, tourType) => api.get(`/virtual-tours/entity/${entityId}/tour`, { params: { tourType } }),
  create: (tour) => api.post('/virtual-tours', tour),
  update: (id, tour) => api.put(`/virtual-tours/${id}`, tour),
  rate: (id, rating) => api.post(`/virtual-tours/${id}/rate`, { rating }),
  getTopRated: (limit = 10) => api.get('/virtual-tours/top-rated', { params: { limit } }),
};

// IoT Monitoring Service (Post-Op Monitoring)
export const iotMonitoringService = {
  recordData: (data) => api.post('/iot-monitoring/data', data),
  getByUser: (userId) => api.get(`/iot-monitoring/user/${userId}`),
  getRecentByUser: (userId, hours = 24) => api.get(`/iot-monitoring/user/${userId}/recent`, { params: { hours } }),
  getLatestByUser: (userId) => api.get(`/iot-monitoring/user/${userId}/latest`),
  getByReservation: (reservationId) => api.get(`/iot-monitoring/reservation/${reservationId}`),
  getByDoctor: (doctorId) => api.get(`/iot-monitoring/doctor/${doctorId}`),
  getAlerts: (alertStatus) => api.get(`/iot-monitoring/alerts/${alertStatus}`),
};

// Cost Predictor Service (AI-Powered Medical Cost Prediction)
export const costPredictorService = {
  predictCost: (predictionData) => api.post('/cost-predictor/predict', predictionData),
  getByUser: (userId) => api.get(`/cost-predictor/user/${userId}`),
  getById: (id) => api.get(`/cost-predictor/${id}`),
};

// Health Tokens Service (Gamified Rehabilitation)
export const healthTokenService = {
  awardRehabilitation: (tokenData) => api.post('/gamification/health-tokens/rehabilitation', tokenData),
  awardMedicationCompliance: (tokenData) => api.post('/gamification/health-tokens/medication-compliance', tokenData),
  awardHealthyLifestyle: (tokenData) => api.post('/gamification/health-tokens/healthy-lifestyle', tokenData),
  redeem: (tokenId, redemptionData) => api.post(`/gamification/health-tokens/${tokenId}/redeem`, redemptionData),
  getBalance: (userId) => api.get(`/gamification/health-tokens/user/${userId}/balance`),
  getByUser: (userId) => api.get(`/gamification/health-tokens/user/${userId}`),
};

// Live Translation Service (Cultural & Language Concierge)
export const liveTranslationService = {
  startSession: (sessionData) => api.post('/translation/live/session/start', sessionData),
  translateSpeech: (sessionId, audioData) => api.post(`/translation/live/session/${sessionId}/translate`, audioData),
  translateText: (textData) => api.post('/translation/live/translate-text', textData),
  endSession: (sessionId) => api.post(`/translation/live/session/${sessionId}/end`),
  getByConsultation: (consultationId) => api.get(`/translation/live/session/consultation/${consultationId}`),
  getByUser: (userId) => api.get(`/translation/live/session/user/${userId}`),
};

// Legal Ledger Service (Blockchain Time-stamped Documents)
export const legalLedgerService = {
  createDocument: (documentData) => api.post('/legal-ledger/document', documentData),
  signDocument: (documentId, signatureData) => api.post(`/legal-ledger/document/${documentId}/sign`, signatureData),
  verifyDocument: (documentId) => api.post(`/legal-ledger/document/${documentId}/verify`),
  getByUser: (userId) => api.get(`/legal-ledger/document/user/${userId}`),
  getByReservation: (reservationId) => api.get(`/legal-ledger/document/reservation/${reservationId}`),
  getById: (id) => api.get(`/legal-ledger/document/${id}`),
};

// AI Health Companion Service (7/24 Digital Nurse)
export const aiHealthCompanionService = {
  askQuestion: (questionData) => api.post('/ai-health-companion/ask', questionData),
  getByUser: (userId) => api.get(`/ai-health-companion/user/${userId}`),
  getByReservation: (reservationId) => api.get(`/ai-health-companion/reservation/${reservationId}`),
  getById: (id) => api.get(`/ai-health-companion/${id}`),
};

// Patient Risk Scoring Service (AI-Driven Recovery Score)
export const patientRiskScoringService = {
  calculateScore: (requestData) => api.post('/patient-risk-scoring/calculate', requestData),
  getLatestScore: (userId, reservationId) => api.get(`/patient-risk-scoring/user/${userId}/reservation/${reservationId}`),
  getScoreHistory: (userId, reservationId) => api.get(`/patient-risk-scoring/user/${userId}/reservation/${reservationId}/history`),
  getScoresRequiringAlert: () => api.get('/patient-risk-scoring/alerts'),
};

// Health Wallet Service (Unified Health Wallet with QR Code)
export const healthWalletService = {
  createOrUpdate: (requestData) => api.post('/health-wallet/create', requestData),
  getByUserId: (userId) => api.get(`/health-wallet/user/${userId}`),
  getByWalletId: (walletId) => api.get(`/health-wallet/wallet/${walletId}`),
  accessByQR: (qrCodeHash) => api.get(`/health-wallet/qr/${qrCodeHash}`),
  getCompleteData: (userId) => api.get(`/health-wallet/user/${userId}/complete`),
};

// Blockchain Wallet Service (Polygon Network)
export const blockchainWalletService = {
  getBalance: (walletAddress) => api.get(`/blockchain/polygon/wallet/${walletAddress}/balance`),
  getTransactions: (walletAddress, params) => api.get(`/blockchain/polygon/wallet/${walletAddress}/transactions`, { params }),
  getHealthTokens: (walletAddress) => api.get(`/blockchain/polygon/wallet/${walletAddress}/tokens`),
  getNFTs: (walletAddress) => api.get(`/blockchain/polygon/wallet/${walletAddress}/nfts`),
  getEscrowBalance: (reservationId) => api.get(`/blockchain/polygon/escrow/${reservationId}/balance`),
  getNetworkStatus: () => api.get('/blockchain/polygon/status'),
};

// Clinical Decision Support Service (GraphRAG/Neo4j)
export const clinicalDecisionService = {
  getSimilarCases: (patientId, params) => api.get(`/clinical-decision/patient/${patientId}/similar-cases`, { params }),
  getRiskAnalysis: (patientId) => api.get(`/clinical-decision/patient/${patientId}/risk-analysis`),
  getIotCorrelation: (patientId, hours = 24) => api.get(`/clinical-decision/patient/${patientId}/iot-correlation`, { params: { hours } }),
  getGraphInsights: (patientId) => api.get(`/clinical-decision/patient/${patientId}/graph-insights`),
  getEvidenceBasedRecommendations: (patientId) => api.get(`/clinical-decision/patient/${patientId}/recommendations`),
};

// Audit & Transparency Service (Hibernate Envers/Zipkin)
export const auditService = {
  getAuditLogs: (params) => api.get('/audit/logs', { params }),
  getAuditLogsByUser: (userId, params) => api.get(`/audit/logs/user/${userId}`, { params }),
  getAuditLogsByResource: (resourceType, resourceId, params) => api.get(`/audit/logs/resource/${resourceType}/${resourceId}`, { params }),
  getTraceById: (traceId) => api.get(`/audit/trace/${traceId}`),
  getTracesByUser: (userId, params) => api.get(`/audit/traces/user/${userId}`, { params }),
  getDataAccessLogs: (params) => api.get('/audit/data-access', { params }),
  exportAuditLogs: (params) => api.get('/audit/export', { params, responseType: 'blob' }),
};

// Security & Vault Service (Quantum-Safe, Zero-Trust)
export const securityService = {
  getActiveSessions: (userId) => api.get(`/security/sessions/user/${userId}`),
  revokeSession: (sessionId) => api.delete(`/security/sessions/${sessionId}`),
  revokeAllSessions: (userId) => api.delete(`/security/sessions/user/${userId}/all`),
  getAccessPermissions: (userId) => api.get(`/security/permissions/user/${userId}`),
  getKeyRotationStatus: () => api.get('/security/vault/key-rotation/status'),
  getQuantumSafeKeys: () => api.get('/security/vault/quantum-safe-keys'),
  rotateKey: (keyId) => api.post(`/security/vault/keys/${keyId}/rotate`),
  getZeroTrustStatus: () => api.get('/security/zero-trust/status'),
};

// Patient Journey Service
export const patientJourneyService = {
  getJourney: (patientId, reservationId) => api.get(`/patient-journey/patient/${patientId}/reservation/${reservationId}`),
  getJourneySteps: (patientId, reservationId) => api.get(`/patient-journey/patient/${patientId}/reservation/${reservationId}/steps`),
  updateStepStatus: (patientId, reservationId, stepId, status) => api.put(`/patient-journey/patient/${patientId}/reservation/${reservationId}/steps/${stepId}`, { status }),
  getCurrentStep: (patientId, reservationId) => api.get(`/patient-journey/patient/${patientId}/reservation/${reservationId}/current-step`),
};

// Patient Monitoring Service (Doctor's Patient List)
export const patientMonitoringService = {
  getPatientsByDoctor: (doctorId) => api.get(`/patient-monitoring/doctor/${doctorId}/patients`),
  getPatientDetails: (patientId) => api.get(`/patient-monitoring/patient/${patientId}`),
  getAll: (params) => api.get('/patient-monitoring/patients', { params }),
  getByReservation: (reservationId) => api.get(`/patient-monitoring/reservation/${reservationId}`),
};

// Default export (Opsiyonel, geriye dönük uyumluluk için)
export default api;