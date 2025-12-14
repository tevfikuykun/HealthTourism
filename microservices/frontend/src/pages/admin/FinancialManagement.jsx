// src/pages/admin/FinancialManagement.jsx
import React, { useState } from 'react';
import {
  Container, Box, Typography, Grid, Card, CardContent, Paper,
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
  TextField, Button, Select, MenuItem, FormControl, InputLabel
} from '@mui/material';
import AttachMoneyIcon from '@mui/icons-material/AttachMoney';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';
import ReceiptIcon from '@mui/icons-material/Receipt';
import LineChart from '../../components/Charts/LineChart';
import { useTranslation } from 'react-i18next';

const FinancialManagement = () => {
  const { t } = useTranslation();
  const [payments] = useState([
    {
      id: 1,
      user: 'Ahmet Yılmaz',
      amount: 5000,
      method: 'Kredi Kartı',
      date: '2024-01-15',
      status: 'completed',
    },
  ]);

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4" gutterBottom>
        {t('financialManagement', 'Finansal Yönetim')}
      </Typography>

      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                <AttachMoneyIcon sx={{ fontSize: 40, color: 'success.main' }} />
                <Box>
                  <Typography variant="h5">₺125,000</Typography>
                  <Typography variant="body2" color="text.secondary">
                    Toplam Gelir
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                <TrendingUpIcon sx={{ fontSize: 40, color: 'primary.main' }} />
                <Box>
                  <Typography variant="h5">₺45,000</Typography>
                  <Typography variant="body2" color="text.secondary">
                    Bu Ay
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                <ReceiptIcon sx={{ fontSize: 40, color: 'warning.main' }} />
                <Box>
                  <Typography variant="h5">₺5,000</Typography>
                  <Typography variant="body2" color="text.secondary">
                    Bekleyen
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                <AttachMoneyIcon sx={{ fontSize: 40, color: 'error.main' }} />
                <Box>
                  <Typography variant="h5">₺2,500</Typography>
                  <Typography variant="body2" color="text.secondary">
                    İadeler
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      <Paper sx={{ p: 3, mb: 3 }}>
        <Typography variant="h6" gutterBottom>
          Gelir Trendi
        </Typography>
        <LineChart
          data={[
            { name: 'Ocak', gelir: 40000 },
            { name: 'Şubat', gelir: 35000 },
            { name: 'Mart', gelir: 45000 },
            { name: 'Nisan', gelir: 50000 },
          ]}
          dataKey="gelir"
          name="Gelir (₺)"
          color="#4caf50"
        />
      </Paper>

      <Paper>
        <Box sx={{ p: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Typography variant="h6">Ödeme İşlemleri</Typography>
          <Box sx={{ display: 'flex', gap: 2 }}>
            <TextField
              size="small"
              label="Tarih Aralığı"
              type="date"
              InputLabelProps={{ shrink: true }}
            />
            <Button variant="outlined">Filtrele</Button>
          </Box>
        </Box>
        <TableContainer>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>ID</TableCell>
                <TableCell>Kullanıcı</TableCell>
                <TableCell>Tutar</TableCell>
                <TableCell>Yöntem</TableCell>
                <TableCell>Tarih</TableCell>
                <TableCell>Durum</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {payments.map((payment) => (
                <TableRow key={payment.id}>
                  <TableCell>{payment.id}</TableCell>
                  <TableCell>{payment.user}</TableCell>
                  <TableCell>₺{payment.amount.toLocaleString()}</TableCell>
                  <TableCell>{payment.method}</TableCell>
                  <TableCell>{payment.date}</TableCell>
                  <TableCell>
                    {payment.status === 'completed' ? 'Tamamlandı' : 'Bekliyor'}
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </Paper>
    </Container>
  );
};

export default FinancialManagement;

