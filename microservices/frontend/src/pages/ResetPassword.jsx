import React, { useState, useEffect } from 'react';
import {
  Container,
  Box,
  Paper,
  TextField,
  Button,
  Typography,
  Alert,
  InputAdornment,
  IconButton,
} from '@mui/material';
import { useNavigate, useParams } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { authService } from '../services/api';
import LockIcon from '@mui/icons-material/Lock';
import VisibilityIcon from '@mui/icons-material/Visibility';
import VisibilityOffIcon from '@mui/icons-material/VisibilityOff';
import Loading from '../components/Loading';
import { useTranslation } from 'react-i18next';

const getResetPasswordSchema = (t) => yup.object().shape({
  newPassword: yup
    .string()
    .min(8, t('passwordMinLength', 'Şifre en az 8 karakter olmalıdır', { min: 8 }))
    .matches(
      /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/,
      t('passwordRequirements', 'Şifre en az bir büyük harf, bir küçük harf ve bir rakam içermelidir')
    )
    .required(t('passwordRequired', 'Şifre gereklidir')),
  confirmPassword: yup
    .string()
    .oneOf([yup.ref('newPassword'), null], t('passwordsMismatch', 'Şifreler eşleşmiyor'))
    .required(t('confirmPasswordRequired', 'Şifre tekrarı gereklidir')),
});

export default function ResetPassword() {
  const { t } = useTranslation();
  const { token } = useParams();
  const navigate = useNavigate();
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: yupResolver(getResetPasswordSchema(t)),
  });

  const onSubmit = async (data) => {
    try {
      setIsLoading(true);
      setError('');
      setSuccess(false);

      await authService.resetPassword(token, data.newPassword);
      setSuccess(true);

      setTimeout(() => {
        navigate('/login');
      }, 2000);
    } catch (err) {
      setError(err.response?.data?.message || t('resetPasswordError', 'Şifre sıfırlanırken bir hata oluştu'));
    } finally {
      setIsLoading(false);
    }
  };

  if (isLoading) {
    return <Loading message={t('resettingPassword', 'Şifre sıfırlanıyor...')} fullScreen />;
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
            {t('setNewPassword', 'Yeni Şifre Belirle')}
          </Typography>
          <Typography variant="body2" color="text.secondary" align="center" sx={{ mb: 3 }}>
            {t('enterNewPassword', 'Yeni şifrenizi girin.')}
          </Typography>

          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}

          {success && (
            <Alert severity="success" sx={{ mb: 2 }}>
              {t('passwordResetSuccess', 'Şifreniz başarıyla sıfırlandı! Giriş sayfasına yönlendiriliyorsunuz...')}
            </Alert>
          )}

          {!success && (
            <Box component="form" onSubmit={handleSubmit(onSubmit)}>
              <TextField
                {...register('newPassword')}
                fullWidth
                label={t('newPassword', 'Yeni Şifre')}
                type={showPassword ? 'text' : 'password'}
                error={!!errors.newPassword}
                helperText={errors.newPassword?.message}
                sx={{ mb: 2 }}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <LockIcon />
                    </InputAdornment>
                  ),
                  endAdornment: (
                    <InputAdornment position="end">
                      <IconButton onClick={() => setShowPassword(!showPassword)} edge="end">
                        {showPassword ? <VisibilityOffIcon /> : <VisibilityIcon />}
                      </IconButton>
                    </InputAdornment>
                  ),
                }}
              />

              <TextField
                {...register('confirmPassword')}
                fullWidth
                label={t('confirmPassword', 'Şifre Tekrar')}
                type={showConfirmPassword ? 'text' : 'password'}
                error={!!errors.confirmPassword}
                helperText={errors.confirmPassword?.message}
                sx={{ mb: 2 }}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <LockIcon />
                    </InputAdornment>
                  ),
                  endAdornment: (
                    <InputAdornment position="end">
                      <IconButton onClick={() => setShowConfirmPassword(!showConfirmPassword)} edge="end">
                        {showConfirmPassword ? <VisibilityOffIcon /> : <VisibilityIcon />}
                      </IconButton>
                    </InputAdornment>
                  ),
                }}
              />

              <Button type="submit" fullWidth variant="contained" size="large">
                {t('resetPassword', 'Şifreyi Sıfırla')}
              </Button>
            </Box>
          )}
        </Paper>
      </Box>
    </Container>
  );
}

