// src/components/LoadingState/LoadingState.jsx
import React from 'react';
import { Box, CircularProgress, Typography } from '@mui/material';
import { useTranslation } from 'react-i18next';

const LoadingState = ({ message, fullScreen = false }) => {
  const { t } = useTranslation();
  const defaultMessage = message || t('loading', 'Yükleniyor...');
  const content = (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        gap: 2,
        ...(fullScreen && {
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          bgcolor: 'background.paper',
          zIndex: 9999,
        }),
      }}
    >
      <CircularProgress />
      {defaultMessage && (
        <Typography variant="body1" color="text.secondary">
          {defaultMessage}
        </Typography>
      )}
    </Box>
  );

  return content;
};

export default LoadingState;

