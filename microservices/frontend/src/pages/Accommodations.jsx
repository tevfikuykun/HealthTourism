// src/pages/Accommodations.jsx
import React, { useState } from 'react';
import { 
    Container, Box, Typography, Grid, Card, CardContent, CardMedia, 
    TextField, Button, FormControl, InputLabel, Select, MenuItem,
    Rating, Paper, useTheme, Chip, Divider, IconButton, Slider,
    Checkbox, FormGroup, FormControlLabel
} from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import LocationOnIcon from '@mui/icons-material/LocationOn';
import HotelIcon from '@mui/icons-material/Hotel';
import HomeWorkIcon from '@mui/icons-material/HomeWork';
import WifiIcon from '@mui/icons-material/Wifi';
import LocalHospitalIcon from '@mui/icons-material/LocalHospital';
import { Link as RouterLink } from 'react-router-dom';

// --- ÖRNEK VERİLER ---
const dummyAccommodations = [
    { id: 1, name: 'Comfort City Suites', type: 'Daire/Rezidans', city: 'İstanbul', proximity: 5, rating: 4.5, pricePerNight: 75, amenities: ['Wifi', 'Otopark', 'Klima'], image: 'https://source.unsplash.com/random/400x250/?apartment-suite' },
    { id: 2, name: 'Luxury Business Hotel', type: 'Otel', city: 'İstanbul', proximity: 2, rating: 4.8, pricePerNight: 120, amenities: ['Wifi', 'Kahvaltı', 'Havuz'], image: 'https://source.unsplash.com/random/400x250/?luxury-hotel-room' },
    { id: 3, name: 'Ankara Health Residence', type: 'Daire/Rezidans', city: 'Ankara', proximity: 1, rating: 4.7, pricePerNight: 85, amenities: ['Wifi', 'Transfer', 'Hastane Yakın'], image: 'https://source.unsplash.com/random/400x250/?modern-residence' },
    { id: 4, name: 'İzmir Marina Otel', type: 'Otel', city: 'İzmir', proximity: 8, rating: 4.2, pricePerNight: 60, amenities: ['Wifi', 'Kahvaltı'], image: 'https://source.unsplash.com/random/400x250/?izmir-hotel' },
    { id: 5, name: 'Antalya Family Aparts', type: 'Daire/Rezidans', city: 'Antalya', proximity: 3, rating: 4.6, pricePerNight: 95, amenities: ['Wifi', 'Mutfak', 'Klima'], image: 'https://source.unsplash.com/random/400x250/?family-apartment' },
];

const accommodationTypes = ['Otel', 'Daire/Rezidans', 'Özel Daire'];
const cities = ['İstanbul', 'Ankara', 'İzmir', 'Antalya', 'Bursa'];
// --- ÖRNEK VERİLER SONU ---


// --- BİLEŞEN: AccommodationCard ---
const AccommodationCard = ({ item }) => {
    const theme = useTheme();
    const typeIcon = item.type === 'Otel' ? HotelIcon : HomeWorkIcon;

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
            <CardMedia
                component="img"
                height="180"
                image={item.image}
                alt={item.name}
                sx={{ objectFit: 'cover' }}
            />
            <CardContent sx={{ flexGrow: 1 }}>
                <Chip
                    icon={React.createElement(typeIcon)}
                    label={item.type}
                    size="small"
                    color="primary"
                    sx={{ mb: 1, fontWeight: 'bold' }}
                />
                <Typography gutterBottom variant="h6" component="div" sx={{ fontWeight: 700 }}>
                    {item.name}
                </Typography>

                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
                    <Box sx={{ display: 'flex', alignItems: 'center' }}>
                        <LocationOnIcon fontSize="small" color="action" sx={{ mr: 0.5 }} />
                        <Typography variant="body2" color="text.secondary">{item.city}</Typography>
                    </Box>
                    <Box sx={{ display: 'flex', alignItems: 'center' }}>
                        <LocalHospitalIcon fontSize="small" color="error" sx={{ mr: 0.5 }} />
                        <Typography variant="body2" color="error" sx={{ fontWeight: 'bold' }}>{item.proximity} km</Typography>
                    </Box>
                </Box>

                <Rating name="read-only" value={item.rating} readOnly precision={0.1} size="small" sx={{ mb: 1 }} />

                <Box sx={{ mt: 1, mb: 2 }}>
                    {item.amenities.map(amenity => (
                        <Chip key={amenity} label={amenity} size="small" icon={<WifiIcon fontSize="inherit" />} variant="outlined" sx={{ mr: 0.5, mb: 0.5 }} />
                    ))}
                </Box>

                <Box sx={{ mt: 'auto', borderTop: `1px solid ${theme.palette.divider}`, pt: 1 }}>
                    <Typography variant="h5" color="primary" sx={{ fontWeight: 700 }}>
                        ${item.pricePerNight}
                        <Typography component="span" variant="body2" color="text.secondary"> / Gece</Typography>
                    </Typography>
                </Box>

                <Button
                    variant="contained"
                    color="secondary"
                    fullWidth
                    component={RouterLink}
                    to={`/accommodations/${item.id}`}
                    sx={{ mt: 2 }}
                >
                    Teklif Al / Rezervasyon
                </Button>
            </CardContent>
        </Card>
    );
};
// --- BİLEŞEN SONU: AccommodationCard ---


function Accommodations() {
    const theme = useTheme();
    const [filters, setFilters] = useState({
        search: '',
        city: '',
        type: '',
        priceRange: [50, 150], // $50 - $150
        maxProximity: 5, // 5 km
        amenities: [],
    });

    const handleChange = (event) => {
        const { name, value } = event.target;
        setFilters(prev => ({ ...prev, [name]: value }));
    };

    const handleSliderChange = (name) => (event, newValue) => {
        setFilters(prev => ({ ...prev, [name]: newValue }));
    };

    const handleAmenityChange = (event) => {
        const { value, checked } = event.target;
        setFilters(prev => ({
            ...prev,
            amenities: checked
                ? [...prev.amenities, value]
                : prev.amenities.filter((a) => a !== value),
        }));
    };

    const availableAmenities = ['Wifi', 'Kahvaltı', 'Otopark', 'Transfer', 'Mutfak', 'Hastane Yakın'];

    const filteredAccommodations = dummyAccommodations.filter(item => {
        const searchMatch = item.name.toLowerCase().includes(filters.search.toLowerCase());
        const cityMatch = filters.city === '' || item.city === filters.city;
        const typeMatch = filters.type === '' || item.type === filters.type;
        const priceMatch = item.pricePerNight >= filters.priceRange[0] && item.pricePerNight <= filters.priceRange[1];
        const proximityMatch = item.proximity <= filters.maxProximity;
        const amenityMatch = filters.amenities.every(filterAmenity => item.amenities.includes(filterAmenity));

        return searchMatch && cityMatch && typeMatch && priceMatch && proximityMatch && amenityMatch;
    });

    return (
        <Container maxWidth="lg" sx={{ py: 5 }}>
            <Typography variant="h3" component="h1" gutterBottom sx={{ fontWeight: 'bold', mb: 1 }}>
                Konaklama Çözümleri
            </Typography>
            <Typography variant="h6" color="text.secondary" sx={{ mb: 4 }}>
                Tedaviye yakın, konforlu otel ve rezidansları keşfedin.
            </Typography>

            <Grid container spacing={4}>

                {/* Sol Kolon: Filtreler */}
                <Grid item xs={12} md={3}>
                    <Paper sx={{ p: 3, borderRadius: 2, position: 'sticky', top: 100 }}>
                        <Typography variant="h6" sx={{ fontWeight: 600, mb: 2 }}>Filtrele</Typography>
                        <Divider sx={{ mb: 2 }} />

                        {/* Şehir Filtresi */}
                        <FormControl fullWidth size="small" sx={{ mb: 2 }}>
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

                        {/* Tip Filtresi */}
                        <FormControl fullWidth size="small" sx={{ mb: 3 }}>
                            <InputLabel id="type-label">Konaklama Tipi</InputLabel>
                            <Select
                                labelId="type-label"
                                name="type"
                                value={filters.type}
                                label="Konaklama Tipi"
                                onChange={handleChange}
                            >
                                <MenuItem value="">Tümü</MenuItem>
                                {accommodationTypes.map(t => (
                                    <MenuItem key={t} value={t}>{t}</MenuItem>
                                ))}
                            </Select>
                        </FormControl>

                        {/* Fiyat Aralığı Slider */}
                        <Typography variant="subtitle1" gutterBottom sx={{ mt: 1 }}>
                            Fiyat Aralığı (Gecelik):
                            <Box component="span" sx={{ fontWeight: 'bold', color: theme.palette.primary.main, ml: 1 }}>
                                ${filters.priceRange[0]} - ${filters.priceRange[1]}
                            </Box>
                        </Typography>
                        <Slider
                            name="priceRange"
                            value={filters.priceRange}
                            onChange={handleSliderChange('priceRange')}
                            valueLabelDisplay="auto"
                            min={20}
                            max={300}
                            step={5}
                            color="secondary"
                            sx={{ mb: 3 }}
                        />

                        {/* Hastaneye Yakınlık Slider */}
                        <Typography variant="subtitle1" gutterBottom>
                            Hastaneye Uzaklık (Max):
                            <Box component="span" sx={{ fontWeight: 'bold', color: theme.palette.primary.main, ml: 1 }}>
                                {filters.maxProximity} km
                            </Box>
                        </Typography>
                        <Slider
                            name="maxProximity"
                            value={filters.maxProximity}
                            onChange={handleSliderChange('maxProximity')}
                            valueLabelDisplay="auto"
                            min={1}
                            max={10}
                            step={1}
                            sx={{ mb: 3 }}
                        />

                        {/* Olanaklar Checkbox */}
                        <Typography variant="subtitle1" sx={{ fontWeight: 600, mb: 1 }}>Olanaklar</Typography>
                        <FormGroup>
                            {availableAmenities.map(amenity => (
                                <FormControlLabel
                                    key={amenity}
                                    control={
                                        <Checkbox
                                            checked={filters.amenities.includes(amenity)}
                                            onChange={handleAmenityChange}
                                            value={amenity}
                                            size="small"
                                        />
                                    }
                                    label={<Typography variant="body2">{amenity}</Typography>}
                                />
                            ))}
                        </FormGroup>

                        <Button
                            variant="contained"
                            color="primary"
                            fullWidth
                            sx={{ mt: 3 }}
                            onClick={() => setFilters(prev => ({ ...prev, search: '', city: '', type: ''}))} // Filtreleri Sıfırla (Basitleştirilmiş)
                        >
                            Filtreleri Temizle
                        </Button>

                    </Paper>
                </Grid>

                {/* Sağ Kolon: Liste ve Arama */}
                <Grid item xs={12} md={9}>
                    <TextField
                        fullWidth
                        label="Konaklama Adı Veya Anahtar Kelime Ara"
                        name="search"
                        value={filters.search}
                        onChange={handleChange}
                        variant="outlined"
                        size="small"
                        sx={{ mb: 3 }}
                        InputProps={{
                            startAdornment: <SearchIcon sx={{ color: 'action.active', mr: 1 }} />,
                        }}
                    />

                    <Box sx={{ mb: 3 }}>
                        <Typography variant="subtitle1" sx={{ fontWeight: 'bold' }}>
                            Gösterilen Sonuç: {filteredAccommodations.length} Konaklama Yeri
                        </Typography>
                    </Box>

                    {/* Konaklama Listesi */}
                    <Grid container spacing={4}>
                        {filteredAccommodations.length > 0 ? (
                            filteredAccommodations.map(item => (
                                <Grid item xs={12} sm={6} lg={4} key={item.id}>
                                    <AccommodationCard item={item} />
                                </Grid>
                            ))
                        ) : (
                            <Grid item xs={12}>
                                <Paper sx={{ p: 4, textAlign: 'center', bgcolor: 'background.default' }}>
                                    <Typography variant="h6" color="text.secondary">
                                        Aradığınız kriterlere uygun konaklama seçeneği bulunamadı.
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

export default Accommodations;