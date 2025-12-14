// src/pages/CarRentals.jsx
import React, { useState } from 'react';
import { 
    Container, Box, Typography, Grid, Card, CardContent,
    TextField, Button, FormControl, InputLabel, Select, MenuItem,
    Paper, useTheme, Divider, RadioGroup, FormControlLabel, Radio,
    InputAdornment, IconButton, Checkbox
} from '@mui/material';
import DirectionsCarFilledIcon from '@mui/icons-material/DirectionsCarFilled';
import LocationOnIcon from '@mui/icons-material/LocationOn';
import EventIcon from '@mui/icons-material/Event';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import CalendarTodayIcon from '@mui/icons-material/CalendarToday';
import { useTranslation } from 'react-i18next';

// --- ÖRNEK VERİLER ---
const rentalLocations = [
    'İstanbul Havalimanı (IST)', 'Sabiha Gökçen Havalimanı (SAW)',
    'Ankara Esenboğa Havalimanı (ESB)', 'İzmir Adnan Menderes Havalimanı (ADB)',
    'Otel: Swissôtel The Bosphorus', 'Otel: Hilton Ankara'
];
// --- ÖRNEK VERİLER SONU ---

// --- BİLEŞEN: CarRentalForm ---
const CarRentalForm = () => {
    const { t } = useTranslation();
    const theme = useTheme();
    const [formData, setFormData] = useState({
        pickUpLocation: '',
        dropOffLocation: '',
        pickUpDate: '',
        dropOffDate: '',
        pickUpTime: '10:00',
        dropOffTime: '10:00',
        isSameDropOff: true,
        driverAge: 30
    });

    const [isSearching, setIsSearching] = useState(false);

    const handleChange = (event) => {
        const { name, value, checked, type } = event.target;
        setFormData(prev => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value
        }));
    };

    const handleSearch = (e) => {
        e.preventDefault();
        setIsSearching(true);

        console.log("Araç Aranıyor:", formData);

        // Arama sonucunu simüle etmek için 2 saniye bekleme
        setTimeout(() => {
            setIsSearching(false);
            alert(`Araç arama talebi alındı. Alış Yeri: ${formData.pickUpLocation}. (Gerçek arama için API entegrasyonu gereklidir.)`);
        }, 2000);
    };

    return (
        <Paper sx={{ p: { xs: 3, md: 5 }, borderRadius: 2, boxShadow: theme.shadows[5] }}>
            <Typography variant="h5" sx={{ fontWeight: 700, mb: 3, color: theme.palette.primary.dark }}>
                {t('carRentalSearch', 'Araç Kiralama Arama')}
            </Typography>

            <form onSubmit={handleSearch}>
                <Grid container spacing={3}>

                    {/* 1. Alış Yeri */}
                    <Grid item xs={12} sm={6}>
                        <FormControl fullWidth size="small" required>
                            <InputLabel id="pickup-label">{t('pickUpLocation', 'Alış Yeri')}</InputLabel>
                            <Select
                                labelId="pickup-label"
                                name="pickUpLocation"
                                value={formData.pickUpLocation}
                                label={t('pickUpLocation', 'Alış Yeri')}
                                onChange={handleChange}
                                startAdornment={<InputAdornment position="start"><LocationOnIcon fontSize="small" /></InputAdornment>}
                            >
                                <MenuItem value="">{t('select', 'Seçiniz')}</MenuItem>
                                {rentalLocations.map(loc => (
                                    <MenuItem key={loc} value={loc}>{loc}</MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                    </Grid>

                    {/* 2. Bırakış Yeri */}
                    <Grid item xs={12} sm={6}>
                        <FormControlLabel
                            control={
                                <Checkbox
                                    checked={formData.isSameDropOff}
                                    onChange={handleChange}
                                    name="isSameDropOff"
                                    color="primary"
                                />
                            }
                            label={t('sameDropOffLocation', 'Bırakış Yeri, Alış Yeri ile Aynı')}
                            sx={{ mb: 1 }}
                        />
                        {!formData.isSameDropOff && (
                            <FormControl fullWidth size="small" required>
                                <InputLabel id="dropoff-label">{t('dropOffLocation', 'Bırakış Yeri')}</InputLabel>
                                <Select
                                    labelId="dropoff-label"
                                    name="dropOffLocation"
                                    value={formData.dropOffLocation}
                                    label={t('dropOffLocation', 'Bırakış Yeri')}
                                    onChange={handleChange}
                                    startAdornment={<InputAdornment position="start"><LocationOnIcon fontSize="small" /></InputAdornment>}
                                >
                                    <MenuItem value="">{t('select', 'Seçiniz')}</MenuItem>
                                    {rentalLocations.map(loc => (
                                        <MenuItem key={loc} value={loc}>{loc}</MenuItem>
                                    ))}
                                </Select>
                            </FormControl>
                        )}
                        {formData.isSameDropOff && (
                            <TextField
                                fullWidth
                                disabled
                                label={t('dropOffLocation', 'Bırakış Yeri')}
                                size="small"
                                value={t('sameAsPickUp', 'Alış Yeri ile Aynı')}
                                InputProps={{
                                    startAdornment: <InputAdornment position="start"><LocationOnIcon fontSize="small" /></InputAdornment>,
                                }}
                            />
                        )}
                    </Grid>

                    <Divider sx={{ my: 2, width: '100%', mx: 3 }} />

                    {/* 3. Alış Tarihi ve Saati */}
                    <Grid item xs={6} sm={3}>
                        <TextField
                            fullWidth
                            name="pickUpDate"
                            label={t('pickupDate', 'Alış Tarihi')}
                            type="date"
                            size="small"
                            value={formData.pickUpDate}
                            onChange={handleChange}
                            InputLabelProps={{ shrink: true }}
                            InputProps={{
                                startAdornment: <InputAdornment position="start"><CalendarTodayIcon fontSize="small" /></InputAdornment>,
                            }}
                            required
                        />
                    </Grid>
                    <Grid item xs={6} sm={3}>
                        <TextField
                            fullWidth
                            name="pickUpTime"
                            label={t('pickupTime', 'Alış Saati')}
                            type="time"
                            size="small"
                            value={formData.pickUpTime}
                            onChange={handleChange}
                            InputLabelProps={{ shrink: true }}
                            InputProps={{
                                startAdornment: <InputAdornment position="start"><AccessTimeIcon fontSize="small" /></InputAdornment>,
                            }}
                            required
                        />
                    </Grid>

                    {/* 4. Bırakış Tarihi ve Saati */}
                    <Grid item xs={6} sm={3}>
                        <TextField
                            fullWidth
                            name="dropOffDate"
                            label={t('dropoffDate', 'Bırakış Tarihi')}
                            type="date"
                            size="small"
                            value={formData.dropOffDate}
                            onChange={handleChange}
                            InputLabelProps={{ shrink: true }}
                            InputProps={{
                                startAdornment: <InputAdornment position="start"><CalendarTodayIcon fontSize="small" /></InputAdornment>,
                            }}
                            required
                        />
                    </Grid>
                    <Grid item xs={6} sm={3}>
                        <TextField
                            fullWidth
                            name="dropOffTime"
                            label={t('dropoffTime', 'Bırakış Saati')}
                            type="time"
                            size="small"
                            value={formData.dropOffTime}
                            onChange={handleChange}
                            InputLabelProps={{ shrink: true }}
                            InputProps={{
                                startAdornment: <InputAdornment position="start"><AccessTimeIcon fontSize="small" /></InputAdornment>,
                            }}
                            required
                        />
                    </Grid>

                    {/* 5. Sürücü Yaşı */}
                    <Grid item xs={12}>
                        <TextField
                            name="driverAge"
                            label={t('driverAge', 'Sürücü Yaşı')}
                            type="number"
                            size="small"
                            value={formData.driverAge}
                            onChange={handleChange}
                            sx={{ maxWidth: 150 }}
                            InputProps={{ inputProps: { min: 18, max: 99 } }}
                            required
                        />
                        <Typography variant="caption" color="text.secondary" sx={{ ml: 1 }}>
                            {t('ageLimitNote', 'Bazı araç grupları için yaş sınırı 21 veya 25 olabilir.')}
                        </Typography>
                    </Grid>

                    {/* 6. Arama Butonu */}
                    <Grid item xs={12}>
                        <Button
                            type="submit"
                            variant="contained"
                            color="secondary"
                            fullWidth
                            size="large"
                            sx={{ py: 1.5, fontWeight: 'bold', mt: 2 }}
                            disabled={isSearching}
                        >
                            {isSearching ? t('searchingCars', 'Araçlar Aranıyor...') : t('searchCarsGetPrice', 'Araç Ara ve Fiyat Al')}
                        </Button>
                    </Grid>
                </Grid>
            </form>
        </Paper>
    );
};
// --- BİLEŞEN SONU: CarRentalForm ---


function CarRentals() {
    const { t } = useTranslation();
    const theme = useTheme();

    return (
        <Container maxWidth="lg" sx={{ py: 5 }}>
            <Box textAlign="center" sx={{ mb: 5 }}>
                <DirectionsCarFilledIcon sx={{ fontSize: 60, color: 'primary.main' }} />
                <Typography variant="h3" component="h1" gutterBottom sx={{ fontWeight: 'bold', mt: 1 }}>
                    {t('airportAndCityCarRental', 'Havalimanı ve Şehir İçi Araç Kiralama')}
                </Typography>
                <Typography variant="h6" color="text.secondary">
                    {t('carRentalDescription', 'Tedavi süreniz boyunca özgürce seyahat edin. Geniş araç filosu seçenekleri.')}
                </Typography>
            </Box>

            <Grid container spacing={4} justifyContent="center">
                <Grid item xs={12} lg={8}>
                    <CarRentalForm />
                </Grid>

                <Grid item xs={12} lg={4}>
                    <Card sx={{ p: 3, bgcolor: theme.palette.mode === 'dark' ? 'grey.800' : 'background.paper', borderRadius: 2, border: `1px solid ${theme.palette.divider}` }}>
                        <Typography variant="h6" sx={{ fontWeight: 700, mb: 2, color: 'primary.main' }}>
                            {t('whyRentWithUs', 'Neden Bizimle Kiralamalısınız?')}
                        </Typography>
                        <Box sx={{ mb: 1 }}>
                            <Typography variant="subtitle1" sx={{ fontWeight: 600 }}>★ {t('insuranceAdvantages', 'Sigorta Avantajları')}</Typography>
                            <Typography variant="body2" color="text.secondary">{t('insuranceAdvantagesDesc', 'Kapsamlı kaza ve sağlık sigortası seçenekleri.')}</Typography>
                        </Box>
                        <Box sx={{ mb: 1 }}>
                            <Typography variant="subtitle1" sx={{ fontWeight: 600 }}>★ {t('hospitalDelivery', 'Hastane Teslimat')}</Typography>
                            <Typography variant="body2" color="text.secondary">{t('hospitalDeliveryDesc', 'Aracı hastane veya otelinize teslim alma imkanı.')}</Typography>
                        </Box>
                        <Box>
                            <Typography variant="subtitle1" sx={{ fontWeight: 600 }}>★ {t('easyCancellation', 'Kolay İptal')}</Typography>
                            <Typography variant="body2" color="text.secondary">{t('easyCancellationDesc', 'Esnek iptal seçenekleriyle tedavinize odaklanın.')}</Typography>
                        </Box>
                        <Divider sx={{ my: 2 }} />
                         <Button
                            variant="contained"
                            color="primary"
                            fullWidth
                            onClick={() => alert(t('carRentalSupportAlert', 'Canlı destek üzerinden araç kiralama bilgisi alabilirsiniz.'))}
                        >
                            {t('getExpertSupport', 'Uzman Desteği Al')}
                        </Button>
                    </Card>
                </Grid>
            </Grid>
        </Container>
    );
}

export default CarRentals;