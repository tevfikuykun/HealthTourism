import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { Box, CircularProgress, Typography } from '@mui/material';

/**
 * ProtectedRoute Component
 * 
 * Kullanıcının kimlik doğrulamasını kontrol eden ve 
 * yetkisi olmayan kullanıcıları login sayfasına yönlendiren component.
 * 
 * @param {React.ReactNode} children - Korunacak içerik
 * @param {string[]} allowedRoles - İzin verilen roller (opsiyonel)
 */
export default function ProtectedRoute({ children, allowedRoles = [] }) {
  const { user, isLoading, isAuthenticated } = useAuth();
  const location = useLocation();

  // Yükleniyor durumu
  if (isLoading) {
    return (
      <Box
        sx={{
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
          minHeight: '60vh',
        }}
      >
        <CircularProgress size={60} />
        <Typography variant="body1" sx={{ mt: 2 }}>
          Yükleniyor...
        </Typography>
      </Box>
    );
  }

  // Kullanıcı giriş yapmamışsa login sayfasına yönlendir
  if (!isAuthenticated) {
    return (
      <Navigate
        to="/login"
        state={{ from: location }}
        replace
      />
    );
  }

  // Rol kontrolü varsa
  if (allowedRoles.length > 0 && user) {
    const userRoles = user.roles || [];
    const hasRole = allowedRoles.some(role => userRoles.includes(role));

    if (!hasRole) {
      return (
        <Box
          sx={{
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            justifyContent: 'center',
            minHeight: '60vh',
            p: 4,
          }}
        >
          <Typography variant="h5" color="error" gutterBottom>
            Yetki Hatası
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Bu sayfaya erişim yetkiniz bulunmamaktadır.
          </Typography>
        </Box>
      );
    }
  }

  return children;
}

