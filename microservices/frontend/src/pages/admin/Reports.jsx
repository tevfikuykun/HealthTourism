// src/pages/admin/Reports.jsx
import React, { useState } from 'react';
import {
  Container, Box, Typography, Paper, Grid, Button, TextField,
  Select, MenuItem, FormControl, InputLabel, Tabs, Tab
} from '@mui/material';
import PictureAsPdfIcon from '@mui/icons-material/PictureAsPdf';
import TableChartIcon from '@mui/icons-material/TableChart';
import DateRangeIcon from '@mui/icons-material/DateRange';
import { useTranslation } from 'react-i18next';

const Reports = () => {
  const { t } = useTranslation();
  const [activeTab, setActiveTab] = useState(0);
  const [reportType, setReportType] = useState('');
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');

  const handleGeneratePDF = () => {
    // PDF oluşturma
  };

  const handleGenerateExcel = () => {
    // Excel oluşturma
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4" gutterBottom>
        {t('reports', 'Raporlar')}
      </Typography>

      <Paper sx={{ p: 3, mb: 3 }}>
        <Grid container spacing={2}>
          <Grid item xs={12} sm={4}>
            <FormControl fullWidth>
              <InputLabel>Rapor Tipi</InputLabel>
              <Select
                value={reportType}
                onChange={(e) => setReportType(e.target.value)}
                label="Rapor Tipi"
              >
                <MenuItem value="users">Kullanıcı Raporu</MenuItem>
                <MenuItem value="reservations">Rezervasyon Raporu</MenuItem>
                <MenuItem value="revenue">Gelir Raporu</MenuItem>
                <MenuItem value="services">Hizmet Raporu</MenuItem>
              </Select>
            </FormControl>
          </Grid>
          <Grid item xs={12} sm={4}>
            <TextField
              fullWidth
              label="Başlangıç Tarihi"
              type="date"
              value={startDate}
              onChange={(e) => setStartDate(e.target.value)}
              InputLabelProps={{ shrink: true }}
            />
          </Grid>
          <Grid item xs={12} sm={4}>
            <TextField
              fullWidth
              label="Bitiş Tarihi"
              type="date"
              value={endDate}
              onChange={(e) => setEndDate(e.target.value)}
              InputLabelProps={{ shrink: true }}
            />
          </Grid>
        </Grid>
        <Box sx={{ mt: 3, display: 'flex', gap: 2 }}>
          <Button
            variant="contained"
            startIcon={<PictureAsPdfIcon />}
            onClick={handleGeneratePDF}
          >
            PDF Oluştur
          </Button>
          <Button
            variant="contained"
            color="success"
            startIcon={<TableChartIcon />}
            onClick={handleGenerateExcel}
          >
            Excel Oluştur
          </Button>
        </Box>
      </Paper>

      <Tabs value={activeTab} onChange={(e, v) => setActiveTab(v)}>
        <Tab label="Hazır Raporlar" />
        <Tab label="Özel Raporlar" />
        <Tab label="Zamanlanmış Raporlar" />
      </Tabs>

      <Box sx={{ mt: 3 }}>
        {activeTab === 0 && (
          <Grid container spacing={2}>
            <Grid item xs={12} sm={6} md={4}>
              <Paper sx={{ p: 2, textAlign: 'center' }}>
                <DateRangeIcon sx={{ fontSize: 48, color: 'primary.main', mb: 1 }} />
                <Typography variant="h6">Günlük Rapor</Typography>
                <Button variant="outlined" sx={{ mt: 1 }}>Görüntüle</Button>
              </Paper>
            </Grid>
            <Grid item xs={12} sm={6} md={4}>
              <Paper sx={{ p: 2, textAlign: 'center' }}>
                <DateRangeIcon sx={{ fontSize: 48, color: 'primary.main', mb: 1 }} />
                <Typography variant="h6">Haftalık Rapor</Typography>
                <Button variant="outlined" sx={{ mt: 1 }}>Görüntüle</Button>
              </Paper>
            </Grid>
            <Grid item xs={12} sm={6} md={4}>
              <Paper sx={{ p: 2, textAlign: 'center' }}>
                <DateRangeIcon sx={{ fontSize: 48, color: 'primary.main', mb: 1 }} />
                <Typography variant="h6">Aylık Rapor</Typography>
                <Button variant="outlined" sx={{ mt: 1 }}>Görüntüle</Button>
              </Paper>
            </Grid>
          </Grid>
        )}
      </Box>
    </Container>
  );
};

export default Reports;

