import React, { useEffect, useState } from 'react';
import {
  Container,
  Box,
  Paper,
  Typography,
  Button,
  Alert,
  CircularProgress,
} from '@mui/material';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { authService } from '../services/api';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import ErrorIcon from '@mui/icons-material/Error';
import { useTranslation } from 'react-i18next';

export default function VerifyEmail() {
  const { t } = useTranslation();
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const token = searchParams.get('token');
  const [status, setStatus] = useState('verifying'); // verifying, success, error
  const [error, setError] = useState('');

  useEffect(() => {
    const verifyEmail = async () => {
      if (!token) {
        setStatus('error');
        setError(t('verificationTokenNotFound', 'Doğrulama token\'ı bulunamadı'));
        return;
      }

      try {
        await authService.verifyEmail(token);
        setStatus('success');
      } catch (err) {
        setStatus('error');
        setError(err.response?.data?.message || t('emailVerificationError', 'E-posta doğrulanırken bir hata oluştu'));
      }
    };

    verifyEmail();
  }, [token]);

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
          {status === 'verifying' && (
            <>
              <CircularProgress sx={{ mb: 2 }} />
              <Typography variant="h5" gutterBottom>
                {t('verifyingEmail', 'E-posta Doğrulanıyor...')}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                {t('pleaseWait', 'Lütfen bekleyin.')}
              </Typography>
            </>
          )}

          {status === 'success' && (
            <>
              <CheckCircleIcon sx={{ fontSize: 80, color: 'success.main', mb: 2 }} />
              <Typography variant="h5" gutterBottom>
                {t('emailVerifiedSuccess', 'E-posta Başarıyla Doğrulandı!')}
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
                {t('accountActivated', 'Hesabınız aktif edildi. Şimdi giriş yapabilirsiniz.')}
              </Typography>
              <Button variant="contained" onClick={() => navigate('/login')}>
                {t('login', 'Giriş Yap')}
              </Button>
            </>
          )}

          {status === 'error' && (
            <>
              <ErrorIcon sx={{ fontSize: 80, color: 'error.main', mb: 2 }} />
              <Typography variant="h5" gutterBottom>
                {t('verificationFailed', 'Doğrulama Başarısız')}
              </Typography>
              <Alert severity="error" sx={{ mb: 2 }}>
                {error}
              </Alert>
              <Button variant="contained" onClick={() => navigate('/login')}>
                {t('backToLoginPage', 'Giriş Sayfasına Dön')}
              </Button>
            </>
          )}
        </Paper>
      </Box>
    </Container>
  );
}

