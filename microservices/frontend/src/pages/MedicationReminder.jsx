// src/pages/MedicationReminder.jsx
import React, { useState } from 'react';
import {
  Container, Box, Typography, Card, CardContent, Grid, Button,
  List, ListItem, ListItemText, ListItemSecondaryAction, Switch,
  IconButton, Dialog, DialogTitle, DialogContent, DialogActions,
  TextField, Chip
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import DeleteIcon from '@mui/icons-material/Delete';
import NotificationsIcon from '@mui/icons-material/Notifications';
import MedicationIcon from '@mui/icons-material/Medication';
import { useTranslation } from 'react-i18next';

const MedicationReminder = () => {
  const { t } = useTranslation();
  const [openAdd, setOpenAdd] = useState(false);
  const [medications, setMedications] = useState([
    {
      id: 1,
      name: 'Aspirin',
      dosage: '100mg',
      frequency: 'Günde 1 kez',
      time: '08:00',
      enabled: true,
    },
  ]);

  const handleToggle = (id) => {
    setMedications(medications.map(med =>
      med.id === id ? { ...med, enabled: !med.enabled } : med
    ));
  };

  return (
    <Container maxWidth="md" sx={{ py: 4 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
        <Typography variant="h4">{t('medicationReminders', 'İlaç Hatırlatıcıları')}</Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => setOpenAdd(true)}
        >
          {t('addNewMedication', 'Yeni İlaç Ekle')}
        </Button>
      </Box>

      <Grid container spacing={2} sx={{ mb: 3 }}>
        <Grid item xs={12} sm={6}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                <NotificationsIcon sx={{ fontSize: 40, color: 'primary.main' }} />
                <Box>
                  <Typography variant="h5">
                    {medications.filter(m => m.enabled).length}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    {t('activeReminders', 'Aktif Hatırlatıcı')}
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                <MedicationIcon sx={{ fontSize: 40, color: 'success.main' }} />
                <Box>
                  <Typography variant="h5">{medications.length}</Typography>
                  <Typography variant="body2" color="text.secondary">
                    {t('totalMedications', 'Toplam İlaç')}
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      <Card>
        <CardContent>
          <List>
            {medications.map((medication) => (
              <ListItem key={medication.id}>
                <ListItemText
                  primary={
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                      <Typography variant="h6">{medication.name}</Typography>
                      <Chip label={medication.dosage} size="small" />
                      {!medication.enabled && (
                        <Chip label={t('inactive', 'Pasif')} size="small" color="default" />
                      )}
                    </Box>
                  }
                  secondary={
                    <>
                      <Typography variant="body2">{medication.frequency}</Typography>
                      <Typography variant="body2" color="text.secondary">
                        {t('time', 'Saat')}: {medication.time}
                      </Typography>
                    </>
                  }
                />
                <ListItemSecondaryAction>
                  <Switch
                    edge="end"
                    checked={medication.enabled}
                    onChange={() => handleToggle(medication.id)}
                  />
                  <IconButton edge="end" onClick={() => {}}>
                    <DeleteIcon />
                  </IconButton>
                </ListItemSecondaryAction>
              </ListItem>
            ))}
          </List>
        </CardContent>
      </Card>

      <Dialog open={openAdd} onClose={() => setOpenAdd(false)} maxWidth="sm" fullWidth>
        <DialogTitle>{t('addNewMedication', 'Yeni İlaç Ekle')}</DialogTitle>
        <DialogContent>
          <TextField
            fullWidth
            label={t('medicationName', 'İlaç Adı')}
            margin="normal"
          />
          <TextField
            fullWidth
            label={t('dosage', 'Dozaj')}
            margin="normal"
            placeholder={t('dosagePlaceholder', 'örn: 100mg')}
          />
          <TextField
            fullWidth
            label={t('frequency', 'Sıklık')}
            select
            SelectProps={{ native: true }}
            margin="normal"
          >
            <option value="">{t('selectFrequency', 'Sıklık seçin')}</option>
            <option value="once">{t('onceDaily', 'Günde 1 kez')}</option>
            <option value="twice">{t('twiceDaily', 'Günde 2 kez')}</option>
            <option value="thrice">{t('thriceDaily', 'Günde 3 kez')}</option>
            <option value="custom">{t('custom', 'Özel')}</option>
          </TextField>
          <TextField
            fullWidth
            label={t('time', 'Saat')}
            type="time"
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

export default MedicationReminder;

