// src/utils/constants.js

// API Endpoints
export const API_ENDPOINTS = {
  AUTH: {
    LOGIN: '/auth/login',
    REGISTER: '/auth/register',
    LOGOUT: '/auth/logout',
    REFRESH: '/auth/refresh',
    FORGOT_PASSWORD: '/auth/forgot-password',
    RESET_PASSWORD: '/auth/reset-password',
  },
  USERS: {
    BASE: '/users',
    PROFILE: '/users/profile',
  },
  HOSPITALS: {
    BASE: '/hospitals',
    SEARCH: '/hospitals/search',
  },
  DOCTORS: {
    BASE: '/doctors',
    BY_HOSPITAL: '/doctors/hospital',
  },
  RESERVATIONS: {
    BASE: '/reservations',
  },
  PAYMENTS: {
    BASE: '/payments',
  },
};

// Local Storage Keys
export const STORAGE_KEYS = {
  TOKEN: 'token',
  REFRESH_TOKEN: 'refreshToken',
  USER: 'user',
  THEME: 'theme',
  LANGUAGE: 'language',
};

// Pagination
export const PAGINATION = {
  DEFAULT_PAGE_SIZE: 10,
  MAX_PAGE_SIZE: 100,
};

// Date Formats
export const DATE_FORMATS = {
  DISPLAY: 'DD.MM.YYYY',
  API: 'YYYY-MM-DD',
  DATETIME: 'DD.MM.YYYY HH:mm',
};

// Validation Rules
export const VALIDATION = {
  PASSWORD_MIN_LENGTH: 6,
  PASSWORD_MAX_LENGTH: 50,
  EMAIL_REGEX: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
  PHONE_REGEX: /^[0-9]{10,15}$/,
};

// Error Messages
export const ERROR_MESSAGES = {
  NETWORK_ERROR: 'Ağ bağlantısı hatası. Lütfen internet bağlantınızı kontrol edin.',
  UNAUTHORIZED: 'Oturum süreniz dolmuş. Lütfen tekrar giriş yapın.',
  FORBIDDEN: 'Bu işlem için yetkiniz bulunmamaktadır.',
  NOT_FOUND: 'İstenen kaynak bulunamadı.',
  SERVER_ERROR: 'Sunucu hatası. Lütfen daha sonra tekrar deneyin.',
  VALIDATION_ERROR: 'Lütfen tüm alanları doğru şekilde doldurun.',
};

// Success Messages
export const SUCCESS_MESSAGES = {
  SAVED: 'Kayıt başarıyla kaydedildi.',
  UPDATED: 'Kayıt başarıyla güncellendi.',
  DELETED: 'Kayıt başarıyla silindi.',
  LOGGED_IN: 'Başarıyla giriş yapıldı.',
  LOGGED_OUT: 'Başarıyla çıkış yapıldı.',
};

// Theme
export const THEME = {
  LIGHT: 'light',
  DARK: 'dark',
};

// Languages
export const LANGUAGES = {
  TR: 'tr',
  EN: 'en',
};

