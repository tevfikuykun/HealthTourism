import React, { useState } from 'react';
import {
  Popover,
  Box,
  Typography,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  IconButton,
  Badge,
  Chip,
  Divider,
  Button,
  Alert,
  LinearProgress,
} from '@mui/material';
import {
  Notifications,
  Close,
  Warning,
  Info,
  CheckCircle,
  SmartToy,
  ArrowForward,
} from '@mui/icons-material';
import { useTranslation } from '../../i18n';
import { useNavigate } from 'react-router-dom';

/**
 * AI-Driven Notification Center
 * AI servisinden gelen kritik uyarıları gösterir
 */
const AINotificationCenter = ({ notifications, unreadCount, anchorEl, onClose, onOpen }) => {
  const { t } = useTranslation();
  const navigate = useNavigate();

  const criticalNotifications = notifications?.filter((n) => n.priority === 'CRITICAL') || [];
  const highPriorityNotifications = notifications?.filter((n) => n.priority === 'HIGH') || [];
  const normalNotifications = notifications?.filter((n) => !['CRITICAL', 'HIGH'].includes(n.priority)) || [];

  const getNotificationIcon = (priority) => {
    switch (priority) {
      case 'CRITICAL':
        return <Warning color="error" />;
      case 'HIGH':
        return <Warning color="warning" />;
      case 'INFO':
        return <Info color="info" />;
      default:
        return <CheckCircle color="success" />;
    }
  };

  const handleNotificationClick = (notification) => {
    // Navigate based on notification type
    if (notification.type === 'RISK_SCORE') {
      navigate('/patient-risk-scoring');
    } else if (notification.type === 'AI_INSIGHT') {
      navigate('/ai-health-companion');
    } else if (notification.type === 'EMERGENCY') {
      navigate('/emergency-help');
    }
    onClose();
  };

  return (
    <>
      <IconButton onClick={onOpen} size="small" sx={{ position: 'relative' }}>
        <Badge badgeContent={unreadCount} color="error" max={99}>
          <Notifications />
        </Badge>
      </IconButton>

      <Popover
        open={Boolean(anchorEl)}
        anchorEl={anchorEl}
        onClose={onClose}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
        transformOrigin={{ vertical: 'top', horizontal: 'right' }}
        PaperProps={{
          sx: {
            width: 420,
            maxHeight: 600,
            mt: 1,
          },
        }}
      >
        <Box sx={{ p: 2, borderBottom: 1, borderColor: 'divider' }}>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
              <SmartToy color="primary" />
              <Typography variant="h6">{t('header.aiNotifications')}</Typography>
              {unreadCount > 0 && (
                <Chip label={unreadCount} size="small" color="error" />
              )}
            </Box>
            <IconButton size="small" onClick={onClose}>
              <Close />
            </IconButton>
          </Box>
        </Box>

        <Box sx={{ maxHeight: 500, overflow: 'auto' }}>
          {/* Critical Alerts */}
          {criticalNotifications.length > 0 && (
            <Box sx={{ p: 2 }}>
              <Typography variant="subtitle2" color="error" gutterBottom>
                {t('header.criticalAlerts')}
              </Typography>
              {criticalNotifications.map((notification) => (
                <Alert
                  key={notification.id}
                  severity="error"
                  icon={getNotificationIcon(notification.priority)}
                  sx={{ mb: 1, cursor: 'pointer' }}
                  onClick={() => handleNotificationClick(notification)}
                  action={
                    <IconButton size="small">
                      <ArrowForward />
                    </IconButton>
                  }
                >
                  <Typography variant="subtitle2">{notification.title}</Typography>
                  <Typography variant="body2">{notification.message}</Typography>
                </Alert>
              ))}
            </Box>
          )}

          {/* High Priority */}
          {highPriorityNotifications.length > 0 && (
            <>
              {criticalNotifications.length > 0 && <Divider />}
              <List>
                {highPriorityNotifications.map((notification) => (
                  <ListItem
                    key={notification.id}
                    button
                    onClick={() => handleNotificationClick(notification)}
                    sx={{
                      bgcolor: notification.read ? 'transparent' : 'action.selected',
                      borderLeft: '4px solid',
                      borderColor: 'warning.main',
                    }}
                  >
                    <ListItemIcon>{getNotificationIcon(notification.priority)}</ListItemIcon>
                    <ListItemText
                      primary={notification.title}
                      secondary={notification.message}
                    />
                  </ListItem>
                ))}
              </List>
            </>
          )}

          {/* Normal Notifications */}
          {normalNotifications.length > 0 && (
            <>
              {(criticalNotifications.length > 0 || highPriorityNotifications.length > 0) && (
                <Divider />
              )}
              <List>
                {normalNotifications.slice(0, 5).map((notification) => (
                  <ListItem
                    key={notification.id}
                    button
                    onClick={() => handleNotificationClick(notification)}
                    sx={{
                      bgcolor: notification.read ? 'transparent' : 'action.selected',
                    }}
                  >
                    <ListItemIcon>{getNotificationIcon(notification.priority)}</ListItemIcon>
                    <ListItemText
                      primary={notification.title}
                      secondary={notification.message}
                    />
                  </ListItem>
                ))}
              </List>
            </>
          )}

          {(!notifications || notifications.length === 0) && (
            <Box sx={{ p: 4, textAlign: 'center' }}>
              <Typography variant="body2" color="text.secondary">
                {t('header.noNotifications')}
              </Typography>
            </Box>
          )}
        </Box>

        <Divider />
        <Box sx={{ p: 1, textAlign: 'center' }}>
          <Button size="small" onClick={() => { navigate('/notifications'); onClose(); }}>
            {t('header.viewAll')}
          </Button>
        </Box>
      </Popover>
    </>
  );
};

export default AINotificationCenter;



