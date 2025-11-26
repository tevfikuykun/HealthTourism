// src/pages/Hospitals.jsx
import React, { useState } from 'react';
import { 
    Container, Box, Typography, Grid, Card, CardContent, CardMedia, 
    TextField, Button, FormControl, InputLabel, Select, MenuItem,
    Rating, Paper, useTheme, Chip, Divider, IconButton
} from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import StarIcon from '@mui/icons-material/Star';
import LocationOnIcon from '@mui/icons-material/LocationOn';
import FavoriteBorderIcon from '@mui/icons-material/FavoriteBorder';
import { Link as RouterLink } from 'react-router-dom';

// --- ÖRNEK VERİLER ---
const dummyHospitals = [
    { id: 1, name: 'MedGlobal Tıp Merkezi', city: 'İstanbul', specialty: 'Kardiyoloji', rating: 4.8, accreditations: ['JCI', 'ISO'], image: 'https://source.unsplash.com/random/400x250/?cardiology-hospital', slug: 'medglobal-tıp-merkezi', treatments: 120 },
    { id: 2, name: 'Ankara Estetik Cerrahi', city: 'Ankara', specialty: 'Estetik', rating: 4.6, accreditations: ['TÜRKAK'], image: 'https://source.unsplash.com/random/400x250/?aesthetic-clinic', slug: 'ankara-estetik-cerrahi', treatments: 85 },
    { id: 3, name: 'İzmir Ortopedi Merkezi', city: 'İzmir', specialty: 'Ortopedi', rating: 4.9, accreditations: ['JCI'], image: 'https://source.unsplash.com/random/400x250/?orthopedic-hospital', slug: 'izmir-ortopedi-merkezi', treatments: 150 },
    { id: 4, name: 'Akdeniz Göz Kliniği', city: 'Antalya', specialty: 'Göz Hastalıkları', rating: 4.5, accreditations: ['ISO'], image: 'https://source.unsplash.com/random/400x250/?eye-clinic', slug: 'akdeniz-göz-kliniği', treatments: 90 },
    { id: 5, name: 'Bursa Onkoloji Enstitüsü', city: 'Bursa', specialty: 'Onkoloji', rating: 4.7, accreditations: ['JCI', 'ESC'], image: 'https://source.unsplash.com/random/400x250/?oncology-center', slug: 'bursa-onkoloji-enstitüsü', treatments: 70 },
];

const specialties = ['Kardiyoloji', 'Estetik', 'Diş Hekimliği', 'Onkoloji', 'Ortopedi', 'Göz Hastalıkları', 'Genel Cerrahi'];
const cities = ['İstanbul', 'Ankara', 'İzmir', 'Antalya', 'Bursa', 'Muğla', 'Adana'];
// --- ÖRNEK VERİLER SONU ---


// --- BİLEŞEN: HospitalCard ---
const HospitalCard = ({ hospital }) => {
    const theme = useTheme();
    return (
        <Card
            sx={{
                borderRadius: 2,
                boxShadow: theme.shadows[3],
                transition: '0.3s',
                height: '100%',
                display: 'flex',
                flexDirection: 'column',
                '&:hover': { boxShadow: theme.shadows[8] }
            }}
        >
            <Box sx={{ position: 'relative' }}>
                <CardMedia
                    component="img"
                    height="180"
                    image={hospital.image}
                    alt={hospital.name}
                    sx={{ objectFit: 'cover' }}
                />
                <Chip
                    label={hospital.specialty}
                    size="small"
                    color="primary"
                    sx={{
                        position: 'absolute',
                        top: 10,
                        left: 10,
                        fontWeight: 'bold'
                    }}
                />
                <IconButton
                    aria-label="favorilere ekle"
                    sx={{
                        position: 'absolute',
                        top: 10,
                        right: 10,
                        bgcolor: 'rgba(255, 255, 255, 0.8)',
                        '&:hover': { bgcolor: 'white' }
                    }}
                >
                    <FavoriteBorderIcon color="action" />
                </IconButton>
            </Box>
            <CardContent sx={{ flexGrow: 1 }}>
                <Typography gutterBottom variant="h6" component="div" sx={{ fontWeight: 700 }}>
                    {hospital.name}
                </Typography>
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                    <LocationOnIcon fontSize="small" color="action" sx={{ mr: 0.5 }} />
                    <Typography variant="body2" color="text.secondary">{hospital.city}, Türkiye</Typography>
                </Box>

                <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                    <Rating name="read-only" value={hospital.rating} readOnly precision={0.1} size="small" icon={<StarIcon color="warning" />} />
                    <Typography variant="body2" sx={{ ml: 1, fontWeight: 'bold', color: theme.palette.warning.main }}>{hospital.rating}</Typography>
                    <Divider orientation="vertical" flexItem sx={{ mx: 1 }} />
                    <Typography variant="body2" color="text.secondary">{hospital.treatments}+ Tedavi</Typography>
                </Box>

                <Box sx={{ mt: 1, mb: 2 }}>
                    {hospital.accreditations.map(acc => (
                        <Chip key={acc} label={acc} size="small" color="primary" variant="outlined" sx={{ mr: 0.5, mb: 0.5 }} />
                    ))}
                </Box>

                <Button
                    variant="contained"
                    color="secondary"
                    fullWidth
                    component={RouterLink}
                    to={`/hospitals/${hospital.slug}`}
                    sx={{ mt: 'auto' }} // Butonun daima en altta olmasını sağlar
                >
                    Detaylı İncele
                </Button>
            </CardContent>
        </Card>
    );
};
// --- BİLEŞEN SONU: HospitalCard ---


function Hospitals() {
    const [filters, setFilters] = useState({
        search: '',
        city: '',
        specialty: '',
    });

    const handleChange = (event) => {
        const { name, value } = event.target;
        setFilters(prev => ({ ...prev, [name]: value }));
    };

    const filteredHospitals = dummyHospitals.filter(h =>
        h.name.toLowerCase().includes(filters.search.toLowerCase()) &&
        (filters.city === '' || h.city === filters.city) &&
        (filters.specialty === '' || h.specialty === filters.specialty)
    );

    return (
        <Container maxWidth="lg" sx={{ py: 5 }}>
            <Typography variant="h3" component="h1" gutterBottom sx={{ fontWeight: 'bold', mb: 1 }}>
                Global Standartlarda Hastaneler
            </Typography>
            <Typography variant="h6" color="text.secondary" sx={{ mb: 4 }}>
                500'den fazla akredite hastane ve kliniği keşfedin.
            </Typography>

            {/* Arama ve Filtreler Alanı */}
            <Paper sx={{ p: 3, mb: 4, borderRadius: 2, boxShadow: 6 }}>
                <Grid container spacing={3} alignItems="center">
                    <Grid item xs={12} md={6}>
                        <TextField
                            fullWidth
                            label="Hastane Adı Veya Anahtar Kelime Ara"
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
                            <InputLabel id="specialty-label">Uzmanlık Alanı</InputLabel>
                            <Select
                                labelId="specialty-label"
                                name="specialty"
                                value={filters.specialty}
                                label="Uzmanlık Alanı"
                                onChange={handleChange}
                            >
                                <MenuItem value="">Tümü</MenuItem>
                                {specialties.map(s => (
                                    <MenuItem key={s} value={s}>{s}</MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                    </Grid>
                    <Grid item xs={12} sm={6} md={3}>
                        <FormControl fullWidth>
                            <InputLabel id="city-label">Şehir</InputLabel>
                            <Select
                                labelId="city-label"
                                name="city"
                                value={filters.city}
                                label="Şehir"
                                onChange={handleChange}
                            >
                                <MenuItem value="">Tümü</MenuItem>
                                {cities.map(c => (
                                    <MenuItem key={c} value={c}>{c}</MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                    </Grid>
                </Grid>
            </Paper>

            <Box sx={{ mb: 3 }}>
                <Typography variant="subtitle1" sx={{ fontWeight: 'bold' }}>
                    Gösterilen Sonuç: {filteredHospitals.length} Hastane
                </Typography>
            </Box>

            {/* Hastane Listesi */}
            <Grid container spacing={4}>
                {filteredHospitals.length > 0 ? (
                    filteredHospitals.map(hospital => (
                        <Grid item xs={12} sm={6} lg={4} key={hospital.id}>
                            <HospitalCard hospital={hospital} />
                        </Grid>
                    ))
                ) : (
                    <Grid item xs={12}>
                        <Paper sx={{ p: 4, textAlign: 'center', bgcolor: 'background.default' }}>
                            <Typography variant="h6" color="text.secondary">
                                Aradığınız kriterlere uygun hastane bulunamadı. Lütfen filtreleri değiştirin.
                            </Typography>
                        </Paper>
                    </Grid>
                )}
            </Grid>
        </Container>
    );
}

export default Hospitals;