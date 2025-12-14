// src/pages/BiometricAuth.jsx
import React, { useState } from 'react';
import {
  Container, Box, Typography, Paper, Button, Grid, Card, CardContent,
  Switch, FormControlLabel, Alert
} from '@mui/material';
import FingerprintIcon from '@mui/icons-material/Fingerprint';
import FaceIcon from '@mui/icons-material/Face';
import SecurityIcon from '@mui/icons-material/Security';
import { useTranslation } from 'react-i18next';

const BiometricAuth = () => {
  const { t } = useTranslation();
  const [fingerprintEnabled, setFingerprintEnabled] = useState(false);
  const [faceRecognitionEnabled, setFaceRecognitionEnabled] = useState(false);
  const [isSupported, setIsSupported] = useState(false);

  React.useEffect(() => {
    // Check if WebAuthn is supported
    if (window.PublicKeyCredential) {
      setIsSupported(true);
    }
  }, []);

  const handleFingerprintToggle = async () => {
    if (!fingerprintEnabled) {
      try {
        // Request fingerprint authentication
        const credential = await navigator.credentials.create({
          publicKey: {
            challenge: new Uint8Array(32),
            rp: { name: 'Health Tourism' },
            user: {
              id: new Uint8Array(16),
              name: 'user@example.com',
              displayName: 'User',
            },
            pubKeyCredParams: [{ alg: -7, type: 'public-key' }],
            authenticatorSelection: {
              authenticatorAttachment: 'platform',
              userVerification: 'required',
            },
          },
        });
        setFingerprintEnabled(true);
      } catch (error) {
        console.error('Fingerprint registration failed:', error);
      }
    } else {
      setFingerprintEnabled(false);
    }
  };

  const handleFaceRecognitionToggle = async () => {
    if (!faceRecognitionEnabled) {
      try {
        // Similar to fingerprint but for face recognition
        const credential = await navigator.credentials.create({
          publicKey: {
            challenge: new Uint8Array(32),
            rp: { name: 'Health Tourism' },
            user: {
              id: new Uint8Array(16),
              name: 'user@example.com',
              displayName: 'User',
            },
            pubKeyCredParams: [{ alg: -7, type: 'public-key' }],
            authenticatorSelection: {
              authenticatorAttachment: 'platform',
              userVerification: 'required',
            },
          },
        });
        setFaceRecognitionEnabled(true);
      } catch (error) {
        console.error('Face recognition registration failed:', error);
      }
    } else {
      setFaceRecognitionEnabled(false);
    }
  };

  return (
    <Container maxWidth="md" sx={{ py: 4 }}>
      <Typography variant="h4" gutterBottom>
        {t('biometricAuthentication', 'Biyometrik Kimlik Doğrulama')}
      </Typography>

      {!isSupported && (
        <Alert severity="warning" sx={{ mb: 3 }}>
          {t('biometricNotSupported', 'Bu tarayıcı biyometrik kimlik doğrulamayı desteklemiyor.')}
        </Alert>
      )}

      <Grid container spacing={3}>
        <Grid item xs={12} sm={6}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 2 }}>
                <FingerprintIcon sx={{ fontSize: 40, color: 'primary.main' }} />
                <Box>
                  <Typography variant="h6">{t('fingerprint', 'Parmak İzi')}</Typography>
                  <Typography variant="body2" color="text.secondary">
                    {t('loginWithFingerprint', 'Parmak izinizle giriş yapın')}
                  </Typography>
                </Box>
              </Box>
              <FormControlLabel
                control={
                  <Switch
                    checked={fingerprintEnabled}
                    onChange={handleFingerprintToggle}
                    disabled={!isSupported}
                  />
                }
                label={fingerprintEnabled ? t('active', 'Aktif') : t('inactive', 'Pasif')}
              />
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 2 }}>
                <FaceIcon sx={{ fontSize: 40, color: 'primary.main' }} />
                <Box>
                  <Typography variant="h6">{t('faceRecognition', 'Yüz Tanıma')}</Typography>
                  <Typography variant="body2" color="text.secondary">
                    {t('loginWithFace', 'Yüzünüzle giriş yapın')}
                  </Typography>
                </Box>
              </Box>
              <FormControlLabel
                control={
                  <Switch
                    checked={faceRecognitionEnabled}
                    onChange={handleFaceRecognitionToggle}
                    disabled={!isSupported}
                  />
                }
                label={faceRecognitionEnabled ? t('active', 'Aktif') : t('inactive', 'Pasif')}
              />
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {(fingerprintEnabled || faceRecognitionEnabled) && (
        <Alert severity="success" sx={{ mt: 3 }}>
          {t('biometricEnabledSuccess', 'Biyometrik kimlik doğrulama başarıyla etkinleştirildi.')}
        </Alert>
      )}
    </Container>
  );
};

export default BiometricAuth;

