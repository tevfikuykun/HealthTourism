// src/pages/TwoFactorAuth.jsx
import React, { useState } from 'react';
import {
  Container, Box, Typography, Paper, TextField, Button, Grid,
  Card, CardContent, Switch, FormControlLabel, QRCode, Alert
} from '@mui/material';
import SecurityIcon from '@mui/icons-material/Security';
import QrCodeIcon from '@mui/icons-material/QrCode';
import PhoneAndroidIcon from '@mui/icons-material/PhoneAndroid';
import EmailIcon from '@mui/icons-material/Email';
import { useTranslation } from 'react-i18next';

const TwoFactorAuth = () => {
  const { t } = useTranslation();
  const [enabled, setEnabled] = useState(false);
  const [method, setMethod] = useState('app'); // 'app' or 'sms' or 'email'
  const [verificationCode, setVerificationCode] = useState('');
  const [qrCode, setQrCode] = useState('');

  const handleEnable = () => {
    if (method === 'app') {
      // QR kod oluştur
      setQrCode('otpauth://totp/HealthTourism:user@example.com?secret=JBSWY3DPEHPK3PXP&issuer=HealthTourism');
    }
    setEnabled(true);
  };

  const handleVerify = () => {
    // Doğrulama kodu kontrolü
    if (verificationCode.length === 6) {
      setEnabled(true);
    }
  };

  return (
    <Container maxWidth="md" sx={{ py: 4 }}>
      <Typography variant="h4" gutterBottom>
        İki Faktörlü Kimlik Doğrulama (2FA)
      </Typography>

      <Paper sx={{ p: 3, mt: 3 }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
          <Box>
            <Typography variant="h6">2FA Durumu</Typography>
            <Typography variant="body2" color="text.secondary">
              {enabled ? 'Aktif' : 'Pasif'}
            </Typography>
          </Box>
          <FormControlLabel
            control={
              <Switch
                checked={enabled}
                onChange={(e) => setEnabled(e.target.checked)}
              />
            }
            label={enabled ? 'Aktif' : 'Pasif'}
          />
        </Box>

        {!enabled && (
          <>
            <Typography variant="h6" gutterBottom sx={{ mt: 3 }}>
              Doğrulama Yöntemi Seçin
            </Typography>
            <Grid container spacing={2} sx={{ mt: 1 }}>
              <Grid item xs={12} sm={4}>
                <Card
                  sx={{
                    cursor: 'pointer',
                    border: method === 'app' ? 2 : 1,
                    borderColor: method === 'app' ? 'primary.main' : 'divider',
                  }}
                  onClick={() => setMethod('app')}
                >
                  <CardContent>
                    <Box sx={{ textAlign: 'center' }}>
                      <PhoneAndroidIcon sx={{ fontSize: 48, color: 'primary.main', mb: 1 }} />
                      <Typography variant="h6">Authenticator App</Typography>
                      <Typography variant="body2" color="text.secondary">
                        Google Authenticator, Microsoft Authenticator
                      </Typography>
                    </Box>
                  </CardContent>
                </Card>
              </Grid>
              <Grid item xs={12} sm={4}>
                <Card
                  sx={{
                    cursor: 'pointer',
                    border: method === 'sms' ? 2 : 1,
                    borderColor: method === 'sms' ? 'primary.main' : 'divider',
                  }}
                  onClick={() => setMethod('sms')}
                >
                  <CardContent>
                    <Box sx={{ textAlign: 'center' }}>
                      <PhoneAndroidIcon sx={{ fontSize: 48, color: 'primary.main', mb: 1 }} />
                      <Typography variant="h6">SMS</Typography>
                      <Typography variant="body2" color="text.secondary">
                        Telefon numaranıza kod gönderilir
                      </Typography>
                    </Box>
                  </CardContent>
                </Card>
              </Grid>
              <Grid item xs={12} sm={4}>
                <Card
                  sx={{
                    cursor: 'pointer',
                    border: method === 'email' ? 2 : 1,
                    borderColor: method === 'email' ? 'primary.main' : 'divider',
                  }}
                  onClick={() => setMethod('email')}
                >
                  <CardContent>
                    <Box sx={{ textAlign: 'center' }}>
                      <EmailIcon sx={{ fontSize: 48, color: 'primary.main', mb: 1 }} />
                      <Typography variant="h6">E-posta</Typography>
                      <Typography variant="body2" color="text.secondary">
                        E-posta adresinize kod gönderilir
                      </Typography>
                    </Box>
                  </CardContent>
                </Card>
              </Grid>
            </Grid>

            {method === 'app' && (
              <Box sx={{ mt: 3, textAlign: 'center' }}>
                <Button
                  variant="contained"
                  startIcon={<QrCodeIcon />}
                  onClick={handleEnable}
                >
                  QR Kod Oluştur
                </Button>
                {qrCode && (
                  <Box sx={{ mt: 3 }}>
                    <Alert severity="info" sx={{ mb: 2 }}>
                      Authenticator uygulamanızla QR kodu tarayın
                    </Alert>
                    <Box sx={{ display: 'flex', justifyContent: 'center' }}>
                      <img
                        src={`https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=${encodeURIComponent(qrCode)}`}
                        alt="QR Code"
                      />
                    </Box>
                    <TextField
                      fullWidth
                      label="Doğrulama Kodu"
                      value={verificationCode}
                      onChange={(e) => setVerificationCode(e.target.value)}
                      sx={{ mt: 2, maxWidth: 300, mx: 'auto' }}
                      inputProps={{ maxLength: 6 }}
                    />
                    <Button
                      variant="contained"
                      fullWidth
                      sx={{ mt: 2, maxWidth: 300, mx: 'auto' }}
                      onClick={handleVerify}
                      disabled={verificationCode.length !== 6}
                    >
                      Doğrula ve Etkinleştir
                    </Button>
                  </Box>
                )}
              </Box>
            )}

            {(method === 'sms' || method === 'email') && (
              <Box sx={{ mt: 3 }}>
                <TextField
                  fullWidth
                  label={method === 'sms' ? 'Telefon Numarası' : 'E-posta Adresi'}
                  sx={{ mb: 2 }}
                />
                <Button variant="contained" onClick={handleEnable}>
                  Kod Gönder
                </Button>
                {enabled && (
                  <Box sx={{ mt: 2 }}>
                    <TextField
                      fullWidth
                      label="Doğrulama Kodu"
                      value={verificationCode}
                      onChange={(e) => setVerificationCode(e.target.value)}
                      sx={{ mb: 2 }}
                    />
                    <Button variant="contained" onClick={handleVerify}>
                      Doğrula
                    </Button>
                  </Box>
                )}
              </Box>
            )}
          </>
        )}

        {enabled && (
          <Alert severity="success" sx={{ mt: 2 }}>
            2FA başarıyla etkinleştirildi. Artık giriş yaparken doğrulama kodu gerekecek.
          </Alert>
        )}
      </Paper>
    </Container>
  );
};

export default TwoFactorAuth;

