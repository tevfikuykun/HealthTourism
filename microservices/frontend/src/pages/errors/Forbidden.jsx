import React from 'react';
import { Container, Box, Typography, Button } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import BlockIcon from '@mui/icons-material/Block';
import HomeIcon from '@mui/icons-material/Home';
import LockIcon from '@mui/icons-material/Lock';

export default function Forbidden() {
  const navigate = useNavigate();

  return (
    <Container maxWidth="md">
      <Box
        sx={{
          minHeight: '80vh',
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
          textAlign: 'center',
          py: 4,
        }}
      >
        <BlockIcon sx={{ fontSize: 150, color: 'error.main', mb: 2 }} />
        <Typography variant="h1" component="h1" gutterBottom>
          403
        </Typography>
        <Typography variant="h4" gutterBottom>
          Erişim Reddedildi
        </Typography>
        <Typography variant="body1" color="text.secondary" sx={{ mb: 4, maxWidth: 500 }}>
          Bu sayfaya erişim yetkiniz bulunmamaktadır. Lütfen yöneticinizle iletişime geçin.
        </Typography>
        <Box sx={{ display: 'flex', gap: 2 }}>
          <Button
            variant="contained"
            size="large"
            startIcon={<HomeIcon />}
            onClick={() => navigate('/')}
          >
            Ana Sayfaya Dön
          </Button>
          <Button
            variant="outlined"
            size="large"
            startIcon={<LockIcon />}
            onClick={() => navigate('/login')}
          >
            Giriş Yap
          </Button>
        </Box>
      </Box>
    </Container>
  );
}

