// src/pages/Reservations.jsx
import React, { useState } from 'react';
import { 
    Container, Box, Typography, Grid, Paper, Stepper, Step,
    StepLabel, Button, TextField, FormControl, InputLabel,
    Select, MenuItem, RadioGroup, FormControlLabel, Radio,
    useTheme, Divider, Checkbox, FormGroup
} from '@mui/material';
import SendIcon from '@mui/icons-material/Send';
import InfoIcon from '@mui/icons-material/Info';
import DoneAllIcon from '@mui/icons-material/DoneAll';

// Adım Etiketleri
const steps = ['Temel Bilgiler', 'Tedavi Detayları', 'Seyahat Tercihleri', 'Onay ve Gönder'];

// Başlangıç Form Verisi
const initialFormData = {
    name: '',
    surname: '',
    email: '',
    phone: '',
    country: '',
    specialty: '',
    treatmentDescription: '',
    isUrgent: 'hayir',
    hasMedicalReports: false,
    flightNeeded: false,
    accommodationType: 'otel',
    contactPreference: '', // e-posta, telefon, whatsapp
};

// --- BİLEŞEN: Adım İçerikleri ---

// Adım 1: Temel Bilgiler
const StepOne = ({ formData, handleChange }) => (
    <Grid container spacing={3}>
        <Grid item xs={12}>
            <Typography variant="h6" color="primary">Kişisel Bilgilerinizi Girin</Typography>
            <Typography variant="body2" color="text.secondary" gutterBottom>Size özel teklif hazırlayabilmemiz için iletişim bilgileri önemlidir.</Typography>
        </Grid>
        <Grid item xs={12} sm={6}>
            <TextField fullWidth label="Adınız" name="name" value={formData.name} onChange={handleChange} required />
        </Grid>
        <Grid item xs={12} sm={6}>
            <TextField fullWidth label="Soyadınız" name="surname" value={formData.surname} onChange={handleChange} required />
        </Grid>
        <Grid item xs={12} sm={6}>
            <TextField fullWidth label="E-posta Adresi" name="email" type="email" value={formData.email} onChange={handleChange} required />
        </Grid>
        <Grid item xs={12} sm={6}>
            <TextField fullWidth label="Telefon Numarası (WhatsApp Dahil)" name="phone" value={formData.phone} onChange={handleChange} required />
        </Grid>
        <Grid item xs={12} sm={6}>
            <FormControl fullWidth required>
                <InputLabel>Ülke</InputLabel>
                <Select label="Ülke" name="country" value={formData.country} onChange={handleChange}>
                    <MenuItem value="">Seçiniz</MenuItem>
                    <MenuItem value="Almanya">Almanya</MenuItem>
                    <MenuItem value="İngiltere">İngiltere</MenuItem>
                    <MenuItem value="Hollanda">Hollanda</MenuItem>
                    {/* Daha fazla ülke eklenebilir */}
                </Select>
            </FormControl>
        </Grid>
        <Grid item xs={12} sm={6}>
            <FormControl fullWidth required>
                <InputLabel>İletişim Tercihi</InputLabel>
                <Select label="İletişim Tercihi" name="contactPreference" value={formData.contactPreference} onChange={handleChange}>
                    <MenuItem value="whatsapp">WhatsApp</MenuItem>
                    <MenuItem value="email">E-posta</MenuItem>
                    <MenuItem value="phone">Telefon</MenuItem>
                </Select>
            </FormControl>
        </Grid>
    </Grid>
);

// Adım 2: Tedavi Detayları
const StepTwo = ({ formData, handleChange }) => (
    <Grid container spacing={3}>
        <Grid item xs={12}>
            <Typography variant="h6" color="primary">Tedavi İhtiyacınızı Belirtin</Typography>
            <Typography variant="body2" color="text.secondary" gutterBottom>Hangi uzmanlık alanında ve ne tür bir tedaviye ihtiyacınız var?</Typography>
        </Grid>
        <Grid item xs={12} sm={6}>
            <FormControl fullWidth required>
                <InputLabel>İhtiyaç Duyulan Uzmanlık Alanı</InputLabel>
                <Select label="Uzmanlık Alanı" name="specialty" value={formData.specialty} onChange={handleChange}>
                    <MenuItem value="">Seçiniz</MenuItem>
                    <MenuItem value="Estetik Cerrahi">Estetik Cerrahi</MenuItem>
                    <MenuItem value="Diş Hekimliği">Diş Hekimliği</MenuItem>
                    <MenuItem value="Ortopedi">Ortopedi</MenuItem>
                    <MenuItem value="Kardiyoloji">Kardiyoloji</MenuItem>
                    {/* Daha fazla uzmanlık eklenebilir */}
                </Select>
            </FormControl>
        </Grid>
        <Grid item xs={12}>
            <TextField
                fullWidth
                label="Tedavi Hakkında Kısa Açıklama"
                name="treatmentDescription"
                value={formData.treatmentDescription}
                onChange={handleChange}
                multiline
                rows={4}
                placeholder="Örn: Saç ekimi düşünüyorum, en az 4000 greft. Veya: Diz protezi ameliyatı için fiyat teklifi istiyorum."
                required
            />
        </Grid>
        <Grid item xs={12} sm={6}>
            <Typography variant="subtitle1" gutterBottom>Raporlarınız mevcut mu?</Typography>
            <FormGroup>
                <FormControlLabel
                    control={<Checkbox checked={formData.hasMedicalReports} onChange={handleChange} name="hasMedicalReports" />}
                    label="Evet, tıbbi raporlarımı/görsellerimi gönderebilirim."
                />
            </FormGroup>
            <Typography variant="caption" color="error">
                *Raporlar, daha kesin bir fiyat teklifi almanız için önemlidir.
            </Typography>
        </Grid>
        <Grid item xs={12} sm={6}>
            <Typography variant="subtitle1" gutterBottom>Acil Durum</Typography>
            <FormControl component="fieldset">
                <RadioGroup row name="isUrgent" value={formData.isUrgent} onChange={handleChange}>
                    <FormControlLabel value="hayir" control={<Radio />} label="Normal (1-3 Ay içinde)" />
                    <FormControlLabel value="evet" control={<Radio />} label="Acil (1 Ay içinde)" />
                </RadioGroup>
            </FormControl>
        </Grid>
    </Grid>
);

// Adım 3: Seyahat Tercihleri
const StepThree = ({ formData, handleChange }) => (
    <Grid container spacing={3}>
        <Grid item xs={12}>
            <Typography variant="h6" color="primary">Seyahat ve Konaklama İhtiyaçları</Typography>
            <Typography variant="body2" color="text.secondary" gutterBottom>Transfer ve konaklama hizmetlerine ihtiyacınız var mı?</Typography>
        </Grid>
        <Grid item xs={12} sm={6}>
            <Typography variant="subtitle1" gutterBottom>Uçuş Bileti Desteği</Typography>
            <FormGroup>
                <FormControlLabel
                    control={<Checkbox checked={formData.flightNeeded} onChange={handleChange} name="flightNeeded" />}
                    label="Evet, uçuş bileti konusunda yardıma ihtiyacım var."
                />
            </FormGroup>
        </Grid>

        <Grid item xs={12} sm={6}>
            <Typography variant="subtitle1" gutterBottom>Konaklama Tipi Tercihi</Typography>
            <FormControl component="fieldset">
                <RadioGroup row name="accommodationType" value={formData.accommodationType} onChange={handleChange}>
                    <FormControlLabel value="otel" control={<Radio />} label="4/5 Yıldızlı Otel" />
                    <FormControlLabel value="rezidans" control={<Radio />} label="Lüks Rezidans Daire" />
                </RadioGroup>
            </FormControl>
        </Grid>
    </Grid>
);

// Adım 4: Onay ve Gönder
const StepFour = ({ formData }) => (
    <Box>
        <Typography variant="h6" color="primary" gutterBottom>
            <InfoIcon sx={{ mr: 1, verticalAlign: 'bottom' }} /> Girilen Bilgilerin Özeti
        </Typography>
        <Paper variant="outlined" sx={{ p: 3, mb: 3 }}>
            <Grid container spacing={2}>
                <Grid item xs={12} sm={6}>
                    <Typography variant="body1">**Ad-Soyad:** {formData.name} {formData.surname}</Typography>
                    <Typography variant="body1">**E-posta:** {formData.email}</Typography>
                    <Typography variant="body1">**Telefon:** {formData.phone}</Typography>
                </Grid>
                <Grid item xs={12} sm={6}>
                    <Typography variant="body1">**Ülke:** {formData.country}</Typography>
                    <Typography variant="body1">**İletişim Tercihi:** {formData.contactPreference}</Typography>
                    <Typography variant="body1">**Aciliyet:** {formData.isUrgent === 'evet' ? 'Yüksek' : 'Normal'}</Typography>
                </Grid>
            </Grid>
            <Divider sx={{ my: 2 }} />
            <Typography variant="body1">**Uzmanlık Alanı:** {formData.specialty}</Typography>
            <Typography variant="body1" sx={{ mt: 1 }}>**Açıklama:** *{formData.treatmentDescription.substring(0, 100)}...*</Typography>
            <Typography variant="body1" sx={{ mt: 1 }}>**Rapor Durumu:** {formData.hasMedicalReports ? 'Rapor mevcut' : 'Rapor yok'}</Typography>
        </Paper>

        <Typography variant="subtitle1" sx={{ mt: 2, fontWeight: 'bold' }}>
            <DoneAllIcon color="success" sx={{ mr: 1, verticalAlign: 'bottom' }} /> Bilgilerimin doğru olduğunu onaylıyorum.
        </Typography>
    </Box>
);

const getStepContent = (step, formData, handleChange) => {
    switch (step) {
        case 0: return <StepOne formData={formData} handleChange={handleChange} />;
        case 1: return <StepTwo formData={formData} handleChange={handleChange} />;
        case 2: return <StepThree formData={formData} handleChange={handleChange} />;
        case 3: return <StepFour formData={formData} />;
        default: return 'Bilinmeyen Adım';
    }
};

const isStepValid = (step, formData) => {
    switch (step) {
        case 0: // Temel Bilgiler
            return formData.name && formData.surname && formData.email && formData.phone && formData.country && formData.contactPreference;
        case 1: // Tedavi Detayları
            return formData.specialty && formData.treatmentDescription.length > 20;
        case 2: // Seyahat Tercihleri
            return true; // Bu adımda zorunlu alan yok
        case 3: // Onay
            return true;
        default: return false;
    }
};


// --- ANA BİLEŞEN: Reservations ---
function Reservations() {
    const theme = useTheme();
    const [activeStep, setActiveStep] = useState(0);
    const [formData, setFormData] = useState(initialFormData);

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value
        }));
    };

    const handleNext = () => {
        if (isStepValid(activeStep, formData)) {
            setActiveStep((prevActiveStep) => prevActiveStep + 1);
        } else {
            alert("Lütfen tüm zorunlu (*) alanları doldurun.");
        }
    };

    const handleBack = () => {
        setActiveStep((prevActiveStep) => prevActiveStep - 1);
    };

    const handleSubmit = () => {
        // Form gönderme mantığı buraya eklenecek (API çağrısı vb.)
        console.log('Form Gönderildi:', formData);
        setActiveStep((prevActiveStep) => prevActiveStep + 1); // Başarılı tamamlama sayfasına geçiş
    };

    return (
        <Container maxWidth="md" sx={{ py: 6 }}>
            <Typography variant="h3" component="h1" align="center" sx={{ fontWeight: 'bold', mb: 1 }}>
                Ücretsiz Teklif Talep Edin
            </Typography>
            <Typography variant="h6" color="text.secondary" align="center" sx={{ mb: 4 }}>
                Size özel tedavi paketini 4 kolay adımda hazırlayalım.
            </Typography>

            <Paper elevation={4} sx={{ p: { xs: 2, md: 4 }, borderRadius: 2 }}>

                {/* Stepper (Adımlar) */}
                <Stepper activeStep={activeStep} alternativeLabel sx={{ mb: 4, pt: 1 }}>
                    {steps.map((label) => (
                        <Step key={label}>
                            <StepLabel>{label}</StepLabel>
                        </Step>
                    ))}
                </Stepper>

                {activeStep === steps.length ? (
                    // Son Sayfa (Başarıyla Tamamlandı)
                    <Box sx={{ textAlign: 'center', py: 6 }}>
                        <DoneAllIcon color="success" sx={{ fontSize: 80, mb: 2 }} />
                        <Typography variant="h4" gutterBottom>Talebiniz Başarıyla Alındı!</Typography>
                        <Typography variant="body1" color="text.secondary" sx={{ mb: 3 }}>
                            Teklifiniz en kısa sürede (genellikle 24 saat içinde) hazırlanıp seçtiğiniz iletişim kanalı üzerinden size gönderilecektir. Teşekkür ederiz.
                        </Typography>
                        <Button component={RouterLink} to="/" variant="contained" color="primary">
                            Ana Sayfaya Dön
                        </Button>
                    </Box>
                ) : (
                    // Form Adımları
                    <Box>
                        {getStepContent(activeStep, formData, handleChange)}

                        {/* Butonlar */}
                        <Box sx={{ display: 'flex', flexDirection: 'row', pt: 4, borderTop: `1px solid ${theme.palette.divider}` }}>
                            <Button
                                color="inherit"
                                disabled={activeStep === 0}
                                onClick={handleBack}
                                sx={{ mr: 1 }}
                            >
                                Geri
                            </Button>
                            <Box sx={{ flex: '1 1 auto' }} />

                            {activeStep === steps.length - 1 ? (
                                <Button onClick={handleSubmit} variant="contained" color="secondary" endIcon={<SendIcon />}>
                                    Teklifi Gönder
                                </Button>
                            ) : (
                                <Button onClick={handleNext} variant="contained" color="primary">
                                    Sonraki Adım
                                </Button>
                            )}
                        </Box>
                    </Box>
                )}
            </Paper>
        </Container>
    );
}

export default Reservations;