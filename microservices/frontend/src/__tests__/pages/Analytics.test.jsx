// src/__tests__/pages/Analytics.test.jsx
import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import Analytics from '../../pages/Analytics';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: { retry: false },
  },
});

describe('Analytics Page', () => {
  it('renders analytics page', () => {
    render(
      <QueryClientProvider client={queryClient}>
        <Analytics />
      </QueryClientProvider>
    );
    expect(screen.getByText('Analitik ve Raporlama')).toBeDefined();
  });

  it('displays statistics cards', () => {
    render(
      <QueryClientProvider client={queryClient}>
        <Analytics />
      </QueryClientProvider>
    );
    expect(screen.getByText('Toplam Rezervasyon')).toBeDefined();
  });
});

