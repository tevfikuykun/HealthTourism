// src/pages/Packages.jsx
import React, { useState } from 'react';
import { 
    Container, Box, Typography, Grid, Card, CardContent, CardActions,
    Button, Paper, useTheme, Chip, Divider,
    FormControl, InputLabel, Select, MenuItem, TextField,
    Rating, Tooltip, InputAdornment, Slider
} from '@mui/material';
import HealthAndSafetyIcon from '@mui/icons-material/HealthAndSafety';
import LocalHospitalIcon from '@mui/icons-material/LocalHospital';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import VisibilityIcon from '@mui/icons-material/Visibility';
import FaceRetouchingNaturalIcon from '@mui/icons-material/FaceRetouchingNatural';
import StarIcon from '@mui/icons-material/Star';
import { useTranslation } from 'react-i18next';

// --- ÖRNEK VERİLER ---
// Maksimum fiyatı belirlemek için kullanılır
const MAX_PRICE = 6000;

const dummyPackages = [
    {
        id: 1, name: 'Gülüş Tasarımı Premium', category: 'Diş',
        price: 3500, duration: 7, rating: 4.8,
        services: ['20x Porselen Veneer', 'Otel Konaklama (4 Gece)', 'VIP Transfer', 'Kontrol Randevusu'],
        image: 'https://source.unsplash.com/random/400x250/?smile-design', hospital: 'Dental Art Clinic'
    },
    {
        id: 2, name: 'Full Body Lipo + Otel', category: 'Estetik',
        price: 5900, duration: 10, rating: 4.9,
        services: ['360° Liposuction', 'Özel Hastane Odası', 'Ameliyat Sonrası Korse', 'Uçuş Organizasyonu Desteği'],
        image: 'https://source.unsplash.com/random/400x250/?liposuction-surgery', hospital: 'Estetik Excellence'
    },
    {
        id: 3, name: 'LASIK Göz Tedavisi', category: 'Göz',
        price: 1800, duration: 3, rating: 4.7,
        services: ['Göz Muayenesi', 'LASIK Ameliyatı', 'İlaçlar', 'Havaalanı Transferi'],
        image: 'https://source.unsplash.com/random/400x250/?eye-surgery', hospital: 'Optimed Göz Merkezi'
    },
    {
        id: 4, name: 'Saç Ekimi FUE Plus', category: 'Saç',
        price: 2200, duration: 5, rating: 4.6,
        services: ['Maksimum Greft Sayısı', '3 Gün Konaklama', 'İlaç Paketi', 'Özel Şampuan Seti'],
        image: 'https://source.unsplash.com/random/400x250/?hair-transplant', hospital: 'Golden Hair Clinic'
    },
];

const packageCategories = ['Diş', 'Estetik', 'Saç', 'Göz', 'Genel Cerrahi'];
// --- ÖRNEK VERİLER SONU ---


// --- BİLEŞEN: PackageCard (Öncekiyle aynı, sadeleştirildi) ---
const PackageCard = ({ pkg }) => {
    const { t } = useTranslation();
    const theme = useTheme();

    const getIconForCategory = (category) => {
        switch(category) {
            case 'Diş': return <HealthAndSafetyIcon />;
            case 'Estetik': return <FaceRetouchingNaturalIcon />;
            case 'Göz': return <VisibilityIcon />;
            default: return <LocalHospitalIcon />;
        }
    };

    return (
        <Card
            sx={{
                borderRadius: 2,
                boxShadow: theme.shadows[3],
                height: '100%',
                display: 'flex',
                flexDirection: 'column',
                transition: '0.3s',
                '&:hover': { boxShadow: theme.shadows[8] }
            }}
        >
            <Box
                sx={{
                    height: 160,
                    backgroundImage: `url(${pkg.image})`,
                    backgroundSize: 'cover',
                    backgroundPosition: 'center',
                    borderTopLeftRadius: theme.shape.borderRadius,
                    borderTopRightRadius: theme.shape.borderRadius,
                    position: 'relative',
                }}
            >
                <Chip
                    label={pkg.category}
                    color="primary"
                    icon={getIconForCategory(pkg.category)}
                    sx={{ position: 'absolute', top: 10, left: 10, fontWeight: 'bold' }}
                />
            </Box>

            <CardContent sx={{ flexGrow: 1, pb: 1 }}>
                <Typography gutterBottom variant="h6" component="div" sx={{ fontWeight: 700 }}>
                    {pkg.name}
                </Typography>

                <Box sx={{ display: 'flex', alignItems: 'center', mb: 1, color: 'text.secondary' }}>
                    <LocalHospitalIcon fontSize="small" sx={{ mr: 0.5 }} />
                    <Typography variant="body2">{pkg.hospital}</Typography>
                </Box>

                <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                    <Rating name="read-only" value={pkg.rating} readOnly precision={0.1} size="small" />
                    <Typography variant="body2" color="text.secondary" sx={{ ml: 1 }}>({pkg.rating})</Typography>
                </Box>

                <Divider sx={{ my: 1 }} />

                <Typography variant="subtitle2" sx={{ fontWeight: 600, mb: 1 }}>{t('packageContent', 'Paket İçeriği')}:</Typography>
                <Box sx={{ height: 90, overflowY: 'hidden' }}>
                    {pkg.services.map((service, index) => (
                        <Box key={index} sx={{ display: 'flex', alignItems: 'center', mb: 0.5 }}>
                            <CheckCircleIcon color="success" sx={{ fontSize: 16, mr: 1, flexShrink: 0 }} />
                            <Tooltip title={service} placement="top-start">
                                <Typography variant="body2" sx={{ whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
                                    {service}
                                </Typography>
                            </Tooltip>
                        </Box>
                    ))}
                    <Box sx={{ height: 10 }} />
                </Box>
            </CardContent>

            <CardActions sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', p: 2, borderTop: `1px solid ${theme.palette.divider}` }}>
                <Box>
                    <Typography variant="body2" color="text.secondary">Toplam Fiyat:</Typography>
                    <Typography variant="h5" color="secondary" sx={{ fontWeight: 700 }}>
                        €{pkg.price}
                    </Typography>
                    <Typography variant="caption" color="text.secondary">/ {pkg.duration} {t('days', 'Gün')}</Typography>
                </Box>
                <Button
                    variant="contained"
                    color="primary"
                    size="large"
                    onClick={() => alert(`${pkg.name} paketi için teklif alınıyor...`)}
                >
                    {t('getQuote', 'Teklif Al')}
                </Button>
            </CardActions>
        </Card>
    );
};
// --- BİLEŞEN SONU: PackageCard ---


function Packages() {
    const { t } = useTranslation();
    const theme = useTheme();
    // Filtreler güncellendi: minPrice ve maxPrice birleştirildi
    const [filters, setFilters] = useState({
        search: '',
        category: '',
        priceRange: [500, MAX_PRICE], // Slider için başlangıç ve bitiş
        minRating: 4.0
    });

    const handleChange = (event) => {
        const { name, value } = event.target;
        setFilters(prev => ({ ...prev, [name]: value }));
    };

    const handlePriceSliderChange = (event, newValue) => {
        setFilters(prev => ({ ...prev, priceRange: newValue }));
    };

    const handleRatingChange = (event, newValue) => {
        setFilters(prev => ({ ...prev, minRating: newValue || 0 }));
    };

    const filteredPackages = dummyPackages.filter(pkg => {
        const searchMatch = pkg.name.toLowerCase().includes(filters.search.toLowerCase());
        const categoryMatch = filters.category === '' || pkg.category === filters.category;

        // Fiyat filtresi kontrolü düzeltildi
        const priceMatch = pkg.price >= filters.priceRange[0] && pkg.price <= filters.priceRange[1];

        const ratingMatch = pkg.rating >= filters.minRating;

        return searchMatch && categoryMatch && priceMatch && ratingMatch;
    });

    return (
        <Container maxWidth="lg" sx={{ py: 5 }}>
            <Box textAlign="center" sx={{ mb: 5 }}>
                <HealthAndSafetyIcon sx={{ fontSize: 60, color: 'primary.main' }} />
                <Typography variant="h3" component="h1" gutterBottom sx={{ fontWeight: 'bold', mt: 1 }}>
                    Sağlık Paketleri Kataloğu
                </Typography>
                <Typography variant="h6" color="text.secondary">
                    {t('packagesDescription', 'Tedavi, konaklama ve transferi içeren anahtar teslim paketleri inceleyin.')}
                </Typography>
            </Box>

            <Grid container spacing={4}>

                {/* Sol Kolon: Filtreler */}
                <Grid item xs={12} md={3}>
                    <Paper sx={{ p: 3, borderRadius: 2, position: 'sticky', top: 100 }}>
                        <Typography variant="h6" sx={{ fontWeight: 600, mb: 2 }}>{t('filterPackages', 'Paketleri Filtrele')}</Typography>
                        <Divider sx={{ mb: 2 }} />

                        {/* Arama Kutusu */}
                        <TextField
                            fullWidth
                            label={t('searchPackage', 'Paket Adı Ara')}
                            name="search"
                            value={filters.search}
                            onChange={handleChange}
                            variant="outlined"
                            size="small"
                            sx={{ mb: 2 }}
                        />

                        {/* Kategori Filtresi */}
                        <FormControl fullWidth size="small" sx={{ mb: 3 }}>
                            <InputLabel id="category-label">{t('treatmentArea', 'Tedavi Alanı')}</InputLabel>
                            <Select
                                labelId="category-label"
                                name="category"
                                value={filters.category}
                                label={t('treatmentArea', 'Tedavi Alanı')}
                                onChange={handleChange}
                            >
                                <MenuItem value="">Tümü</MenuItem>
                                {packageCategories.map(c => (
                                    <MenuItem key={c} value={c}>{c}</MenuItem>
                                ))}
                            </Select>
                        </FormControl>

                        {/* Fiyat Aralığı Slider */}
                        <Typography variant="subtitle1" gutterBottom sx={{ mt: 1 }}>
                            {t('priceRange', 'Fiyat Aralığı')} (€):
                            <Box component="span" sx={{ fontWeight: 'bold', color: theme.palette.primary.main, ml: 1 }}>
                                €{filters.priceRange[0]} - €{filters.priceRange[1]}
                            </Box>
                        </Typography>
                        <Slider
                            name="priceRange"
                            value={filters.priceRange}
                            onChange={handlePriceSliderChange}
                            valueLabelDisplay="auto"
                            min={500}
                            max={MAX_PRICE}
                            step={100}
                            color="secondary"
                            sx={{ mb: 3 }}
                        />

                        {/* Minimum Rating Filtresi */}
                        <Typography variant="subtitle1" sx={{ fontWeight: 600, mb: 1, mt: 1 }}>Minimum Puan</Typography>
                        <Rating
                            name="minRating"
                            value={filters.minRating}
                            onChange={handleRatingChange}
                            precision={0.5}
                            emptyIcon={<StarIcon style={{ opacity: 0.55 }} fontSize="inherit" />}
                        />
                        <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5, mb: 3 }}>
                            ({filters.minRating} ve üzeri)
                        </Typography>

                        <Button
                            variant="outlined"
                            color="primary"
                            fullWidth
                            onClick={() => setFilters({ search: '', category: '', priceRange: [500, MAX_PRICE], minRating: 4.0 })}
                        >
                            Filtreleri Temizle
                        </Button>
                    </Paper>
                </Grid>

                {/* Sağ Kolon: Paket Listesi */}
                <Grid item xs={12} md={9}>
                    <Box sx={{ mb: 3 }}>
                        <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
                            {filteredPackages.length} Paket Bulundu
                        </Typography>
                    </Box>

                    {/* Paket Kartları */}
                    <Grid container spacing={4}>
                        {filteredPackages.length > 0 ? (
                            filteredPackages.map(pkg => (
                                <Grid item xs={12} sm={6} lg={4} key={pkg.id}>
                                    <PackageCard pkg={pkg} />
                                </Grid>
                            ))
                        ) : (
                            <Grid item xs={12}>
                                <Paper sx={{ p: 4, textAlign: 'center', bgcolor: 'background.default' }}>
                                    <Typography variant="h6" color="text.secondary">
                                        Aradığınız kriterlere uygun sağlık paketi bulunamadı.
                                    </Typography>
                                </Paper>
                            </Grid>
                        )}
                    </Grid>
                </Grid>
            </Grid>
        </Container>
    );
}

export default Packages;