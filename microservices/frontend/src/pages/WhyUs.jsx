// src/pages/WhyUs.jsx
import React from 'react';
import {
    Container, Box, Typography, Grid, Card, CardContent,
    Paper, useTheme, Divider, List, ListItem, ListItemIcon, ListItemText
} from '@mui/material';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import LocalHospitalIcon from '@mui/icons-material/LocalHospital';
import SecurityIcon from '@mui/icons-material/Security';
import StarIcon from '@mui/icons-material/Star';
import LanguageIcon from '@mui/icons-material/Language';
import SupportAgentIcon from '@mui/icons-material/SupportAgent';
import VerifiedUserIcon from '@mui/icons-material/VerifiedUser';
import SEOHead from '../components/SEO/SEOHead';
import { useTranslation } from 'react-i18next';

export default function WhyUs() {
    const { t } = useTranslation();
    const theme = useTheme();

    const advantages = [
        {
            icon: <LocalHospitalIcon sx={{ fontSize: 40 }} />,
            title: 'JCI Akreditasyonlu Hastaneler',
            description: 'Tüm partner hastanelerimiz uluslararası JCI (Joint Commission International) akreditasyonuna sahiptir.'
        },
        {
            icon: <StarIcon sx={{ fontSize: 40 }} />,
            title: 'Dünya Standartlarında Doktorlar',
            description: 'Alanında uzman, uluslararası deneyime sahip doktorlarımızla çalışıyoruz.'
        },
        {
            icon: <LanguageIcon sx={{ fontSize: 40 }} />,
            title: 'Çok Dilli Destek',
            description: '20+ dilde hizmet veriyoruz. Tercüman desteği ve çok dilli hasta koordinatörleri.'
        },
        {
            icon: <SecurityIcon sx={{ fontSize: 40 }} />,
            title: 'Güvenli ve Şeffaf İşlemler',
            description: 'Tüm işlemleriniz güvenli ödeme sistemleri ile korunur. Fiyatlar şeffaftır.'
        },
        {
            icon: <SupportAgentIcon sx={{ fontSize: 40 }} />,
            title: '7/24 Destek',
            description: 'Türkiye\'ye gelmeden önce, sırasında ve sonrasında yanınızdayız.'
        },
        {
            icon: <VerifiedUserIcon sx={{ fontSize: 40 }} />,
            title: 'Kapsamlı Hizmet Paketi',
            description: 'Uçak bileti, konaklama, transfer, vize desteği - her şey tek çatı altında.'
        }
    ];

    const stats = [
        { number: '50,000+', label: 'Mutlu Hasta' },
        { number: '150+', label: 'Partner Hastane' },
        { number: '500+', label: 'Uzman Doktor' },
        { number: '95%', label: 'Memnuniyet Oranı' }
    ];

    return (
        <>
            <SEOHead
                title="Neden Biz? - Health Tourism"
                description="Sağlık turizmi için neden bizi seçmelisiniz? JCI akreditasyonlu hastaneler, uzman doktorlar, çok dilli destek ve kapsamlı hizmet paketleri."
                keywords="sağlık turizmi, neden biz, JCI akreditasyon, uzman doktorlar, çok dilli destek"
            />
            <Container maxWidth="lg" sx={{ py: 6 }}>
                <Box sx={{ textAlign: 'center', mb: 6 }}>
                    <Typography variant="h3" component="h1" gutterBottom fontWeight="bold">
                        {t('whyChooseUs', 'Neden Biz?')}
                    </Typography>
                    <Typography variant="h6" color="text.secondary" sx={{ mt: 2, maxWidth: 800, mx: 'auto' }}>
                        {t('whyChooseUsDescription', 'Sağlık turizmi deneyiminizi en üst seviyeye çıkarmak için yanınızdayız')}
                    </Typography>
                </Box>

                <Grid container spacing={4} sx={{ mb: 6 }}>
                    {advantages.map((advantage, index) => (
                        <Grid item xs={12} md={6} key={index}>
                            <Card elevation={3} sx={{ height: '100%', transition: 'transform 0.3s', '&:hover': { transform: 'translateY(-5px)' } }}>
                                <CardContent sx={{ p: 4 }}>
                                    <Box sx={{ color: 'primary.main', mb: 2 }}>
                                        {advantage.icon}
                                    </Box>
                                    <Typography variant="h5" gutterBottom fontWeight="bold">
                                        {advantage.title}
                                    </Typography>
                                    <Typography variant="body1" color="text.secondary">
                                        {advantage.description}
                                    </Typography>
                                </CardContent>
                            </Card>
                        </Grid>
                    ))}
                </Grid>

                <Divider sx={{ my: 6 }} />

                <Box sx={{ textAlign: 'center', mb: 4 }}>
                    <Typography variant="h4" component="h2" gutterBottom fontWeight="bold">
                        {t('usInNumbers', 'Rakamlarla Biz')}
                    </Typography>
                </Box>

                <Grid container spacing={4} sx={{ mb: 6 }}>
                    {stats.map((stat, index) => (
                        <Grid item xs={6} md={3} key={index}>
                            <Paper elevation={2} sx={{ p: 3, textAlign: 'center' }}>
                                <Typography variant="h3" color="primary" fontWeight="bold">
                                    {stat.number}
                                </Typography>
                                <Typography variant="body1" color="text.secondary" sx={{ mt: 1 }}>
                                    {stat.label}
                                </Typography>
                            </Paper>
                        </Grid>
                    ))}
                </Grid>

                <Paper elevation={3} sx={{ p: 4, bgcolor: 'primary.light', color: 'primary.contrastText' }}>
                    <Typography variant="h5" gutterBottom fontWeight="bold">
                        {t('serviceGuarantee', 'Hizmet Garantimiz')}
                    </Typography>
                    <List>
                        <ListItem>
                            <ListItemIcon>
                                <CheckCircleIcon sx={{ color: 'white' }} />
                            </ListItemIcon>
                            <ListItemText primary={t('supportBeforeDuringAfter', 'Tedavi öncesi, sırası ve sonrası tam destek')} />
                        </ListItem>
                        <ListItem>
                            <ListItemIcon>
                                <CheckCircleIcon sx={{ color: 'white' }} />
                            </ListItemIcon>
                            <ListItemText primary={t('transparentPricing', 'Şeffaf fiyatlandırma, gizli maliyet yok')} />
                        </ListItem>
                        <ListItem>
                            <ListItemIcon>
                                <CheckCircleIcon sx={{ color: 'white' }} />
                            </ListItemIcon>
                            <ListItemText primary={t('internationalMedicalStandards', 'Uluslararası standartlarda tıbbi hizmet')} />
                        </ListItem>
                        <ListItem>
                            <ListItemIcon>
                                <CheckCircleIcon sx={{ color: 'white' }} />
                            </ListItemIcon>
                            <ListItemText primary={t('allInclusive', 'Vize, konaklama, transfer - her şey dahil')} />
                        </ListItem>
                    </List>
                </Paper>
            </Container>
        </>
    );
}

