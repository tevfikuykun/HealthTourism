// src/pages/Privacy.jsx
import React from 'react';
import {
    Container, Box, Typography, Paper, useTheme, Divider
} from '@mui/material';
import SecurityIcon from '@mui/icons-material/Security';
import SEOHead from '../components/SEO/SEOHead';
import { useTranslation } from 'react-i18next';

export default function Privacy() {
    const { t } = useTranslation();
    const theme = useTheme();

    const sections = [
        {
            title: '1. Kişisel Verilerin Toplanması',
            content: 'Health Tourism olarak, hizmetlerimizi sunabilmek için belirli kişisel verilerinizi topluyoruz. Bu veriler arasında ad, soyad, e-posta adresi, telefon numarası, sağlık bilgileri, pasaport bilgileri ve ödeme bilgileri yer almaktadır.'
        },
        {
            title: '2. Verilerin Kullanım Amacı',
            content: 'Toplanan kişisel verileriniz, rezervasyon işlemlerinizin gerçekleştirilmesi, sağlık hizmetlerinin planlanması, iletişim kurulması, yasal yükümlülüklerin yerine getirilmesi ve hizmet kalitesinin artırılması amacıyla kullanılmaktadır.'
        },
        {
            title: '3. Verilerin Paylaşılması',
            content: 'Kişisel verileriniz, yalnızca hizmetlerin sunulması için gerekli olan partner hastaneler, doktorlar, konaklama tesisleri ve lojistik hizmet sağlayıcıları ile paylaşılmaktadır. Verileriniz hiçbir zaman üçüncü taraflara satılmamaktadır.'
        },
        {
            title: '4. Veri Güvenliği',
            content: 'Tüm kişisel verileriniz SSL/TLS şifreleme protokolleri ile korunmaktadır. Veritabanlarımız güvenli sunucularda saklanmakta ve düzenli olarak yedeklenmektedir. Sağlık verileriniz özel olarak korunmakta ve yalnızca yetkili personel tarafından erişilebilmektedir.'
        },
        {
            title: '5. Çerezler (Cookies)',
            content: 'Web sitemizde kullanıcı deneyimini iyileştirmek için çerezler kullanılmaktadır. Çerez tercihlerinizi tarayıcı ayarlarınızdan yönetebilirsiniz.'
        },
        {
            title: '6. Haklarınız',
            content: 'KVKK ve GDPR kapsamında, kişisel verilerinize erişim, düzeltme, silme, itiraz etme ve veri taşınabilirliği haklarınız bulunmaktadır. Bu haklarınızı kullanmak için bizimle iletişime geçebilirsiniz.'
        },
        {
            title: '7. Veri Saklama Süresi',
            content: 'Kişisel verileriniz, yasal saklama süreleri ve hizmet süresi boyunca saklanmaktadır. Bu süre sona erdiğinde, verileriniz güvenli bir şekilde silinmektedir.'
        },
        {
            title: '8. İletişim',
            content: 'Gizlilik politikamız hakkında sorularınız için: privacy@healthtourism.com adresine e-posta gönderebilir veya +90 (212) XXX XX XX numaralı telefonu arayabilirsiniz.'
        }
    ];

    return (
        <>
            <SEOHead
                title="Gizlilik Politikası - Health Tourism"
                description="Health Tourism gizlilik politikası. Kişisel verilerinizin nasıl toplandığı, kullanıldığı ve korunduğu hakkında bilgiler."
                keywords="gizlilik politikası, KVKK, GDPR, veri koruma, kişisel veri"
            />
            <Container maxWidth="lg" sx={{ py: 6 }}>
                <Box sx={{ textAlign: 'center', mb: 6 }}>
                    <SecurityIcon sx={{ fontSize: 60, color: 'primary.main', mb: 2 }} />
                    <Typography variant="h3" component="h1" gutterBottom fontWeight="bold">
                        {t('privacyPolicy', 'Gizlilik Politikası')}
                    </Typography>
                    <Typography variant="body1" color="text.secondary" sx={{ mt: 2 }}>
                        {t('lastUpdated', 'Son Güncelleme')}: {new Date().toLocaleDateString('tr-TR', { year: 'numeric', month: 'long', day: 'numeric' })}
                    </Typography>
                </Box>

                <Paper elevation={3} sx={{ p: 4 }}>
                    <Typography variant="body1" paragraph sx={{ mb: 4 }}>
                        Health Tourism olarak, kişisel verilerinizin korunmasına büyük önem veriyoruz. Bu gizlilik politikası, 
                        kişisel verilerinizin nasıl toplandığı, kullanıldığı, saklandığı ve korunduğu hakkında bilgi vermektedir.
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

                    <Box sx={{ mt: 4, p: 3, bgcolor: 'primary.light', borderRadius: 2 }}>
                        <Typography variant="h6" gutterBottom fontWeight="bold">
                        {t('importantNote', 'Önemli Not')}
                    </Typography>
                    <Typography variant="body2">
                        {t('privacyPolicyUpdateNote', 'Bu gizlilik politikası, yasal düzenlemelere uygun olarak düzenli olarak güncellenmektedir. Önemli değişiklikler durumunda size bildirim yapılacaktır.')}
                    </Typography>
                    </Box>
                </Paper>
            </Container>
        </>
    );
}

