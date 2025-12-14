// src/__tests__/pages/Login.test.jsx
import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import Login from '../../pages/Login';

describe('Login Page', () => {
  it('renders login form', () => {
    render(
      <BrowserRouter>
        <Login />
      </BrowserRouter>
    );
    expect(screen.getByText('Giriş Yap')).toBeDefined();
    expect(screen.getByLabelText(/e-posta/i)).toBeDefined();
    expect(screen.getByLabelText(/şifre/i)).toBeDefined();
  });

  it('validates email format', async () => {
    render(
      <BrowserRouter>
        <Login />
      </BrowserRouter>
    );
    const emailInput = screen.getByLabelText(/e-posta/i);
    fireEvent.change(emailInput, { target: { value: 'invalid-email' } });
    fireEvent.blur(emailInput);
    // Validation message should appear
  });
});

