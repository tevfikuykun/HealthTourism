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
  IconButton,
} from '@mui/material';
import { Link as RouterLink, useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { useAuth } from '../hooks/useAuth';
import { authService } from '../services/api';
import EmailIcon from '@mui/icons-material/Email';
import LockIcon from '@mui/icons-material/Lock';
import PersonIcon from '@mui/icons-material/Person';
import PhoneIcon from '@mui/icons-material/Phone';
import VisibilityIcon from '@mui/icons-material/Visibility';
import VisibilityOffIcon from '@mui/icons-material/VisibilityOff';
import Loading from '../components/Loading';
import { useTranslation } from '../i18n';

const registerSchema = yup.object().shape({
  firstName: yup.string().required('Ad gereklidir'),
  lastName: yup.string().required('Soyad gereklidir'),
  email: yup
    .string()
    .email('Geçerli bir e-posta adresi girin')
    .required('E-posta adresi gereklidir'),
  phone: yup
    .string()
    .matches(/^[0-9+\-\s()]+$/, 'Geçerli bir telefon numarası girin')
    .required('Telefon numarası gereklidir'),
  password: yup
    .string()
    .min(8, 'Şifre en az 8 karakter olmalıdır')
    .matches(
      /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/,
      'Şifre en az bir büyük harf, bir küçük harf ve bir rakam içermelidir'
    )
    .required('Şifre gereklidir'),
  confirmPassword: yup
    .string()
    .oneOf([yup.ref('password'), null], 'Şifreler eşleşmiyor')
    .required('Şifre tekrarı gereklidir'),
});

export default function Register() {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const { login } = useAuth();
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
    resolver: yupResolver(registerSchema),
  });

  const onSubmit = async (data) => {
    try {
      setIsLoading(true);
      setError('');
      setSuccess(false);

      const { confirmPassword, ...registerData } = data;
      const response = await authService.register(registerData);

      setSuccess(true);

      // Auto login after registration
      const loginResponse = await authService.login({
        email: data.email,
        password: data.password,
      });

      const { token, refreshToken, user } = loginResponse.data;
      localStorage.setItem('token', token);
      localStorage.setItem('refreshToken', refreshToken);
      localStorage.setItem('user', JSON.stringify(user));
      login(user);

      // Redirect to home
      setTimeout(() => {
        navigate('/');
      }, 2000);
    } catch (err) {
      setError(err.response?.data?.message || 'Kayıt olurken bir hata oluştu');
    } finally {
      setIsLoading(false);
    }
  };

  if (isLoading) {
    return <Loading message="Kayıt yapılıyor..." fullScreen />;
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
        <Paper
          elevation={3}
          sx={{
            p: 4,
            width: '100%',
          }}
        >
          <Typography variant="h4" component="h1" gutterBottom align="center">
            {t('registerTitle', 'Kayıt Ol')}
          </Typography>
          <Typography variant="body2" color="text.secondary" align="center" sx={{ mb: 3 }}>
            {t('registerSubtitle', 'Yeni hesap oluşturun')}
          </Typography>

          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}

          {success && (
            <Alert severity="success" sx={{ mb: 2 }}>
              Kayıt başarılı! Yönlendiriliyorsunuz...
            </Alert>
          )}

          <Box component="form" onSubmit={handleSubmit(onSubmit)}>
            <Box sx={{ display: 'flex', gap: 2, mb: 2 }}>
              <TextField
                {...register('firstName')}
                fullWidth
                label={t('firstName')}
                error={!!errors.firstName}
                helperText={errors.firstName?.message}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <PersonIcon />
                    </InputAdornment>
                  ),
                }}
              />
              <TextField
                {...register('lastName')}
                fullWidth
                label={t('lastName', 'Soyad')}
                error={!!errors.lastName}
                helperText={errors.lastName?.message}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <PersonIcon />
                    </InputAdornment>
                  ),
                }}
              />
            </Box>

            <TextField
              {...register('email')}
              fullWidth
              label={t('email')}
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

            <TextField
              {...register('phone')}
              fullWidth
              label={t('phone', 'Telefon')}
              error={!!errors.phone}
              helperText={errors.phone?.message}
              sx={{ mb: 2 }}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <PhoneIcon />
                  </InputAdornment>
                ),
              }}
            />

            <TextField
              {...register('password')}
              fullWidth
              label={t('password')}
              type={showPassword ? 'text' : 'password'}
              error={!!errors.password}
              helperText={errors.password?.message}
              sx={{ mb: 2 }}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <LockIcon />
                  </InputAdornment>
                ),
                endAdornment: (
                  <InputAdornment position="end">
                    <IconButton
                      onClick={() => setShowPassword(!showPassword)}
                      edge="end"
                    >
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
                    <IconButton
                      onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                      edge="end"
                    >
                      {showConfirmPassword ? <VisibilityOffIcon /> : <VisibilityIcon />}
                    </IconButton>
                  </InputAdornment>
                ),
              }}
            />

            <Button
              type="submit"
              fullWidth
              variant="contained"
              size="large"
              sx={{ mb: 2 }}
            >
              {t('register')}
            </Button>

            <Typography variant="body2" align="center">
              {t('alreadyHaveAccount', 'Zaten hesabınız var mı?')}{' '}
              <Link component={RouterLink} to="/login">
                {t('login', 'Giriş Yap')}
              </Link>
            </Typography>
          </Box>
        </Paper>
      </Box>
    </Container>
  );
}

