// src/pages/Home.jsx
import React from 'react';
import { motion } from 'framer-motion';
import {
    Container, Typography, Box, Grid, Button, Paper,
    useTheme, Chip, Divider, Avatar
} from '@mui/material';
import LocalHospitalIcon from '@mui/icons-material/LocalHospital';
import MedicalServicesIcon from '@mui/icons-material/MedicalServices';
import FlightTakeoffIcon from '@mui/icons-material/FlightTakeoff';
import HotelIcon from '@mui/icons-material/Hotel';
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
import FavoriteIcon from '@mui/icons-material/Favorite';
import VpnKeyIcon from '@mui/icons-material/VpnKey';
import { Link } from 'react-router-dom';
import { useTranslation } from '../i18n';
import { fadeInUp, staggerContainer, staggerItem, hoverLift } from '../utils/ui-helpers';
import SmartSearch from '../components/SmartSearch/SmartSearch';
import LazySection from '../components/LazySection/LazySection';

function Home() {
    const { t } = useTranslation();
    const theme = useTheme();

    // Özel Hizmet Kartı Bileşeni - Material-UI + Tailwind + Framer Motion
    // Dark mode için elevation optimizasyonu eklendi
    const ServiceCard = ({ icon: Icon, title, description, to, index }) => {
        const isDark = theme.palette.mode === 'dark';
        return (
            <motion.div
                variants={staggerItem}
                initial="initial"
                animate="animate"
                {...hoverLift}
                className="h-full"
            >
                <Paper
                    elevation={isDark ? 1 : 3}
                    className="shadow-lg hover:shadow-xl transition-shadow duration-300"
                    sx={{
                        p: 3,
                        textAlign: 'left',
                        height: '100%',
                        display: 'flex',
                        flexDirection: 'column',
                        justifyContent: 'flex-start',
                        borderRadius: '12px',
                        borderLeft: `5px solid ${theme.palette.primary.main}`,
                        bgcolor: isDark ? 'rgba(255, 255, 255, 0.05)' : 'background.paper',
                        '&:hover': {
                            elevation: isDark ? 4 : 6,
                        },
                    }}
                >
                    <Icon color="primary" sx={{ fontSize: 40, mb: 2 }} />
                    <Typography variant="h6" gutterBottom sx={{ fontWeight: 700 }}>{title}</Typography>
                    <Typography variant="body2" color="text.secondary" sx={{ flexGrow: 1, mb: 2 }}>{description}</Typography>
                    <Button
                        component={Link}
                        to={to}
                        size="small"
                        variant="outlined"
                        color="secondary"
                        endIcon={<ArrowForwardIcon />}
                        className="mt-auto"
                        sx={{ mt: 'auto' }}
                    >
                        {t('explore', 'Keşfet')}
                    </Button>
                </Paper>
            </motion.div>
        );
    };

    // Neden Bizi Seçmelisiniz Kartı - Material-UI + Tailwind + Framer Motion
    const WhyUsCard = ({ icon: Icon, title, description, index }) => {
        const cardTheme = useTheme();
        const isDark = cardTheme.palette.mode === 'dark';
        return (
            <motion.div
                variants={staggerItem}
                initial="initial"
                animate="animate"
                {...hoverLift}
            >
                <Box 
                    className="text-center p-4 rounded-lg backdrop-blur-sm"
                    sx={{ 
                        textAlign: 'center', 
                        p: 2,
                        bgcolor: isDark 
                            ? 'rgba(255, 255, 255, 0.05)' 
                            : 'rgba(255, 255, 255, 0.5)',
                        border: isDark 
                            ? '1px solid rgba(255, 255, 255, 0.1)' 
                            : 'none',
                        borderRadius: 2,
                    }}
                >
                    <Icon color="secondary" sx={{ fontSize: 60, mb: 1.5 }} />
                    <Typography 
                        variant="h6" 
                        sx={{ 
                            fontWeight: 700, 
                            mb: 1,
                            color: 'text.primary',
                        }}
                    >
                        {title}
                    </Typography>
                    <Typography 
                        variant="body2" 
                        sx={{ 
                            color: 'text.secondary',
                        }}
                    >
                        {description}
                    </Typography>
                </Box>
            </motion.div>
        );
    };

    return (
        <Box>
            {/* 1. Hero Section (Kahraman Bölümü) - Material-UI + Tailwind + Framer Motion */}
            <motion.div
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                transition={{ duration: 0.6 }}
            >
                <Box
                    className="bg-gradient-to-r from-blue-900 via-blue-800 to-blue-700 text-white py-16 md:py-24 text-left bg-cover bg-center"
                    sx={{
                        bgcolor: 'primary.dark',
                        color: 'white',
                        py: { xs: 8, md: 15 },
                        textAlign: 'left',
                        position: 'relative',
                        overflow: 'hidden',
                        '&::before': {
                            content: '""',
                            position: 'absolute',
                            top: 0,
                            left: 0,
                            right: 0,
                            bottom: 0,
                            backgroundImage: 'url(https://images.unsplash.com/photo-1576091160399-112ba8d25d1f?w=1600&q=80)',
                            backgroundSize: 'cover',
                            backgroundPosition: 'center',
                            opacity: 0.3,
                            zIndex: 0,
                        },
                        '&::after': {
                            content: '""',
                            position: 'absolute',
                            top: 0,
                            left: 0,
                            right: 0,
                            bottom: 0,
                            background: 'linear-gradient(to right, rgba(0, 70, 100, 0.95), rgba(0, 70, 100, 0.7))',
                            zIndex: 1,
                        },
                    }}
                >
                    <Container maxWidth="lg" sx={{ position: 'relative', zIndex: 2 }}>
                        <motion.div
                            variants={fadeInUp}
                            initial="initial"
                            animate="animate"
                        >
                            <Grid container spacing={4} alignItems="center">
                                <Grid item xs={12}>
                                    {/* YENİ SLOGAN */}
                                    <Typography variant="h2" component="h1" gutterBottom className="font-extrabold leading-tight" sx={{ fontWeight: 800, lineHeight: 1.2, mb: 2 }}>
                                        {t('heroTitle', 'Kişiselleştirilmiş Sağlık Yolculuğunuz: Profesyonel Tedavi, Konforlu Konaklama')}
                                    </Typography>
                                    {/* YENİ ALT BAŞLIK */}
                                    <Typography variant="h5" className="mb-6 font-light" sx={{ mb: 4, fontWeight: 300 }}>
                                        {t('heroSubtitle', 'Dünya standartlarında uzmanları ve iyileşmenize odaklı lüks konaklama seçeneklerini keşfedin.')}
                                    </Typography>

                                    {/* SMART SEARCH BAR */}
                                    <Box sx={{ mb: 4 }}>
                                        <SmartSearch variant="hero" />
                                    </Box>

                                    {/* SADELEŞTİRİLMİŞ CTA BUTONLARI */}
                                    <Box
                                        component={motion.div}
                                        initial={{ opacity: 0, y: 20 }}
                                        animate={{ opacity: 1, y: 0 }}
                                        transition={{ delay: 0.5 }}
                                        sx={{
                                            display: 'flex',
                                            justifyContent: 'center',
                                            gap: 2,
                                            flexWrap: 'wrap',
                                            mt: 4,
                                        }}
                                    >
                                        <Button
                                            variant="contained"
                                            size="large"
                                            sx={{
                                                minHeight: '52px',
                                                px: 5,
                                                py: 1.75,
                                                fontSize: '1rem',
                                                fontWeight: 700,
                                                textTransform: 'none',
                                                borderRadius: '16px',
                                                bgcolor: '#0EA5E9',
                                                color: 'white',
                                                boxShadow: '0 4px 14px rgba(14, 165, 233, 0.25)',
                                                transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
                                                '&:hover': {
                                                    bgcolor: '#0284C7',
                                                    boxShadow: '0 8px 24px rgba(14, 165, 233, 0.35)',
                                                    transform: 'translateY(-2px)',
                                                },
                                                '&:active': {
                                                    transform: 'translateY(0)',
                                                    boxShadow: '0 4px 14px rgba(14, 165, 233, 0.25)',
                                                },
                                            }}
                                            component={Link}
                                            to="/reservations"
                                        >
                                            {t('getFreeQuote', 'Ücretsiz Teklif Alın')}
                                        </Button>
                                        <Button
                                            variant="outlined"
                                            size="large"
                                            sx={{
                                                minHeight: '52px',
                                                px: 5,
                                                py: 1.75,
                                                fontSize: '1rem',
                                                fontWeight: 700,
                                                textTransform: 'none',
                                                borderRadius: '16px',
                                                color: 'white',
                                                borderColor: 'rgba(255, 255, 255, 0.8)',
                                                borderWidth: '2px',
                                                bgcolor: 'rgba(255, 255, 255, 0.05)',
                                                backdropFilter: 'blur(10px)',
                                                transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
                                                '&:hover': {
                                                    borderColor: 'white',
                                                    borderWidth: '2px',
                                                    bgcolor: 'rgba(255, 255, 255, 0.15)',
                                                    transform: 'translateY(-2px)',
                                                    boxShadow: '0 8px 24px rgba(255, 255, 255, 0.1)',
                                                },
                                                '&:active': {
                                                    transform: 'translateY(0)',
                                                    bgcolor: 'rgba(255, 255, 255, 0.1)',
                                                },
                                            }}
                                            component={Link}
                                            to="/hospitals"
                                        >
                                            {t('home.browseHospitals', 'Anlaşmalı Hastaneleri Keşfet')}
                                        </Button>
                                    </Box>
                                </Grid>
                            </Grid>
                        </motion.div>
                    </Container>
                </Box>
            </motion.div>

            {/* 2. Temel Hizmetler Bölümü (Cards) - Material-UI + Tailwind + Framer Motion */}
            <Container maxWidth="lg" className="py-16" sx={{ py: 10 }}>
                <motion.div
                    variants={fadeInUp}
                    initial="initial"
                    whileInView="animate"
                    viewport={{ once: true }}
                >
                    <Chip label={t('home.services')} color="secondary" className="font-bold" sx={{ mb: 1, fontWeight: 'bold' }} />
                    <Typography variant="h4" className="font-bold mb-2" sx={{ mb: 2, fontWeight: 700 }}>
                        {t('home.servicesTitle')}
                    </Typography>
                    <Typography variant="body1" color="text.secondary" className="mb-12" sx={{ mb: 6 }}>
                        {t('home.servicesDescription')}
                    </Typography>
                    <motion.div
                        variants={staggerContainer}
                        initial="hidden"
                        whileInView="show"
                        viewport={{ once: true }}
                    >
                        <Grid container spacing={4}>
                            <Grid item xs={12} sm={6} md={3}>
                                <ServiceCard
                                    icon={LocalHospitalIcon}
                                    title={t('home.accreditedHospitals')}
                                    description={t('home.accreditedHospitalsDesc')}
                                    to="/hospitals"
                                    index={0}
                                />
                            </Grid>
                            <Grid item xs={12} sm={6} md={3}>
                                <ServiceCard
                                    icon={MedicalServicesIcon}
                                    title={t('home.personalTreatmentPlan')}
                                    description={t('home.personalTreatmentPlanDesc')}
                                    to="/doctors"
                                    index={1}
                                />
                            </Grid>
                            <Grid item xs={12} sm={6} md={3}>
                                <ServiceCard
                                    icon={HotelIcon}
                                    title={t('home.accommodationDuringTreatment')}
                                    description={t('home.accommodationDuringTreatmentDesc')}
                                    to="/accommodations"
                                    index={2}
                                />
                            </Grid>
                            <Grid item xs={12} sm={6} md={3}>
                                <ServiceCard
                                    icon={FlightTakeoffIcon}
                                    title={t('home.travelSupport')}
                                    description={t('home.travelSupportDesc')}
                                    to="/transfers"
                                    index={3}
                                />
                            </Grid>
                        </Grid>
                    </motion.div>
                </motion.div>
            </Container>

            {/* 3. Neden Bizi Seçmelisiniz (Güven Bölümü) - Material-UI + Tailwind + Framer Motion */}
            <Box 
                className="py-16" 
                sx={{ 
                    bgcolor: theme.palette.mode === 'dark' 
                        ? 'rgba(15, 23, 42, 0.5)' 
                        : theme.palette.primary.light, 
                    py: 8 
                }}
            >
                <Container maxWidth="lg">
                    <motion.div
                        variants={fadeInUp}
                        initial="initial"
                        whileInView="animate"
                        viewport={{ once: true }}
                    >
                        <Typography 
                            variant="h4" 
                            align="center" 
                            className="font-bold mb-2" 
                            sx={{ 
                                mb: 2, 
                                fontWeight: 700, 
                                color: theme.palette.mode === 'dark' 
                                    ? 'text.primary' 
                                    : 'primary.dark' 
                            }}
                        >
                            {t('home.whyChooseUs')}
                        </Typography>
                        <Divider sx={{ mb: 6, width: '100px', mx: 'auto', bgcolor: theme.palette.secondary.main, height: 3 }} />
                        <motion.div
                            variants={staggerContainer}
                            initial="hidden"
                            whileInView="show"
                            viewport={{ once: true }}
                        >
                            <Grid container spacing={4}>
                                <Grid item xs={12} md={4}>
                                    <WhyUsCard
                                        icon={FavoriteIcon}
                                        title={t('home.personalHealthConsultant')}
                                        description={t('home.personalHealthConsultantDesc')}
                                        index={0}
                                    />
                                </Grid>
                                <Grid item xs={12} md={4}>
                                    <WhyUsCard
                                        icon={CheckCircleOutlineIcon}
                                        title={t('home.accreditedQuality')}
                                        description={t('home.accreditedQualityDesc')}
                                        index={1}
                                    />
                                </Grid>
                                <Grid item xs={12} md={4}>
                                    <WhyUsCard
                                        icon={VpnKeyIcon}
                                        title={t('home.transparentPricing')}
                                        description={t('home.transparentPricingDesc')}
                                        index={2}
                                    />
                                </Grid>
                            </Grid>
                        </motion.div>
                    </motion.div>
                </Container>
            </Box>

            {/* 4. Nasıl Çalışır Bölümü (Adımlar) - Lazy Loading ile */}
            <LazySection fallback={<Box sx={{ py: 10, minHeight: 400 }} />}>
                <Box sx={{ py: 10 }}>
                    <Container maxWidth="md">
                        <motion.div
                            initial={{ opacity: 0, y: 20 }}
                            whileInView={{ opacity: 1, y: 0 }}
                            viewport={{ once: true }}
                            transition={{ duration: 0.6 }}
                        >
                            <Typography variant="h4" align="center" sx={{ mb: 6, fontWeight: 700 }}>
                                {t('home.howItWorks')}
                            </Typography>
                            <Grid container spacing={4} sx={{ textAlign: 'center' }}>

                        {/* Adım 1 */}
                        <Grid item xs={12} md={4}>
                            <Box sx={{ p: 3, border: `1px solid ${theme.palette.divider}`, borderRadius: '8px' }}>
                                <Chip label={t('home.step1')} color="secondary" sx={{ mb: 2, fontWeight: 'bold' }} />
                                <CheckCircleOutlineIcon color="primary" sx={{ fontSize: 48, mb: 1 }} />
                                <Typography variant="h6" sx={{ fontWeight: 600 }}>{t('home.step1Title')}</Typography>
                                <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                                    {t('home.step1Desc')}
                                </Typography>
                            </Box>
                        </Grid>
                        {/* Adım 2 */}
                        <Grid item xs={12} md={4}>
                            <Box sx={{ p: 3, border: `1px solid ${theme.palette.divider}`, borderRadius: '8px' }}>
                                <Chip label={t('home.step2')} color="secondary" sx={{ mb: 2, fontWeight: 'bold' }} />
                                <CheckCircleOutlineIcon color="primary" sx={{ fontSize: 48, mb: 1 }} />
                                <Typography variant="h6" sx={{ fontWeight: 600 }}>{t('home.step2Title')}</Typography>
                                <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                                    {t('home.step2Desc')}
                                </Typography>
                            </Box>
                        </Grid>
                        {/* Adım 3 */}
                        <Grid item xs={12} md={4}>
                            <Box sx={{ p: 3, border: `1px solid ${theme.palette.divider}`, borderRadius: '8px' }}>
                                <Chip label={t('home.step3')} color="secondary" sx={{ mb: 2, fontWeight: 'bold' }} />
                                <CheckCircleOutlineIcon color="primary" sx={{ fontSize: 48, mb: 1 }} />
                                <Typography variant="h6" sx={{ fontWeight: 600 }}>{t('home.step3Title')}</Typography>
                                <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                                    {t('home.step3Desc')}
                                </Typography>
                            </Box>
                        </Grid>
                            </Grid>
                            <Box sx={{ textAlign: 'center', mt: 6 }}>
                                <Button
                                    variant="contained"
                                    color="primary"
                                    size="large"
                                    sx={{ py: 1.5, px: 6, fontSize: '1.1rem' }}
                                    component={Link}
                                    to="/reservations"
                                >
                                    {t('home.getStarted')}
                                </Button>
                            </Box>
                        </motion.div>
                    </Container>
                </Box>
            </LazySection>

            {/* 5. Trust Signals - Partnerler ve Akreditasyonlar */}
            <LazySection fallback={<Box sx={{ py: 8, minHeight: 300 }} />}>
                <Box 
                    sx={{ 
                        py: 8,
                        bgcolor: theme.palette.mode === 'dark' 
                            ? 'rgba(15, 23, 42, 0.3)' 
                            : 'rgba(249, 250, 251, 1)',
                    }}
                >
                    <Container maxWidth="lg">
                        <motion.div
                            initial={{ opacity: 0, y: 20 }}
                            whileInView={{ opacity: 1, y: 0 }}
                            viewport={{ once: true }}
                            transition={{ duration: 0.6 }}
                        >
                            <Typography 
                                variant="h5" 
                                align="center" 
                                sx={{ mb: 1, fontWeight: 700, color: 'text.primary' }}
                            >
                                {t('home.trustedBy', 'Güvenilir ve Akredite')}
                            </Typography>
                            <Typography 
                                variant="body2" 
                                align="center" 
                                color="text.secondary" 
                                sx={{ mb: 4 }}
                            >
                                {t('home.trustedByDesc', 'Dünya standartlarında akreditasyonlar ve güvenilir ortaklarımız')}
                            </Typography>
                            
                            {/* Accreditation Logos */}
                            <Grid container spacing={4} sx={{ mb: 6 }}>
                                <Grid item xs={6} sm={4} md={3}>
                                    <Box
                                        sx={{
                                            display: 'flex',
                                            flexDirection: 'column',
                                            alignItems: 'center',
                                            p: 2,
                                            borderRadius: 2,
                                            bgcolor: theme.palette.mode === 'dark' 
                                                ? 'rgba(255, 255, 255, 0.05)' 
                                                : 'white',
                                        }}
                                    >
                                        <Avatar
                                            sx={{
                                                width: 80,
                                                height: 80,
                                                bgcolor: 'primary.light',
                                                mb: 1,
                                            }}
                                        >
                                            <Typography variant="h6" sx={{ fontWeight: 700, color: 'white' }}>
                                                JCI
                                            </Typography>
                                        </Avatar>
                                        <Typography variant="caption" sx={{ fontWeight: 600, textAlign: 'center' }}>
                                            Joint Commission International
                                        </Typography>
                                    </Box>
                                </Grid>
                                <Grid item xs={6} sm={4} md={3}>
                                    <Box
                                        sx={{
                                            display: 'flex',
                                            flexDirection: 'column',
                                            alignItems: 'center',
                                            p: 2,
                                            borderRadius: 2,
                                            bgcolor: theme.palette.mode === 'dark' 
                                                ? 'rgba(255, 255, 255, 0.05)' 
                                                : 'white',
                                        }}
                                    >
                                        <Avatar
                                            sx={{
                                                width: 80,
                                                height: 80,
                                                bgcolor: 'success.light',
                                                mb: 1,
                                            }}
                                        >
                                            <Typography variant="h6" sx={{ fontWeight: 700, color: 'white' }}>
                                                ISO
                                            </Typography>
                                        </Avatar>
                                        <Typography variant="caption" sx={{ fontWeight: 600, textAlign: 'center' }}>
                                            ISO 9001:2015
                                        </Typography>
                                    </Box>
                                </Grid>
                                <Grid item xs={6} sm={4} md={3}>
                                    <Box
                                        sx={{
                                            display: 'flex',
                                            flexDirection: 'column',
                                            alignItems: 'center',
                                            p: 2,
                                            borderRadius: 2,
                                            bgcolor: theme.palette.mode === 'dark' 
                                                ? 'rgba(255, 255, 255, 0.05)' 
                                                : 'white',
                                        }}
                                    >
                                        <Avatar
                                            sx={{
                                                width: 80,
                                                height: 80,
                                                bgcolor: 'warning.light',
                                                mb: 1,
                                            }}
                                        >
                                            <Typography variant="h6" sx={{ fontWeight: 700, color: 'white' }}>
                                                CE
                                            </Typography>
                                        </Avatar>
                                        <Typography variant="caption" sx={{ fontWeight: 600, textAlign: 'center' }}>
                                            CE Certification
                                        </Typography>
                                    </Box>
                                </Grid>
                                <Grid item xs={6} sm={4} md={3}>
                                    <Box
                                        sx={{
                                            display: 'flex',
                                            flexDirection: 'column',
                                            alignItems: 'center',
                                            p: 2,
                                            borderRadius: 2,
                                            bgcolor: theme.palette.mode === 'dark' 
                                                ? 'rgba(255, 255, 255, 0.05)' 
                                                : 'white',
                                        }}
                                    >
                                        <Avatar
                                            sx={{
                                                width: 80,
                                                height: 80,
                                                bgcolor: 'info.light',
                                                mb: 1,
                                            }}
                                        >
                                            <Typography variant="h6" sx={{ fontWeight: 700, color: 'white' }}>
                                                WHO
                                            </Typography>
                                        </Avatar>
                                        <Typography variant="caption" sx={{ fontWeight: 600, textAlign: 'center' }}>
                                            WHO Collaborating
                                        </Typography>
                                    </Box>
                                </Grid>
                            </Grid>

                            {/* Partner Hospitals */}
                            <Divider sx={{ my: 4 }} />
                            <Typography 
                                variant="h6" 
                                align="center" 
                                sx={{ mb: 4, fontWeight: 700 }}
                            >
                                {t('home.partnerHospitals', 'Partner Hastanelerimiz')}
                            </Typography>
                            <Grid container spacing={2.5} justifyContent="center" sx={{ alignItems: 'stretch' }}>
                                {['Acıbadem', 'Memorial', 'Liv Hospital', 'Medical Park', 'Anadolu Sağlık', 'Amerikan Hastanesi'].map((name, idx) => (
                                    <Grid item xs={6} sm={4} md={2} key={idx} sx={{ display: 'flex' }}>
                                        <motion.div
                                            whileHover={{ y: -8, scale: 1.02 }}
                                            transition={{ duration: 0.2 }}
                                            style={{ width: '100%', height: '100%' }}
                                        >
                                            <Paper
                                                elevation={0}
                                                sx={{
                                                    p: 3,
                                                    textAlign: 'center',
                                                    borderRadius: '20px',
                                                    border: `1.5px solid ${theme.palette.mode === 'dark' ? 'rgba(255, 255, 255, 0.1)' : 'rgba(79, 70, 229, 0.1)'}`,
                                                    bgcolor: theme.palette.mode === 'dark' 
                                                        ? 'rgba(255, 255, 255, 0.05)' 
                                                        : 'white',
                                                    transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
                                                    cursor: 'pointer',
                                                    width: '100%',
                                                    height: '100%',
                                                    display: 'flex',
                                                    flexDirection: 'column',
                                                    alignItems: 'center',
                                                    justifyContent: 'center',
                                                    position: 'relative',
                                                    overflow: 'hidden',
                                                    '&::before': {
                                                        content: '""',
                                                        position: 'absolute',
                                                        top: 0,
                                                        left: 0,
                                                        right: 0,
                                                        height: '3px',
                                                        bgcolor: 'primary.main',
                                                        opacity: 0,
                                                        transition: 'opacity 0.3s ease',
                                                    },
                                                    '&:hover': {
                                                        borderColor: theme.palette.primary.main,
                                                        boxShadow: theme.palette.mode === 'dark'
                                                            ? '0 12px 24px rgba(79, 70, 229, 0.2)'
                                                            : '0 12px 24px rgba(79, 70, 229, 0.15)',
                                                        bgcolor: theme.palette.mode === 'dark'
                                                            ? 'rgba(79, 70, 229, 0.1)'
                                                            : 'rgba(79, 70, 229, 0.02)',
                                                        '&::before': {
                                                            opacity: 1,
                                                        },
                                                    },
                                                }}
                                            >
                                                <Box
                                                    sx={{
                                                        width: 64,
                                                        height: 64,
                                                        borderRadius: '16px',
                                                        bgcolor: 'primary.main',
                                                        display: 'flex',
                                                        alignItems: 'center',
                                                        justifyContent: 'center',
                                                        mb: 2.5,
                                                        boxShadow: '0 4px 12px rgba(79, 70, 229, 0.3)',
                                                    }}
                                                >
                                                    <LocalHospitalIcon 
                                                        sx={{ 
                                                            fontSize: 32, 
                                                            color: 'white',
                                                        }} 
                                                    />
                                                </Box>
                                                <Typography 
                                                    variant="body2" 
                                                    sx={{ 
                                                        fontWeight: 700,
                                                        color: 'text.primary',
                                                        fontSize: '0.875rem',
                                                        lineHeight: 1.4,
                                                        textAlign: 'center',
                                                    }}
                                                >
                                                    {name}
                                                </Typography>
                                            </Paper>
                                        </motion.div>
                                    </Grid>
                                ))}
                            </Grid>
                        </motion.div>
                    </Container>
                </Box>
            </LazySection>
        </Box>
    );
}

export default Home;