// src/pages/Hospitals.jsx
import React, { useState, useEffect } from 'react';
import { useQuery } from '@tanstack/react-query';
import { hospitalService } from '../services/api';
import { 
    Container, Box, Typography, Grid, Card, CardContent, CardMedia, 
    TextField, Button, FormControl, InputLabel, Select, MenuItem,
    Rating, Paper, useTheme, Chip, Divider, IconButton, Avatar, Tooltip
} from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import StarIcon from '@mui/icons-material/Star';
import LocationOnIcon from '@mui/icons-material/LocationOn';
import VerifiedIcon from '@mui/icons-material/Verified';
import SecurityIcon from '@mui/icons-material/Security';
import MedicalServicesIcon from '@mui/icons-material/MedicalServices';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import FavoriteButton from '../components/FavoriteButton';
import Pagination from '../components/Pagination';
import { HospitalCardSkeleton } from '../components/Skeleton';
import EmptyState from '../components/EmptyState';
import { Link as RouterLink, useSearchParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { motion, AnimatePresence } from 'framer-motion';

const specialties = ['Kardiyoloji', 'Estetik', 'Diş Hekimliği', 'Onkoloji', 'Ortopedi', 'Göz Hastalıkları', 'Genel Cerrahi'];
const cities = ['İstanbul', 'Ankara', 'İzmir', 'Antalya', 'Bursa', 'Muğla', 'Adana'];

// Accreditation badge icons mapping
const accreditationIcons = {
    'JCI': VerifiedIcon,
    'ISO 9001:2015': SecurityIcon,
    'WHO': MedicalServicesIcon,
    'CE': CheckCircleIcon,
    'Default': CheckCircleIcon,
};

const getAccreditationIcon = (acc) => {
    const Icon = accreditationIcons[acc] || accreditationIcons['Default'];
    return Icon;
};


// --- BİLEŞEN: HospitalCard ---
const HospitalCard = ({ hospital }) => {
    const { t } = useTranslation();
    const theme = useTheme();
    
    return (
        <motion.div
            layout
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, scale: 0.95 }}
            transition={{ duration: 0.3 }}
            style={{ height: '100%' }}
        >
            <Card
                component={motion.div}
                whileHover={{ 
                    scale: 1.02,
                    transition: { duration: 0.2 }
                }}
                sx={{
                    borderRadius: 2,
                    boxShadow: theme.shadows[3],
                    transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
                    height: '100%',
                    display: 'flex',
                    flexDirection: 'column',
                    overflow: 'hidden',
                    '&:hover': { 
                        boxShadow: theme.shadows[8],
                        '& .hospital-image': {
                            transform: 'scale(1.05)',
                        }
                    }
                }}
            >
                <Box sx={{ position: 'relative', overflow: 'hidden' }}>
                    <CardMedia
                        component="img"
                        height="180"
                        image={hospital.image}
                        alt={hospital.name}
                        className="hospital-image"
                        sx={{ 
                            objectFit: 'cover',
                            transition: 'transform 0.5s cubic-bezier(0.4, 0, 0.2, 1)',
                        }}
                    />
                    <Chip
                        label={hospital.specialty}
                        size="small"
                        color="primary"
                        sx={{
                            position: 'absolute',
                            top: 10,
                            left: 10,
                            fontWeight: 'bold',
                            backdropFilter: 'blur(10px)',
                            bgcolor: 'rgba(79, 70, 229, 0.9)',
                        }}
                    />
                    <Box sx={{ position: 'absolute', top: 10, right: 10 }}>
                        <FavoriteButton itemId={hospital.id} itemType="hospital" />
                    </Box>
                </Box>
                <CardContent sx={{ flexGrow: 1, display: 'flex', flexDirection: 'column' }}>
                    <Typography gutterBottom variant="h6" component="div" sx={{ fontWeight: 700, mb: 1 }}>
                        {hospital.name}
                    </Typography>
                    <Box sx={{ display: 'flex', alignItems: 'center', mb: 1.5 }}>
                        <LocationOnIcon fontSize="small" color="action" sx={{ mr: 0.5 }} />
                        <Typography variant="body2" color="text.secondary">
                            {hospital.city}, {t('turkey', 'Türkiye')}
                        </Typography>
                    </Box>

                    <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                        <Rating 
                            name="read-only" 
                            value={hospital.rating} 
                            readOnly 
                            precision={0.1} 
                            size="small" 
                            icon={<StarIcon sx={{ color: theme.palette.warning.main }} />}
                            emptyIcon={<StarIcon sx={{ color: theme.palette.action.disabled }} />}
                        />
                        <Typography variant="body2" sx={{ ml: 1, fontWeight: 'bold', color: theme.palette.warning.main }}>
                            {hospital.rating}
                        </Typography>
                        <Divider orientation="vertical" flexItem sx={{ mx: 1.5, height: 16 }} />
                        <Typography variant="body2" color="text.secondary">
                            {hospital.treatments}+ {t('treatments', 'Tedavi')}
                        </Typography>
                    </Box>

                    {/* Accreditation Badges with Icons */}
                    <Box sx={{ mt: 1, mb: 2, display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
                        {hospital.accreditations.map(acc => {
                            const Icon = getAccreditationIcon(acc);
                            return (
                                <Tooltip key={acc} title={acc} arrow>
                                    <Chip
                                        icon={<Icon sx={{ fontSize: '16px !important' }} />}
                                        label={acc}
                                        size="small"
                                        color="primary"
                                        variant="outlined"
                                        sx={{
                                            fontWeight: 600,
                                            '& .MuiChip-icon': {
                                                color: theme.palette.primary.main,
                                            },
                                            '&:hover': {
                                                bgcolor: `${theme.palette.primary.main}15`,
                                                borderColor: theme.palette.primary.main,
                                            }
                                        }}
                                    />
                                </Tooltip>
                            );
                        })}
                    </Box>

                    <Button
                        variant="contained"
                        color="secondary"
                        fullWidth
                        component={RouterLink}
                        to={`/hospitals/${hospital.slug}`}
                        sx={{ 
                            mt: 'auto',
                            fontWeight: 600,
                            textTransform: 'none',
                        }}
                    >
                        {t('viewDetails', 'Detaylı İncele')}
                    </Button>
                </CardContent>
            </Card>
        </motion.div>
    );
};
// --- BİLEŞEN SONU: HospitalCard ---

// Export HospitalCard for use in other components
export { HospitalCard };


function Hospitals() {
    const { t } = useTranslation();
    const [searchParams] = useSearchParams();
    
    // Initialize filters from URL params
    const [filters, setFilters] = useState({
        search: searchParams.get('search') || '',
        city: searchParams.get('city') || '',
        specialty: searchParams.get('specialty') || '',
    });
    
    const [page, setPage] = useState(parseInt(searchParams.get('page')) || 1);
    const itemsPerPage = 12;

    // Sync URL params when filters or page change
    useEffect(() => {
        const params = new URLSearchParams();
        if (filters.search) params.set('search', filters.search);
        if (filters.city) params.set('city', filters.city);
        if (filters.specialty) params.set('specialty', filters.specialty);
        if (page > 1) params.set('page', page.toString());
        
        // Update URL without page reload
        const newUrl = params.toString() ? `?${params.toString()}` : window.location.pathname;
        window.history.replaceState({}, '', newUrl);
    }, [filters, page]);

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
                <>
                    <Box sx={{ mb: 3 }}>
                        <Typography variant="subtitle1" sx={{ fontWeight: 'bold' }}>
                            {t('loadingHospitals', 'Yükleniyor...')}
                        </Typography>
                    </Box>
                    <Grid container spacing={4}>
                        {Array.from({ length: itemsPerPage }).map((_, index) => (
                            <Grid item xs={12} sm={6} lg={4} key={index}>
                                <HospitalCardSkeleton />
                            </Grid>
                        ))}
                    </Grid>
                </>
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
                            <AnimatePresence mode="wait">
                                <Grid container spacing={4}>
                                    {hospitals.map(hospital => (
                                        <Grid item xs={12} sm={6} lg={4} key={hospital.id}>
                                            <HospitalCard hospital={hospital} />
                                        </Grid>
                                    ))}
                                </Grid>
                            </AnimatePresence>
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