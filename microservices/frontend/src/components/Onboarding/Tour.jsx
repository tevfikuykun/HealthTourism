import React, { useState, useEffect } from 'react';
import { Box, Button, Paper, Typography, Stepper, Step, StepLabel } from '@mui/material';
import { useLocation } from 'react-router-dom';

/**
 * Onboarding Tour Component
 * 
 * Yeni kullanıcılar için tanıtım turu.
 */
export default function Tour() {
  const location = useLocation();
  const [completedSteps, setCompletedSteps] = useState([]);
  const [isActive, setIsActive] = useState(false);

  useEffect(() => {
    // İlk kez giriş yapan kullanıcılar için tur göster
    const hasSeenTour = localStorage.getItem('hasSeenTour');
    if (!hasSeenTour && location.pathname === '/') {
      setIsActive(true);
    }
  }, [location]);

  const steps = [
    'Hoş Geldiniz',
    'Hastaneleri Keşfedin',
    'Doktorları İnceleyin',
    'Rezervasyon Yapın',
  ];

  const handleComplete = () => {
    localStorage.setItem('hasSeenTour', 'true');
    setIsActive(false);
  };

  const handleSkip = () => {
    localStorage.setItem('hasSeenTour', 'true');
    setIsActive(false);
  };

  if (!isActive) return null;

  return (
    <Paper
      sx={{
        position: 'fixed',
        top: '50%',
        left: '50%',
        transform: 'translate(-50%, -50%)',
        p: 4,
        zIndex: 9999,
        maxWidth: 500,
        boxShadow: 10,
      }}
    >
      <Typography variant="h5" gutterBottom>
        Hoş Geldiniz!
      </Typography>
      <Typography variant="body1" color="text.secondary" sx={{ mb: 3 }}>
        Platformumuzu keşfetmenize yardımcı olalım.
      </Typography>

      <Stepper activeStep={0} alternativeLabel sx={{ mb: 3 }}>
        {steps.map((label) => (
          <Step key={label}>
            <StepLabel>{label}</StepLabel>
          </Step>
        ))}
      </Stepper>

      <Box sx={{ display: 'flex', gap: 2, justifyContent: 'flex-end' }}>
        <Button onClick={handleSkip}>Şimdilik Atla</Button>
        <Button variant="contained" onClick={handleComplete}>
          Başlayalım
        </Button>
      </Box>
    </Paper>
  );
}

