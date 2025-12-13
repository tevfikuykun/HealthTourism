import React, { useEffect } from 'react';
import { Container, Box, Paper, Typography, Button } from '@mui/material';
import { useNavigate, useSearchParams } from 'react-router-dom';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import ReceiptIcon from '@mui/icons-material/Receipt';
import HomeIcon from '@mui/icons-material/Home';

export default function PaymentSuccess() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const paymentId = searchParams.get('paymentId');
  const amount = searchParams.get('amount');

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
          <CheckCircleIcon sx={{ fontSize: 100, color: 'success.main', mb: 2 }} />
          <Typography variant="h4" gutterBottom>
            Ödeme Başarılı!
          </Typography>
          <Typography variant="body1" color="text.secondary" sx={{ mb: 3 }}>
            Ödemeniz başarıyla tamamlandı. {paymentId && `Ödeme ID: ${paymentId}`}
          </Typography>
          {amount && (
            <Typography variant="h6" color="primary" sx={{ mb: 3 }}>
              Ödenen Tutar: {parseFloat(amount).toLocaleString('tr-TR')} ₺
            </Typography>
          )}
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
            <Button
              variant="contained"
              size="large"
              startIcon={<ReceiptIcon />}
              onClick={() => navigate('/dashboard')}
            >
              Rezervasyonlarımı Gör
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

