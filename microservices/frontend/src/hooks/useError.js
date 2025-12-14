// src/hooks/useError.js
import { useState, useCallback } from 'react';
import { toast } from 'react-toastify';

export const useError = () => {
  const [error, setError] = useState(null);

  const handleError = useCallback((err, customMessage) => {
    const message = customMessage || err?.message || 'Bir hata oluştu';
    setError(message);
    toast.error(message);
  }, []);

  const clearError = useCallback(() => {
    setError(null);
  }, []);

  return {
    error,
    handleError,
    clearError,
  };
};

