import React, { useState, useRef, useEffect } from 'react';
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
  firstName: yup
    .string()
    .required('Ad gereklidir')
    .min(2, 'Ad en az 2 karakter olmalıdır')
    .max(50, 'Ad en fazla 50 karakter olabilir')
    .matches(/^[a-zA-Z\s]+$/, 'Ad sadece harf içermelidir'),
  lastName: yup
    .string()
    .required('Soyad gereklidir')
    .min(2, 'Soyad en az 2 karakter olmalıdır')
    .max(50, 'Soyad en fazla 50 karakter olabilir')
    .matches(/^[a-zA-Z\s]+$/, 'Soyad sadece harf içermelidir'),
  email: yup
    .string()
    .email('Geçerli bir e-posta adresi girin')
    .required('E-posta adresi gereklidir')
    .max(255, 'E-posta en fazla 255 karakter olabilir'),
  phone: yup
    .string()
    .required('Telefon numarası gereklidir')
    .matches(
      /^[+]?[(]?[0-9]{1,4}[)]?[-\s.]?[(]?[0-9]{1,4}[)]?[-\s.]?[0-9]{1,9}$/,
      'Geçerli bir telefon numarası girin (örn: +905551234567)'
    ),
  country: yup
    .string()
    .required('Ülke gereklidir')
    .max(100, 'Ülke adı en fazla 100 karakter olabilir'),
  password: yup
    .string()
    .required('Şifre gereklidir')
    .min(8, 'Şifre en az 8 karakter olmalıdır')
    .max(128, 'Şifre en fazla 128 karakter olabilir')
    .matches(
      /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/,
      'Şifre en az bir büyük harf, bir küçük harf, bir rakam ve bir özel karakter (@$!%*?&) içermelidir'
    ),
  confirmPassword: yup
    .string()
    .oneOf([yup.ref('password'), null], 'Şifreler eşleşmiyor')
    .required('Şifre tekrarı gereklidir'),
});

export default function Register() {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const formRef = useRef(null);
  const alertRef = useRef(null);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: yupResolver(registerSchema),
  });

  // Scroll to alert when error or success message appears
  useEffect(() => {
    if ((error || success) && alertRef.current) {
      alertRef.current.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  }, [error, success]);

  const onSubmit = async (data) => {
    try {
      setIsLoading(true);
      setError('');
      setSuccess(false);

      // Scroll to form to show messages
      if (formRef.current) {
        formRef.current.scrollIntoView({ behavior: 'smooth', block: 'start' });
      }

      const { confirmPassword, ...registerData } = data;
      
      // Ensure country is set (default to Turkey if not provided)
      if (!registerData.country || registerData.country.trim() === '') {
        registerData.country = 'Türkiye';
      }
      
      const response = await authService.register(registerData);

      // Check if registration was successful
      const responseData = response?.data || response;
      
      if (responseData) {
        setSuccess(true);
        
        // Scroll to form to show success message
        if (formRef.current) {
          formRef.current.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }

        // Auto login after registration
        try {
          const loginResponse = await authService.login({
            email: data.email,
            password: data.password,
          }, { suppressErrorToast: true });

          const loginData = loginResponse?.data || loginResponse;
          const token = loginData?.token || loginData?.accessToken;
          const refreshToken = loginData?.refreshToken;
          const user = loginData?.user || loginData;

          if (token) {
            localStorage.setItem('token', token);
            if (refreshToken) {
              localStorage.setItem('refreshToken', refreshToken);
            }
            localStorage.setItem('user', JSON.stringify(user));

            // Redirect to home
            setTimeout(() => {
              window.location.href = '/';
            }, 2000);
          } else {
            // Registration successful but auto-login failed
            setSuccess(false); // Clear success message
            setError('Kayıt başarılı! Lütfen giriş yapın.');
            setTimeout(() => {
              navigate('/login');
            }, 3000);
          }
        } catch (loginErr) {
          console.error('Auto-login error:', loginErr);
          // Registration successful but auto-login failed
          // User can manually login
          setSuccess(false); // Clear success message
          setError('Kayıt başarılı! Lütfen giriş yapın.');
          setTimeout(() => {
            navigate('/login');
          }, 3000);
        }
      } else {
        throw new Error('Kayıt başarısız. Lütfen tekrar deneyin.');
      }
    } catch (err) {
      console.error('=== REGISTRATION ERROR START ===');
      console.error('Error object:', err);
      console.error('Error name:', err?.name);
      console.error('Error message:', err?.message);
      console.error('Error statusCode:', err?.statusCode);
      console.error('Error code:', err?.code);
      
      // Check for response data (from axios)
      if (err?.response) {
        console.error('Response status:', err.response.status);
        console.error('Response data:', err.response.data);
        console.error('Response headers:', err.response.headers);
      }
      
      // Check for request (network error)
      if (err?.request) {
        console.error('Request object:', err.request);
      }
      
      console.error('=== REGISTRATION ERROR END ===');
      
      // Better error handling
      let errorMessage = 'Kayıt olurken bir hata oluştu';
      
      // Priority 1: Check response.data (direct backend response)
      if (err?.response?.data) {
        const responseData = err.response.data;
        console.log('Response data:', responseData);
        
        if (responseData.message) {
          errorMessage = responseData.message;
        } else if (responseData.error) {
          errorMessage = responseData.error;
        } else if (responseData.errorMessage) {
          errorMessage = responseData.errorMessage;
        } else if (Array.isArray(responseData.errors) && responseData.errors.length > 0) {
          errorMessage = responseData.errors.map(e => e.defaultMessage || e.message || e).join(', ');
        } else if (typeof responseData === 'string') {
          errorMessage = responseData;
        } else {
          errorMessage = `Sunucu hatası (${err.response.status}): ${JSON.stringify(responseData)}`;
        }
      }
      // Priority 2: Check if it's an AppError instance (from api.js interceptor)
      else if (err?.name === 'AppError' && err?.message) {
        // Check if it's a network error
        if (err.code === 'NETWORK_ERROR') {
          errorMessage = 'Backend servisine bağlanılamadı. Lütfen backend servislerinin çalıştığından emin olun. (http://localhost:8080)';
        } else {
          errorMessage = err.message;
        }
      }
      // Priority 3: Check err.message (generic error)
      else if (err?.message) {
        // Check if it's a network error message
        if (err.message.includes('Network Error') || err.message.includes('network')) {
          errorMessage = 'Backend servisine bağlanılamadı. Lütfen backend servislerinin çalıştığından emin olun. (http://localhost:8080)';
        } else {
          errorMessage = err.message;
        }
      }
      // Priority 4: Network error (no response)
      else if (err?.request) {
        errorMessage = 'Backend servisine bağlanılamadı. Lütfen backend servislerinin çalıştığından emin olun. (http://localhost:8080)';
      }
      // Priority 5: Fallback
      else {
        errorMessage = `Beklenmeyen bir hata oluştu: ${err?.name || 'Unknown error'}`;
      }
      
      console.log('Final error message:', errorMessage);
      setError(errorMessage);
      
      // Scroll to form to show error message
      if (formRef.current) {
        formRef.current.scrollIntoView({ behavior: 'smooth', block: 'start' });
      }
    } finally {
      setIsLoading(false);
    }
  };

  if (isLoading) {
    return <Loading message="Kayıt yapılıyor..." fullScreen />;
  }

  return (
    <Container maxWidth="sm" sx={{ py: 4, minHeight: 'auto' }}>
      <Box
        sx={{
          minHeight: '80vh',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          py: 4,
          position: 'relative',
        }}
      >
        <Paper
          ref={formRef}
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

          <Box ref={alertRef}>
            {error && (
              <Alert 
                severity="error" 
                sx={{ mb: 2 }}
                onClose={() => setError('')}
              >
                {error}
              </Alert>
            )}

            {success && (
              <Alert severity="success" sx={{ mb: 2 }}>
                Kayıt başarılı! Yönlendiriliyorsunuz...
              </Alert>
            )}
          </Box>

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
              {...register('country')}
              fullWidth
              label={t('country', 'Ülke')}
              error={!!errors.country}
              helperText={errors.country?.message}
              sx={{ mb: 2 }}
              placeholder="Türkiye"
              defaultValue="Türkiye"
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

