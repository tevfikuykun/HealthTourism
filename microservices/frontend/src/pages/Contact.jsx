// src/pages/Contact.jsx
import React, { useState } from 'react';
import {
    Container, Box, Typography, Grid, Card, CardContent,
    Button, Paper, useTheme, Divider, TextField,
    List, ListItem, ListItemIcon, ListItemText
} from '@mui/material';
import PhoneIcon from '@mui/icons-material/Phone';
import EmailIcon from '@mui/icons-material/Email';
import LocationOnIcon from '@mui/icons-material/LocationOn';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import SendIcon from '@mui/icons-material/Send';
import HeadsetMicIcon from '@mui/icons-material/HeadsetMic';

// --- ÖRNEK BİLGİLER ---
const contactInfo = {
    phone: '+90 850 444 0000',
    email: 'info@saglikturizmiplatformu.com',
    address: 'Büyükdere Cd. No: 100, 34394 Şişli/İstanbul, Türkiye',
    workingHours: 'Pazartesi - Cuma: 09:00 - 18:00 (GMT+3)',
    emergencyLine: '+90 532 123 4567 (7/24 Acil Hat)',
};
// --- ÖRNEK BİLGİLER SONU ---


// --- BİLEŞEN: ContactForm ---
const ContactForm = () => {
    const [formData, setFormData] = useState({
        name: '',
        email: '',
        subject: '',
        message: ''
    });

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        // Form gönderme mantığı burada yer alacaktır (API/Mail entegrasyonu)
        alert(`Mesajınız alındı. En kısa sürede (${formData.email}) adresinden size dönüş yapılacaktır.`);
        setFormData({ name: '', email: '', subject: '', message: '' });
    };

    return (
        <Paper sx={{ p: { xs: 3, md: 5 }, borderRadius: 2, boxShadow: 6 }}>
            <Typography variant="h5" sx={{ fontWeight: 700, mb: 3, color: 'primary.dark' }}>
                Bize Mesaj Gönderin
            </Typography>
            <form onSubmit={handleSubmit}>
                <Grid container spacing={3}>
                    <Grid item xs={12} sm={6}>
                        <TextField
                            fullWidth
                            label="Adınız Soyadınız"
                            name="name"
                            value={formData.name}
                            onChange={handleChange}
                            required
                            size="small"
                        />
                    </Grid>
                    <Grid item xs={12} sm={6}>
                        <TextField
                            fullWidth
                            label="E-posta Adresiniz"
                            name="email"
                            type="email"
                            value={formData.email}
                            onChange={handleChange}
                            required
                            size="small"
                        />
                    </Grid>
                    <Grid item xs={12}>
                        <TextField
                            fullWidth
                            label="Konu (Teklif, Randevu, Genel Soru)"
                            name="subject"
                            value={formData.subject}
                            onChange={handleChange}
                            required
                            size="small"
                        />
                    </Grid>
                    <Grid item xs={12}>
                        <TextField
                            fullWidth
                            label="Mesajınız"
                            name="message"
                            multiline
                            rows={5}
                            value={formData.message}
                            onChange={handleChange}
                            required
                        />
                    </Grid>
                    <Grid item xs={12}>
                        <Button
                            type="submit"
                            variant="contained"
                            color="secondary"
                            fullWidth
                            size="large"
                            startIcon={<SendIcon />}
                            sx={{ mt: 1, fontWeight: 'bold' }}
                        >
                            Mesajı Gönder
                        </Button>
                    </Grid>
                </Grid>
            </form>
        </Paper>
    );
};
// --- BİLEŞEN SONU: ContactForm ---


// --- BİLEŞEN: Contact.jsx ---
function Contact() {
    const theme = useTheme();

    return (
        <Container maxWidth="lg" sx={{ py: 5 }}>
            <Box textAlign="center" sx={{ mb: 5 }}>
                <HeadsetMicIcon sx={{ fontSize: 60, color: 'primary.main' }} />
                <Typography variant="h3" component="h1" gutterBottom sx={{ fontWeight: 'bold', mt: 1 }}>
                    Bize Ulaşın
                </Typography>
                <Typography variant="h6" color="text.secondary">
                    Tıbbi sorularınız, teklif talepleriniz ve tüm destek ihtiyaçlarınız için hazırız.
                </Typography>
            </Box>

            <Grid container spacing={4}>

                {/* Sol Kolon: İletişim Formu */}
                <Grid item xs={12} md={8}>
                    <ContactForm />
                </Grid>

                {/* Sağ Kolon: Bilgiler ve Harita */}
                <Grid item xs={12} md={4}>
                    {/* İletişim Bilgileri */}
                    <Card sx={{ p: 3, borderRadius: 2, mb: 3, boxShadow: 3, bgcolor: theme.palette.mode === 'dark' ? 'grey.900' : 'background.paper' }}>
                        <Typography variant="h6" sx={{ fontWeight: 700, mb: 2, color: 'primary.main' }}>
                            İletişim Kanallarımız
                        </Typography>
                        <Divider sx={{ mb: 2 }} />
                        <List disablePadding>
                            <ListItem>
                                <ListItemIcon><PhoneIcon color="primary" /></ListItemIcon>
                                <ListItemText primary="Çağrı Merkezi" secondary={contactInfo.phone} />
                            </ListItem>
                            <ListItem>
                                <ListItemIcon><EmailIcon color="primary" /></ListItemIcon>
                                <ListItemText primary="E-posta" secondary={contactInfo.email} />
                            </ListItem>
                            <ListItem>
                                <ListItemIcon><LocationOnIcon color="primary" /></ListItemIcon>
                                <ListItemText primary="Merkez Adresimiz" secondary={contactInfo.address} />
                            </ListItem>
                            <ListItem>
                                <ListItemIcon><AccessTimeIcon color="primary" /></ListItemIcon>
                                <ListItemText primary="Çalışma Saatleri" secondary={contactInfo.workingHours} />
                            </ListItem>
                        </List>
                        <Button
                            variant="outlined"
                            color="secondary"
                            fullWidth
                            sx={{ mt: 2 }}
                            startIcon={<PhoneIcon />}
                        >
                            {contactInfo.emergencyLine} (7/24 Acil)
                        </Button>
                    </Card>

                    {/* Harita Yer Tutucu */}
                    <Paper sx={{ height: 300, display: 'flex', alignItems: 'center', justifyContent: 'center', bgcolor: 'grey.200', borderRadius: 2, boxShadow: 3 }}>
                        <Typography variant="subtitle1" color="text.secondary">
                            Google Haritası Yer Tutucusu
                        </Typography>
                    </Paper>
                </Grid>
            </Grid>
        </Container>
    );
}

export default Contact;