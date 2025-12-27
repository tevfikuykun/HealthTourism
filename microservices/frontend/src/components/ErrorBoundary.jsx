import React from 'react';
import { ErrorBoundary } from 'react-error-boundary';
import { Box, Button, Typography, Container } from '@mui/material';
import ErrorOutlineIcon from '@mui/icons-material/ErrorOutline';
import { isNetworkError } from '../utils/networkErrorHandler';

function ErrorFallback({ error, resetErrorBoundary }) {
  // Network error kontrolü
  const isNetwork = isNetworkError(error);
  
  // i18n'den tamamen bağımsız - hardcoded strings kullan
  const messages = {
    somethingWentWrong: isNetwork ? 'Bağlantı Sorunu' : 'Bir şeyler yanlış gitti',
    unexpectedError: isNetwork 
      ? 'Ağ bağlantısı değişti veya kesildi. Lütfen internet bağlantınızı kontrol edin ve sayfayı yenileyin.'
      : 'Beklenmeyen bir hata oluştu',
    tryAgain: 'Tekrar Dene',
    goToHomepage: 'Ana Sayfaya Dön',
    refreshPage: 'Sayfayı Yenile'
  };
  
  return (
    <Container maxWidth="md">
      <Box
        sx={{
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
          minHeight: '60vh',
          textAlign: 'center',
          p: 4,
        }}
      >
        <ErrorOutlineIcon sx={{ fontSize: 80, color: 'error.main', mb: 2 }} />
        <Typography variant="h4" gutterBottom>
          {messages.somethingWentWrong}
        </Typography>
        <Typography variant="body1" color="text.secondary" sx={{ mb: 3 }}>
          {error?.message || messages.unexpectedError}
        </Typography>
        <Box sx={{ display: 'flex', gap: 2 }}>
          {isNetwork ? (
            <>
              <Button 
                variant="contained" 
                onClick={() => window.location.reload()}
              >
                {messages.refreshPage}
              </Button>
              <Button variant="outlined" onClick={() => window.location.href = '/'}>
                {messages.goToHomepage}
              </Button>
            </>
          ) : (
            <>
              <Button variant="contained" onClick={resetErrorBoundary}>
                {messages.tryAgain}
              </Button>
              <Button variant="outlined" onClick={() => window.location.href = '/'}>
                {messages.goToHomepage}
              </Button>
            </>
          )}
        </Box>
      </Box>
    </Container>
  );
}

export default function AppErrorBoundary({ children }) {
  return (
    <ErrorBoundary
      FallbackComponent={ErrorFallback}
      onError={(error, errorInfo) => {
        // Log error to monitoring service
        console.error('Error caught by boundary:', error, errorInfo);
        // You can send to error tracking service here (e.g., Sentry)
      }}
      onReset={() => {
        // Reset app state if needed
        window.location.reload();
      }}
    >
      {children}
    </ErrorBoundary>
  );
}

