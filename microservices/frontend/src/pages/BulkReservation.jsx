// src/pages/BulkReservation.jsx
import React, { useState } from 'react';
import {
  Container, Box, Typography, Stepper, Step, StepLabel, Button,
  Paper, Grid, TextField, Card, CardContent, IconButton, Chip
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import DeleteIcon from '@mui/icons-material/Delete';
import PersonAddIcon from '@mui/icons-material/PersonAdd';
import { useTranslation } from 'react-i18next';

const BulkReservation = () => {
  const { t } = useTranslation();
  const [activeStep, setActiveStep] = useState(0);
  const [members, setMembers] = useState([
    { id: 1, name: '', email: '', phone: '', relation: 'self' },
  ]);

  const steps = [t('familyMembers', 'Aile Üyeleri'), t('serviceSelection', 'Hizmet Seçimi'), t('dateSelection', 'Tarih Seçimi'), t('summary', 'Özet')];

  const handleAddMember = () => {
    setMembers([...members, {
      id: members.length + 1,
      name: '',
      email: '',
      phone: '',
      relation: 'family',
    }]);
  };

  const handleRemoveMember = (id) => {
    if (members.length > 1) {
      setMembers(members.filter(m => m.id !== id));
    }
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4" gutterBottom>
        {t('bulkReservation', 'Toplu Rezervasyon')}
      </Typography>
      <Typography variant="body1" color="text.secondary" sx={{ mb: 4 }}>
        {t('bulkReservationDescription', 'Aileniz veya grubunuz için toplu rezervasyon yapın')}
      </Typography>

      <Paper sx={{ p: 3 }}>
        <Stepper activeStep={activeStep}>
          {steps.map((label) => (
            <Step key={label}>
              <StepLabel>{label}</StepLabel>
            </Step>
          ))}
        </Stepper>

        <Box sx={{ mt: 4 }}>
          {activeStep === 0 && (
            <Box>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
                <Typography variant="h6">{t('familyMembersGroupMembers', 'Aile Üyeleri / Grup Üyeleri')}</Typography>
                <Button
                  variant="outlined"
                  startIcon={<PersonAddIcon />}
                  onClick={handleAddMember}
                >
                  {t('addMember', 'Üye Ekle')}
                </Button>
              </Box>
              <Grid container spacing={2}>
                {members.map((member, index) => (
                  <Grid item xs={12} key={member.id}>
                    <Card>
                      <CardContent>
                        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start', mb: 2 }}>
                          <Chip
                            label={index === 0 ? t('mainUser', 'Ana Kullanıcı') : `${t('member', 'Üye')} ${index}`}
                            color={index === 0 ? 'primary' : 'default'}
                          />
                          {members.length > 1 && (
                            <IconButton
                              size="small"
                              color="error"
                              onClick={() => handleRemoveMember(member.id)}
                            >
                              <DeleteIcon />
                            </IconButton>
                          )}
                        </Box>
                        <Grid container spacing={2}>
                          <Grid item xs={12} sm={4}>
                            <TextField
                              fullWidth
                              label={t('fullName', 'Ad Soyad')}
                              value={member.name}
                              onChange={(e) => {
                                const newMembers = [...members];
                                newMembers[index].name = e.target.value;
                                setMembers(newMembers);
                              }}
                            />
                          </Grid>
                          <Grid item xs={12} sm={4}>
                            <TextField
                              fullWidth
                              label={t('email', 'E-posta')}
                              type="email"
                              value={member.email}
                              onChange={(e) => {
                                const newMembers = [...members];
                                newMembers[index].email = e.target.value;
                                setMembers(newMembers);
                              }}
                            />
                          </Grid>
                          <Grid item xs={12} sm={4}>
                            <TextField
                              fullWidth
                              label={t('phone', 'Telefon')}
                              value={member.phone}
                              onChange={(e) => {
                                const newMembers = [...members];
                                newMembers[index].phone = e.target.value;
                                setMembers(newMembers);
                              }}
                            />
                          </Grid>
                        </Grid>
                      </CardContent>
                    </Card>
                  </Grid>
                ))}
              </Grid>
            </Box>
          )}

          {activeStep === 1 && (
            <Box>
              <Typography variant="h6" gutterBottom>{t('serviceSelection', 'Hizmet Seçimi')}</Typography>
              <Grid container spacing={2}>
                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    label={t('serviceType', 'Hizmet Tipi')}
                    select
                    SelectProps={{ native: true }}
                  >
                    <option value="">{t('selectService', 'Hizmet seçin')}</option>
                    <option value="consultation">{t('consultation', 'Konsültasyon')}</option>
                    <option value="surgery">{t('surgery', 'Ameliyat')}</option>
                    <option value="test">{t('test', 'Test')}</option>
                    <option value="package">{t('package', 'Paket')}</option>
                  </TextField>
                </Grid>
              </Grid>
            </Box>
          )}

          {activeStep === 2 && (
            <Box>
              <Typography variant="h6" gutterBottom>{t('dateSelection', 'Tarih Seçimi')}</Typography>
              <Grid container spacing={2}>
                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    label={t('startDate', 'Başlangıç Tarihi')}
                    type="date"
                    InputLabelProps={{ shrink: true }}
                  />
                </Grid>
                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    label={t('endDate', 'Bitiş Tarihi')}
                    type="date"
                    InputLabelProps={{ shrink: true }}
                  />
                </Grid>
              </Grid>
            </Box>
          )}

          {activeStep === 3 && (
            <Box>
              <Typography variant="h6" gutterBottom>{t('reservationSummary', 'Rezervasyon Özeti')}</Typography>
              <Typography variant="body1">
                {t('reservationForMembers', '{count} üye için rezervasyon yapılacak', { count: members.length })}
              </Typography>
            </Box>
          )}
        </Box>

        <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 4 }}>
          <Button
            disabled={activeStep === 0}
            onClick={() => setActiveStep(activeStep - 1)}
          >
            {t('back', 'Geri')}
          </Button>
          <Button
            variant="contained"
            onClick={() => {
              if (activeStep < steps.length - 1) {
                setActiveStep(activeStep + 1);
              } else {
                // Rezervasyon işlemi
              }
            }}
          >
            {activeStep === steps.length - 1 ? t('makeReservation', 'Rezervasyon Yap') : t('next', 'İleri')}
          </Button>
        </Box>
      </Paper>
    </Container>
  );
};

export default BulkReservation;

