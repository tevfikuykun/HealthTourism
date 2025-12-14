// src/pages/PaymentHistory.jsx
import React, { useState } from 'react';
import {
  Container, Box, Typography, Table, TableBody, TableCell,
  TableContainer, TableHead, TableRow, Paper, Chip, Button,
  TextField, Grid, Card, CardContent
} from '@mui/material';
import DownloadIcon from '@mui/icons-material/Download';
import ReceiptIcon from '@mui/icons-material/Receipt';
import AttachMoneyIcon from '@mui/icons-material/AttachMoney';
import { useTranslation } from 'react-i18next';

const PaymentHistory = () => {
  const { t } = useTranslation();
  const [payments, setPayments] = useState([
    {
      id: 1,
      date: '2024-01-15',
      amount: 5000,
      method: 'Kredi Kartı',
      status: 'completed',
      description: 'Kardiyoloji Konsültasyonu',
      invoice: true,
    },
    {
      id: 2,
      date: '2024-01-10',
      amount: 12000,
      method: 'Banka Havalesi',
      status: 'pending',
      description: 'Paket Rezervasyon',
      invoice: false,
    },
  ]);

  const totalPaid = payments
    .filter(p => p.status === 'completed')
    .reduce((sum, p) => sum + p.amount, 0);

  const totalPending = payments
    .filter(p => p.status === 'pending')
    .reduce((sum, p) => sum + p.amount, 0);

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4" gutterBottom>
        {t('paymentHistory', 'Ödeme Geçmişi')}
      </Typography>

      <Grid container spacing={3} sx={{ mb: 3 }}>
        <Grid item xs={12} sm={4}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                <AttachMoneyIcon sx={{ fontSize: 40, color: 'success.main' }} />
                <Box>
                  <Typography variant="h5">₺{totalPaid.toLocaleString()}</Typography>
                  <Typography variant="body2" color="text.secondary">
                    {t('totalPaid', 'Toplam Ödenen')}
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={4}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                <AttachMoneyIcon sx={{ fontSize: 40, color: 'warning.main' }} />
                <Box>
                  <Typography variant="h5">₺{totalPending.toLocaleString()}</Typography>
                  <Typography variant="body2" color="text.secondary">
                    {t('pendingPayment', 'Bekleyen Ödeme')}
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={4}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                <ReceiptIcon sx={{ fontSize: 40, color: 'primary.main' }} />
                <Box>
                  <Typography variant="h5">{payments.length}</Typography>
                  <Typography variant="body2" color="text.secondary">
                    {t('totalTransactions', 'Toplam İşlem')}
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      <Box sx={{ mb: 2, display: 'flex', gap: 2 }}>
        <TextField
          label={t('dateRange', 'Tarih Aralığı')}
          type="date"
          InputLabelProps={{ shrink: true }}
          size="small"
        />
        <TextField
          label={t('status', 'Durum')}
          select
          SelectProps={{ native: true }}
          size="small"
        >
          <option value="">{t('all', 'Tümü')}</option>
          <option value="completed">{t('completed', 'Tamamlandı')}</option>
          <option value="pending">{t('pending', 'Bekliyor')}</option>
          <option value="failed">{t('failed', 'Başarısız')}</option>
        </TextField>
      </Box>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>{t('date', 'Tarih')}</TableCell>
              <TableCell>{t('description', 'Açıklama')}</TableCell>
              <TableCell>{t('amount', 'Tutar')}</TableCell>
              <TableCell>{t('paymentMethod', 'Ödeme Yöntemi')}</TableCell>
              <TableCell>{t('status', 'Durum')}</TableCell>
              <TableCell>{t('actions', 'İşlemler')}</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {payments.map((payment) => (
              <TableRow key={payment.id}>
                <TableCell>{payment.date}</TableCell>
                <TableCell>{payment.description}</TableCell>
                <TableCell>₺{payment.amount.toLocaleString()}</TableCell>
                <TableCell>{payment.method}</TableCell>
                <TableCell>
                  <Chip
                    label={
                      payment.status === 'completed' ? t('completed', 'Tamamlandı') :
                      payment.status === 'pending' ? t('pending', 'Bekliyor') : t('failed', 'Başarısız')
                    }
                    color={
                      payment.status === 'completed' ? 'success' :
                      payment.status === 'pending' ? 'warning' : 'error'
                    }
                    size="small"
                  />
                </TableCell>
                <TableCell>
                  {payment.invoice && (
                    <Button
                      size="small"
                      startIcon={<DownloadIcon />}
                    >
                      {t('invoice', 'Fatura')}
                    </Button>
                  )}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </Container>
  );
};

export default PaymentHistory;

