// src/pages/InstallmentPlans.jsx
import React, { useState } from 'react';
import {
  Container, Box, Typography, Grid, Card, CardContent, Button,
  Radio, RadioGroup, FormControlLabel, FormControl, Chip, Alert
} from '@mui/material';
import CreditCardIcon from '@mui/icons-material/CreditCard';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import { useTranslation } from 'react-i18next';

const InstallmentPlans = () => {
  const { t } = useTranslation();
  const [selectedPlan, setSelectedPlan] = useState('');
  const [totalAmount] = useState(10000);

  const plans = [
    {
      id: 'single',
      name: 'Tek Ödeme',
      installments: 1,
      monthlyPayment: totalAmount,
      total: totalAmount,
      interest: 0,
      savings: 0,
    },
    {
      id: '3',
      name: '3 Taksit',
      installments: 3,
      monthlyPayment: Math.ceil(totalAmount / 3),
      total: totalAmount,
      interest: 0,
      savings: 0,
    },
    {
      id: '6',
      name: '6 Taksit',
      installments: 6,
      monthlyPayment: Math.ceil(totalAmount / 6),
      total: totalAmount * 1.05,
      interest: 5,
      savings: 0,
    },
    {
      id: '12',
      name: '12 Taksit',
      installments: 12,
      monthlyPayment: Math.ceil((totalAmount * 1.1) / 12),
      total: totalAmount * 1.1,
      interest: 10,
      savings: 0,
    },
  ];

  return (
    <Container maxWidth="md" sx={{ py: 4 }}>
      <Typography variant="h4" gutterBottom>
        {t('installmentPlans', 'Taksit Planları')}
      </Typography>
      <Typography variant="body1" color="text.secondary" sx={{ mb: 4 }}>
        {t('totalAmount')}: ₺{totalAmount.toLocaleString()}
      </Typography>

      <FormControl component="fieldset" fullWidth>
        <RadioGroup
          value={selectedPlan}
          onChange={(e) => setSelectedPlan(e.target.value)}
        >
          <Grid container spacing={2}>
            {plans.map((plan) => (
              <Grid item xs={12} sm={6} key={plan.id}>
                <Card
                  sx={{
                    border: selectedPlan === plan.id ? 2 : 1,
                    borderColor: selectedPlan === plan.id ? 'primary.main' : 'divider',
                    cursor: 'pointer',
                  }}
                  onClick={() => setSelectedPlan(plan.id)}
                >
                  <CardContent>
                    <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', mb: 2 }}>
                      <FormControlLabel
                        value={plan.id}
                        control={<Radio />}
                        label={plan.name}
                        sx={{ m: 0 }}
                      />
                      {plan.interest === 0 && (
                        <Chip label={t('interestFree', 'Faizsiz')} color="success" size="small" />
                      )}
                    </Box>
                    <Box sx={{ pl: 4 }}>
                      <Typography variant="h6" color="primary">
                        ₺{plan.monthlyPayment.toLocaleString()}/{t('month', 'ay')}
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        {plan.installments} {t('installments', 'taksit')}
                      </Typography>
                      {plan.interest > 0 && (
                        <Typography variant="body2" color="error">
                          {t('total')}: ₺{plan.total.toLocaleString()} (%{plan.interest} {t('interest', 'faiz')})
                        </Typography>
                      )}
                      {plan.interest === 0 && (
                        <Typography variant="body2" color="success.main">
                          {t('total')}: ₺{plan.total.toLocaleString()} ({t('interestFree', 'Faizsiz')})
                        </Typography>
                      )}
                    </Box>
                  </CardContent>
                </Card>
              </Grid>
            ))}
          </Grid>
        </RadioGroup>
      </FormControl>

      {selectedPlan && (
        <Box sx={{ mt: 3 }}>
          <Alert severity="info" sx={{ mb: 2 }}>
            {t('selectedPlan', 'Seçilen plan')}: {plans.find(p => p.id === selectedPlan)?.name}
          </Alert>
          <Button
            variant="contained"
            fullWidth
            size="large"
            startIcon={<CreditCardIcon />}
          >
            {t('continueToPayment', 'Ödemeye Devam Et')}
          </Button>
        </Box>
      )}
    </Container>
  );
};

export default InstallmentPlans;

