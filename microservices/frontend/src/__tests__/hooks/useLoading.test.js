// src/__tests__/hooks/useLoading.test.js
import { describe, it, expect } from 'vitest';
import { renderHook, act } from '@testing-library/react';
import { useLoading } from '../../hooks/useLoading';

describe('useLoading', () => {
  it('initializes with false', () => {
    const { result } = renderHook(() => useLoading());
    expect(result.current.isLoading).toBe(false);
  });

  it('starts loading', () => {
    const { result } = renderHook(() => useLoading());
    act(() => {
      result.current.startLoading();
    });
    expect(result.current.isLoading).toBe(true);
  });

  it('stops loading', () => {
    const { result } = renderHook(() => useLoading(true));
    act(() => {
      result.current.stopLoading();
    });
    expect(result.current.isLoading).toBe(false);
  });
});

