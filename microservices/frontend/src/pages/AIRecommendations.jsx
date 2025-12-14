// src/pages/AIRecommendations.jsx
import React, { useState } from 'react';
import {
  Container, Box, Typography, Grid, Card, CardContent, Button,
  Chip, Avatar, Rating, Paper, Alert
} from '@mui/material';
import PsychologyIcon from '@mui/icons-material/Psychology';
import LocalHospitalIcon from '@mui/icons-material/LocalHospital';
import PersonIcon from '@mui/icons-material/Person';
import StarIcon from '@mui/icons-material/Star';
import { useTranslation } from 'react-i18next';

const AIRecommendations = () => {
  const { t } = useTranslation();
  const [recommendations, setRecommendations] = useState([
    {
      id: 1,
      type: 'hospital',
      name: 'Acıbadem Hastanesi',
      reason: 'Geçmiş rezervasyonlarınıza göre',
      match: 95,
      image: null,
    },
    {
      id: 2,
      type: 'doctor',
      name: 'Dr. Ahmet Yılmaz',
      reason: 'Uzmanlık alanınıza uygun',
      match: 88,
      image: null,
    },
    {
      id: 3,
      type: 'package',
      name: 'Kardiyoloji Paketi',
      reason: 'Bütçenize ve ihtiyaçlarınıza uygun',
      match: 92,
      image: null,
    },
  ]);

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 4 }}>
        <PsychologyIcon sx={{ fontSize: 40, color: 'primary.main' }} />
        <Box>
          <Typography variant="h4">{t('aiRecommendations', 'AI Önerileri')}</Typography>
          <Typography variant="body2" color="text.secondary">
            {t('personalizedRecommendations', 'Size özel öneriler')}
          </Typography>
        </Box>
      </Box>

      <Alert severity="info" sx={{ mb: 3 }}>
        {t('recommendationsDescription', 'Öneriler, geçmiş rezervasyonlarınız, tercihleriniz ve benzer kullanıcıların deneyimlerine göre oluşturulmuştur.')}
      </Alert>

      <Grid container spacing={3}>
        {recommendations.map((item) => (
          <Grid item xs={12} sm={6} md={4} key={item.id}>
            <Card>
              <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 2 }}>
                  <Avatar sx={{ bgcolor: 'primary.main' }}>
                    {item.type === 'hospital' ? <LocalHospitalIcon /> : <PersonIcon />}
                  </Avatar>
                  <Box sx={{ flexGrow: 1 }}>
                    <Typography variant="h6">{item.name}</Typography>
                    <Chip
                      label={`%${item.match} ${t('match', 'Eşleşme')}`}
                      color="success"
                      size="small"
                    />
                  </Box>
                </Box>
                <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                  {item.reason}
                </Typography>
                <Button variant="outlined" fullWidth>
                  {t('viewDetails', 'Detayları Gör')}
                </Button>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>

      <Paper sx={{ p: 3, mt: 4 }}>
        <Typography variant="h6" gutterBottom>
          {t('recommendationPreferences', 'Öneri Tercihleri')}
        </Typography>
        <Typography variant="body2" color="text.secondary">
          {t('personalizeRecommendationsDesc', 'Önerilerinizi kişiselleştirmek için tercihlerinizi güncelleyebilirsiniz.')}
        </Typography>
        <Button variant="outlined" sx={{ mt: 2 }}>
          {t('editPreferences', 'Tercihleri Düzenle')}
        </Button>
      </Paper>
    </Container>
  );
};

export default AIRecommendations;

