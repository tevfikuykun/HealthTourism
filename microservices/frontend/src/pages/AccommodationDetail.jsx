// src/pages/AccommodationDetail.jsx
import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
    Container, Box, Typography, Grid, Card, CardContent,
    Button, Paper, useTheme, Divider, Rating, Chip, Tabs, Tab,
    List, ListItem, ListItemIcon, ListItemText, CardMedia, Avatar
} from '@mui/material';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import StarIcon from '@mui/icons-material/Star';
import LocationOnIcon from '@mui/icons-material/LocationOn';
import LocalHospitalIcon from '@mui/icons-material/LocalHospital';
import WifiIcon from '@mui/icons-material/Wifi';
import HotelIcon from '@mui/icons-material/Hotel';
import HomeWorkIcon from '@mui/icons-material/HomeWork';
import { accommodationService } from '../services/api';
import { useQuery } from '@tanstack/react-query';
import LoadingState from '../components/LoadingState/LoadingState';
import ErrorState from '../components/ErrorState/ErrorState';
import SEOHead from '../components/SEO/SEOHead';
import { useTranslation } from 'react-i18next';

function TabPanel(props) {
    const { children, value, index, ...other } = props;
    return (
        <div role="tabpanel" hidden={value !== index} {...other}>
            {value === index && <Box sx={{ p: 3 }}>{children}</Box>}
        </div>
    );
}

export default function AccommodationDetail() {
    const { t } = useTranslation();
    const { slug } = useParams();
    const navigate = useNavigate();
    const theme = useTheme();
    const [tabValue, setTabValue] = useState(0);

    const { data: accommodation, isLoading, error } = useQuery({
        queryKey: ['accommodation', slug],
        queryFn: () => accommodationService.getBySlug(slug),
        enabled: !!slug
    });

    if (isLoading) return <LoadingState />;
    if (error) return <ErrorState error={error} />;
    if (!accommodation) return <ErrorState error={{ message: t('accommodationNotFound', 'Konaklama bulunamadı') }} />;

    const handleTabChange = (event, newValue) => {
        setTabValue(newValue);
    };

    return (
        <>
            <SEOHead
                title={`${accommodation.name} - ${t('accommodationDetails', 'Konaklama Detayları')}`}
                description={accommodation.description || `${accommodation.name} ${t('accommodationDetailsDesc', 'konaklama detayları, fiyatlar ve rezervasyon bilgileri')}`}
                keywords={`${accommodation.name}, ${t('accommodation', 'konaklama')}, ${accommodation.city}, ${t('healthTourism', 'sağlık turizmi')}`}
            />
            <Container maxWidth="lg" sx={{ py: 4 }}>
                <Grid container spacing={4}>
                    <Grid item xs={12} md={8}>
                        <CardMedia
                            component="img"
                            height="400"
                            image={accommodation.image || 'https://source.unsplash.com/random/800x400/?hotel'}
                            alt={accommodation.name}
                            sx={{ borderRadius: 2, mb: 3 }}
                        />

                        <Paper elevation={2} sx={{ p: 3, mb: 3 }}>
                            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start', mb: 2 }}>
                                <Box>
                                    <Typography variant="h4" gutterBottom fontWeight="bold">
                                        {accommodation.name}
                                    </Typography>
                                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 2 }}>
                                        <Chip
                                            icon={accommodation.type === 'Otel' ? <HotelIcon /> : <HomeWorkIcon />}
                                            label={accommodation.type}
                                            color="primary"
                                        />
                                        <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                            <LocationOnIcon fontSize="small" color="action" sx={{ mr: 0.5 }} />
                                            <Typography variant="body2">{accommodation.city}</Typography>
                                        </Box>
                                        {accommodation.proximity && (
                                            <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                                <LocalHospitalIcon fontSize="small" color="error" sx={{ mr: 0.5 }} />
                                                <Typography variant="body2" color="error">
                                                    {accommodation.proximity} {t('kmToHospital', 'km hastaneye')}
                                                </Typography>
                                            </Box>
                                        )}
                                    </Box>
                                    <Rating value={accommodation.rating || 0} readOnly precision={0.1} />
                                </Box>
                            </Box>
                        </Paper>

                        <Paper elevation={2} sx={{ mb: 3 }}>
                            <Tabs value={tabValue} onChange={handleTabChange}>
                                <Tab label={t('generalInfo', 'Genel Bilgiler')} />
                                <Tab label={t('amenities', 'Olanaklar')} />
                                <Tab label={t('location', 'Konum')} />
                                <Tab label={t('comments', 'Yorumlar')} />
                            </Tabs>
                            <TabPanel value={tabValue} index={0}>
                                <Typography variant="body1" paragraph>
                                    {accommodation.description || t('makeReservationForDetails', 'Detaylı bilgi için rezervasyon yapın.')}
                                </Typography>
                            </TabPanel>
                            <TabPanel value={tabValue} index={1}>
                                <List>
                                    {(accommodation.amenities || []).map((amenity, index) => (
                                        <ListItem key={index}>
                                            <ListItemIcon>
                                                <CheckCircleIcon color="primary" />
                                            </ListItemIcon>
                                            <ListItemText primary={amenity} />
                                        </ListItem>
                                    ))}
                                </List>
                            </TabPanel>
                            <TabPanel value={tabValue} index={2}>
                                <Typography variant="body1" paragraph>
                                    {accommodation.address || `${accommodation.city} ${t('nearCityCenter', 'şehir merkezine yakın konumda')}`}
                                </Typography>
                            </TabPanel>
                            <TabPanel value={tabValue} index={3}>
                                <Typography variant="body2" color="text.secondary">
                                    {t('commentsComingSoon', 'Yorumlar yakında eklenecek.')}
                                </Typography>
                            </TabPanel>
                        </Paper>
                    </Grid>

                    <Grid item xs={12} md={4}>
                        <Paper elevation={3} sx={{ p: 3, position: 'sticky', top: 20 }}>
                            <Typography variant="h5" gutterBottom fontWeight="bold">
                                {t('priceInfo', 'Fiyat Bilgisi')}
                            </Typography>
                            <Divider sx={{ my: 2 }} />
                            <Box sx={{ mb: 3 }}>
                                <Typography variant="h4" color="primary" fontWeight="bold">
                                    ${accommodation.pricePerNight || 0}
                                </Typography>
                                <Typography variant="body2" color="text.secondary">
                                    / {t('perNight', 'Gece')}
                                </Typography>
                            </Box>
                            <Button
                                variant="contained"
                                fullWidth
                                size="large"
                                onClick={() => navigate(`/reservations?accommodation=${accommodation.id}`)}
                                sx={{ mb: 2 }}
                            >
                                {t('makeReservation', 'Rezervasyon Yap')}
                            </Button>
                            <Button
                                variant="outlined"
                                fullWidth
                                onClick={() => navigate('/accommodations')}
                            >
                                {t('otherAccommodations', 'Diğer Konaklamalar')}
                            </Button>
                        </Paper>
                    </Grid>
                </Grid>
            </Container>
        </>
    );
}

