import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { Search, X } from 'lucide-react';
import { cn } from '../../lib/utils';
import { useDebounce } from '../../hooks/useDebounce';

/**
 * Premium Search Bar Component
 * Debounced search ile performans optimizasyonu
 * Query parametreleri ile React Query entegrasyonu
 * 
 * @param {string} value - Başlangıç değeri
 * @param {function} onChange - Değer değiştiğinde çağrılacak fonksiyon (debounced)
 * @param {function} onImmediateChange - Anında değişiklik için (UI güncellemesi)
 * @param {string} placeholder - Placeholder metni
 * @param {number} debounceDelay - Debounce gecikme süresi (ms) - default: 500ms
 */
export const SearchBar = ({ 
  value = '', 
  onChange, 
  onImmediateChange,
  placeholder = 'Ara...',
  onClear,
  className,
  debounceDelay = 500,
  ...props 
}) => {
  const [localValue, setLocalValue] = useState(value);
  const debouncedValue = useDebounce(localValue, debounceDelay);

  useEffect(() => {
    setLocalValue(value);
  }, [value]);

  // Debounced değer değiştiğinde onChange'i çağır
  useEffect(() => {
    if (debouncedValue !== value) {
      onChange?.(debouncedValue);
    }
  }, [debouncedValue, onChange, value]);

  const handleChange = (e) => {
    const newValue = e.target.value;
    setLocalValue(newValue);
    // UI'ı anında güncelle (debounce olmadan)
    onImmediateChange?.(newValue);
  };

  const handleClear = () => {
    setLocalValue('');
    onChange?.('');
    onClear?.();
  };

  return (
    <motion.div
      initial={{ opacity: 0, y: -10 }}
      animate={{ opacity: 1, y: 0 }}
      className={cn('relative w-full', className)}
      {...props}
    >
      <div className="relative">
        <Search className="absolute left-3 top-1/2 h-5 w-5 -translate-y-1/2 text-slate-400" />
        <input
          type="text"
          value={localValue}
          onChange={handleChange}
          placeholder={placeholder}
          className={cn(
            'w-full rounded-lg border border-slate-300 bg-white py-3 pl-10 pr-10',
            'text-sm placeholder:text-slate-400',
            'focus:border-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-500/20',
            'transition-all duration-200'
          )}
        />
        {localValue && (
          <motion.button
            initial={{ opacity: 0, scale: 0.8 }}
            animate={{ opacity: 1, scale: 1 }}
            exit={{ opacity: 0, scale: 0.8 }}
            onClick={handleClear}
            className="absolute right-3 top-1/2 -translate-y-1/2 rounded-full p-1 text-slate-400 hover:bg-slate-100 hover:text-slate-600"
          >
            <X className="h-4 w-4" />
          </motion.button>
        )}
      </div>
    </motion.div>
  );
};

