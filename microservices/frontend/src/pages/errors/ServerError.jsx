import React from 'react';
import { Container, Box, Typography, Button } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import WarningIcon from '@mui/icons-material/Warning';
import HomeIcon from '@mui/icons-material/Home';
import RefreshIcon from '@mui/icons-material/Refresh';

export default function ServerError() {
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
        <WarningIcon sx={{ fontSize: 150, color: 'warning.main', mb: 2 }} />
        <Typography variant="h1" component="h1" gutterBottom>
          500
        </Typography>
        <Typography variant="h4" gutterBottom>
          Sunucu Hatası
        </Typography>
        <Typography variant="body1" color="text.secondary" sx={{ mb: 4, maxWidth: 500 }}>
          Üzgünüz, bir şeyler ters gitti. Lütfen daha sonra tekrar deneyin. Sorun devam ederse, lütfen bizimle iletişime geçin.
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
            startIcon={<RefreshIcon />}
            onClick={() => window.location.reload()}
          >
            Sayfayı Yenile
          </Button>
        </Box>
      </Box>
    </Container>
  );
}

