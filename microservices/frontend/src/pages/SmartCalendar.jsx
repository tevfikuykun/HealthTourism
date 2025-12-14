// src/pages/SmartCalendar.jsx
import React, { useState } from 'react';
import {
  Container, Box, Typography, Paper, Grid, Card, CardContent,
  Button, Chip, Dialog, DialogTitle, DialogContent, DialogActions,
  TextField, Alert
} from '@mui/material';
import CalendarTodayIcon from '@mui/icons-material/CalendarToday';
import EventIcon from '@mui/icons-material/Event';
import WarningIcon from '@mui/icons-material/Warning';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import { useTranslation } from 'react-i18next';

const SmartCalendar = () => {
  const { t } = useTranslation();
  const [selectedDate, setSelectedDate] = useState(new Date());
  const [appointments, setAppointments] = useState([
    {
      id: 1,
      date: '2024-01-20',
      time: '10:00',
      type: 'Konsültasyon',
      doctor: 'Dr. Ahmet Yılmaz',
      hospital: 'Acıbadem Hastanesi',
      status: 'confirmed',
    },
    {
      id: 2,
      date: '2024-01-22',
      time: '14:00',
      type: 'Test',
      doctor: 'Dr. Ayşe Demir',
      hospital: 'Memorial Hastanesi',
      status: 'pending',
    },
  ]);

  const [conflicts, setConflicts] = useState([]);

  const checkConflicts = (newAppointment) => {
    // Çakışma kontrolü
    const conflicts = appointments.filter(apt =>
      apt.date === newAppointment.date &&
      apt.time === newAppointment.time
    );
    return conflicts;
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4" gutterBottom>
        {t('smartCalendar', 'Akıllı Takvim')}
      </Typography>

      <Grid container spacing={3}>
        <Grid item xs={12} md={8}>
          <Paper sx={{ p: 3, mb: 3 }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
              <Typography variant="h6">{t('myAppointments', 'Randevularım')}</Typography>
              <Button variant="contained" startIcon={<EventIcon />}>
                {t('newAppointment', 'Yeni Randevu')}
              </Button>
            </Box>

            {conflicts.length > 0 && (
              <Alert severity="warning" sx={{ mb: 2 }} icon={<WarningIcon />}>
                {conflicts.length} {t('conflictingAppointmentsFound', 'çakışan randevu bulundu!')}
              </Alert>
            )}

            <Grid container spacing={2}>
              {appointments.map((appointment) => (
                <Grid item xs={12} key={appointment.id}>
                  <Card>
                    <CardContent>
                      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start' }}>
                        <Box>
                          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
                            <CalendarTodayIcon color="primary" />
                            <Typography variant="h6">
                              {appointment.date} - {appointment.time}
                            </Typography>
                            <Chip
                              label={appointment.status === 'confirmed' ? t('confirmed', 'Onaylandı') : t('pending', 'Bekliyor')}
                              color={appointment.status === 'confirmed' ? 'success' : 'warning'}
                              size="small"
                            />
                          </Box>
                          <Typography variant="body1" sx={{ fontWeight: 'bold' }}>
                            {appointment.type}
                          </Typography>
                          <Typography variant="body2" color="text.secondary">
                            {appointment.doctor} - {appointment.hospital}
                          </Typography>
                        </Box>
                        <Button size="small" variant="outlined">
                          {t('details', 'Detaylar')}
                        </Button>
                      </Box>
                    </CardContent>
                  </Card>
                </Grid>
              ))}
            </Grid>
          </Paper>
        </Grid>

        <Grid item xs={12} md={4}>
          <Paper sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>
              {t('upcomingAppointments', 'Yaklaşan Randevular')}
            </Typography>
            {appointments
              .filter(apt => new Date(apt.date) >= new Date())
              .slice(0, 3)
              .map((appointment) => (
                <Card key={appointment.id} sx={{ mb: 2 }}>
                  <CardContent>
                    <Typography variant="body2" color="text.secondary">
                      {appointment.date}
                    </Typography>
                    <Typography variant="body1" sx={{ fontWeight: 'bold' }}>
                      {appointment.time} - {appointment.type}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      {appointment.doctor}
                    </Typography>
                  </CardContent>
                </Card>
              ))}
          </Paper>
        </Grid>
      </Grid>
    </Container>
  );
};

export default SmartCalendar;

