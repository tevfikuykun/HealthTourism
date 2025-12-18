import React from 'react';
import { Select, MenuItem, FormControl, InputLabel } from '@mui/material';
import { useTranslation } from '../i18n';
import { setCurrency, getCurrentCurrency } from '../utils/currency';

const CurrencySelector = () => {
  const { t } = useTranslation();
  const [selectedCurrency, setSelectedCurrency] = React.useState(getCurrentCurrency());

  const currencies = [
    { code: 'TRY', name: 'Türk Lirası (₺)' },
    { code: 'USD', name: 'US Dollar ($)' },
    { code: 'EUR', name: 'Euro (€)' },
    { code: 'GBP', name: 'British Pound (£)' },
  ];

  const handleChange = (event) => {
    const newCurrency = event.target.value;
    setSelectedCurrency(newCurrency);
    setCurrency(newCurrency);
    window.location.reload(); // Reload to apply currency changes
  };

  return (
    <FormControl size="small" sx={{ minWidth: 150 }}>
      <InputLabel>{t('currency', 'Para Birimi')}</InputLabel>
      <Select
        value={selectedCurrency}
        onChange={handleChange}
        label={t('currency', 'Para Birimi')}
      >
        {currencies.map((currency) => (
          <MenuItem key={currency.code} value={currency.code}>
            {currency.name}
          </MenuItem>
        ))}
      </Select>
    </FormControl>
  );
};

export default CurrencySelector;
