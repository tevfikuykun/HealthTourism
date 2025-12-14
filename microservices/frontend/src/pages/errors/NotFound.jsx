import React from 'react';
import { Container, Box, Typography, Button } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import ErrorOutlineIcon from '@mui/icons-material/ErrorOutline';
import HomeIcon from '@mui/icons-material/Home';
import { useTranslation } from 'react-i18next';

export default function NotFound() {
  const { t } = useTranslation();
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
        <ErrorOutlineIcon sx={{ fontSize: 150, color: 'error.main', mb: 2 }} />
        <Typography variant="h1" component="h1" gutterBottom>
          404
        </Typography>
        <Typography variant="h4" gutterBottom>
          {t('pageNotFound', 'Sayfa Bulunamadı')}
        </Typography>
        <Typography variant="body1" color="text.secondary" sx={{ mb: 4, maxWidth: 500 }}>
          {t('pageNotFoundDescription', 'Aradığınız sayfa mevcut değil veya taşınmış olabilir. Lütfen URL\'yi kontrol edin veya ana sayfaya dönün.')}
        </Typography>
        <Box sx={{ display: 'flex', gap: 2 }}>
          <Button
            variant="contained"
            size="large"
            startIcon={<HomeIcon />}
            onClick={() => navigate('/')}
          >
            {t('backToHome', 'Ana Sayfaya Dön')}
          </Button>
          <Button variant="outlined" size="large" onClick={() => navigate(-1)}>
            {t('goBack', 'Geri Dön')}
          </Button>
        </Box>
      </Box>
    </Container>
  );
}

