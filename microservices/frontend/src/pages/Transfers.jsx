// src/pages/Transfers.jsx
import React, { useState } from 'react';
import { 
    Container, Box, Typography, Grid, Card, CardContent,
    TextField, Button, FormControl, InputLabel, Select, MenuItem,
    Paper, useTheme, Divider, RadioGroup, FormControlLabel, Radio,
    InputAdornment, IconButton
} from '@mui/material';
import DirectionsCarIcon from '@mui/icons-material/DirectionsCar';
import FlightTakeoffIcon from '@mui/icons-material/FlightTakeoff';
import EventIcon from '@mui/icons-material/Event';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import PeopleIcon from '@mui/icons-material/People';
import LocalHospitalIcon from '@mui/icons-material/LocalHospital';
import CalendarTodayIcon from '@mui/icons-material/CalendarToday';
import { useTranslation } from 'react-i18next';

// --- ÖRNEK VERİLER ---
const cityOptions = ['İstanbul (IST/SAW)', 'Ankara (ESB)', 'İzmir (ADB)', 'Antalya (AYT)'];
const vehicleTypes = [
    { name: 'VIP Vito (5 Kişi)', icon: 'van', description: 'Konforlu, geniş iç hacim.', priceBase: 45 },
    { name: 'Sedan/Taksi (3 Kişi)', icon: 'car', description: 'Ekonomik ve hızlı çözüm.', priceBase: 25 },
    { name: 'Minibüs (12 Kişi)', icon: 'bus', description: 'Büyük gruplar ve aileler için ideal.', priceBase: 70 },
];

// Basit ikon eşleştirme fonksiyonu
const getVehicleIcon = (type) => {
    switch (type) {
        case 'van':
            return <DirectionsCarIcon sx={{ transform: 'scaleX(-1)' }} />; // Vito için ters çevrilmiş araç ikonu
        case 'car':
            return <DirectionsCarIcon />;
        case 'bus':
            return <PeopleIcon />;
        default:
            return <DirectionsCarIcon />;
    }
};
// --- ÖRNEK VERİLER SONU ---


// --- BİLEŞEN: TransferForm ---
const TransferForm = () => {
    const { t } = useTranslation();
    const theme = useTheme();
    const [formData, setFormData] = useState({
        transferType: 'oneWay',
        direction: 'airportToHotel',
        city: '',
        date: '',
        time: '14:00',
        passengers: 1,
        flightNumber: '',
        details: '',
        vehicle: 'VIP Vito (5 Kişi)'
    });

    const [estimatedPrice, setEstimatedPrice] = useState(null);

    const handleChange = (event) => {
        const { name, value } = event.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handlePriceCalculation = (e) => {
        e.preventDefault();

        // Basit Fiyat Hesaplama Simülasyonu
        const selectedVehicle = vehicleTypes.find(v => v.name === formData.vehicle);
        let price = selectedVehicle ? selectedVehicle.priceBase : 45;

        // Yolcu sayısına göre ek ücret
        if (formData.passengers > 3) {
            price += 10;
        }

        // Çift yönlü transfer ise fiyatı iki katına çıkar
        if (formData.transferType === 'roundTrip') {
            price *= 1.8; // %20 indirimle gidiş dönüş
        }

        setEstimatedPrice(Math.round(price));
    };

    return (
        <Paper sx={{ p: { xs: 3, md: 5 }, borderRadius: 2, boxShadow: theme.shadows[5] }}>
            <Typography variant="h5" sx={{ fontWeight: 700, mb: 3, color: theme.palette.primary.dark }}>
                Hızlı Transfer Rezervasyon Formu
            </Typography>

            <form onSubmit={handlePriceCalculation}>
                <Grid container spacing={3}>

                    {/* 1. Transfer Yönü ve Tipi */}
                    <Grid item xs={12}>
                        <Box sx={{ display: 'flex', gap: 2, mb: 1, flexDirection: { xs: 'column', sm: 'row' } }}>
                            <FormControl component="fieldset">
                                <Typography variant="subtitle2" sx={{ fontWeight: 600, mb: 1 }}>{t('transferType', 'Transfer Tipi')}</Typography>
                                <RadioGroup
                                    row
                                    name="transferType"
                                    value={formData.transferType}
                                    onChange={handleChange}
                                >
                                    <FormControlLabel value="oneWay" control={<Radio size="small" />} label={t('oneWay', 'Tek Yön')} />
                                    <FormControlLabel value="roundTrip" control={<Radio size="small" />} label="Gidiş-Dönüş" />
                                </RadioGroup>
                            </FormControl>
                            <FormControl component="fieldset">
                                <Typography variant="subtitle2" sx={{ fontWeight: 600, mb: 1 }}>Güzergah</Typography>
                                <RadioGroup
                                    row
                                    name="direction"
                                    value={formData.direction}
                                    onChange={handleChange}
                                >
                                    <FormControlLabel value="airportToHotel" control={<Radio size="small" />} label={<Box sx={{ display: 'flex', alignItems: 'center' }}><FlightTakeoffIcon fontSize="small" sx={{ mr: 0.5 }} /> {t('airportToHotel', 'Havaalanı - Otel')}</Box>} />
                                    <FormControlLabel value="hotelToHospital" control={<Radio size="small" />} label={<Box sx={{ display: 'flex', alignItems: 'center' }}><LocalHospitalIcon fontSize="small" sx={{ mr: 0.5 }} /> {t('hotelToHospital', 'Otel - Hastane')}</Box>} />
                                </RadioGroup>
                            </FormControl>
                        </Box>
                        <Divider sx={{ my: 2 }} />
                    </Grid>

                    {/* 2. Şehir ve Tarih/Saat */}
                    <Grid item xs={12} sm={6}>
                        <FormControl fullWidth size="small">
                            <InputLabel id="city-label">{t('transferCityAirport', 'Transfer Şehri ve Havaalanı')}</InputLabel>
                            <Select
                                labelId="city-label"
                                name="city"
                                value={formData.city}
                                label={t('transferCityAirport', 'Transfer Şehri ve Havaalanı')}
                                onChange={handleChange}
                                required
                            >
                                <MenuItem value="">Seçiniz</MenuItem>
                                {cityOptions.map(city => (
                                    <MenuItem key={city} value={city}>{city}</MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                    </Grid>
                    <Grid item xs={12} sm={3}>
                        <TextField
                            fullWidth
                            name="date"
                            label={t('date')}
                            type="date"
                            size="small"
                            value={formData.date}
                            onChange={handleChange}
                            InputLabelProps={{ shrink: true }}
                            InputProps={{
                                startAdornment: <InputAdornment position="start"><CalendarTodayIcon fontSize="small" /></InputAdornment>,
                            }}
                            required
                        />
                    </Grid>
                    <Grid item xs={12} sm={3}>
                        <TextField
                            fullWidth
                            name="time"
                            label={t('time')}
                            type="time"
                            size="small"
                            value={formData.time}
                            onChange={handleChange}
                            InputLabelProps={{ shrink: true }}
                            InputProps={{
                                startAdornment: <InputAdornment position="start"><AccessTimeIcon fontSize="small" /></InputAdornment>,
                            }}
                            required
                        />
                    </Grid>

                    {/* 3. Yolcu ve Uçuş Bilgisi */}
                    <Grid item xs={12} sm={6}>
                        <TextField
                            fullWidth
                            name="flightNumber"
                            label="Uçuş Numarası (Havaalanı transferi için)"
                            size="small"
                            value={formData.flightNumber}
                            onChange={handleChange}
                            InputProps={{
                                startAdornment: <InputAdornment position="start"><FlightTakeoffIcon fontSize="small" /></InputAdornment>,
                            }}
                        />
                    </Grid>
                    <Grid item xs={12} sm={6}>
                        <TextField
                            fullWidth
                            name="passengers"
                            label={t('passengerCount', 'Yolcu Sayısı')}
                            type="number"
                            size="small"
                            value={formData.passengers}
                            onChange={handleChange}
                            InputProps={{
                                startAdornment: <InputAdornment position="start"><PeopleIcon fontSize="small" /></InputAdornment>,
                                inputProps: { min: 1, max: 15 }
                            }}
                            required
                        />
                    </Grid>

                    {/* 4. Araç Tipi Seçimi */}
                    <Grid item xs={12}>
                        <Typography variant="subtitle1" sx={{ fontWeight: 600, mb: 1, mt: 1 }}>{t('vehicleTypeSelection', 'Araç Tipi Seçimi')}</Typography>
                        <RadioGroup
                            row
                            name="vehicle"
                            value={formData.vehicle}
                            onChange={handleChange}
                            sx={{ gap: 2, flexWrap: 'wrap' }}
                        >
                            {vehicleTypes.map((v) => (
                                <Card
                                    key={v.name}
                                    variant="outlined"
                                    sx={{
                                        flexGrow: 1,
                                        minWidth: 150,
                                        p: 1,
                                        borderColor: formData.vehicle === v.name ? theme.palette.secondary.main : 'divider',
                                        boxShadow: formData.vehicle === v.name ? theme.shadows[3] : 'none',
                                    }}
                                >
                                    <FormControlLabel
                                        value={v.name}
                                        control={<Radio size="small" sx={{ p: 0 }} />}
                                        label={
                                            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                                                {getVehicleIcon(v.icon)}
                                                <Typography variant="body2" sx={{ fontWeight: 600 }}>{v.name}</Typography>
                                            </Box>
                                        }
                                        sx={{ m: 0, width: '100%', mb: 1 }}
                                    />
                                    <Typography variant="caption" color="text.secondary" sx={{ ml: 3 }}>
                                        {v.description}
                                    </Typography>
                                </Card>
                            ))}
                        </RadioGroup>
                    </Grid>

                    {/* 5. Ek Notlar */}
                    <Grid item xs={12}>
                        <TextField
                            fullWidth
                            name="details"
                            label="Ek Bilgi ve Talepler (Tıbbi malzeme, bebek koltuğu vb.)"
                            multiline
                            rows={3}
                            value={formData.details}
                            onChange={handleChange}
                        />
                    </Grid>

                    {/* 6. Hesaplama ve Buton */}
                    <Grid item xs={12} sm={6}>
                        <Typography variant="h6" color="primary" sx={{ fontWeight: 700 }}>
                            Tahmini Fiyat: {estimatedPrice ? `$${estimatedPrice}` : 'Bilgi Giriniz'}
                        </Typography>
                        <Typography variant="caption" color="text.secondary">
                            *Bu fiyatlar tahmini olup, kesin fiyat teklifi form sonrası verilecektir.
                        </Typography>
                    </Grid>
                    <Grid item xs={12} sm={6}>
                        <Button
                            type="submit"
                            variant="contained"
                            color="secondary"
                            fullWidth
                            size="large"
                            sx={{ py: 1.5, fontWeight: 'bold' }}
                            // Hesaplama yerine formu doğrudan göndermek isterseniz (API entegrasyonu için):
                            // onClick={() => alert('Rezervasyon talebiniz alındı!')}
                        >
                            Fiyatı Hesapla / Rezervasyon Yap
                        </Button>
                    </Grid>

                </Grid>
            </form>
        </Paper>
    );
}
// --- BİLEŞEN SONU: TransferForm ---


function Transfers() {
    const { t } = useTranslation();
    return (
        <Container maxWidth="lg" sx={{ py: 5 }}>
            <Box textAlign="center" sx={{ mb: 5 }}>
                <DirectionsCarIcon sx={{ fontSize: 60, color: 'primary.main' }} />
                <Typography variant="h3" component="h1" gutterBottom sx={{ fontWeight: 'bold', mt: 1 }}>
                    Özel VIP Transfer Hizmetleri
                </Typography>
                <Typography variant="h6" color="text.secondary">
                    Havaalanından otelinize, otelden hastaneye konforlu ve güvenli yolculuk.
                </Typography>
            </Box>

            <Grid container spacing={4} justifyContent="center">
                <Grid item xs={12} lg={8}>
                    <TransferForm />
                </Grid>

                <Grid item xs={12} lg={4}>
                    <Card sx={{ p: 3, bgcolor: 'primary.light', borderRadius: 2 }}>
                        <Typography variant="h6" sx={{ fontWeight: 700, mb: 2, color: 'primary.dark' }}>
                            Neden Bizimle Transfer?
                        </Typography>
                        <Box sx={{ mb: 1 }}>
                            <Typography variant="subtitle1" sx={{ fontWeight: 600, color: 'primary.dark' }}>✔ Hastaneye Yakınlık Garantisi</Typography>
                            <Typography variant="body2" color="text.secondary">Tıbbi randevularınıza dakik ve stressiz ulaşım.</Typography>
                        </Box>
                        <Box sx={{ mb: 1 }}>
                            <Typography variant="subtitle1" sx={{ fontWeight: 600, color: 'primary.dark' }}>✔ 7/24 Karşılama</Typography>
                            <Typography variant="body2" color="text.secondary">Uçuşunuzun saatine bakılmaksızın size özel karşılama.</Typography>
                        </Box>
                        <Box>
                            <Typography variant="subtitle1" sx={{ fontWeight: 600, color: 'primary.dark' }}>✔ Tıbbi Ekipman Uyumu</Typography>
                            <Typography variant="body2" color="text.secondary">Gerekli tekerlekli sandalye ve tıbbi malzemeye uygun araçlar.</Typography>
                        </Box>
                    </Card>
                </Grid>
            </Grid>
        </Container>
    );
}

export default Transfers;