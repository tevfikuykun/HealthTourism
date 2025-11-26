// src/pages/HospitalDetail.jsx
import React, { useState } from 'react';
import {
    Container, Box, Typography, Grid, Card, CardContent,
    Button, Paper, useTheme, Divider, Rating, Chip, Tabs, Tab,
    List, ListItem, ListItemIcon, ListItemText, CardMedia, Avatar
} from '@mui/material';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import StarIcon from '@mui/icons-material/Star';
import PeopleIcon from '@mui/icons-material/People';
import LocalHospitalIcon from '@mui/icons-material/LocalHospital';
import SecurityIcon from '@mui/icons-material/Security';
import LocationOnIcon from '@mui/icons-material/LocationOn';
import LanguageIcon from '@mui/icons-material/Language';
import DirectionsCarIcon from '@mui/icons-material/DirectionsCar';
import { Link as RouterLink } from 'react-router-dom';

// --- ÖRNEK VERİLER ---
const dummyHospital = {
    id: 1,
    name: 'Anadolu Tıp Merkezi',
    city: 'İstanbul',
    rating: 4.9,
    reviews: 125,
    description: '1995 yılından beri hizmet veren Anadolu Tıp Merkezi, özellikle uluslararası hastalara sunduğu ileri teknoloji tedavi yöntemleri ve konforlu hasta odaları ile tanınmaktadır. JCI ve ISO 9001 akreditasyonlarına sahiptir. 5 farklı dilde hizmet verilmektedir.',
    accreditations: ['JCI Onaylı', 'ISO 9001 Sertifikalı', 'Sağlık Bakanlığı Lisanslı'],
    specialties: ['Kardiyoloji', 'Estetik Cerrahi', 'Nöroloji', 'Ortopedi', 'Diş Hekimliği'],
    amenities: ['VIP Transfer', 'Otel Ortaklığı', 'Tercüman Desteği', 'Helikopter Pisti'],
    gallery: [
        'https://source.unsplash.com/random/800x450/?hospital-facade',
        'https://source.unsplash.com/random/400x250/?hospital-room',
        'https://source.unsplash.com/random/400x250/?hospital-surgery'
    ],
};

const dummyDoctors = [
    { id: 101, name: 'Prof. Dr. Ali Yılmaz', specialty: 'Kardiyoloji', rating: 4.9 },
    { id: 102, name: 'Op. Dr. Elif Kaya', specialty: 'Estetik Cerrahi', rating: 4.8 },
    { id: 103, name: 'Uzm. Dr. Mehmet Özen', specialty: 'Nöroloji', rating: 4.7 },
];
// --- ÖRNEK VERİLER SONU ---

// Sekmeler için A11yProps
function a11yProps(index) {
    return {
        id: `simple-tab-${index}`,
        'aria-controls': `simple-tabpanel-${index}`,
    };
}

// Sekme İçeriği Bileşeni
function TabPanel(props) {
    const { children, value, index, ...other } = props;
    return (
        <div
            role="tabpanel"
            hidden={value !== index}
            id={`simple-tabpanel-${index}`}
            aria-labelledby={`simple-tab-${index}`}
            {...other}
        >
            {value === index && (
                <Box sx={{ py: 3 }}>
                    {children}
                </Box>
            )}
        </div>
    );
}

// --- BİLEŞEN: HospitalDetail ---
function HospitalDetail() {
    const theme = useTheme();
    const [tabValue, setTabValue] = useState(0);

    const handleTabChange = (event, newValue) => {
        setTabValue(newValue);
    };

    const hospital = dummyHospital;

    return (
        <Container maxWidth="lg" sx={{ py: 5 }}>

            {/* 1. Üst Başlık ve Galeri */}
            <Paper sx={{ mb: 4, borderRadius: 2, overflow: 'hidden', boxShadow: theme.shadows[6] }}>
                {/* Ana Resim */}
                <CardMedia
                    component="img"
                    height="450"
                    image={hospital.gallery[0]}
                    alt={`${hospital.name} Dış Görünüm`}
                    sx={{ objectFit: 'cover' }}
                />

                {/* Başlık Bloğu */}
                <Box sx={{ p: { xs: 2, md: 4 }, bgcolor: 'white' }}>
                    <Typography variant="h3" component="h1" gutterBottom sx={{ fontWeight: 'bold', color: theme.palette.primary.dark }}>
                        {hospital.name}
                    </Typography>

                    {/* Bilgi Çipleri ve Puanlama */}
                    <Box sx={{ display: 'flex', flexWrap: 'wrap', alignItems: 'center', mb: 2, gap: 2 }}>
                        <Rating name="read-only" value={hospital.rating} readOnly precision={0.1} size="large" />
                        <Typography variant="h6" sx={{ fontWeight: 600 }}>
                            {hospital.rating} / 5.0
                        </Typography>
                        <Chip
                            icon={<PeopleIcon />}
                            label={`${hospital.reviews} Yorum`}
                            variant="outlined"
                        />
                        <Chip
                            icon={<LocationOnIcon />}
                            label={hospital.city}
                            color="secondary"
                        />
                    </Box>

                    {/* Kısa Açıklama */}
                    <Typography variant="body1" color="text.secondary" sx={{ mb: 3 }}>
                        {hospital.description}
                    </Typography>

                    <Button
                        variant="contained"
                        color="secondary"
                        size="large"
                        startIcon={<LocalHospitalIcon />}
                    >
                        Bu Hastane İçin Teklif Al
                    </Button>
                </Box>
            </Paper>

            {/* 2. Detay Sekmeleri */}
            <Grid container spacing={4}>

                {/* Sol Kolon: Sekmeli İçerik */}
                <Grid item xs={12} md={8}>
                    <Paper sx={{ borderRadius: 2, boxShadow: theme.shadows[3] }}>
                        <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
                            <Tabs value={tabValue} onChange={handleTabChange} aria-label="hastane detay sekmeleri" variant="scrollable" scrollButtons="auto">
                                <Tab label="Akreditasyon ve Uzmanlıklar" {...a11yProps(0)} />
                                <Tab label="Galeri ve Olanaklar" {...a11yProps(1)} />
                                <Tab label="İlgili Doktorlar" {...a11yProps(2)} />
                            </Tabs>
                        </Box>

                        {/* Sekme 1: Akreditasyon ve Uzmanlıklar */}
                        <TabPanel value={tabValue} index={0}>
                            <Typography variant="h5" sx={{ fontWeight: 600, mb: 2 }}>Akreditasyonlar</Typography>
                            <List>
                                {hospital.accreditations.map((acc, index) => (
                                    <ListItem key={index}>
                                        <ListItemIcon>
                                            <SecurityIcon color="primary" />
                                        </ListItemIcon>
                                        <ListItemText primary={<Typography variant="body1" sx={{ fontWeight: 500 }}>{acc}</Typography>} />
                                    </ListItem>
                                ))}
                            </List>
                            <Divider sx={{ my: 3 }} />
                            <Typography variant="h5" sx={{ fontWeight: 600, mb: 2 }}>Uzmanlık Alanları</Typography>
                            <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                                {hospital.specialties.map((spec, index) => (
                                    <Chip
                                        key={index}
                                        label={spec}
                                        color="secondary"
                                        variant="outlined"
                                        sx={{ p: 1, fontWeight: 'bold' }}
                                    />
                                ))}
                            </Box>
                        </TabPanel>

                        {/* Sekme 2: Galeri ve Olanaklar */}
                        <TabPanel value={tabValue} index={1}>
                            <Typography variant="h5" sx={{ fontWeight: 600, mb: 2 }}>Hastane Olanakları</Typography>
                            <Grid container spacing={2}>
                                {hospital.amenities.map((amenity, index) => (
                                    <Grid item xs={6} sm={4} key={index}>
                                        <Chip
                                            icon={<CheckCircleIcon color="success" />}
                                            label={amenity}
                                            variant="outlined"
                                            sx={{ width: '100%', justifyContent: 'flex-start', p: 1 }}
                                        />
                                    </Grid>
                                ))}
                            </Grid>
                            <Divider sx={{ my: 3 }} />
                            <Typography variant="h5" sx={{ fontWeight: 600, mb: 2 }}>Hastane İçi Görüntüler</Typography>
                            <Grid container spacing={2}>
                                {hospital.gallery.slice(1).map((imgUrl, index) => (
                                    <Grid item xs={12} sm={6} key={index}>
                                        <CardMedia
                                            component="img"
                                            height="200"
                                            image={imgUrl}
                                            alt={`Galeri Görüntüsü ${index + 1}`}
                                            sx={{ borderRadius: 1 }}
                                        />
                                    </Grid>
                                ))}
                            </Grid>
                        </TabPanel>

                        {/* Sekme 3: İlgili Doktorlar */}
                        <TabPanel value={tabValue} index={2}>
                            <Typography variant="h5" sx={{ fontWeight: 600, mb: 2 }}>Bu Merkezdeki Uzmanlar</Typography>
                            <List>
                                {dummyDoctors.map((doctor) => (
                                    <Paper key={doctor.id} sx={{ mb: 2, p: 2, display: 'flex', alignItems: 'center', gap: 2, transition: '0.2s', '&:hover': { bgcolor: 'grey.50' } }}>
                                        <Avatar sx={{ width: 60, height: 60, bgcolor: 'secondary.main' }}>
                                            {doctor.name[0]}
                                        </Avatar>
                                        <Box sx={{ flexGrow: 1 }}>
                                            <Typography variant="subtitle1" sx={{ fontWeight: 700 }}>
                                                {doctor.name}
                                            </Typography>
                                            <Typography variant="body2" color="text.secondary">
                                                {doctor.specialty}
                                            </Typography>
                                            <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                                <Rating name="read-only" value={doctor.rating} readOnly precision={0.1} size="small" />
                                                <Typography variant="caption" color="text.secondary" sx={{ ml: 1 }}>({doctor.rating})</Typography>
                                            </Box>
                                        </Box>
                                        <Button
                                            variant="outlined"
                                            size="small"
                                            component={RouterLink}
                                            to={`/doctors/${doctor.id}`} // Doktor detay sayfasına yönlendirme
                                        >
                                            İncele
                                        </Button>
                                    </Paper>
                                ))}
                            </List>
                        </TabPanel>
                    </Paper>
                </Grid>

                {/* Sağ Kolon: Harita ve İletişim Formu */}
                <Grid item xs={12} md={4}>
                    <Card sx={{ p: 3, borderRadius: 2, mb: 3 }}>
                        <Typography variant="h6" sx={{ fontWeight: 700, mb: 2, color: 'primary.main' }}>
                            Bize Ulaşın
                        </Typography>
                        <List dense>
                            <ListItem>
                                <ListItemIcon><LocationOnIcon color="action" /></ListItemIcon>
                                <ListItemText primary="Konum" secondary={`${hospital.city} - Türkiye`} />
                            </ListItem>
                            <ListItem>
                                <ListItemIcon><LanguageIcon color="action" /></ListItemIcon>
                                <ListItemText primary="Hizmet Dili" secondary="Türkçe, İngilizce, Arapça, Almanca, Rusça" />
                            </ListItem>
                            <ListItem>
                                <ListItemIcon><DirectionsCarIcon color="action" /></ListItemIcon>
                                <ListItemText primary="Transfer Hizmeti" secondary="Havaalanından direk ulaşım" />
                            </ListItem>
                        </List>
                        <Button
                            variant="contained"
                            color="secondary"
                            fullWidth
                            sx={{ mt: 2 }}
                            onClick={() => alert('İletişim Formu açılacak...')}
                        >
                            Ücretsiz Danışmanlık Al
                        </Button>
                    </Card>

                    {/* Harita Yer Tutucu */}
                    <Paper sx={{ height: 250, display: 'flex', alignItems: 'center', justifyContent: 'center', bgcolor: 'grey.200', borderRadius: 2 }}>
                        <Typography variant="subtitle1" color="text.secondary">
                            Google Haritası Yer Tutucusu
                        </Typography>
                    </Paper>
                </Grid>
            </Grid>
        </Container>
    );
}

export default HospitalDetail;