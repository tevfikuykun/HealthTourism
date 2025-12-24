// src/pages/Settings.jsx
import React, { useState } from 'react';
import {
  Container,
  Box,
  Typography,
  Grid,
  Card,
  CardContent,
  Switch,
  FormControlLabel,
  Button,
  Divider,
  List,
  ListItem,
  ListItemText,
  ListItemSecondaryAction,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  TextField,
  Alert,
  Snackbar,
} from '@mui/material';
import {
  Language as LanguageIcon,
  Notifications as NotificationsIcon,
  Security as SecurityIcon,
  Palette as PaletteIcon,
  Delete as DeleteIcon,
  Save as SaveIcon,
} from '@mui/icons-material';
import { useAuth } from '../hooks/useAuth';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import ProtectedRoute from '../components/ProtectedRoute';
import Loading from '../components/Loading';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import i18n from '../i18n';

export default function Settings() {
  const { t } = useTranslation();
  const { user, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });

  const [settings, setSettings] = useState({
    language: i18n.language || 'tr',
    theme: localStorage.getItem('theme') || 'light',
    emailNotifications: true,
    smsNotifications: false,
    pushNotifications: true,
    marketingEmails: false,
    twoFactorAuth: false,
    biometricAuth: false,
  });

  if (!isAuthenticated) {
    return (
      <ProtectedRoute>
        <div />
      </ProtectedRoute>
    );
  }

  const handleSettingChange = (key) => (event) => {
    const value = event.target.checked !== undefined ? event.target.checked : event.target.value;
    setSettings({ ...settings, [key]: value });
    
    // Apply changes immediately for some settings
    if (key === 'language') {
      i18n.changeLanguage(value);
      localStorage.setItem('i18nextLng', value);
    }
    if (key === 'theme') {
      localStorage.setItem('theme', value);
      window.dispatchEvent(new Event('storage'));
    }
  };

  const handleSave = () => {
    // In production, save to backend
    localStorage.setItem('userSettings', JSON.stringify(settings));
    setSnackbar({ open: true, message: t('settingsSaved', 'Ayarlar kaydedildi'), severity: 'success' });
  };

  const handleDeleteAccount = () => {
    if (window.confirm(t('confirmDeleteAccount', 'Hesabınızı silmek istediğinizden emin misiniz? Bu işlem geri alınamaz.'))) {
      // In production, call delete account API
      setSnackbar({ open: true, message: t('accountDeletionRequested', 'Hesap silme talebi gönderildi'), severity: 'info' });
    }
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" gutterBottom>
          {t('settings', 'Ayarlar')}
        </Typography>
        <Typography variant="body1" color="text.secondary">
          {t('manageSettings', 'Hesap ayarlarınızı yönetin')}
        </Typography>
      </Box>

      <Grid container spacing={3}>
        {/* Dil ve Bölge */}
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <LanguageIcon sx={{ mr: 1, color: 'primary.main' }} />
                <Typography variant="h6">{t('languageAndRegion', 'Dil ve Bölge')}</Typography>
              </Box>
              <Divider sx={{ mb: 2 }} />
              <FormControl fullWidth sx={{ mb: 2 }}>
                <InputLabel>{t('language', 'Dil')}</InputLabel>
                <Select
                  value={settings.language}
                  onChange={handleSettingChange('language')}
                  label={t('language', 'Dil')}
                >
                  <MenuItem value="tr">Türkçe</MenuItem>
                  <MenuItem value="en">English</MenuItem>
                  <MenuItem value="ru">Русский</MenuItem>
                  <MenuItem value="ar">العربية</MenuItem>
                  <MenuItem value="de">Deutsch</MenuItem>
                  <MenuItem value="fr">Français</MenuItem>
                  <MenuItem value="es">Español</MenuItem>
                </Select>
              </FormControl>
              <FormControl fullWidth>
                <InputLabel>{t('timezone', 'Saat Dilimi')}</InputLabel>
                <Select
                  value={Intl.DateTimeFormat().resolvedOptions().timeZone}
                  label={t('timezone', 'Saat Dilimi')}
                  disabled
                >
                  <MenuItem value={Intl.DateTimeFormat().resolvedOptions().timeZone}>
                    {Intl.DateTimeFormat().resolvedOptions().timeZone}
                  </MenuItem>
                </Select>
              </FormControl>
            </CardContent>
          </Card>
        </Grid>

        {/* Tema */}
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <PaletteIcon sx={{ mr: 1, color: 'primary.main' }} />
                <Typography variant="h6">{t('appearance', 'Görünüm')}</Typography>
              </Box>
              <Divider sx={{ mb: 2 }} />
              <FormControl fullWidth>
                <InputLabel>{t('theme', 'Tema')}</InputLabel>
                <Select
                  value={settings.theme}
                  onChange={handleSettingChange('theme')}
                  label={t('theme', 'Tema')}
                >
                  <MenuItem value="light">{t('light', 'Açık')}</MenuItem>
                  <MenuItem value="dark">{t('dark', 'Koyu')}</MenuItem>
                  <MenuItem value="auto">{t('auto', 'Otomatik')}</MenuItem>
                </Select>
              </FormControl>
            </CardContent>
          </Card>
        </Grid>

        {/* Bildirimler */}
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <NotificationsIcon sx={{ mr: 1, color: 'primary.main' }} />
                <Typography variant="h6">{t('notifications', 'Bildirimler')}</Typography>
              </Box>
              <Divider sx={{ mb: 2 }} />
              <List>
                <ListItem>
                  <ListItemText
                    primary={t('emailNotifications', 'E-posta Bildirimleri')}
                    secondary={t('emailNotificationsDesc', 'E-posta bildirimleri al')}
                  />
                  <ListItemSecondaryAction>
                    <Switch
                      checked={settings.emailNotifications}
                      onChange={handleSettingChange('emailNotifications')}
                    />
                  </ListItemSecondaryAction>
                </ListItem>
                <ListItem>
                  <ListItemText
                    primary={t('smsNotifications', 'SMS Bildirimleri')}
                    secondary={t('smsNotificationsDesc', 'SMS bildirimleri al')}
                  />
                  <ListItemSecondaryAction>
                    <Switch
                      checked={settings.smsNotifications}
                      onChange={handleSettingChange('smsNotifications')}
                    />
                  </ListItemSecondaryAction>
                </ListItem>
                <ListItem>
                  <ListItemText
                    primary={t('pushNotifications', 'Push Bildirimleri')}
                    secondary={t('pushNotificationsDesc', 'Tarayıcı push bildirimleri al')}
                  />
                  <ListItemSecondaryAction>
                    <Switch
                      checked={settings.pushNotifications}
                      onChange={handleSettingChange('pushNotifications')}
                    />
                  </ListItemSecondaryAction>
                </ListItem>
                <ListItem>
                  <ListItemText
                    primary={t('marketingEmails', 'Pazarlama E-postaları')}
                    secondary={t('marketingEmailsDesc', 'Kampanya ve teklif e-postaları al')}
                  />
                  <ListItemSecondaryAction>
                    <Switch
                      checked={settings.marketingEmails}
                      onChange={handleSettingChange('marketingEmails')}
                    />
                  </ListItemSecondaryAction>
                </ListItem>
              </List>
              <Button
                fullWidth
                variant="outlined"
                onClick={() => navigate('/notification-preferences')}
                sx={{ mt: 2 }}
              >
                {t('advancedSettings', 'Gelişmiş Ayarlar')}
              </Button>
            </CardContent>
          </Card>
        </Grid>

        {/* Güvenlik */}
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <SecurityIcon sx={{ mr: 1, color: 'primary.main' }} />
                <Typography variant="h6">{t('security', 'Güvenlik')}</Typography>
              </Box>
              <Divider sx={{ mb: 2 }} />
              <List>
                <ListItem>
                  <ListItemText
                    primary={t('twoFactorAuth', 'İki Faktörlü Kimlik Doğrulama')}
                    secondary={t('twoFactorAuthDesc', 'Hesabınızı ekstra güvenlik katmanı ile koruyun')}
                  />
                  <ListItemSecondaryAction>
                    <Button
                      variant="outlined"
                      size="small"
                      onClick={() => navigate('/security/2fa')}
                    >
                      {t('configure', 'Yapılandır')}
                    </Button>
                  </ListItemSecondaryAction>
                </ListItem>
                <ListItem>
                  <ListItemText
                    primary={t('biometricAuth', 'Biyometrik Kimlik Doğrulama')}
                    secondary={t('biometricAuthDesc', 'Parmak izi veya yüz tanıma ile giriş yapın')}
                  />
                  <ListItemSecondaryAction>
                    <Button
                      variant="outlined"
                      size="small"
                      onClick={() => navigate('/security/biometric')}
                    >
                      {t('configure', 'Yapılandır')}
                    </Button>
                  </ListItemSecondaryAction>
                </ListItem>
                <ListItem>
                  <ListItemText
                    primary={t('changePassword', 'Şifre Değiştir')}
                    secondary={t('changePasswordDesc', 'Düzenli olarak şifrenizi güncelleyin')}
                  />
                  <ListItemSecondaryAction>
                    <Button
                      variant="outlined"
                      size="small"
                      onClick={() => navigate('/reset-password')}
                    >
                      {t('change', 'Değiştir')}
                    </Button>
                  </ListItemSecondaryAction>
                </ListItem>
              </List>
            </CardContent>
          </Card>
        </Grid>

        {/* Hesap */}
        <Grid item xs={12}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom color="error">
                {t('dangerZone', 'Tehlikeli Bölge')}
              </Typography>
              <Divider sx={{ mb: 2 }} />
              <Alert severity="warning" sx={{ mb: 2 }}>
                {t('deleteAccountWarning', 'Hesabınızı silmek istediğinizde, tüm verileriniz kalıcı olarak silinecektir. Bu işlem geri alınamaz.')}
              </Alert>
              <Button
                variant="outlined"
                color="error"
                startIcon={<DeleteIcon />}
                onClick={handleDeleteAccount}
              >
                {t('deleteAccount', 'Hesabı Sil')}
              </Button>
            </CardContent>
          </Card>
        </Grid>

        {/* Kaydet Butonu */}
        <Grid item xs={12}>
          <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 2 }}>
            <Button variant="outlined" onClick={() => navigate('/dashboard')}>
              {t('cancel', 'İptal')}
            </Button>
            <Button variant="contained" startIcon={<SaveIcon />} onClick={handleSave}>
              {t('save', 'Kaydet')}
            </Button>
          </Box>
        </Grid>
      </Grid>

      <Snackbar
        open={snackbar.open}
        autoHideDuration={6000}
        onClose={() => setSnackbar({ ...snackbar, open: false })}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
      >
        <Alert severity={snackbar.severity} onClose={() => setSnackbar({ ...snackbar, open: false })}>
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Container>
  );
}


