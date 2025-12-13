import React from 'react';
import { Breadcrumbs, Link, Typography } from '@mui/material';
import { Link as RouterLink, useLocation } from 'react-router-dom';
import NavigateNextIcon from '@mui/icons-material/NavigateNext';
import HomeIcon from '@mui/icons-material/Home';

const routeLabels = {
  '/': 'Ana Sayfa',
  '/hospitals': 'Hastaneler',
  '/doctors': 'Doktorlar',
  '/accommodations': 'Konaklama',
  '/flights': 'Uçak Bileti',
  '/car-rentals': 'Araç Kiralama',
  '/transfers': 'Transfer Hizmetleri',
  '/packages': 'Paketler',
  '/reservations': 'Rezervasyonlar',
  '/payments': 'Ödemeler',
  '/dashboard': 'Dashboard',
  '/favorites': 'Favoriler',
  '/notifications': 'Bildirimler',
  '/contact': 'İletişim',
  '/about': 'Hakkımızda',
};

export default function Breadcrumb() {
  const location = useLocation();
  const pathnames = location.pathname.split('/').filter((x) => x);

  return (
    <Breadcrumbs
      aria-label="breadcrumb"
      separator={<NavigateNextIcon fontSize="small" />}
      sx={{ mb: 2, mt: 2 }}
    >
      <Link component={RouterLink} to="/" underline="hover" color="inherit" sx={{ display: 'flex', alignItems: 'center' }}>
        <HomeIcon sx={{ mr: 0.5 }} fontSize="inherit" />
        Ana Sayfa
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

