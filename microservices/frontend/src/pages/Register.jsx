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
  FormControl,
  InputLabel,
  Select,
  MenuItem,
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
import SupervisorAccountIcon from '@mui/icons-material/SupervisorAccount';
import LocalHospitalIcon from '@mui/icons-material/LocalHospital';
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
  role: yup
    .string()
    .oneOf(['USER', 'DOCTOR'], 'Geçerli bir rol seçin')
    .required('Rol seçimi gereklidir'),
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

  // Debug: Check if component is rendering
  useEffect(() => {
    console.log('Register component mounted');
    return () => console.log('Register component unmounted');
  }, []);

  const {
    register,
    handleSubmit,
    formState: { errors },
    setValue,
    watch,
  } = useForm({
    resolver: yupResolver(registerSchema),
    defaultValues: {
      role: 'USER',
      country: 'Türkiye',
    },
  });

  const selectedRole = watch('role');
  const selectedCountry = watch('country');

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
      
      // Ensure country is set (trim whitespace)
      if (registerData.country) {
        registerData.country = registerData.country.trim();
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

  // Debug: Log before render
  console.log('Register page rendering...', { isLoading, error, success });

  return (
    <Container maxWidth="sm" sx={{ py: 4, minHeight: 'auto', overflow: 'visible', position: 'relative' }}>
      <Box
        sx={{
          minHeight: '80vh',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          py: 4,
          position: 'relative',
          overflow: 'visible',
        }}
      >
        <Paper
          ref={formRef}
          elevation={3}
          sx={{
            p: 4,
            width: '100%',
            position: 'relative',
            overflow: 'visible',
            zIndex: 1,
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

            <FormControl fullWidth sx={{ mb: 2 }} error={!!errors.country}>
              <InputLabel>{t('country', 'Ülke')}</InputLabel>
              <Select
                value={selectedCountry || ''}
                onChange={(e) => setValue('country', e.target.value, { shouldValidate: true })}
                label={t('country', 'Ülke')}
                MenuProps={{
                  disablePortal: false,
                  container: typeof document !== 'undefined' ? document.body : undefined,
                  PaperProps: {
                    style: {
                      maxHeight: 300,
                      zIndex: 1300,
                    },
                    sx: {
                      zIndex: '1300 !important',
                    },
                  },
                }}
              >
                <MenuItem value="">{t('selectCountry', 'Ülke Seçiniz')}</MenuItem>
                <MenuItem value="Türkiye">Türkiye</MenuItem>
                <MenuItem value="Almanya">Almanya</MenuItem>
                <MenuItem value="İngiltere">İngiltere</MenuItem>
                <MenuItem value="Fransa">Fransa</MenuItem>
                <MenuItem value="İtalya">İtalya</MenuItem>
                <MenuItem value="İspanya">İspanya</MenuItem>
                <MenuItem value="Hollanda">Hollanda</MenuItem>
                <MenuItem value="Belçika">Belçika</MenuItem>
                <MenuItem value="İsviçre">İsviçre</MenuItem>
                <MenuItem value="Avusturya">Avusturya</MenuItem>
                <MenuItem value="ABD">ABD</MenuItem>
                <MenuItem value="Kanada">Kanada</MenuItem>
                <MenuItem value="Avustralya">Avustralya</MenuItem>
                <MenuItem value="Japonya">Japonya</MenuItem>
                <MenuItem value="Çin">Çin</MenuItem>
                <MenuItem value="Rusya">Rusya</MenuItem>
                <MenuItem value="Birleşik Arap Emirlikleri">Birleşik Arap Emirlikleri</MenuItem>
                <MenuItem value="Suudi Arabistan">Suudi Arabistan</MenuItem>
                <MenuItem value="Güney Kore">Güney Kore</MenuItem>
                <MenuItem value="Brezilya">Brezilya</MenuItem>
                <MenuItem value="Arjantin">Arjantin</MenuItem>
                <MenuItem value="Meksika">Meksika</MenuItem>
                <MenuItem value="Güney Afrika">Güney Afrika</MenuItem>
                <MenuItem value="Mısır">Mısır</MenuItem>
                <MenuItem value="Yunanistan">Yunanistan</MenuItem>
                <MenuItem value="İsveç">İsveç</MenuItem>
                <MenuItem value="Norveç">Norveç</MenuItem>
                <MenuItem value="Danimarka">Danimarka</MenuItem>
                <MenuItem value="Finlandiya">Finlandiya</MenuItem>
                <MenuItem value="Polonya">Polonya</MenuItem>
              </Select>
              {errors.country && (
                <Typography variant="caption" color="error" sx={{ mt: 0.5, ml: 1.75 }}>
                  {errors.country.message}
                </Typography>
              )}
            </FormControl>

            <FormControl fullWidth sx={{ mb: 2 }} error={!!errors.role}>
              <InputLabel>{t('role', 'Rol')}</InputLabel>
              <Select
                value={selectedRole || 'USER'}
                onChange={(e) => setValue('role', e.target.value, { shouldValidate: true })}
                label={t('role', 'Rol')}
                MenuProps={{
                  disablePortal: false,
                  container: typeof document !== 'undefined' ? document.body : undefined,
                  PaperProps: {
                    style: {
                      maxHeight: 300,
                      zIndex: 1300,
                    },
                    sx: {
                      zIndex: '1300 !important',
                    },
                  },
                }}
              >
                <MenuItem value="USER">
                  {t('rolePatient', 'Hasta')}
                </MenuItem>
                <MenuItem value="DOCTOR">
                  {t('roleDoctor', 'Doktor')}
                </MenuItem>
              </Select>
              {errors.role && (
                <Typography variant="caption" color="error" sx={{ mt: 0.5, ml: 1.75 }}>
                  {errors.role.message}
                </Typography>
              )}
            </FormControl>

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

