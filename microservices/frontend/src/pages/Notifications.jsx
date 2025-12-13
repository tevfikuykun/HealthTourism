import React from 'react';
import {
  Container,
  Box,
  Typography,
  List,
  ListItem,
  ListItemText,
  IconButton,
  Card,
  CardContent,
  Chip,
  Button,
} from '@mui/material';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { notificationService } from '../services/api';
import ProtectedRoute from '../components/ProtectedRoute';
import Loading from '../components/Loading';
import DeleteIcon from '@mui/icons-material/Delete';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';

export default function Notifications() {
  const queryClient = useQueryClient();

  const { data: notifications, isLoading } = useQuery({
    queryKey: ['notifications'],
    queryFn: () => notificationService.getByUser(null), // TODO: User ID
  });

  const markAsReadMutation = useMutation({
    mutationFn: (id) => notificationService.markAsRead(id),
    onSuccess: () => {
      queryClient.invalidateQueries(['notifications']);
    },
  });

  const markAllAsReadMutation = useMutation({
    mutationFn: () => notificationService.markAllAsRead(null), // TODO: User ID
    onSuccess: () => {
      queryClient.invalidateQueries(['notifications']);
    },
  });

  if (isLoading) {
    return <Loading message="Bildirimler yükleniyor..." />;
  }

  const unreadCount = notifications?.data?.filter((n) => !n.isRead)?.length || 0;

  return (
    <ProtectedRoute>
      <Container maxWidth="md" sx={{ py: 4 }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
          <Typography variant="h4">Bildirimlerim</Typography>
          {unreadCount > 0 && (
            <Button
              variant="outlined"
              startIcon={<CheckCircleIcon />}
              onClick={() => markAllAsReadMutation.mutate()}
            >
              Tümünü Okundu İşaretle
            </Button>
          )}
        </Box>

        {notifications?.data?.length === 0 ? (
          <Card>
            <CardContent>
              <Typography variant="body1" color="text.secondary" align="center" sx={{ py: 4 }}>
                Bildirim bulunmamaktadır.
              </Typography>
            </CardContent>
          </Card>
        ) : (
          <List>
            {notifications?.data?.map((notification) => (
              <Card key={notification.id} sx={{ mb: 2 }}>
                <CardContent>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                    <Box sx={{ flex: 1 }}>
                      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
                        <Typography variant="h6">{notification.title}</Typography>
                        {!notification.isRead && <Chip label="Yeni" color="primary" size="small" />}
                      </Box>
                      <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                        {notification.message}
                      </Typography>
                      <Typography variant="caption" color="text.secondary">
                        {new Date(notification.createdAt).toLocaleString('tr-TR')}
                      </Typography>
                    </Box>
                    {!notification.isRead && (
                      <IconButton
                        size="small"
                        onClick={() => markAsReadMutation.mutate(notification.id)}
                      >
                        <CheckCircleIcon />
                      </IconButton>
                    )}
                  </Box>
                </CardContent>
              </Card>
            ))}
          </List>
        )}
      </Container>
    </ProtectedRoute>
  );
}

