import React, { useState, useEffect } from 'react';
import {
  Box,
  Container,
  Typography,
  Stepper,
  Step,
  StepLabel,
  StepContent,
  Card,
  CardContent,
  Chip,
  Button,
  Alert,
  LinearProgress,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Divider,
} from '@mui/material';
import {
  FlightTakeoff,
  FlightLand,
  LocalTaxi,
  Hotel,
  LocalHospital,
  Medication,
  CheckCircle,
  Schedule,
  LocationOn,
  NotificationsActive,
  Refresh,
  Warning,
} from '@mui/icons-material';
import { useTranslation } from 'react-i18next';
import { useQuery } from '@tanstack/react-query';
import api from '../services/api';

/**
 * Journey Timeline - Yolculuk Takvimi
 * Hasta için tüm süreci gösteren dinamik zaman çizelgesi
 * IoT verileriyle senkronize, checklist özellikli
 */
const JourneyTimeline = () => {
  const { t } = useTranslation();
  const [activeStep, setActiveStep] = useState(0);
  const [completedSteps, setCompletedSteps] = useState(new Set());
  const [checklistItems, setChecklistItems] = useState({});
  const [openDetail, setOpenDetail] = useState(null);

  // Journey data fetch
  const { data: journeyData, isLoading, refetch } = useQuery({
    queryKey: ['journey-timeline'],
    queryFn: async () => {
      const response = await api.get('/api/reservations/current/journey');
      return response.data;
    },
    refetchInterval: 30000, // 30 saniyede bir güncelle
  });

  // IoT data sync
  const { data: iotData } = useQuery({
    queryKey: ['iot-monitoring', journeyData?.reservationId],
    queryFn: async () => {
      if (!journeyData?.reservationId) return null;
      const response = await api.get(`/api/iot-monitoring/user/${journeyData.userId}/recent?hours=24`);
      return response.data;
    },
    enabled: !!journeyData?.reservationId,
    refetchInterval: 10000, // 10 saniyede bir IoT verisi çek
  });

  useEffect(() => {
    if (journeyData) {
      // Calculate active step based on current time
      const now = new Date();
      const steps = journeyData.timeline || [];
      const currentStep = steps.findIndex(
        (step) => new Date(step.startTime) <= now && new Date(step.endTime) >= now
      );
      if (currentStep >= 0) {
        setActiveStep(currentStep);
      }

      // Update completed steps
      const completed = new Set();
      steps.forEach((step, index) => {
        if (new Date(step.endTime) < now) {
          completed.add(index);
        }
      });
      setCompletedSteps(completed);
    }
  }, [journeyData]);

  const steps = journeyData?.timeline || [
    {
      id: 'flight-departure',
      title: t('journey.flightDeparture'),
      description: t('journey.flightDepartureDesc'),
      startTime: '2024-06-10T08:00:00Z',
      endTime: '2024-06-10T10:00:00Z',
      type: 'flight',
      status: 'upcoming',
      checklist: [
        { id: 'passport', label: t('journey.checklist.passport'), completed: false },
        { id: 'visa', label: t('journey.checklist.visa'), completed: false },
        { id: 'tickets', label: t('journey.checklist.tickets'), completed: true },
      ],
    },
    {
      id: 'arrival-transfer',
      title: t('journey.arrivalTransfer'),
      description: t('journey.arrivalTransferDesc'),
      startTime: '2024-06-10T10:00:00Z',
      endTime: '2024-06-10T11:00:00Z',
      type: 'transfer',
      status: 'upcoming',
      checklist: [
        { id: 'meet-driver', label: t('journey.checklist.meetDriver'), completed: false },
        { id: 'luggage', label: t('journey.checklist.luggage'), completed: false },
      ],
    },
    {
      id: 'accommodation',
      title: t('journey.accommodation'),
      description: t('journey.accommodationDesc'),
      startTime: '2024-06-10T11:00:00Z',
      endTime: '2024-06-11T08:00:00Z',
      type: 'accommodation',
      status: 'upcoming',
      checklist: [
        { id: 'checkin', label: t('journey.checklist.checkin'), completed: false },
        { id: 'room', label: t('journey.checklist.room'), completed: false },
      ],
    },
    {
      id: 'surgery',
      title: t('journey.surgery'),
      description: t('journey.surgeryDesc'),
      startTime: '2024-06-11T10:00:00Z',
      endTime: '2024-06-11T14:00:00Z',
      type: 'surgery',
      status: 'upcoming',
      checklist: [
        { id: 'prep', label: t('journey.checklist.preparation'), completed: false },
        { id: 'fasting', label: t('journey.checklist.fasting'), completed: false },
      ],
    },
    {
      id: 'recovery',
      title: t('journey.recovery'),
      description: t('journey.recoveryDesc'),
      startTime: '2024-06-11T14:00:00Z',
      endTime: '2024-06-18T10:00:00Z',
      type: 'recovery',
      status: 'active',
      checklist: [
        { id: 'medication', label: t('journey.checklist.medication'), completed: false },
        { id: 'followup', label: t('journey.checklist.followup'), completed: false },
      ],
    },
  ];

  const getStepIcon = (type) => {
    const icons = {
      flight: <FlightTakeoff />,
      transfer: <LocalTaxi />,
      accommodation: <Hotel />,
      surgery: <LocalHospital />,
      recovery: <Medication />,
    };
    return icons[type] || <Schedule />;
  };

  const getStepColor = (status) => {
    const colors = {
      completed: 'success',
      active: 'primary',
      upcoming: 'default',
      delayed: 'error',
    };
    return colors[status] || 'default';
  };

  const toggleChecklistItem = (stepId, itemId) => {
    setChecklistItems((prev) => {
      const key = `${stepId}-${itemId}`;
      return {
        ...prev,
        [key]: !prev[key],
      };
    });
  };

  const getProgressPercentage = () => {
    if (steps.length === 0) return 0;
    return (completedSteps.size / steps.length) * 100;
  };

  if (isLoading) {
    return (
      <Box sx={{ p: 3 }}>
        <LinearProgress />
        <Typography sx={{ mt: 2 }}>{t('common.loading')}</Typography>
      </Box>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Box sx={{ mb: 4, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Typography variant="h4" component="h1" gutterBottom>
          {t('journey.title')}
        </Typography>
        <Box>
          <IconButton onClick={() => refetch()} color="primary">
            <Refresh />
          </IconButton>
        </Box>
      </Box>

      {/* Progress Bar */}
      <Card sx={{ mb: 4 }}>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            {t('journey.progress')}: {Math.round(getProgressPercentage())}%
          </Typography>
          <LinearProgress
            variant="determinate"
            value={getProgressPercentage()}
            sx={{ height: 10, borderRadius: 5 }}
          />
        </CardContent>
      </Card>

      {/* IoT Data Alert */}
      {iotData && iotData.length > 0 && (
        <Alert severity="info" sx={{ mb: 3 }}>
          <Typography variant="body2">
            {t('journey.iotSync')}: {iotData.length} {t('journey.recentReadings')}
          </Typography>
        </Alert>
      )}

      {/* Timeline Stepper */}
      <Card>
        <CardContent>
          <Stepper activeStep={activeStep} orientation="vertical">
            {steps.map((step, index) => (
              <Step key={step.id} completed={completedSteps.has(index)}>
                <StepLabel
                  StepIconComponent={() => (
                    <Box
                      sx={{
                        width: 40,
                        height: 40,
                        borderRadius: '50%',
                        bgcolor: completedSteps.has(index)
                          ? 'success.main'
                          : index === activeStep
                          ? 'primary.main'
                          : 'grey.300',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        color: 'white',
                      }}
                    >
                      {completedSteps.has(index) ? (
                        <CheckCircle />
                      ) : (
                        getStepIcon(step.type)
                      )}
                    </Box>
                  )}
                >
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                    <Typography variant="h6">{step.title}</Typography>
                    <Chip
                      label={t(`journey.status.${step.status}`)}
                      color={getStepColor(step.status)}
                      size="small"
                    />
                  </Box>
                </StepLabel>
                <StepContent>
                  <Typography variant="body2" color="text.secondary" paragraph>
                    {step.description}
                  </Typography>

                  <Box sx={{ mb: 2 }}>
                    <Typography variant="subtitle2" gutterBottom>
                      <Schedule sx={{ verticalAlign: 'middle', mr: 0.5 }} />
                      {new Date(step.startTime).toLocaleString()} -{' '}
                      {new Date(step.endTime).toLocaleString()}
                    </Typography>
                    {step.location && (
                      <Typography variant="subtitle2" color="text.secondary">
                        <LocationOn sx={{ verticalAlign: 'middle', mr: 0.5 }} />
                        {step.location}
                      </Typography>
                    )}
                  </Box>

                  {/* Checklist */}
                  {step.checklist && step.checklist.length > 0 && (
                    <Box sx={{ mb: 2 }}>
                      <Typography variant="subtitle2" gutterBottom>
                        {t('journey.checklist.title')}:
                      </Typography>
                      <List dense>
                        {step.checklist.map((item) => {
                          const isCompleted =
                            checklistItems[`${step.id}-${item.id}`] || item.completed;
                          return (
                            <ListItem
                              key={item.id}
                              button
                              onClick={() => toggleChecklistItem(step.id, item.id)}
                              sx={{
                                bgcolor: isCompleted ? 'success.light' : 'transparent',
                                borderRadius: 1,
                                mb: 0.5,
                              }}
                            >
                              <ListItemIcon>
                                {isCompleted ? (
                                  <CheckCircle color="success" />
                                ) : (
                                  <Box
                                    sx={{
                                      width: 24,
                                      height: 24,
                                      border: '2px solid',
                                      borderColor: 'grey.400',
                                      borderRadius: '50%',
                                    }}
                                  />
                                )}
                              </ListItemIcon>
                              <ListItemText primary={item.label} />
                            </ListItem>
                          );
                        })}
                      </List>
                    </Box>
                  )}

                  <Button
                    variant="outlined"
                    size="small"
                    onClick={() => setOpenDetail(step)}
                    sx={{ mt: 1 }}
                  >
                    {t('common.viewDetails')}
                  </Button>
                </StepContent>
              </Step>
            ))}
          </Stepper>
        </CardContent>
      </Card>

      {/* Detail Dialog */}
      <Dialog open={!!openDetail} onClose={() => setOpenDetail(null)} maxWidth="md" fullWidth>
        {openDetail && (
          <>
            <DialogTitle>{openDetail.title}</DialogTitle>
            <DialogContent>
              <Typography paragraph>{openDetail.description}</Typography>
              <Divider sx={{ my: 2 }} />
              <Typography variant="subtitle2" gutterBottom>
                {t('journey.details')}
              </Typography>
              <List>
                <ListItem>
                  <ListItemIcon>
                    <Schedule />
                  </ListItemIcon>
                  <ListItemText
                    primary={t('journey.startTime')}
                    secondary={new Date(openDetail.startTime).toLocaleString()}
                  />
                </ListItem>
                <ListItem>
                  <ListItemIcon>
                    <Schedule />
                  </ListItemIcon>
                  <ListItemText
                    primary={t('journey.endTime')}
                    secondary={new Date(openDetail.endTime).toLocaleString()}
                  />
                </ListItem>
                {openDetail.location && (
                  <ListItem>
                    <ListItemIcon>
                      <LocationOn />
                    </ListItemIcon>
                    <ListItemText primary={t('journey.location')} secondary={openDetail.location} />
                  </ListItem>
                )}
              </List>
            </DialogContent>
            <DialogActions>
              <Button onClick={() => setOpenDetail(null)}>{t('common.close')}</Button>
            </DialogActions>
          </>
        )}
      </Dialog>
    </Container>
  );
};

export default JourneyTimeline;



