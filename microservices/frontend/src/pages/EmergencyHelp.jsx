import React, { useState, useEffect } from 'react';
import {
  Box,
  Container,
  Typography,
  Card,
  CardContent,
  Button,
  Alert,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  CircularProgress,
  Fab,
} from '@mui/material';
import {
  Emergency,
  Phone,
  LocationOn,
  Send,
  Cancel,
  CheckCircle,
  Warning,
  LocalHospital,
  Person,
} from '@mui/icons-material';
import { useTranslation } from 'react-i18next';
import api from '../services/api';

/**
 * Emergency Help - Panik Butonu
 * Yabancı ülkede olan hasta için tek tuşla koordinat gönderen,
 * hastaneye ve refakatçıye bildirim atan acil durum sayfası
 */
const EmergencyHelp = () => {
  const { t } = useTranslation();
  const [emergencyActive, setEmergencyActive] = useState(false);
  const [location, setLocation] = useState(null);
  const [sending, setSending] = useState(false);
  const [sent, setSent] = useState(false);
  const [openConfirm, setOpenConfirm] = useState(false);

  useEffect(() => {
    // Get current location
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          setLocation({
            latitude: position.coords.latitude,
            longitude: position.coords.longitude,
          });
        },
        (error) => {
          console.error('Location error:', error);
        }
      );
    }
  }, []);

  const handleEmergencyPress = () => {
    setOpenConfirm(true);
  };

  const handleConfirmEmergency = async () => {
    setOpenConfirm(false);
    setSending(true);
    setEmergencyActive(true);

    try {
      // Send emergency alert
      const response = await api.post('/api/emergency/alert', {
        location,
        timestamp: new Date().toISOString(),
        type: 'MEDICAL_EMERGENCY',
      });

      if (response.data.success) {
        setSent(true);
        
        // Auto-call hospital
        setTimeout(() => {
          window.location.href = `tel:${response.data.hospitalPhone}`;
        }, 2000);
      }
    } catch (error) {
      console.error('Emergency alert failed:', error);
    } finally {
      setSending(false);
    }
  };

  const handleCancelEmergency = () => {
    setOpenConfirm(false);
  };

  const resetEmergency = () => {
    setEmergencyActive(false);
    setSent(false);
    setSending(false);
  };

  return (
    <Container maxWidth="sm" sx={{ py: 4, textAlign: 'center' }}>
      <Typography variant="h4" component="h1" gutterBottom color="error">
        {t('emergency.title')}
      </Typography>
      <Typography variant="body1" color="text.secondary" paragraph>
        {t('emergency.subtitle')}
      </Typography>

      {!sent && !emergencyActive && (
        <>
          <Card sx={{ mb: 4, bgcolor: 'error.light' }}>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                {t('emergency.warning')}
              </Typography>
              <Typography variant="body2">
                {t('emergency.warningText')}
              </Typography>
            </CardContent>
          </Card>

          {/* Emergency Button */}
          <Box sx={{ position: 'relative', display: 'inline-block', mb: 4 }}>
            <Fab
              color="error"
              size="large"
              sx={{
                width: 200,
                height: 200,
                fontSize: '1.5rem',
                '&:hover': {
                  bgcolor: 'error.dark',
                  transform: 'scale(1.1)',
                },
                transition: 'all 0.3s',
              }}
              onClick={handleEmergencyPress}
            >
              <Emergency sx={{ fontSize: 80 }} />
            </Fab>
            <Typography variant="h6" sx={{ mt: 2 }}>
              {t('emergency.pressButton')}
            </Typography>
          </Box>
        </>
      )}

      {sending && (
        <Box sx={{ textAlign: 'center', py: 4 }}>
          <CircularProgress size={60} />
          <Typography variant="h6" sx={{ mt: 2 }}>
            {t('emergency.sending')}
          </Typography>
        </Box>
      )}

      {sent && (
        <>
          <Alert severity="success" sx={{ mb: 3 }} icon={<CheckCircle />}>
            <Typography variant="h6">{t('emergency.sent')}</Typography>
            <Typography variant="body2">
              {t('emergency.sentMessage')}
            </Typography>
          </Alert>

          <Card sx={{ mb: 3 }}>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                {t('emergency.actionsTaken')}
              </Typography>
              <List>
                <ListItem>
                  <ListItemIcon>
                    <LocationOn color="primary" />
                  </ListItemIcon>
                  <ListItemText
                    primary={t('emergency.locationSent')}
                    secondary={
                      location
                        ? `${location.latitude.toFixed(4)}, ${location.longitude.toFixed(4)}`
                        : t('emergency.locationUnavailable')
                    }
                  />
                </ListItem>
                <ListItem>
                  <ListItemIcon>
                    <LocalHospital color="primary" />
                  </ListItemIcon>
                  <ListItemText
                    primary={t('emergency.hospitalNotified')}
                    secondary={t('emergency.hospitalNotifiedDesc')}
                  />
                </ListItem>
                <ListItem>
                  <ListItemIcon>
                    <Person color="primary" />
                  </ListItemIcon>
                  <ListItemText
                    primary={t('emergency.companionNotified')}
                    secondary={t('emergency.companionNotifiedDesc')}
                  />
                </ListItem>
              </List>
            </CardContent>
          </Card>

          <Button
            variant="contained"
            color="primary"
            size="large"
            onClick={resetEmergency}
            sx={{ mt: 2 }}
          >
            {t('emergency.reset')}
          </Button>
        </>
      )}

      {/* Quick Contacts */}
      <Card sx={{ mt: 4 }}>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            {t('emergency.quickContacts')}
          </Typography>
          <List>
            <ListItem>
              <ListItemIcon>
                <Phone color="primary" />
              </ListItemIcon>
              <ListItemText
                primary={t('emergency.hospitalPhone')}
                secondary="+90 212 XXX XX XX"
              />
              <Button
                variant="outlined"
                size="small"
                href="tel:+90212XXXXXXX"
              >
                {t('emergency.call')}
              </Button>
            </ListItem>
            <ListItem>
              <ListItemIcon>
                <Phone color="primary" />
              </ListItemIcon>
              <ListItemText
                primary={t('emergency.emergencyLine')}
                secondary="112"
              />
              <Button
                variant="outlined"
                size="small"
                href="tel:112"
                color="error"
              >
                {t('emergency.call')}
              </Button>
            </ListItem>
          </List>
        </CardContent>
      </Card>

      {/* Confirmation Dialog */}
      <Dialog open={openConfirm} onClose={handleCancelEmergency}>
        <DialogTitle>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            <Warning color="error" />
            {t('emergency.confirmTitle')}
          </Box>
        </DialogTitle>
        <DialogContent>
          <Typography>{t('emergency.confirmMessage')}</Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCancelEmergency} startIcon={<Cancel />}>
            {t('common.cancel')}
          </Button>
          <Button
            onClick={handleConfirmEmergency}
            variant="contained"
            color="error"
            startIcon={<Send />}
          >
            {t('emergency.confirm')}
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default EmergencyHelp;



