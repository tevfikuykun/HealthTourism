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
import { Link } from 'react-router-dom';

function Home() {
    const theme = useTheme();

    // Özel Hizmet Kartı Bileşeni
    const ServiceCard = ({ icon: Icon, title, description, to }) => (
        <Paper
            sx={{
                p: 3,
                textAlign: 'center',
                height: '100%',
                display: 'flex',
                flexDirection: 'column',
                justifyContent: 'space-between',
                transition: 'transform 0.3s, box-shadow 0.3s',
                '&:hover': {
                    transform: 'translateY(-5px)',
                    boxShadow: theme.shadows[10],
                    borderBottom: `4px solid ${theme.palette.secondary.main}`
                }
            }}
        >
            <Box>
                <Icon color="primary" sx={{ fontSize: 50, mb: 2 }} />
                <Typography variant="h6" gutterBottom sx={{ fontWeight: 600 }}>{title}</Typography>
                <Typography variant="body2" color="text.secondary">{description}</Typography>
            </Box>
            <Button
                component={Link}
                to={to}
                size="small"
                variant="text"
                color="secondary"
                endIcon={<ArrowForwardIcon />}
                sx={{ mt: 2 }}
            >
                Keşfet
            </Button>
        </Paper>
    );

    return (
        <Box>
            {/* 1. Hero Section (Kahraman Bölümü) */}
            <Box
                sx={{
                    bgcolor: 'primary.main',
                    color: 'white',
                    py: { xs: 8, md: 12 },
                    textAlign: 'left',
                    // Arka plan resmi ile modern görünüm
                    backgroundImage: 'linear-gradient(to right, rgba(0, 122, 138, 0.9), rgba(0, 122, 138, 0.4)), url(https://source.unsplash.com/1600x900/?health,medical-travel)',
                    backgroundSize: 'cover',
                    backgroundPosition: 'center',
                }}
            >
                <Container maxWidth="lg">
                    <Grid container spacing={4} alignItems="center">
                        <Grid item xs={12} md={7}>
                            <Typography variant="h2" component="h1" gutterBottom sx={{ fontWeight: 800, lineHeight: 1.2 }}>
                                Global Sağlık Turizminde **Güven** ve **Konfor**
                            </Typography>
                            <Typography variant="h5" sx={{ mb: 4, fontWeight: 300 }}>
                                En seçkin hastaneler, kişiselleştirilmiş tedavi planları ve A'dan Z'ye tüm seyahat organizasyonu.
                            </Typography>
                            <Button
                                variant="contained"
                                color="secondary"
                                size="large"
                                sx={{ py: 1.5, px: 4, fontSize: '1.1rem', mr: 2 }}
                                component={Link}
                                to="/reservations"
                            >
                                Ücretsiz Teklif Alın
                            </Button>
                            <Button
                                variant="outlined"
                                size="large"
                                sx={{ py: 1.5, px: 4, fontSize: '1.1rem', color: 'white', borderColor: 'white', '&:hover': { borderColor: theme.palette.secondary.light } }}
                                component={Link}
                                to="/about"
                            >
                                Neden Biz?
                            </Button>
                        </Grid>
                        {/* Görsel veya video yer tutucusu (md=5 kısmı) burada boş bırakılabilir */}
                    </Grid>
                </Container>
            </Box>

            {/* 2. Temel Hizmetler Bölümü (Cards) */}
            <Container maxWidth="lg" sx={{ py: 8 }}>
                <Typography variant="h4" align="center" sx={{ mb: 6, fontWeight: 700 }}>
                    Sağlık Yolculuğunuz İçin En İyi Çözümler
                </Typography>
                <Grid container spacing={4}>
                    <Grid item xs={12} sm={6} md={3}>
                        <ServiceCard
                            icon={LocalHospitalIcon}
                            title="Akredite Hastaneler"
                            description="Uluslararası standartlara sahip, alanında lider sağlık kuruluşları."
                            to="/hospitals"
                        />
                    </Grid>
                    <Grid item xs={12} sm={6} md={3}>
                        <ServiceCard
                            icon={MedicalServicesIcon}
                            title="Uzman Doktor Seçimi"
                            description="Tecrübeli, tanınmış doktorlardan sizin için özel randevu ve konsültasyon."
                            to="/doctors"
                        />
                    </Grid>
                    <Grid item xs={12} sm={6} md={3}>
                        <ServiceCard
                            icon={HotelIcon}
                            title="Konaklama Desteği"
                            description="Tedavi süreniz boyunca konforlu ve hijyenik otel veya rezidans seçimi."
                            to="/accommodations"
                        />
                    </Grid>
                    <Grid item xs={12} sm={6} md={3}>
                        <ServiceCard
                            icon={FlightTakeoffIcon}
                            title="VIP Transfer & Uçuş"
                            description="Havalimanı karşılama, özel transfer ve tüm seyahat planlama hizmetleri."
                            to="/transfers"
                        />
                    </Grid>
                </Grid>
            </Container>

            {/* 3. Nasıl Çalışır Bölümü (Adımlar) */}
            <Box sx={{ bgcolor: theme.palette.background.default, py: 8 }}>
                <Container maxWidth="md">
                    <Typography variant="h4" align="center" sx={{ mb: 6, fontWeight: 700 }}>
                        Sağlık Yolculuğunuz 3 Kolay Adımda Başlasın
                    </Typography>
                    <Grid container spacing={4} sx={{ textAlign: 'center' }}>

                        {/* Adım 1 */}
                        <Grid item xs={12} md={4}>
                            <Box sx={{ p: 3 }}>
                                <Chip label="ADIM 1" color="secondary" sx={{ mb: 2, fontWeight: 'bold' }} />
                                <CheckCircleOutlineIcon color="primary" sx={{ fontSize: 48, mb: 1 }} />
                                <Typography variant="h6" sx={{ fontWeight: 600 }}>Teklif Talebi ve Analiz</Typography>
                                <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                                    Sağlık durumunuzu ve beklentilerinizi iletin. Ücretsiz, kişisel tedavi planınızı oluşturalım.
                                </Typography>
                            </Box>
                        </Grid>

                        {/* Adım 2 */}
                        <Grid item xs={12} md={4}>
                            <Box sx={{ p: 3 }}>
                                <Chip label="ADIM 2" color="secondary" sx={{ mb: 2, fontWeight: 'bold' }} />
                                <CheckCircleOutlineIcon color="primary" sx={{ fontSize: 48, mb: 1 }} />
                                <Typography variant="h6" sx={{ fontWeight: 600 }}>Onay ve Rezervasyon</Typography>
                                <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                                    Sunulan paketi (tedavi, konaklama, uçuş) onaylayın. Tüm rezervasyonları biz yapalım.
                                </Typography>
                            </Box>
                        </Grid>

                        {/* Adım 3 */}
                        <Grid item xs={12} md={4}>
                            <Box sx={{ p: 3 }}>
                                <Chip label="ADIM 3" color="secondary" sx={{ mb: 2, fontWeight: 'bold' }} />
                                <CheckCircleOutlineIcon color="primary" sx={{ fontSize: 48, mb: 1 }} />
                                <Typography variant="h6" sx={{ fontWeight: 600 }}>Karşılama ve Tedavi</Typography>
                                <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                                    Havaalanında karşılanın ve size özel danışmanınız eşliğinde tedavi sürecinize başlayın.
                                </Typography>
                            </Box>
                        </Grid>
                    </Grid>
                </Container>
            </Box>
        </Box>
    );
}

export default Home;