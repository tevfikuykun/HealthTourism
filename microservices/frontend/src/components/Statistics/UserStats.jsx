import React from 'react';
import { Card, CardContent, Typography, Box, Grid } from '@mui/material';
import { useQuery } from '@tanstack/react-query';
import { reservationService, paymentService } from '../../services/api';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';
import TrendingDownIcon from '@mui/icons-material/TrendingDown';

export default function UserStats({ userId }) {
  const { data: reservations } = useQuery({
    queryKey: ['userReservations', userId],
    queryFn: () => reservationService.getAll(userId ? { userId } : {}),
    enabled: !!userId,
  });

  const { data: payments } = useQuery({
    queryKey: ['userPayments', userId],
    queryFn: () => paymentService.getByUser(userId),
    enabled: !!userId,
  });

  const totalReservations = reservations?.data?.length || 0;
  const totalSpent = payments?.data?.reduce((sum, p) => sum + (p.amount || 0), 0) || 0;
  const activeReservations = reservations?.data?.filter((r) => r.status === 'CONFIRMED' || r.status === 'PENDING')?.length || 0;
  const completedReservations = reservations?.data?.filter((r) => r.status === 'COMPLETED')?.length || 0;

  const stats = [
    {
      title: 'Toplam Rezervasyon',
      value: totalReservations,
      icon: <TrendingUpIcon />,
      color: 'primary',
    },
    {
      title: 'Toplam Harcama',
      value: `${totalSpent.toLocaleString('tr-TR')} ₺`,
      icon: <TrendingUpIcon />,
      color: 'success',
    },
    {
      title: 'Aktif Rezervasyon',
      value: activeReservations,
      icon: <TrendingUpIcon />,
      color: 'warning',
    },
    {
      title: 'Tamamlanan',
      value: completedReservations,
      icon: <TrendingDownIcon />,
      color: 'info',
    },
  ];

  return (
    <Grid container spacing={3}>
      {stats.map((stat, index) => (
        <Grid item xs={12} sm={6} md={3} key={index}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <Box>
                  <Typography color="text.secondary" gutterBottom variant="body2">
                    {stat.title}
                  </Typography>
                  <Typography variant="h4">{stat.value}</Typography>
                </Box>
                <Box sx={{ color: `${stat.color}.main` }}>{stat.icon}</Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      ))}
    </Grid>
  );
}

