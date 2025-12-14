// src/pages/Hospitals.jsx
import React, { useState, useEffect } from 'react';
import { useQuery } from '@tanstack/react-query';
import { hospitalService } from '../services/api';
import { 
    Container, Box, Typography, Grid, Card, CardContent, CardMedia, 
    TextField, Button, FormControl, InputLabel, Select, MenuItem,
    Rating, Paper, useTheme, Chip, Divider, IconButton
} from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import StarIcon from '@mui/icons-material/Star';
import LocationOnIcon from '@mui/icons-material/LocationOn';
import FavoriteButton from '../components/FavoriteButton';
import Pagination from '../components/Pagination';
import Loading from '../components/Loading';
import EmptyState from '../components/EmptyState';
import { Link as RouterLink } from 'react-router-dom';
import { useTranslation } from 'react-i18next';

const specialties = ['Kardiyoloji', 'Estetik', 'Diş Hekimliği', 'Onkoloji', 'Ortopedi', 'Göz Hastalıkları', 'Genel Cerrahi'];
const cities = ['İstanbul', 'Ankara', 'İzmir', 'Antalya', 'Bursa', 'Muğla', 'Adana'];


// --- BİLEŞEN: HospitalCard ---
const HospitalCard = ({ hospital }) => {
    const { t } = useTranslation();
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
                <Box sx={{ position: 'absolute', top: 10, right: 10 }}>
                    <FavoriteButton itemId={hospital.id} itemType="hospital" />
                </Box>
            </Box>
            <CardContent sx={{ flexGrow: 1 }}>
                <Typography gutterBottom variant="h6" component="div" sx={{ fontWeight: 700 }}>
                    {hospital.name}
                </Typography>
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                    <LocationOnIcon fontSize="small" color="action" sx={{ mr: 0.5 }} />
                    <Typography variant="body2" color="text.secondary">{hospital.city}, {t('turkey', 'Türkiye')}</Typography>
                </Box>

                <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                    <Rating name="read-only" value={hospital.rating} readOnly precision={0.1} size="small" icon={<StarIcon color="warning" />} />
                    <Typography variant="body2" sx={{ ml: 1, fontWeight: 'bold', color: theme.palette.warning.main }}>{hospital.rating}</Typography>
                    <Divider orientation="vertical" flexItem sx={{ mx: 1 }} />
                    <Typography variant="body2" color="text.secondary">{hospital.treatments}+ {t('treatments', 'Tedavi')}</Typography>
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
                    {t('viewDetails', 'Detaylı İncele')}
                </Button>
            </CardContent>
        </Card>
    );
};
// --- BİLEŞEN SONU: HospitalCard ---


function Hospitals() {
    const { t } = useTranslation();
    const [filters, setFilters] = useState({
        search: '',
        city: '',
        specialty: '',
    });
    const [page, setPage] = useState(1);
    const itemsPerPage = 12;

    // Backend API entegrasyonu
    const { data, isLoading, error } = useQuery({
        queryKey: ['hospitals', filters, page],
        queryFn: () => {
            const params = {
                page: page - 1,
                size: itemsPerPage,
            };
            if (filters.search) params.search = filters.search;
            if (filters.city) params.city = filters.city;
            if (filters.specialty) params.specialty = filters.specialty;
            return hospitalService.getAll(params);
        },
    });

    const hospitals = data?.data?.content || data?.data || [];
    const totalPages = data?.data?.totalPages || 1;
    const totalItems = data?.data?.totalElements || hospitals.length;

    const handleChange = (event) => {
        const { name, value } = event.target;
        setFilters(prev => ({ ...prev, [name]: value }));
        setPage(1); // Filtre değiştiğinde sayfayı sıfırla
    };

    const handlePageChange = (newPage) => {
        setPage(newPage);
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    return (
        <Container maxWidth="lg" sx={{ py: 5 }}>
            <Typography variant="h3" component="h1" gutterBottom sx={{ fontWeight: 'bold', mb: 1 }}>
                {t('globalStandards')}
            </Typography>
            <Typography variant="h6" color="text.secondary" sx={{ mb: 4 }}>
                {t('discoverHospitals')}
            </Typography>

            {/* Arama ve Filtreler Alanı */}
            <Paper sx={{ p: 3, mb: 4, borderRadius: 2, boxShadow: 6 }}>
                <Grid container spacing={3} alignItems="center">
                    <Grid item xs={12} md={6}>
                        <TextField
                            fullWidth
                            label={t('searchHospital')}
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
                    <Grid item xs={12} sm={6} md={3}>
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
                </Grid>
            </Paper>

            {isLoading ? (
                <Loading message={t('loadingHospitals')} />
            ) : error ? (
                <EmptyState
                    title="Hata oluştu"
                    description={t('errorLoading')}
                />
            ) : (
                <>
                    <Box sx={{ mb: 3 }}>
                        <Typography variant="subtitle1" sx={{ fontWeight: 'bold' }}>
                            Gösterilen Sonuç: {totalItems} Hastane
                        </Typography>
                    </Box>

                    {/* Hastane Listesi */}
                    {hospitals.length > 0 ? (
                        <>
                            <Grid container spacing={4}>
                                {hospitals.map(hospital => (
                                    <Grid item xs={12} sm={6} lg={4} key={hospital.id}>
                                        <HospitalCard hospital={hospital} />
                                    </Grid>
                                ))}
                            </Grid>
                            <Pagination
                                currentPage={page}
                                totalPages={totalPages}
                                totalItems={totalItems}
                                itemsPerPage={itemsPerPage}
                                onPageChange={handlePageChange}
                            />
                        </>
                    ) : (
                        <EmptyState
                            title="Hastane bulunamadı"
                            description={t('noResultsDescription')}
                        />
                    )}
                </>
            )}
        </Container>
    );
}

export default Hospitals;