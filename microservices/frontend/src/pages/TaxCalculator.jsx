// src/pages/TaxCalculator.jsx
import React, { useState } from 'react';
import {
  Container, Box, Typography, Paper, Grid, TextField, Select,
  MenuItem, FormControl, InputLabel, Card, CardContent, Divider
} from '@mui/material';
import CalculateIcon from '@mui/icons-material/Calculate';
import ReceiptIcon from '@mui/icons-material/Receipt';
import { useTranslation } from 'react-i18next';

const TaxCalculator = () => {
  const { t } = useTranslation();
  const [amount, setAmount] = useState(1000);
  const [taxRate, setTaxRate] = useState(20); // KDV %20
  const [includeTax, setIncludeTax] = useState(false);

  const taxAmount = includeTax
    ? (amount * taxRate) / (100 + taxRate)
    : (amount * taxRate) / 100;

  const totalAmount = includeTax ? amount : amount + taxAmount;
  const baseAmount = includeTax ? amount - taxAmount : amount;

  return (
    <Container maxWidth="md" sx={{ py: 4 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 4 }}>
        <CalculateIcon sx={{ fontSize: 40, color: 'primary.main' }} />
        <Box>
          <Typography variant="h4">{t('taxCalculator', 'Vergi Hesaplayıcı')}</Typography>
          <Typography variant="body2" color="text.secondary">
            {t('taxCalculatorDescription', 'KDV ve diğer vergileri hesaplayın')}
          </Typography>
        </Box>
      </Box>

      <Paper sx={{ p: 3 }}>
        <Grid container spacing={3}>
          <Grid item xs={12} sm={6}>
            <TextField
              fullWidth
              label={t('amount')}
              type="number"
              value={amount}
              onChange={(e) => setAmount(parseFloat(e.target.value) || 0)}
            />
          </Grid>
          <Grid item xs={12} sm={6}>
            <FormControl fullWidth>
              <InputLabel>{t('taxRate', 'Vergi Oranı')}</InputLabel>
              <Select
                value={taxRate}
                onChange={(e) => setTaxRate(e.target.value)}
                label={t('taxRate', 'Vergi Oranı')}
              >
                <MenuItem value={1}>%1 ({t('vatExemption', 'KDV İstisnası')})</MenuItem>
                <MenuItem value={10}>%10 ({t('vat', 'KDV')})</MenuItem>
                <MenuItem value={20}>%20 ({t('vat', 'KDV')})</MenuItem>
              </Select>
            </FormControl>
          </Grid>
          <Grid item xs={12}>
            <FormControl fullWidth>
              <InputLabel>{t('taxStatus', 'Vergi Durumu')}</InputLabel>
              <Select
                value={includeTax ? 'included' : 'excluded'}
                onChange={(e) => setIncludeTax(e.target.value === 'included')}
                label={t('taxStatus', 'Vergi Durumu')}
              >
                <MenuItem value="excluded">{t('taxExcluded', 'Vergi Hariç')}</MenuItem>
                <MenuItem value="included">{t('taxIncluded', 'Vergi Dahil')}</MenuItem>
              </Select>
            </FormControl>
          </Grid>
        </Grid>

        <Divider sx={{ my: 3 }} />

        <Card>
          <CardContent>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
              <Typography variant="body1">{t('baseAmount', 'Ana Tutar')}:</Typography>
              <Typography variant="h6">₺{baseAmount.toFixed(2)}</Typography>
            </Box>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
              <Typography variant="body1">{t('tax', 'Vergi')} (%{taxRate}):</Typography>
              <Typography variant="h6" color="error">
                ₺{taxAmount.toFixed(2)}
              </Typography>
            </Box>
            <Divider sx={{ my: 2 }} />
            <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
              <Typography variant="h6">{t('total')}:</Typography>
              <Typography variant="h5" color="primary">
                ₺{totalAmount.toFixed(2)}
              </Typography>
            </Box>
          </CardContent>
        </Card>
      </Paper>
    </Container>
  );
};

export default TaxCalculator;

