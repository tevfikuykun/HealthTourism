// src/pages/DoctorDetail.jsx
import React, { useState } from 'react';
import {
    Container, Box, Typography, Grid, Card, CardContent,
    Button, Paper, useTheme, Divider, Rating, Chip, List,
    ListItem, ListItemIcon, ListItemText, Avatar,
    Tabs, Tab, TextField
} from '@mui/material';
import StarIcon from '@mui/icons-material/Star';
import LocalHospitalIcon from '@mui/icons-material/LocalHospital';
import SchoolIcon from '@mui/icons-material/School';
import EventAvailableIcon from '@mui/icons-material/EventAvailable';
import VerifiedUserIcon from '@mui/icons-material/VerifiedUser';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import ThumbUpIcon from '@mui/icons-material/ThumbUp';
import MedicalServicesIcon from '@mui/icons-material/MedicalServices';
import { Link as RouterLink, useParams } from 'react-router-dom';
import { doctorService } from '../services/api';
import { useQuery } from '@tanstack/react-query';
import LoadingState from '../components/LoadingState/LoadingState';
import ErrorState from '../components/ErrorState/ErrorState';
import SEOHead from '../components/SEO/SEOHead';
import { useTranslation } from 'react-i18next';

// --- ÖRNEK VERİLER ---
const dummyDoctor = {
    id: 101,
    name: 'Prof. Dr. Ali Yılmaz',
    specialty: 'İnvaziv Kardiyoloji Uzmanı',
    hospital: 'Anadolu Tıp Merkezi',
    city: 'İstanbul',
    rating: 4.9,
    reviews: 185,
    experience: '25 Yıl',
    education: ['İstanbul Tıp Fakültesi (Lisans)', 'Cleveland Clinic, ABD (Uzmanlık)', 'Hacettepe Üniversitesi (Doçentlik)'],
    certifications: ['Avrupa Kardiyoloji Board Sertifikası', 'JCI Tıbbi Mükemmeliyet Ödülü'],
    areasOfExpertise: ['Minimal İnvaziv Koroner Girişimler', 'Aritmi Tedavisi', 'Kalp Kapakçığı Onarımı', 'Ekokardiyografi'],
    profilePicture: 'https://source.unsplash.com/random/100x100/?doctor-portrait-male',
    about: 'Prof. Dr. Ali Yılmaz, Türkiye’nin önde gelen kardiyologlarından olup, özellikle karmaşık kalp damar hastalıklarının tedavisinde minimal invaziv yöntemleri kullanmaktadır. Yurt içi ve yurt dışı birçok bilimsel yayını bulunmaktadır. Hastalarıyla kurduğu güçlü iletişim ve etik yaklaşımıyla tanınır.',
};

const dummyReviews = [
    { id: 1, author: 'Ahmet E.', date: '10.05.2024', rating: 5.0, comment: 'Hayatımı kurtaran doktor. Süreç çok şeffaftı, ekibi de harika.' },
    { id: 2, author: 'Maria S.', date: '15.04.2024', rating: 4.5, comment: 'Yurtdışından geldim, tercüman desteği harikaydı. Tedavim beklediğimden hızlı bitti.' },
    { id: 3, author: 'Zeynep K.', date: '01.03.2024', rating: 5.0, comment: 'İlgili ve alanında çok bilgili bir uzman. Gönül rahatlığıyla tavsiye ederim.' },
];
// --- ÖRNEK VERİLER SONU ---

// Sekmeler için A11yProps
function a11yProps(index) {
    return { id: `simple-tab-${index}`, 'aria-controls': `simple-tabpanel-${index}` };
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
                <Box sx={{ py: 3 }}>{children}</Box>
            )}
        </div>
    );
}


// --- BİLEŞEN: DoctorDetail ---
function DoctorDetail() {
    const { t } = useTranslation();
    const { slug } = useParams();
    const theme = useTheme();
    const [tabValue, setTabValue] = useState(0);

    const { data: doctor, isLoading, error } = useQuery({
        queryKey: ['doctor', slug],
        queryFn: () => doctorService.getBySlug(slug),
        enabled: !!slug,
        placeholderData: dummyDoctor
    });

    const handleTabChange = (event, newValue) => { setTabValue(newValue); };

    if (isLoading) return <LoadingState />;
    if (error) return <ErrorState error={error} />;
    if (!doctor) return <ErrorState error={{ message: 'Doktor bulunamadı' }} />;

    return (
        <>
            <SEOHead
                title={`${doctor.name} - ${t('doctorProfile', 'Doktor Profili')}`}
                description={doctor.about || `${doctor.name} doktor profili, uzmanlık alanları ve hasta yorumları`}
                keywords={`${doctor.name}, doktor, ${doctor.specialty}, sağlık turizmi`}
            />
            <Container maxWidth="lg" sx={{ py: 5 }}>

            <Grid container spacing={4}>

                {/* Sol Kolon: Profil ve Detaylar */}
                <Grid item xs={12} md={8}>

                    {/* Üst Profil Kartı */}
                    <Paper sx={{ p: { xs: 2, md: 4 }, mb: 4, borderRadius: 2, boxShadow: theme.shadows[4] }}>
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 3, flexWrap: 'wrap' }}>
                            <Avatar
                                alt={doctor.name}
                                src={doctor.profilePicture}
                                sx={{ width: 100, height: 100, border: `3px solid ${theme.palette.secondary.main}` }}
                            />
                            <Box>
                                <Typography variant="h4" component="h1" sx={{ fontWeight: 700, color: theme.palette.primary.dark }}>
                                    {doctor.name}
                                </Typography>
                                <Typography variant="h6" color="secondary.main" sx={{ mb: 1 }}>
                                    {doctor.specialty}
                                </Typography>
                                <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                                    <Rating name="read-only" value={doctor.rating} readOnly precision={0.1} size="medium" />
                                    <Typography variant="body1" color="text.secondary" sx={{ ml: 1, fontWeight: 600 }}>
                                        {doctor.rating} ({doctor.reviews} {t('reviews', 'Yorum')})
                                    </Typography>
                                </Box>
                                <Chip
                                    icon={<LocalHospitalIcon />}
                                    label={doctor.hospital}
                                    color="primary"
                                    component={RouterLink}
                                    to={`/hospitals/${doctor.hospital.replace(/\s/g, '-')}`}
                                    clickable
                                />
                            </Box>
                        </Box>
                        <Divider sx={{ my: 3 }} />
                        <Typography variant="body1" color="text.secondary" sx={{ mb: 2 }}>
                            **{t('specialty', 'Uzmanlık Alanı')}:** {doctor.specialty}
                        </Typography>
                        <Typography variant="body1" color="text.secondary">
                            **{t('experience', 'Deneyim')}:** {doctor.experience}
                        </Typography>
                    </Paper>

                    {/* Detay Sekmeleri */}
                    <Paper sx={{ borderRadius: 2, boxShadow: theme.shadows[3] }}>
                        <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
                            <Tabs value={tabValue} onChange={handleTabChange} aria-label="doktor detay sekmeleri" variant="scrollable" scrollButtons="auto">
                                <Tab label={t('aboutAndResume', 'Hakkında & Özgeçmiş')} {...a11yProps(0)} />
                                <Tab label={t('specialtiesAndCertifications', 'Uzmanlıklar & Sertifikalar')} {...a11yProps(1)} />
                                <Tab label={t('patientReviews', 'Hasta Yorumları')} {...a11yProps(2)} />
                            </Tabs>
                        </Box>

                        {/* Sekme 1: Hakkında & Özgeçmiş */}
                        <TabPanel value={tabValue} index={0}>
                            <Typography variant="h5" sx={{ fontWeight: 600, mb: 2 }}>{t('about', 'Hakkında')}</Typography>
                            <Typography variant="body1" sx={{ mb: 3 }}>{doctor.about}</Typography>

                            <Typography variant="h5" sx={{ fontWeight: 600, mb: 2 }}>{t('educationInfo', 'Eğitim Bilgileri')}</Typography>
                            <List>
                                {doctor.education.map((edu, index) => (
                                    <ListItem key={index}>
                                        <ListItemIcon><SchoolIcon color="secondary" /></ListItemIcon>
                                        <ListItemText primary={edu} />
                                    </ListItem>
                                ))}
                            </List>
                        </TabPanel>

                        {/* Sekme 2: Uzmanlıklar & Sertifikalar */}
                        <TabPanel value={tabValue} index={1}>
                            <Typography variant="h5" sx={{ fontWeight: 600, mb: 2 }}>{t('specialtyAreas', 'Uzmanlık Alanları')}</Typography>
                            <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                                {doctor.areasOfExpertise.map((area, index) => (
                                    <Chip
                                        key={index}
                                        icon={<MedicalServicesIcon />}
                                        label={area}
                                        color="primary"
                                        variant="outlined"
                                        sx={{ p: 1, fontWeight: 'bold' }}
                                    />
                                ))}
                            </Box>
                            <Divider sx={{ my: 3 }} />
                            <Typography variant="h5" sx={{ fontWeight: 600, mb: 2 }}>{t('certifications', 'Sertifikalar')}</Typography>
                            <List>
                                {doctor.certifications.map((cert, index) => (
                                    <ListItem key={index}>
                                        <ListItemIcon><VerifiedUserIcon color="success" /></ListItemIcon>
                                        <ListItemText primary={<Typography sx={{ fontWeight: 500 }}>{cert}</Typography>} />
                                    </ListItem>
                                ))}
                            </List>
                        </TabPanel>

                        {/* Sekme 3: Hasta Yorumları */}
                        <TabPanel value={tabValue} index={2}>
                            <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
                                <Typography variant="h4" sx={{ fontWeight: 700, mr: 1 }}>{doctor.rating}</Typography>
                                <Rating value={doctor.rating} readOnly precision={0.1} size="large" />
                                <Typography variant="h6" color="text.secondary" sx={{ ml: 1 }}>({doctor.reviews} {t('reviews', 'Yorum')})</Typography>
                            </Box>

                            {dummyReviews.map((review) => (
                                <Paper key={review.id} sx={{ p: 2, mb: 2, borderLeft: `4px solid ${theme.palette.secondary.main}` }}>
                                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
                                        <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                            <ThumbUpIcon color="success" sx={{ fontSize: 18, mr: 1 }} />
                                            <Typography variant="subtitle2" sx={{ fontWeight: 700 }}>{review.author}</Typography>
                                        </Box>
                                        <Typography variant="caption" color="text.secondary">{review.date}</Typography>
                                    </Box>
                                    <Rating value={review.rating} readOnly size="small" sx={{ mb: 1 }} />
                                    <Typography variant="body2">{review.comment}</Typography>
                                </Paper>
                            ))}
                        </TabPanel>
                    </Paper>
                </Grid>

                {/* Sağ Kolon: Randevu Formu */}
                <Grid item xs={12} md={4}>
                    <Card sx={{ p: 3, borderRadius: 2, boxShadow: theme.shadows[6], bgcolor: 'primary.light', position: 'sticky', top: 100 }}>
                        <Box textAlign="center" sx={{ mb: 3 }}>
                            <EventAvailableIcon sx={{ fontSize: 40, color: 'primary.dark' }} />
                            <Typography variant="h5" sx={{ fontWeight: 700, color: 'primary.dark', mt: 1 }}>
                                {t('appointmentRequestForm', 'Randevu Talep Formu')}
                            </Typography>
                            <Typography variant="body2" color="text.secondary">
                                {t('appointmentRequestDescription', '{name} ile görüşmek için hemen talep oluşturun.', { name: doctor.name })}
                            </Typography>
                        </Box>

                        <form onSubmit={(e) => { e.preventDefault(); alert(`Randevu talebi ${doctor.name} için alındı!`); }}>
                            <TextField
                                fullWidth
                                label="Adınız Soyadınız"
                                size="small"
                                sx={{ mb: 2 }}
                                required
                            />
                            <TextField
                                fullWidth
                                label="E-posta Adresiniz"
                                type="email"
                                size="small"
                                sx={{ mb: 2 }}
                                required
                            />
                            <TextField
                                fullWidth
                                label="Telefon Numaranız"
                                size="small"
                                sx={{ mb: 2 }}
                            />
                            <TextField
                                fullWidth
                                label={t('preferredAppointmentDate', 'Tercih Edilen Randevu Tarihi')}
                                type="date"
                                size="small"
                                sx={{ mb: 2 }}
                                InputLabelProps={{ shrink: true }}
                            />
                            <TextField
                                fullWidth
                                label="Şikayetiniz / Tedavi İsteğiniz"
                                multiline
                                rows={3}
                                size="small"
                                sx={{ mb: 2 }}
                                required
                            />

                            <Button
                                type="submit"
                                variant="contained"
                                color="secondary"
                                fullWidth
                                size="large"
                                startIcon={<AccessTimeIcon />}
                                sx={{ mt: 1, fontWeight: 'bold' }}
                            >
                                Hemen Talep Oluştur
                            </Button>
                        </form>
                    </Card>
                </Grid>
            </Grid>
        </Container>
        </>
    );
}

export default DoctorDetail;