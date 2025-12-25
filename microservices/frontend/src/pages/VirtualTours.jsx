// src/pages/VirtualTours.jsx
import React, { useState } from 'react';
import {
  Container,
  Box,
  Typography,
  Grid,
  Card,
  CardContent,
  CardMedia,
  Button,
  Chip,
  Tabs,
  Tab,
  Dialog,
  IconButton,
  CircularProgress,
  Alert,
} from '@mui/material';
import {
  Visibility as VisibilityIcon,
  Star as StarIcon,
  Close as CloseIcon,
  Fullscreen as FullscreenIcon,
} from '@mui/icons-material';
import { useQuery } from '@tanstack/react-query';
import { useTranslation } from '../i18n';
import Loading from '../components/Loading';
import VirtualTourViewer from '../components/VirtualTour/VirtualTourViewer';
import { api } from '../services/api';

function TabPanel({ children, value, index }) {
  return (
    <div role="tabpanel" hidden={value !== index}>
      {value === index && <Box sx={{ py: 3 }}>{children}</Box>}
    </div>
  );
}

export default function VirtualTours() {
  const { t } = useTranslation();
  const [selectedTab, setSelectedTab] = useState(0);
  const [selectedTour, setSelectedTour] = useState(null);
  const [viewerOpen, setViewerOpen] = useState(false);

  // Fetch virtual tours
  const { data: tours, isLoading, error } = useQuery({
    queryKey: ['virtualTours', selectedTab],
    queryFn: async () => {
      const tourType = ['HOSPITAL', 'ACCOMMODATION', 'DOCTOR_OFFICE', 'OPERATING_ROOM'][selectedTab];
      const response = await api.get(`/virtual-tours/type/${tourType}`);
      return response.data || [];
    },
  });

  const handleTabChange = (event, newValue) => {
    setSelectedTab(newValue);
  };

  const handleViewTour = (tour) => {
    setSelectedTour(tour);
    setViewerOpen(true);
  };

  const tourTypes = [
    { label: t('virtualTour.hospitals', 'Hastaneler'), value: 'HOSPITAL' },
    { label: t('virtualTour.accommodations', 'Konaklama'), value: 'ACCOMMODATION' },
    { label: t('virtualTour.doctorOffices', 'Doktor Ofisleri'), value: 'DOCTOR_OFFICE' },
    { label: t('virtualTour.operatingRooms', 'Ameliyathaneler'), value: 'OPERATING_ROOM' },
  ];

  if (isLoading) {
    return <Loading />;
  }

  if (error) {
    return (
      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        <Alert severity="error">
          {t('virtualTour.loadError', 'Sanal turlar yüklenirken bir hata oluştu')}
        </Alert>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" gutterBottom>
          {t('virtualTour.title', '360° Sanal Turlar')}
        </Typography>
        <Typography variant="body1" color="text.secondary">
          {t('virtualTour.subtitle', 'Hastanelerimizi, konaklama tesislerimizi ve ameliyathaneleri sanal olarak keşfedin')}
        </Typography>
      </Box>

      <Tabs value={selectedTab} onChange={handleTabChange} sx={{ mb: 3 }}>
        {tourTypes.map((type, index) => (
          <Tab key={index} label={type.label} />
        ))}
      </Tabs>

      {!tours || tours.length === 0 ? (
        <Card>
          <CardContent>
            <Box sx={{ textAlign: 'center', py: 4 }}>
              <Typography variant="h6" color="text.secondary">
                {t('virtualTour.noTours', 'Bu kategoride henüz sanal tur bulunmamaktadır')}
              </Typography>
            </Box>
          </CardContent>
        </Card>
      ) : (
        <Grid container spacing={3}>
          {tours.map((tour) => (
            <Grid item xs={12} sm={6} md={4} key={tour.id}>
              <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
                <CardMedia
                  component="img"
                  height="200"
                  image={tour.thumbnailUrl || '/placeholder-tour.jpg'}
                  alt={tour.title}
                  sx={{ objectFit: 'cover' }}
                />
                <CardContent sx={{ flexGrow: 1, display: 'flex', flexDirection: 'column' }}>
                  <Typography variant="h6" gutterBottom>
                    {tour.title}
                  </Typography>
                  <Typography variant="body2" color="text.secondary" sx={{ mb: 2, flexGrow: 1 }}>
                    {tour.description}
                  </Typography>
                  <Box sx={{ display: 'flex', gap: 1, mb: 2, flexWrap: 'wrap' }}>
                    <Chip
                      icon={<VisibilityIcon />}
                      label={`${tour.viewCount || 0} ${t('virtualTour.views', 'görüntülenme')}`}
                      size="small"
                    />
                    {tour.rating && (
                      <Chip
                        icon={<StarIcon />}
                        label={tour.rating.toFixed(1)}
                        size="small"
                        color="primary"
                      />
                    )}
                    {tour.supportsAR && (
                      <Chip
                        label={t('virtualTour.arSupported', 'AR Destekli')}
                        size="small"
                        color="secondary"
                      />
                    )}
                  </Box>
                  <Button
                    variant="contained"
                    fullWidth
                    startIcon={<FullscreenIcon />}
                    onClick={() => handleViewTour(tour)}
                  >
                    {t('virtualTour.viewTour', 'Turu Görüntüle')}
                  </Button>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}

      {/* Virtual Tour Viewer Dialog */}
      <Dialog
        open={viewerOpen}
        onClose={() => setViewerOpen(false)}
        maxWidth="lg"
        fullWidth
        PaperProps={{
          sx: {
            height: '90vh',
            maxHeight: '90vh',
          },
        }}
      >
        <Box sx={{ position: 'relative', height: '100%' }}>
          <IconButton
            onClick={() => setViewerOpen(false)}
            sx={{
              position: 'absolute',
              top: 8,
              right: 8,
              zIndex: 1000,
              bgcolor: 'rgba(0,0,0,0.5)',
              color: 'white',
              '&:hover': { bgcolor: 'rgba(0,0,0,0.7)' },
            }}
          >
            <CloseIcon />
          </IconButton>
          {selectedTour && (
            <VirtualTourViewer
              tourUrl={selectedTour.tourUrl}
              panoramaImageUrl={selectedTour.panoramaImageUrl}
              title={selectedTour.title}
              onClose={() => setViewerOpen(false)}
            />
          )}
        </Box>
      </Dialog>
    </Container>
  );
}



