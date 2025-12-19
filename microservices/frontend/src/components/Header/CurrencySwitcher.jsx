import React, { useState, useEffect } from 'react';
import {
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Chip,
  Box,
  Tooltip,
} from '@mui/material';
import {
  CurrencyExchange,
  AccountBalance,
} from '@mui/icons-material';
import { useTranslation } from '../../i18n';
import { useQuery } from '@tanstack/react-query';
import api from '../../services/api';

/**
 * Currency Switcher Component
 * USD, EUR, TRY, HealthToken seçimi
 * AI Cost Predictor verilerini seçilen birimde gösterir
 */
const CurrencySwitcher = ({ onCurrencyChange }) => {
  const { t } = useTranslation();
  const [selectedCurrency, setSelectedCurrency] = useState(
    localStorage.getItem('selectedCurrency') || 'USD'
  );

  // Fetch exchange rates
  const { data: exchangeRates } = useQuery({
    queryKey: ['exchange-rates'],
    queryFn: async () => {
      const response = await api.get('/api/currency/rates');
      return response.data;
    },
    refetchInterval: 60000, // 1 dakikada bir güncelle
  });

  useEffect(() => {
    localStorage.setItem('selectedCurrency', selectedCurrency);
    if (onCurrencyChange) {
      onCurrencyChange(selectedCurrency);
    }
  }, [selectedCurrency, onCurrencyChange]);

  const currencies = [
    { code: 'USD', symbol: '$', name: 'US Dollar' },
    { code: 'EUR', symbol: '€', name: 'Euro' },
    { code: 'TRY', symbol: '₺', name: 'Turkish Lira' },
    { code: 'HT', symbol: 'HT', name: 'Health Token', isCrypto: true },
  ];

  const getExchangeRate = (currency) => {
    if (currency === 'USD') return 1;
    if (!exchangeRates) return null;
    return exchangeRates[currency] || null;
  };

  return (
    <Tooltip title={t('header.selectCurrency')}>
      <FormControl size="small" sx={{ minWidth: 100 }}>
        <Select
          value={selectedCurrency}
          onChange={(e) => setSelectedCurrency(e.target.value)}
          sx={{ height: 32 }}
          renderValue={(value) => {
            const currency = currencies.find((c) => c.code === value);
            return (
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                <CurrencyExchange fontSize="small" />
                <span>{currency?.symbol || value}</span>
              </Box>
            );
          }}
        >
          {currencies.map((currency) => (
            <MenuItem key={currency.code} value={currency.code}>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, width: '100%' }}>
                <AccountBalance fontSize="small" />
                <Box sx={{ flex: 1 }}>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                    <span>{currency.symbol}</span>
                    <span>{currency.code}</span>
                    {currency.isCrypto && (
                      <Chip label="Crypto" size="small" color="primary" />
                    )}
                  </Box>
                  <Box sx={{ fontSize: '0.75rem', color: 'text.secondary' }}>
                    {currency.name}
                  </Box>
                </Box>
                {getExchangeRate(currency.code) && (
                  <Box sx={{ fontSize: '0.75rem', color: 'text.secondary' }}>
                    1 USD = {getExchangeRate(currency.code).toFixed(2)} {currency.code}
                  </Box>
                )}
              </Box>
            </MenuItem>
          ))}
        </Select>
      </FormControl>
    </Tooltip>
  );
};

export default CurrencySwitcher;


