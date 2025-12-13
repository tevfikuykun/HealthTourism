import React, { useState } from 'react';
import {
  Container,
  Box,
  Typography,
  Grid,
  Card,
  CardContent,
  Paper,
  AppBar,
  Toolbar,
  Drawer,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  IconButton,
  Avatar,
  Menu,
  MenuItem,
  Divider,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import DashboardIcon from '@mui/icons-material/Dashboard';
import PeopleIcon from '@mui/icons-material/People';
import LocalHospitalIcon from '@mui/icons-material/LocalHospital';
import PaymentIcon from '@mui/icons-material/Payment';
import EventNoteIcon from '@mui/icons-material/EventNote';
import SettingsIcon from '@mui/icons-material/Settings';
import LogoutIcon from '@mui/icons-material/Logout';
import MenuIcon from '@mui/icons-material/Menu';
import BarChartIcon from '@mui/icons-material/BarChart';
import AdminPanelSettingsIcon from '@mui/icons-material/AdminPanelSettings';
import AdminStats from '../../components/Statistics/AdminStats';

const drawerWidth = 240;

export default function AdminDashboard() {
  const navigate = useNavigate();
  const [selectedMenu, setSelectedMenu] = useState('dashboard');
  const [anchorEl, setAnchorEl] = useState(null);

  const menuItems = [
    { id: 'dashboard', label: 'Dashboard', icon: <DashboardIcon /> },
    { id: 'users', label: 'Kullanıcılar', icon: <PeopleIcon /> },
    { id: 'hospitals', label: 'Hastaneler', icon: <LocalHospitalIcon /> },
    { id: 'reservations', label: 'Rezervasyonlar', icon: <EventNoteIcon /> },
    { id: 'payments', label: 'Ödemeler', icon: <PaymentIcon /> },
    { id: 'reports', label: 'Raporlar', icon: <BarChartIcon /> },
    { id: 'settings', label: 'Ayarlar', icon: <SettingsIcon /> },
  ];

  const handleMenuClick = (menuId) => {
    setSelectedMenu(menuId);
  };

  const handleProfileMenuOpen = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleProfileMenuClose = () => {
    setAnchorEl(null);
  };

  const handleLogout = () => {
    localStorage.removeItem('adminToken');
    localStorage.removeItem('adminUser');
    navigate('/admin/login');
  };

  return (
    <Box sx={{ display: 'flex' }}>
      <AppBar
        position="fixed"
        sx={{ zIndex: (theme) => theme.zIndex.drawer + 1, backgroundColor: '#1976d2' }}
      >
        <Toolbar>
          <IconButton color="inherit" edge="start" sx={{ mr: 2 }}>
            <MenuIcon />
          </IconButton>
          <Typography variant="h6" noWrap component="div" sx={{ flexGrow: 1 }}>
            Health Tourism Admin Panel
          </Typography>
          <IconButton onClick={handleProfileMenuOpen}>
            <Avatar sx={{ bgcolor: 'secondary.main' }}>A</Avatar>
          </IconButton>
          <Menu anchorEl={anchorEl} open={Boolean(anchorEl)} onClose={handleProfileMenuClose}>
            <MenuItem onClick={handleProfileMenuClose}>Profil</MenuItem>
            <MenuItem onClick={handleProfileMenuClose}>Ayarlar</MenuItem>
            <Divider />
            <MenuItem onClick={handleLogout}>
              <ListItemIcon>
                <LogoutIcon fontSize="small" />
              </ListItemIcon>
              Çıkış Yap
            </MenuItem>
          </Menu>
        </Toolbar>
      </AppBar>

      <Drawer
        variant="permanent"
        sx={{
          width: drawerWidth,
          flexShrink: 0,
          '& .MuiDrawer-paper': {
            width: drawerWidth,
            boxSizing: 'border-box',
            mt: 8,
          },
        }}
      >
        <List>
          {menuItems.map((item) => (
            <ListItem
              key={item.id}
              button
              selected={selectedMenu === item.id}
              onClick={() => handleMenuClick(item.id)}
            >
              <ListItemIcon>{item.icon}</ListItemIcon>
              <ListItemText primary={item.label} />
            </ListItem>
          ))}
        </List>
      </Drawer>

      <Box component="main" sx={{ flexGrow: 1, p: 3, mt: 8 }}>
        {selectedMenu === 'dashboard' && (
          <>
            <Typography variant="h4" gutterBottom>
              Dashboard
            </Typography>
            <AdminStats />

            <Paper sx={{ p: 3, mt: 3 }}>
              <Typography variant="h6" gutterBottom>
                Son Aktiviteler
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Aktivite listesi yakında eklenecektir.
              </Typography>
            </Paper>
          </>
        )}

        {selectedMenu === 'users' && (
          <Paper sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>
              Kullanıcı Yönetimi
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Kullanıcı listesi ve yönetim özellikleri yakında eklenecektir.
            </Typography>
          </Paper>
        )}

        {selectedMenu === 'hospitals' && (
          <Paper sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>
              Hastane Yönetimi
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Hastane listesi ve yönetim özellikleri yakında eklenecektir.
            </Typography>
          </Paper>
        )}

        {selectedMenu === 'reservations' && (
          <Paper sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>
              Rezervasyon Yönetimi
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Rezervasyon listesi ve yönetim özellikleri yakında eklenecektir.
            </Typography>
          </Paper>
        )}

        {selectedMenu === 'payments' && (
          <Paper sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>
              Ödeme Yönetimi
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Ödeme listesi ve yönetim özellikleri yakında eklenecektir.
            </Typography>
          </Paper>
        )}

        {selectedMenu === 'reports' && (
          <Paper sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>
              Raporlar
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Raporlar yakında eklenecektir.
            </Typography>
          </Paper>
        )}

        {selectedMenu === 'settings' && (
          <Paper sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>
              Ayarlar
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Sistem ayarları yakında eklenecektir.
            </Typography>
          </Paper>
        )}
      </Box>
    </Box>
  );
}

