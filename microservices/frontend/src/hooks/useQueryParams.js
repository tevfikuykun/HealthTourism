import { useSearchParams } from 'react-router-dom';
import { useMemo } from 'react';

/**
 * URL query parametrelerini yönetmek için custom hook
 * React Query ile entegre çalışır
 */
export const useQueryParams = (defaultParams = {}) => {
  const [searchParams, setSearchParams] = useSearchParams();

  const params = useMemo(() => {
    const result = { ...defaultParams };
    searchParams.forEach((value, key) => {
      // Boolean değerleri parse et
      if (value === 'true') {
        result[key] = true;
      } else if (value === 'false') {
        result[key] = false;
      } else if (!isNaN(value) && value !== '') {
        // Sayısal değerleri parse et
        result[key] = Number(value);
      } else if (value !== '') {
        result[key] = value;
      }
    });
    return result;
  }, [searchParams, defaultParams]);

  const updateParams = (updates) => {
    const newParams = new URLSearchParams(searchParams);
    
    Object.entries(updates).forEach(([key, value]) => {
      if (value === null || value === undefined || value === '') {
        newParams.delete(key);
      } else {
        newParams.set(key, String(value));
      }
    });

    setSearchParams(newParams, { replace: true });
  };

  const setParam = (key, value) => {
    updateParams({ [key]: value });
  };

  const removeParam = (key) => {
    const newParams = new URLSearchParams(searchParams);
    newParams.delete(key);
    setSearchParams(newParams, { replace: true });
  };

  const clearParams = () => {
    setSearchParams({}, { replace: true });
  };

  return {
    params,
    updateParams,
    setParam,
    removeParam,
    clearParams,
  };
};

