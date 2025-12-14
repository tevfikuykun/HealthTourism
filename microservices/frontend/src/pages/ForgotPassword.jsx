import React, { useState } from 'react';
import {
  Container,
  Box,
  Paper,
  TextField,
  Button,
  Typography,
  Link,
  Alert,
  InputAdornment,
} from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { authService } from '../services/api';
import EmailIcon from '@mui/icons-material/Email';
import Loading from '../components/Loading';
import { useTranslation } from 'react-i18next';

const getForgotPasswordSchema = (t) => yup.object().shape({
  email: yup
    .string()
    .email(t('validEmail', 'Geçerli bir e-posta adresi girin'))
    .required(t('emailRequired', 'E-posta adresi gereklidir')),
});

export default function ForgotPassword() {
  const { t } = useTranslation();
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: yupResolver(getForgotPasswordSchema(t)),
  });

  const onSubmit = async (data) => {
    try {
      setIsLoading(true);
      setError('');
      setSuccess(false);

      await authService.forgotPassword(data.email);
      setSuccess(true);
    } catch (err) {
      setError(err.response?.data?.message || t('forgotPasswordError', 'Şifre sıfırlama e-postası gönderilirken bir hata oluştu'));
    } finally {
      setIsLoading(false);
    }
  };

  if (isLoading) {
    return <Loading message={t('processing', 'İşleniyor...')} fullScreen />;
  }

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
        <Paper elevation={3} sx={{ p: 4, width: '100%' }}>
          <Typography variant="h4" component="h1" gutterBottom align="center">
            {t('forgotPassword', 'Şifremi Unuttum')}
          </Typography>
          <Typography variant="body2" color="text.secondary" align="center" sx={{ mb: 3 }}>
            {t('forgotPasswordDescription', 'E-posta adresinizi girin, size şifre sıfırlama linki gönderelim.')}
          </Typography>

          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}

          {success && (
            <Alert severity="success" sx={{ mb: 2 }}>
              {t('resetLinkSent', 'Şifre sıfırlama linki e-posta adresinize gönderildi. Lütfen e-postanızı kontrol edin.')}
            </Alert>
          )}

          {!success && (
            <Box component="form" onSubmit={handleSubmit(onSubmit)}>
              <TextField
                {...register('email')}
                fullWidth
                label={t('email', 'E-posta')}
                type="email"
                error={!!errors.email}
                helperText={errors.email?.message}
                sx={{ mb: 2 }}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <EmailIcon />
                    </InputAdornment>
                  ),
                }}
              />

              <Button type="submit" fullWidth variant="contained" size="large" sx={{ mb: 2 }}>
                {t('sendResetLink', 'Şifre Sıfırlama Linki Gönder')}
              </Button>

              <Typography variant="body2" align="center">
                <Link component={RouterLink} to="/login">
                  {t('backToLogin', 'Giriş sayfasına dön')}
                </Link>
              </Typography>
            </Box>
          )}

          {success && (
            <Box sx={{ textAlign: 'center' }}>
              <Button component={RouterLink} to="/login" variant="contained" sx={{ mt: 2 }}>
                {t('backToLoginPage', 'Giriş Sayfasına Dön')}
              </Button>
            </Box>
          )}
        </Paper>
      </Box>
    </Container>
  );
}

