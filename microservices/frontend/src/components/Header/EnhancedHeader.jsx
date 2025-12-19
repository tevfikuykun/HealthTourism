import React, { useState, useEffect } from 'react';
import {
  AppBar,
  Toolbar,
  Typography,
  Button,
  Box,
  IconButton,
  Menu,
  MenuItem,
  Container,
  Avatar,
  Divider,
  Chip,
  Badge,
  Popover,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  TextField,
  InputAdornment,
  Select,
  FormControl,
  Switch,
  FormControlLabel,
  Tooltip,
  LinearProgress,
} from '@mui/material';
import {
  LocalHospital,
  Dashboard,
  Timeline,
  AccountBalanceWallet,
  Notifications,
  Settings,
  Person,
  Logout,
  Search,
  Language,
  Translate,
  AccountBalance,
  CloudSync,
  CheckCircle,
  Warning,
  Sync,
  SyncDisabled,
  AccountTree,
  CurrencyExchange,
  Wallet,
  Web,
  Close,
} from '@mui/icons-material';
import { Link as RouterLink, useNavigate } from 'react-router-dom';
import { useTranslation } from '../../i18n';
import { useAuth } from '../../hooks/useAuth';
import { useQuery } from '@tanstack/react-query';
import api from '../../services/api';

/**
 * Enhanced Professional Header
 * Blockchain, IoT, Multi-Tenancy, AI entegrasyonlu
 */
const EnhancedHeader = () => {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const { user, isAuthenticated, logout } = useAuth();
  
  // State management
  const [userMenuAnchor, setUserMenuAnchor] = useState(null);
  const [notificationAnchor, setNotificationAnchor] = useState(null);
  const [tenantMenuAnchor, setTenantMenuAnchor] = useState(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [translationEnabled, setTranslationEnabled] = useState(false);
  const [selectedCurrency, setSelectedCurrency] = useState('USD');
  const [selectedTenant, setSelectedTenant] = useState(null);

  // Blockchain status
  const { data: blockchainStatus } = useQuery({
    queryKey: ['blockchain-status'],
    queryFn: async () => {
      const response = await api.get('/api/blockchain/polygon/status');
      return response.data;
    },
    refetchInterval: 30000,
  });

  // IoT sync status
  const { data: iotStatus } = useQuery({
    queryKey: ['iot-sync-status'],
    queryFn: async () => {
      const response = await api.get('/api/iot-monitoring/user/me/latest');
      return response.data;
    },
    refetchInterval: 10000,
    enabled: isAuthenticated,
  });

  // Wallet balance
  const { data: walletBalance } = useQuery({
    queryKey: ['wallet-balance'],
    queryFn: async () => {
      const response = await api.get('/api/health-wallet/balance');
      return response.data;
    },
    refetchInterval: 60000,
    enabled: isAuthenticated,
  });

  // AI notifications
  const { data: aiNotifications } = useQuery({
    queryKey: ['ai-notifications'],
    queryFn: async () => {
      const response = await api.get('/api/notifications/ai-insights');
      return response.data;
    },
    refetchInterval: 30000,
    enabled: isAuthenticated,
  });

  // User tenants (multi-tenancy)
  const { data: userTenants } = useQuery({
    queryKey: ['user-tenants'],
    queryFn: async () => {
      const response = await api.get('/api/tenants/user/me');
      return response.data;
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

  return (
    <AppBar
      position="sticky"
      sx={{
        bgcolor: 'background.paper',
        borderBottom: 1,
        borderColor: 'divider',
        boxShadow: 2,
      }}
    >
      <Container maxWidth="xl">
        <Toolbar disableGutters sx={{ py: 1 }}>
          {/* LEFT: Brand + Tenant */}
          <Box sx={{ display: 'flex', alignItems: 'center', flexShrink: 0, mr: 3 }}>
            <LocalHospital sx={{ mr: 1, color: 'primary.main', fontSize: 32 }} />
            <Box>
              <Typography
                variant="h6"
                component={RouterLink}
                to="/"
                sx={{
                  fontWeight: 700,
                  color: 'primary.main',
                  textDecoration: 'none',
                  lineHeight: 1.2,
                }}
              >
                HEALTH TURİZM
              </Typography>
              {selectedTenant && (
                <Typography variant="caption" color="text.secondary">
                  {selectedTenant.name}
                </Typography>
              )}
            </Box>
          </Box>

          {/* CENTER: Navigation + Search */}
          <Box sx={{ flexGrow: 1, display: 'flex', alignItems: 'center', gap: 2 }}>
            {/* Navigation */}
            <Box sx={{ display: { xs: 'none', md: 'flex' }, gap: 1 }}>
              <Button component={RouterLink} to="/dashboard" startIcon={<Dashboard />}>
                {t('nav.dashboard')}
              </Button>
              <Button component={RouterLink} to="/journey-timeline" startIcon={<Timeline />}>
                {t('nav.journey')}
              </Button>
              <Button component={RouterLink} to="/health-wallet" startIcon={<AccountBalanceWallet />}>
                {t('nav.wallet')}
              </Button>
              <Button component={RouterLink} to="/ai-health-companion" startIcon={<LocalHospital />}>
                {t('nav.aiAssistant')}
              </Button>
            </Box>

            {/* Global Search */}
            <Box component="form" onSubmit={handleSearch} sx={{ flexGrow: 1, maxWidth: 400 }}>
              <TextField
                fullWidth
                size="small"
                placeholder={t('header.searchPlaceholder')}
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <Search fontSize="small" />
                    </InputAdornment>
                  ),
                }}
                sx={{ bgcolor: 'action.hover', borderRadius: 2 }}
              />
            </Box>
          </Box>

          {/* RIGHT: Status Indicators + User Actions */}
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, flexShrink: 0 }}>
            {isAuthenticated && (
              <>
                {/* Blockchain Status */}
                <Tooltip title={blockchainStatus?.connected ? 'Polygon Mainnet - Connected' : 'Blockchain Disconnected'}>
                  <Chip
                    icon={blockchainStatus?.connected ? <CheckCircle /> : <Warning />}
                    label={blockchainStatus?.network || 'Polygon'}
                    size="small"
                    color={blockchainStatus?.connected ? 'success' : 'error'}
                    sx={{ display: { xs: 'none', lg: 'flex' } }}
                  />
                </Tooltip>

                {/* IoT Sync Status */}
                <Tooltip title={iotStatus ? 'IoT Data Syncing' : 'No IoT Data'}>
                  <IconButton size="small" sx={{ display: { xs: 'none', md: 'flex' } }}>
                    {iotStatus ? (
                      <Sync sx={{ color: 'success.main', animation: 'spin 2s linear infinite' }} />
                    ) : (
                      <SyncDisabled sx={{ color: 'text.disabled' }} />
                    )}
                  </IconButton>
                </Tooltip>

                {/* Wallet Balance */}
                {walletBalance && (
                  <Tooltip title={`Health Token Balance: ${walletBalance.balance} HT`}>
                    <Chip
                      icon={<Wallet />}
                      label={`${walletBalance.balance} HT`}
                      size="small"
                      color="primary"
                      variant="outlined"
                      sx={{ display: { xs: 'none', sm: 'flex' } }}
                      onClick={() => navigate('/health-wallet')}
                    />
                  </Tooltip>
                )}

                {/* Tenant Switcher */}
                {userTenants && userTenants.length > 1 && (
                  <Tooltip title="Switch Organization">
                    <IconButton
                      size="small"
                      onClick={handleTenantMenuOpen}
                      sx={{ display: { xs: 'none', lg: 'flex' } }}
                    >
                      <AccountTree />
                    </IconButton>
                  </Tooltip>
                )}

                {/* AI Notifications */}
                <Tooltip title={`${unreadCount} unread notifications`}>
                  <IconButton onClick={handleNotificationOpen} size="small">
                    <Badge badgeContent={unreadCount} color="error">
                      <Notifications />
                    </Badge>
                  </IconButton>
                </Tooltip>

                {/* Currency Switcher */}
                <FormControl size="small" sx={{ minWidth: 80, display: { xs: 'none', md: 'block' } }}>
                  <Select
                    value={selectedCurrency}
                    onChange={(e) => setSelectedCurrency(e.target.value)}
                    sx={{ height: 32 }}
                  >
                    <MenuItem value="USD">USD</MenuItem>
                    <MenuItem value="EUR">EUR</MenuItem>
                    <MenuItem value="TRY">TRY</MenuItem>
                    <MenuItem value="HT">HealthToken</MenuItem>
                  </Select>
                </FormControl>

                {/* Translation Toggle */}
                <Tooltip title="Real-time Translation">
                  <FormControlLabel
                    control={
                      <Switch
                        size="small"
                        checked={translationEnabled}
                        onChange={(e) => setTranslationEnabled(e.target.checked)}
                      />
                    }
                    label={<Translate fontSize="small" />}
                    sx={{ display: { xs: 'none', lg: 'flex' }, m: 0 }}
                  />
                </Tooltip>

                {/* Language Switcher */}
                <IconButton size="small">
                  <Language />
                </IconButton>

                {/* User Menu */}
                <IconButton onClick={handleUserMenuOpen} size="small">
                  <Avatar sx={{ width: 32, height: 32, bgcolor: 'primary.main' }}>
                    {user?.firstName?.charAt(0)?.toUpperCase() || 'U'}
                  </Avatar>
                </IconButton>
              </>
            )}

            {!isAuthenticated && (
              <>
                <Button component={RouterLink} to="/login" variant="outlined" size="small">
                  {t('login')}
                </Button>
                <Button component={RouterLink} to="/register" variant="contained" size="small">
                  {t('register')}
                </Button>
              </>
            )}
          </Box>
        </Toolbar>
      </Container>

      {/* Notification Popover */}
      <Popover
        open={Boolean(notificationAnchor)}
        anchorEl={notificationAnchor}
        onClose={handleNotificationClose}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
        transformOrigin={{ vertical: 'top', horizontal: 'right' }}
        PaperProps={{ sx: { width: 400, maxHeight: 500 } }}
      >
        <Box sx={{ p: 2, borderBottom: 1, borderColor: 'divider' }}>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <Typography variant="h6">{t('header.notifications')}</Typography>
            <IconButton size="small" onClick={handleNotificationClose}>
              <Close />
            </IconButton>
          </Box>
        </Box>
        <List sx={{ maxHeight: 400, overflow: 'auto' }}>
          {criticalNotifications.length > 0 && (
            <ListItem>
              <Alert severity="error" sx={{ width: '100%' }}>
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
              <ListItemText primary={t('header.noNotifications')} />
            </ListItem>
          )}
        </List>
        <Box sx={{ p: 1, borderTop: 1, borderColor: 'divider', textAlign: 'center' }}>
          <Button size="small" onClick={() => navigate('/notifications')}>
            {t('header.viewAll')}
          </Button>
        </Box>
      </Popover>

      {/* Tenant Switcher Menu */}
      <Menu
        anchorEl={tenantMenuAnchor}
        open={Boolean(tenantMenuAnchor)}
        onClose={handleTenantMenuClose}
      >
        {userTenants?.map((tenant) => (
          <MenuItem
            key={tenant.id}
            selected={selectedTenant?.id === tenant.id}
            onClick={() => {
              setSelectedTenant(tenant);
              handleTenantMenuClose();
            }}
          >
            <ListItemIcon>
              <AccountTree />
            </ListItemIcon>
            <ListItemText primary={tenant.name} secondary={tenant.type} />
          </MenuItem>
        ))}
      </Menu>

      {/* User Menu */}
      <Menu
        anchorEl={userMenuAnchor}
        open={Boolean(userMenuAnchor)}
        onClose={handleUserMenuClose}
      >
        <MenuItem onClick={() => { navigate('/dashboard'); handleUserMenuClose(); }}>
          <Dashboard sx={{ mr: 1 }} fontSize="small" />
          {t('dashboard')}
        </MenuItem>
        <MenuItem onClick={() => { navigate('/profile'); handleUserMenuClose(); }}>
          <Person sx={{ mr: 1 }} fontSize="small" />
          {t('profile')}
        </MenuItem>
        <MenuItem onClick={() => { navigate('/settings'); handleUserMenuClose(); }}>
          <Settings sx={{ mr: 1 }} fontSize="small" />
          {t('settings')}
        </MenuItem>
        <Divider />
        <MenuItem onClick={handleLogout}>
          <Logout sx={{ mr: 1 }} fontSize="small" />
          {t('logout')}
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
  );
};

export default EnhancedHeader;

