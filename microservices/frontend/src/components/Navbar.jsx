// src/components/Navbar.jsx
import React, { useState, useMemo } from 'react';
import {
    AppBar, Toolbar, Typography, Button, Box, IconButton,
    Drawer, List, ListItem, ListItemButton, ListItemText,
    useMediaQuery, useTheme, Menu, MenuItem, Link as MuiLink
} from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import LocalHospitalIcon from '@mui/icons-material/LocalHospital';
import FlightTakeoffIcon from '@mui/icons-material/FlightTakeoff';
import InfoIcon from '@mui/icons-material/Info';
import EventNoteIcon from '@mui/icons-material/EventNote';
import { Link as RouterLink } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { Container, Divider } from '@mui/material';

export default function Navbar() {
  const { t } = useTranslation();
  
  // Ana Navigasyon Verileri
  const navItems = useMemo(() => [
      { label: t('hospitals', 'Hastaneler'), path: '/hospitals', icon: LocalHospitalIcon },
      { label: t('doctors', 'Doktorlar'), path: '/doctors', icon: LocalHospitalIcon },
      { label: t('accommodations', 'Konaklama'), path: '/accommodations', icon: LocalHospitalIcon },
      { label: t('packages', 'Paketler'), path: '/packages', icon: LocalHospitalIcon },
  ], [t]);

  // Seyahat Alt Menü Verileri
  const travelMenuItems = useMemo(() => [
      { label: t('flights', 'Uçak Bileti'), path: '/flights' },
      { label: t('transfers', 'Transferler'), path: '/transfers' },
      { label: t('carRentals', 'Araç Kiralama'), path: '/car-rentals' },
  ], [t]);

  // Kurumsal Alt Menü Verileri
  const corporateMenuItems = useMemo(() => [
      { label: t('about', 'Hakkımızda'), path: '/about' },
      { label: t('contact', 'İletişim'), path: '/contact' },
      { label: t('faq', 'S.S.S.'), path: '/faq' },
  ], [t]);

  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));
    const [drawerOpen, setDrawerOpen] = useState(false);
    const [travelAnchorEl, setTravelAnchorEl] = useState(null);
    const [corporateAnchorEl, setCorporateAnchorEl] = useState(null);

    const handleTravelMenuOpen = (event) => { setTravelAnchorEl(event.currentTarget); };
    const handleTravelMenuClose = () => { setTravelAnchorEl(null); };

    const handleCorporateMenuOpen = (event) => { setCorporateAnchorEl(event.currentTarget); };
    const handleCorporateMenuClose = () => { setCorporateAnchorEl(null); };

    const toggleDrawer = (open) => () => { setDrawerOpen(open); };

    const drawerList = (
        <Box
            sx={{ width: 250 }}
            role="presentation"
            onClick={toggleDrawer(false)}
            onKeyDown={toggleDrawer(false)}
        >
            <List>
                <ListItem>
                    <Typography variant="h6" sx={{ color: 'primary.main', fontWeight: 'bold', ml: 1 }}>{t('menu', 'MENÜ')}</Typography>
                </ListItem>
                <Divider />
                {navItems.map((item) => (
                    <ListItem key={item.label} disablePadding>
                        <ListItemButton component={RouterLink} to={item.path}>
                            <ListItemText primary={item.label} />
                        </ListItemButton>
                    </ListItem>
                ))}
                <Divider sx={{ my: 1 }} />
                {/* Seyahat ve Kurumsal Linkler (Mobil) */}
                <ListItemText primary={t('travelServices', 'SEYAHAT HİZMETLERİ')} sx={{ ml: 2, fontWeight: 'bold' }} />
                {travelMenuItems.map((item) => (
                     <ListItem key={item.label} disablePadding>
                        <ListItemButton component={RouterLink} to={item.path} sx={{ pl: 4 }}>
                            <ListItemText primary={item.label} />
                        </ListItemButton>
                    </ListItem>
                ))}
                <Divider sx={{ my: 1 }} />
                <ListItemText primary={t('corporate', 'KURUMSAL')} sx={{ ml: 2, fontWeight: 'bold' }} />
                {corporateMenuItems.map((item) => (
                     <ListItem key={item.label} disablePadding>
                        <ListItemButton component={RouterLink} to={item.path} sx={{ pl: 4 }}>
                            <ListItemText primary={item.label} />
                        </ListItemButton>
                    </ListItem>
                ))}
            </List>
        </Box>
    );

    return (
        <AppBar position="sticky" sx={{ bgcolor: theme.palette.background.paper, boxShadow: theme.shadows[1] }}>
            <Container maxWidth="lg" sx={{ px: { xs: 1, sm: 2 } }}>
                <Toolbar disableGutters>
                    {/* Logo/Başlık */}
                    <MuiLink component={RouterLink} to="/" sx={{ textDecoration: 'none', color: 'inherit', display: 'flex', alignItems: 'center' }}>
                        <LocalHospitalIcon sx={{ mr: 1, color: theme.palette.primary.main }} />
                        <Typography
                            variant="h6"
                            noWrap
                            sx={{
                                fontWeight: 700,
                                color: 'text.primary',
                                display: { xs: 'none', sm: 'block' }
                            }}
                        >
                            {t('healthTourism', 'Sağlık Turizmi')}
                        </Typography>
                    </MuiLink>

                    {isMobile ? (
                        /* Mobil Menü */
                        <>
                            <Box sx={{ flexGrow: 1 }} />
                            <IconButton
                                edge="start"
                                color="inherit"
                                aria-label="menu"
                                onClick={toggleDrawer(true)}
                                sx={{ color: 'text.primary' }}
                            >
                                <MenuIcon />
                            </IconButton>
                            <Drawer
                                anchor="right"
                                open={drawerOpen}
                                onClose={toggleDrawer(false)}
                            >
                                {drawerList}
                            </Drawer>
                        </>
                    ) : (
                        /* Masaüstü Menü */
                        <Box sx={{ flexGrow: 1, display: 'flex', ml: 4, alignItems: 'center' }}>
                            {navItems.map((item) => (
                                <Button
                                    key={item.label}
                                    component={RouterLink}
                                    to={item.path}
                                    sx={{ color: 'text.primary', fontWeight: 600, mx: 1 }}
                                >
                                    {item.label}
                                </Button>
                            ))}

                            {/* Seyahat Menüsü */}
                            <Button
                                color="inherit"
                                sx={{ color: 'text.primary', fontWeight: 600, mx: 1 }}
                                onClick={handleTravelMenuOpen}
                                startIcon={<FlightTakeoffIcon />}
                            >
                                {t('travel', 'Seyahat')}
                            </Button>
                            <Menu
                                anchorEl={travelAnchorEl}
                                open={Boolean(travelAnchorEl)}
                                onClose={handleTravelMenuClose}
                            >
                                {travelMenuItems.map((item) => (
                                    <MenuItem
                                        key={item.label}
                                        onClick={handleTravelMenuClose}
                                        component={RouterLink}
                                        to={item.path}
                                    >
                                        {item.label}
                                    </MenuItem>
                                ))}
                            </Menu>

                             {/* Kurumsal Menüsü */}
                            <Button
                                color="inherit"
                                sx={{ color: 'text.primary', fontWeight: 600, mx: 1 }}
                                onClick={handleCorporateMenuOpen}
                                startIcon={<InfoIcon />}
                            >
                                {t('corporate', 'Kurumsal')}
                            </Button>
                            <Menu
                                anchorEl={corporateAnchorEl}
                                open={Boolean(corporateAnchorEl)}
                                onClose={handleCorporateMenuClose}
                            >
                                {corporateMenuItems.map((item) => (
                                    <MenuItem
                                        key={item.label}
                                        onClick={handleCorporateMenuClose}
                                        component={RouterLink}
                                        to={item.path}
                                    >
                                        {item.label}
                                    </MenuItem>
                                ))}
                            </Menu>
                        </Box>
                    )}

                    {/* Sağ Taraf - CTA Butonu */}
                    <Button
                        variant="contained"
                        color="secondary"
                        component={RouterLink}
                        to="/reservations"
                        startIcon={<EventNoteIcon />}
                        sx={{ ml: { xs: 0, md: 2 }, py: 1, px: 2, fontWeight: 'bold' }}
                    >
                        {t('getFreeQuote', 'Teklif Al')}
                    </Button>
                </Toolbar>
            </Container>
            <Divider />
        </AppBar>
    );
}

export default Navbar;