import React, { useState, useRef, useEffect } from 'react';
import {
  Box,
  Container,
  Typography,
  Card,
  CardContent,
  Button,
  IconButton,
  Grid,
  Paper,
  TextField,
  List,
  ListItem,
  ListItemText,
  Chip,
  Alert,
} from '@mui/material';
import {
  Videocam,
  VideocamOff,
  Mic,
  MicOff,
  CallEnd,
  Chat,
  Translate,
  Settings,
  Fullscreen,
} from '@mui/icons-material';
import { useTranslation } from 'react-i18next';
import { useParams } from 'react-router-dom';

/**
 * Tele-Consultation Interface
 * WebRTC tabanlı görüntülü görüşme arayüzü
 * Live Translation entegrasyonlu
 */
const TeleConsultation = () => {
  const { t } = useTranslation();
  const { patientId } = useParams();
  const [isConnected, setIsConnected] = useState(false);
  const [isVideoEnabled, setIsVideoEnabled] = useState(true);
  const [isAudioEnabled, setIsAudioEnabled] = useState(true);
  const [isTranslating, setIsTranslating] = useState(false);
  const [messages, setMessages] = useState([]);
  const [translationText, setTranslationText] = useState('');

  const localVideoRef = useRef(null);
  const remoteVideoRef = useRef(null);
  const peerConnectionRef = useRef(null);
  const wsRef = useRef(null);

  useEffect(() => {
    // Initialize WebRTC connection
    return () => {
      if (peerConnectionRef.current) {
        peerConnectionRef.current.close();
      }
      if (wsRef.current) {
        wsRef.current.close();
      }
    };
  }, []);

  const handleStartCall = async () => {
    try {
      // Initialize WebRTC
      const pc = new RTCPeerConnection({
        iceServers: [{ urls: 'stun:stun.l.google.com:19302' }],
      });

      // Get local stream
      const stream = await navigator.mediaDevices.getUserMedia({
        video: true,
        audio: true,
      });

      if (localVideoRef.current) {
        localVideoRef.current.srcObject = stream;
      }

      // Add tracks to peer connection
      stream.getTracks().forEach((track) => {
        pc.addTrack(track, stream);
      });

      // Handle remote stream
      pc.ontrack = (event) => {
        if (remoteVideoRef.current) {
          remoteVideoRef.current.srcObject = event.streams[0];
        }
      };

      peerConnectionRef.current = pc;
      setIsConnected(true);

      // Connect to signaling server (WebSocket)
      // wsRef.current = new WebSocket('ws://localhost:8080/api/tele-consultation/signaling');
    } catch (error) {
      console.error('Failed to start call:', error);
    }
  };

  const handleEndCall = () => {
    if (peerConnectionRef.current) {
      peerConnectionRef.current.close();
      peerConnectionRef.current = null;
    }
    if (localVideoRef.current?.srcObject) {
      localVideoRef.current.srcObject.getTracks().forEach((track) => track.stop());
    }
    setIsConnected(false);
  };

  const toggleVideo = () => {
    if (localVideoRef.current?.srcObject) {
      const videoTrack = localVideoRef.current.srcObject
        .getVideoTracks()[0];
      videoTrack.enabled = !isVideoEnabled;
      setIsVideoEnabled(!isVideoEnabled);
    }
  };

  const toggleAudio = () => {
    if (localVideoRef.current?.srcObject) {
      const audioTrack = localVideoRef.current.srcObject
        .getAudioTracks()[0];
      audioTrack.enabled = !isAudioEnabled;
      setIsAudioEnabled(!isAudioEnabled);
    }
  };

  const handleTranslate = async (text) => {
    if (!text.trim()) return;

    setIsTranslating(true);
    try {
      // Call translation API
      const response = await fetch('/api/translation/live', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ text, targetLanguage: 'en' }),
      });
      const data = await response.json();
      setTranslationText(data.translatedText);
    } catch (error) {
      console.error('Translation failed:', error);
    } finally {
      setIsTranslating(false);
    }
  };

  return (
    <Container maxWidth="xl" sx={{ py: 4 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        {t('teleconsultation.title')}
      </Typography>

      {!isConnected ? (
        <Card>
          <CardContent sx={{ textAlign: 'center', py: 8 }}>
            <Typography variant="h6" gutterBottom>
              {t('teleconsultation.readyToStart')}
            </Typography>
            <Button
              variant="contained"
              color="primary"
              size="large"
              startIcon={<Videocam />}
              onClick={handleStartCall}
              sx={{ mt: 3 }}
            >
              {t('teleconsultation.startCall')}
            </Button>
          </CardContent>
        </Card>
      ) : (
        <Grid container spacing={3}>
          {/* Video Area */}
          <Grid item xs={12} md={8}>
            <Paper sx={{ position: 'relative', bgcolor: 'black', borderRadius: 2, overflow: 'hidden' }}>
              <Box
                sx={{
                  position: 'relative',
                  width: '100%',
                  paddingTop: '56.25%', // 16:9 aspect ratio
                }}
              >
                {/* Remote Video */}
                <Box
                  component="video"
                  ref={remoteVideoRef}
                  autoPlay
                  playsInline
                  sx={{
                    position: 'absolute',
                    top: 0,
                    left: 0,
                    width: '100%',
                    height: '100%',
                    objectFit: 'cover',
                  }}
                />

                {/* Local Video (Picture-in-Picture) */}
                <Box
                  component="video"
                  ref={localVideoRef}
                  autoPlay
                  playsInline
                  muted
                  sx={{
                    position: 'absolute',
                    bottom: 16,
                    right: 16,
                    width: '25%',
                    borderRadius: 2,
                    border: '2px solid white',
                  }}
                />

                {/* Controls Overlay */}
                <Box
                  sx={{
                    position: 'absolute',
                    bottom: 0,
                    left: 0,
                    right: 0,
                    bgcolor: 'rgba(0,0,0,0.7)',
                    p: 2,
                    display: 'flex',
                    justifyContent: 'center',
                    gap: 2,
                  }}
                >
                  <IconButton
                    onClick={toggleVideo}
                    color={isVideoEnabled ? 'primary' : 'error'}
                    sx={{ bgcolor: 'rgba(255,255,255,0.2)' }}
                  >
                    {isVideoEnabled ? <Videocam /> : <VideocamOff />}
                  </IconButton>
                  <IconButton
                    onClick={toggleAudio}
                    color={isAudioEnabled ? 'primary' : 'error'}
                    sx={{ bgcolor: 'rgba(255,255,255,0.2)' }}
                  >
                    {isAudioEnabled ? <Mic /> : <MicOff />}
                  </IconButton>
                  <IconButton
                    onClick={() => setIsTranslating(!isTranslating)}
                    color={isTranslating ? 'primary' : 'default'}
                    sx={{ bgcolor: 'rgba(255,255,255,0.2)' }}
                  >
                    <Translate />
                  </IconButton>
                  <IconButton
                    sx={{ bgcolor: 'rgba(255,255,255,0.2)' }}
                  >
                    <Settings />
                  </IconButton>
                  <IconButton
                    onClick={handleEndCall}
                    color="error"
                    sx={{ bgcolor: 'rgba(255,255,255,0.2)' }}
                  >
                    <CallEnd />
                  </IconButton>
                </Box>
              </Box>
            </Paper>
          </Grid>

          {/* Sidebar */}
          <Grid item xs={12} md={4}>
            {/* Translation Panel */}
            {isTranslating && (
              <Card sx={{ mb: 2 }}>
                <CardContent>
                  <Typography variant="h6" gutterBottom>
                    <Translate sx={{ verticalAlign: 'middle', mr: 1 }} />
                    {t('teleconsultation.liveTranslation')}
                  </Typography>
                  <TextField
                    fullWidth
                    multiline
                    rows={3}
                    placeholder={t('teleconsultation.enterText')}
                    onChange={(e) => handleTranslate(e.target.value)}
                    sx={{ mb: 2 }}
                  />
                  {translationText && (
                    <Alert severity="info">
                      <Typography variant="body2">{translationText}</Typography>
                    </Alert>
                  )}
                </CardContent>
              </Card>
            )}

            {/* Chat */}
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  <Chat sx={{ verticalAlign: 'middle', mr: 1 }} />
                  {t('teleconsultation.chat')}
                </Typography>
                <List sx={{ maxHeight: 300, overflow: 'auto' }}>
                  {messages.map((msg, idx) => (
                    <ListItem key={idx}>
                      <ListItemText
                        primary={msg.text}
                        secondary={new Date(msg.timestamp).toLocaleTimeString()}
                      />
                    </ListItem>
                  ))}
                </List>
                <TextField
                  fullWidth
                  placeholder={t('teleconsultation.typeMessage')}
                  size="small"
                  sx={{ mt: 2 }}
                />
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      )}
    </Container>
  );
};

export default TeleConsultation;


