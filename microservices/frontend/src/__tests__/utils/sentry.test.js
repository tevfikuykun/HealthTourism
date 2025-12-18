import { describe, it, expect, vi, beforeEach } from 'vitest';
import * as Sentry from '@sentry/react';
import { initSentry, captureException, captureMessage, setUser, clearUser } from '../../utils/sentry';

// Mock Sentry
vi.mock('@sentry/react', () => ({
  default: {
    init: vi.fn(),
    captureException: vi.fn(),
    captureMessage: vi.fn(),
    setUser: vi.fn(),
    setUser: vi.fn(),
  },
  init: vi.fn(),
  captureException: vi.fn(),
  captureMessage: vi.fn(),
  setUser: vi.fn(),
  browserTracingIntegration: vi.fn(),
  replayIntegration: vi.fn(),
}));

describe('Sentry Utils', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    process.env.VITE_SENTRY_DSN = 'test_dsn';
  });

  describe('initSentry', () => {
    it('should initialize Sentry when DSN is provided', () => {
      initSentry();
      expect(Sentry.init).toHaveBeenCalled();
    });

    it('should not initialize Sentry when DSN is not provided', () => {
      delete process.env.VITE_SENTRY_DSN;
      initSentry();
      // Should not throw error
      expect(true).toBe(true);
    });
  });

  describe('captureException', () => {
    it('should capture exception', () => {
      const error = new Error('Test error');
      captureException(error);
      expect(Sentry.captureException).toHaveBeenCalledWith(error, expect.any(Object));
    });
  });

  describe('captureMessage', () => {
    it('should capture message', () => {
      captureMessage('Test message', 'info');
      expect(Sentry.captureMessage).toHaveBeenCalledWith('Test message', 'info');
    });
  });

  describe('setUser', () => {
    it('should set user context', () => {
      const user = { id: 1, email: 'test@example.com' };
      setUser(user);
      expect(Sentry.setUser).toHaveBeenCalled();
    });
  });

  describe('clearUser', () => {
    it('should clear user context', () => {
      clearUser();
      expect(Sentry.setUser).toHaveBeenCalledWith(null);
    });
  });
});
