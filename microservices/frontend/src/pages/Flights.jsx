// src/pages/Flights.jsx
import React, { useState } from 'react';
import { 
    Container, Box, Typography, Grid, Card, CardContent,
    TextField, Button, FormControl, InputLabel, Select, MenuItem,
    Paper, useTheme, Divider, RadioGroup, FormControlLabel, Radio,
    InputAdornment, IconButton
} from '@mui/material';
import FlightTakeoffIcon from '@mui/icons-material/FlightTakeoff';
import FlightLandIcon from '@mui/icons-material/FlightLand';
import EventIcon from '@mui/icons-material/Event';
import SwapHorizIcon from '@mui/icons-material/SwapHoriz';
import PersonIcon from '@mui/icons-material/Person';
import LuggageIcon from '@mui/icons-material/Luggage';
import CalendarTodayIcon from '@mui/icons-material/CalendarToday';
import { useTranslation } from 'react-i18next';

// --- ÖRNEK VERİLER ---
const airportOptions = [
    'İstanbul Havalimanı (IST)', 'Sabiha Gökçen Havalimanı (SAW)',
    'Ankara Esenboğa Havalimanı (ESB)', 'İzmir Adnan Menderes Havalimanı (ADB)',
    'Antalya Havalimanı (AYT)', 'Berlin Brandenburg (BER)', 'Frankfurt (FRA)',
    'Londra Heathrow (LHR)', 'Paris Charles de Gaulle (CDG)'
];

const travelClasses = ['Ekonomi', 'Business'];
// --- ÖRNEK VERİLER SONU ---


// --- BİLEŞEN: FlightSearchForm ---
const FlightSearchForm = () => {
    const { t } = useTranslation();
    const theme = useTheme();
    const [formData, setFormData] = useState({
        tripType: 'roundTrip',
        departureAirport: '',
        arrivalAirport: '',
        departureDate: '',
        returnDate: '',
        adults: 1,
        children: 0,
        infants: 0,
        travelClass: 'Ekonomi'
    });

    const [isSearching, setIsSearching] = useState(false);

    const handleChange = (event) => {
        const { name, value } = event.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleSwapAirports = () => {
        setFormData(prev => ({
            ...prev,
            departureAirport: prev.arrivalAirport,
            arrivalAirport: prev.departureAirport,
        }));
    };

    const handleSearch = (e) => {
        e.preventDefault();
        setIsSearching(true);

        // Gerçek bir API entegrasyonu burada yapılacaktır.
        console.log("Uçuş Aranıyor:", formData);

        // Arama sonucunu simüle etmek için 2 saniye bekleme
        setTimeout(() => {
            setIsSearching(false);
            alert(`Arama yapıldı. Güzergah: ${formData.departureAirport} -> ${formData.arrivalAirport}. (Gerçek uçuş sonuçları için API entegrasyonu gereklidir.)`);
        }, 2000);
    };

    return (
        <Paper sx={{ p: { xs: 3, md: 5 }, borderRadius: 2, boxShadow: theme.shadows[5] }}>
            <Typography variant="h5" sx={{ fontWeight: 700, mb: 3, color: theme.palette.primary.dark }}>
                Uçak Bileti Ara
            </Typography>

            <form onSubmit={handleSearch}>
                <Grid container spacing={3}>

                    {/* 1. Seyahat Tipi */}
                    <Grid item xs={12}>
                        <FormControl component="fieldset">
                            <RadioGroup
                                row
                                name="tripType"
                                value={formData.tripType}
                                onChange={handleChange}
                            >
                                <FormControlLabel value="roundTrip" control={<Radio size="small" />} label={t('roundTrip', 'Gidiş-Dönüş')} />
                                <FormControlLabel value="oneWay" control={<Radio size="small" />} label="Tek Yön" />
                            </RadioGroup>
                        </FormControl>
                        <Divider sx={{ my: 2 }} />
                    </Grid>

                    {/* 2. Kalkış ve Varış */}
                    <Grid item xs={12} sm={5}>
                        <FormControl fullWidth size="small">
                            <InputLabel id="departure-label">Nereden (Kalkış)</InputLabel>
                            <Select
                                labelId="departure-label"
                                name="departureAirport"
                                value={formData.departureAirport}
                                label="Nereden (Kalkış)"
                                onChange={handleChange}
                                required
                                startAdornment={<InputAdornment position="start"><FlightTakeoffIcon fontSize="small" /></InputAdornment>}
                            >
                                <MenuItem value="">Seçiniz</MenuItem>
                                {airportOptions.map(airport => (
                                    <MenuItem key={airport} value={airport}>{airport}</MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                    </Grid>
                    <Grid item xs={12} sm={2} sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
                        <IconButton onClick={handleSwapAirports} color="primary" size="large">
                            <SwapHorizIcon />
                        </IconButton>
                    </Grid>
                    <Grid item xs={12} sm={5}>
                        <FormControl fullWidth size="small">
                            <InputLabel id="arrival-label">Nereye (Varış)</InputLabel>
                            <Select
                                labelId="arrival-label"
                                name="arrivalAirport"
                                value={formData.arrivalAirport}
                                label="Nereye (Varış)"
                                onChange={handleChange}
                                required
                                startAdornment={<InputAdornment position="start"><FlightLandIcon fontSize="small" /></InputAdornment>}
                            >
                                <MenuItem value="">Seçiniz</MenuItem>
                                {airportOptions.map(airport => (
                                    <MenuItem key={airport} value={airport}>{airport}</MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                    </Grid>

                    {/* 3. Tarihler */}
                    <Grid item xs={12} sm={formData.tripType === 'roundTrip' ? 6 : 12}>
                        <TextField
                            fullWidth
                            name="departureDate"
                            label={t('departureDate', 'Gidiş Tarihi')}
                            type="date"
                            size="small"
                            value={formData.departureDate}
                            onChange={handleChange}
                            InputLabelProps={{ shrink: true }}
                            InputProps={{
                                startAdornment: <InputAdornment position="start"><CalendarTodayIcon fontSize="small" /></InputAdornment>,
                            }}
                            required
                        />
                    </Grid>
                    {formData.tripType === 'roundTrip' && (
                        <Grid item xs={12} sm={6}>
                            <TextField
                                fullWidth
                                name="returnDate"
                                label={t('returnDate', 'Dönüş Tarihi')}
                                type="date"
                                size="small"
                                value={formData.returnDate}
                                onChange={handleChange}
                                InputLabelProps={{ shrink: true }}
                                InputProps={{
                                    startAdornment: <InputAdornment position="start"><CalendarTodayIcon fontSize="small" /></InputAdornment>,
                                }}
                                required
                            />
                        </Grid>
                    )}

                    {/* 4. Yolcu ve Sınıf */}
                    <Grid item xs={6} sm={3}>
                         <TextField
                            fullWidth
                            name="adults"
                            label="Yetişkin"
                            type="number"
                            size="small"
                            value={formData.adults}
                            onChange={handleChange}
                            InputProps={{
                                startAdornment: <InputAdornment position="start"><PersonIcon fontSize="small" /></InputAdornment>,
                                inputProps: { min: 1, max: 9 }
                            }}
                            required
                        />
                    </Grid>
                    <Grid item xs={6} sm={3}>
                         <TextField
                            fullWidth
                            name="children"
                            label="Çocuk (2-12 yaş)"
                            type="number"
                            size="small"
                            value={formData.children}
                            onChange={handleChange}
                            InputProps={{ inputProps: { min: 0, max: 8 } }}
                        />
                    </Grid>
                    <Grid item xs={12} sm={6}>
                        <FormControl fullWidth size="small">
                            <InputLabel id="class-label">{t('travelClass', 'Seyahat Sınıfı')}</InputLabel>
                            <Select
                                labelId="class-label"
                                name="travelClass"
                                value={formData.travelClass}
                                label={t('travelClass', 'Seyahat Sınıfı')}
                                onChange={handleChange}
                                required
                                startAdornment={<InputAdornment position="start"><LuggageIcon fontSize="small" /></InputAdornment>}
                            >
                                {travelClasses.map(tClass => (
                                    <MenuItem key={tClass} value={tClass}>{tClass}</MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                    </Grid>

                    {/* 5. Arama Butonu */}
                    <Grid item xs={12}>
                        <Button
                            type="submit"
                            variant="contained"
                            color="secondary"
                            fullWidth
                            size="large"
                            sx={{ py: 1.5, fontWeight: 'bold' }}
                            disabled={isSearching}
                        >
                            {isSearching ? t('searchingFlights', 'Uçuş Aranıyor...') : t('searchFlights', 'Uçuş Ara')}
                        </Button>
                    </Grid>
                </Grid>
            </form>
        </Paper>
    );
};
// --- BİLEŞEN SONU: FlightSearchForm ---


function Flights() {
    const { t } = useTranslation();
    const theme = useTheme();

    return (
        <Container maxWidth="lg" sx={{ py: 5 }}>
            <Box textAlign="center" sx={{ mb: 5 }}>
                <FlightTakeoffIcon sx={{ fontSize: 60, color: 'primary.main' }} />
                <Typography variant="h3" component="h1" gutterBottom sx={{ fontWeight: 'bold', mt: 1 }}>
                    Sağlık Turizmi Uçuşları
                </Typography>
                <Typography variant="h6" color="text.secondary">
                    Dünya genelindeki havalimanlarından Türkiye'deki sağlık noktalarına uygun fiyatlı biletler.
                </Typography>
            </Box>

            <Grid container spacing={4} justifyContent="center">
                <Grid item xs={12} lg={8}>
                    <FlightSearchForm />
                </Grid>

                <Grid item xs={12} lg={4}>
                    <Card sx={{ p: 3, bgcolor: theme.palette.mode === 'dark' ? 'grey.800' : 'background.paper', borderRadius: 2, border: `1px solid ${theme.palette.divider}` }}>
                        <Typography variant="h6" sx={{ fontWeight: 700, mb: 2, color: 'primary.main' }}>
                            Neden Bizimle Uçuş Rezervasyonu?
                        </Typography>
                        <Box sx={{ mb: 1 }}>
                            <Typography variant="subtitle1" sx={{ fontWeight: 600 }}>★ Entegrasyon</Typography>
                            <Typography variant="body2" color="text.secondary">Tıbbi randevu tarihlerinize tam uyumlu uçuş önerileri.</Typography>
                        </Box>
                        <Box sx={{ mb: 1 }}>
                            <Typography variant="subtitle1" sx={{ fontWeight: 600 }}>★ Özel Fiyatlar</Typography>
                            <Typography variant="body2" color="text.secondary">Sağlık turizmi paketlerine özel anlaşmalı fiyatlar.</Typography>
                        </Box>
                        <Box>
                            <Typography variant="subtitle1" sx={{ fontWeight: 600 }}>★ 7/24 Destek</Typography>
                            <Typography variant="body2" color="text.secondary">Uçuş değişiklikleri ve iptallerinde hızlı yardım.</Typography>
                        </Box>
                        <Divider sx={{ my: 2 }} />
                         <Button
                            variant="outlined"
                            color="primary"
                            fullWidth
                            startIcon={<PersonIcon />}
                            onClick={() => alert('Asistan ile Canlı Görüşme Başlatılıyor...')}
                        >
                            Özel Destek Hattı
                        </Button>
                    </Card>
                </Grid>
            </Grid>
        </Container>
    );
}

export default Flights;