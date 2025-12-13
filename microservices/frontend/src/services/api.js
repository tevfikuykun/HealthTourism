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
  timeout: 30000, // 30 seconds
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
    
    // Handle 401 - Unauthorized (logout user)
    if (appError.statusCode === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    
    // Show error toast for non-401 errors
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
  search: (query) => api.get('/accommodations/search', { params: { q: query } }),
};

export const flightService = {
  search: (params) => api.post('/flights/search', params),
  getById: (id) => api.get(`/flights/${id}`),
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

export default api;
