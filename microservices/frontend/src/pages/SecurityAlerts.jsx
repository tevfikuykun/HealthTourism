// src/pages/SecurityAlerts.jsx
import React, { useState } from 'react';
import {
  Container, Box, Typography, Card, CardContent, List, ListItem,
  ListItemText, ListItemIcon, Chip, Switch, FormControlLabel, Paper
} from '@mui/material';
import SecurityIcon from '@mui/icons-material/Security';
import WarningIcon from '@mui/icons-material/Warning';
import EmailIcon from '@mui/icons-material/Email';
import SmsIcon from '@mui/icons-material/Sms';
import NotificationsIcon from '@mui/icons-material/Notifications';
import { useTranslation } from 'react-i18next';

const SecurityAlerts = () => {
  const { t } = useTranslation();
  const [alerts, setAlerts] = useState([
    {
      id: 1,
      type: 'login',
      message: 'Yeni bir cihazdan giriş yapıldı',
      date: '2024-01-15 10:30',
      location: 'İstanbul, Türkiye',
      device: 'Chrome - Windows',
      severity: 'warning',
    },
    {
      id: 2,
      type: 'password',
      message: 'Şifre değiştirildi',
      date: '2024-01-10 14:20',
      severity: 'info',
    },
    {
      id: 3,
      type: 'payment',
      message: 'Yeni ödeme işlemi',
      date: '2024-01-08 09:15',
      amount: 5000,
      severity: 'success',
    },
  ]);

  const [notifications, setNotifications] = useState({
    email: true,
    sms: false,
    push: true,
  });

  const getSeverityColor = (severity) => {
    switch (severity) {
      case 'warning':
        return 'warning';
      case 'error':
        return 'error';
      case 'success':
        return 'success';
      default:
        return 'info';
    }
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4" gutterBottom>
        {t('securityAlerts', 'Güvenlik Bildirimleri')}
      </Typography>

      <Paper sx={{ p: 3, mb: 3 }}>
        <Typography variant="h6" gutterBottom>
          {t('notificationPreferences', 'Bildirim Tercihleri')}
        </Typography>
        <Box>
          <FormControlLabel
            control={
              <Switch
                checked={notifications.email}
                onChange={(e) => setNotifications({ ...notifications, email: e.target.checked })}
              />
            }
            label={
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <EmailIcon />
                <Typography>{t('emailNotifications', 'E-posta Bildirimleri')}</Typography>
              </Box>
            }
          />
        </Box>
        <Box>
          <FormControlLabel
            control={
              <Switch
                checked={notifications.sms}
                onChange={(e) => setNotifications({ ...notifications, sms: e.target.checked })}
              />
            }
            label={
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <SmsIcon />
                <Typography>{t('smsNotifications', 'SMS Bildirimleri')}</Typography>
              </Box>
            }
          />
        </Box>
        <Box>
          <FormControlLabel
            control={
              <Switch
                checked={notifications.push}
                onChange={(e) => setNotifications({ ...notifications, push: e.target.checked })}
              />
            }
            label={
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <NotificationsIcon />
                <Typography>{t('pushNotifications', 'Push Bildirimleri')}</Typography>
              </Box>
            }
          />
        </Box>
      </Paper>

      <Card>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            {t('recentSecurityEvents', 'Son Güvenlik Olayları')}
          </Typography>
          <List>
            {alerts.map((alert) => (
              <ListItem key={alert.id}>
                <ListItemIcon>
                  <SecurityIcon color={getSeverityColor(alert.severity)} />
                </ListItemIcon>
                <ListItemText
                  primary={
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                      <Typography variant="body1">{alert.message}</Typography>
                      <Chip
                        label={alert.severity}
                        color={getSeverityColor(alert.severity)}
                        size="small"
                      />
                    </Box>
                  }
                  secondary={
                    <Box>
                      <Typography variant="body2" color="text.secondary">
                        {alert.date}
                      </Typography>
                      {alert.location && (
                        <Typography variant="body2" color="text.secondary">
                          {t('location')}: {alert.location}
                        </Typography>
                      )}
                      {alert.device && (
                        <Typography variant="body2" color="text.secondary">
                          {t('device', 'Cihaz')}: {alert.device}
                        </Typography>
                      )}
                    </Box>
                  }
                />
              </ListItem>
            ))}
          </List>
        </CardContent>
      </Card>
    </Container>
  );
};

export default SecurityAlerts;

