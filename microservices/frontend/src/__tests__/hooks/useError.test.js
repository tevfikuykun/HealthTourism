// src/__tests__/hooks/useError.test.js
import { describe, it, expect, vi } from 'vitest';
import { renderHook, act } from '@testing-library/react';
import { useError } from '../../hooks/useError';

describe('useError', () => {
  it('initializes with null error', () => {
    const { result } = renderHook(() => useError());
    expect(result.current.error).toBe(null);
  });

  it('handles error', () => {
    const { result } = renderHook(() => useError());
    const mockError = { message: 'Test error' };
    act(() => {
      result.current.handleError(mockError);
    });
    expect(result.current.error).toBe('Test error');
  });

  it('clears error', () => {
    const { result } = renderHook(() => useError());
    act(() => {
      result.current.handleError({ message: 'Test error' });
      result.current.clearError();
    });
    expect(result.current.error).toBe(null);
  });
});

