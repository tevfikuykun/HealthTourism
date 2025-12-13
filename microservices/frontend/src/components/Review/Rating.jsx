import React from 'react';
import { Rating as MuiRating, Box, Typography } from '@mui/material';

/**
 * Rating Component
 * 
 * Yıldız bazlı değerlendirme componenti.
 * 
 * @param {number} value - Mevcut puan (0-5 arası)
 * @param {function} onChange - Puan değiştiğinde çağrılacak fonksiyon
 * @param {boolean} readOnly - Sadece okunabilir mi (default: false)
 * @param {number} size - Yıldız boyutu (default: 'medium')
 * @param {boolean} showValue - Puan değeri gösterilsin mi (default: false)
 */
export default function Rating({ value = 0, onChange, readOnly = false, size = 'medium', showValue = false }) {
  return (
    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
      <MuiRating
        value={value}
        onChange={(event, newValue) => {
          if (onChange) {
            onChange(newValue);
          }
        }}
        readOnly={readOnly}
        size={size}
        precision={0.5}
      />
      {showValue && value > 0 && (
        <Typography variant="body2" color="text.secondary">
          ({value.toFixed(1)})
        </Typography>
      )}
    </Box>
  );
}

