import React from 'react';
import {
  Container,
  Box,
  Typography,
  Card,
  CardContent,
  Grid,
  Chip,
  Button,
  Divider,
  List,
  ListItem,
  ListItemText,
  Paper,
  Stepper,
  Step,
  StepLabel,
} from '@mui/material';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { reservationService } from '../services/api';
import ProtectedRoute from '../components/ProtectedRoute';
import Loading from '../components/Loading';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import PrintIcon from '@mui/icons-material/Print';
import CancelIcon from '@mui/icons-material/Cancel';
import { useTranslation } from 'react-i18next';

const statusSteps = {
  PENDING: 0,
  CONFIRMED: 1,
  IN_PROGRESS: 2,
  COMPLETED: 3,
  CANCELLED: -1,
};

export default function ReservationDetail() {
  const { t } = useTranslation();
  const { id } = useParams();
  const navigate = useNavigate();

  const { data: reservation, isLoading } = useQuery({
    queryKey: ['reservation', id],
    queryFn: () => reservationService.getById(id),
    enabled: !!id,
  });

  if (isLoading) {
    return <Loading message={t('loadingReservationDetails', 'Rezervasyon detayları yükleniyor...')} />;
  }

  const reservationData = reservation?.data;

  if (!reservationData) {
    return (
      <Container maxWidth="lg" sx={{ py: 4 }}>
        <Typography variant="h6">{t('reservationNotFound', 'Rezervasyon bulunamadı')}</Typography>
      </Container>
    );
  }

  const getStatusColor = (status) => {
    switch (status) {
      case 'CONFIRMED':
        return 'success';
      case 'PENDING':
        return 'warning';
      case 'CANCELLED':
        return 'error';
      default:
        return 'default';
    }
  };

  const handleCancel = async () => {
    if (window.confirm(t('confirmCancelReservation', 'Bu rezervasyonu iptal etmek istediğinizden emin misiniz?'))) {
      try {
        await reservationService.cancel(id);
        navigate('/dashboard');
      } catch (error) {
        console.error('Rezervasyon iptal edilemedi:', error);
      }
    }
  };

  const handlePrint = () => {
    window.print();
  };

  return (
    <ProtectedRoute>
      <Container maxWidth="lg" sx={{ py: 4 }}>
        <Box sx={{ mb: 3, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Button startIcon={<ArrowBackIcon />} onClick={() => navigate(-1)}>
            {t('back', 'Geri')}
          </Button>
          <Box sx={{ display: 'flex', gap: 2 }}>
            <Button startIcon={<PrintIcon />} variant="outlined" onClick={handlePrint}>
              {t('print', 'Yazdır')}
            </Button>
            {reservationData.status !== 'CANCELLED' && reservationData.status !== 'COMPLETED' && (
              <Button startIcon={<CancelIcon />} variant="outlined" color="error" onClick={handleCancel}>
                {t('cancel', 'İptal Et')}
              </Button>
            )}
          </Box>
        </Box>

        <Paper sx={{ p: 3 }}>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 3 }}>
            <Box>
              <Typography variant="h4" gutterBottom>
                {t('reservationDetails', 'Rezervasyon Detayları')}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                {t('reservationId', 'Rezervasyon ID')}: #{reservationData.id}
              </Typography>
            </Box>
            <Chip label={reservationData.status} color={getStatusColor(reservationData.status)} size="large" />
          </Box>

          <Divider sx={{ mb: 3 }} />

          <Grid container spacing={3}>
            <Grid item xs={12} md={8}>
              <Card sx={{ mb: 3 }}>
                <CardContent>
                  <Typography variant="h6" gutterBottom>
                    {t('reservationInformation', 'Rezervasyon Bilgileri')}
                  </Typography>
                  <List>
                    <ListItem>
                      <ListItemText
                        primary={t('serviceType', 'Hizmet Tipi')}
                        secondary={reservationData.serviceType || '-'}
                      />
                    </ListItem>
                    <ListItem>
                      <ListItemText
                        primary={t('createdDate', 'Oluşturulma Tarihi')}
                        secondary={new Date(reservationData.createdAt).toLocaleString('tr-TR')}
                      />
                    </ListItem>
                    <ListItem>
                      <ListItemText
                        primary={t('reservationDate', 'Rezervasyon Tarihi')}
                        secondary={
                          reservationData.reservationDate
                            ? new Date(reservationData.reservationDate).toLocaleDateString('tr-TR')
                            : '-'
                        }
                      />
                    </ListItem>
                    {reservationData.notes && (
                      <ListItem>
                        <ListItemText primary="Notlar" secondary={reservationData.notes} />
                      </ListItem>
                    )}
                  </List>
                </CardContent>
              </Card>

              {reservationData.status !== 'CANCELLED' && (
                <Card>
                  <CardContent>
                    <Typography variant="h6" gutterBottom>
                      {t('reservationStatus', 'Rezervasyon Durumu')}
                    </Typography>
                    <Stepper activeStep={statusSteps[reservationData.status] || 0} alternativeLabel>
                      <Step>
                        <StepLabel>Beklemede</StepLabel>
                      </Step>
                      <Step>
                        <StepLabel>Onaylandı</StepLabel>
                      </Step>
                      <Step>
                        <StepLabel>Devam Ediyor</StepLabel>
                      </Step>
                      <Step>
                        <StepLabel>Tamamlandı</StepLabel>
                      </Step>
                    </Stepper>
                  </CardContent>
                </Card>
              )}
            </Grid>

            <Grid item xs={12} md={4}>
              <Card sx={{ mb: 3 }}>
                <CardContent>
                  <Typography variant="h6" gutterBottom>
                    Özet
                  </Typography>
                  <List>
                    <ListItem>
                      <ListItemText
                        primary={t('totalAmount')}
                        secondary={`${(reservationData.totalAmount || 0).toLocaleString('tr-TR')} ₺`}
                      />
                    </ListItem>
                    <ListItem>
                      <ListItemText
                        primary={t('paymentStatus', 'Ödeme Durumu')}
                        secondary={reservationData.paymentStatus || t('pending', 'Beklemede')}
                      />
                    </ListItem>
                  </List>
                </CardContent>
              </Card>

              <Card>
                <CardContent>
                  <Typography variant="h6" gutterBottom>
                    İletişim Bilgileri
                  </Typography>
                  <List>
                    <ListItem>
                      <ListItemText primary="Müşteri Adı" secondary={reservationData.customerName || '-'} />
                    </ListItem>
                    <ListItem>
                      <ListItemText primary="E-posta" secondary={reservationData.customerEmail || '-'} />
                    </ListItem>
                    <ListItem>
                      <ListItemText primary="Telefon" secondary={reservationData.customerPhone || '-'} />
                    </ListItem>
                  </List>
                </CardContent>
              </Card>
            </Grid>
          </Grid>
        </Paper>
      </Container>
    </ProtectedRoute>
  );
}

