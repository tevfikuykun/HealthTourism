import React from 'react';
import { Card, CardContent, Typography, Box, Grid } from '@mui/material';
import PeopleIcon from '@mui/icons-material/People';
import EventNoteIcon from '@mui/icons-material/EventNote';
import AttachMoneyIcon from '@mui/icons-material/AttachMoney';
import LocalHospitalIcon from '@mui/icons-material/LocalHospital';

export default function AdminStats() {
  // TODO: Admin API entegrasyonu
  const stats = [
    {
      title: 'Toplam Kullanıcı',
      value: '1,234',
      icon: <PeopleIcon />,
      color: 'primary',
    },
    {
      title: 'Toplam Rezervasyon',
      value: '567',
      icon: <EventNoteIcon />,
      color: 'success',
    },
    {
      title: 'Toplam Gelir',
      value: '125,000 ₺',
      icon: <AttachMoneyIcon />,
      color: 'warning',
    },
    {
      title: 'Aktif Hastane',
      value: '24',
      icon: <LocalHospitalIcon />,
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
                <Box sx={{ color: `${stat.color}.main`, fontSize: 40 }}>{stat.icon}</Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      ))}
    </Grid>
  );
}

