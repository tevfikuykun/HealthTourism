// src/pages/Terms.jsx
import React from 'react';
import {
    Container, Box, Typography, Paper, useTheme, Divider, List, ListItem, ListItemIcon, ListItemText
} from '@mui/material';
import GavelIcon from '@mui/icons-material/Gavel';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import SEOHead from '../components/SEO/SEOHead';
import { useTranslation } from 'react-i18next';

export default function Terms() {
    const { t } = useTranslation();
    const theme = useTheme();

    const sections = [
        {
            title: '1. Genel Hükümler',
            content: 'Bu kullanım koşulları, Health Tourism platformunu kullanırken geçerlidir. Platformu kullanarak, bu koşulları kabul etmiş sayılırsınız.'
        },
        {
            title: '2. Hizmet Kapsamı',
            content: 'Health Tourism, sağlık turizmi hizmetleri sunmaktadır. Bu hizmetler arasında hastane ve doktor seçimi, rezervasyon yönetimi, konaklama, uçak bileti, transfer ve vize desteği yer almaktadır.'
        },
        {
            title: '3. Kullanıcı Sorumlulukları',
            content: 'Kullanıcılar, platformda doğru ve güncel bilgiler sağlamakla yükümlüdür. Yanlış bilgi vermekten kaynaklanan sorunlardan Health Tourism sorumlu değildir.'
        },
        {
            title: '4. Rezervasyon ve İptal Politikası',
            content: 'Rezervasyonlar, belirtilen koşullar çerçevesinde yapılır. İptal ve değişiklik talepleri, rezervasyon türüne göre farklı koşullara tabidir. Detaylı bilgi için rezervasyon sayfasındaki iptal politikasına bakınız.'
        },
        {
            title: '5. Ödeme Koşulları',
            content: 'Ödemeler, belirtilen yöntemlerle yapılabilir. Ödeme işlemleri güvenli ödeme sistemleri üzerinden gerçekleştirilir. Fiyatlar, rezervasyon anındaki döviz kurlarına göre belirlenir.'
        },
        {
            title: '6. Sağlık Hizmetleri',
            content: 'Health Tourism, sağlık hizmeti sağlayıcısı değildir. Tıbbi hizmetler, partner hastaneler ve doktorlar tarafından sağlanmaktadır. Health Tourism, bu hizmetlerin kalitesinden sorumlu değildir.'
        },
        {
            title: '7. Fikri Mülkiyet',
            content: 'Platformdaki tüm içerik, tasarım ve yazılım, Health Tourism\'e aittir. İzinsiz kullanım yasaktır.'
        },
        {
            title: '8. Sorumluluk Sınırlaması',
            content: 'Health Tourism, platformun kesintisiz çalışmasını garanti etmez. Teknik sorunlar, doğal afetler veya üçüncü taraf hizmetlerindeki aksaklıklardan kaynaklanan zararlardan sorumlu değildir.'
        },
        {
            title: '9. Değişiklikler',
            content: 'Health Tourism, bu kullanım koşullarını herhangi bir zamanda değiştirme hakkını saklı tutar. Önemli değişiklikler kullanıcılara bildirilir.'
        },
        {
            title: '10. Uygulanacak Hukuk',
            content: 'Bu kullanım koşulları, Türkiye Cumhuriyeti yasalarına tabidir. Herhangi bir uyuşmazlık durumunda İstanbul mahkemeleri yetkilidir.'
        }
    ];

    return (
        <>
            <SEOHead
                title="Kullanım Koşulları - Health Tourism"
                description="Health Tourism kullanım koşulları. Platform kullanımı, rezervasyon, ödeme ve sorumluluklar hakkında bilgiler."
                keywords="kullanım koşulları, şartlar, rezervasyon, ödeme, sorumluluk"
            />
            <Container maxWidth="lg" sx={{ py: 6 }}>
                <Box sx={{ textAlign: 'center', mb: 6 }}>
                    <GavelIcon sx={{ fontSize: 60, color: 'primary.main', mb: 2 }} />
                    <Typography variant="h3" component="h1" gutterBottom fontWeight="bold">
                        {t('termsOfService', 'Kullanım Koşulları')}
                    </Typography>
                    <Typography variant="body1" color="text.secondary" sx={{ mt: 2 }}>
                        {t('lastUpdated', 'Son Güncelleme')}: {new Date().toLocaleDateString('tr-TR', { year: 'numeric', month: 'long', day: 'numeric' })}
                    </Typography>
                </Box>

                <Paper elevation={3} sx={{ p: 4 }}>
                    <Typography variant="body1" paragraph sx={{ mb: 4 }}>
                        Lütfen bu kullanım koşullarını dikkatlice okuyun. Health Tourism platformunu kullanarak, 
                        bu koşulları kabul etmiş sayılırsınız.
                    </Typography>

                    {sections.map((section, index) => (
                        <Box key={index} sx={{ mb: 4 }}>
                            <Typography variant="h5" gutterBottom fontWeight="bold" color="primary">
                                {section.title}
                            </Typography>
                            <Typography variant="body1" paragraph>
                                {section.content}
                            </Typography>
                            {index < sections.length - 1 && <Divider sx={{ mt: 3 }} />}
                        </Box>
                    ))}

                    <Box sx={{ mt: 4, p: 3, bgcolor: 'info.light', borderRadius: 2 }}>
                        <Typography variant="h6" gutterBottom fontWeight="bold">
                            {t('userRights', 'Kullanıcı Hakları')}
                        </Typography>
                        <List>
                            <ListItem>
                                <ListItemIcon>
                                    <CheckCircleIcon color="primary" />
                                </ListItemIcon>
                                <ListItemText primary={t('reservationCancelChangeRights', 'Rezervasyon iptal ve değişiklik hakları')} />
                            </ListItem>
                            <ListItem>
                                <ListItemIcon>
                                    <CheckCircleIcon color="primary" />
                                </ListItemIcon>
                                <ListItemText primary="Şeffaf fiyatlandırma ve gizli maliyet yok" />
                            </ListItem>
                            <ListItem>
                                <ListItemIcon>
                                    <CheckCircleIcon color="primary" />
                                </ListItemIcon>
                                <ListItemText primary="7/24 müşteri desteği" />
                            </ListItem>
                            <ListItem>
                                <ListItemIcon>
                                    <CheckCircleIcon color="primary" />
                                </ListItemIcon>
                                <ListItemText primary="Kişisel veri koruma hakları" />
                            </ListItem>
                        </List>
                    </Box>
                </Paper>
            </Container>
        </>
    );
}

