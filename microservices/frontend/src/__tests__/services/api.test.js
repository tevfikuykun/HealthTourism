import { describe, it, expect, vi, beforeEach } from 'vitest';
import { favoriteService, chatService, adminService } from '../../services/api';
import api from '../../services/api';

describe('API Services', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('favoriteService', () => {
    it('should have getAll method', () => {
      expect(favoriteService.getAll).toBeDefined();
      expect(typeof favoriteService.getAll).toBe('function');
    });

    it('should have add method', () => {
      expect(favoriteService.add).toBeDefined();
      expect(typeof favoriteService.add).toBe('function');
    });

    it('should have remove method', () => {
      expect(favoriteService.remove).toBeDefined();
      expect(typeof favoriteService.remove).toBe('function');
    });
  });

  describe('chatService', () => {
    it('should have sendMessage method', () => {
      expect(chatService.sendMessage).toBeDefined();
      expect(typeof chatService.sendMessage).toBe('function');
    });

    it('should have getMessages method', () => {
      expect(chatService.getMessages).toBeDefined();
      expect(typeof chatService.getMessages).toBe('function');
    });
  });

  describe('adminService', () => {
    it('should have login method', () => {
      expect(adminService.login).toBeDefined();
      expect(typeof adminService.login).toBe('function');
    });

    it('should have getDashboard method', () => {
      expect(adminService.getDashboard).toBeDefined();
      expect(typeof adminService.getDashboard).toBe('function');
    });
  });
});
