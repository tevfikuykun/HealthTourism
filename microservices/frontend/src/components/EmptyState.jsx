import React from 'react';
import { Box, Typography, Button } from '@mui/material';
import InboxIcon from '@mui/icons-material/Inbox';

/**
 * EmptyState Component
 * 
 * Boş durumları gösteren component.
 * 
 * @param {string} title - Başlık
 * @param {string} description - Açıklama
 * @param {string} actionLabel - Aksiyon butonu metni
 * @param {function} onAction - Aksiyon butonu tıklandığında çağrılacak fonksiyon
 * @param {React.ReactNode} icon - Özel ikon (opsiyonel)
 */
export default function EmptyState({
  title = 'Veri bulunamadı',
  description = 'Bu bölümde henüz veri bulunmamaktadır.',
  actionLabel,
  onAction,
  icon,
}) {
  return (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        py: 8,
        textAlign: 'center',
      }}
    >
      {icon || <InboxIcon sx={{ fontSize: 80, color: 'text.secondary', mb: 2 }} />}
      <Typography variant="h6" gutterBottom>
        {title}
      </Typography>
      <Typography variant="body2" color="text.secondary" sx={{ mb: 3, maxWidth: 400 }}>
        {description}
      </Typography>
      {actionLabel && onAction && (
        <Button variant="contained" onClick={onAction}>
          {actionLabel}
        </Button>
      )}
    </Box>
  );
}

