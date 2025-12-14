// src/components/OfflineIndicator/OfflineIndicator.jsx
import React, { useState, useEffect } from 'react';
import { Alert, Snackbar } from '@mui/material';
import CloudOffIcon from '@mui/icons-material/CloudOff';
import CloudQueueIcon from '@mui/icons-material/CloudQueue';

const OfflineIndicator = () => {
  const [isOnline, setIsOnline] = useState(navigator.onLine);
  const [showAlert, setShowAlert] = useState(false);

  useEffect(() => {
    const handleOnline = () => {
      setIsOnline(true);
      setShowAlert(true);
      setTimeout(() => setShowAlert(false), 3000);
    };

    const handleOffline = () => {
      setIsOnline(false);
      setShowAlert(true);
    };

    window.addEventListener('online', handleOnline);
    window.addEventListener('offline', handleOffline);

    return () => {
      window.removeEventListener('online', handleOnline);
      window.removeEventListener('offline', handleOffline);
    };
  }, []);

  return (
    <Snackbar
      open={showAlert}
      anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
    >
      <Alert
        severity={isOnline ? 'success' : 'warning'}
        icon={isOnline ? <CloudQueueIcon /> : <CloudOffIcon />}
        onClose={() => setShowAlert(false)}
      >
        {isOnline ? 'İnternet bağlantısı yeniden kuruldu' : 'İnternet bağlantısı yok - Offline mod aktif'}
      </Alert>
    </Snackbar>
  );
};

export default OfflineIndicator;

