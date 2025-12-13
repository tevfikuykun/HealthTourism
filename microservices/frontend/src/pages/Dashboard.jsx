import React, { useState } from 'react';
import {
  Container,
  Box,
  Typography,
  Grid,
  Card,
  CardContent,
  Tabs,
  Tab,
  Paper,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Avatar,
  Chip,
  Button,
  Divider,
  IconButton,
} from '@mui/material';
import { useAuth } from '../hooks/useAuth';
import { useQuery } from '@tanstack/react-query';
import { reservationService, paymentService, userService } from '../services/api';
import ProtectedRoute from '../components/ProtectedRoute';
import Loading from '../components/Loading';
import DashboardIcon from '@mui/icons-material/Dashboard';
import PersonIcon from '@mui/icons-material/Person';
import CalendarTodayIcon from '@mui/icons-material/CalendarToday';
import PaymentIcon from '@mui/icons-material/Payment';
import FavoriteIcon from '@mui/icons-material/Favorite';
import NotificationsIcon from '@mui/icons-material/Notifications';
import EditIcon from '@mui/icons-material/Edit';
import SettingsIcon from '@mui/icons-material/Settings';
import UserStats from '../components/Statistics/UserStats';

function TabPanel({ children, value, index }) {
  return (
    <div role="tabpanel" hidden={value !== index}>
      {value === index && <Box sx={{ p: 3 }}>{children}</Box>}
    </div>
  );
}

export default function Dashboard() {
  const { user, isAuthenticated } = useAuth();
  const [activeTab, setActiveTab] = useState(0);

  const { data: profile, isLoading: profileLoading } = useQuery({
    queryKey: ['userProfile'],
    queryFn: () => userService.getProfile(),
    enabled: isAuthenticated,
  });

  const { data: reservations, isLoading: reservationsLoading } = useQuery({
    queryKey: ['userReservations'],
    queryFn: () => reservationService.getAll({ userId: user?.id }),
    enabled: isAuthenticated,
  });

  const { data: payments, isLoading: paymentsLoading } = useQuery({
    queryKey: ['userPayments'],
    queryFn: () => paymentService.getByUser(user?.id),
    enabled: isAuthenticated,
  });

  if (!isAuthenticated) {
    return (
      <ProtectedRoute>
        <div />
      </ProtectedRoute>
    );
  }

  if (profileLoading || reservationsLoading || paymentsLoading) {
    return <Loading message="Profil bilgileri yükleniyor..." />;
  }

  const handleTabChange = (event, newValue) => {
    setActiveTab(newValue);
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" gutterBottom>
          Hoş Geldiniz, {profile?.data?.firstName || user?.firstName || 'Kullanıcı'}!
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Hesap bilgilerinizi ve rezervasyonlarınızı buradan yönetebilirsiniz.
        </Typography>
      </Box>

      <Grid container spacing={3}>
        {/* Sol Sidebar - Profil Özeti */}
        <Grid item xs={12} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', mb: 2 }}>
                <Avatar sx={{ width: 100, height: 100, mb: 2, bgcolor: 'primary.main' }}>
                  {(profile?.data?.firstName || user?.firstName || 'U')?.charAt(0).toUpperCase()}
                </Avatar>
                <Typography variant="h6">
                  {profile?.data?.firstName || user?.firstName} {profile?.data?.lastName || user?.lastName}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  {profile?.data?.email || user?.email}
                </Typography>
                <Chip label={profile?.data?.role || user?.role || 'USER'} color="primary" size="small" sx={{ mt: 1 }} />
              </Box>
              <Divider sx={{ my: 2 }} />
              <Box>
                <Button
                  fullWidth
                  variant="outlined"
                  startIcon={<EditIcon />}
                  sx={{ mb: 1 }}
                  onClick={() => setActiveTab(4)}
                >
                  Profili Düzenle
                </Button>
                <Button fullWidth variant="outlined" startIcon={<SettingsIcon />}>
                  Ayarlar
                </Button>
              </Box>
            </CardContent>
          </Card>

          {/* İstatistikler */}
          <Card sx={{ mt: 2 }}>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Özet
              </Typography>
              <Box sx={{ mt: 2 }}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
                  <Typography variant="body2">Toplam Rezervasyon</Typography>
                  <Typography variant="h6">{reservations?.data?.length || 0}</Typography>
                </Box>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
                  <Typography variant="body2">Toplam Ödeme</Typography>
                  <Typography variant="h6">
                    {payments?.data?.reduce((sum, p) => sum + (p.amount || 0), 0).toLocaleString('tr-TR') || 0} ₺
                  </Typography>
                </Box>
                <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                  <Typography variant="body2">Aktif Rezervasyon</Typography>
                  <Typography variant="h6">
                    {reservations?.data?.filter((r) => r.status === 'CONFIRMED' || r.status === 'PENDING')?.length || 0}
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        {/* Ana İçerik Alanı */}
        <Grid item xs={12} md={9}>
          <Paper>
            <Tabs value={activeTab} onChange={handleTabChange} variant="scrollable" scrollButtons="auto">
              <Tab icon={<DashboardIcon />} label="Genel Bakış" />
              <Tab icon={<CalendarTodayIcon />} label="Rezervasyonlarım" />
              <Tab icon={<PaymentIcon />} label="Ödemelerim" />
              <Tab icon={<FavoriteIcon />} label="Favorilerim" />
              <Tab icon={<PersonIcon />} label="Profil Bilgileri" />
              <Tab icon={<NotificationsIcon />} label="Bildirimler" />
            </Tabs>

            <TabPanel value={activeTab} index={0}>
              <Typography variant="h6" gutterBottom>
                Genel Bakış
              </Typography>
              <UserStats userId={user?.id} />

              {/* Son Rezervasyonlar */}
              <Box sx={{ mt: 3 }}>
                <Typography variant="h6" gutterBottom>
                  Son Rezervasyonlar
                </Typography>
                <List>
                  {reservations?.data?.slice(0, 5).map((reservation) => (
                    <ListItem key={reservation.id} divider>
                      <ListItemIcon>
                        <CalendarTodayIcon color="primary" />
                      </ListItemIcon>
                      <ListItemText
                        primary={reservation.serviceType || 'Rezervasyon'}
                        secondary={`${new Date(reservation.createdAt).toLocaleDateString('tr-TR')} - ${reservation.status}`}
                      />
                      <Chip
                        label={reservation.status}
                        color={
                          reservation.status === 'CONFIRMED'
                            ? 'success'
                            : reservation.status === 'PENDING'
                            ? 'warning'
                            : 'default'
                        }
                        size="small"
                      />
                    </ListItem>
                  )) || (
                    <ListItem>
                      <ListItemText primary="Henüz rezervasyonunuz bulunmamaktadır." />
                    </ListItem>
                  )}
                </List>
              </Box>
            </TabPanel>

            <TabPanel value={activeTab} index={1}>
              <Typography variant="h6" gutterBottom>
                Rezervasyonlarım
              </Typography>
              <List>
                {reservations?.data?.map((reservation) => (
                  <Card key={reservation.id} sx={{ mb: 2 }}>
                    <CardContent>
                      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                        <Box>
                          <Typography variant="h6">{reservation.serviceType || 'Rezervasyon'}</Typography>
                          <Typography variant="body2" color="text.secondary">
                            Tarih: {new Date(reservation.createdAt).toLocaleDateString('tr-TR')}
                          </Typography>
                          <Typography variant="body2" color="text.secondary">
                            Tutar: {reservation.totalAmount?.toLocaleString('tr-TR') || 0} ₺
                          </Typography>
                        </Box>
                        <Box>
                          <Chip
                            label={reservation.status}
                            color={
                              reservation.status === 'CONFIRMED'
                                ? 'success'
                                : reservation.status === 'PENDING'
                                ? 'warning'
                                : 'default'
                            }
                            sx={{ mb: 1 }}
                          />
                          <Button size="small" variant="outlined" sx={{ display: 'block', width: '100%' }}>
                            Detayları Gör
                          </Button>
                        </Box>
                      </Box>
                    </CardContent>
                  </Card>
                )) || (
                  <Typography variant="body1" color="text.secondary">
                    Henüz rezervasyonunuz bulunmamaktadır.
                  </Typography>
                )}
              </List>
            </TabPanel>

            <TabPanel value={activeTab} index={2}>
              <Typography variant="h6" gutterBottom>
                Ödemelerim
              </Typography>
              <List>
                {payments?.data?.map((payment) => (
                  <Card key={payment.id} sx={{ mb: 2 }}>
                    <CardContent>
                      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                        <Box>
                          <Typography variant="h6">Ödeme #{payment.id}</Typography>
                          <Typography variant="body2" color="text.secondary">
                            Tarih: {new Date(payment.createdAt || payment.paymentDate).toLocaleDateString('tr-TR')}
                          </Typography>
                          <Typography variant="body2" color="text.secondary">
                            Tutar: {payment.amount?.toLocaleString('tr-TR') || 0} ₺
                          </Typography>
                        </Box>
                        <Chip
                          label={payment.status || 'COMPLETED'}
                          color={payment.status === 'COMPLETED' || !payment.status ? 'success' : 'warning'}
                        />
                      </Box>
                    </CardContent>
                  </Card>
                )) || (
                  <Typography variant="body1" color="text.secondary">
                    Henüz ödemeniz bulunmamaktadır.
                  </Typography>
                )}
              </List>
            </TabPanel>

            <TabPanel value={activeTab} index={3}>
              <Typography variant="h6" gutterBottom>
                Favorilerim
              </Typography>
              <Typography variant="body1" color="text.secondary">
                Favori özelliği yakında eklenecektir.
              </Typography>
            </TabPanel>

            <TabPanel value={activeTab} index={4}>
              <Typography variant="h6" gutterBottom>
                Profil Bilgileri
              </Typography>
              <Grid container spacing={2} sx={{ mt: 1 }}>
                <Grid item xs={12} sm={6}>
                  <Typography variant="body2" color="text.secondary">
                    Ad
                  </Typography>
                  <Typography variant="body1">{profile?.data?.firstName || user?.firstName || '-'}</Typography>
                </Grid>
                <Grid item xs={12} sm={6}>
                  <Typography variant="body2" color="text.secondary">
                    Soyad
                  </Typography>
                  <Typography variant="body1">{profile?.data?.lastName || user?.lastName || '-'}</Typography>
                </Grid>
                <Grid item xs={12} sm={6}>
                  <Typography variant="body2" color="text.secondary">
                    E-posta
                  </Typography>
                  <Typography variant="body1">{profile?.data?.email || user?.email || '-'}</Typography>
                </Grid>
                <Grid item xs={12} sm={6}>
                  <Typography variant="body2" color="text.secondary">
                    Telefon
                  </Typography>
                  <Typography variant="body1">{profile?.data?.phone || '-'}</Typography>
                </Grid>
                <Grid item xs={12} sm={6}>
                  <Typography variant="body2" color="text.secondary">
                    Ülke
                  </Typography>
                  <Typography variant="body1">{profile?.data?.country || '-'}</Typography>
                </Grid>
                <Grid item xs={12}>
                  <Button variant="contained" startIcon={<EditIcon />}>
                    Profili Düzenle
                  </Button>
                </Grid>
              </Grid>
            </TabPanel>

            <TabPanel value={activeTab} index={5}>
              <Typography variant="h6" gutterBottom>
                Bildirimler
              </Typography>
              <Typography variant="body1" color="text.secondary">
                Bildirim özelliği yakında eklenecektir.
              </Typography>
            </TabPanel>
          </Paper>
        </Grid>
      </Grid>
    </Container>
  );
}

