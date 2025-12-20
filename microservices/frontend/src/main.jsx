// main.jsx
import React from 'react';
import ReactDOM from 'react-dom/client';
import { ThemeProvider } from '@mui/material/styles';
import { HelmetProvider } from 'react-helmet-async';
import { I18nextProvider } from 'react-i18next';
import theme from './theme';
import App from './App';
import AppErrorBoundary from './components/ErrorBoundary';
import ToastContainer from './components/ToastContainer';
import { initAnalytics } from './utils/analytics';
import { requestNotificationPermission } from './utils/pushNotifications';
import { initSentry } from './utils/sentry';
import { initCurrency } from './utils/currency';
import i18n from './i18n'; // Initialize i18n
import './index.css';
import './styles/print.css';

// Initialize Sentry for error tracking (must be first)
initSentry();

// Initialize analytics
initAnalytics();

// Initialize currency service
initCurrency();

// Request notification permission
requestNotificationPermission();

// Register service worker for PWA (only in production)
if ('serviceWorker' in navigator) {
  window.addEventListener('load', () => {
    // Always unregister service workers in development mode
    if (import.meta.env.DEV) {
      navigator.serviceWorker.getRegistrations().then((registrations) => {
        for (const registration of registrations) {
          registration.unregister().then(() => {
            console.log('Service worker unregistered for dev mode');
          });
        }
      });
      return;
    }

    // Service worker will be registered by vite-plugin-pwa in production
    if (import.meta.env.PROD) {
      fetch('/sw.js', { method: 'HEAD' })
        .then(() => {
          navigator.serviceWorker.register('/sw.js')
            .then((registration) => {
              console.log('SW registered: ', registration);
            })
            .catch((registrationError) => {
              // Silently fail if service worker not available
            });
        })
        .catch(() => {
          // Service worker not available, skip registration
        });
    }
  });
}

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <I18nextProvider i18n={i18n}>
      <HelmetProvider>
        <ThemeProvider theme={theme}>
          <AppErrorBoundary>
            <App />
            <ToastContainer />
          </AppErrorBoundary>
        </ThemeProvider>
      </HelmetProvider>
    </I18nextProvider>
  </React.StrictMode>,
);
