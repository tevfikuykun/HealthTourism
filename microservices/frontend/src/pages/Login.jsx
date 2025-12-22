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
  // useAuth hook removed - using authService directly
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

      // Debug: Log the data being sent
      console.log('Login request data:', data);

      // Suppress toast notifications for login (we show Alert instead)
      // Suppress toast notifications for login endpoint - we handle errors ourselves
      const response = await authService.login(data, { suppressErrorToast: true });
      
      // Debug: Log the response
      console.log('Login response:', response);
      
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
      console.error('=== LOGIN ERROR START ===');
      console.error('Error object:', err);
      console.error('Error name:', err?.name);
      console.error('Error message:', err?.message);
      console.error('Error statusCode:', err?.statusCode);
      console.error('Error code:', err?.code);
      
      // Check for response data (from axios)
      if (err?.response) {
        console.error('Response status:', err.response.status);
        console.error('Response data (raw):', err.response.data);
        console.error('Response data (stringified):', JSON.stringify(err.response.data, null, 2));
        console.error('Response data type:', typeof err.response.data);
        console.error('Response headers:', err.response.headers);
      }
      
      // Check for request (network error)
      if (err?.request) {
        console.error('Request object:', err.request);
      }
      
      console.error('=== LOGIN ERROR END ===');
      
      let errorMessage = 'Giriş yapılırken bir hata oluştu';
      
      // Priority 1: Check response.data FIRST (direct backend response - most accurate)
      if (err?.response?.data) {
        const responseData = err.response.data;
        console.log('Response data (raw):', responseData);
        console.log('Response data (stringified):', JSON.stringify(responseData, null, 2));
        
        // Try different possible error message fields
        if (responseData.message) {
          errorMessage = responseData.message;
          console.log('Found message in responseData.message:', errorMessage);
        } else if (responseData.error) {
          errorMessage = responseData.error;
          console.log('Found message in responseData.error:', errorMessage);
        } else if (responseData.errorMessage) {
          errorMessage = responseData.errorMessage;
          console.log('Found message in responseData.errorMessage:', errorMessage);
        } else if (Array.isArray(responseData.errors) && responseData.errors.length > 0) {
          errorMessage = responseData.errors.map(e => e.defaultMessage || e.message || e).join(', ');
          console.log('Found message in responseData.errors:', errorMessage);
        } else if (typeof responseData === 'string') {
          errorMessage = responseData;
          console.log('Response data is string:', errorMessage);
        } else {
          // If no message found, show the full response for debugging
          errorMessage = `Geçersiz istek. Detaylar: ${JSON.stringify(responseData)}`;
          console.log('No message found, using full response:', errorMessage);
        }
      }
      // Priority 2: Check if it's an AppError instance (from api.js interceptor)
      else if (err?.name === 'AppError' && err?.message) {
        errorMessage = err.message;
        console.log('Using AppError message:', errorMessage);
      }
      // Priority 3: Check err.message (generic error)
      else if (err?.message) {
        errorMessage = err.message;
        console.log('Using error message:', errorMessage);
      }
      // Priority 4: Network error
      else if (err?.request) {
        errorMessage = 'Backend servisine bağlanılamadı. Lütfen backend servislerinin çalıştığından emin olun. (http://localhost:8080)';
        console.log('Using network error message');
      }
      // Priority 5: Fallback
      else {
        errorMessage = `Beklenmeyen bir hata oluştu: ${err?.name || 'Unknown error'}`;
        console.log('Using fallback message');
      }
      
      console.log('Final error message:', errorMessage);
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

