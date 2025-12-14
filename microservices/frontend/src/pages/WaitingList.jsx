// src/pages/WaitingList.jsx
import React, { useState } from 'react';
import {
  Container, Box, Typography, Card, CardContent, Grid, Button,
  Chip, Table, TableBody, TableCell, TableContainer, TableHead,
  TableRow, Paper, IconButton, Dialog, DialogTitle, DialogContent,
  DialogActions, TextField, Alert
} from '@mui/material';
import NotificationsActiveIcon from '@mui/icons-material/NotificationsActive';
import DeleteIcon from '@mui/icons-material/Delete';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import { useTranslation } from 'react-i18next';

const WaitingList = () => {
  const { t } = useTranslation();
  const [waitingItems, setWaitingItems] = useState([
    {
      id: 1,
      type: 'Randevu',
      doctor: 'Dr. Ahmet Yılmaz',
      hospital: 'Acıbadem Hastanesi',
      preferredDate: '2024-02-01',
      status: 'active',
      notified: false,
    },
    {
      id: 2,
      type: 'Paket',
      name: 'Kardiyoloji Paketi',
      preferredDate: '2024-02-15',
      status: 'active',
      notified: false,
    },
  ]);

  const [openAdd, setOpenAdd] = useState(false);

  const handleRemove = (id) => {
    setWaitingItems(waitingItems.filter(item => item.id !== id));
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
        <Typography variant="h4">{t('waitingList', 'Bekleme Listesi')}</Typography>
        <Button
          variant="contained"
          startIcon={<NotificationsActiveIcon />}
          onClick={() => setOpenAdd(true)}
        >
          {t('addNew', 'Yeni Ekle')}
        </Button>
      </Box>

      <Alert severity="info" sx={{ mb: 3 }}>
        {t('waitingListDescription', 'Bekleme listesine eklediğiniz öğeler için müsaitlik durumunda bildirim alacaksınız.')}
      </Alert>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>{t('type', 'Tip')}</TableCell>
              <TableCell>{t('details', 'Detay')}</TableCell>
              <TableCell>{t('preferredDate', 'Tercih Edilen Tarih')}</TableCell>
              <TableCell>{t('status')}</TableCell>
              <TableCell>{t('actions')}</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {waitingItems.map((item) => (
              <TableRow key={item.id}>
                <TableCell>
                  <Chip label={item.type} size="small" />
                </TableCell>
                <TableCell>
                  {item.doctor ? (
                    <>
                      <Typography variant="body2">{item.doctor}</Typography>
                      <Typography variant="caption" color="text.secondary">
                        {item.hospital}
                      </Typography>
                    </>
                  ) : (
                    <Typography variant="body2">{item.name}</Typography>
                  )}
                </TableCell>
                <TableCell>{item.preferredDate}</TableCell>
                <TableCell>
                  <Chip
                    label={item.status === 'active' ? t('active', 'Aktif') : t('inactive', 'Pasif')}
                    color={item.status === 'active' ? 'success' : 'default'}
                    size="small"
                  />
                </TableCell>
                <TableCell>
                  <IconButton
                    size="small"
                    color="error"
                    onClick={() => handleRemove(item.id)}
                  >
                    <DeleteIcon />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={openAdd} onClose={() => setOpenAdd(false)} maxWidth="sm" fullWidth>
        <DialogTitle>{t('addToWaitingList', 'Bekleme Listesine Ekle')}</DialogTitle>
        <DialogContent>
          <TextField
            fullWidth
            label={t('type', 'Tip')}
            select
            SelectProps={{ native: true }}
            margin="normal"
          >
            <option value="">{t('selectType', 'Tip seçin')}</option>
            <option value="appointment">{t('appointment', 'Randevu')}</option>
            <option value="package">{t('package', 'Paket')}</option>
            <option value="accommodation">{t('accommodation', 'Konaklama')}</option>
          </TextField>
          <TextField
            fullWidth
            label={t('preferredDate', 'Tercih Edilen Tarih')}
            type="date"
            InputLabelProps={{ shrink: true }}
            margin="normal"
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenAdd(false)}>{t('cancel')}</Button>
          <Button variant="contained" onClick={() => setOpenAdd(false)}>
            {t('add', 'Ekle')}
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default WaitingList;

