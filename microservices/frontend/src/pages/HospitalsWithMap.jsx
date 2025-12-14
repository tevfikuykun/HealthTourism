// src/pages/HospitalsWithMap.jsx
import React, { useState } from 'react';
import {
  Container, Box, Typography, Grid, Card, CardContent, Button
} from '@mui/material';
import HospitalMap from '../components/Map/HospitalMap';
import { useTranslation } from 'react-i18next';

const HospitalsWithMap = () => {
  const { t } = useTranslation();
  const [hospitals] = useState([
    {
      id: 1,
      name: 'Acıbadem Hastanesi',
      latitude: 41.0082,
      longitude: 28.9784,
    },
    {
      id: 2,
      name: 'Memorial Hastanesi',
      latitude: 41.0123,
      longitude: 28.9856,
    },
  ]);

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4" gutterBottom>
        {t('hospitalsMapView', 'Hastaneler - Harita Görünümü')}
      </Typography>

      <Grid container spacing={3}>
        <Grid item xs={12} md={8}>
          <HospitalMap hospitals={hospitals} />
        </Grid>
        <Grid item xs={12} md={4}>
          <Box>
            {hospitals.map((hospital) => (
              <Card key={hospital.id} sx={{ mb: 2 }}>
                <CardContent>
                  <Typography variant="h6">{hospital.name}</Typography>
                  <Button variant="outlined" size="small" sx={{ mt: 1 }}>
                    {t('details', 'Detaylar')}
                  </Button>
                </CardContent>
              </Card>
            ))}
          </Box>
        </Grid>
      </Grid>
    </Container>
  );
};

export default HospitalsWithMap;

