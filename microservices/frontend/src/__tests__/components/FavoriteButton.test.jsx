import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import FavoriteButton from '../../components/FavoriteButton';
import { useAuth } from '../../hooks/useAuth';

vi.mock('../../hooks/useAuth');
vi.mock('../../services/api', () => ({
  favoriteService: {
    add: vi.fn(),
    remove: vi.fn(),
    isFavorite: vi.fn(),
  },
}));

describe('FavoriteButton', () => {
  it('renders favorite button', () => {
    useAuth.mockReturnValue({ isAuthenticated: true, user: { id: 1 } });
    
    render(<FavoriteButton itemId={1} itemType="hospital" />);
    
    expect(screen.getByRole('button')).toBeInTheDocument();
  });

  it('shows login message when not authenticated', () => {
    useAuth.mockReturnValue({ isAuthenticated: false });
    
    render(<FavoriteButton itemId={1} itemType="hospital" />);
    
    const button = screen.getByRole('button');
    fireEvent.click(button);
    
    // Should show toast error (mocked)
  });
});
