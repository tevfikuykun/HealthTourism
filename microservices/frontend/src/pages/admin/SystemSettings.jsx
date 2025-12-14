// src/pages/admin/SystemSettings.jsx
import React, { useState } from 'react';
import {
  Container, Box, Typography, Paper, Grid, TextField, Button,
  Switch, FormControlLabel, Divider, Tabs, Tab, Alert
} from '@mui/material';
import SaveIcon from '@mui/icons-material/Save';
import SettingsIcon from '@mui/icons-material/Settings';
import { useTranslation } from 'react-i18next';

const SystemSettings = () => {
  const { t } = useTranslation();
  const [activeTab, setActiveTab] = useState(0);
  const [settings, setSettings] = useState({
    siteName: 'Health Tourism',
    siteEmail: 'info@healthtourism.com',
    maintenanceMode: false,
    registrationEnabled: true,
    emailNotifications: true,
    smsNotifications: false,
  });

  const handleSave = () => {
    // Ayarları kaydet
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 4 }}>
        <SettingsIcon sx={{ fontSize: 40, color: 'primary.main' }} />
        <Typography variant="h4">{t('systemSettings', 'Sistem Ayarları')}</Typography>
      </Box>

      <Paper>
        <Tabs value={activeTab} onChange={(e, v) => setActiveTab(v)}>
          <Tab label="Genel Ayarlar" />
          <Tab label="E-posta Ayarları" />
          <Tab label="Bildirim Ayarları" />
          <Tab label="Bakım Modu" />
        </Tabs>

        <Box sx={{ p: 3 }}>
          {activeTab === 0 && (
            <Grid container spacing={3}>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Site Adı"
                  value={settings.siteName}
                  onChange={(e) => setSettings({ ...settings, siteName: e.target.value })}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Site E-posta"
                  type="email"
                  value={settings.siteEmail}
                  onChange={(e) => setSettings({ ...settings, siteEmail: e.target.value })}
                />
              </Grid>
              <Grid item xs={12}>
                <FormControlLabel
                  control={
                    <Switch
                      checked={settings.registrationEnabled}
                      onChange={(e) => setSettings({ ...settings, registrationEnabled: e.target.checked })}
                    />
                  }
                  label="Kayıt Açık"
                />
              </Grid>
            </Grid>
          )}

          {activeTab === 1 && (
            <Grid container spacing={3}>
              <Grid item xs={12}>
                <TextField fullWidth label="SMTP Sunucu" />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField fullWidth label="SMTP Port" type="number" />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField fullWidth label="SMTP Kullanıcı Adı" />
              </Grid>
              <Grid item xs={12}>
                <TextField fullWidth label="SMTP Şifre" type="password" />
              </Grid>
            </Grid>
          )}

          {activeTab === 2 && (
            <Grid container spacing={3}>
              <Grid item xs={12}>
                <FormControlLabel
                  control={
                    <Switch
                      checked={settings.emailNotifications}
                      onChange={(e) => setSettings({ ...settings, emailNotifications: e.target.checked })}
                    />
                  }
                  label="E-posta Bildirimleri"
                />
              </Grid>
              <Grid item xs={12}>
                <FormControlLabel
                  control={
                    <Switch
                      checked={settings.smsNotifications}
                      onChange={(e) => setSettings({ ...settings, smsNotifications: e.target.checked })}
                    />
                  }
                  label="SMS Bildirimleri"
                />
              </Grid>
            </Grid>
          )}

          {activeTab === 3 && (
            <Box>
              <Alert severity="warning" sx={{ mb: 2 }}>
                Bakım modu aktif olduğunda site kullanıcılara kapalı olacaktır.
              </Alert>
              <FormControlLabel
                control={
                  <Switch
                    checked={settings.maintenanceMode}
                    onChange={(e) => setSettings({ ...settings, maintenanceMode: e.target.checked })}
                  />
                }
                label="Bakım Modu"
              />
            </Box>
          )}

          <Divider sx={{ my: 3 }} />
          <Button
            variant="contained"
            startIcon={<SaveIcon />}
            onClick={handleSave}
          >
            Ayarları Kaydet
          </Button>
        </Box>
      </Paper>
    </Container>
  );
};

export default SystemSettings;

