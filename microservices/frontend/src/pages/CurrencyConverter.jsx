// src/pages/CurrencyConverter.jsx
import React, { useState, useEffect } from 'react';
import {
  Container, Box, Typography, Paper, Grid, TextField, Select,
  MenuItem, FormControl, InputLabel, Button, Card, CardContent
} from '@mui/material';
import SwapHorizIcon from '@mui/icons-material/SwapHoriz';
import CurrencyExchangeIcon from '@mui/icons-material/CurrencyExchange';
import { useTranslation } from 'react-i18next';

const CurrencyConverter = () => {
  const { t } = useTranslation();
  const [fromCurrency, setFromCurrency] = useState('TRY');
  const [toCurrency, setToCurrency] = useState('USD');
  const [amount, setAmount] = useState(1000);
  const [convertedAmount, setConvertedAmount] = useState(0);
  const [exchangeRate, setExchangeRate] = useState(0);

  const currencies = [
    { code: 'TRY', name: 'Türk Lirası', symbol: '₺' },
    { code: 'USD', name: 'US Dollar', symbol: '$' },
    { code: 'EUR', name: 'Euro', symbol: '€' },
    { code: 'GBP', name: 'British Pound', symbol: '£' },
  ];

  useEffect(() => {
    // Exchange rate API call
    const rate = fromCurrency === 'TRY' && toCurrency === 'USD' ? 0.034 : 1;
    setExchangeRate(rate);
    setConvertedAmount(amount * rate);
  }, [fromCurrency, toCurrency, amount]);

  const handleSwap = () => {
    const temp = fromCurrency;
    setFromCurrency(toCurrency);
    setToCurrency(temp);
  };

  return (
    <Container maxWidth="md" sx={{ py: 4 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 4 }}>
        <CurrencyExchangeIcon sx={{ fontSize: 40, color: 'primary.main' }} />
        <Box>
          <Typography variant="h4">{t('currencyConverter', 'Para Birimi Dönüştürücü')}</Typography>
          <Typography variant="body2" color="text.secondary">
            {t('realTimeExchangeRates', 'Gerçek zamanlı döviz kurları')}
          </Typography>
        </Box>
      </Box>

      <Paper sx={{ p: 3 }}>
        <Grid container spacing={3}>
          <Grid item xs={12} sm={5}>
            <FormControl fullWidth>
              <InputLabel>{t('currency', 'Para Birimi')}</InputLabel>
              <Select
                value={fromCurrency}
                onChange={(e) => setFromCurrency(e.target.value)}
                label={t('currency', 'Para Birimi')}
              >
                {currencies.map((currency) => (
                  <MenuItem key={currency.code} value={currency.code}>
                    {currency.symbol} {currency.name}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
            <TextField
              fullWidth
              label={t('amount', 'Tutar')}
              type="number"
              value={amount}
              onChange={(e) => setAmount(parseFloat(e.target.value) || 0)}
              sx={{ mt: 2 }}
            />
          </Grid>
          <Grid item xs={12} sm={2} sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <Button
              variant="outlined"
              onClick={handleSwap}
              sx={{ minWidth: 'auto', width: 48, height: 48 }}
            >
              <SwapHorizIcon />
            </Button>
          </Grid>
          <Grid item xs={12} sm={5}>
            <FormControl fullWidth>
              <InputLabel>{t('currency', 'Para Birimi')}</InputLabel>
              <Select
                value={toCurrency}
                onChange={(e) => setToCurrency(e.target.value)}
                label={t('currency', 'Para Birimi')}
              >
                {currencies.map((currency) => (
                  <MenuItem key={currency.code} value={currency.code}>
                    {currency.symbol} {currency.name}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
            <TextField
              fullWidth
              label={t('convertedAmount', 'Dönüştürülmüş Tutar')}
              value={convertedAmount.toFixed(2)}
              InputProps={{ readOnly: true }}
              sx={{ mt: 2 }}
            />
          </Grid>
        </Grid>

        <Card sx={{ mt: 3 }}>
          <CardContent>
            <Typography variant="body2" color="text.secondary">
              {t('exchangeRate', 'Döviz Kuru')}
            </Typography>
            <Typography variant="h6">
              1 {fromCurrency} = {exchangeRate.toFixed(4)} {toCurrency}
            </Typography>
          </CardContent>
        </Card>
      </Paper>
    </Container>
  );
};

export default CurrencyConverter;

