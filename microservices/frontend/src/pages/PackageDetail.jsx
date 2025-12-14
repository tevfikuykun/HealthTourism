// src/pages/PackageDetail.jsx
import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
    Container, Box, Typography, Grid, Card, CardContent,
    Button, Paper, useTheme, Divider, Rating, Chip, Tabs, Tab,
    List, ListItem, ListItemIcon, ListItemText, Stepper, Step, StepLabel
} from '@mui/material';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import LocalHospitalIcon from '@mui/icons-material/LocalHospital';
import HealthAndSafetyIcon from '@mui/icons-material/HealthAndSafety';
import { packageService } from '../services/api';
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

export default function PackageDetail() {
    const { t } = useTranslation();
    const { slug } = useParams();
    const navigate = useNavigate();
    const theme = useTheme();
    const [tabValue, setTabValue] = useState(0);

    const { data: packageData, isLoading, error } = useQuery({
        queryKey: ['package', slug],
        queryFn: () => packageService.getBySlug(slug),
        enabled: !!slug
    });

    if (isLoading) return <LoadingState />;
    if (error) return <ErrorState error={error} />;
    if (!packageData) return <ErrorState error={{ message: t('packageNotFound', 'Paket bulunamadı') }} />;

    const handleTabChange = (event, newValue) => {
        setTabValue(newValue);
    };

    const steps = [t('reservation'), t('payment'), t('confirmation'), t('treatment')];

    return (
        <>
            <SEOHead
                title={`${packageData.name} - ${t('healthPackage', 'Sağlık Paketi')}`}
                description={packageData.description || `${packageData.name} ${t('healthPackageDetails', 'sağlık paketi detayları, fiyatlar ve rezervasyon bilgileri')}`}
                keywords={`${packageData.name}, ${t('healthPackage', 'sağlık paketi')}, ${packageData.category}, ${t('healthTourism', 'sağlık turizmi')}`}
            />
            <Container maxWidth="lg" sx={{ py: 4 }}>
                <Grid container spacing={4}>
                    <Grid item xs={12} md={8}>
                        <Box
                            sx={{
                                height: 400,
                                backgroundImage: `url(${packageData.image || 'https://source.unsplash.com/random/800x400/?medical'})`,
                                backgroundSize: 'cover',
                                backgroundPosition: 'center',
                                borderRadius: 2,
                                mb: 3,
                                position: 'relative'
                            }}
                        >
                            <Chip
                                label={packageData.category}
                                color="primary"
                                icon={<HealthAndSafetyIcon />}
                                sx={{ position: 'absolute', top: 16, left: 16, fontWeight: 'bold' }}
                            />
                        </Box>

                        <Paper elevation={2} sx={{ p: 3, mb: 3 }}>
                            <Typography variant="h4" gutterBottom fontWeight="bold">
                                {packageData.name}
                            </Typography>
                            <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 2 }}>
                                <Rating value={packageData.rating || 0} readOnly precision={0.1} />
                                <Typography variant="body2" color="text.secondary">
                                    {packageData.hospital || t('partnerHospital', 'Partner Hastane')}
                                </Typography>
                            </Box>
                            <Divider sx={{ my: 2 }} />
                            <Typography variant="body1" paragraph>
                                {packageData.description || t('makeReservationForDetails', 'Detaylı bilgi için rezervasyon yapın.')}
                            </Typography>
                        </Paper>

                        <Paper elevation={2} sx={{ mb: 3 }}>
                            <Tabs value={tabValue} onChange={handleTabChange}>
                                <Tab label={t('packageContent', 'Paket İçeriği')} />
                                <Tab label={t('process', 'Süreç')} />
                                <Tab label={t('hospitalInfo', 'Hastane Bilgileri')} />
                                <Tab label={t('comments', 'Yorumlar')} />
                            </Tabs>
                            <TabPanel value={tabValue} index={0}>
                                <List>
                                    {(packageData.services || []).map((service, index) => (
                                        <ListItem key={index}>
                                            <ListItemIcon>
                                                <CheckCircleIcon color="primary" />
                                            </ListItemIcon>
                                            <ListItemText primary={service} />
                                        </ListItem>
                                    ))}
                                </List>
                            </TabPanel>
                            <TabPanel value={tabValue} index={1}>
                                <Stepper activeStep={-1} orientation="vertical">
                                    {steps.map((label) => (
                                        <Step key={label}>
                                            <StepLabel>{label}</StepLabel>
                                        </Step>
                                    ))}
                                </Stepper>
                            </TabPanel>
                            <TabPanel value={tabValue} index={2}>
                                <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                                    <LocalHospitalIcon sx={{ mr: 1, color: 'primary.main' }} />
                                    <Typography variant="h6">{packageData.hospital || t('partnerHospital', 'Partner Hastane')}</Typography>
                                </Box>
                                <Typography variant="body2" color="text.secondary">
                                    {t('detailedHospitalInfoComingSoon', 'Detaylı hastane bilgileri yakında eklenecek.')}
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
                                {t('packagePrice', 'Paket Fiyatı')}
                            </Typography>
                            <Divider sx={{ my: 2 }} />
                            <Box sx={{ mb: 3 }}>
                                <Typography variant="h4" color="primary" fontWeight="bold">
                                    ${packageData.price || 0}
                                </Typography>
                                <Typography variant="body2" color="text.secondary">
                                    {packageData.duration && `${t('duration', 'Süre')}: ${packageData.duration} ${t('days', 'gün')}`}
                                </Typography>
                            </Box>
                            <Button
                                variant="contained"
                                fullWidth
                                size="large"
                                onClick={() => navigate(`/reservations?package=${packageData.id}`)}
                                sx={{ mb: 2 }}
                            >
                                {t('makeReservation', 'Rezervasyon Yap')}
                            </Button>
                            <Button
                                variant="outlined"
                                fullWidth
                                onClick={() => navigate('/packages')}
                            >
                                {t('otherPackages', 'Diğer Paketler')}
                            </Button>
                        </Paper>
                    </Grid>
                </Grid>
            </Container>
        </>
    );
}

