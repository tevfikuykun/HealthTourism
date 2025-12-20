import React, { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Button,
  IconButton,
  Collapse,
  Chip,
  Alert,
  LinearProgress,
} from '@mui/material';
import {
  SmartToy,
  Close,
  ExpandMore,
  ExpandLess,
  NotificationsActive,
  Schedule,
  CheckCircle,
  Warning,
} from '@mui/icons-material';
import { useTranslation } from 'react-i18next';
import { useQuery } from '@tanstack/react-query';
import api from '../../services/api';

/**
 * Smart Concierge - Akıllı Asistan
 * Hastayı karşılayan ve o anki durumuna göre içerik değiştiren bileşen
 */
const SmartConcierge = () => {
  const { t } = useTranslation();
  const [expanded, setExpanded] = useState(true);
  const [dismissed, setDismissed] = useState(false);

  // Fetch current journey status
  const { data: journeyStatus } = useQuery({
    queryKey: ['concierge-status'],
    queryFn: async () => {
      const response = await api.get('/reservations/current/status');
      return response.data;
    },
    refetchInterval: 30000,
  });

  // Fetch IoT data for personalized messages
  const { data: iotData } = useQuery({
    queryKey: ['concierge-iot'],
    queryFn: async () => {
      const response = await api.get('/iot-monitoring/user/me/latest');
      return response.data;
    },
    refetchInterval: 10000,
  });

  const getConciergeMessage = () => {
    if (!journeyStatus) return null;

    const now = new Date();
    const nextEvent = journeyStatus.nextEvent;

    if (!nextEvent) {
      // Post-surgery recovery
      if (iotData) {
        const heartRate = iotData.heartRate;
        if (heartRate > 100) {
          return {
            type: 'warning',
            title: t('concierge.highHeartRate'),
            message: t('concierge.highHeartRateDesc'),
            action: t('concierge.contactDoctor'),
            icon: <Warning />,
          };
        } else if (heartRate < 60) {
          return {
            type: 'info',
            title: t('concierge.normalVitals'),
            message: t('concierge.normalVitalsDesc'),
            icon: <CheckCircle />,
          };
        }
      }
      return {
        type: 'success',
        title: t('concierge.recovery'),
        message: t('concierge.recoveryDesc'),
        icon: <CheckCircle />,
      };
    }

    const timeUntilEvent = new Date(nextEvent.startTime) - now;
    const hoursUntil = timeUntilEvent / (1000 * 60 * 60);

    if (hoursUntil < 0.5) {
      // Less than 30 minutes
      return {
        type: 'warning',
        title: t('concierge.upcomingEvent'),
        message: t('concierge.upcomingEventDesc', {
          event: nextEvent.title,
          minutes: Math.round(hoursUntil * 60),
        }),
        action: t('concierge.prepare'),
        icon: <NotificationsActive />,
      };
    } else if (hoursUntil < 2) {
      // Less than 2 hours
      return {
        type: 'info',
        title: t('concierge.eventSoon'),
        message: t('concierge.eventSoonDesc', {
          event: nextEvent.title,
          hours: Math.round(hoursUntil),
        }),
        icon: <Schedule />,
      };
    }

    return {
      type: 'info',
      title: t('concierge.nextEvent'),
      message: t('concierge.nextEventDesc', {
        event: nextEvent.title,
        time: new Date(nextEvent.startTime).toLocaleTimeString(),
      }),
      icon: <Schedule />,
    };
  };

  const message = getConciergeMessage();

  if (dismissed || !message) return null;

  return (
    <Card
      sx={{
        position: 'fixed',
        bottom: 16,
        right: 16,
        maxWidth: 400,
        zIndex: 1000,
        boxShadow: 6,
      }}
    >
      <CardContent>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start', mb: 1 }}>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            <SmartToy color="primary" />
            <Typography variant="h6">{t('concierge.title')}</Typography>
          </Box>
          <Box>
            <IconButton
              size="small"
              onClick={() => setExpanded(!expanded)}
            >
              {expanded ? <ExpandLess /> : <ExpandMore />}
            </IconButton>
            <IconButton
              size="small"
              onClick={() => setDismissed(true)}
            >
              <Close />
            </IconButton>
          </Box>
        </Box>

        <Collapse in={expanded}>
          <Alert
            severity={message.type}
            icon={message.icon}
            sx={{ mb: 2 }}
          >
            <Typography variant="subtitle2" gutterBottom>
              {message.title}
            </Typography>
            <Typography variant="body2">
              {message.message}
            </Typography>
          </Alert>

          {message.action && (
            <Button
              variant="contained"
              color="primary"
              fullWidth
              onClick={() => {
                // Navigate to relevant page
                window.location.href = '/journey-timeline';
              }}
            >
              {message.action}
            </Button>
          )}

          {journeyStatus?.progress && (
            <Box sx={{ mt: 2 }}>
              <Typography variant="caption" color="text.secondary">
                {t('concierge.progress')}: {Math.round(journeyStatus.progress)}%
              </Typography>
              <LinearProgress
                variant="determinate"
                value={journeyStatus.progress}
                sx={{ mt: 0.5 }}
              />
            </Box>
          )}
        </Collapse>
      </CardContent>
    </Card>
  );
};

export default SmartConcierge;



