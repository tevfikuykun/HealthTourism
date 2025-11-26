// src/components/Navbar.jsx
import React, { useState } from 'react';
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

// Ana Navigasyon Verileri
const navItems = [
    { label: 'Hastaneler', path: '/hospitals', icon: LocalHospitalIcon },
    { label: 'Doktorlar', path: '/doctors', icon: LocalHospitalIcon },
    { label: 'Konaklama', path: '/accommodations', icon: LocalHospitalIcon },
    { label: 'Sağlık Paketleri', path: '/packages', icon: LocalHospitalIcon },
];

// Seyahat Alt Menü Verileri
const travelMenuItems = [
    { label: 'Uçak Biletleri', path: '/flights' },
    { label: 'Transfer Hizmetleri', path: '/transfers' },
    { label: 'Araç Kiralama', path: '/car-rentals' },
];

// Kurumsal Alt Menü Verileri
const corporateMenuItems = [
    { label: 'Hakkımızda', path: '/about' },
    { label: 'İletişim', path: '/contact' },
    { label: 'S.S.S.', path: '/faq' },
];


function Navbar() {
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
                    <Typography variant="h6" sx={{ color: 'primary.main', fontWeight: 'bold', ml: 1 }}>MENÜ</Typography>
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
                <ListItemText primary="SEYAHAT HİZMETLERİ" sx={{ ml: 2, fontWeight: 'bold' }} />
                {travelMenuItems.map((item) => (
                     <ListItem key={item.label} disablePadding>
                        <ListItemButton component={RouterLink} to={item.path} sx={{ pl: 4 }}>
                            <ListItemText primary={item.label} />
                        </ListItemButton>
                    </ListItem>
                ))}
                <Divider sx={{ my: 1 }} />
                <ListItemText primary="KURUMSAL" sx={{ ml: 2, fontWeight: 'bold' }} />
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
                            Sağlık Turizmi
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
                                Seyahat
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
                                Kurumsal
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
                        Teklif Al
                    </Button>
                </Toolbar>
            </Container>
            <Divider />
        </AppBar>
    );
}

export default Navbar;