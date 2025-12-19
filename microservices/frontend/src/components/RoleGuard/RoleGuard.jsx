import React from 'react';
import { Box, Alert, Typography } from '@mui/material';
import { useAuth } from '../../hooks/useAuth';
import { useTranslation } from '../../i18n';

/**
 * Role-Based Component Guard
 * Yetkisiz butonları sadece gizlemekle kalmaz, sayfa yapısını role göre yeniden düzenler
 */
const RoleGuard = ({ 
  children, 
  allowedRoles = [], 
  fallback = null,
  showAlert = true 
}) => {
  const { user } = useAuth();
  const { t } = useTranslation();

  if (!user) {
    return showAlert ? (
      <Alert severity="warning">
        <Typography>{t('roleGuard.loginRequired')}</Typography>
      </Alert>
    ) : null;
  }

  const userRoles = user.roles || [];
  const hasPermission = allowedRoles.some((role) => userRoles.includes(role));

  if (!hasPermission) {
    return fallback || (showAlert ? (
      <Alert severity="error">
        <Typography>{t('roleGuard.accessDenied')}</Typography>
      </Alert>
    ) : null);
  }

  return <>{children}</>;
};

/**
 * Role-Based Layout Component
 * Role'e göre farklı layout'lar gösterir
 */
export const RoleBasedLayout = ({ 
  adminLayout, 
  doctorLayout, 
  patientLayout,
  defaultLayout 
}) => {
  const { user } = useAuth();

  if (!user) {
    return defaultLayout || null;
  }

  const userRoles = user.roles || [];

  if (userRoles.includes('ADMIN') && adminLayout) {
    return adminLayout;
  }

  if (userRoles.includes('DOCTOR') && doctorLayout) {
    return doctorLayout;
  }

  if (userRoles.includes('PATIENT') && patientLayout) {
    return patientLayout;
  }

  return defaultLayout || null;
};

export default RoleGuard;


