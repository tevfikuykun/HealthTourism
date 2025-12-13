import React from 'react';
import { Container, Box, Typography, Button } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import LockIcon from '@mui/icons-material/Lock';
import LoginIcon from '@mui/icons-material/Login';

export default function Unauthorized() {
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
        <LockIcon sx={{ fontSize: 150, color: 'warning.main', mb: 2 }} />
        <Typography variant="h1" component="h1" gutterBottom>
          401
        </Typography>
        <Typography variant="h4" gutterBottom>
          Yetkisiz Erişim
        </Typography>
        <Typography variant="body1" color="text.secondary" sx={{ mb: 4, maxWidth: 500 }}>
          Bu sayfayı görüntülemek için giriş yapmanız gerekmektedir. Lütfen giriş yapın veya kayıt olun.
        </Typography>
        <Box sx={{ display: 'flex', gap: 2 }}>
          <Button
            variant="contained"
            size="large"
            startIcon={<LoginIcon />}
            onClick={() => navigate('/login')}
          >
            Giriş Yap
          </Button>
          <Button
            variant="outlined"
            size="large"
            onClick={() => navigate('/register')}
          >
            Kayıt Ol
          </Button>
        </Box>
      </Box>
    </Container>
  );
}

