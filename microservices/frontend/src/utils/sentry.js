import * as Sentry from "@sentry/react";

/**
 * Initialize Sentry for error tracking
 */
export const initSentry = () => {
  const dsn = import.meta.env.VITE_SENTRY_DSN;
  
  if (!dsn) {
    console.warn("Sentry DSN not configured. Error tracking disabled.");
    return;
  }

  Sentry.init({
    dsn: dsn,
    environment: import.meta.env.MODE || "development",
    integrations: [
      Sentry.browserTracingIntegration(),
      Sentry.replayIntegration({
        maskAllText: true,
        blockAllMedia: true,
      }),
    ],
    // Performance Monitoring
    tracesSampleRate: import.meta.env.MODE === "production" ? 0.1 : 1.0,
    // Session Replay
    replaysSessionSampleRate: 0.1,
    replaysOnErrorSampleRate: 1.0,
  });
};

/**
 * Capture exception
 */
export const captureException = (error, context = {}) => {
  Sentry.captureException(error, {
    extra: context,
  });
};

/**
 * Capture message
 */
export const captureMessage = (message, level = "info") => {
  Sentry.captureMessage(message, level);
};

/**
 * Set user context
 */
export const setUser = (user) => {
  Sentry.setUser({
    id: user?.id,
    email: user?.email,
    username: user?.username || user?.email,
  });
};

/**
 * Clear user context
 */
export const clearUser = () => {
  Sentry.setUser(null);
};

export default Sentry;
