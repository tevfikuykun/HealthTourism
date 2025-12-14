import React, { useState } from 'react';
import {
  Container,
  Box,
  Typography,
  Grid,
  TextField,
  Button,
  Paper,
  Alert,
} from '@mui/material';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { contactService } from '../services/api';
import SendIcon from '@mui/icons-material/Send';
import Loading from '../components/Loading';
import { useTranslation } from 'react-i18next';

const getContactSchema = (t) => yup.object().shape({
  name: yup.string().required(t('nameRequired', 'Ad soyad gereklidir')),
  email: yup.string().email(t('validEmail', 'Geçerli bir e-posta adresi girin')).required(t('emailRequired', 'E-posta adresi gereklidir')),
  phone: yup.string().required(t('phoneRequired', 'Telefon numarası gereklidir')),
  subject: yup.string().required(t('subjectRequired', 'Konu gereklidir')),
  message: yup.string().required(t('messageRequired', 'Mesaj gereklidir')).min(10, t('messageMinLength', 'Mesaj en az 10 karakter olmalıdır')),
});

export default function Contact() {
  const { t } = useTranslation();
  const [isLoading, setIsLoading] = useState(false);
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState('');

  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm({
    resolver: yupResolver(getContactSchema(t)),
  });

  const onSubmit = async (data) => {
    try {
      setIsLoading(true);
      setError('');
      setSuccess(false);

      await contactService.send(data);
      setSuccess(true);
      reset();
    } catch (err) {
      setError(err.response?.data?.message || t('messageSendError', 'Mesaj gönderilirken bir hata oluştu'));
    } finally {
      setIsLoading(false);
    }
  };

  if (isLoading) {
    return <Loading message={t('sendingMessage', 'Mesaj gönderiliyor...')} />;
  }

  return (
    <Container maxWidth="md" sx={{ py: 4 }}>
      <Typography variant="h4" align="center" gutterBottom>
        {t('contact')}
      </Typography>
      <Typography variant="body1" align="center" color="text.secondary" sx={{ mb: 4 }}>
        {t('contactDescription', 'Sorularınız, önerileriniz veya şikayetleriniz için bizimle iletişime geçebilirsiniz.')}
      </Typography>

      <Paper sx={{ p: 4 }}>
        {error && (
          <Alert severity="error" sx={{ mb: 3 }}>
            {error}
          </Alert>
        )}

        {success && (
          <Alert severity="success" sx={{ mb: 3 }}>
            {t('messageSentSuccess', 'Mesajınız başarıyla gönderildi! En kısa sürede size dönüş yapacağız.')}
          </Alert>
        )}

        <Box component="form" onSubmit={handleSubmit(onSubmit)}>
          <Grid container spacing={3}>
            <Grid item xs={12} sm={6}>
              <TextField
                {...register('name')}
                fullWidth
                label={t('fullName', 'Ad Soyad')}
                error={!!errors.name}
                helperText={errors.name?.message}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                {...register('email')}
                fullWidth
                label={t('email')}
                type="email"
                error={!!errors.email}
                helperText={errors.email?.message}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                {...register('phone')}
                fullWidth
                label={t('phone')}
                error={!!errors.phone}
                helperText={errors.phone?.message}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                {...register('subject')}
                fullWidth
                label={t('subject', 'Konu')}
                error={!!errors.subject}
                helperText={errors.subject?.message}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                {...register('message')}
                fullWidth
                multiline
                rows={6}
                label={t('yourMessage', 'Mesajınız')}
                error={!!errors.message}
                helperText={errors.message?.message}
              />
            </Grid>
            <Grid item xs={12}>
              <Button type="submit" variant="contained" size="large" startIcon={<SendIcon />} fullWidth>
                {t('send')}
              </Button>
            </Grid>
          </Grid>
        </Box>

        <Box sx={{ mt: 4, pt: 4, borderTop: 1, borderColor: 'divider' }}>
          <Typography variant="h6" gutterBottom>
            {t('contactInformation', 'İletişim Bilgileri')}
          </Typography>
          <Typography variant="body2" color="text.secondary">
            {t('email')}: info@healthtourism.com
          </Typography>
          <Typography variant="body2" color="text.secondary">
            {t('phone')}: +90 (212) 123 45 67
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Adres: İstanbul, Türkiye
          </Typography>
        </Box>
      </Paper>
    </Container>
  );
}
