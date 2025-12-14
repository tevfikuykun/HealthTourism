// src/pages/Doctors.jsx
import React, { useState } from 'react';
import { 
    Container, Box, Typography, Grid, Card, CardContent, CardMedia, 
    TextField, FormControl, InputLabel, Select, MenuItem,
    Rating, Paper, useTheme, Chip, Divider, IconButton, Button
} from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import StarIcon from '@mui/icons-material/Star';
import LocalHospitalIcon from '@mui/icons-material/LocalHospital';
import SchoolIcon from '@mui/icons-material/School';
import { Link as RouterLink } from 'react-router-dom';
import { useTranslation } from 'react-i18next';

// --- ÖRNEK VERİLER ---
const dummyDoctors = [
    { id: 1, name: 'Dr. Ayşe Yılmaz', specialty: 'Kardiyoloji', city: 'İstanbul', hospital: 'MedGlobal Tıp Merkezi', rating: 4.9, experience: 15, image: 'https://source.unsplash.com/random/400x250/?female-doctor-portrait', slug: 'ayse-yilmaz' },
    { id: 2, name: 'Prof. Dr. Mehmet Öztürk', specialty: 'Estetik', city: 'Ankara', hospital: 'Estetik Cerrahi', rating: 4.7, experience: 25, image: 'https://source.unsplash.com/random/400x250/?male-doctor-portrait', slug: 'mehmet-ozturk' },
    { id: 3, name: 'Uzm. Dr. Canan Demir', specialty: 'Diş Hekimliği', city: 'İzmir', hospital: 'Dent İzmir', rating: 4.8, experience: 10, image: 'https://source.unsplash.com/random/400x250/?dentist-portrait', slug: 'canan-demir' },
    { id: 4, name: 'Op. Dr. Ali Kara', specialty: 'Ortopedi', city: 'Antalya', hospital: 'Akdeniz Hastanesi', rating: 4.6, experience: 18, image: 'https://source.unsplash.com/random/400x250/?surgeon-portrait', slug: 'ali-kara' },
];

const specialties = ['Kardiyoloji', 'Estetik', 'Diş Hekimliği', 'Onkoloji', 'Ortopedi', 'Göz Hastalıkları'];
const cities = ['İstanbul', 'Ankara', 'İzmir', 'Antalya', 'Bursa'];
// --- ÖRNEK VERİLER SONU ---


// --- BİLEŞEN: DoctorCard ---
const DoctorCard = ({ doctor }) => {
    const { t } = useTranslation();
    const theme = useTheme();
    return (
        <Card
            sx={{
                borderRadius: 2,
                boxShadow: theme.shadows[3],
                transition: '0.3s',
                height: '100%',
                '&:hover': { boxShadow: theme.shadows[8] }
            }}
        >
            <CardMedia
                component="img"
                height="200"
                image={doctor.image}
                alt={doctor.name}
                sx={{ objectFit: 'cover' }}
            />
            <CardContent>
                <Typography gutterBottom variant="h5" component="div" sx={{ fontWeight: 700, color: theme.palette.primary.dark }}>
                    {doctor.name}
                </Typography>
                <Chip
                    label={doctor.specialty}
                    size="medium"
                    color="secondary"
                    sx={{ mb: 1, fontWeight: 'bold' }}
                />

                <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                    <StarIcon fontSize="small" color="warning" sx={{ mr: 0.5 }} />
                    <Typography variant="body2" sx={{ mr: 2, fontWeight: 'bold' }}>{doctor.rating} / 5</Typography>

                    <SchoolIcon fontSize="small" color="action" sx={{ mr: 0.5 }} />
                    <Typography variant="body2" color="text.secondary">{doctor.experience}+ {t('years')} {t('experience')}</Typography>
                </Box>

                <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                    <LocalHospitalIcon fontSize="small" color="primary" sx={{ mr: 0.5 }} />
                    <Typography variant="body2" color="text.primary">{doctor.hospital} ({doctor.city})</Typography>
                </Box>

                <Button
                    variant="contained"
                    color="primary"
                    fullWidth
                    component={RouterLink}
                    to={`/doctors/${doctor.slug}`}
                >
                    {t('viewProfile')}
                </Button>
            </CardContent>
        </Card>
    );
};
// --- BİLEŞEN SONU: DoctorCard ---


function Doctors() {
    const { t } = useTranslation();
    const [filters, setFilters] = useState({
        search: '',
        city: '',
        specialty: '',
        minExperience: 0,
    });

    const handleChange = (event) => {
        const { name, value } = event.target;
        setFilters(prev => ({ ...prev, [name]: value }));
    };

    const filteredDoctors = dummyDoctors.filter(d =>
        d.name.toLowerCase().includes(filters.search.toLowerCase()) &&
        (filters.city === '' || d.city === filters.city) &&
        (filters.specialty === '' || d.specialty === filters.specialty) &&
        d.experience >= filters.minExperience
    );

    return (
        <Container maxWidth="lg" sx={{ py: 5 }}>
            <Typography variant="h3" component="h1" gutterBottom sx={{ fontWeight: 'bold', mb: 1 }}>
                {t('expertDoctors')}
            </Typography>
            <Typography variant="h6" color="text.secondary" sx={{ mb: 4 }}>
                {t('expertDoctorsDescription', 'Alanında en az 10 yıl deneyimli, global çapta tanınan hekimler.')}
            </Typography>

            {/* Arama ve Filtreler Alanı */}
            <Paper sx={{ p: 3, mb: 4, borderRadius: 2, boxShadow: 6 }}>
                <Grid container spacing={3} alignItems="center">
                    <Grid item xs={12} md={5}>
                        <TextField
                            fullWidth
                            label={t('searchDoctor', 'Doktor Adı Veya Uzmanlık Alanı Ara')}
                            name="search"
                            value={filters.search}
                            onChange={handleChange}
                            variant="outlined"
                            InputProps={{
                                startAdornment: <SearchIcon sx={{ color: 'action.active', mr: 1 }} />,
                            }}
                        />
                    </Grid>
                    <Grid item xs={12} sm={6} md={3}>
                        <FormControl fullWidth>
                            <InputLabel id="specialty-label">{t('specialty')}</InputLabel>
                            <Select
                                labelId="specialty-label"
                                name="specialty"
                                value={filters.specialty}
                                label={t('specialty')}
                                onChange={handleChange}
                            >
                                <MenuItem value="">Tümü</MenuItem>
                                {specialties.map(s => (
                                    <MenuItem key={s} value={s}>{s}</MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                    </Grid>
                    <Grid item xs={12} sm={6} md={2}>
                        <FormControl fullWidth>
                            <InputLabel id="city-label">{t('city')}</InputLabel>
                            <Select
                                labelId="city-label"
                                name="city"
                                value={filters.city}
                                label={t('city')}
                                onChange={handleChange}
                            >
                                <MenuItem value="">Tümü</MenuItem>
                                {cities.map(c => (
                                    <MenuItem key={c} value={c}>{c}</MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                    </Grid>
                    <Grid item xs={12} sm={6} md={2}>
                        <FormControl fullWidth>
                            <InputLabel id="min-experience-label">Minimum Deneyim</InputLabel>
                            <Select
                                labelId="min-experience-label"
                                name="minExperience"
                                value={filters.minExperience}
                                label="Minimum Deneyim"
                                onChange={handleChange}
                            >
                                <MenuItem value={0}>Tümü</MenuItem>
                                <MenuItem value={10}>10+ Yıl</MenuItem>
                                <MenuItem value={15}>15+ Yıl</MenuItem>
                                <MenuItem value={20}>20+ Yıl</MenuItem>
                            </Select>
                        </FormControl>
                    </Grid>
                </Grid>
            </Paper>

            <Box sx={{ mb: 3 }}>
                <Typography variant="subtitle1" sx={{ fontWeight: 'bold' }}>
                    Gösterilen Sonuç: {filteredDoctors.length} Doktor
                </Typography>
            </Box>

            {/* Doktor Listesi */}
            <Grid container spacing={4}>
                {filteredDoctors.length > 0 ? (
                    filteredDoctors.map(doctor => (
                        <Grid item xs={12} sm={6} md={4} key={doctor.id}>
                            <DoctorCard doctor={doctor} />
                        </Grid>
                    ))
                ) : (
                    <Grid item xs={12}>
                        <Paper sx={{ p: 4, textAlign: 'center', bgcolor: 'background.default' }}>
                            <Typography variant="h6" color="text.secondary">
                                Aradığınız kriterlere uygun doktor bulunamadı. Lütfen filtreleri değiştirin.
                            </Typography>
                        </Paper>
                    </Grid>
                )}
            </Grid>
        </Container>
    );
}

export default Doctors;