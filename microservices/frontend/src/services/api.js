import axios from 'axios';
import { handleApiError } from '../utils/errorHandler';
import { toast } from 'react-toastify';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

// Create axios instance with default config
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 30000,
});

// Request interceptor - Add auth token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor - Handle errors globally
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const appError = handleApiError(error);
    
    if (appError.statusCode === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    
    if (appError.statusCode !== 401) {
      toast.error(appError.message);
    }
    
    return Promise.reject(appError);
  }
);

// API Services
export const authService = {
  register: (data) => api.post('/auth/register', data),
  login: (data) => api.post('/auth/login', data),
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
  getBySlug: (slug) => api.get(`/doctors/slug/${slug}`),
  getByHospital: (hospitalId) => api.get(`/doctors/hospital/${hospitalId}`),
  search: (query) => api.get('/doctors/search', { params: { q: query } }),
};

export const accommodationService = {
  getAll: (params) => api.get('/accommodations', { params }),
  getById: (id) => api.get(`/accommodations/${id}`),
  getBySlug: (slug) => api.get(`/accommodations/slug/${slug}`),
  search: (query) => api.get('/accommodations/search', { params: { q: query } }),
};

export const flightService = {
  search: (params) => api.post('/flights/search', params),
  getById: (id) => api.get(`/flights/${id}`),
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

export const notificationService = {
  send: (notification) => api.post('/notifications', notification),
  getByUser: (userId) => api.get(`/notifications/user/${userId}`),
  markAsRead: (id) => api.put(`/notifications/${id}/read`),
  markAllAsRead: (userId) => api.put(`/notifications/user/${userId}/read-all`),
};

export const contactService = {
  send: (contact) => api.post('/contact', contact),
  getAll: () => api.get('/contact'),
};

export const fileStorageService = {
  upload: (formData, config) => api.post('/files/upload', formData, { ...config, headers: { 'Content-Type': 'multipart/form-data' } }),
  getById: (id) => api.get(`/files/${id}`),
  delete: (id) => api.delete(`/files/${id}`),
};

// New Feature Services
export const comparisonService = {
  compare: (items, type) => api.post('/comparison/compare', { items, type }),
  getComparison: (type) => api.get(`/comparison/${type}`),
};

export const analyticsService = {
  getRevenue: (params) => api.get('/analytics/revenue', { params }),
  getUsers: (params) => api.get('/analytics/users', { params }),
  getReservations: (params) => api.get('/analytics/reservations', { params }),
  getServices: (params) => api.get('/analytics/services', { params }),
};

export const healthRecordsService = {
  getAll: () => api.get('/health-records'),
  getById: (id) => api.get(`/health-records/${id}`),
  create: (record) => api.post('/health-records', record),
  update: (id, record) => api.put(`/health-records/${id}`, record),
  delete: (id) => api.delete(`/health-records/${id}`),
};

export const reviewService = {
  create: (review) => api.post('/reviews', review),
  update: (id, review) => api.put(`/reviews/${id}`, review),
  getByEntity: (entityType, entityId) => api.get(`/reviews/entity/${entityType}/${entityId}`),
  addDoctorResponse: (id, response) => api.post(`/reviews/${id}/response`, { response }),
  markHelpful: (id, helpful) => api.post(`/reviews/${id}/helpful`, { helpful }),
  verify: (id) => api.post(`/reviews/${id}/verify`),
};

export const postTreatmentService = {
  createCarePackage: (packageData) => api.post('/post-treatment', packageData),
  getByUser: (userId) => api.get(`/post-treatment/user/${userId}`),
  updateTaskStatus: (id, taskIndex, isCompleted) => api.put(`/post-treatment/${id}/task/${taskIndex}`, { isCompleted }),
  scheduleFollowUp: (id, date) => api.post(`/post-treatment/${id}/follow-up`, { date }),
};

export const influencerService = {
  register: (influencer) => api.post('/influencers/register', influencer),
  approve: (id) => api.post(`/influencers/${id}/approve`),
  createCampaign: (campaign) => api.post('/influencers/campaigns', campaign),
  getCampaigns: (influencerId) => api.get(`/influencers/campaigns/${influencerId}`),
  updatePerformance: (id, clicks, conversions) => api.put(`/influencers/campaigns/${id}/performance`, { clicks, conversions }),
  calculateCommission: (id) => api.get(`/influencers/campaigns/${id}/commission`),
};

export const affiliateService = {
  register: (userId) => api.post('/affiliate/register', { userId }),
  getByCode: (code) => api.get(`/affiliate/code/${code}`),
  trackClick: (referralCode, userId) => api.post('/affiliate/track/click', { referralCode, userId }),
  trackConversion: (referralId, reservationId, amount) => api.post('/affiliate/track/conversion', { referralId, reservationId, amount }),
  getReferrals: (affiliateId) => api.get(`/affiliate/referrals/${affiliateId}`),
};

export const gamificationService = {
  addPoints: (userId, points, reason) => api.post('/gamification/points/add', { userId, points, reason }),
  awardBadge: (userId, badgeId) => api.post('/gamification/badges/award', { userId, badgeId }),
  getAllBadges: () => api.get('/gamification/badges'),
  getUserBadges: (userId) => api.get(`/gamification/badges/user/${userId}`),
  getLeaderboard: (limit) => api.get('/gamification/leaderboard', { params: { limit } }),
  createChallenge: (challenge) => api.post('/gamification/challenges', challenge),
  getActiveChallenges: () => api.get('/gamification/challenges/active'),
};

export const searchService = {
  search: (query, params) => api.get('/search', { params: { q: query, ...params } }),
  getHistory: () => api.get('/search/history'),
  saveSearch: (search) => api.post('/search/save', search),
};

export const currencyService = {
  getRates: () => api.get('/currency/rates'),
  convert: (from, to, amount) => api.post('/currency/convert', { from, to, amount }),
};

export const taxService = {
  calculate: (amount, taxRate, includeTax) => api.post('/tax/calculate', { amount, taxRate, includeTax }),
};

export default api;
