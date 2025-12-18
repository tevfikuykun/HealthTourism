import React, { useState } from 'react';
import {
  Container,
  Box,
  Paper,
  TextField,
  Button,
  Typography,
  Alert,
  InputAdornment,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import AdminPanelSettingsIcon from '@mui/icons-material/AdminPanelSettings';
import EmailIcon from '@mui/icons-material/Email';
import LockIcon from '@mui/icons-material/Lock';
import Loading from '../../components/Loading';
import { useTranslation } from 'react-i18next';

export default function AdminLogin() {
  const { t } = useTranslation();
  
  const adminLoginSchema = yup.object().shape({
    email: yup.string().email(t('validEmail', 'Geçerli bir e-posta adresi girin')).required(t('emailRequired', 'E-posta adresi gereklidir')),
    password: yup.string().required(t('passwordRequired', 'Şifre gereklidir')),
  });
  const navigate = useNavigate();
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: yupResolver(adminLoginSchema),
  });

  const onSubmit = async (data) => {
    try {
      setIsLoading(true);
      setError('');
      
      const response = await adminService.login(data);
      
      if (response.data) {
        localStorage.setItem('adminToken', response.data.token || response.data.accessToken);
        localStorage.setItem('adminUser', JSON.stringify(response.data.user || response.data));
        navigate('/admin/dashboard');
      } else {
        setError(t('loginFailed', 'Giriş başarısız. E-posta ve şifrenizi kontrol edin.'));
      }
    } catch (err) {
      setError(err.response?.data?.message || t('loginFailed', 'Giriş başarısız. E-posta ve şifrenizi kontrol edin.'));
    } finally {
      setIsLoading(false);
    }
  };

  if (isLoading) {
    return <Loading message={t('loggingIn', 'Giriş yapılıyor...')} fullScreen />;
  }

  return (
    <Container maxWidth="sm">
      <Box
        sx={{
          minHeight: '100vh',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          py: 4,
          background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
        }}
      >
        <Paper elevation={10} sx={{ p: 4, width: '100%' }}>
          <Box sx={{ textAlign: 'center', mb: 3 }}>
            <AdminPanelSettingsIcon sx={{ fontSize: 60, color: 'primary.main', mb: 2 }} />
            <Typography variant="h4" component="h1" gutterBottom>
              {t('adminPanel', 'Admin Panel')}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              {t('adminLogin', 'Yönetici girişi yapın')}
            </Typography>
          </Box>

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
              type="password"
              error={!!errors.password}
              helperText={errors.password?.message}
              sx={{ mb: 2 }}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <LockIcon />
                  </InputAdornment>
                ),
              }}
            />

            <Button type="submit" fullWidth variant="contained" size="large">
              {t('loginButton', 'Giriş Yap')}
            </Button>
          </Box>
        </Paper>
      </Box>
    </Container>
  );
}

