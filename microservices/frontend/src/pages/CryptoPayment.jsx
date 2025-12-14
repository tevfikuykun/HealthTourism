// src/pages/CryptoPayment.jsx
import React, { useState } from 'react';
import {
  Container, Box, Typography, Grid, Card, CardContent, Button,
  TextField, Alert, Chip
} from '@mui/material';
import CurrencyBitcoinIcon from '@mui/icons-material/CurrencyBitcoin';
import ContentCopyIcon from '@mui/icons-material/ContentCopy';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import { useTranslation } from 'react-i18next';

const CryptoPayment = () => {
  const { t } = useTranslation();
  const [selectedCrypto, setSelectedCrypto] = useState('bitcoin');
  const [amount] = useState(10000);
  const [cryptoAmount, setCryptoAmount] = useState(0.25);

  const cryptocurrencies = [
    {
      id: 'bitcoin',
      name: 'Bitcoin',
      symbol: 'BTC',
      icon: <CurrencyBitcoinIcon />,
      address: '1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa',
      rate: 40000,
    },
    {
      id: 'ethereum',
      name: 'Ethereum',
      symbol: 'ETH',
      icon: <CurrencyBitcoinIcon />,
      address: '0x742d35Cc6634C0532925a3b844Bc9e7595f0bEb',
      rate: 2500,
    },
  ];

  const selected = cryptocurrencies.find(c => c.id === selectedCrypto);

  return (
    <Container maxWidth="md" sx={{ py: 4 }}>
      <Typography variant="h4" gutterBottom>
        {t('cryptoPayment', 'Kripto Para ile Ödeme')}
      </Typography>
      <Alert severity="info" sx={{ mb: 3 }}>
        {t('cryptoPaymentDescription', 'Kripto para ödemeleri anında işleme alınır ve güvenlidir.')}
      </Alert>

      <Grid container spacing={3}>
        <Grid item xs={12}>
          <Typography variant="h6" gutterBottom>
            {t('selectCryptocurrency', 'Kripto Para Seçin')}
          </Typography>
          <Grid container spacing={2}>
            {cryptocurrencies.map((crypto) => (
              <Grid item xs={12} sm={6} key={crypto.id}>
                <Card
                  sx={{
                    border: selectedCrypto === crypto.id ? 2 : 1,
                    borderColor: selectedCrypto === crypto.id ? 'primary.main' : 'divider',
                    cursor: 'pointer',
                  }}
                  onClick={() => setSelectedCrypto(crypto.id)}
                >
                  <CardContent>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                      {crypto.icon}
                      <Box>
                        <Typography variant="h6">{crypto.name}</Typography>
                        <Typography variant="body2" color="text.secondary">
                          {crypto.symbol}
                        </Typography>
                      </Box>
                    </Box>
                  </CardContent>
                </Card>
              </Grid>
            ))}
          </Grid>
        </Grid>

        {selected && (
          <>
            <Grid item xs={12}>
              <Card>
                <CardContent>
                  <Typography variant="h6" gutterBottom>
                    {t('paymentDetails', 'Ödeme Detayları')}
                  </Typography>
                  <Box sx={{ mb: 2 }}>
                    <Typography variant="body2" color="text.secondary">
                      {t('amountToPay', 'Ödenecek Tutar')} (TRY)
                    </Typography>
                    <Typography variant="h5">₺{amount.toLocaleString()}</Typography>
                  </Box>
                  <Box sx={{ mb: 2 }}>
                    <Typography variant="body2" color="text.secondary">
                      {selected.symbol} {t('amount', 'Miktarı')}
                    </Typography>
                    <Typography variant="h5">
                      {cryptoAmount} {selected.symbol}
                    </Typography>
                    <Typography variant="caption" color="text.secondary">
                      (1 {selected.symbol} = ${selected.rate.toLocaleString()})
                    </Typography>
                  </Box>
                </CardContent>
              </Card>
            </Grid>

            <Grid item xs={12}>
              <Card>
                <CardContent>
                  <Typography variant="h6" gutterBottom>
                    {t('walletAddress', 'Cüzdan Adresi')}
                  </Typography>
                  <Box sx={{ display: 'flex', gap: 2, mb: 2 }}>
                    <TextField
                      fullWidth
                      value={selected.address}
                      InputProps={{ readOnly: true }}
                      sx={{ fontFamily: 'monospace' }}
                    />
                    <Button
                      variant="outlined"
                      startIcon={<ContentCopyIcon />}
                      onClick={() => navigator.clipboard.writeText(selected.address)}
                    >
                      {t('copy', 'Kopyala')}
                    </Button>
                  </Box>
                  <Box sx={{ textAlign: 'center', mt: 2 }}>
                    {/* QR Code will be generated using a library like qrcode.react */}
                    <Box sx={{ 
                      width: 200, 
                      height: 200, 
                      bgcolor: 'grey.100', 
                      display: 'flex', 
                      alignItems: 'center', 
                      justifyContent: 'center',
                      mx: 'auto',
                      borderRadius: 1
                    }}>
                      <Typography variant="caption">{t('qrCode', 'QR Code')}</Typography>
                    </Box>
                  </Box>
                  <Alert severity="warning" sx={{ mt: 2 }}>
                    {t('cryptoPaymentWarning', 'Sadece {symbol} gönderin. Diğer kripto paralar kaybolabilir.', { symbol: selected.symbol })}
                  </Alert>
                </CardContent>
              </Card>
            </Grid>

            <Grid item xs={12}>
              <Button
                variant="contained"
                fullWidth
                size="large"
                startIcon={<CheckCircleIcon />}
              >
                {t('paymentCompleted', 'Ödemeyi Tamamladım')}
              </Button>
            </Grid>
          </>
        )}
      </Grid>
    </Container>
  );
};

export default CryptoPayment;

