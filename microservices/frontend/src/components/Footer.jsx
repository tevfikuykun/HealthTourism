// src/components/Footer.jsx
import React from 'react';
import {
    Box, Typography, Container, Grid, Link,
    Divider, IconButton, useTheme
} from '@mui/material';
import FacebookIcon from '@mui/icons-material/Facebook';
import TwitterIcon from '@mui/icons-material/Twitter';
import InstagramIcon from '@mui/icons-material/Instagram';
import LinkedInIcon from '@mui/icons-material/LinkedIn';
import MailOutlineIcon from '@mui/icons-material/MailOutline';
import PhoneIphoneIcon from '@mui/icons-material/PhoneIphone';
import LocationOnIcon from '@mui/icons-material/LocationOn';
import { Link as RouterLink } from 'react-router-dom';

const companyLinks = [
    { title: 'Hakkımızda', to: '/about' },
    { title: 'Neden Biz?', to: '/why-us' },
    { title: 'Gizlilik Politikası', to: '/privacy' },
    { title: 'Kullanım Koşulları', to: '/terms' },
];

const serviceLinks = [
    { title: 'Hastaneler', to: '/hospitals' },
    { title: 'Uzman Doktorlar', to: '/doctors' },
    { title: 'Sağlık Paketleri', to: '/packages' },
    { title: 'Rezervasyon Talep Et', to: '/reservations' },
];

function Footer() {
    const theme = useTheme();

    return (
        <Box
            component="footer"
            sx={{
                bgcolor: theme.palette.grey[900],
                color: 'white',
                py: 6,
                borderTop: `4px solid ${theme.palette.secondary.main}`
            }}
        >
            <Container maxWidth="lg">
                <Grid container spacing={4}>

                    {/* Kolon 1: Şirket Logosu ve İletişim */}
                    <Grid item xs={12} md={4}>
                        <Typography variant="h5" sx={{ fontWeight: 'bold', mb: 2, color: theme.palette.secondary.main }}>
                            HEALTH TURİZM
                        </Typography>
                        <Typography variant="body2" sx={{ mb: 2, opacity: 0.8 }}>
                            Sağlık turizminde global çözüm ortağınız. Tedavi, konaklama ve transfer dahil tüm süreçlerinizde yanınızdayız.
                        </Typography>

                        <Box sx={{ mt: 3 }}>
                            <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                                <MailOutlineIcon fontSize="small" color="secondary" sx={{ mr: 1 }} />
                                <Link href="mailto:info@healthturizm.com" color="inherit" underline="hover">
                                    info@healthturizm.com
                                </Link>
                            </Box>
                            <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                                <PhoneIphoneIcon fontSize="small" color="secondary" sx={{ mr: 1 }} />
                                <Typography variant="body2">+90 555 123 45 67 (WhatsApp)</Typography>
                            </Box>
                            <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                                <LocationOnIcon fontSize="small" color="secondary" sx={{ mr: 1 }} />
                                <Typography variant="body2">İstanbul, Türkiye</Typography>
                            </Box>
                        </Box>
                    </Grid>

                    {/* Kolon 2: Kurumsal Linkler */}
                    <Grid item xs={6} sm={4} md={2}>
                        <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 2, color: theme.palette.primary.light }}>
                            Kurumsal
                        </Typography>
                        <Box component="ul" sx={{ listStyle: 'none', p: 0 }}>
                            {companyLinks.map((link) => (
                                <li key={link.title} style={{ marginBottom: theme.spacing(1) }}>
                                    <Link component={RouterLink} to={link.to} color="inherit" underline="none" sx={{ opacity: 0.8, '&:hover': { opacity: 1, color: theme.palette.secondary.main } }}>
                                        {link.title}
                                    </Link>
                                </li>
                            ))}
                        </Box>
                    </Grid>

                    {/* Kolon 3: Hizmetlerimiz */}
                    <Grid item xs={6} sm={4} md={3}>
                        <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 2, color: theme.palette.primary.light }}>
                            Hizmetlerimiz
                        </Typography>
                        <Box component="ul" sx={{ listStyle: 'none', p: 0 }}>
                            {serviceLinks.map((link) => (
                                <li key={link.title} style={{ marginBottom: theme.spacing(1) }}>
                                    <Link component={RouterLink} to={link.to} color="inherit" underline="none" sx={{ opacity: 0.8, '&:hover': { opacity: 1, color: theme.palette.secondary.main } }}>
                                        {link.title}
                                    </Link>
                                </li>
                            ))}
                        </Box>
                    </Grid>

                    {/* Kolon 4: Sosyal Medya */}
                    <Grid item xs={12} sm={4} md={3}>
                        <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 2, color: theme.palette.primary.light }}>
                            Bizi Takip Edin
                        </Typography>
                        <Box>
                            <IconButton color="secondary" aria-label="facebook" sx={{ mr: 1 }}>
                                <FacebookIcon />
                            </IconButton>
                            <IconButton color="secondary" aria-label="twitter" sx={{ mr: 1 }}>
                                <TwitterIcon />
                            </IconButton>
                            <IconButton color="secondary" aria-label="instagram" sx={{ mr: 1 }}>
                                <InstagramIcon />
                            </IconButton>
                            <IconButton color="secondary" aria-label="linkedin">
                                <LinkedInIcon />
                            </IconButton>
                        </Box>
                        <Box sx={{ mt: 3 }}>
                            <img src="https://via.placeholder.com/150x50?text=Logo+Health" alt="Logo" style={{ maxWidth: '100%', opacity: 0.7 }} />
                        </Box>
                    </Grid>
                </Grid>

                {/* Telif Hakkı ve Alt Bar */}
                <Divider sx={{ mt: 4, mb: 3, bgcolor: theme.palette.grey[700] }} />
                <Typography variant="body2" align="center" sx={{ opacity: 0.6 }}>
                    © {new Date().getFullYear()} Health Turizm. Tüm Hakları Saklıdır.
                </Typography>
            </Container>
        </Box>
    );
}

export default Footer;