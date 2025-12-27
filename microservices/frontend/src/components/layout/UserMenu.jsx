import React, { useState } from 'react';
import {
  Box,
  IconButton,
  Menu,
  MenuItem,
  Avatar,
  Typography,
  Divider,
  ListItemIcon,
  ListItemText,
} from '@mui/material';
import {
  Person,
  Dashboard,
  BookOnline,
  Settings,
  Logout,
  AccountCircle,
  Payment,
  Favorite,
} from '@mui/icons-material';
import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from '../../i18n';

/**
 * UserMenu Component
 * JWT tabanlı login durumunu kontrol eden; profil, rezervasyonlarım ve çıkış butonlarını barındıran kullanıcı menüsü
 */
const UserMenu = ({ user, onLogout }) => {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [anchorEl, setAnchorEl] = useState(null);
  const open = Boolean(anchorEl);

  const handleClick = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const handleMenuItemClick = (path) => () => {
    navigate(path);
    handleClose();
  };

  const handleLogout = () => {
    onLogout();
    handleClose();
    navigate('/');
  };

  const userInitials = user?.firstName?.charAt(0)?.toUpperCase() || 
                       user?.name?.charAt(0)?.toUpperCase() || 
                       user?.email?.charAt(0)?.toUpperCase() || 
                       'U';

  const userName = user?.firstName || user?.name || user?.email || 'User';

  return (
    <>
      <IconButton
        onClick={handleClick}
        size="small"
        sx={{
          ml: 2,
          '&:hover': {
            transform: 'scale(1.1)',
          },
          transition: 'transform 0.2s ease',
        }}
      >
        <motion.div
          whileHover={{ scale: 1.1 }}
          whileTap={{ scale: 0.95 }}
        >
          <Avatar
            sx={{
              width: 40,
              height: 40,
              bgcolor: 'primary.main',
              fontWeight: 700,
              fontSize: '0.875rem',
              background: 'linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%)',
              boxShadow: '0 4px 12px rgba(79, 70, 229, 0.4)',
            }}
          >
            {userInitials}
          </Avatar>
        </motion.div>
      </IconButton>
      <Menu
        anchorEl={anchorEl}
        open={open}
        onClose={handleClose}
        onClick={handleClose}
        PaperProps={{
          elevation: 3,
          sx: {
            mt: 1.5,
            minWidth: 250,
            borderRadius: 3,
            boxShadow: '0 20px 25px -5px rgba(0, 0, 0, 0.1)',
            overflow: 'visible',
            filter: 'drop-shadow(0px 2px 8px rgba(0,0,0,0.1))',
            '& .MuiAvatar-root': {
              width: 32,
              height: 32,
              ml: -0.5,
              mr: 1,
            },
            '&:before': {
              content: '""',
              display: 'block',
              position: 'absolute',
              top: 0,
              right: 14,
              width: 10,
              height: 10,
              bgcolor: 'background.paper',
              transform: 'translateY(-50%) rotate(45deg)',
              zIndex: 0,
            },
          },
        }}
        transformOrigin={{ horizontal: 'right', vertical: 'top' }}
        anchorOrigin={{ horizontal: 'right', vertical: 'bottom' }}
      >
        {/* User Info Header */}
        <Box sx={{ px: 2, py: 1.5, bgcolor: 'primary.main', color: 'white', borderRadius: '12px 12px 0 0' }}>
          <Typography variant="subtitle2" sx={{ fontWeight: 700 }}>
            {userName}
          </Typography>
          <Typography variant="caption" sx={{ opacity: 0.9 }}>
            {user?.email || ''}
          </Typography>
        </Box>

        <Divider />

        {/* Dashboard */}
        <MenuItem onClick={handleMenuItemClick('/dashboard')} sx={{ borderRadius: 1, mx: 1, mt: 1 }}>
          <ListItemIcon>
            <Dashboard fontSize="small" />
          </ListItemIcon>
          <ListItemText primary={t('dashboard', 'Dashboard')} />
        </MenuItem>

        {/* Profile */}
        <MenuItem onClick={handleMenuItemClick('/profile')} sx={{ borderRadius: 1, mx: 1 }}>
          <ListItemIcon>
            <Person fontSize="small" />
          </ListItemIcon>
          <ListItemText primary={t('profile', 'Profil')} />
        </MenuItem>

        {/* Reservations */}
        <MenuItem onClick={handleMenuItemClick('/reservations')} sx={{ borderRadius: 1, mx: 1 }}>
          <ListItemIcon>
            <BookOnline fontSize="small" />
          </ListItemIcon>
          <ListItemText primary={t('reservations', 'Rezervasyonlarım')} />
        </MenuItem>

        {/* Favorites */}
        <MenuItem onClick={handleMenuItemClick('/favorites')} sx={{ borderRadius: 1, mx: 1 }}>
          <ListItemIcon>
            <Favorite fontSize="small" />
          </ListItemIcon>
          <ListItemText primary={t('favorites', 'Favoriler')} />
        </MenuItem>

        {/* Payments */}
        <MenuItem onClick={handleMenuItemClick('/payments')} sx={{ borderRadius: 1, mx: 1 }}>
          <ListItemIcon>
            <Payment fontSize="small" />
          </ListItemIcon>
          <ListItemText primary={t('payments', 'Ödemeler')} />
        </MenuItem>

        <Divider sx={{ my: 1 }} />

        {/* Settings */}
        <MenuItem onClick={handleMenuItemClick('/settings')} sx={{ borderRadius: 1, mx: 1 }}>
          <ListItemIcon>
            <Settings fontSize="small" />
          </ListItemIcon>
          <ListItemText primary={t('settings', 'Ayarlar')} />
        </MenuItem>

        {/* Logout */}
        <MenuItem
          onClick={handleLogout}
          sx={{
            borderRadius: 1,
            mx: 1,
            mb: 1,
            color: 'error.main',
            '&:hover': {
              bgcolor: 'error.lighter',
            },
          }}
        >
          <ListItemIcon>
            <Logout fontSize="small" sx={{ color: 'error.main' }} />
          </ListItemIcon>
          <ListItemText primary={t('logout', 'Çıkış Yap')} />
        </MenuItem>
      </Menu>
    </>
  );
};

export default UserMenu;



