import React, { useState, useEffect } from 'react';
import {
  AppBar, Toolbar, Typography, Button, Box, IconButton,
  Menu, MenuItem, Container, Avatar, Divider, Chip, Badge,
  Popover, List, ListItem, ListItemIcon, ListItemText,
  TextField, InputAdornment, Select, FormControl, Switch,
  FormControlLabel, Tooltip, LinearProgress, useScrollTrigger,
  Slide,
} from '@mui/material';
import {
  LocalHospital, Dashboard, Timeline, AccountBalanceWallet,
  Notifications, Settings, Person, Logout, Search, Language,
  Translate, AccountBalance, CloudSync, CheckCircle, Warning,
  Sync, SyncDisabled, AccountTree, CurrencyExchange, Wallet,
  Web, Close, Menu as MenuIcon, FlightTakeoff, DirectionsCar,
} from '@mui/icons-material';
import { Search as SearchIcon, Bell, Wallet as WalletIcon, User, Menu as MenuIconLucide, X, Command, Cpu, Sparkles } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import { Link as RouterLink, useNavigate, useLocation } from 'react-router-dom';
import { useTranslation } from '../../i18n';
import { useAuth } from '../../hooks/useAuth';
import { useQuery } from '@tanstack/react-query';
import api from '../../services/api';
import ThemeToggle from '../ThemeToggle';
import LanguageSwitcher from '../LanguageSwitcher';
import { fadeInUp, hoverLift } from '../../utils/ui-helpers';

/**
 * Enhanced Professional Header - Modern UI/UX
 * Material-UI + Tailwind CSS + Framer Motion + Lucide-React entegrasyonu
 * Blockchain, IoT, Multi-Tenancy, AI entegrasyonlu
 */
const EnhancedHeader = () => {
  const { t, i18n, ready } = useTranslation();
  const navigate = useNavigate();
  const location = useLocation();
  const { user, isAuthenticated, logout } = useAuth();
  
  // Scroll detection
  const [isScrolled, setIsScrolled] = useState(false);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  
  useEffect(() => {
    const handleScroll = () => setIsScrolled(window.scrollY > 20);
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  // State management
  const [userMenuAnchor, setUserMenuAnchor] = useState(null);
  const [notificationAnchor, setNotificationAnchor] = useState(null);
  const [tenantMenuAnchor, setTenantMenuAnchor] = useState(null);
  const [travelMenuAnchor, setTravelMenuAnchor] = useState(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [translationEnabled, setTranslationEnabled] = useState(false);
  const [selectedCurrency, setSelectedCurrency] = useState('USD');
  const [selectedTenant, setSelectedTenant] = useState(null);

  // Blockchain status
  const { data: blockchainStatus } = useQuery({
    queryKey: ['blockchain-status'],
    queryFn: async () => {
      try {
        const response = await api.get('/blockchain/polygon/status');
        return response.data;
      } catch (error) {
        // Silently handle 503 errors (service unavailable)
        if (error?.response?.status === 503) {
          return { connected: false, network: 'Polygon' };
        }
        return { connected: false, network: 'Polygon' };
      }
    },
    refetchInterval: 60000, // Reduced from 30s to 60s
    retry: false, // Don't retry on failure
  });

  // IoT sync status
  const { data: iotStatus } = useQuery({
    queryKey: ['iot-sync-status'],
    queryFn: async () => {
      try {
        const response = await api.get('/iot-monitoring/user/me/latest');
        return response.data;
      } catch (error) {
        return null;
      }
    },
    refetchInterval: 10000,
    enabled: isAuthenticated,
  });

  // Wallet balance
  const { data: walletBalance } = useQuery({
    queryKey: ['wallet-balance'],
    queryFn: async () => {
      try {
        const response = await api.get('/health-wallet/balance');
        return response.data;
      } catch (error) {
        return null;
      }
    },
    refetchInterval: 60000,
    enabled: isAuthenticated,
  });

  // AI notifications
  const { data: aiNotifications } = useQuery({
    queryKey: ['ai-notifications'],
    queryFn: async () => {
      try {
        const response = await api.get('/notifications/ai-insights');
        return response.data;
      } catch (error) {
        return [];
      }
    },
    refetchInterval: 30000,
    enabled: isAuthenticated,
  });

  // User tenants (multi-tenancy)
  const { data: userTenants } = useQuery({
    queryKey: ['user-tenants'],
    queryFn: async () => {
      try {
        const response = await api.get('/tenants/user/me');
        return response.data;
      } catch (error) {
        return [];
      }
    },
    enabled: isAuthenticated,
  });

  useEffect(() => {
    if (userTenants && userTenants.length > 0 && !selectedTenant) {
      setSelectedTenant(userTenants[0]);
    }
  }, [userTenants, selectedTenant]);

  const criticalNotifications = aiNotifications?.filter((n) => n.priority === 'CRITICAL') || [];
  const unreadCount = aiNotifications?.filter((n) => !n.read).length || 0;

  const handleUserMenuOpen = (event) => setUserMenuAnchor(event.currentTarget);
  const handleUserMenuClose = () => setUserMenuAnchor(null);
  const handleNotificationOpen = (event) => setNotificationAnchor(event.currentTarget);
  const handleNotificationClose = () => setNotificationAnchor(null);
  const handleTenantMenuOpen = (event) => setTenantMenuAnchor(event.currentTarget);
  const handleTenantMenuClose = () => setTenantMenuAnchor(null);
  const handleTravelMenuOpen = (event) => setTravelMenuAnchor(event.currentTarget);
  const handleTravelMenuClose = () => setTravelMenuAnchor(null);

  const handleLogout = () => {
    logout();
    handleUserMenuClose();
    navigate('/');
  };

  const handleSearch = (e) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      navigate(`/search?q=${encodeURIComponent(searchQuery)}`);
    }
  };

  // Keyboard shortcut for search (Ctrl+K)
  useEffect(() => {
    const handleKeyDown = (e) => {
      if ((e.ctrlKey || e.metaKey) && e.key === 'k') {
        e.preventDefault();
        document.querySelector('input[type="text"]')?.focus();
      }
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, []);

  return (
    <Slide direction="down" in={!isScrolled} appear={false}>
      <AppBar
        position="sticky"
        className={`transition-all duration-300 ${
          isScrolled 
            ? 'bg-white/70 backdrop-blur-xl border-b border-slate-200/50 shadow-md' 
            : 'bg-transparent shadow-none'
        }`}
        sx={{
          background: isScrolled 
            ? 'linear-gradient(135deg, rgba(255,255,255,0.95) 0%, rgba(255,255,255,0.98) 100%)'
            : 'transparent',
          backdropFilter: isScrolled ? 'blur(20px) saturate(180%)' : 'none',
          WebkitBackdropFilter: isScrolled ? 'blur(20px) saturate(180%)' : 'none',
          borderBottom: isScrolled ? '1px solid rgba(0,0,0,0.08)' : 'none',
          boxShadow: isScrolled ? '0 4px 24px rgba(0,0,0,0.06)' : 'none',
          transition: 'all 0.3s ease',
        }}
      >
        <Container maxWidth="xl">
          <Toolbar disableGutters sx={{ py: { xs: 1.5, md: 2 }, minHeight: { xs: 64, md: 72 } }}>
            {/* LEFT: Brand Logo - Material-UI + Tailwind + Framer Motion */}
            <motion.div
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ duration: 0.3 }}
            >
              <Box 
                component={RouterLink}
                to="/"
                className="flex items-center gap-3 no-underline"
                sx={{ 
                  display: 'flex', 
                  alignItems: 'center', 
                  gap: 1.5,
                  flexShrink: 0, 
                  mr: { xs: 2, md: 4 },
                  textDecoration: 'none',
                  transition: 'transform 0.2s ease',
                  '&:hover': {
                    transform: 'scale(1.02)',
                    textDecoration: 'none',
                  },
                }}
              >
                {/* Logo Icon - Lucide-React */}
                <motion.div
                  whileHover={{ rotate: 5, scale: 1.1 }}
                  whileTap={{ scale: 0.95 }}
                  className="p-2 bg-indigo-600 rounded-xl shadow-indigo-200 shadow-lg text-white"
                  style={{
                    padding: '8px',
                    background: 'linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%)',
                    borderRadius: '12px',
                    boxShadow: '0 10px 25px rgba(79, 70, 229, 0.3)',
                  }}
                >
                  <Cpu size={24} strokeWidth={2.5} />
                </motion.div>
                
                <Box>
                  <Typography
                    variant="h6"
                    className="text-xl font-black tracking-tighter text-slate-900"
                    sx={{
                      fontWeight: 900,
                      fontSize: { xs: '1rem', md: '1.25rem' },
                      letterSpacing: '-0.02em',
                      background: 'linear-gradient(135deg, #1e293b 0%, #4f46e5 100%)',
                      WebkitBackgroundClip: 'text',
                      WebkitTextFillColor: 'transparent',
                      backgroundClip: 'text',
                    }}
                  >
                    HEALTH<span className="text-indigo-600" style={{ color: '#4f46e5' }}>CHAIN</span>
                  </Typography>
                  <Box className="flex items-center gap-1.5" sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                    <motion.span
                      animate={{ opacity: [1, 0.5, 1] }}
                      transition={{ duration: 2, repeat: Infinity }}
                      className="block h-1.5 w-1.5 rounded-full bg-green-500"
                      style={{ 
                        display: 'block',
                        height: '6px',
                        width: '6px',
                        borderRadius: '50%',
                        backgroundColor: '#22c55e',
                      }}
                    />
                    <Typography 
                      variant="caption" 
                      className="text-[10px] font-bold text-slate-400 uppercase tracking-widest"
                      sx={{ 
                        fontSize: '0.65rem', 
                        fontWeight: 700, 
                        color: '#94a3b8',
                        letterSpacing: '0.1em',
                        textTransform: 'uppercase',
                      }}
                    >
                      MAINNET v2.5
                    </Typography>
                  </Box>
                  {selectedTenant && (
                    <Typography 
                      variant="caption" 
                      sx={{ 
                        color: 'text.secondary',
                        fontSize: '0.7rem',
                        fontWeight: 500,
                        display: 'block',
                        mt: 0.25,
                      }}
                    >
                      {selectedTenant.name}
                    </Typography>
                  )}
                </Box>
              </Box>
            </motion.div>

            {/* CENTER: Navigation + Search - Material-UI + Tailwind + Framer Motion */}
            <Box sx={{ flexGrow: 1, display: 'flex', alignItems: 'center', gap: { xs: 1, md: 2 }, justifyContent: 'center' }}>
              {/* Desktop Navigation */}
              <Box sx={{ display: { xs: 'none', lg: 'flex' }, gap: 0.5, flexWrap: 'wrap' }}>
                {/* Ana Sayfalar - Herkese Açık */}
                <motion.div whileHover="hover" variants={hoverLift}>
                  <Button 
                    component={RouterLink} 
                    to="/hospitals"
                    sx={{
                      px: 2,
                      py: 1,
                      borderRadius: 3,
                      textTransform: 'none',
                      fontWeight: 600,
                      fontSize: '0.875rem',
                      color: location.pathname === '/hospitals' ? 'primary.main' : 'text.primary',
                      bgcolor: location.pathname === '/hospitals' ? 'rgba(79, 70, 229, 0.08)' : 'transparent',
                      transition: 'all 0.2s ease',
                      '&:hover': {
                        bgcolor: 'rgba(79, 70, 229, 0.08)',
                        transform: 'translateY(-1px)',
                      }
                    }}
                  >
                    {t('nav.hospitals', 'Hastaneler')}
                  </Button>
                </motion.div>
                
                <motion.div whileHover="hover" variants={hoverLift}>
                  <Button 
                    component={RouterLink} 
                    to="/doctors"
                    sx={{
                      px: 2,
                      py: 1,
                      borderRadius: 3,
                      textTransform: 'none',
                      fontWeight: 600,
                      fontSize: '0.875rem',
                      color: location.pathname === '/doctors' ? 'primary.main' : 'text.primary',
                      bgcolor: location.pathname === '/doctors' ? 'rgba(79, 70, 229, 0.08)' : 'transparent',
                      transition: 'all 0.2s ease',
                      '&:hover': {
                        bgcolor: 'rgba(79, 70, 229, 0.08)',
                        transform: 'translateY(-1px)',
                      }
                    }}
                  >
                    {t('nav.doctors', 'Doktorlar')}
                  </Button>
                </motion.div>
                
                <motion.div whileHover="hover" variants={hoverLift}>
                  <Button 
                    component={RouterLink} 
                    to="/accommodations"
                    sx={{
                      px: 2,
                      py: 1,
                      borderRadius: 3,
                      textTransform: 'none',
                      fontWeight: 600,
                      fontSize: '0.875rem',
                      color: location.pathname === '/accommodations' ? 'primary.main' : 'text.primary',
                      bgcolor: location.pathname === '/accommodations' ? 'rgba(79, 70, 229, 0.08)' : 'transparent',
                      transition: 'all 0.2s ease',
                      '&:hover': {
                        bgcolor: 'rgba(79, 70, 229, 0.08)',
                        transform: 'translateY(-1px)',
                      }
                    }}
                  >
                    {t('nav.accommodations', 'Konaklama')}
                  </Button>
                </motion.div>
                
                <motion.div whileHover="hover" variants={hoverLift}>
                  <Button 
                    component={RouterLink} 
                    to="/packages"
                    sx={{
                      px: 2,
                      py: 1,
                      borderRadius: 3,
                      textTransform: 'none',
                      fontWeight: 600,
                      fontSize: '0.875rem',
                      color: location.pathname === '/packages' ? 'primary.main' : 'text.primary',
                      bgcolor: location.pathname === '/packages' ? 'rgba(79, 70, 229, 0.08)' : 'transparent',
                      transition: 'all 0.2s ease',
                      '&:hover': {
                        bgcolor: 'rgba(79, 70, 229, 0.08)',
                        transform: 'translateY(-1px)',
                      }
                    }}
                  >
                    {t('nav.packages', 'Paketler')}
                  </Button>
                </motion.div>
                
                {/* Seyahat Dropdown */}
                <motion.div whileHover="hover" variants={hoverLift}>
                  <Button 
                    onClick={handleTravelMenuOpen}
                    endIcon={<FlightTakeoff />}
                    sx={{
                      px: 2,
                      py: 1,
                      borderRadius: 3,
                      textTransform: 'none',
                      fontWeight: 600,
                      fontSize: '0.875rem',
                      color: ['/flights', '/transfers', '/car-rentals'].some(path => location.pathname.startsWith(path)) ? 'primary.main' : 'text.primary',
                      bgcolor: ['/flights', '/transfers', '/car-rentals'].some(path => location.pathname.startsWith(path)) ? 'rgba(79, 70, 229, 0.08)' : 'transparent',
                      transition: 'all 0.2s ease',
                      '&:hover': {
                        bgcolor: 'rgba(79, 70, 229, 0.08)',
                        transform: 'translateY(-1px)',
                      }
                    }}
                  >
                    {t('nav.travel', 'Seyahat')}
                  </Button>
                </motion.div>

                {/* Giriş Yapıldıktan Sonra Görünür Sayfalar */}
                {isAuthenticated && (
                  <>
                    <motion.div whileHover="hover" variants={hoverLift}>
                      <Button 
                        component={RouterLink} 
                        to="/dashboard"
                        sx={{
                          px: 2,
                          py: 1,
                          borderRadius: 3,
                          textTransform: 'none',
                          fontWeight: 600,
                          fontSize: '0.875rem',
                          color: location.pathname === '/dashboard' ? 'primary.main' : 'text.primary',
                          bgcolor: location.pathname === '/dashboard' ? 'rgba(79, 70, 229, 0.08)' : 'transparent',
                          transition: 'all 0.2s ease',
                          '&:hover': {
                            bgcolor: 'rgba(79, 70, 229, 0.08)',
                            transform: 'translateY(-1px)',
                          }
                        }}
                      >
                        {t('nav.dashboard', 'Dashboard')}
                      </Button>
                    </motion.div>
                    
                    <motion.div whileHover="hover" variants={hoverLift}>
                      <Button 
                        component={RouterLink} 
                        to="/patient-journey"
                        sx={{
                          px: 2,
                          py: 1,
                          borderRadius: 3,
                          textTransform: 'none',
                          fontWeight: 600,
                          fontSize: '0.875rem',
                          color: location.pathname === '/patient-journey' ? 'primary.main' : 'text.primary',
                          bgcolor: location.pathname === '/patient-journey' ? 'rgba(79, 70, 229, 0.08)' : 'transparent',
                          transition: 'all 0.2s ease',
                          '&:hover': {
                            bgcolor: 'rgba(79, 70, 229, 0.08)',
                            transform: 'translateY(-1px)',
                          }
                        }}
                      >
                        {t('nav.journey', 'Yolculuk')}
                      </Button>
                    </motion.div>
                    
                    <motion.div whileHover="hover" variants={hoverLift}>
                      <Button 
                        component={RouterLink} 
                        to="/health-wallet"
                        sx={{
                          px: 2,
                          py: 1,
                          borderRadius: 3,
                          textTransform: 'none',
                          fontWeight: 600,
                          fontSize: '0.875rem',
                          color: location.pathname === '/health-wallet' ? 'primary.main' : 'text.primary',
                          bgcolor: location.pathname === '/health-wallet' ? 'rgba(79, 70, 229, 0.08)' : 'transparent',
                          transition: 'all 0.2s ease',
                          '&:hover': {
                            bgcolor: 'rgba(79, 70, 229, 0.08)',
                            transform: 'translateY(-1px)',
                          }
                        }}
                      >
                        {t('nav.wallet', 'Cüzdan')}
                      </Button>
                    </motion.div>
                    
                    <motion.div whileHover="hover" variants={hoverLift}>
                      <Button 
                        component={RouterLink} 
                        to="/reservations"
                        sx={{
                          px: 2,
                          py: 1,
                          borderRadius: 3,
                          textTransform: 'none',
                          fontWeight: 600,
                          fontSize: '0.875rem',
                          color: location.pathname.startsWith('/reservations') ? 'primary.main' : 'text.primary',
                          bgcolor: location.pathname.startsWith('/reservations') ? 'rgba(79, 70, 229, 0.08)' : 'transparent',
                          transition: 'all 0.2s ease',
                          '&:hover': {
                            bgcolor: 'rgba(79, 70, 229, 0.08)',
                            transform: 'translateY(-1px)',
                          }
                        }}
                      >
                        {t('nav.reservations', 'Rezervasyonlar')}
                      </Button>
                    </motion.div>
                    
                    <motion.div whileHover="hover" variants={hoverLift}>
                      <Button 
                        component={RouterLink} 
                        to="/digital-twin"
                        sx={{
                          px: 2,
                          py: 1,
                          borderRadius: 3,
                          textTransform: 'none',
                          fontWeight: 600,
                          fontSize: '0.875rem',
                          color: location.pathname === '/digital-twin' ? 'primary.main' : 'text.primary',
                          bgcolor: location.pathname === '/digital-twin' ? 'rgba(79, 70, 229, 0.08)' : 'transparent',
                          transition: 'all 0.2s ease',
                          '&:hover': {
                            bgcolor: 'rgba(79, 70, 229, 0.08)',
                            transform: 'translateY(-1px)',
                          }
                        }}
                      >
                        {t('nav.digitalTwin', 'Dijital İkiz')}
                      </Button>
                    </motion.div>
                    
                    <motion.div whileHover="hover" variants={hoverLift}>
                      <Button 
                        component={RouterLink} 
                        to="/favorites"
                        sx={{
                          px: 2,
                          py: 1,
                          borderRadius: 3,
                          textTransform: 'none',
                          fontWeight: 600,
                          fontSize: '0.875rem',
                          color: location.pathname === '/favorites' ? 'primary.main' : 'text.primary',
                          bgcolor: location.pathname === '/favorites' ? 'rgba(79, 70, 229, 0.08)' : 'transparent',
                          transition: 'all 0.2s ease',
                          '&:hover': {
                            bgcolor: 'rgba(79, 70, 229, 0.08)',
                            transform: 'translateY(-1px)',
                          }
                        }}
                      >
                        {t('nav.favorites', 'Favoriler')}
                      </Button>
                    </motion.div>
                  </>
                )}
              </Box>

              {/* Global Search - Material-UI + Tailwind + Lucide-React */}
              <Box 
                component="form" 
                onSubmit={handleSearch} 
                className="flex-1 max-w-md"
                sx={{ flexGrow: 1, maxWidth: { xs: 200, md: 400 } }}
              >
                <motion.div
                  whileFocus={{ scale: 1.02 }}
                  className="relative group"
                >
                  <TextField
                    fullWidth
                    size="small"
                    placeholder={t('header.searchPlaceholder', 'AI Asistanına sor... (Ctrl+K)')}
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    InputProps={{
                      startAdornment: (
                        <InputAdornment position="start">
                          <SearchIcon size={18} className="text-slate-400 group-focus-within:text-indigo-600 transition-colors" style={{ color: '#94a3b8' }} strokeWidth={2} />
                        </InputAdornment>
                      ),
                      endAdornment: (
                        <InputAdornment position="end">
                          <kbd className="hidden sm:inline-block px-1.5 py-0.5 text-[10px] font-medium text-slate-400 bg-white border border-slate-200 rounded-md shadow-sm">
                            <Command size={10} className="inline mr-1" strokeWidth={2} />
                            K
                          </kbd>
                        </InputAdornment>
                      ),
                    }}
                    className="pl-10 pr-12 py-2.5 bg-slate-100 border-transparent focus:bg-white focus:ring-2 focus:ring-indigo-100 focus:border-indigo-200 rounded-2xl text-sm transition-all outline-none"
                    sx={{ 
                      bgcolor: 'rgba(0,0,0,0.02)',
                      borderRadius: 3,
                      transition: 'all 0.2s ease',
                      '& .MuiOutlinedInput-root': {
                        '& fieldset': {
                          borderColor: 'transparent',
                        },
                        '&:hover fieldset': {
                          borderColor: 'rgba(79, 70, 229, 0.3)',
                        },
                        '&.Mui-focused fieldset': {
                          borderColor: 'primary.main',
                          borderWidth: '2px',
                        },
                      },
                      '&:hover': {
                        bgcolor: 'rgba(0,0,0,0.04)',
                      }
                    }}
                  />
                </motion.div>
              </Box>
            </Box>

            {/* RIGHT: Status Indicators + User Actions - Material-UI + Tailwind + Framer Motion */}
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, flexShrink: 0, position: 'relative' }}>
              {isAuthenticated && (
                <>
                  {/* Blockchain Status */}
                  <Tooltip title={blockchainStatus?.connected ? 'Polygon Mainnet - Connected' : 'Blockchain Disconnected'}>
                    <motion.div
                      whileHover={{ scale: 1.1 }}
                      whileTap={{ scale: 0.95 }}
                    >
                      <Chip
                        icon={blockchainStatus?.connected ? <CheckCircle fontSize="small" /> : <Warning fontSize="small" />}
                        label={blockchainStatus?.network || 'Polygon'}
                        size="small"
                        color={blockchainStatus?.connected ? 'success' : 'error'}
                        className="hidden lg:flex rounded-xl font-bold"
                        sx={{ 
                          display: { xs: 'none', lg: 'flex' },
                          borderRadius: 3,
                          fontWeight: 700,
                        }}
                      />
                    </motion.div>
                  </Tooltip>

                  {/* IoT Sync Status */}
                  <Tooltip title={iotStatus ? 'IoT Data Syncing' : 'No IoT Data'}>
                    <motion.div
                      whileHover={{ scale: 1.1 }}
                      whileTap={{ scale: 0.95 }}
                    >
                      <IconButton 
                        size="small" 
                        className="hover:bg-indigo-50 rounded-xl"
                        sx={{ 
                          display: { xs: 'none', md: 'flex' },
                          borderRadius: 3,
                        }}
                      >
                        {iotStatus ? (
                          <Sync sx={{ color: 'success.main', animation: 'spin 2s linear infinite' }} />
                        ) : (
                          <SyncDisabled sx={{ color: 'text.disabled' }} />
                        )}
                      </IconButton>
                    </motion.div>
                  </Tooltip>

                  {/* Wallet Balance */}
                  {walletBalance && (
                    <Tooltip title={`Health Token Balance: ${walletBalance.balance} HT`}>
                      <motion.div
                        whileHover={{ scale: 1.05 }}
                        whileTap={{ scale: 0.95 }}
                      >
                        <Chip
                          icon={<WalletIcon size={16} style={{ marginLeft: 4 }} strokeWidth={2} />}
                          label={`${walletBalance.balance || 0} HT`}
                          size="small"
                          color="primary"
                          variant="outlined"
                          className="hidden sm:flex rounded-xl font-bold cursor-pointer hover:bg-indigo-50"
                          sx={{ 
                            display: { xs: 'none', sm: 'flex' },
                            borderRadius: 3,
                            fontWeight: 700,
                            cursor: 'pointer',
                            '&:hover': {
                              bgcolor: 'rgba(79, 70, 229, 0.08)',
                            }
                          }}
                          onClick={() => navigate('/health-wallet')}
                        />
                      </motion.div>
                    </Tooltip>
                  )}

                  {/* AI Notifications - Lucide-React */}
                  <Tooltip title={`${unreadCount} unread notifications`}>
                    <motion.div
                      whileHover={{ scale: 1.1 }}
                      whileTap={{ scale: 0.95 }}
                    >
                      <IconButton 
                        onClick={handleNotificationOpen} 
                        size="small"
                        className="relative hover:bg-indigo-50 rounded-xl"
                        sx={{ borderRadius: 3 }}
                      >
                        <Badge badgeContent={unreadCount} color="error">
                          <Bell size={22} strokeWidth={2} style={{ color: '#64748b' }} />
                        </Badge>
                      </IconButton>
                    </motion.div>
                  </Tooltip>

                  {/* Currency Switcher */}
                  <FormControl size="small" sx={{ minWidth: 80, display: { xs: 'none', md: 'block' } }}>
                    <Select
                      value={selectedCurrency}
                      onChange={(e) => setSelectedCurrency(e.target.value)}
                      className="rounded-xl"
                      sx={{ 
                        height: 32,
                        borderRadius: 3,
                      }}
                    >
                      <MenuItem value="USD">USD</MenuItem>
                      <MenuItem value="EUR">EUR</MenuItem>
                      <MenuItem value="TRY">TRY</MenuItem>
                      <MenuItem value="HT">HealthToken</MenuItem>
                    </Select>
                  </FormControl>

                  {/* User Menu - Modern Avatar */}
                  <motion.div
                    whileHover={{ scale: 1.1 }}
                    whileTap={{ scale: 0.95 }}
                  >
                    <IconButton 
                      onClick={handleUserMenuOpen} 
                      size="small"
                      className="ml-1"
                      sx={{
                        ml: 1,
                        transition: 'all 0.2s ease',
                      }}
                    >
                      <Avatar 
                        sx={{ 
                          width: { xs: 32, md: 36 }, 
                          height: { xs: 32, md: 36 },
                          background: 'linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%)',
                          boxShadow: '0 4px 12px rgba(79, 70, 229, 0.4)',
                          fontWeight: 700,
                          fontSize: '0.875rem',
                          transition: 'all 0.2s ease',
                          '&:hover': {
                            boxShadow: '0 6px 16px rgba(79, 70, 229, 0.6)',
                          }
                        }}
                      >
                        {user?.firstName?.charAt(0)?.toUpperCase() || user?.name?.charAt(0)?.toUpperCase() || 'U'}
                      </Avatar>
                    </IconButton>
                  </motion.div>
                </>
              )}

              {!isAuthenticated && (
                <>
                  <Button 
                    component={RouterLink} 
                    to="/login" 
                    variant="text"
                    size="small"
                    className="px-4 py-2 rounded-xl text-sm font-semibold hover:bg-gray-50"
                    sx={{
                      px: 2.5,
                      py: 1,
                      borderRadius: 3,
                      textTransform: 'none',
                      fontWeight: 600,
                      fontSize: '0.875rem',
                      color: 'text.primary',
                      transition: 'all 0.2s ease',
                      '&:hover': {
                        bgcolor: 'rgba(0,0,0,0.04)',
                      }
                    }}
                  >
                    {t('common.login', 'Giriş Yap')}
                  </Button>
                  <Button 
                    component={RouterLink} 
                    to="/register" 
                    variant="contained"
                    size="small"
                    className="px-6 py-2 rounded-xl text-sm font-bold shadow-lg hover:shadow-xl transition-all"
                    sx={{
                      px: 3,
                      py: 1,
                      borderRadius: 3,
                      textTransform: 'none',
                      fontWeight: 700,
                      fontSize: '0.875rem',
                      background: 'linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%)',
                      boxShadow: '0 10px 25px rgba(79, 70, 229, 0.3)',
                      transition: 'all 0.3s ease',
                      '&:hover': {
                        transform: 'translateY(-2px)',
                        boxShadow: '0 15px 35px rgba(79, 70, 229, 0.4)',
                        background: 'linear-gradient(135deg, #7c3aed 0%, #4f46e5 100%)',
                      }
                    }}
                  >
                    {t('common.register', 'Kayıt Ol')}
                  </Button>
                </>
              )}

              {/* Theme Toggle & Language Switcher */}
              <Box 
                sx={{ 
                  display: 'flex', 
                  flexDirection: 'column', 
                  alignItems: 'center', 
                  gap: 0.5,
                  ml: 1,
                }}
              >
                <ThemeToggle />
                <LanguageSwitcher />
              </Box>

              {/* Mobile Menu Button */}
              <IconButton
                onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
                sx={{ display: { xs: 'flex', lg: 'none' }, ml: 1 }}
              >
                {mobileMenuOpen ? <X size={24} strokeWidth={2} /> : <MenuIconLucide size={24} strokeWidth={2} />}
              </IconButton>
            </Box>
          </Toolbar>
        </Container>

        {/* Mobile Menu */}
        <AnimatePresence>
          {mobileMenuOpen && (
            <motion.div
              initial={{ opacity: 0, height: 0 }}
              animate={{ opacity: 1, height: 'auto' }}
              exit={{ opacity: 0, height: 0 }}
              className="bg-white border-t border-gray-200"
            >
              <Box sx={{ p: 2, display: 'flex', flexDirection: 'column', gap: 1 }}>
                {/* Herkese Açık Sayfalar */}
                <Button component={RouterLink} to="/hospitals" fullWidth className="rounded-xl">
                  {t('nav.hospitals', 'Hastaneler')}
                </Button>
                <Button component={RouterLink} to="/doctors" fullWidth className="rounded-xl">
                  {t('nav.doctors', 'Doktorlar')}
                </Button>
                <Button component={RouterLink} to="/accommodations" fullWidth className="rounded-xl">
                  {t('nav.accommodations', 'Konaklama')}
                </Button>
                <Button component={RouterLink} to="/packages" fullWidth className="rounded-xl">
                  {t('nav.packages', 'Paketler')}
                </Button>
                <Button onClick={handleTravelMenuOpen} fullWidth className="rounded-xl">
                  {t('nav.travel', 'Seyahat')}
                </Button>
                
                {/* Giriş Yapıldıktan Sonra Görünür */}
                {isAuthenticated && (
                  <>
                    <Divider sx={{ my: 1 }} />
                    <Button component={RouterLink} to="/dashboard" fullWidth className="rounded-xl">
                      {t('nav.dashboard', 'Dashboard')}
                    </Button>
                    <Button component={RouterLink} to="/patient-journey" fullWidth className="rounded-xl">
                      {t('nav.journey', 'Yolculuk')}
                    </Button>
                    <Button component={RouterLink} to="/health-wallet" fullWidth className="rounded-xl">
                      {t('nav.wallet', 'Cüzdan')}
                    </Button>
                    <Button component={RouterLink} to="/reservations" fullWidth className="rounded-xl">
                      {t('nav.reservations', 'Rezervasyonlar')}
                    </Button>
                    <Button component={RouterLink} to="/digital-twin" fullWidth className="rounded-xl">
                      {t('nav.digitalTwin', 'Dijital İkiz')}
                    </Button>
                    <Button component={RouterLink} to="/favorites" fullWidth className="rounded-xl">
                      {t('nav.favorites', 'Favoriler')}
                    </Button>
                  </>
                )}
              </Box>
            </motion.div>
          )}
        </AnimatePresence>

        {/* Notification Popover */}
        <Popover
          open={Boolean(notificationAnchor)}
          anchorEl={notificationAnchor}
          onClose={handleNotificationClose}
          anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
          transformOrigin={{ vertical: 'top', horizontal: 'right' }}
          PaperProps={{ 
            sx: { 
              width: 400, 
              maxHeight: 500,
              borderRadius: 4,
              boxShadow: '0 20px 25px -5px rgba(0, 0, 0, 0.1)',
            } 
          }}
        >
          <Box sx={{ p: 2, borderBottom: 1, borderColor: 'divider' }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <Typography variant="h6" className="font-bold" sx={{ fontWeight: 700 }}>
                {t('header.notifications', 'Bildirimler')}
              </Typography>
              <IconButton size="small" onClick={handleNotificationClose}>
                <Close />
              </IconButton>
            </Box>
          </Box>
          <List sx={{ maxHeight: 400, overflow: 'auto' }}>
            {criticalNotifications.length > 0 && (
              <ListItem>
                <Alert severity="error" sx={{ width: '100%', borderRadius: 2 }}>
                  {criticalNotifications[0].message}
                </Alert>
              </ListItem>
            )}
            {aiNotifications?.slice(0, 10).map((notification) => (
              <ListItem
                key={notification.id}
                sx={{
                  bgcolor: notification.read ? 'transparent' : 'action.selected',
                  borderLeft: notification.priority === 'CRITICAL' ? '4px solid' : 'none',
                  borderColor: 'error.main',
                  borderRadius: 2,
                  mb: 0.5,
                }}
              >
                <ListItemIcon>
                  {notification.priority === 'CRITICAL' ? (
                    <Warning color="error" />
                  ) : (
                    <Notifications color="primary" />
                  )}
                </ListItemIcon>
                <ListItemText
                  primary={notification.title}
                  secondary={notification.message}
                />
              </ListItem>
            ))}
            {(!aiNotifications || aiNotifications.length === 0) && (
              <ListItem>
                <ListItemText primary={t('header.noNotifications', 'Bildirim yok')} />
              </ListItem>
            )}
          </List>
          <Box sx={{ p: 1, borderTop: 1, borderColor: 'divider', textAlign: 'center' }}>
            <Button size="small" onClick={() => { navigate('/notifications'); handleNotificationClose(); }} className="rounded-xl">
              {t('header.viewAll', 'Tümünü Gör')}
            </Button>
          </Box>
        </Popover>

        {/* Tenant Switcher Menu */}
        <Menu
          anchorEl={tenantMenuAnchor}
          open={Boolean(tenantMenuAnchor)}
          onClose={handleTenantMenuClose}
          PaperProps={{ sx: { borderRadius: 3, mt: 1 } }}
        >
          {userTenants?.map((tenant) => (
            <MenuItem
              key={tenant.id}
              selected={selectedTenant?.id === tenant.id}
              onClick={() => {
                setSelectedTenant(tenant);
                handleTenantMenuClose();
              }}
              className="rounded-lg"
              sx={{ borderRadius: 2, mb: 0.5 }}
            >
              <ListItemIcon>
                <AccountTree />
              </ListItemIcon>
              <ListItemText primary={tenant.name} secondary={tenant.type} />
            </MenuItem>
          ))}
        </Menu>

        {/* Travel Menu (Seyahat) */}
        <Menu
          anchorEl={travelMenuAnchor}
          open={Boolean(travelMenuAnchor)}
          onClose={handleTravelMenuClose}
          PaperProps={{ sx: { borderRadius: 3, mt: 1, minWidth: 200 } }}
        >
          <MenuItem
            component={RouterLink}
            to="/flights"
            onClick={handleTravelMenuClose}
            className="rounded-lg"
            sx={{ borderRadius: 2, mb: 0.5 }}
          >
            <ListItemIcon>
              <FlightTakeoff fontSize="small" />
            </ListItemIcon>
            <ListItemText primary={t('nav.flights', 'Uçak Bileti')} />
          </MenuItem>
          <MenuItem
            component={RouterLink}
            to="/transfers"
            onClick={handleTravelMenuClose}
            className="rounded-lg"
            sx={{ borderRadius: 2, mb: 0.5 }}
          >
            <ListItemIcon>
              <AccountBalance fontSize="small" />
            </ListItemIcon>
            <ListItemText primary={t('nav.transfers', 'Transferler')} />
          </MenuItem>
          <MenuItem
            component={RouterLink}
            to="/car-rentals"
            onClick={handleTravelMenuClose}
            className="rounded-lg"
            sx={{ borderRadius: 2 }}
          >
            <ListItemIcon>
              <DirectionsCar fontSize="small" />
            </ListItemIcon>
            <ListItemText primary={t('nav.carRentals', 'Araç Kiralama')} />
          </MenuItem>
        </Menu>

        {/* User Menu */}
        <Menu
          anchorEl={userMenuAnchor}
          open={Boolean(userMenuAnchor)}
          onClose={handleUserMenuClose}
          PaperProps={{ sx: { borderRadius: 3, mt: 1, minWidth: 200 } }}
        >
          <MenuItem onClick={() => { navigate('/dashboard'); handleUserMenuClose(); }} className="rounded-lg" sx={{ borderRadius: 2, mb: 0.5 }}>
            <Dashboard sx={{ mr: 1 }} fontSize="small" />
            {t('dashboard', 'Dashboard')}
          </MenuItem>
          <MenuItem onClick={() => { navigate('/profile'); handleUserMenuClose(); }} className="rounded-lg" sx={{ borderRadius: 2, mb: 0.5 }}>
            <Person sx={{ mr: 1 }} fontSize="small" />
            {t('profile', 'Profil')}
          </MenuItem>
          <MenuItem onClick={() => { navigate('/settings'); handleUserMenuClose(); }} className="rounded-lg" sx={{ borderRadius: 2, mb: 0.5 }}>
            <Settings sx={{ mr: 1 }} fontSize="small" />
            {t('settings', 'Ayarlar')}
          </MenuItem>
          <Divider />
          <MenuItem onClick={handleLogout} className="rounded-lg text-red-600" sx={{ borderRadius: 2, color: 'error.main' }}>
            <Logout sx={{ mr: 1 }} fontSize="small" />
            {t('logout', 'Çıkış Yap')}
          </MenuItem>
        </Menu>

        {/* CSS Animation for Sync Icon */}
        <style>
          {`
            @keyframes spin {
              from { transform: rotate(0deg); }
              to { transform: rotate(360deg); }
            }
          `}
        </style>
      </AppBar>
    </Slide>
  );
};

export default EnhancedHeader;
