// src/pages/NotificationPreferences.jsx
import React, { useState } from 'react';
import {
  Container, Box, Typography, Paper, Grid, Switch, FormControlLabel,
  Divider, Button, Alert
} from '@mui/material';
import NotificationsIcon from '@mui/icons-material/Notifications';
import EmailIcon from '@mui/icons-material/Email';
import SmsIcon from '@mui/icons-material/Sms';
import SaveIcon from '@mui/icons-material/Save';
import { useTranslation } from 'react-i18next';

const NotificationPreferences = () => {
  const { t } = useTranslation();
  const [preferences, setPreferences] = useState({
    email: {
      reservations: true,
      payments: true,
      reminders: true,
      promotions: false,
    },
    sms: {
      reservations: false,
      payments: true,
      reminders: true,
      promotions: false,
    },
    push: {
      reservations: true,
      payments: true,
      reminders: true,
      promotions: true,
    },
  });

  const handleSave = () => {
    // Tercihleri kaydet
  };

  return (
    <Container maxWidth="md" sx={{ py: 4 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 4 }}>
        <NotificationsIcon sx={{ fontSize: 40, color: 'primary.main' }} />
        <Box>
          <Typography variant="h4">{t('notificationPreferences', 'Bildirim Tercihleri')}</Typography>
          <Typography variant="body2" color="text.secondary">
            {t('selectNotificationsDescription', 'Hangi bildirimleri almak istediğinizi seçin')}
          </Typography>
        </Box>
      </Box>

      <Paper sx={{ p: 3, mb: 3 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 3 }}>
          <EmailIcon sx={{ fontSize: 32, color: 'primary.main' }} />
          <Typography variant="h6">{t('emailNotifications', 'E-posta Bildirimleri')}</Typography>
        </Box>
        <Grid container spacing={2}>
          <Grid item xs={12}>
            <FormControlLabel
              control={
                <Switch
                  checked={preferences.email.reservations}
                  onChange={(e) => setPreferences({
                    ...preferences,
                    email: { ...preferences.email, reservations: e.target.checked }
                  })}
                />
              }
              label={t('reservationNotifications', 'Rezervasyon Bildirimleri')}
            />
          </Grid>
          <Grid item xs={12}>
            <FormControlLabel
              control={
                <Switch
                  checked={preferences.email.payments}
                  onChange={(e) => setPreferences({
                    ...preferences,
                    email: { ...preferences.email, payments: e.target.checked }
                  })}
                />
              }
              label={t('paymentNotifications', 'Ödeme Bildirimleri')}
            />
          </Grid>
          <Grid item xs={12}>
            <FormControlLabel
              control={
                <Switch
                  checked={preferences.email.reminders}
                  onChange={(e) => setPreferences({
                    ...preferences,
                    email: { ...preferences.email, reminders: e.target.checked }
                  })}
                />
              }
              label={t('reminders', 'Hatırlatmalar')}
            />
          </Grid>
          <Grid item xs={12}>
            <FormControlLabel
              control={
                <Switch
                  checked={preferences.email.promotions}
                  onChange={(e) => setPreferences({
                    ...preferences,
                    email: { ...preferences.email, promotions: e.target.checked }
                  })}
                />
              }
              label={t('promotions', 'Promosyonlar')}
            />
          </Grid>
        </Grid>
      </Paper>

      <Paper sx={{ p: 3, mb: 3 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 3 }}>
          <SmsIcon sx={{ fontSize: 32, color: 'primary.main' }} />
          <Typography variant="h6">{t('smsNotifications', 'SMS Bildirimleri')}</Typography>
        </Box>
        <Grid container spacing={2}>
          <Grid item xs={12}>
            <FormControlLabel
              control={
                <Switch
                  checked={preferences.sms.reservations}
                  onChange={(e) => setPreferences({
                    ...preferences,
                    sms: { ...preferences.sms, reservations: e.target.checked }
                  })}
                />
              }
              label={t('reservationNotifications', 'Rezervasyon Bildirimleri')}
            />
          </Grid>
          <Grid item xs={12}>
            <FormControlLabel
              control={
                <Switch
                  checked={preferences.sms.payments}
                  onChange={(e) => setPreferences({
                    ...preferences,
                    sms: { ...preferences.sms, payments: e.target.checked }
                  })}
                />
              }
              label={t('paymentNotifications', 'Ödeme Bildirimleri')}
            />
          </Grid>
        </Grid>
      </Paper>

      <Paper sx={{ p: 3 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 3 }}>
          <NotificationsIcon sx={{ fontSize: 32, color: 'primary.main' }} />
          <Typography variant="h6">{t('pushNotifications', 'Push Bildirimleri')}</Typography>
        </Box>
        <Grid container spacing={2}>
          <Grid item xs={12}>
            <FormControlLabel
              control={
                <Switch
                  checked={preferences.push.reservations}
                  onChange={(e) => setPreferences({
                    ...preferences,
                    push: { ...preferences.push, reservations: e.target.checked }
                  })}
                />
              }
              label={t('reservationNotifications', 'Rezervasyon Bildirimleri')}
            />
          </Grid>
          <Grid item xs={12}>
            <FormControlLabel
              control={
                <Switch
                  checked={preferences.push.payments}
                  onChange={(e) => setPreferences({
                    ...preferences,
                    push: { ...preferences.push, payments: e.target.checked }
                  })}
                />
              }
              label={t('paymentNotifications', 'Ödeme Bildirimleri')}
            />
          </Grid>
        </Grid>
      </Paper>

      <Box sx={{ mt: 3, display: 'flex', justifyContent: 'flex-end' }}>
        <Button
          variant="contained"
          startIcon={<SaveIcon />}
          onClick={handleSave}
        >
          {t('savePreferences', 'Tercihleri Kaydet')}
        </Button>
      </Box>
    </Container>
  );
};

export default NotificationPreferences;

