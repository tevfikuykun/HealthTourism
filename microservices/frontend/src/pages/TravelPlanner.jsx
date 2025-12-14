// src/pages/TravelPlanner.jsx
import React, { useState } from 'react';
import {
  Container, Box, Typography, Stepper, Step, StepLabel, Button,
  Paper, Grid, Card, CardContent, TextField, Chip, Timeline,
  TimelineItem, TimelineSeparator, TimelineConnector, TimelineContent,
  TimelineDot, TimelineOppositeContent
} from '@mui/material';
import FlightTakeoffIcon from '@mui/icons-material/FlightTakeoff';
import HotelIcon from '@mui/icons-material/Hotel';
import LocalHospitalIcon from '@mui/icons-material/LocalHospital';
import DirectionsCarIcon from '@mui/icons-material/DirectionsCar';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import { useTranslation } from 'react-i18next';

const steps = ['Uçuş', 'Konaklama', 'Hastane', 'Transfer', 'Özet'];

const TravelPlanner = () => {
  const { t } = useTranslation();
  const [activeStep, setActiveStep] = useState(0);
  const [travelPlan, setTravelPlan] = useState({
    flight: null,
    accommodation: null,
    hospital: null,
    transfer: null,
  });

  const handleNext = () => {
    setActiveStep((prevActiveStep) => prevActiveStep + 1);
  };

  const handleBack = () => {
    setActiveStep((prevActiveStep) => prevActiveStep - 1);
  };

  const renderStepContent = (step) => {
    switch (step) {
      case 0:
        return (
          <Box>
            <Typography variant="h6" gutterBottom>Uçuş Seçimi</Typography>
            <Grid container spacing={2}>
              <Grid item xs={12} sm={6}>
                <TextField fullWidth label="Nereden" />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField fullWidth label="Nereye" />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField fullWidth label="Gidiş Tarihi" type="date" InputLabelProps={{ shrink: true }} />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField fullWidth label="Dönüş Tarihi" type="date" InputLabelProps={{ shrink: true }} />
              </Grid>
            </Grid>
          </Box>
        );
      case 1:
        return (
          <Box>
            <Typography variant="h6" gutterBottom>Konaklama Seçimi</Typography>
            <Grid container spacing={2}>
              <Grid item xs={12} sm={6}>
                <TextField fullWidth label="Giriş Tarihi" type="date" InputLabelProps={{ shrink: true }} />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField fullWidth label="Çıkış Tarihi" type="date" InputLabelProps={{ shrink: true }} />
              </Grid>
              <Grid item xs={12}>
                <TextField fullWidth label="Kişi Sayısı" type="number" />
              </Grid>
            </Grid>
          </Box>
        );
      case 2:
        return (
          <Box>
            <Typography variant="h6" gutterBottom>Hastane ve Doktor Seçimi</Typography>
            <Grid container spacing={2}>
              <Grid item xs={12}>
                <TextField fullWidth label="Hastane" select SelectProps={{ native: true }}>
                  <option value="">Hastane seçin</option>
                </TextField>
              </Grid>
              <Grid item xs={12}>
                <TextField fullWidth label="Doktor" select SelectProps={{ native: true }}>
                  <option value="">Doktor seçin</option>
                </TextField>
              </Grid>
            </Grid>
          </Box>
        );
      case 3:
        return (
          <Box>
            <Typography variant="h6" gutterBottom>Transfer Hizmetleri</Typography>
            <Grid container spacing={2}>
              <Grid item xs={12}>
                <TextField fullWidth label="Transfer Tipi" select SelectProps={{ native: true }}>
                  <option value="">Transfer tipi seçin</option>
                  <option value="airport-hospital">Havalimanı - Hastane</option>
                  <option value="airport-hotel">Havalimanı - Otel</option>
                  <option value="hotel-hospital">Otel - Hastane</option>
                </TextField>
              </Grid>
            </Grid>
          </Box>
        );
      case 4:
        return (
          <Box>
            <Typography variant="h6" gutterBottom>Rezervasyon Özeti</Typography>
            <Timeline>
              <TimelineItem>
                <TimelineOppositeContent>
                  <Typography variant="body2" color="text.secondary">
                    15 Ocak 2024
                  </Typography>
                </TimelineOppositeContent>
                <TimelineSeparator>
                  <TimelineDot color="primary">
                    <FlightTakeoffIcon />
                  </TimelineDot>
                  <TimelineConnector />
                </TimelineSeparator>
                <TimelineContent>
                  <Paper sx={{ p: 2 }}>
                    <Typography variant="h6">Uçuş</Typography>
                    <Typography variant="body2">İstanbul'a varış</Typography>
                  </Paper>
                </TimelineContent>
              </TimelineItem>
              <TimelineItem>
                <TimelineOppositeContent>
                  <Typography variant="body2" color="text.secondary">
                    15 Ocak 2024
                  </Typography>
                </TimelineOppositeContent>
                <TimelineSeparator>
                  <TimelineDot color="primary">
                    <DirectionsCarIcon />
                  </TimelineDot>
                  <TimelineConnector />
                </TimelineSeparator>
                <TimelineContent>
                  <Paper sx={{ p: 2 }}>
                    <Typography variant="h6">Transfer</Typography>
                    <Typography variant="body2">Havalimanı - Otel</Typography>
                  </Paper>
                </TimelineContent>
              </TimelineItem>
              <TimelineItem>
                <TimelineOppositeContent>
                  <Typography variant="body2" color="text.secondary">
                    16 Ocak 2024
                  </Typography>
                </TimelineOppositeContent>
                <TimelineSeparator>
                  <TimelineDot color="primary">
                    <LocalHospitalIcon />
                  </TimelineDot>
                  <TimelineConnector />
                </TimelineSeparator>
                <TimelineContent>
                  <Paper sx={{ p: 2 }}>
                    <Typography variant="h6">Hastane Ziyareti</Typography>
                    <Typography variant="body2">Konsültasyon</Typography>
                  </Paper>
                </TimelineContent>
              </TimelineItem>
            </Timeline>
          </Box>
        );
      default:
        return null;
    }
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4" gutterBottom>
        Seyahat Planlayıcı
      </Typography>

      <Paper sx={{ p: 3, mt: 3 }}>
        <Stepper activeStep={activeStep}>
          {steps.map((label) => (
            <Step key={label}>
              <StepLabel>{label}</StepLabel>
            </Step>
          ))}
        </Stepper>

        <Box sx={{ mt: 4, mb: 4 }}>
          {renderStepContent(activeStep)}
        </Box>

        <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
          <Button
            disabled={activeStep === 0}
            onClick={handleBack}
          >
            Geri
          </Button>
          <Button
            variant="contained"
            onClick={activeStep === steps.length - 1 ? () => {} : handleNext}
          >
            {activeStep === steps.length - 1 ? 'Rezervasyon Yap' : 'İleri'}
          </Button>
        </Box>
      </Paper>
    </Container>
  );
};

export default TravelPlanner;

