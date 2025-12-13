import React from 'react';
import { Box, CircularProgress, Typography, LinearProgress } from '@mui/material';

/**
 * Loading Component
 * 
 * Genel amaçlı yükleme göstergesi componenti.
 * 
 * @param {string} message - Gösterilecek mesaj (opsiyonel)
 * @param {string} variant - 'circular' veya 'linear' (varsayılan: 'circular')
 * @param {boolean} fullScreen - Tam ekran modu (varsayılan: false)
 */
export default function Loading({ 
  message = 'Yükleniyor...', 
  variant = 'circular',
  fullScreen = false 
}) {
  const containerStyles = fullScreen
    ? {
        position: 'fixed',
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        backgroundColor: 'rgba(255, 255, 255, 0.9)',
        zIndex: 9999,
      }
    : {
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        p: 4,
        minHeight: '200px',
      };

  return (
    <Box sx={containerStyles}>
      {variant === 'circular' ? (
        <>
          <CircularProgress size={60} />
          {message && (
            <Typography variant="body1" sx={{ mt: 2 }}>
              {message}
            </Typography>
          )}
        </>
      ) : (
        <>
          <Box sx={{ width: '100%', maxWidth: '400px', mb: 2 }}>
            <LinearProgress />
          </Box>
          {message && (
            <Typography variant="body2" color="text.secondary">
              {message}
            </Typography>
          )}
        </>
      )}
    </Box>
  );
}

