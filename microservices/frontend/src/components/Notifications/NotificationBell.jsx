import React, { useState } from 'react';
import { IconButton, Badge, Menu, MenuItem, Typography, Box, Divider, Button } from '@mui/material';
import NotificationsIcon from '@mui/icons-material/Notifications';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { notificationService } from '../../services/api';

export default function NotificationBell() {
  const [anchorEl, setAnchorEl] = useState(null);
  const queryClient = useQueryClient();

  const { data: notifications } = useQuery({
    queryKey: ['notifications'],
    queryFn: () => notificationService.getByUser(null), // TODO: User ID
    refetchInterval: 30000, // 30 saniyede bir kontrol et
  });

  const unreadCount = notifications?.data?.filter((n) => !n.isRead)?.length || 0;

  const handleClick = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const markAsReadMutation = useMutation({
    mutationFn: (id) => notificationService.markAsRead(id),
    onSuccess: () => {
      queryClient.invalidateQueries(['notifications']);
    },
  });

  const markAllAsReadMutation = useMutation({
    mutationFn: () => {
      const userStr = localStorage.getItem('user');
      const user = userStr ? JSON.parse(userStr) : null;
      return notificationService.markAllAsRead(user?.id);
    },
    onSuccess: () => {
      queryClient.invalidateQueries(['notifications']);
    },
  });

  return (
    <>
      <IconButton 
        onClick={handleClick}
        sx={{ 
          color: 'text.primary',
          '&:hover': {
            backgroundColor: 'action.hover'
          }
        }}
      >
        <Badge badgeContent={unreadCount} color="error">
          <NotificationsIcon />
        </Badge>
      </IconButton>
      <Menu
        anchorEl={anchorEl}
        open={Boolean(anchorEl)}
        onClose={handleClose}
        PaperProps={{
          sx: { width: 360, maxHeight: 500 },
        }}
      >
        <Box sx={{ p: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Typography variant="h6">Bildirimler</Typography>
          {unreadCount > 0 && (
            <Button size="small" onClick={() => markAllAsReadMutation.mutate()}>
              Tümünü Okundu İşaretle
            </Button>
          )}
        </Box>
        <Divider />
        <Box sx={{ maxHeight: 400, overflow: 'auto' }}>
          {notifications?.data?.length === 0 ? (
            <MenuItem disabled>
              <Typography variant="body2" color="text.secondary">
                Bildirim bulunmamaktadır.
              </Typography>
            </MenuItem>
          ) : (
            notifications?.data?.map((notification) => (
              <MenuItem
                key={notification.id}
                onClick={() => {
                  if (!notification.isRead) {
                    markAsReadMutation.mutate(notification.id);
                  }
                  handleClose();
                }}
                sx={{
                  backgroundColor: notification.isRead ? 'transparent' : 'action.selected',
                }}
              >
                <Box sx={{ width: '100%' }}>
                  <Typography variant="body2" fontWeight={notification.isRead ? 'normal' : 'bold'}>
                    {notification.title}
                  </Typography>
                  <Typography variant="caption" color="text.secondary">
                    {notification.message}
                  </Typography>
                  <Typography variant="caption" color="text.secondary" display="block" sx={{ mt: 0.5 }}>
                    {new Date(notification.createdAt).toLocaleDateString('tr-TR')}
                  </Typography>
                </Box>
              </MenuItem>
            ))
          )}
        </Box>
      </Menu>
    </>
  );
}

