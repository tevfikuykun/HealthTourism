// src/__tests__/pages/HealthRecords.test.jsx
import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import HealthRecords from '../../pages/HealthRecords';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: { retry: false },
  },
});

describe('HealthRecords Page', () => {
  it('renders health records page', () => {
    render(
      <QueryClientProvider client={queryClient}>
        <HealthRecords />
      </QueryClientProvider>
    );
    expect(screen.getByText('Sağlık Kayıtlarım')).toBeDefined();
  });
});

