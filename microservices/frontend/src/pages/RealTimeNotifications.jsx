// src/pages/RealTimeNotifications.jsx
import React from 'react';
import {
  Container, Box, Typography, List, ListItem, ListItemText,
  Chip, Paper, Alert
} from '@mui/material';
import { useWebSocket } from '../hooks/useWebSocket';
import NotificationsIcon from '@mui/icons-material/Notifications';
import { useTranslation } from 'react-i18next';

const RealTimeNotifications = () => {
  const { t } = useTranslation();
  const { isConnected, messages } = useWebSocket();

  return (
    <Container maxWidth="md" sx={{ py: 4 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 4 }}>
        <NotificationsIcon sx={{ fontSize: 40, color: 'primary.main' }} />
        <Box>
          <Typography variant="h4">{t('realTimeNotifications', 'Gerçek Zamanlı Bildirimler')}</Typography>
          <Typography variant="body2" color="text.secondary">
            {t('websocketConnection', 'WebSocket bağlantısı')}: {isConnected ? t('connected', 'Bağlı') : t('disconnected', 'Bağlı Değil')}
          </Typography>
        </Box>
      </Box>

      {!isConnected && (
        <Alert severity="warning" sx={{ mb: 3 }}>
          {t('websocketConnectionFailed', 'WebSocket bağlantısı kurulamadı. Bildirimler alınamıyor.')}
        </Alert>
      )}

      <Paper>
        <List>
          {messages.length === 0 ? (
            <ListItem>
              <ListItemText
                primary={t('noNotificationsYet', 'Henüz bildirim yok')}
                secondary={t('notificationsWillAppearHere', 'Yeni bildirimler burada görünecek')}
              />
            </ListItem>
          ) : (
            messages.map((message, index) => (
              <ListItem key={index}>
                <ListItemText
                  primary={message.title || t('notification', 'Bildirim')}
                  secondary={message.message || JSON.stringify(message)}
                />
                {message.type && (
                  <Chip label={message.type} size="small" sx={{ ml: 2 }} />
                )}
              </ListItem>
            ))
          )}
        </List>
      </Paper>
    </Container>
  );
};

export default RealTimeNotifications;

