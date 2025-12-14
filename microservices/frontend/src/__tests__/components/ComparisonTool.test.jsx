// src/__tests__/components/ComparisonTool.test.jsx
import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import ComparisonTool from '../../components/Comparison/ComparisonTool';

describe('ComparisonTool', () => {
  const mockItems = [
    { id: 1, name: 'Hospital 1', city: 'Istanbul', rating: 4.5 },
    { id: 2, name: 'Hospital 2', city: 'Ankara', rating: 4.8 },
  ];

  it('renders comparison tool', () => {
    render(<ComparisonTool type="hospitals" items={mockItems} />);
    expect(screen.getByText('Karşılaştırma')).toBeDefined();
  });

  it('allows adding items to compare', () => {
    render(<ComparisonTool type="hospitals" items={mockItems} />);
    // Test implementation
  });
});

