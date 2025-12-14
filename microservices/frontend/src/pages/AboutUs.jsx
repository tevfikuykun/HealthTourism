// src/pages/AboutUs.jsx
import React from 'react';
import {
    Container, Box, Typography, Grid, Card, CardContent,
    Button, Paper, useTheme, Divider, Avatar
} from '@mui/material';
import GroupIcon from '@mui/icons-material/Group';
import LightbulbIcon from '@mui/icons-material/Lightbulb';
import DiamondIcon from '@mui/icons-material/Diamond';
import LocalHospitalIcon from '@mui/icons-material/LocalHospital';
import SecurityIcon from '@mui/icons-material/Security';
import StarIcon from '@mui/icons-material/Star';
import PeopleIcon from '@mui/icons-material/People';
import { useTranslation } from 'react-i18next';

// --- ÖRNEK VERİLER ---
const coreValues = [
    { icon: SecurityIcon, title: 'Güvenilirlik', description: 'Şeffaf fiyatlandırma ve uluslararası akredite merkezlerle çalışıyoruz.' },
    { icon: DiamondIcon, title: 'Kalite Odaklılık', description: 'Misafirlerimize sadece en yüksek standartlarda tıbbi hizmet ve konfor sunuyoruz.' },
    { icon: PeopleIcon, title: 'Hasta Merkezli Yaklaşım', description: 'Tüm süreç boyunca kişisel tercümanlık ve 7/24 destek sağlıyoruz.' },
];

const teamMembers = [
    { name: 'Dr. Zeynep Aksoy', role: 'Kurucu & Medikal Direktör', photo: 'https://source.unsplash.com/random/100x100/?woman-doctor-smile' },
    { name: 'Emre Çelik', role: 'Uluslararası Operasyon Yöneticisi', photo: 'https://source.unsplash.com/random/100x100/?man-business-smile' },
    { name: 'Maria Santos', role: 'Global Hasta İlişkileri', photo: 'https://source.unsplash.com/random/100x100/?woman-portrait-happy' },
];
// --- ÖRNEK VERİLER SONU ---

// --- BİLEŞEN: AboutUs ---
function AboutUs() {
    const { t } = useTranslation();
    const theme = useTheme();

    return (
        <Container maxWidth="lg" sx={{ py: 5 }}>

            {/* 1. Başlık ve Misyon */}
            <Box textAlign="center" sx={{ mb: 6 }}>
                <GroupIcon sx={{ fontSize: 60, color: 'primary.main' }} />
                <Typography variant="h3" component="h1" gutterBottom sx={{ fontWeight: 'bold', mt: 1 }}>
                    {t('aboutUsTitle', 'Bizi Tanıyın: Sağlığın Güvenilir Adresi')}
                </Typography>
                <Typography variant="h6" color="text.secondary" sx={{ maxWidth: 800, mx: 'auto' }}>
                    {t('aboutUsMission', 'Misyonumuz, dünyanın dört bir yanındaki hastalara Türkiye\'nin üstün kaliteli sağlık hizmetlerini en konforlu ve güvenilir şekilde ulaştırmaktır.')}
                </Typography>
            </Box>

            {/* 2. Vizyon ve Değerler */}
            <Grid container spacing={4} sx={{ mb: 6 }}>
                {/* Vizyon Kartı */}
                <Grid item xs={12} md={4}>
                    <Card sx={{ p: 3, height: '100%', bgcolor: 'secondary.main', color: 'white', boxShadow: theme.shadows[8] }}>
                        <LightbulbIcon sx={{ fontSize: 40, mb: 2 }} />
                        <Typography variant="h5" sx={{ fontWeight: 700, mb: 1 }}>{t('ourVision', 'Vizyonumuz')}</Typography>
                        <Typography variant="body1">
                            {t('visionDescription', 'Sağlık turizminde global çapta lider bir köprü olmak, teknoloji ve insan odaklı çözümlerimizle sektör standartlarını belirlemek.')}
                        </Typography>
                    </Card>
                </Grid>

                {/* Değerler Kartları */}
                {coreValues.map((value, index) => (
                    <Grid item xs={12} sm={6} md={4} key={index}>
                        <Card component={Paper} sx={{ p: 3, height: '100%', boxShadow: theme.shadows[4] }}>
                            <value.icon sx={{ fontSize: 40, mb: 2, color: 'primary.main' }} />
                            <Typography variant="h6" sx={{ fontWeight: 700, mb: 1 }}>{value.title}</Typography>
                            <Typography variant="body2" color="text.secondary">
                                {value.description}
                            </Typography>
                        </Card>
                    </Grid>
                ))}
            </Grid>

            {/* 3. Başarı Metrikleri (Stats) */}
            <Paper sx={{ p: { xs: 3, md: 5 }, bgcolor: theme.palette.primary.light, borderRadius: 2, mb: 6, boxShadow: theme.shadows[4] }}>
                <Grid container spacing={4} textAlign="center">
                    <Grid item xs={6} md={3}>
                        <Typography variant="h3" sx={{ fontWeight: 800, color: 'primary.dark' }}>250+</Typography>
                        <Typography variant="subtitle1" color="text.secondary">{t('successfulTreatments', 'Başarılı Tedavi')}</Typography>
                    </Grid>
                    <Grid item xs={6} md={3}>
                        <Typography variant="h3" sx={{ fontWeight: 800, color: 'primary.dark' }}>20+</Typography>
                        <Typography variant="subtitle1" color="text.secondary">{t('accreditedPartnerHospitals', 'Akredite Ortak Hastane')}</Typography>
                    </Grid>
                    <Grid item xs={6} md={3}>
                        <Typography variant="h3" sx={{ fontWeight: 800, color: 'primary.dark' }}>4.9/5</Typography>
                        <Typography variant="subtitle1" color="text.secondary">{t('patientSatisfactionScore', 'Hasta Memnuniyeti Puanı')}</Typography>
                    </Grid>
                    <Grid item xs={6} md={3}>
                        <Typography variant="h3" sx={{ fontWeight: 800, color: 'primary.dark' }}>12+</Typography>
                        <Typography variant="subtitle1" color="text.secondary">{t('differentServiceLanguages', 'Farklı Hizmet Dili')}</Typography>
                    </Grid>
                </Grid>
            </Paper>

            {/* 4. Ekibimiz */}
            <Box sx={{ mb: 6, textAlign: 'center' }}>
                <Typography variant="h4" sx={{ fontWeight: 700, mb: 4, color: 'text.primary' }}>
                    {t('ourExpertTeam', 'Uzman Ekibimiz')}
                </Typography>
                <Grid container spacing={4} justifyContent="center">
                    {teamMembers.map((member, index) => (
                        <Grid item xs={12} sm={4} key={index}>
                            <Card sx={{ p: 2, boxShadow: theme.shadows[2] }}>
                                <Avatar
                                    alt={member.name}
                                    src={member.photo}
                                    sx={{ width: 100, height: 100, mx: 'auto', mb: 2, border: `3px solid ${theme.palette.secondary.light}` }}
                                />
                                <Typography variant="h6" sx={{ fontWeight: 600 }}>{member.name}</Typography>
                                <Typography variant="body2" color="primary.main">{member.role}</Typography>
                            </Card>
                        </Grid>
                    ))}
                </Grid>
            </Box>

            {/* 5. CTA (Call to Action) */}
            <Paper sx={{ p: { xs: 3, md: 6 }, bgcolor: 'secondary.light', borderRadius: 2, textAlign: 'center' }}>
                <Typography variant="h4" sx={{ fontWeight: 700, mb: 2, color: 'secondary.dark' }}>
                    {t('startYourHealthJourney', 'Sağlık Yolculuğunuza Hemen Başlayın')}
                </Typography>
                <Typography variant="body1" sx={{ mb: 3, color: 'text.secondary' }}>
                    {t('startJourneyDescription', 'Profesyonel ekibimiz size özel tedavi planınızı hazırlamak için hazır. İlk adımı atın ve ücretsiz danışmanlık hizmetimizden yararlanın.')}
                </Typography>
                <Button
                    variant="contained"
                    color="primary"
                    size="large"
                    startIcon={<StarIcon />}
                    onClick={() => alert('Rezervasyon sayfasına yönlendiriliyor...')}
                >
                    {t('getFreeQuote')}
                </Button>
            </Paper>

        </Container>
    );
}

export default AboutUs;