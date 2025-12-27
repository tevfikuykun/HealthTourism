import React from 'react';
import { motion } from 'framer-motion';
import { MapPin } from 'lucide-react';
import { cn } from '../../lib/utils';

/**
 * Distance Slider Component
 * Havalimanı mesafesi filtresi için şık bir kaydırıcı
 * 
 * @param {number} value - Mevcut mesafe değeri (km)
 * @param {function} onChange - Değer değiştiğinde çağrılacak fonksiyon
 * @param {number} min - Minimum değer (default: 0)
 * @param {number} max - Maximum değer (default: 50)
 * @param {number} step - Adım değeri (default: 5)
 */
export const DistanceSlider = ({
  value,
  onChange,
  min = 0,
  max = 50,
  step = 5,
  className,
}) => {
  const handleChange = (e) => {
    const newValue = Number(e.target.value);
    onChange?.(newValue);
  };

  const percentage = ((value - min) / (max - min)) * 100;

  return (
    <div className={cn('space-y-3', className)}>
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2">
          <MapPin className="h-4 w-4 text-primary-600" />
          <span className="text-sm font-medium text-slate-700">
            Havalimanı Mesafesi
          </span>
        </div>
        <span className="text-sm font-semibold text-primary-600">
          {value === max ? 'Tümü' : `${value} km`}
        </span>
      </div>

      <div className="relative">
        {/* Track */}
        <div className="h-2 w-full rounded-full bg-slate-200">
          {/* Filled Track */}
          <motion.div
            className="h-2 rounded-full bg-gradient-to-r from-primary-500 to-primary-600"
            initial={{ width: 0 }}
            animate={{ width: `${percentage}%` }}
            transition={{ duration: 0.2 }}
          />
        </div>

        {/* Slider Input */}
        <input
          type="range"
          min={min}
          max={max}
          step={step}
          value={value}
          onChange={handleChange}
          className="absolute top-0 h-2 w-full cursor-pointer appearance-none bg-transparent"
          style={{
            background: 'transparent',
          }}
        />
      </div>

      {/* Labels */}
      <div className="flex justify-between text-xs text-slate-500">
        <span>{min} km</span>
        <span>{max} km</span>
      </div>
    </div>
  );
};

