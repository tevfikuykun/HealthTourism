import React, { useMemo } from 'react';
import { Breadcrumbs, Link, Typography } from '@mui/material';
import { Link as RouterLink, useLocation } from 'react-router-dom';
import NavigateNextIcon from '@mui/icons-material/NavigateNext';
import HomeIcon from '@mui/icons-material/Home';
import { useTranslation } from 'react-i18next';

export default function Breadcrumb() {
  const { t } = useTranslation();
  const location = useLocation();
  const pathnames = location.pathname.split('/').filter((x) => x);

  const routeLabels = useMemo(() => ({
    '/': t('home', 'Ana Sayfa'),
    '/hospitals': t('hospitals', 'Hastaneler'),
    '/doctors': t('doctors', 'Doktorlar'),
    '/accommodations': t('accommodations', 'Konaklama'),
    '/flights': t('flights', 'Uçak Bileti'),
    '/car-rentals': t('carRentals', 'Araç Kiralama'),
    '/transfers': t('transfers', 'Transfer Hizmetleri'),
    '/packages': t('packages', 'Paketler'),
    '/reservations': t('reservations', 'Rezervasyonlar'),
    '/payments': t('payments', 'Ödemeler'),
    '/dashboard': t('dashboard', 'Dashboard'),
    '/favorites': t('favorites', 'Favoriler'),
    '/notifications': t('notifications', 'Bildirimler'),
    '/contact': t('contact', 'İletişim'),
    '/about': t('about', 'Hakkımızda'),
  }), [t]);

  return (
    <Breadcrumbs
      aria-label="breadcrumb"
      separator={<NavigateNextIcon fontSize="small" />}
      sx={{ mb: 2, mt: 2 }}
    >
      <Link component={RouterLink} to="/" underline="hover" color="inherit" sx={{ display: 'flex', alignItems: 'center' }}>
        <HomeIcon sx={{ mr: 0.5 }} fontSize="inherit" />
        {t('home', 'Ana Sayfa')}
      </Link>
      {pathnames.map((value, index) => {
        const last = index === pathnames.length - 1;
        const to = `/${pathnames.slice(0, index + 1).join('/')}`;
        const label = routeLabels[to] || value.charAt(0).toUpperCase() + value.slice(1);

        return last ? (
          <Typography color="text.primary" key={to}>
            {label}
          </Typography>
        ) : (
          <Link component={RouterLink} to={to} underline="hover" color="inherit" key={to}>
            {label}
          </Link>
        );
      })}
    </Breadcrumbs>
  );
}

