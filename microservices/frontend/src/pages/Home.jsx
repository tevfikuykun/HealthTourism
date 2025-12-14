// src/pages/Home.jsx
import React from 'react';
import {
    Container, Typography, Box, Grid, Button, Paper,
    useTheme, Chip, Divider
} from '@mui/material';
import LocalHospitalIcon from '@mui/icons-material/LocalHospital';
import MedicalServicesIcon from '@mui/icons-material/MedicalServices';
import FlightTakeoffIcon from '@mui/icons-material/FlightTakeoff';
import HotelIcon from '@mui/icons-material/Hotel';
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
import FavoriteIcon from '@mui/icons-material/Favorite';
import VpnKeyIcon from '@mui/icons-material/VpnKey';
import { Link } from 'react-router-dom';
import { useTranslation } from '../i18n';

function Home() {
    const { t } = useTranslation();
    const theme = useTheme();

    // Özel Hizmet Kartı Bileşeni - Modern ve Sol Vurgulu
    const ServiceCard = ({ icon: Icon, title, description, to }) => (
        <Paper
            elevation={3}
            sx={{
                p: 3,
                textAlign: 'left',
                height: '100%',
                display: 'flex',
                flexDirection: 'column',
                justifyContent: 'flex-start',
                transition: 'all 0.3s ease',
                borderRadius: '12px',
                borderLeft: `5px solid ${theme.palette.primary.main}`,
                '&:hover': {
                    transform: 'translateY(-3px)',
                    boxShadow: theme.shadows[8],
                }
            }}
        >
            <Icon color="primary" sx={{ fontSize: 40, mb: 2 }} />
            <Typography variant="h6" gutterBottom sx={{ fontWeight: 700 }}>{title}</Typography>
            <Typography variant="body2" color="text.secondary" sx={{ flexGrow: 1, mb: 2 }}>{description}</Typography>
            <Button
                component={Link}
                to={to}
                size="small"
                variant="outlined"
                color="secondary"
                endIcon={<ArrowForwardIcon />}
                sx={{ mt: 'auto' }}
            >
                {t('explore', 'Keşfet')}
            </Button>
        </Paper>
    );

    // Neden Bizi Seçmelisiniz Kartı
    const WhyUsCard = ({ icon: Icon, title, description }) => (
        <Box sx={{ textAlign: 'center', p: 2 }}>
            <Icon color="secondary" sx={{ fontSize: 60, mb: 1.5 }} />
            <Typography variant="h6" sx={{ fontWeight: 700, mb: 1 }}>{title}</Typography>
            <Typography variant="body2" color="text.secondary">{description}</Typography>
        </Box>
    );

    return (
        <Box>
            {/* 1. Hero Section (Kahraman Bölümü) - Yeni Başlık ve Sadeleştirilmiş CTA */}
            <Box
                sx={{
                    bgcolor: 'primary.dark',
                    color: 'white',
                    py: { xs: 8, md: 15 },
                    textAlign: 'left',
                    // Yeni ve daha soyut bir arka plan resmi
                    backgroundImage: 'linear-gradient(to right, rgba(0, 70, 100, 0.95), rgba(0, 70, 100, 0.5)), url(https://source.unsplash.com/1600x900/?healthy-city,modern-hospital)',
                    backgroundSize: 'cover',
                    backgroundPosition: 'center',
                }}
            >
                <Container maxWidth="lg">
                    <Grid container spacing={4} alignItems="center">
                        <Grid item xs={12} md={8}>
                            {/* YENİ SLOGAN */}
                            <Typography variant="h2" component="h1" gutterBottom sx={{ fontWeight: 800, lineHeight: 1.2 }}>
                                {t('heroTitle', 'Kişiselleştirilmiş Sağlık Yolculuğunuz: Profesyonel Tedavi, Konforlu Konaklama')}
                            </Typography>
                            {/* YENİ ALT BAŞLIK */}
                            <Typography variant="h5" sx={{ mb: 4, fontWeight: 300 }}>
                                {t('heroSubtitle', 'Dünya standartlarında uzmanları ve iyileşmenize odaklı lüks konaklama seçeneklerini keşfedin.')}
                            </Typography>

                            {/* SADELEŞTİRİLMİŞ CTA BUTONLARI */}
                            <Button
                                variant="contained"
                                color="secondary"
                                size="large"
                                sx={{ py: 1.5, px: 6, fontSize: '1.2rem', mr: 2 }}
                                component={Link}
                                to="/reservations" // Teklif/Rezervasyon sayfasına yönlendirildi.
                            >
                                {t('getFreeQuote', 'Ücretsiz Tedavi Teklifi Alın')}
                            </Button>
                            <Button
                                variant="outlined"
                                size="large"
                                sx={{ py: 1.5, px: 4, fontSize: '1.1rem', color: 'white', borderColor: 'white', '&:hover': { borderColor: theme.palette.secondary.light } }}
                                component={Link}
                                to="/hospitals"
                            >
                                {t('browseHospitals', 'Anlaşmalı Hastaneleri Keşfet')}
                            </Button>
                        </Grid>
                    </Grid>
                </Container>
            </Box>

            {/* 2. Temel Hizmetler Bölümü (Cards) */}
            <Container maxWidth="lg" sx={{ py: 10 }}>
                <Chip label="HİZMETLERİMİZ" color="secondary" sx={{ mb: 1, fontWeight: 'bold' }} />
                <Typography variant="h4" sx={{ mb: 2, fontWeight: 700 }}>
                    Sağlık ve Konfor Odağında 4 Ana Alan
                </Typography>
                <Typography variant="body1" color="text.secondary" sx={{ mb: 6 }}>
                    Global çapta akredite hastaneler ve size özel seyahat/konaklama paketleri sunuyoruz.
                </Typography>
                <Grid container spacing={4}>
                    <Grid item xs={12} sm={6} md={3}>
                        <ServiceCard
                            icon={LocalHospitalIcon}
                            title="Akredite Hastaneler"
                            description="Uluslararası standartlara sahip, alanında lider sağlık kuruluşları ve klinikler."
                            to="/hospitals"
                        />
                    </Grid>
                    <Grid item xs={12} sm={6} md={3}>
                        <ServiceCard
                            icon={MedicalServicesIcon}
                            title="Kişisel Tedavi Planı"
                            description="Tecrübeli uzman doktorlardan randevu ve size özel oluşturulmuş tedavi paketleri."
                            to="/doctors"
                        />
                    </Grid>
                    <Grid item xs={12} sm={6} md={3}>
                        <ServiceCard
                            icon={HotelIcon}
                            title="Tedavi Boyunca Konaklama"
                            description="İyileşme sürecinizi destekleyen, hastane yakını konforlu otel veya rezidanslar."
                            to="/accommodations"
                        />
                    </Grid>
                    <Grid item xs={12} sm={6} md={3}>
                        <ServiceCard
                            icon={FlightTakeoffIcon}
                            title="A'dan Z'ye Seyahat Desteği"
                            description="Uçuş, VIP transfer, havalimanı karşılama ve tedavi sonrası takip organizasyonu."
                            to="/transfers"
                        />
                    </Grid>
                </Grid>
            </Container>

            {/* 3. Neden Bizi Seçmelisiniz (Güven Bölümü) */}
            <Box sx={{ bgcolor: theme.palette.primary.light, py: 8 }}>
                <Container maxWidth="lg">
                    <Typography variant="h4" align="center" sx={{ mb: 2, fontWeight: 700, color: 'primary.dark' }}>
                        Neden Sağlık Yolculuğunuzda Bizi Tercih Etmelisiniz?
                    </Typography>
                    <Divider sx={{ mb: 6, width: '100px', mx: 'auto', bgcolor: theme.palette.secondary.main, height: 3 }} />
                    <Grid container spacing={4}>
                        <Grid item xs={12} md={4}>
                            <WhyUsCard
                                icon={FavoriteIcon}
                                title="Kişisel Sağlık Danışmanı"
                                description="Tüm süreç boyunca (öncesi, sırası ve sonrası) size özel, tek bir danışman eşlik eder."
                            />
                        </Grid>
                        <Grid item xs={12} md={4}>
                            <WhyUsCard
                                icon={CheckCircleOutlineIcon}
                                title="Akredite Kalite Güvencesi"
                                description="Çalıştığımız tüm hastaneler ve doktorlar, uluslararası akreditasyon standartlarına sahiptir."
                            />
                        </Grid>
                        <Grid item xs={12} md={4}>
                            <WhyUsCard
                                icon={VpnKeyIcon}
                                title="Şeffaf ve Sabit Fiyatlar"
                                description="Gizli ücret yok. Tedavi, konaklama ve transfer dahil, size özel sabit fiyat garantisi."
                            />
                        </Grid>
                    </Grid>
                </Container>
            </Box>

            {/* 4. Nasıl Çalışır Bölümü (Adımlar) */}
            <Box sx={{ py: 10 }}>
                <Container maxWidth="md">
                    <Typography variant="h4" align="center" sx={{ mb: 6, fontWeight: 700 }}>
                        Sağlık Yolculuğunuz Sadece 3 Adımda
                    </Typography>
                    <Grid container spacing={4} sx={{ textAlign: 'center' }}>

                        {/* Adım 1 */}
                        <Grid item xs={12} md={4}>
                            <Box sx={{ p: 3, border: `1px solid ${theme.palette.divider}`, borderRadius: '8px' }}>
                                <Chip label="ADIM 1" color="secondary" sx={{ mb: 2, fontWeight: 'bold' }} />
                                <CheckCircleOutlineIcon color="primary" sx={{ fontSize: 48, mb: 1 }} />
                                <Typography variant="h6" sx={{ fontWeight: 600 }}>Teklif ve Analiz</Typography>
                                <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                                    Beklentinizi iletin. Ücretsiz, kişisel tedavi planınızı uzmanlarımız oluştursun.
                                </Typography>
                            </Box>
                        </Grid>
                        {/* Adım 2 */}
                        <Grid item xs={12} md={4}>
                            <Box sx={{ p: 3, border: `1px solid ${theme.palette.divider}`, borderRadius: '8px' }}>
                                <Chip label="ADIM 2" color="secondary" sx={{ mb: 2, fontWeight: 'bold' }} />
                                <CheckCircleOutlineIcon color="primary" sx={{ fontSize: 48, mb: 1 }} />
                                <Typography variant="h6" sx={{ fontWeight: 600 }}>Rezervasyon Onayı</Typography>
                                <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                                    Paketi (tedavi, konaklama, seyahat) onaylayın. Tüm rezervasyonları biz tamamlayalım.
                                </Typography>
                            </Box>
                        </Grid>
                        {/* Adım 3 */}
                        <Grid item xs={12} md={4}>
                            <Box sx={{ p: 3, border: `1px solid ${theme.palette.divider}`, borderRadius: '8px' }}>
                                <Chip label="ADIM 3" color="secondary" sx={{ mb: 2, fontWeight: 'bold' }} />
                                <CheckCircleOutlineIcon color="primary" sx={{ fontSize: 48, mb: 1 }} />
                                <Typography variant="h6" sx={{ fontWeight: 600 }}>Seyahat ve Tedavi</Typography>
                                <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                                    Havaalanında karşılanın ve size özel danışmanınız eşliğinde iyileşme sürecinize başlayın.
                                </Typography>
                            </Box>
                        </Grid>
                    </Grid>
                    <Box sx={{ textAlign: 'center', mt: 6 }}>
                        <Button
                            variant="contained"
                            color="primary"
                            size="large"
                            sx={{ py: 1.5, px: 6, fontSize: '1.1rem' }}
                            component={Link}
                            to="/reservations"
                        >
                            Şimdi Başlayın
                        </Button>
                    </Box>
                </Container>
            </Box>
        </Box>
    );
}

export default Home;