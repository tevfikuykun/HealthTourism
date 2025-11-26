// src/components/Header.jsx
import React, { useState } from 'react';
import {
    AppBar, Toolbar, Typography, Button, Box, IconButton,
    Menu, MenuItem, useMediaQuery, useTheme, Container
} from '@mui/material';
import LocalHospitalIcon from '@mui/icons-material/LocalHospital';
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';
import ArrowDropDownIcon from '@mui/icons-material/ArrowDropDown';
import { Link as RouterLink } from 'react-router-dom';

// Navigasyon Linkleri
const navItems = [
    { title: 'Hastaneler', path: '/hospitals' },
    { title: 'Doktorlar', path: '/doctors' },
    { title: 'Konaklama', path: '/accommodations' },
    { title: 'Sağlık Paketleri', path: '/packages' },
    {
        title: 'Seyahat Hizmetleri',
        isDropdown: true,
        items: [
            { title: 'Uçak Bileti (Uçuş)', path: '/flights' },
            { title: 'Özel Transferler', path: '/transfers' },
            { title: 'Araç Kiralama', path: '/car-rentals' },
        ]
    },
    { title: 'Rezervasyon (Teklif Al)', path: '/reservations' },
];

function Header() {
    const theme = useTheme();
    const [anchorElDropdown, setAnchorElDropdown] = useState(null);

    const handleOpenDropdown = (event) => { setAnchorElDropdown(event.currentTarget); };
    const handleCloseDropdown = () => { setAnchorElDropdown(null); };

    return (
        <AppBar position="sticky" sx={{ bgcolor: 'white', borderBottom: `1px solid ${theme.palette.divider}`, boxShadow: '0 2px 4px rgba(0,0,0,0.05)' }}>
            <Container maxWidth="lg">
                <Toolbar disableGutters>

                    {/* Logo ve Marka Adı (Kırpılmayı önlemek için flexShrink: 0) */}
                    <Box sx={{ display: 'flex', alignItems: 'center', flexShrink: 0, mr: { xs: 1, sm: 2 } }}>
                        <LocalHospitalIcon sx={{ mr: 1, color: theme.palette.primary.main }} />
                        <Typography
                            variant="h6"
                            noWrap
                            component={RouterLink}
                            to="/"
                            sx={{
                                fontWeight: 700,
                                letterSpacing: '.1rem',
                                color: theme.palette.primary.main,
                                textDecoration: 'none',
                                fontSize: { xs: '1rem', sm: '1.2rem', md: '1.25rem' },
                            }}
                        >
                            HEALTH TURİZM
                        </Typography>
                    </Box>

                    {/* Navigasyon Alanı (Kaydırılabilir ve Sıkışma Önleyici) */}
                    <Box
                        sx={{
                            flexGrow: 1, // Kalan alana yayılma
                            flexShrink: 1, // * Navigasyon alanının daralmasına izin ver (butona yer açmak için)
                            display: 'flex',
                            alignItems: 'center',
                            overflowX: 'auto', // Yatay kaydırma aktif
                            whiteSpace: 'nowrap',
                            minWidth: 0, // Flex elemanının minimum genişliğini sıfırlar
                            mx: { xs: 0.5, sm: 1 }, // Navigasyon alanının yanındaki boşlukları küçültüyoruz
                            '&::-webkit-scrollbar': { display: 'none' },
                            MsOverflowStyle: 'none',
                        }}
                    >
                        {navItems.map((item) => (
                            item.isDropdown ? (
                                <Box key={item.title} sx={{ display: 'inline-block', flexShrink: 0, mr: { xs: 1.5, md: 2 } }}> {/* DROPDAWN ARALIĞI */}
                                    <Button
                                        onClick={handleOpenDropdown}
                                        endIcon={<ArrowDropDownIcon />}
                                        sx={{
                                            my: 2,
                                            color: theme.palette.text.primary,
                                            textTransform: 'none',
                                            fontWeight: 600,
                                            px: { xs: 1, sm: 1.5 } // Buton içine padding ekledik
                                        }}
                                    >
                                        {item.title}
                                    </Button>

                                    <Menu
                                        anchorEl={anchorElDropdown}
                                        open={Boolean(anchorElDropdown)}
                                        onClose={handleCloseDropdown}
                                    >
                                        {item.items.map((subItem) => (
                                            <MenuItem
                                                key={subItem.title}
                                                onClick={handleCloseDropdown}
                                                component={RouterLink}
                                                to={subItem.path}
                                            >
                                                {subItem.title}
                                            </MenuItem>
                                        ))}
                                    </Menu>
                                </Box>
                            ) : (
                                // Normal Navigasyon Öğesi
                                <Button
                                    key={item.title}
                                    sx={{
                                        flexShrink: 0, // * Bu butonun asla daralmasını istemiyoruz
                                        my: 2,
                                        color: theme.palette.text.primary,
                                        display: 'block',
                                        textTransform: 'none',
                                        fontWeight: 600,
                                        mr: { xs: 1.5, md: 2 }, // SABİT YAN BOŞLUK
                                        px: { xs: 1, sm: 1.5 } // Buton içine padding ekledik
                                    }}
                                    component={RouterLink}
                                    to={item.path}
                                >
                                    {item.title}
                                </Button>
                            )
                        ))}
                    </Box>

                    {/* Sağ Taraf Aksiyon Butonları (Daralmayacak Alan) */}
                    <Box sx={{ flexShrink: 0, display: 'flex', alignItems: 'center', ml: { xs: 0.5, sm: 1 } }}>

                        {/* Ödeme Yap Butonu */}
                        <Button
                            color="inherit"
                            variant="outlined"
                            size="small"
                            sx={{
                                mr: { xs: 0.5, sm: 1 },
                                textTransform: 'none',
                                color: theme.palette.primary.main,
                                borderColor: theme.palette.primary.main,
                                p: { xs: 0.5, sm: 1 },
                                minWidth: { xs: 'auto', sm: 80 }
                            }}
                            component={RouterLink}
                            to="/payments"
                        >
                            <ShoppingCartIcon fontSize="small" sx={{ mr: { xs: 0, sm: 0.5 } }} />
                            <Box component="span" sx={{ display: { xs: 'none', sm: 'inline' } }}>Ödeme Yap</Box>
                            <Box component="span" sx={{ display: { xs: 'inline', sm: 'none' } }}>Öde</Box>
                        </Button>

                        {/* Teklif Al Butonu */}
                        <Button
                            variant="contained"
                            color="secondary"
                            component={RouterLink}
                            to="/reservations"
                            sx={{
                                textTransform: 'none',
                                py: { xs: 0.5, sm: 1 },
                                px: { xs: 1, sm: 2 }
                            }}
                        >
                            <Box component="span" sx={{ display: { xs: 'none', sm: 'inline' } }}>Ücretsiz Teklif Al</Box>
                            <Box component="span" sx={{ display: { xs: 'inline', sm: 'none' } }}>Teklif Al</Box>
                        </Button>
                    </Box>
                </Toolbar>
            </Container>
        </AppBar>
    );
}

export default Header;