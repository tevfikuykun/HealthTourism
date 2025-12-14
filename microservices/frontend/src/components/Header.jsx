// src/components/Header.jsx
import React, { useState } from 'react';
import {
    AppBar, Toolbar, Typography, Button, Box, IconButton,
    Menu, MenuItem, useMediaQuery, useTheme, Container, Avatar, Divider
} from '@mui/material';
import LocalHospitalIcon from '@mui/icons-material/LocalHospital';
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';
import ArrowDropDownIcon from '@mui/icons-material/ArrowDropDown';
import PersonIcon from '@mui/icons-material/Person';
import DashboardIcon from '@mui/icons-material/Dashboard';
import LogoutIcon from '@mui/icons-material/Logout';
import { Link as RouterLink, useNavigate } from 'react-router-dom';
import { useTranslation } from '../i18n';
import { useAuth } from '../hooks/useAuth';
import NotificationBell from './Notifications/NotificationBell';
import ThemeToggle from './ThemeToggle';
import LanguageSwitcher from './LanguageSwitcher';

function Header() {
    const { t } = useTranslation();
    const theme = useTheme();
    const navigate = useNavigate();
    const { user, isAuthenticated, logout } = useAuth();
    const [anchorElDropdown, setAnchorElDropdown] = useState(null);
    const [userMenuAnchor, setUserMenuAnchor] = useState(null);
    
    // Navigasyon Linkleri - useTranslation ile dinamik olacak
    const navItems = React.useMemo(() => [
        { title: t('hospitals', 'Hastaneler'), path: '/hospitals' },
        { title: t('doctors', 'Doktorlar'), path: '/doctors' },
        { title: t('accommodations', 'Konaklama'), path: '/accommodations' },
        { title: t('packages', 'Paketler'), path: '/packages' },
        {
            title: t('travelServices', 'Seyahat Hizmetleri'),
            isDropdown: true,
            items: [
                { title: t('flights', 'Uçak Bileti'), path: '/flights' },
                { title: t('transfers', 'Transferler'), path: '/transfers' },
                { title: t('carRentals', 'Araç Kiralama'), path: '/car-rentals' },
            ]
        },
        { title: t('reservations', 'Rezervasyonlar'), path: '/reservations' },
    ], [t]);

    const handleOpenDropdown = (event) => { setAnchorElDropdown(event.currentTarget); };
    const handleCloseDropdown = () => { setAnchorElDropdown(null); };
    
    const handleUserMenuOpen = (event) => { setUserMenuAnchor(event.currentTarget); };
    const handleUserMenuClose = () => { setUserMenuAnchor(null); };
    
    const handleLogout = () => {
        logout();
        handleUserMenuClose();
        navigate('/');
    };

    return (
        <AppBar 
            position="sticky" 
            sx={{ 
                bgcolor: theme.palette.mode === 'dark' ? theme.palette.background.paper : 'white',
                borderBottom: `1px solid ${theme.palette.divider}`, 
                boxShadow: theme.palette.mode === 'dark' ? '0 2px 8px rgba(0,0,0,0.3)' : '0 2px 4px rgba(0,0,0,0.05)'
            }}
        >
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
                                        endIcon={<ArrowDropDownIcon sx={{ color: theme.palette.text.primary }} />}
                                        sx={{
                                            my: 2,
                                            color: theme.palette.text.primary,
                                            textTransform: 'none',
                                            fontWeight: 600,
                                            px: { xs: 1, sm: 1.5 },
                                            '&:hover': {
                                                backgroundColor: theme.palette.mode === 'dark' ? 'rgba(255, 255, 255, 0.08)' : 'rgba(0, 0, 0, 0.04)'
                                            }
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
                                        flexShrink: 0,
                                        my: 2,
                                        color: theme.palette.text.primary,
                                        display: 'block',
                                        textTransform: 'none',
                                        fontWeight: 600,
                                        mr: { xs: 1.5, md: 2 },
                                        px: { xs: 1, sm: 1.5 },
                                        '&:hover': {
                                            backgroundColor: theme.palette.mode === 'dark' ? 'rgba(255, 255, 255, 0.08)' : 'rgba(0, 0, 0, 0.04)'
                                        }
                                    }}
                                    component={RouterLink}
                                    to={item.path}
                                >
                                    {item.title}
                                </Button>
                            )
                        ))}
                    </Box>

                    {/* Sağ Taraf Aksiyon Butonları */}
                    <Box sx={{ flexShrink: 0, display: 'flex', alignItems: 'center', ml: { xs: 0.5, sm: 1 }, gap: 1 }}>
                        
                        {/* Giriş/Kayıt Butonları veya Kullanıcı Menüsü */}
                        {isAuthenticated ? (
                            /* Kullanıcı Menüsü */
                            <>
                                {/* Bildirimler */}
                                <NotificationBell />
                                
                                <IconButton 
                                    onClick={handleUserMenuOpen} 
                                    sx={{ 
                                        ml: 0.5,
                                        '&:hover': {
                                            backgroundColor: 'action.hover'
                                        }
                                    }}
                                >
                                    <Avatar sx={{ width: 32, height: 32, bgcolor: 'primary.main' }}>
                                        {user?.firstName?.charAt(0)?.toUpperCase() || 'U'}
                                    </Avatar>
                                </IconButton>
                                <Menu
                                    anchorEl={userMenuAnchor}
                                    open={Boolean(userMenuAnchor)}
                                    onClose={handleUserMenuClose}
                                >
                                    <MenuItem onClick={() => { navigate('/dashboard'); handleUserMenuClose(); }}>
                                        <DashboardIcon sx={{ mr: 1 }} fontSize="small" />
                                        {t('dashboard')}
                                    </MenuItem>
                                    <MenuItem onClick={() => { navigate('/favorites'); handleUserMenuClose(); }}>
                                        <PersonIcon sx={{ mr: 1 }} fontSize="small" />
                                        {t('favorites', 'Favorilerim')}
                                    </MenuItem>
                                    <Divider />
                                    <MenuItem onClick={handleLogout}>
                                        <LogoutIcon sx={{ mr: 1 }} fontSize="small" />
                                        {t('logout')}
                                    </MenuItem>
                                </Menu>
                            </>
                        ) : (
                            /* Giriş/Kayıt Butonları */
                            <>
                                <Button
                                    component={RouterLink}
                                    to="/login"
                                    sx={{ 
                                        textTransform: 'none',
                                        color: theme.palette.text.primary,
                                        '&:hover': {
                                            backgroundColor: theme.palette.mode === 'dark' ? 'rgba(255, 255, 255, 0.08)' : 'rgba(0, 0, 0, 0.04)'
                                        }
                                    }}
                                >
                                    {t('login', 'Giriş Yap')}
                                </Button>
                                <Button
                                    variant="contained"
                                    component={RouterLink}
                                    to="/register"
                                    sx={{ 
                                        textTransform: 'none',
                                        backgroundColor: theme.palette.secondary.main,
                                        '&:hover': {
                                            backgroundColor: theme.palette.secondary.dark
                                        }
                                    }}
                                >
                                    {t('register', 'Kayıt Ol')}
                                </Button>
                            </>
                        )}

                        {/* Tema ve Dil Değiştiriciler (Altlı Üstlü) */}
                        <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', ml: 0.5, gap: 0.25 }}>
                            <ThemeToggle />
                            <LanguageSwitcher />
                        </Box>
                    </Box>
                </Toolbar>
            </Container>
        </AppBar>
    );
}

export default Header;