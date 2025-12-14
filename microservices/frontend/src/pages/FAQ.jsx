// src/pages/FAQ.jsx
import React, { useState } from 'react';
import {
    Container, Box, Typography, Grid, Paper, useTheme,
    Accordion, AccordionSummary, AccordionDetails,
    Divider, Chip, List, ListItem, ListItemText
} from '@mui/material';
import HelpOutlineIcon from '@mui/icons-material/HelpOutline';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import LocalHospitalIcon from '@mui/icons-material/LocalHospital';
import FlightIcon from '@mui/icons-material/Flight';
import PaymentIcon from '@mui/icons-material/Payment';
import SecurityIcon from '@mui/icons-material/Security';
import { useTranslation } from 'react-i18next';

// --- ÖRNEK VERİLER ---
const faqData = [
    {
        category: 'Tedavi Süreci ve Hastaneler',
        icon: LocalHospitalIcon,
        questions: [
            { q: 'Hastanelerinizin akreditasyonu var mı?', a: 'Evet, çalıştığımız tüm hastaneler uluslararası JCI (Joint Commission International) veya yerel Sağlık Bakanlığı onayına sahiptir. Bu, en yüksek tıbbi standartları garanti eder.' },
            { q: 'Tedaviden önce doktorumla görüşebilir miyim?', a: 'Elbette. Tüm paketlerimizde, Türkiye’ye gelmeden önce uzman doktorunuzla görüntülü görüşme (online konsültasyon) yapma imkanı bulunmaktadır.' },
            { q: 'Tedavi sonrası kontrol süreçleri nasıl işliyor?', a: 'Tedaviniz sonrası ülkenize döndüğünüzde uzaktan takip ve kontrol randevuları sunuyoruz. Gerekli durumlarda lokal doktorunuza rehberlik sağlıyoruz.' },
        ]
    },
    {
        category: 'Seyahat ve Lojistik',
        icon: FlightIcon,
        questions: [
            { q: 'Vize işlemlerinde yardımcı oluyor musunuz?', a: 'Sağlık vizesi için gereken davet mektuplarını ve diğer resmi belgeleri hızlıca hazırlayıp size iletiyoruz. Vize başvurusu sizin sorumluluğunuzda olsa da, süreç boyunca danışmanlık sağlıyoruz.' },
            { q: 'Havaalanı transferleri dahil mi?', a: 'Tüm sağlık paketlerimizde, Havaalanı – Otel – Hastane arasındaki VIP transfer hizmetleri fiyata dahildir.' },
            { q: 'Konaklama seçenekleri nelerdir?', a: 'Anlaşmalı olduğumuz 4 ve 5 yıldızlı otellerde veya tam teşekküllü lüks rezidanslarda, iyileşme sürecinize uygun konforlu konaklama seçenekleri sunulmaktadır.' },
        ]
    },
    {
        category: 'Ödeme ve Sigorta',
        icon: PaymentIcon,
        questions: [
            { q: 'Hangi ödeme yöntemlerini kabul ediyorsunuz?', a: 'Banka transferi (EUR, USD, GBP), kredi kartı (VISA/Mastercard) ve hastanede nakit ödeme seçeneklerimiz mevcuttur.' },
            { q: 'Fiyatlar neden bu kadar uygun?', a: 'Türkiye, döviz kuru avantajı ve yüksek rekabet nedeniyle Batı ülkelerine kıyasla çok daha düşük maliyetli hizmet sunabilmektedir. Kaliteden asla ödün verilmez.' },
            { q: 'Uluslararası sağlık sigortam geçerli mi?', a: 'Bu, sigorta poliçenize bağlıdır. Sigorta şirketinizle görüşmeniz ve poliçenizin sağlık turizmi masraflarını kapsayıp kapsamadığını kontrol etmeniz gerekmektedir.' },
        ]
    },
];
// --- ÖRNEK VERİLER SONU ---


// --- BİLEŞEN: FAQAccordion ---
const FAQAccordion = ({ data, t }) => {
    const [expanded, setExpanded] = useState(false);

    const handleChange = (panel) => (event, isExpanded) => {
        setExpanded(isExpanded ? panel : false);
    };

    return (
        <Paper sx={{ p: { xs: 2, md: 4 }, borderRadius: 2, boxShadow: 6 }}>
            {data.questions.map((item, index) => (
                <Accordion
                    key={index}
                    expanded={expanded === `panel${index}`}
                    onChange={handleChange(`panel${index}`)}
                    sx={{ my: 1, border: '1px solid #eee', '&:before': { display: 'none' } }}
                >
                    <AccordionSummary
                        expandIcon={<ExpandMoreIcon color="primary" />}
                        aria-controls={`panel${index}bh-content`}
                        id={`panel${index}bh-header`}
                    >
                        <Typography sx={{ width: '90%', flexShrink: 0, fontWeight: 600 }}>
                            {item.q}
                        </Typography>
                    </AccordionSummary>
                    <AccordionDetails>
                        <Typography variant="body2" color="text.secondary">
                            {item.a}
                        </Typography>
                    </AccordionDetails>
                </Accordion>
            ))}
        </Paper>
    );
};
// --- BİLEŞEN SONU: FAQAccordion ---


// --- BİLEŞEN: FAQ.jsx ---
function FAQ() {
    const { t } = useTranslation();
    const theme = useTheme();

    return (
        <Container maxWidth="lg" sx={{ py: 5 }}>
            <Box textAlign="center" sx={{ mb: 5 }}>
                <HelpOutlineIcon sx={{ fontSize: 60, color: 'primary.main' }} />
                <Typography variant="h3" component="h1" gutterBottom sx={{ fontWeight: 'bold', mt: 1 }}>
                    {t('faq')} (SSS)
                </Typography>
                <Typography variant="h6" color="text.secondary">
                    {t('faqDescription', 'Sağlık turizmi sürecinizle ilgili en çok merak edilen konuları burada bulabilirsiniz.')}
                </Typography>
            </Box>

            <Grid container spacing={4}>
                {faqData.map((categoryData, catIndex) => (
                    <Grid item xs={12} key={catIndex}>
                        <Box sx={{ display: 'flex', alignItems: 'center', mb: 2, mt: catIndex > 0 ? 3 : 0 }}>
                            <categoryData.icon sx={{ fontSize: 30, color: 'secondary.main', mr: 1 }} />
                            <Typography variant="h5" sx={{ fontWeight: 700, color: 'text.primary' }}>
                                {categoryData.category}
                            </Typography>
                        </Box>
                        <Divider sx={{ mb: 2 }} />
                        <FAQAccordion data={categoryData} t={t} />
                    </Grid>
                ))}
            </Grid>

            {/* CTA - Eğer sorunuz yanıtlanmadıysa */}
            <Paper sx={{ p: { xs: 3, md: 5 }, bgcolor: theme.palette.primary.light, borderRadius: 2, mt: 6, textAlign: 'center' }}>
                <Typography variant="h5" sx={{ fontWeight: 700, mb: 2, color: 'primary.dark' }}>
                    {t('didntFindAnswer', 'Sorunuza Yanıt Bulamadınız mı?')}
                </Typography>
                <Typography variant="body1" sx={{ mb: 3, color: 'text.secondary' }}>
                    {t('supportAvailable', 'Uzman danışman ekibimiz size özel destek sağlamak için 7/24 hazır.')}
                </Typography>
                <Button
                    variant="contained"
                    color="secondary"
                    size="large"
                    onClick={() => alert('İletişim sayfasına yönlendiriliyor...')}
                >
                    {t('connectLiveSupport', 'Canlı Destek Hattına Bağlan')}
                </Button>
            </Paper>

        </Container>
    );
}

export default FAQ;