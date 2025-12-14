// src/pages/Analytics.jsx
import React, { useState, useEffect } from 'react';
import { useQuery } from '@tanstack/react-query';
import {
  Container, Box, Typography, Grid, Card, CardContent, Paper,
  Tabs, Tab
} from '@mui/material';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';
import PeopleIcon from '@mui/icons-material/People';
import LocalHospitalIcon from '@mui/icons-material/LocalHospital';
import AttachMoneyIcon from '@mui/icons-material/AttachMoney';
import LineChart from '../components/Charts/LineChart';
import BarChart from '../components/Charts/BarChart';
import PieChart from '../components/Charts/PieChart';
import LoadingState from '../components/LoadingState/LoadingState';
import ErrorState from '../components/ErrorState/ErrorState';
import { analyticsService } from '../services/api';
import { useTranslation } from 'react-i18next';

const Analytics = () => {
  const { t } = useTranslation();
  const [activeTab, setActiveTab] = useState(0);

  const { data: revenueData, isLoading: isLoadingRevenue, error: revenueError } = useQuery({
    queryKey: ['analytics', 'revenue'],
    queryFn: () => analyticsService.getRevenue({ period: 'monthly' }),
  });

  const { data: usersData, isLoading: isLoadingUsers, error: usersError } = useQuery({
    queryKey: ['analytics', 'users'],
    queryFn: () => analyticsService.getUsers({ period: 'monthly' }),
  });

  const { data: reservationsData, isLoading: isLoadingReservations, error: reservationsError } = useQuery({
    queryKey: ['analytics', 'reservations'],
    queryFn: () => analyticsService.getReservations({ period: 'weekly' }),
  });

  const { data: servicesData, isLoading: isLoadingServices, error: servicesError } = useQuery({
    queryKey: ['analytics', 'services'],
    queryFn: () => analyticsService.getServices({ period: 'monthly' }),
  });

  if (isLoadingRevenue || isLoadingUsers || isLoadingReservations || isLoadingServices) {
    return <LoadingState message="Analitik veriler yükleniyor..." />;
  }

  if (revenueError || usersError || reservationsError || servicesError) {
    return <ErrorState message="Analitik veriler yüklenirken bir hata oluştu" />;
  }

  const stats = [
    { title: 'Toplam Rezervasyon', value: '1,234', icon: <LocalHospitalIcon />, color: 'primary' },
    { title: 'Aktif Kullanıcılar', value: '5,678', icon: <PeopleIcon />, color: 'success' },
    { title: 'Toplam Gelir', value: '₺2,345,678', icon: <AttachMoneyIcon />, color: 'warning' },
    { title: 'Büyüme Oranı', value: '+23%', icon: <TrendingUpIcon />, color: 'info' },
  ];

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4" gutterBottom>
        Analitik ve Raporlama
      </Typography>

      <Grid container spacing={3} sx={{ mt: 2 }}>
        {stats.map((stat, index) => (
          <Grid item xs={12} sm={6} md={3} key={index}>
            <Card>
              <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                  <Box>
                    <Typography variant="h4">{stat.value}</Typography>
                    <Typography variant="body2" color="text.secondary">
                      {stat.title}
                    </Typography>
                  </Box>
                  <Box sx={{ color: `${stat.color}.main` }}>
                    {stat.icon}
                  </Box>
                </Box>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>

      <Paper sx={{ mt: 3 }}>
        <Tabs value={activeTab} onChange={(e, v) => setActiveTab(v)}>
          <Tab label="Gelir Analizi" />
          <Tab label="Kullanıcı Analizi" />
          <Tab label="Rezervasyon Analizi" />
          <Tab label="Hizmet Analizi" />
        </Tabs>

        <Box sx={{ p: 3 }}>
          {activeTab === 0 && (
            <Box>
              <Typography variant="h6" gutterBottom>Aylık Gelir Trendi</Typography>
              <Box sx={{ mt: 2 }}>
                <LineChart
                  data={[
                    { name: 'Ocak', gelir: 4000 },
                    { name: 'Şubat', gelir: 3000 },
                    { name: 'Mart', gelir: 5000 },
                    { name: 'Nisan', gelir: 4500 },
                    { name: 'Mayıs', gelir: 6000 },
                    { name: 'Haziran', gelir: 5500 },
                  ]}
                  dataKey="gelir"
                  name="Gelir (₺)"
                  color="#8884d8"
                />
              </Box>
            </Box>
          )}
          {activeTab === 1 && (
            <Box>
              <Typography variant="h6" gutterBottom>Kullanıcı Dağılımı</Typography>
              <Box sx={{ mt: 2 }}>
                <BarChart
                  data={[
                    { name: 'Yeni', kullanici: 400 },
                    { name: 'Aktif', kullanici: 1200 },
                    { name: 'VIP', kullanici: 300 },
                    { name: 'Premium', kullanici: 150 },
                  ]}
                  dataKey="kullanici"
                  name="Kullanıcı Sayısı"
                  color="#82ca9d"
                />
              </Box>
            </Box>
          )}
          {activeTab === 2 && (
            <Box>
              <Typography variant="h6" gutterBottom>Rezervasyon İstatistikleri</Typography>
              <Box sx={{ mt: 2 }}>
                <LineChart
                  data={[
                    { name: 'Pzt', rezervasyon: 24 },
                    { name: 'Sal', rezervasyon: 30 },
                    { name: 'Çar', rezervasyon: 28 },
                    { name: 'Per', rezervasyon: 35 },
                    { name: 'Cum', rezervasyon: 40 },
                    { name: 'Cmt', rezervasyon: 45 },
                    { name: 'Paz', rezervasyon: 38 },
                  ]}
                  dataKey="rezervasyon"
                  name="Rezervasyon Sayısı"
                  color="#ffc658"
                />
              </Box>
            </Box>
          )}
          {activeTab === 3 && (
            <Box>
              <Typography variant="h6" gutterBottom>Hizmet Popülerliği</Typography>
              <Box sx={{ mt: 2 }}>
                <PieChart
                  data={[
                    { name: 'Konsültasyon', deger: 35 },
                    { name: 'Ameliyat', deger: 25 },
                    { name: 'Test', deger: 20 },
                    { name: 'Paket', deger: 20 },
                  ]}
                  dataKey="deger"
                  nameKey="name"
                />
              </Box>
            </Box>
          )}
        </Box>
      </Paper>
    </Container>
  );
};

export default Analytics;

