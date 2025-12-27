import React, { useState } from 'react';
import { Box, Button, Menu, MenuItem, Typography, Paper, Grid, Link as MuiLink, Divider } from '@mui/material';
import { motion, AnimatePresence } from 'framer-motion';
import { Link as RouterLink, useNavigate } from 'react-router-dom';
import { useTranslation } from '../../i18n';
import {
  LocalHospital,
  MedicalServices,
  Support,
  FlightTakeoff,
  DirectionsCar,
  AccountBalance,
  Hotel,
  Spa,
  FitnessCenter,
  Face,
  Favorite,
  Psychology,
  Healing,
  Coronavirus,
} from '@mui/icons-material';

/**
 * Navigation Component with Mega Menu
 * Tedaviler, Hastaneler ve Destek birimleri için zengin içerikli, ikonlu açılır menüler
 */
const Navigation = () => {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [treatmentsAnchor, setTreatmentsAnchor] = useState(null);
  const [hospitalsAnchor, setHospitalsAnchor] = useState(null);
  const [supportAnchor, setSupportAnchor] = useState(null);

  // Treatments Mega Menu Data
  const treatmentCategories = [
    {
      title: t('treatments.cosmetic', 'Estetik ve Güzellik'),
      icon: <Face fontSize="small" />,
      items: [
        { label: t('treatments.rhinoplasty', 'Rinoplasti'), path: '/treatments/rhinoplasty' },
        { label: t('treatments.breastAugmentation', 'Meme Büyütme'), path: '/treatments/breast-augmentation' },
        { label: t('treatments.liposuction', 'Liposuction'), path: '/treatments/liposuction' },
        { label: t('treatments.hairTransplant', 'Saç Ekimi'), path: '/treatments/hair-transplant' },
      ],
    },
    {
      title: t('treatments.dental', 'Diş Hekimliği'),
      icon: <MedicalServices fontSize="small" />,
      items: [
        { label: t('treatments.dentalImplant', 'Diş İmplantı'), path: '/treatments/dental-implant' },
        { label: t('treatments.veneers', 'Lamina'), path: '/treatments/veneers' },
        { label: t('treatments.teethWhitening', 'Diş Beyazlatma'), path: '/treatments/teeth-whitening' },
        { label: t('treatments.orthodontics', 'Ortodonti'), path: '/treatments/orthodontics' },
      ],
    },
    {
      title: t('treatments.health', 'Sağlık Kontrolü'),
      icon: <Healing fontSize="small" />,
      items: [
        { label: t('treatments.checkup', 'Genel Check-up'), path: '/treatments/checkup' },
        { label: t('treatments.cardiology', 'Kardiyoloji'), path: '/treatments/cardiology' },
        { label: t('treatments.oncology', 'Onkoloji'), path: '/treatments/oncology' },
        { label: t('treatments.orthopedics', 'Ortopedi'), path: '/treatments/orthopedics' },
      ],
    },
    {
      title: t('treatments.wellness', 'Wellness & Spa'),
      icon: <Spa fontSize="small" />,
      items: [
        { label: t('treatments.spa', 'Spa Paketleri'), path: '/treatments/spa' },
        { label: t('treatments.fitness', 'Fitness Programları'), path: '/treatments/fitness' },
        { label: t('treatments.detox', 'Detoks'), path: '/treatments/detox' },
        { label: t('treatments.mentalHealth', 'Ruh Sağlığı'), path: '/treatments/mental-health' },
      ],
    },
  ];

  // Hospitals Mega Menu Data
  const hospitalCategories = [
    {
      title: t('hospitals.premium', 'Premium Hastaneler'),
      icon: <LocalHospital fontSize="small" />,
      items: [
        { label: t('hospitals.istanbul', 'İstanbul Hastaneleri'), path: '/hospitals?city=istanbul' },
        { label: t('hospitals.ankara', 'Ankara Hastaneleri'), path: '/hospitals?city=ankara' },
        { label: t('hospitals.izmir', 'İzmir Hastaneleri'), path: '/hospitals?city=izmir' },
        { label: t('hospitals.antalya', 'Antalya Hastaneleri'), path: '/hospitals?city=antalya' },
      ],
    },
    {
      title: t('hospitals.specialties', 'Uzmanlık Alanları'),
      icon: <MedicalServices fontSize="small" />,
      items: [
        { label: t('hospitals.cardiology', 'Kardiyoloji'), path: '/hospitals?specialty=cardiology' },
        { label: t('hospitals.oncology', 'Onkoloji'), path: '/hospitals?specialty=oncology' },
        { label: t('hospitals.orthopedics', 'Ortopedi'), path: '/hospitals?specialty=orthopedics' },
        { label: t('hospitals.neurology', 'Nöroloji'), path: '/hospitals?specialty=neurology' },
      ],
    },
    {
      title: t('hospitals.services', 'Özel Hizmetler'),
      icon: <Favorite fontSize="small" />,
      items: [
        { label: t('hospitals.emergency', 'Acil Servis'), path: '/hospitals?service=emergency' },
        { label: t('hospitals.icu', 'Yoğun Bakım'), path: '/hospitals?service=icu' },
        { label: t('hospitals.radiology', 'Radyoloji'), path: '/hospitals?service=radiology' },
        { label: t('hospitals.lab', 'Laboratuvar'), path: '/hospitals?service=lab' },
      ],
    },
  ];

  // Support Mega Menu Data
  const supportCategories = [
    {
      title: t('support.travel', 'Seyahat Hizmetleri'),
      icon: <FlightTakeoff fontSize="small" />,
      items: [
        { label: t('support.flights', 'Uçak Bileti'), path: '/flights' },
        { label: t('support.transfers', 'Transfer'), path: '/transfers' },
        { label: t('support.carRental', 'Araç Kiralama'), path: '/car-rentals' },
        { label: t('support.accommodation', 'Konaklama'), path: '/accommodations' },
      ],
    },
    {
      title: t('support.help', 'Yardım & Destek'),
      icon: <Support fontSize="small" />,
      items: [
        { label: t('support.faq', 'Sık Sorulan Sorular'), path: '/faq' },
        { label: t('support.contact', 'İletişim'), path: '/contact' },
        { label: t('support.liveChat', 'Canlı Destek'), path: '/chat' },
        { label: t('support.emergency', 'Acil Yardım'), path: '/emergency-help' },
      ],
    },
    {
      title: t('support.resources', 'Kaynaklar'),
      icon: <MedicalServices fontSize="small" />,
      items: [
        { label: t('support.blog', 'Blog'), path: '/blog' },
        { label: t('support.guide', 'Rehber'), path: '/cultural-guide' },
        { label: t('support.virtualTour', 'Sanal Tur'), path: '/virtual-tours' },
        { label: t('support.packages', 'Paketler'), path: '/packages' },
      ],
    },
  ];

  const handleMenuOpen = (setter) => (event) => {
    setter(event.currentTarget);
  };

  const handleMenuClose = (setter) => () => {
    setter(null);
  };

  const handleMenuItemClick = (path, setter) => () => {
    navigate(path);
    setter(null);
  };

  return (
    <Box sx={{ display: { xs: 'none', lg: 'flex' }, alignItems: 'center', gap: 1 }}>
      {/* Treatments Menu */}
      <Button
        onMouseEnter={handleMenuOpen(setTreatmentsAnchor)}
        sx={{
          color: 'text.primary',
          textTransform: 'none',
          fontWeight: 600,
          fontSize: '0.875rem',
          px: 2,
          py: 1,
        }}
      >
        {t('nav.treatments', 'Tedaviler')}
      </Button>
      <Menu
        anchorEl={treatmentsAnchor}
        open={Boolean(treatmentsAnchor)}
        onClose={handleMenuClose(setTreatmentsAnchor)}
        MenuListProps={{
          onMouseLeave: handleMenuClose(setTreatmentsAnchor),
        }}
        PaperProps={{
          sx: {
            mt: 2,
            minWidth: 600,
            maxWidth: 800,
            borderRadius: 3,
            boxShadow: '0 20px 25px -5px rgba(0, 0, 0, 0.1)',
          },
        }}
      >
        <Grid container spacing={2} sx={{ p: 3 }}>
          {treatmentCategories.map((category, index) => (
            <Grid item xs={6} key={index}>
              <Box sx={{ mb: 2 }}>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1.5 }}>
                  <Box sx={{ color: 'primary.main' }}>{category.icon}</Box>
                  <Typography variant="subtitle2" sx={{ fontWeight: 700, color: 'text.primary' }}>
                    {category.title}
                  </Typography>
                </Box>
                {category.items.map((item, itemIndex) => (
                  <MenuItem
                    key={itemIndex}
                    component={RouterLink}
                    to={item.path}
                    onClick={handleMenuClose(setTreatmentsAnchor)}
                    sx={{
                      borderRadius: 1,
                      mb: 0.5,
                      '&:hover': {
                        bgcolor: 'action.hover',
                      },
                    }}
                  >
                    <Typography variant="body2">{item.label}</Typography>
                  </MenuItem>
                ))}
              </Box>
            </Grid>
          ))}
        </Grid>
      </Menu>

      {/* Hospitals Menu */}
      <Button
        onMouseEnter={handleMenuOpen(setHospitalsAnchor)}
        sx={{
          color: 'text.primary',
          textTransform: 'none',
          fontWeight: 600,
          fontSize: '0.875rem',
          px: 2,
          py: 1,
        }}
      >
        {t('nav.hospitals', 'Hastaneler')}
      </Button>
      <Menu
        anchorEl={hospitalsAnchor}
        open={Boolean(hospitalsAnchor)}
        onClose={handleMenuClose(setHospitalsAnchor)}
        MenuListProps={{
          onMouseLeave: handleMenuClose(setHospitalsAnchor),
        }}
        PaperProps={{
          sx: {
            mt: 2,
            minWidth: 500,
            borderRadius: 3,
            boxShadow: '0 20px 25px -5px rgba(0, 0, 0, 0.1)',
          },
        }}
      >
        <Grid container spacing={2} sx={{ p: 3 }}>
          {hospitalCategories.map((category, index) => (
            <Grid item xs={6} key={index}>
              <Box sx={{ mb: 2 }}>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1.5 }}>
                  <Box sx={{ color: 'primary.main' }}>{category.icon}</Box>
                  <Typography variant="subtitle2" sx={{ fontWeight: 700, color: 'text.primary' }}>
                    {category.title}
                  </Typography>
                </Box>
                {category.items.map((item, itemIndex) => (
                  <MenuItem
                    key={itemIndex}
                    component={RouterLink}
                    to={item.path}
                    onClick={handleMenuClose(setHospitalsAnchor)}
                    sx={{
                      borderRadius: 1,
                      mb: 0.5,
                      '&:hover': {
                        bgcolor: 'action.hover',
                      },
                    }}
                  >
                    <Typography variant="body2">{item.label}</Typography>
                  </MenuItem>
                ))}
              </Box>
            </Grid>
          ))}
        </Grid>
      </Menu>

      {/* Support Menu */}
      <Button
        onMouseEnter={handleMenuOpen(setSupportAnchor)}
        sx={{
          color: 'text.primary',
          textTransform: 'none',
          fontWeight: 600,
          fontSize: '0.875rem',
          px: 2,
          py: 1,
        }}
      >
        {t('nav.support', 'Destek')}
      </Button>
      <Menu
        anchorEl={supportAnchor}
        open={Boolean(supportAnchor)}
        onClose={handleMenuClose(setSupportAnchor)}
        MenuListProps={{
          onMouseLeave: handleMenuClose(setSupportAnchor),
        }}
        PaperProps={{
          sx: {
            mt: 2,
            minWidth: 500,
            borderRadius: 3,
            boxShadow: '0 20px 25px -5px rgba(0, 0, 0, 0.1)',
          },
        }}
      >
        <Grid container spacing={2} sx={{ p: 3 }}>
          {supportCategories.map((category, index) => (
            <Grid item xs={6} key={index}>
              <Box sx={{ mb: 2 }}>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1.5 }}>
                  <Box sx={{ color: 'primary.main' }}>{category.icon}</Box>
                  <Typography variant="subtitle2" sx={{ fontWeight: 700, color: 'text.primary' }}>
                    {category.title}
                  </Typography>
                </Box>
                {category.items.map((item, itemIndex) => (
                  <MenuItem
                    key={itemIndex}
                    component={RouterLink}
                    to={item.path}
                    onClick={handleMenuClose(setSupportAnchor)}
                    sx={{
                      borderRadius: 1,
                      mb: 0.5,
                      '&:hover': {
                        bgcolor: 'action.hover',
                      },
                    }}
                  >
                    <Typography variant="body2">{item.label}</Typography>
                  </MenuItem>
                ))}
              </Box>
            </Grid>
          ))}
        </Grid>
      </Menu>

      {/* Direct Links */}
      <Button
        component={RouterLink}
        to="/doctors"
        sx={{
          color: 'text.primary',
          textTransform: 'none',
          fontWeight: 600,
          fontSize: '0.875rem',
          px: 2,
          py: 1,
        }}
      >
        {t('nav.doctors', 'Doktorlar')}
      </Button>
      <Button
        component={RouterLink}
        to="/packages"
        sx={{
          color: 'text.primary',
          textTransform: 'none',
          fontWeight: 600,
          fontSize: '0.875rem',
          px: 2,
          py: 1,
        }}
      >
        {t('nav.packages', 'Paketler')}
      </Button>
    </Box>
  );
};

export default Navigation;


