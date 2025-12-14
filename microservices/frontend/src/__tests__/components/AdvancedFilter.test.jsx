// src/__tests__/components/AdvancedFilter.test.jsx
import { describe, it, expect } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import AdvancedFilter from '../../components/AdvancedFilter/AdvancedFilter';

describe('AdvancedFilter', () => {
  it('renders filter component', () => {
    render(<AdvancedFilter />);
    expect(screen.getByText('Gelişmiş Filtreleme')).toBeDefined();
  });

  it('allows price range selection', () => {
    render(<AdvancedFilter />);
    // Test slider interaction
  });
});

