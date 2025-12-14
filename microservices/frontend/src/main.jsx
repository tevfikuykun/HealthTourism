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
import i18n from './i18n'; // Initialize i18n
import './index.css';
import './styles/print.css';

// Initialize analytics
initAnalytics();

// Request notification permission
requestNotificationPermission();

// Register service worker for PWA (only if available)
if ('serviceWorker' in navigator) {
  window.addEventListener('load', () => {
    // Service worker will be registered by vite-plugin-pwa when installed
    // For now, skip registration if sw.js doesn't exist
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
