import { useState, useEffect } from 'react';

/**
 * Debounce hook - Performans optimizasyonu için
 * Kullanıcı yazmayı bıraktıktan belirli bir süre sonra değeri günceller
 * Backend Rate Limiting'i korumak için elzemdir
 * 
 * @param {any} value - Debounce edilecek değer
 * @param {number} delay - Gecikme süresi (ms) - default: 500ms
 * @returns {any} - Debounced değer
 */
export function useDebounce(value, delay = 500) {
  const [debouncedValue, setDebouncedValue] = useState(value);

  useEffect(() => {
    // Timer oluştur
    const handler = setTimeout(() => {
      setDebouncedValue(value);
    }, delay);

    // Cleanup: Eğer value değişirse timer'ı iptal et
    return () => {
      clearTimeout(handler);
    };
  }, [value, delay]);

  return debouncedValue;
}

