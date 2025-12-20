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
  Divider,
} from '@mui/material';
import { Link as RouterLink, useNavigate, useLocation } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { authService } from '../services/api';
import EmailIcon from '@mui/icons-material/Email';
import LockIcon from '@mui/icons-material/Lock';
import VisibilityIcon from '@mui/icons-material/Visibility';
import VisibilityOffIcon from '@mui/icons-material/VisibilityOff';
import Loading from '../components/Loading';
import SocialLogin from '../components/SocialLogin/SocialLogin';
import { useTranslation } from '../i18n';

const loginSchema = yup.object().shape({
  email: yup
    .string()
    .email('Geçerli bir e-posta adresi girin')
    .required('E-posta adresi gereklidir'),
  password: yup
    .string()
    .min(6, 'Şifre en az 6 karakter olmalıdır')
    .required('Şifre gereklidir'),
});

export default function Login() {
  // Güvenli useTranslation hook'u kullan (i18n.js'den import edildi)
  const { t } = useTranslation();
  const navigate = useNavigate();
  const location = useLocation();
  const { login } = useAuth();
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: yupResolver(loginSchema),
  });

  const from = location.state?.from?.pathname || '/';

  const onSubmit = async (data) => {
    try {
      setIsLoading(true);
      setError('');

      // Suppress toast notifications for login (we show Alert instead)
      // Suppress toast notifications for login endpoint - we handle errors ourselves
      const response = await authService.login(data, { suppressErrorToast: true });
      
      // Handle different response formats
      const responseData = response?.data || response;
      const token = responseData?.token || responseData?.accessToken;
      const refreshToken = responseData?.refreshToken;
      const user = responseData?.user || responseData;

      if (!token) {
        throw new Error('Token alınamadı. Lütfen tekrar deneyin.');
      }

      // Store tokens
      localStorage.setItem('token', token);
      if (refreshToken) {
        localStorage.setItem('refreshToken', refreshToken);
      }
      localStorage.setItem('user', JSON.stringify(user));

      // Update auth context - useAuth hook expects mutate format
      // But we'll trigger a page reload or use window.location to ensure state is updated
      window.location.href = from === '/' ? '/' : from;
    } catch (err) {
      console.error('Login error:', err);
      let errorMessage = 'Giriş yapılırken bir hata oluştu';
      
      if (err.response) {
        // Server responded with error - show detailed error message
        const responseData = err.response.data;
        errorMessage = responseData?.message || 
                      responseData?.error || 
                      responseData?.errorMessage ||
                      (responseData?.errors ? JSON.stringify(responseData.errors) : null) ||
                      `Sunucu hatası (${err.response.status}): ${responseData?.status || 'Bilinmeyen hata'}`;
        
        // Log full error for debugging
        console.error('Login server error:', {
          status: err.response.status,
          data: responseData,
          headers: err.response.headers
        });
      } else if (err.request) {
        // Request made but no response - Backend is not running
        errorMessage = 'Backend servisine bağlanılamadı. Lütfen backend servislerinin çalıştığından emin olun. (http://localhost:8080)';
        console.error('Backend connection error:', err.request);
      } else {
        // Error in request setup
        errorMessage = err.message || 'Beklenmeyen bir hata oluştu';
      }
      
      setError(errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  if (isLoading) {
    return <Loading message="Giriş yapılıyor..." fullScreen />;
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
            {t('loginTitle', 'Giriş Yap')}
          </Typography>
          <Typography variant="body2" color="text.secondary" align="center" sx={{ mb: 3 }}>
            {t('loginSubtitle', 'Hesabınıza giriş yapın')}
          </Typography>

          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}

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

            <TextField
              {...register('password')}
              fullWidth
              label={t('password', 'Şifre')}
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

            <Box sx={{ textAlign: 'right', mb: 2 }}>
              <Link
                component={RouterLink}
                to="/forgot-password"
                variant="body2"
              >
                {t('forgotPassword', 'Şifrenizi mi unuttunuz?')}
              </Link>
            </Box>

            <Button
              type="submit"
              fullWidth
              variant="contained"
              size="large"
              sx={{ mb: 2 }}
            >
              {t('login', 'Giriş Yap')}
            </Button>

            <SocialLogin />

            <Typography variant="body2" align="center" sx={{ mt: 2 }}>
              {t('noAccount', 'Hesabınız yok mu?')}{' '}
              <Link component={RouterLink} to="/register">
                {t('register', 'Kayıt Ol')}
              </Link>
            </Typography>
          </Box>
        </Paper>
      </Box>
    </Container>
  );
}

