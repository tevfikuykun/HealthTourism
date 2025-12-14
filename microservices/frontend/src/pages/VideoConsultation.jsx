// src/pages/VideoConsultation.jsx
import React, { useState, useRef, useEffect } from 'react';
import { useQuery } from '@tanstack/react-query';
import {
  Container, Box, Typography, Button, Card, CardContent, Grid,
  TextField, Paper, Avatar, Chip, IconButton, Dialog, DialogTitle,
  DialogContent, DialogActions
} from '@mui/material';
import VideocamIcon from '@mui/icons-material/Videocam';
import MicIcon from '@mui/icons-material/Mic';
import MicOffIcon from '@mui/icons-material/MicOff';
import VideocamOffIcon from '@mui/icons-material/VideocamOff';
import CallEndIcon from '@mui/icons-material/CallEnd';
import ChatIcon from '@mui/icons-material/Chat';
import ScreenShareIcon from '@mui/icons-material/ScreenShare';
import PersonIcon from '@mui/icons-material/Person';
import LoadingState from '../components/LoadingState/LoadingState';
import ErrorState from '../components/ErrorState/ErrorState';
import { videoConsultationService, doctorService } from '../services/api';
import { useTranslation } from 'react-i18next';

const VideoConsultation = () => {
  const { t } = useTranslation();
  const [consultations, setConsultations] = useState([]);
  const [isInCall, setIsInCall] = useState(false);
  const [isMuted, setIsMuted] = useState(false);
  const [isVideoOff, setIsVideoOff] = useState(false);
  const [openSchedule, setOpenSchedule] = useState(false);
  const [selectedDoctor, setSelectedDoctor] = useState(null);
  const videoRef = useRef(null);
  const remoteVideoRef = useRef(null);

  const { data: doctors = [], isLoading, error } = useQuery({
    queryKey: ['doctors', 'available'],
    queryFn: () => doctorService.getAll({ available: true }),
  });

  const { data: sessions = [] } = useQuery({
    queryKey: ['videoSessions'],
    queryFn: () => videoConsultationService.getSessions(),
  });

  if (isLoading) {
    return <LoadingState message={t('loadingDoctors', 'Doktorlar yükleniyor...')} />;
  }

  if (error) {
    return <ErrorState message={t('errorLoadingDoctors', 'Doktorlar yüklenirken bir hata oluştu')} />;
  }

  const handleStartCall = (doctor) => {
    setSelectedDoctor(doctor);
    setIsInCall(true);
    // WebRTC bağlantısı burada başlatılacak
  };

  const handleEndCall = () => {
    setIsInCall(false);
    setSelectedDoctor(null);
  };

  const handleScheduleConsultation = () => {
    setOpenSchedule(true);
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4" gutterBottom>
        {t('videoConsultation', 'Video Konsültasyon')}
      </Typography>

      {!isInCall ? (
        <>
          <Box sx={{ mb: 4 }}>
            <Button
              variant="contained"
              startIcon={<VideocamIcon />}
              onClick={handleScheduleConsultation}
            >
              {t('scheduleNewConsultation', 'Yeni Konsültasyon Planla')}
            </Button>
          </Box>

          <Grid container spacing={3}>
            {doctors.map((doctor) => (
              <Grid item xs={12} sm={6} md={4} key={doctor.id}>
                <Card>
                  <CardContent>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 2 }}>
                      <Avatar sx={{ width: 56, height: 56 }}>
                        {doctor.avatar || <PersonIcon />}
                      </Avatar>
                      <Box>
                        <Typography variant="h6">{doctor.name}</Typography>
                        <Typography variant="body2" color="text.secondary">
                          {doctor.specialization}
                        </Typography>
                      </Box>
                    </Box>
                    <Button
                      fullWidth
                      variant="outlined"
                      startIcon={<VideocamIcon />}
                      onClick={() => handleStartCall(doctor)}
                    >
                      {t('startNow', 'Hemen Başlat')}
                    </Button>
                  </CardContent>
                </Card>
              </Grid>
            ))}
          </Grid>
        </>
      ) : (
        <Paper sx={{ p: 3, textAlign: 'center' }}>
          <Box sx={{ mb: 2 }}>
            <Typography variant="h6">
              {t('consultationWith', '{name} ile Görüşme', { name: selectedDoctor?.name })}
            </Typography>
          </Box>
          
          <Box sx={{ display: 'flex', gap: 2, justifyContent: 'center', mb: 3 }}>
            <Box sx={{ position: 'relative', width: 640, height: 480, bgcolor: 'black', borderRadius: 2 }}>
              <video
                ref={videoRef}
                autoPlay
                muted={isMuted}
                style={{ width: '100%', height: '100%', objectFit: 'cover' }}
              />
              {isVideoOff && (
                <Box
                  sx={{
                    position: 'absolute',
                    top: 0,
                    left: 0,
                    right: 0,
                    bottom: 0,
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    bgcolor: 'grey.800',
                  }}
                >
                  <Avatar sx={{ width: 100, height: 100 }}>
                    <PersonIcon sx={{ fontSize: 50 }} />
                  </Avatar>
                </Box>
              )}
            </Box>
          </Box>

          <Box sx={{ display: 'flex', gap: 2, justifyContent: 'center' }}>
            <IconButton
              color={isMuted ? 'error' : 'default'}
              onClick={() => setIsMuted(!isMuted)}
            >
              {isMuted ? <MicOffIcon /> : <MicIcon />}
            </IconButton>
            <IconButton
              color={isVideoOff ? 'error' : 'default'}
              onClick={() => setIsVideoOff(!isVideoOff)}
            >
              {isVideoOff ? <VideocamOffIcon /> : <VideocamIcon />}
            </IconButton>
            <IconButton color="error" onClick={handleEndCall}>
              <CallEndIcon />
            </IconButton>
            <IconButton>
              <ScreenShareIcon />
            </IconButton>
            <IconButton>
              <ChatIcon />
            </IconButton>
          </Box>
        </Paper>
      )}

      <Dialog open={openSchedule} onClose={() => setOpenSchedule(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Konsültasyon Planla</DialogTitle>
        <DialogContent>
          <TextField
            fullWidth
            label="Doktor Seç"
            select
            SelectProps={{ native: true }}
            sx={{ mb: 2, mt: 1 }}
          >
            <option value="">Doktor seçin</option>
            {doctors.map((doctor) => (
              <option key={doctor.id} value={doctor.id}>
                {doctor.name} - {doctor.specialization}
              </option>
            ))}
          </TextField>
          <TextField
            fullWidth
            label="Tarih"
            type="date"
            InputLabelProps={{ shrink: true }}
            sx={{ mb: 2 }}
          />
          <TextField
            fullWidth
            label="Saat"
            type="time"
            InputLabelProps={{ shrink: true }}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenSchedule(false)}>İptal</Button>
          <Button variant="contained" onClick={() => setOpenSchedule(false)}>
            Planla
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default VideoConsultation;

