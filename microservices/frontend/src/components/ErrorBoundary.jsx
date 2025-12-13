import React from 'react';
import { ErrorBoundary } from 'react-error-boundary';
import { Box, Button, Typography, Container } from '@mui/material';
import ErrorOutlineIcon from '@mui/icons-material/ErrorOutline';

function ErrorFallback({ error, resetErrorBoundary }) {
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
          Something went wrong
        </Typography>
        <Typography variant="body1" color="text.secondary" sx={{ mb: 3 }}>
          {error?.message || 'An unexpected error occurred'}
        </Typography>
        <Box sx={{ display: 'flex', gap: 2 }}>
          <Button variant="contained" onClick={resetErrorBoundary}>
            Try again
          </Button>
          <Button variant="outlined" onClick={() => window.location.href = '/'}>
            Go to homepage
          </Button>
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

