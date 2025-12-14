// src/__tests__/services/api.test.js
import { describe, it, expect, vi, beforeEach } from 'vitest';
import axios from 'axios';
import { authService, hospitalService } from '../../services/api';

vi.mock('axios');

describe('API Services', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('authService.login makes correct API call', async () => {
    const mockData = { email: 'test@test.com', password: 'password123' };
    const mockResponse = { data: { token: 'test-token' } };
    
    axios.create = vi.fn(() => ({
      post: vi.fn().mockResolvedValue(mockResponse),
    }));

    await authService.login(mockData);
    expect(axios.create).toHaveBeenCalled();
  });

  it('hospitalService.getAll makes correct API call', async () => {
    const mockResponse = { data: [] };
    
    axios.create = vi.fn(() => ({
      get: vi.fn().mockResolvedValue(mockResponse),
    }));

    await hospitalService.getAll();
    expect(axios.create).toHaveBeenCalled();
  });
});

