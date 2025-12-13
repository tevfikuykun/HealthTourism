import React from 'react';
import { Container, Box, Paper, Typography, Button, Alert } from '@mui/material';
import { useNavigate, useSearchParams } from 'react-router-dom';
import ErrorIcon from '@mui/icons-material/Error';
import PaymentIcon from '@mui/icons-material/Payment';
import HomeIcon from '@mui/icons-material/Home';

export default function PaymentFailed() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const error = searchParams.get('error');

  return (
    <Container maxWidth="sm">
      <Box
        sx={{
          minHeight: '80vh',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          py: 4,
        }}
      >
        <Paper elevation={3} sx={{ p: 4, width: '100%', textAlign: 'center' }}>
          <ErrorIcon sx={{ fontSize: 100, color: 'error.main', mb: 2 }} />
          <Typography variant="h4" gutterBottom>
            Ödeme Başarısız
          </Typography>
          <Typography variant="body1" color="text.secondary" sx={{ mb: 3 }}>
            Ödemeniz işlenirken bir hata oluştu. Lütfen tekrar deneyin.
          </Typography>
          {error && (
            <Alert severity="error" sx={{ mb: 3 }}>
              {error}
            </Alert>
          )}
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
            <Button
              variant="contained"
              size="large"
              startIcon={<PaymentIcon />}
              onClick={() => navigate('/payments')}
            >
              Tekrar Dene
            </Button>
            <Button variant="outlined" size="large" startIcon={<HomeIcon />} onClick={() => navigate('/')}>
              Ana Sayfaya Dön
            </Button>
          </Box>
        </Paper>
      </Box>
    </Container>
  );
}

