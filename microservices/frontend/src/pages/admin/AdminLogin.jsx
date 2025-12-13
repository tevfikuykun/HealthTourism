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

const adminLoginSchema = yup.object().shape({
  email: yup.string().email('Geçerli bir e-posta adresi girin').required('E-posta adresi gereklidir'),
  password: yup.string().required('Şifre gereklidir'),
});

export default function AdminLogin() {
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
      // TODO: Admin login API entegrasyonu
      // const response = await adminService.login(data);
      // localStorage.setItem('adminToken', response.data.token);
      // localStorage.setItem('adminUser', JSON.stringify(response.data.user));
      navigate('/admin/dashboard');
    } catch (err) {
      setError('Giriş başarısız. E-posta ve şifrenizi kontrol edin.');
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
              Admin Panel
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Yönetici girişi yapın
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
              label="E-posta"
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
              label="Şifre"
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
              Giriş Yap
            </Button>
          </Box>
        </Paper>
      </Box>
    </Container>
  );
}

