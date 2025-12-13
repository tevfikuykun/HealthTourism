// Environment configuration
export const config = {
  apiBaseUrl: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  appName: import.meta.env.VITE_APP_NAME || 'Health Tourism',
  appVersion: import.meta.env.VITE_APP_VERSION || '1.0.0',
  environment: import.meta.env.MODE || 'development',
  isDevelopment: import.meta.env.DEV,
  isProduction: import.meta.env.PROD,
  enableAnalytics: import.meta.env.VITE_ENABLE_ANALYTICS === 'true',
  analyticsId: import.meta.env.VITE_ANALYTICS_ID || '',
  enablePWA: import.meta.env.VITE_ENABLE_PWA === 'true',
};

export default config;

