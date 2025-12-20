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
            setSuccess(true);
            setError('Kayıt başarılı! Lütfen giriş yapın.');
            setTimeout(() => {
              navigate('/login');
            }, 3000);
          }
        } catch (loginErr) {
          console.error('Auto-login error:', loginErr);
          // Registration successful but auto-login failed
          // User can manually login
          setSuccess(true);
          setError('Kayıt başarılı! Lütfen giriş yapın.');
          setTimeout(() => {
            navigate('/login');
          }, 3000);
        }
      } else {
        throw new Error('Kayıt başarısız. Lütfen tekrar deneyin.');
      }
    } catch (err) {
      console.error('Registration error:', err);
      
      // Better error handling
      let errorMessage = 'Kayıt olurken bir hata oluştu';
      
      if (err.response) {
        // Server responded with error - show detailed error message
        const responseData = err.response.data;
        errorMessage = responseData?.message || 
                      responseData?.error || 
                      responseData?.errorMessage ||
                      (responseData?.errors ? JSON.stringify(responseData.errors) : null) ||
                      `Sunucu hatası (${err.response.status}): ${responseData?.status || 'Bilinmeyen hata'}`;
        
        // Log full error for debugging
        console.error('Registration server error:', {
          status: err.response.status,
          data: responseData,
          headers: err.response.headers
        });
      } else if (err.request) {
        // Request made but no response - Backend is not running or not accessible
        errorMessage = 'Backend servisine bağlanılamadı. Lütfen backend servislerinin çalıştığından emin olun. (http://localhost:8080)';
        console.error('Backend connection error:', err.request);
      } else {
        // Error in request setup
        errorMessage = err.message || 'Beklenmeyen bir hata oluştu';
      }
      
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

