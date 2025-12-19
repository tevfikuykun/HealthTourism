import React, { useState, useEffect } from 'react';
import {
  Box, Container, Typography, Card, CardContent, Chip,
  LinearProgress, Alert, Button, IconButton, Tooltip,
  Dialog, DialogTitle, DialogContent, DialogActions,
} from '@mui/material';
import {
  Refresh, CheckCircle, Schedule, LocationOn, NotificationsActive,
  FlightTakeoff, FlightLand, LocalTaxi, Hotel, LocalHospital, Medication,
  Info, Warning,
} from '@mui/icons-material';
import { CheckCircle2, Circle, Clock, MapPin, Activity, Calendar } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import { useTranslation } from '../i18n';
import { useQuery } from '@tanstack/react-query';
import { patientJourneyService, iotMonitoringService } from '../services/api';
import { useAuth } from '../hooks/useAuth';
import { fadeInUp, staggerContainer, staggerItem, hoverLift } from '../utils/ui-helpers';

/**
 * Patient Journey Timeline - Tedavi Yolculuğu
 * Gamified dikey timeline ile tedavi sürecini gösteren interaktif sayfa
 * Material-UI + Tailwind CSS + Framer Motion + Lucide-React entegrasyonu
 */
const PatientJourney = () => {
  const { t } = useTranslation();
  const { user } = useAuth();
  const [selectedStep, setSelectedStep] = useState(null);
  const [openDialog, setOpenDialog] = useState(false);

  // Patient Journey Data
  const { data: journeyData, isLoading, refetch } = useQuery({
    queryKey: ['patient-journey', user?.id],
    queryFn: async () => {
      const response = await patientJourneyService.getJourney(user?.id, 1);
      return response.data;
    },
    enabled: !!user?.id,
    refetchInterval: 30000,
  });

  // Journey Steps
  const { data: journeySteps } = useQuery({
    queryKey: ['patient-journey-steps', user?.id],
    queryFn: async () => {
      const response = await patientJourneyService.getJourneySteps(user?.id, 1);
      return response.data;
    },
    enabled: !!user?.id,
  });

  // Current Step
  const { data: currentStep } = useQuery({
    queryKey: ['patient-journey-current-step', user?.id],
    queryFn: async () => {
      const response = await patientJourneyService.getCurrentStep(user?.id, 1);
      return response.data;
    },
    enabled: !!user?.id,
    refetchInterval: 10000,
  });

  // IoT Data for real-time updates
  const { data: iotData } = useQuery({
    queryKey: ['iot-monitoring', user?.id],
    queryFn: async () => {
      const response = await iotMonitoringService.getLatestByUser(user?.id);
      return response.data;
    },
    enabled: !!user?.id,
    refetchInterval: 5000,
  });

  // Default steps if API data is not available
  const steps = journeySteps || [
    {
      id: 1,
      title: t('journey.consultation', 'Konsültasyon'),
      status: 'completed',
      date: '12 Ara 2025',
      desc: t('journey.consultationDesc', 'AI Risk skoru hesaplandı ve onaylandı.'),
      type: 'preparation',
    },
    {
      id: 2,
      title: t('journey.visaAccommodation', 'Vize & Konaklama'),
      status: 'current',
      date: '19 Ara 2025',
      desc: t('journey.visaAccommodationDesc', 'Otel rezervasyonu ve transfer işlemleri sürüyor.'),
      type: 'accommodation',
    },
    {
      id: 3,
      title: t('journey.surgeryDay', 'Ameliyat Günü'),
      status: 'upcoming',
      date: '22 Ara 2025',
      desc: t('journey.surgeryDayDesc', 'X Hastanesi - Op. Dr. Uygun tarafından.'),
      type: 'surgery',
    },
    {
      id: 4,
      title: t('journey.recoveryProcess', 'İyileşme Süreci'),
      status: 'upcoming',
      date: '25 Ara 2025',
      desc: t('journey.recoveryProcessDesc', 'IoT cihazları ile takip başlayacak.'),
      type: 'recovery',
    },
  ];

  const getProgressPercentage = () => {
    if (steps.length === 0) return 0;
    const completed = steps.filter((s) => s.status === 'completed').length;
    const current = steps.filter((s) => s.status === 'current').length;
    return ((completed + current * 0.5) / steps.length) * 100;
  };

  const handleStepClick = (step) => {
    setSelectedStep(step);
    setOpenDialog(true);
  };

  const handleTakeAction = () => {
    // TODO: Implement action handler
    console.log('Taking action for current step...');
  };

  const handleViewDetails = (step) => {
    handleStepClick(step);
  };

  if (isLoading) {
    return (
      <Box className="w-full mt-8" sx={{ width: '100%', mt: 4 }}>
        <LinearProgress />
      </Box>
    );
  }

  return (
    <Box
      sx={{
        minHeight: '100vh',
        background: 'linear-gradient(135deg, #f8fafc 0%, #e0e7ff 100%)',
        position: 'relative',
        overflow: 'hidden',
        '&::before': {
          content: '""',
          position: 'absolute',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          background: 'radial-gradient(circle at 20% 50%, rgba(79, 70, 229, 0.1) 0%, transparent 50%), radial-gradient(circle at 80% 80%, rgba(16, 185, 129, 0.1) 0%, transparent 50%)',
          pointerEvents: 'none',
        },
      }}
    >
      <Container maxWidth="lg" sx={{ py: 6, px: 3, position: 'relative', zIndex: 1 }}>
        {/* Header: Gamified Style */}
        <motion.div
          variants={fadeInUp}
          initial="initial"
          animate="animate"
        >
          <Box sx={{ mb: 6, display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: 2 }}>
            <Box>
              <Typography 
                variant="h3" 
                sx={{ 
                  fontWeight: 900, 
                  letterSpacing: '-0.03em', 
                  background: 'linear-gradient(135deg, #4F46E5 0%, #7C3AED 100%)',
                  backgroundClip: 'text',
                  WebkitBackgroundClip: 'text',
                  WebkitTextFillColor: 'transparent',
                  mb: 1,
                }}
              >
                {t('journey.title', 'Tedavi Yolculuğun')}
              </Typography>
              <Typography 
                variant="body1" 
                sx={{ color: 'text.secondary', fontWeight: 500, letterSpacing: '-0.01em' }}
              >
                {t('journey.subtitle', 'Her adımda yanınızdayız. Süreci buradan anlık takip edebilirsiniz.')}
              </Typography>
            </Box>
            
            <Tooltip title={t('journey.refresh', 'Yenile')}>
              <IconButton 
                onClick={() => refetch()}
                sx={{ 
                  borderRadius: 3,
                  backdropFilter: 'blur(20px) saturate(180%)',
                  WebkitBackdropFilter: 'blur(20px) saturate(180%)',
                  backgroundColor: 'rgba(79, 70, 229, 0.15)',
                  border: '1px solid rgba(79, 70, 229, 0.3)',
                  color: 'primary.main',
                  '&:hover': {
                    backgroundColor: 'rgba(79, 70, 229, 0.25)',
                    boxShadow: '0 0 20px rgba(79, 70, 229, 0.4)',
                  },
                }}
              >
                <Refresh />
              </IconButton>
            </Tooltip>
          </Box>
        </motion.div>

      {/* Progress Card - Gamified with Glow */}
      <motion.div
        initial={{ opacity: 0, y: -10 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.1 }}
      >
        <Card 
          sx={{ 
            mb: 6, 
            borderRadius: 6, 
            backdropFilter: 'blur(20px) saturate(180%)',
            WebkitBackdropFilter: 'blur(20px) saturate(180%)',
            backgroundColor: 'rgba(255, 255, 255, 0.8)',
            border: '1px solid rgba(255, 255, 255, 0.3)',
            boxShadow: '0 8px 32px rgba(0, 0, 0, 0.1)',
            position: 'relative',
            overflow: 'hidden',
            '&::before': {
              content: '""',
              position: 'absolute',
              top: 0,
              left: 0,
              right: 0,
              height: '4px',
              background: `linear-gradient(90deg, #4F46E5 0%, #7C3AED 50%, #10B981 100%)`,
              backgroundSize: '200% 100%',
              animation: 'shimmer 3s ease-in-out infinite',
              '@keyframes shimmer': {
                '0%': { backgroundPosition: '200% 0' },
                '100%': { backgroundPosition: '-200% 0' },
              },
            },
          }}
        >
          <CardContent sx={{ p: 4 }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                <Box
                  sx={{
                    p: 1.5,
                    background: 'linear-gradient(135deg, rgba(79, 70, 229, 0.15) 0%, rgba(124, 58, 237, 0.1) 100%)',
                    borderRadius: 3,
                    border: '1px solid rgba(79, 70, 229, 0.2)',
                  }}
                >
                  <Activity size={24} style={{ color: '#4F46E5' }} strokeWidth={2.5} />
                </Box>
                <Typography variant="h6" sx={{ fontWeight: 800, letterSpacing: '-0.01em' }}>
                  {t('journey.progress', 'İlerleme')}
                </Typography>
              </Box>
              <Typography 
                variant="h4" 
                sx={{ 
                  fontWeight: 900, 
                  background: 'linear-gradient(135deg, #4F46E5 0%, #7C3AED 100%)',
                  backgroundClip: 'text',
                  WebkitBackgroundClip: 'text',
                  WebkitTextFillColor: 'transparent',
                  letterSpacing: '-0.02em',
                }}
              >
                {Math.round(getProgressPercentage())}%
              </Typography>
            </Box>
            <Box sx={{ position: 'relative', height: 16, borderRadius: 8, overflow: 'hidden', background: 'rgba(0, 0, 0, 0.05)' }}>
              <motion.div
                initial={{ width: 0 }}
                animate={{ width: `${getProgressPercentage()}%` }}
                transition={{ duration: 1, ease: 'easeOut' }}
                style={{
                  height: '100%',
                  background: 'linear-gradient(90deg, #4F46E5 0%, #7C3AED 50%, #10B981 100%)',
                  borderRadius: 8,
                  boxShadow: '0 0 20px rgba(79, 70, 229, 0.5)',
                }}
              />
              {getProgressPercentage() === 100 && (
                <motion.div
                  initial={{ opacity: 0, scale: 0 }}
                  animate={{ opacity: 1, scale: 1 }}
                  transition={{ delay: 0.5 }}
                  style={{
                    position: 'absolute',
                    top: '50%',
                    left: '50%',
                    transform: 'translate(-50%, -50%)',
                  }}
                >
                  <CheckCircle2 size={20} style={{ color: '#10B981' }} strokeWidth={2.5} />
                </motion.div>
              )}
            </Box>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 2 }}>
              <Typography variant="caption" sx={{ color: 'text.secondary', fontWeight: 600 }}>
                {steps.filter(s => s.status === 'completed').length} / {steps.length} {t('journey.completed', 'Tamamlandı')}
              </Typography>
              {getProgressPercentage() === 100 && (
                <motion.div
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  transition={{ delay: 0.7 }}
                >
                  <Chip 
                    label={t('journey.allCompleted', 'Tüm Adımlar Tamamlandı!')} 
                    sx={{ 
                      fontWeight: 800,
                      background: 'linear-gradient(135deg, rgba(16, 185, 129, 0.2) 0%, rgba(34, 197, 94, 0.15) 100%)',
                      border: '1px solid rgba(16, 185, 129, 0.3)',
                      color: 'success.dark',
                    }} 
                  />
                </motion.div>
              )}
            </Box>
          </CardContent>
        </Card>
      </motion.div>

      {/* IoT Real-time Alert */}
      {currentStep && currentStep.status === 'active' && iotData && (
        <motion.div
          initial={{ opacity: 0, y: -10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.15 }}
        >
          <Alert
            severity={iotData.heartRate > 100 ? 'warning' : 'info'}
            icon={<NotificationsActive />}
            className="mb-8 rounded-2xl"
            sx={{ mb: 4, borderRadius: 4 }}
          >
            <Typography variant="subtitle2" className="font-bold mb-1" sx={{ fontWeight: 700, mb: 0.5 }}>
              {t('journey.currentStep', 'Şu An')}: {currentStep.title || steps.find(s => s.status === 'current')?.title}
            </Typography>
            {iotData.heartRate > 100 && (
              <Typography variant="body2">
                {t('journey.highHeartRate', 'Yüksek nabız tespit edildi')}: <strong>{iotData.heartRate}</strong> bpm
              </Typography>
            )}
          </Alert>
        </motion.div>
      )}

      {/* Gamified Vertical Timeline */}
      <motion.div
        variants={staggerContainer}
        initial="hidden"
        animate="show"
      >
        <Box className="relative" sx={{ position: 'relative' }}>
          {/* Vertical Line */}
          <Box
            className="absolute left-4 top-0 bottom-0 w-0.5 bg-indigo-100"
            sx={{
              position: 'absolute',
              left: 16,
              top: 0,
              bottom: 0,
              width: 2,
              bgcolor: '#e0e7ff',
            }}
          />

          {/* Steps */}
          <Box className="space-y-12" sx={{ '& > *': { mb: 6 } }}>
            {steps.map((step, index) => (
              <motion.div
                key={step.id}
                variants={staggerItem}
                initial={{ opacity: 0, x: -20 }}
                whileInView={{ opacity: 1, x: 0 }}
                viewport={{ once: true }}
                transition={{ delay: index * 0.1 }}
                className="relative pl-10"
                sx={{ position: 'relative', pl: 5 }}
              >
                {/* Step Indicator */}
                <motion.div
                  className={`absolute left-[-11px] top-0 p-1 rounded-full ${
                    step.status === 'completed' 
                      ? 'bg-green-500 text-white shadow-lg' 
                      : step.status === 'current' 
                      ? 'bg-indigo-600 text-white shadow-xl' 
                      : 'bg-white border-2 border-indigo-100 text-indigo-200'
                  }`}
                  sx={{
                    position: 'absolute',
                    left: -11,
                    top: 0,
                    p: 0.5,
                    borderRadius: '50%',
                    bgcolor: step.status === 'completed' 
                      ? '#22c55e' 
                      : step.status === 'current' 
                      ? '#4f46e5' 
                      : 'white',
                    border: step.status === 'upcoming' ? '2px solid #e0e7ff' : 'none',
                    color: step.status === 'completed' || step.status === 'current' ? 'white' : '#c7d2fe',
                    boxShadow: step.status === 'completed' || step.status === 'current' 
                      ? '0 10px 15px -3px rgba(0, 0, 0, 0.1)' 
                      : 'none',
                  }}
                  animate={step.status === 'current' ? { scale: [1, 1.1, 1] } : {}}
                  transition={{ duration: 2, repeat: Infinity }}
                >
                  {step.status === 'completed' ? (
                    <CheckCircle2 size={16} strokeWidth={2.5} />
                  ) : (
                    <Circle size={16} strokeWidth={2.5} />
                  )}
                </motion.div>

                {/* Step Card */}
                <motion.div
                  className={`p-6 rounded-2xl border transition-all cursor-pointer ${
                    step.status === 'current' 
                      ? 'bg-white shadow-xl border-indigo-200 scale-105' 
                      : 'bg-slate-50 border-transparent hover:bg-white hover:shadow-md'
                  }`}
                  sx={{
                    p: 3,
                    borderRadius: 4,
                    border: step.status === 'current' ? '1px solid #c7d2fe' : '1px solid transparent',
                    bgcolor: step.status === 'current' ? 'white' : '#f8fafc',
                    boxShadow: step.status === 'current' 
                      ? '0 20px 25px -5px rgba(0, 0, 0, 0.1)' 
                      : 'none',
                    transform: step.status === 'current' ? 'scale(1.02)' : 'scale(1)',
                    cursor: 'pointer',
                    '&:hover': {
                      bgcolor: 'white',
                      boxShadow: '0 4px 12px rgba(0,0,0,0.1)',
                    },
                  }}
                  onClick={() => handleViewDetails(step)}
                  {...hoverLift}
                >
                  {/* Step Header */}
                  <Box className="flex justify-between items-start mb-2" sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 1 }}>
                    <Typography variant="h6" className="font-bold text-slate-800" sx={{ fontWeight: 700, color: '#1e293b' }}>
                      {step.title}
                    </Typography>
                    <Box className="flex items-center gap-1 text-sm font-medium text-slate-400" sx={{ display: 'flex', alignItems: 'center', gap: 0.5, fontSize: '0.875rem', fontWeight: 500, color: '#94a3b8' }}>
                      <Clock size={14} strokeWidth={2} />
                      <Typography variant="caption" sx={{ fontSize: '0.875rem' }}>
                        {step.date || (step.startTime ? new Date(step.startTime).toLocaleDateString('tr-TR', { day: 'numeric', month: 'short', year: 'numeric' }) : '')}
                      </Typography>
                    </Box>
                  </Box>

                  {/* Step Description */}
                  <Typography variant="body2" className="text-slate-600 leading-relaxed mb-4" sx={{ color: '#475569', mb: 2, lineHeight: 1.7 }}>
                    {step.desc || step.description}
                  </Typography>

                  {/* Status Chip */}
                  <Box className="mb-4" sx={{ mb: 2 }}>
                    <Chip
                      label={
                        step.status === 'completed' 
                          ? t('journey.completed', 'Tamamlandı')
                          : step.status === 'current' 
                          ? t('journey.current', 'Devam Ediyor')
                          : t('journey.upcoming', 'Yaklaşan')
                      }
                      size="small"
                      color={
                        step.status === 'completed' 
                          ? 'success' 
                          : step.status === 'current' 
                          ? 'primary' 
                          : 'default'
                      }
                      className="font-bold"
                      sx={{ fontWeight: 700 }}
                    />
                  </Box>

                  {/* Action Buttons for Current Step */}
                  {step.status === 'current' && (
                    <motion.div
                      initial={{ opacity: 0, y: 10 }}
                      animate={{ opacity: 1, y: 0 }}
                      transition={{ delay: 0.2 }}
                      className="flex gap-2 mt-4"
                      sx={{ display: 'flex', gap: 1, mt: 2 }}
                    >
                      <Button
                        variant="contained"
                        size="small"
                        onClick={(e) => {
                          e.stopPropagation();
                          handleTakeAction();
                        }}
                        className="text-xs bg-indigo-600 text-white px-4 py-2 rounded-lg font-bold hover:bg-indigo-700"
                        sx={{
                          fontSize: '0.75rem',
                          bgcolor: '#4f46e5',
                          color: 'white',
                          px: 2,
                          py: 1,
                          borderRadius: 2,
                          fontWeight: 700,
                          textTransform: 'none',
                          '&:hover': { bgcolor: '#4338ca' }
                        }}
                      >
                        {t('journey.takeAction', 'Harekete Geç')}
                      </Button>
                      <Button
                        variant="outlined"
                        size="small"
                        onClick={(e) => {
                          e.stopPropagation();
                          handleViewDetails(step);
                        }}
                        className="text-xs border border-indigo-100 text-indigo-600 px-4 py-2 rounded-lg font-bold hover:bg-indigo-50"
                        sx={{
                          fontSize: '0.75rem',
                          borderColor: '#e0e7ff',
                          color: '#4f46e5',
                          px: 2,
                          py: 1,
                          borderRadius: 2,
                          fontWeight: 700,
                          textTransform: 'none',
                          '&:hover': { bgcolor: '#eef2ff', borderColor: '#c7d2fe' }
                        }}
                      >
                        {t('journey.details', 'Detaylar')}
                      </Button>
                    </motion.div>
                  )}

                  {/* Checklist for Completed Steps */}
                  {step.status === 'completed' && step.checklist && step.checklist.length > 0 && (
                    <Box className="mt-4 pt-4 border-t border-gray-200" sx={{ mt: 2, pt: 2, borderTop: '1px solid #e2e8f0' }}>
                      <Typography variant="caption" className="text-xs font-semibold text-gray-600 uppercase mb-2 block" sx={{ fontSize: '0.65rem', fontWeight: 600, mb: 1, display: 'block' }}>
                        {t('journey.checklist', 'Kontrol Listesi')}
                      </Typography>
                      <Box className="flex flex-wrap gap-2" sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                        {step.checklist.map((item) => (
                          <Chip
                            key={item.id}
                            icon={item.completed ? <CheckCircle fontSize="small" /> : <Schedule fontSize="small" />}
                            label={item.label}
                            size="small"
                            color={item.completed ? 'success' : 'default'}
                            className="font-semibold"
                            sx={{ fontWeight: 600 }}
                          />
                        ))}
                      </Box>
                    </Box>
                  )}

                  {/* IoT Alert */}
                  {step.iotAlert && (
                    <Alert severity="warning" className="mt-4 rounded-xl" sx={{ mt: 2, borderRadius: 3 }}>
                      {t('journey.iotAlert', 'IoT verilerinde anomali tespit edildi')}
                    </Alert>
                  )}
                </motion.div>
              </motion.div>
            ))}
          </Box>
        </Box>
      </motion.div>

      {/* Step Detail Dialog */}
      <Dialog 
        open={openDialog} 
        onClose={() => setOpenDialog(false)} 
        maxWidth="md" 
        fullWidth
        PaperProps={{ 
          className: 'rounded-3xl',
          sx: { borderRadius: 6 }
        }}
      >
        <DialogTitle className="font-bold" sx={{ fontWeight: 700 }}>
          {selectedStep?.title}
        </DialogTitle>
        <DialogContent>
          {selectedStep && (
            <Box className="space-y-4" sx={{ '& > *': { mb: 2 } }}>
              <Box>
                <Typography variant="body2" color="text.secondary" className="font-semibold mb-1" sx={{ fontWeight: 600, mb: 0.5 }}>
                  {t('journey.description', 'Açıklama')}
                </Typography>
                <Typography variant="body1" className="font-medium" sx={{ fontWeight: 500 }}>
                  {selectedStep.desc || selectedStep.description}
                </Typography>
              </Box>
              
              {selectedStep.startTime && (
                <Box>
                  <Typography variant="body2" color="text.secondary" className="font-semibold mb-1" sx={{ fontWeight: 600, mb: 0.5 }}>
                    {t('journey.startTime', 'Başlangıç Saati')}
                  </Typography>
                  <Typography variant="body1" className="font-medium" sx={{ fontWeight: 500 }}>
                    {new Date(selectedStep.startTime).toLocaleString('tr-TR')}
                  </Typography>
                </Box>
              )}
              
              {selectedStep.endTime && (
                <Box>
                  <Typography variant="body2" color="text.secondary" className="font-semibold mb-1" sx={{ fontWeight: 600, mb: 0.5 }}>
                    {t('journey.endTime', 'Bitiş Saati')}
                  </Typography>
                  <Typography variant="body1" className="font-medium" sx={{ fontWeight: 500 }}>
                    {new Date(selectedStep.endTime).toLocaleString('tr-TR')}
                  </Typography>
                </Box>
              )}

              {selectedStep.checklist && selectedStep.checklist.length > 0 && (
                <Box>
                  <Typography variant="body2" color="text.secondary" className="font-semibold mb-2" sx={{ fontWeight: 600, mb: 1 }}>
                    {t('journey.checklist', 'Kontrol Listesi')}
                  </Typography>
                  <Box className="flex flex-wrap gap-2" sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                    {selectedStep.checklist.map((item) => (
                      <Chip
                        key={item.id}
                        icon={item.completed ? <CheckCircle fontSize="small" /> : <Schedule fontSize="small" />}
                        label={item.label}
                        color={item.completed ? 'success' : 'default'}
                        className="font-semibold"
                        sx={{ fontWeight: 600 }}
                      />
                    ))}
                  </Box>
                </Box>
              )}
            </Box>
          )}
        </DialogContent>
        <DialogActions className="p-6" sx={{ p: 3 }}>
          <Button 
            onClick={() => setOpenDialog(false)} 
            variant="contained"
            className="rounded-xl normal-case px-6"
            sx={{ borderRadius: 3, textTransform: 'none', px: 3 }}
          >
            {t('common.close', 'Kapat')}
          </Button>
        </DialogActions>
      </Dialog>
      </Container>
    </Box>
  );
};

export default PatientJourney;
