// src/components/ErrorState/ErrorState.jsx
import React from 'react';
import { Box, Typography, Button, Paper } from '@mui/material';
import ErrorOutlineIcon from '@mui/icons-material/ErrorOutline';
import RefreshIcon from '@mui/icons-material/Refresh';
import { useTranslation } from 'react-i18next';

const ErrorState = ({ message, onRetry, fullScreen = false }) => {
  const { t } = useTranslation();
  const defaultMessage = message || t('errorOccurred', 'Bir hata oluştu');
  const content = (
    <Paper
      sx={{
        p: 4,
        textAlign: 'center',
        ...(fullScreen && {
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          zIndex: 9999,
        }),
      }}
    >
      <ErrorOutlineIcon sx={{ fontSize: 64, color: 'error.main', mb: 2 }} />
      <Typography variant="h6" gutterBottom>
        {t('error', 'Hata')}
      </Typography>
      <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
        {defaultMessage}
      </Typography>
      {onRetry && (
        <Button
          variant="contained"
          startIcon={<RefreshIcon />}
          onClick={onRetry}
        >
          {t('tryAgain', 'Tekrar Dene')}
        </Button>
      )}
    </Paper>
  );

  return content;
};

export default ErrorState;

