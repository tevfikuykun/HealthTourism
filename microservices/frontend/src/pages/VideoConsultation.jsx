// src/pages/VideoConsultation.jsx
import React, { useState, useRef, useEffect } from 'react';
import { useQuery, useMutation } from '@tanstack/react-query';
import {
  Container, Box, Typography, Button, Card, CardContent, Grid,
  TextField, Paper, Avatar, Chip, IconButton, Dialog, DialogTitle,
  DialogContent, DialogActions, Alert, CircularProgress
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
import { videoConsultationService, doctorService, telemedicineService } from '../services/api';
import { useTranslation } from '../i18n';
import { useAuth } from '../hooks/useAuth';
import { WebRTCManager } from '../utils/webrtc';
import { toast } from 'react-toastify';

const VideoConsultation = () => {
  const { t } = useTranslation();
  const { user } = useAuth();
  const [consultations, setConsultations] = useState([]);
  const [isInCall, setIsInCall] = useState(false);
  const [isMuted, setIsMuted] = useState(false);
  const [isVideoOff, setIsVideoOff] = useState(false);
  const [openSchedule, setOpenSchedule] = useState(false);
  const [selectedDoctor, setSelectedDoctor] = useState(null);
  const [roomId, setRoomId] = useState(null);
  const [connectionState, setConnectionState] = useState('disconnected');
  const [isConnecting, setIsConnecting] = useState(false);
  const videoRef = useRef(null);
  const remoteVideoRef = useRef(null);
  const webrtcManagerRef = useRef(null);

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

  // Initialize WebRTC connection
  const initializeWebRTC = async (roomId, isInitiator) => {
    try {
      setIsConnecting(true);
      
      const manager = new WebRTCManager(roomId, user?.id, '/api/telemedicine/webrtc');
      webrtcManagerRef.current = manager;

      // Set up event handlers
      manager.onRemoteStream = (stream) => {
        if (remoteVideoRef.current) {
          remoteVideoRef.current.srcObject = stream;
        }
      };

      manager.onConnectionStateChange = (state) => {
        setConnectionState(state);
        if (state === 'connected') {
          setIsConnecting(false);
          toast.success(t('teleconsultation.connected', 'Bağlantı kuruldu'));
        } else if (state === 'disconnected' || state === 'failed') {
          setIsConnecting(false);
          if (state === 'failed') {
            toast.error(t('teleconsultation.disconnected', 'Bağlantı kesildi'));
          }
        }
      };

      // Initialize peer connection
      await manager.initialize(isInitiator);

      // Get user media
      const localStream = await manager.getUserMedia();
      if (videoRef.current) {
        videoRef.current.srcObject = localStream;
      }

      // Create offer if initiator
      if (isInitiator) {
        await manager.createOffer();
      }

      setIsInCall(true);
    } catch (error) {
      console.error('WebRTC initialization error:', error);
      toast.error(error.message || t('teleconsultation.initFailed', 'WebRTC başlatılamadı'));
      setIsConnecting(false);
      setIsInCall(false);
    }
  };

  // Start consultation mutation
  const startConsultationMutation = useMutation({
    mutationFn: ({ patientId, doctorId }) => 
      telemedicineService.scheduleConsultation({
        patientId,
        doctorId,
        scheduledAt: new Date().toISOString(),
      }),
    onSuccess: (data) => {
      const consultation = data.data;
      if (consultation?.consultationRoomId) {
        setRoomId(consultation.consultationRoomId);
        initializeWebRTC(consultation.consultationRoomId, true);
      } else {
        toast.error(t('teleconsultation.noRoomId', 'Oda ID alınamadı'));
        setIsConnecting(false);
      }
    },
    onError: (error) => {
      toast.error(error.message || t('teleconsultation.startFailed', 'Görüşme başlatılamadı'));
      setIsConnecting(false);
    },
  });

  const handleStartCall = async (doctor) => {
    setSelectedDoctor(doctor);
    setIsConnecting(true);
    startConsultationMutation.mutate({
      patientId: user?.id,
      doctorId: doctor.id,
    });
  };

  const handleEndCall = async () => {
    try {
      if (webrtcManagerRef.current) {
        await webrtcManagerRef.current.close();
      }
      if (roomId) {
        await telemedicineService.endConsultation(roomId);
      }
    } catch (error) {
      console.error('Error ending call:', error);
    } finally {
      setIsInCall(false);
      setSelectedDoctor(null);
      setRoomId(null);
      setConnectionState('disconnected');
      setIsConnecting(false);
      setIsMuted(false);
      setIsVideoOff(false);
      
      // Stop all tracks
      if (videoRef.current?.srcObject) {
        videoRef.current.srcObject.getTracks().forEach(track => track.stop());
        videoRef.current.srcObject = null;
      }
      if (remoteVideoRef.current?.srcObject) {
        remoteVideoRef.current.srcObject.getTracks().forEach(track => track.stop());
        remoteVideoRef.current.srcObject = null;
      }
    }
  };

  const handleToggleMute = () => {
    if (webrtcManagerRef.current) {
      webrtcManagerRef.current.toggleMute();
      setIsMuted(!isMuted);
    }
  };

  const handleToggleVideo = () => {
    if (webrtcManagerRef.current) {
      webrtcManagerRef.current.toggleVideo();
      setIsVideoOff(!isVideoOff);
    }
  };

  // Cleanup on unmount
  useEffect(() => {
    return () => {
      if (webrtcManagerRef.current) {
        webrtcManagerRef.current.close();
      }
    };
  }, []);

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
          
          {isConnecting && (
            <Box sx={{ textAlign: 'center', py: 4 }}>
              <CircularProgress sx={{ mb: 2 }} />
              <Typography>{t('teleconsultation.connecting', 'Bağlanıyor...')}</Typography>
            </Box>
          )}

          {connectionState !== 'connected' && !isConnecting && isInCall && (
            <Alert severity="warning" sx={{ mb: 2 }}>
              {t('teleconsultation.connectionState', `Bağlantı durumu: ${connectionState}`)}
            </Alert>
          )}

          <Box sx={{ display: 'flex', gap: 2, justifyContent: 'center', mb: 3, flexWrap: 'wrap' }}>
            {/* Local Video */}
            <Box sx={{ position: 'relative', width: { xs: '100%', md: 640 }, height: 480, bgcolor: 'black', borderRadius: 2 }}>
              <video
                ref={videoRef}
                autoPlay
                muted
                playsInline
                style={{ width: '100%', height: '100%', objectFit: 'cover', borderRadius: 8 }}
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
                    borderRadius: 2,
                  }}
                >
                  <Avatar sx={{ width: 100, height: 100 }}>
                    <PersonIcon sx={{ fontSize: 50 }} />
                  </Avatar>
                </Box>
              )}
              <Chip
                label={t('teleconsultation.you', 'Siz')}
                size="small"
                sx={{ position: 'absolute', top: 8, left: 8 }}
              />
            </Box>

            {/* Remote Video */}
            <Box sx={{ position: 'relative', width: { xs: '100%', md: 640 }, height: 480, bgcolor: 'black', borderRadius: 2 }}>
              <video
                ref={remoteVideoRef}
                autoPlay
                playsInline
                style={{ width: '100%', height: '100%', objectFit: 'cover', borderRadius: 8 }}
              />
              {!remoteVideoRef.current?.srcObject && (
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
                    borderRadius: 2,
                  }}
                >
                  <Avatar sx={{ width: 100, height: 100 }}>
                    <PersonIcon sx={{ fontSize: 50 }} />
                  </Avatar>
                </Box>
              )}
              <Chip
                label={selectedDoctor?.name || t('teleconsultation.remote', 'Karşı Taraf')}
                size="small"
                sx={{ position: 'absolute', top: 8, left: 8 }}
              />
            </Box>
          </Box>

          <Box sx={{ display: 'flex', gap: 2, justifyContent: 'center', flexWrap: 'wrap' }}>
            <IconButton
              color={isMuted ? 'error' : 'default'}
              onClick={handleToggleMute}
              disabled={isConnecting}
              size="large"
              sx={{ bgcolor: 'background.paper' }}
            >
              {isMuted ? <MicOffIcon /> : <MicIcon />}
            </IconButton>
            <IconButton
              color={isVideoOff ? 'error' : 'default'}
              onClick={handleToggleVideo}
              disabled={isConnecting}
              size="large"
              sx={{ bgcolor: 'background.paper' }}
            >
              {isVideoOff ? <VideocamOffIcon /> : <VideocamIcon />}
            </IconButton>
            <IconButton
              color="error"
              onClick={handleEndCall}
              disabled={isConnecting}
              size="large"
              sx={{ bgcolor: 'error.main', color: 'white', '&:hover': { bgcolor: 'error.dark' } }}
            >
              <CallEndIcon />
            </IconButton>
            <IconButton
              disabled={isConnecting}
              size="large"
              sx={{ bgcolor: 'background.paper' }}
            >
              <ScreenShareIcon />
            </IconButton>
            <IconButton
              disabled={isConnecting}
              size="large"
              sx={{ bgcolor: 'background.paper' }}
            >
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

