// src/pages/GDPRDataExport.jsx
import React, { useState } from 'react';
import {
  Container, Box, Typography, Paper, Button, Alert, List, ListItem,
  ListItemText, ListItemIcon, Divider
} from '@mui/material';
import DownloadIcon from '@mui/icons-material/Download';
import SecurityIcon from '@mui/icons-material/Security';
import DeleteForeverIcon from '@mui/icons-material/DeleteForever';
import DataUsageIcon from '@mui/icons-material/DataUsage';
import { useTranslation } from 'react-i18next';

const GDPRDataExport = () => {
  const { t } = useTranslation();
  const [exportStatus, setExportStatus] = useState('idle');

  const handleExportData = async () => {
    setExportStatus('processing');
    // Veri dışa aktarma işlemi
    setTimeout(() => {
      setExportStatus('completed');
    }, 3000);
  };

  const handleDeleteAccount = () => {
    // Hesap silme işlemi
  };

  return (
    <Container maxWidth="md" sx={{ py: 4 }}>
      <Typography variant="h4" gutterBottom>
        {t('dataManagementGDPR', 'Veri Yönetimi (GDPR)')}
      </Typography>

      <Alert severity="info" sx={{ mb: 3 }}>
        {t('gdprRightsDescription', 'GDPR uyarınca, kişisel verilerinize erişim, dışa aktarma ve silme haklarınız bulunmaktadır.')}
      </Alert>

      <Paper sx={{ p: 3, mb: 3 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 3 }}>
          <DataUsageIcon sx={{ fontSize: 40, color: 'primary.main' }} />
          <Box>
            <Typography variant="h6">{t('dataExport', 'Veri Dışa Aktarma')}</Typography>
            <Typography variant="body2" color="text.secondary">
              {t('exportDataDescription', 'Tüm kişisel verilerinizi JSON formatında indirebilirsiniz')}
            </Typography>
          </Box>
        </Box>
        <List>
          <ListItem>
            <ListItemIcon>
              <DownloadIcon />
            </ListItemIcon>
            <ListItemText
              primary={t('personalInformation', 'Kişisel Bilgiler')}
              secondary={t('personalInfoDescription', 'Ad, e-posta, telefon, adres bilgileri')}
            />
          </ListItem>
          <ListItem>
            <ListItemIcon>
              <DownloadIcon />
            </ListItemIcon>
            <ListItemText
              primary={t('reservationHistory', 'Rezervasyon Geçmişi')}
              secondary={t('reservationHistoryDescription', 'Tüm rezervasyon kayıtlarınız')}
            />
          </ListItem>
          <ListItem>
            <ListItemIcon>
              <DownloadIcon />
            </ListItemIcon>
            <ListItemText
              primary={t('paymentHistory', 'Ödeme Geçmişi')}
              secondary={t('paymentHistoryDescription', 'Tüm ödeme işlemleriniz')}
            />
          </ListItem>
          <ListItem>
            <ListItemIcon>
              <DownloadIcon />
            </ListItemIcon>
            <ListItemText
              primary={t('healthRecords', 'Sağlık Kayıtları')}
              secondary={t('healthRecordsDescription', 'Tıbbi kayıtlarınız (izin verdiğiniz ölçüde)')}
            />
          </ListItem>
        </List>
        <Button
          variant="contained"
          fullWidth
          startIcon={<DownloadIcon />}
          onClick={handleExportData}
          disabled={exportStatus === 'processing'}
          sx={{ mt: 2 }}
        >
          {exportStatus === 'processing' ? t('processing') : t('exportData', 'Verileri Dışa Aktar')}
        </Button>
      </Paper>

      <Paper sx={{ p: 3 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 3 }}>
          <DeleteForeverIcon sx={{ fontSize: 40, color: 'error.main' }} />
          <Box>
            <Typography variant="h6">{t('deleteAccount', 'Hesap Silme')}</Typography>
            <Typography variant="body2" color="text.secondary">
              {t('deleteAccountDescription', 'Hesabınızı ve tüm verilerinizi kalıcı olarak silebilirsiniz')}
            </Typography>
          </Box>
        </Box>
        <Alert severity="warning" sx={{ mb: 2 }}>
          {t('deleteAccountWarning', 'Bu işlem geri alınamaz! Tüm verileriniz kalıcı olarak silinecektir.')}
        </Alert>
        <Button
          variant="contained"
          color="error"
          fullWidth
          startIcon={<DeleteForeverIcon />}
          onClick={handleDeleteAccount}
        >
          {t('permanentlyDeleteAccount', 'Hesabı Kalıcı Olarak Sil')}
        </Button>
      </Paper>
    </Container>
  );
};

export default GDPRDataExport;

