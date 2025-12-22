import React, { useState } from 'react';
import {
  Box,
  Container,
  Typography,
  Grid,
  Card,
  CardContent,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Chip,
  Button,
  Tabs,
  Tab,
  LinearProgress,
  Alert,
} from '@mui/material';
import {
  Business,
  TrendingUp,
  Api,
  AccountBalance,
  People,
  Assessment,
  Settings,
} from '@mui/icons-material';
import { useTranslation } from 'react-i18next';
import { useQuery } from '@tanstack/react-query';
import api from '../services/api';

/**
 * B2B / Partner Portal
 * Acentelerin kendi satışlarını ve API Key kullanımlarını
 * gördüğü basitleştirilmiş dashboard
 */
const PartnerPortal = () => {
  const { t } = useTranslation();
  const [selectedTab, setSelectedTab] = useState(0);

  // Fetch partner data
  const { data: partnerData, isLoading } = useQuery({
    queryKey: ['partner-data'],
    queryFn: async () => {
      const response = await api.get('/partners/me');
      return response.data;
    },
  });

  // Fetch API usage
  const { data: apiUsage } = useQuery({
    queryKey: ['api-usage'],
    queryFn: async () => {
      const response = await api.get('/api-keys/usage');
      return response.data;
    },
    refetchInterval: 60000,
  });

  // Fetch sales data
  const { data: salesData } = useQuery({
    queryKey: ['partner-sales'],
    queryFn: async () => {
      const response = await api.get('/partners/me/sales');
      return response.data;
    },
  });

  if (isLoading) {
    return (
      <Box sx={{ p: 3 }}>
        <LinearProgress />
      </Box>
    );
  }

  return (
    <Container maxWidth="xl" sx={{ py: 4 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        {t('partner.title')}
      </Typography>
      <Typography variant="body1" color="text.secondary" paragraph>
        {t('partner.subtitle')}
      </Typography>

      {/* Statistics Cards */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <Box>
                  <Typography color="text.secondary" gutterBottom>
                    {t('partner.totalSales')}
                  </Typography>
                  <Typography variant="h4">
                    {salesData?.totalSales || 0}
                  </Typography>
                </Box>
                <TrendingUp color="primary" sx={{ fontSize: 40 }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <Box>
                  <Typography color="text.secondary" gutterBottom>
                    {t('partner.apiCalls')}
                  </Typography>
                  <Typography variant="h4">
                    {apiUsage?.totalCalls || 0}
                  </Typography>
                </Box>
                <Api color="primary" sx={{ fontSize: 40 }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <Box>
                  <Typography color="text.secondary" gutterBottom>
                    {t('partner.revenue')}
                  </Typography>
                  <Typography variant="h4">
                    ${salesData?.totalRevenue?.toLocaleString() || 0}
                  </Typography>
                </Box>
                <AccountBalance color="primary" sx={{ fontSize: 40 }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <Box>
                  <Typography color="text.secondary" gutterBottom>
                    {t('partner.activeClients')}
                  </Typography>
                  <Typography variant="h4">
                    {partnerData?.activeClients || 0}
                  </Typography>
                </Box>
                <People color="primary" sx={{ fontSize: 40 }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Tabs */}
      <Box sx={{ borderBottom: 1, borderColor: 'divider', mb: 3 }}>
        <Tabs value={selectedTab} onChange={(e, v) => setSelectedTab(v)}>
          <Tab label={t('partner.sales')} />
          <Tab label={t('partner.apiUsage')} />
          <Tab label={t('partner.reports')} />
          <Tab label={t('partner.settings')} />
        </Tabs>
      </Box>

      {/* Tab Content */}
      {selectedTab === 0 && (
        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>{t('partner.date')}</TableCell>
                <TableCell>{t('partner.client')}</TableCell>
                <TableCell>{t('partner.service')}</TableCell>
                <TableCell>{t('partner.amount')}</TableCell>
                <TableCell>{t('partner.status')}</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {salesData?.recentSales?.map((sale) => (
                <TableRow key={sale.id}>
                  <TableCell>{new Date(sale.date).toLocaleDateString()}</TableCell>
                  <TableCell>{sale.clientName}</TableCell>
                  <TableCell>{sale.serviceName}</TableCell>
                  <TableCell>${sale.amount.toLocaleString()}</TableCell>
                  <TableCell>
                    <Chip
                      label={sale.status}
                      color={sale.status === 'COMPLETED' ? 'success' : 'default'}
                      size="small"
                    />
                    </TableCell>
                  </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}

      {selectedTab === 1 && (
        <Card>
          <CardContent>
            <Typography variant="h6" gutterBottom>
              {t('partner.apiUsage')}
            </Typography>
            <TableContainer>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell>{t('partner.endpoint')}</TableCell>
                    <TableCell>{t('partner.calls')}</TableCell>
                    <TableCell>{t('partner.limit')}</TableCell>
                    <TableCell>{t('partner.status')}</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {apiUsage?.endpoints?.map((endpoint) => (
                    <TableRow key={endpoint.path}>
                      <TableCell>{endpoint.path}</TableCell>
                      <TableCell>{endpoint.calls}</TableCell>
                      <TableCell>{endpoint.limit}</TableCell>
                      <TableCell>
                        <Chip
                          label={endpoint.calls < endpoint.limit ? t('partner.withinLimit') : t('partner.overLimit')}
                          color={endpoint.calls < endpoint.limit ? 'success' : 'error'}
                          size="small"
                        />
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          </CardContent>
        </Card>
      )}

      {selectedTab === 2 && (
        <Card>
          <CardContent>
            <Typography variant="h6" gutterBottom>
              {t('partner.reports')}
            </Typography>
            <Alert severity="info" sx={{ mb: 2 }}>
              {t('partner.reportsDesc')}
            </Alert>
            <Button variant="contained" startIcon={<Assessment />}>
              {t('partner.generateReport')}
            </Button>
          </CardContent>
        </Card>
      )}

      {selectedTab === 3 && (
        <Card>
          <CardContent>
            <Typography variant="h6" gutterBottom>
              {t('partner.settings')}
            </Typography>
            <Alert severity="info">
              {t('partner.settingsDesc')}
            </Alert>
            <Button variant="outlined" startIcon={<Settings />} sx={{ mt: 2 }}>
              {t('partner.manageSettings')}
            </Button>
          </CardContent>
        </Card>
      )}
    </Container>
  );
};

export default PartnerPortal;



