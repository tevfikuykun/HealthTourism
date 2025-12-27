import React, { useState } from 'react';
import {
  Box,
  IconButton,
  Badge,
  Drawer,
  Typography,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Button,
  Divider,
  Chip,
  CircularProgress,
  Tooltip,
} from '@mui/material';
import {
  Notifications as NotificationsIcon,
  Close,
  CheckCircle,
  Warning,
  Info,
  Error as ErrorIcon,
} from '@mui/icons-material';
import { motion, AnimatePresence } from 'framer-motion';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { notificationService } from '../../services/api';
import { useAuth } from '../../hooks/useAuth';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from '../../i18n';

/**
 * NotificationDrawer Component
 * Backend'den gelen (veya client-side oluşan) bildirimleri şık bir zil ikonu altında toplayan panel
 */
const NotificationDrawer = () => {
  const { t } = useTranslation();
  const { user, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [open, setOpen] = useState(false);

  // Fetch notifications
  const { data: notifications, isLoading } = useQuery({
    queryKey: ['notifications', user?.id || 'me'],
    queryFn: () => notificationService.getByUser(user?.id || 'me'),
    enabled: isAuthenticated,
    refetchInterval: 30000, // Refetch every 30 seconds
    retry: false,
  });

  const markAsReadMutation = useMutation({
    mutationFn: (id) => notificationService.markAsRead(id),
    onSuccess: () => {
      queryClient.invalidateQueries(['notifications']);
    },
  });

  const markAllAsReadMutation = useMutation({
    mutationFn: () => notificationService.markAllAsRead(user?.id || 'me'),
    onSuccess: () => {
      queryClient.invalidateQueries(['notifications']);
    },
  });

  const notificationList = notifications?.data || [];

  const unreadCount = notificationList.filter((n) => !n.read).length;

  const handleToggleDrawer = () => {
    setOpen(!open);
  };

  const handleCloseDrawer = () => {
    setOpen(false);
  };

  const handleMarkAsRead = (notificationId) => {
    markAsReadMutation.mutate(notificationId);
  };

  const handleMarkAllAsRead = () => {
    markAllAsReadMutation.mutate();
  };

  const handleNotificationClick = (notification) => {
    if (!notification.read) {
      handleMarkAsRead(notification.id);
    }
    
    // Navigate based on notification type or link
    if (notification.link) {
      navigate(notification.link);
    } else if (notification.type === 'RESERVATION') {
      navigate('/reservations');
    } else if (notification.type === 'PAYMENT') {
      navigate('/payments');
    }
    
    handleCloseDrawer();
  };

  const getNotificationIcon = (type, priority) => {
    if (priority === 'CRITICAL' || priority === 'HIGH') {
      return <ErrorIcon color="error" fontSize="small" />;
    }
    if (priority === 'WARNING') {
      return <Warning color="warning" fontSize="small" />;
    }
    switch (type) {
      case 'SUCCESS':
        return <CheckCircle color="success" fontSize="small" />;
      case 'INFO':
        return <Info color="info" fontSize="small" />;
      default:
        return <NotificationsIcon fontSize="small" />;
    }
  };

  const getNotificationColor = (priority) => {
    switch (priority) {
      case 'CRITICAL':
      case 'HIGH':
        return 'error';
      case 'WARNING':
        return 'warning';
      case 'INFO':
        return 'info';
      default:
        return 'default';
    }
  };

  if (!isAuthenticated) {
    return null;
  }

  return (
    <>
      <Tooltip title={t('notifications', 'Bildirimler')}>
        <IconButton
          onClick={handleToggleDrawer}
          sx={{
            position: 'relative',
            '&:hover': {
              transform: 'scale(1.1)',
            },
            transition: 'transform 0.2s ease',
          }}
        >
          <motion.div
            animate={unreadCount > 0 ? { scale: [1, 1.2, 1] } : {}}
            transition={{ duration: 0.5, repeat: Infinity, repeatDelay: 3 }}
          >
            <Badge
              badgeContent={unreadCount}
              color="error"
              sx={{
                '& .MuiBadge-badge': {
                  fontSize: '0.7rem',
                  minWidth: '18px',
                  height: '18px',
                },
              }}
            >
              <NotificationsIcon />
            </Badge>
          </motion.div>
        </IconButton>
      </Tooltip>

      <Drawer
        anchor="right"
        open={open}
        onClose={handleCloseDrawer}
        PaperProps={{
          sx: {
            width: { xs: '100%', sm: 400 },
            borderRadius: '16px 0 0 16px',
          },
        }}
      >
        <Box sx={{ width: '100%', height: '100%', display: 'flex', flexDirection: 'column' }}>
          {/* Header */}
          <Box
            sx={{
              p: 2,
              bgcolor: 'primary.main',
              color: 'white',
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center',
            }}
          >
            <Typography variant="h6" sx={{ fontWeight: 700 }}>
              {t('notifications', 'Bildirimler')}
            </Typography>
            <Box sx={{ display: 'flex', gap: 1, alignItems: 'center' }}>
              {unreadCount > 0 && (
                <Button
                  size="small"
                  onClick={handleMarkAllAsRead}
                  sx={{ color: 'white', fontSize: '0.75rem' }}
                  disabled={markAllAsReadMutation.isPending}
                >
                  {t('markAllRead', 'Tümünü Okundu İşaretle')}
                </Button>
              )}
              <IconButton size="small" onClick={handleCloseDrawer} sx={{ color: 'white' }}>
                <Close />
              </IconButton>
            </Box>
          </Box>

          <Divider />

          {/* Content */}
          <Box sx={{ flexGrow: 1, overflow: 'auto' }}>
            {isLoading ? (
              <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', py: 4 }}>
                <CircularProgress />
              </Box>
            ) : notificationList.length === 0 ? (
              <Box sx={{ p: 4, textAlign: 'center' }}>
                <NotificationsIcon sx={{ fontSize: 48, color: 'text.disabled', mb: 2 }} />
                <Typography variant="body2" color="text.secondary">
                  {t('noNotifications', 'Bildirim yok')}
                </Typography>
              </Box>
            ) : (
              <List sx={{ p: 0 }}>
                <AnimatePresence>
                  {notificationList.map((notification, index) => (
                    <motion.div
                      key={notification.id}
                      initial={{ opacity: 0, x: 20 }}
                      animate={{ opacity: 1, x: 0 }}
                      exit={{ opacity: 0, x: -20 }}
                      transition={{ delay: index * 0.05 }}
                    >
                      <ListItem
                        button
                        onClick={() => handleNotificationClick(notification)}
                        sx={{
                          bgcolor: notification.read ? 'transparent' : 'action.selected',
                          borderLeft: notification.read ? 'none' : '4px solid',
                          borderColor: 'primary.main',
                          py: 1.5,
                          px: 2,
                          '&:hover': {
                            bgcolor: 'action.hover',
                          },
                        }}
                      >
                        <ListItemIcon>
                          {getNotificationIcon(notification.type, notification.priority)}
                        </ListItemIcon>
                        <ListItemText
                          primary={
                            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 0.5 }}>
                              <Typography variant="subtitle2" sx={{ fontWeight: notification.read ? 500 : 700 }}>
                                {notification.title || t('notification', 'Bildirim')}
                              </Typography>
                              {notification.priority && (
                                <Chip
                                  label={notification.priority}
                                  size="small"
                                  color={getNotificationColor(notification.priority)}
                                  sx={{ height: 20, fontSize: '0.65rem' }}
                                />
                              )}
                            </Box>
                          }
                          secondary={
                            <>
                              <Typography variant="body2" color="text.secondary">
                                {notification.message || notification.content}
                              </Typography>
                              {notification.createdAt && (
                                <Typography variant="caption" color="text.disabled" sx={{ display: 'block', mt: 0.5 }}>
                                  {new Date(notification.createdAt).toLocaleString('tr-TR', {
                                    day: 'numeric',
                                    month: 'short',
                                    hour: '2-digit',
                                    minute: '2-digit',
                                  })}
                                </Typography>
                              )}
                            </>
                          }
                        />
                      </ListItem>
                      <Divider />
                    </motion.div>
                  ))}
                </AnimatePresence>
              </List>
            )}
          </Box>

          {/* Footer */}
          {notificationList.length > 0 && (
            <>
              <Divider />
              <Box sx={{ p: 2, textAlign: 'center' }}>
                <Button
                  fullWidth
                  variant="outlined"
                  onClick={() => {
                    navigate('/notifications');
                    handleCloseDrawer();
                  }}
                >
                  {t('viewAll', 'Tümünü Gör')}
                </Button>
              </Box>
            </>
          )}
        </Box>
      </Drawer>
    </>
  );
};

export default NotificationDrawer;



