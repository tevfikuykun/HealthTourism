import React, { useState, useEffect } from 'react';
import {
  Box, Container, Typography, Card, CardContent, Grid, Chip,
  LinearProgress, Alert, Tabs, Tab, Paper, IconButton, Tooltip,
} from '@mui/material';
import {
  MonitorHeart, Favorite, LocalFireDepartment, Speed, TrendingUp, TrendingDown,
  Refresh, Activity, AlertCircle, CheckCircle,
} from '@mui/icons-material';
import { Heart, Thermometer, Droplets, Activity as ActivityIcon, AlertTriangle } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import { useTranslation } from '../i18n';
import { useQuery } from '@tanstack/react-query';
import { iotMonitoringService, patientRiskScoringService } from '../services/api';
import { useAuth } from '../hooks/useAuth';
import { fadeInUp, staggerContainer, staggerItem, hoverLift, scaleIn } from '../utils/ui-helpers';

/**
 * Digital Twin - Dijital İkiz
 * IoT verileriyle senkronize çalışan, hastanın hayati bulgularını gösteren 3D/Grafiksel modelleme
 * Material-UI + Tailwind CSS + Framer Motion + Lucide-React entegrasyonu
 */
const DigitalTwin = () => {
  const { t } = useTranslation();
  const { user } = useAuth();
  const [tabValue, setTabValue] = useState(0);
  const [selectedArea, setSelectedArea] = useState(null);
  const [pulseAnimation, setPulseAnimation] = useState(false);

  // Latest IoT Data - 5 saniyede bir güncelle
  const { data: latestIotData, isLoading: iotLoading, refetch: refetchIot } = useQuery({
    queryKey: ['iot-monitoring-latest', user?.id],
    queryFn: async () => {
      const response = await iotMonitoringService.getLatestByUser(user?.id);
      return response.data;
    },
    enabled: !!user?.id,
    refetchInterval: 5000,
  });

  // Recent IoT Data (24 hours)
  const { data: recentIotData, isLoading: recentLoading } = useQuery({
    queryKey: ['iot-monitoring-recent', user?.id],
    queryFn: async () => {
      const response = await iotMonitoringService.getRecentByUser(user?.id, 24);
      return response.data;
    },
    enabled: !!user?.id,
    refetchInterval: 10000,
  });

  // Recovery Score
  const { data: recoveryScore, isLoading: scoreLoading } = useQuery({
    queryKey: ['recovery-score', user?.id],
    queryFn: async () => {
      const response = await patientRiskScoringService.getLatestScore(user?.id, 1);
      return response.data;
    },
    enabled: !!user?.id,
    refetchInterval: 30000,
  });

  // Pulse animation effect
  useEffect(() => {
    if (latestIotData?.heartRate) {
      const interval = setInterval(() => {
        setPulseAnimation((prev) => !prev);
      }, 1000);
      return () => clearInterval(interval);
    }
  }, [latestIotData?.heartRate]);

  // Calculate health status for each body area based on IoT data
  const getBodyAreas = () => {
    const score = recoveryScore?.recoveryScore || latestIotData?.recoveryScore || 75;
    const heartRate = latestIotData?.heartRate || 72;
    const temperature = latestIotData?.temperature || 36.6;
    const oxygen = latestIotData?.oxygenSaturation || 98;

    // Calculate area scores based on IoT data
    const chestScore = Math.min(100, Math.max(0, 
      (heartRate >= 60 && heartRate <= 100 ? 90 : 60) + 
      (temperature >= 36 && temperature <= 37.5 ? 10 : -20) +
      (oxygen >= 95 ? 10 : -30)
    ));

    return {
      head: { score: 85, color: getHealthColor(85), label: t('digitalTwin.head', 'Baş') },
      chest: { 
        score: Math.round(chestScore), 
        color: getHealthColor(chestScore),
        label: t('digitalTwin.chest', 'Göğüs'),
        data: { heartRate, temperature, oxygen }
      },
      abdomen: { score: 70, color: getHealthColor(70), label: t('digitalTwin.abdomen', 'Karın') },
      leftArm: { score: 80, color: getHealthColor(80), label: t('digitalTwin.leftArm', 'Sol Kol') },
      rightArm: { score: 80, color: getHealthColor(80), label: t('digitalTwin.rightArm', 'Sağ Kol') },
      leftLeg: { score: 75, color: getHealthColor(75), label: t('digitalTwin.leftLeg', 'Sol Bacak') },
      rightLeg: { score: 75, color: getHealthColor(75), label: t('digitalTwin.rightLeg', 'Sağ Bacak') },
    };
  };

  const getHealthColor = (score) => {
    if (score >= 80) return '#4caf50'; // Green
    if (score >= 60) return '#ff9800'; // Orange
    if (score >= 40) return '#ff5722'; // Red
    return '#d32f2f'; // Dark Red
  };

  const bodyAreas = getBodyAreas();
  const currentScore = recoveryScore?.recoveryScore || latestIotData?.recoveryScore || 75;

  if (iotLoading || recentLoading || scoreLoading) {
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
        background: 'linear-gradient(135deg, #0F172A 0%, #1E293B 50%, #0F172A 100%)',
        position: 'relative',
        overflow: 'hidden',
        '&::before': {
          content: '""',
          position: 'absolute',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          background: 'radial-gradient(circle at 20% 30%, rgba(129, 140, 248, 0.15) 0%, transparent 50%), radial-gradient(circle at 80% 70%, rgba(167, 139, 250, 0.15) 0%, transparent 50%)',
          pointerEvents: 'none',
        },
        '&::after': {
          content: '""',
          position: 'absolute',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          backgroundImage: 'linear-gradient(0deg, transparent 0%, rgba(129, 140, 248, 0.03) 50%, transparent 100%)',
          pointerEvents: 'none',
        },
      }}
    >
      <Container maxWidth="xl" sx={{ py: 6, position: 'relative', zIndex: 1 }}>
        {/* Header: Dark Mode with Neon Accents */}
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
                  background: 'linear-gradient(135deg, #818CF8 0%, #A78BFA 100%)',
                  backgroundClip: 'text',
                  WebkitBackgroundClip: 'text',
                  WebkitTextFillColor: 'transparent',
                  mb: 1,
                  textShadow: '0 0 30px rgba(129, 140, 248, 0.5)',
                }}
              >
                {t('digitalTwin.title', 'Dijital İkiz Görselleştirme')}
              </Typography>
              <Typography 
                variant="body1" 
                sx={{ color: 'rgba(255, 255, 255, 0.7)', mt: 1, fontWeight: 500, letterSpacing: '-0.01em' }}
              >
                {t('digitalTwin.subtitle', 'IoT verileriyle gerçek zamanlı sağlık durumu simülasyonu')}
              </Typography>
            </Box>
          
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
            <Box
              sx={{
                backdropFilter: 'blur(20px) saturate(180%)',
                WebkitBackdropFilter: 'blur(20px) saturate(180%)',
                backgroundColor: 'rgba(34, 197, 94, 0.15)',
                border: '1px solid rgba(34, 197, 94, 0.3)',
                borderRadius: 3,
                px: 2,
                py: 1,
                display: 'flex',
                alignItems: 'center',
                gap: 1,
                boxShadow: '0 0 20px rgba(34, 197, 94, 0.3)',
                animation: 'pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite',
                '@keyframes pulse': {
                  '0%, 100%': { boxShadow: '0 0 20px rgba(34, 197, 94, 0.3)' },
                  '50%': { boxShadow: '0 0 30px rgba(34, 197, 94, 0.6)' },
                },
              }}
            >
              <ActivityIcon size={18} style={{ color: '#22c55e' }} />
              <Typography sx={{ fontWeight: 800, color: '#22c55e', fontSize: '0.875rem', letterSpacing: '0.1em' }}>
                {t('digitalTwin.live', 'CANLI')}
              </Typography>
            </Box>
            <Tooltip title={t('digitalTwin.refresh', 'Yenile')}>
              <IconButton 
                onClick={() => refetchIot()}
                sx={{ 
                  borderRadius: 3,
                  backdropFilter: 'blur(20px) saturate(180%)',
                  WebkitBackdropFilter: 'blur(20px) saturate(180%)',
                  backgroundColor: 'rgba(129, 140, 248, 0.15)',
                  border: '1px solid rgba(129, 140, 248, 0.3)',
                  color: '#818CF8',
                  '&:hover': {
                    backgroundColor: 'rgba(129, 140, 248, 0.25)',
                    boxShadow: '0 0 20px rgba(129, 140, 248, 0.4)',
                  },
                }}
              >
                <Refresh />
              </IconButton>
            </Tooltip>
          </Box>
        </Box>
      </motion.div>

      {/* Real-time Status Alert - Dark Mode Glassmorphism */}
      {latestIotData && (
        <motion.div
          initial={{ opacity: 0, y: -10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.1 }}
        >
          <Box
            sx={{
              mb: 3,
              borderRadius: 4,
              backdropFilter: 'blur(20px) saturate(180%)',
              WebkitBackdropFilter: 'blur(20px) saturate(180%)',
              backgroundColor: latestIotData.heartRate > 100 || latestIotData.temperature > 38
                ? 'rgba(245, 158, 11, 0.15)'
                : 'rgba(34, 197, 94, 0.15)',
              border: `1px solid ${latestIotData.heartRate > 100 || latestIotData.temperature > 38
                ? 'rgba(245, 158, 11, 0.3)'
                : 'rgba(34, 197, 94, 0.3)'}`,
              boxShadow: latestIotData.heartRate > 100 || latestIotData.temperature > 38
                ? '0 0 20px rgba(245, 158, 11, 0.3)'
                : '0 0 20px rgba(34, 197, 94, 0.3)',
              p: 3,
            }}
          >
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 1 }}>
              <MonitorHeart sx={{ color: latestIotData.heartRate > 100 || latestIotData.temperature > 38 ? '#F59E0B' : '#22c55e' }} />
              <Typography variant="subtitle2" sx={{ fontWeight: 700, color: 'rgba(255, 255, 255, 0.9)', letterSpacing: '-0.01em' }}>
                {t('digitalTwin.realTimeStatus', 'Gerçek Zamanlı Durum')}
              </Typography>
            </Box>
            <Typography variant="body2" sx={{ color: 'rgba(255, 255, 255, 0.7)', fontWeight: 500 }}>
              {t('digitalTwin.heartRate', 'Nabız')}: <strong style={{ color: '#F43F5E' }}>{latestIotData.heartRate || 'N/A'}</strong> bpm |{' '}
              {t('digitalTwin.temperature', 'Ateş')}: <strong style={{ color: '#F59E0B' }}>{latestIotData.temperature || 'N/A'}</strong> °C |{' '}
              {t('digitalTwin.oxygenSaturation', 'Oksijen')}: <strong style={{ color: '#10B981' }}>{latestIotData.oxygenSaturation || 'N/A'}</strong>%
            </Typography>
          </Box>
        </motion.div>
      )}

      {/* Vital Signs Cards: Material-UI + Tailwind + Framer Motion + Lucide-React */}
      <motion.div
        variants={staggerContainer}
        initial="hidden"
        animate="show"
      >
        <Grid container spacing={3} className="mb-8" sx={{ mb: 4 }}>
          {/* Heart Rate Card */}
          <Grid item xs={12} sm={6} md={3}>
            <motion.div
              variants={staggerItem}
              {...hoverLift}
            >
              <Card 
                sx={{ 
                  borderRadius: 6, 
                  backdropFilter: 'blur(20px) saturate(180%)',
                  WebkitBackdropFilter: 'blur(20px) saturate(180%)',
                  backgroundColor: 'rgba(30, 41, 59, 0.6)',
                  border: '1px solid rgba(244, 63, 94, 0.3)',
                  boxShadow: '0 8px 32px rgba(0, 0, 0, 0.3), 0 0 20px rgba(244, 63, 94, 0.2)',
                  transition: 'all 0.3s ease',
                  '&:hover': {
                    boxShadow: '0 12px 40px rgba(0, 0, 0, 0.4), 0 0 30px rgba(244, 63, 94, 0.4)',
                    borderColor: 'rgba(244, 63, 94, 0.5)',
                    transform: 'translateY(-4px)',
                  },
                }}
              >
                <CardContent sx={{ p: 4 }}>
                  <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', mb: 2 }}>
                    <Box 
                      sx={{ 
                        p: 2, 
                        background: 'linear-gradient(135deg, rgba(244, 63, 94, 0.2) 0%, rgba(225, 29, 72, 0.15) 100%)',
                        borderRadius: 3,
                        border: '1px solid rgba(244, 63, 94, 0.3)',
                        boxShadow: pulseAnimation ? '0 0 20px rgba(244, 63, 94, 0.5)' : '0 0 10px rgba(244, 63, 94, 0.3)',
                        transition: 'all 0.3s ease',
                      }}
                    >
                      <Heart 
                        size={32} 
                        style={{ color: '#F43F5E' }}
                        strokeWidth={2.5}
                      />
                    </Box>
                    {latestIotData?.heartRate && (
                      <Box
                        sx={{
                          backdropFilter: 'blur(10px)',
                          backgroundColor: latestIotData.heartRate > 100
                            ? 'rgba(244, 63, 94, 0.2)'
                            : latestIotData.heartRate < 60
                            ? 'rgba(245, 158, 11, 0.2)'
                            : 'rgba(34, 197, 94, 0.2)',
                          border: `1px solid ${latestIotData.heartRate > 100
                            ? 'rgba(244, 63, 94, 0.4)'
                            : latestIotData.heartRate < 60
                            ? 'rgba(245, 158, 11, 0.4)'
                            : 'rgba(34, 197, 94, 0.4)'}`,
                          borderRadius: 2,
                          px: 1.5,
                          py: 0.5,
                          display: 'flex',
                          alignItems: 'center',
                          gap: 0.5,
                        }}
                      >
                        {latestIotData.heartRate > 100 ? (
                          <TrendingUp sx={{ fontSize: 14, color: '#F43F5E' }} />
                        ) : latestIotData.heartRate < 60 ? (
                          <TrendingDown sx={{ fontSize: 14, color: '#F59E0B' }} />
                        ) : null}
                        <Typography sx={{ fontSize: '0.75rem', fontWeight: 700, color: latestIotData.heartRate > 100 ? '#F43F5E' : latestIotData.heartRate < 60 ? '#F59E0B' : '#22c55e' }}>
                          {latestIotData.heartRate > 100
                            ? t('digitalTwin.high', 'Yüksek')
                            : latestIotData.heartRate < 60
                            ? t('digitalTwin.low', 'Düşük')
                            : t('digitalTwin.normal', 'Normal')}
                        </Typography>
                      </Box>
                    )}
                  </Box>
                  <Typography variant="body2" sx={{ color: 'rgba(255, 255, 255, 0.7)', fontWeight: 600, letterSpacing: '0.05em', textTransform: 'uppercase', fontSize: '0.75rem' }}>
                    {t('digitalTwin.heartRate', 'Nabız')}
                  </Typography>
                  <Typography variant="h4" sx={{ fontWeight: 900, mt: 1, color: '#F43F5E', letterSpacing: '-0.02em' }}>
                    {latestIotData?.heartRate || 'N/A'}
                    <Typography component="span" variant="body2" sx={{ color: 'rgba(255, 255, 255, 0.5)', ml: 1, fontWeight: 500 }}>
                      bpm
                    </Typography>
                  </Typography>
                </CardContent>
              </Card>
            </motion.div>
          </Grid>

          {/* Temperature Card */}
          <Grid item xs={12} sm={6} md={3}>
            <motion.div
              variants={staggerItem}
              {...hoverLift}
            >
              <Card 
                className="rounded-3xl border border-gray-200 shadow-md hover:shadow-xl transition-all duration-300"
                sx={{ borderRadius: 6, border: '1px solid #e2e8f0', boxShadow: '0 4px 12px rgba(0,0,0,0.03)' }}
              >
                <CardContent className="p-6" sx={{ p: 4 }}>
                  <Box className="flex items-center justify-between mb-4" sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', mb: 2 }}>
                    <Box className="p-3 bg-orange-50 rounded-2xl" sx={{ p: 1.5, bgcolor: 'rgba(255, 152, 0, 0.1)', borderRadius: 4 }}>
                      <Thermometer 
                        size={32} 
                        className="text-orange-600"
                        style={{ color: '#ff9800' }}
                        strokeWidth={2}
                      />
                    </Box>
                    {latestIotData?.temperature && (
                      <Chip
                        label={
                          latestIotData.temperature > 38
                            ? t('digitalTwin.fever', 'Ateş')
                            : latestIotData.temperature < 36
                            ? t('digitalTwin.low', 'Düşük')
                            : t('digitalTwin.normal', 'Normal')
                        }
                        size="small"
                        color={
                          latestIotData.temperature > 38
                            ? 'error'
                            : latestIotData.temperature < 36
                            ? 'warning'
                            : 'success'
                        }
                        className="font-bold"
                        sx={{ fontWeight: 700 }}
                      />
                    )}
                  </Box>
                  <Typography variant="body2" color="text.secondary" className="font-semibold" sx={{ fontWeight: 600 }}>
                    {t('digitalTwin.temperature', 'Ateş')}
                  </Typography>
                  <Typography variant="h4" className="font-extrabold mt-2" sx={{ fontWeight: 800, mt: 1 }}>
                    {latestIotData?.temperature || 'N/A'}
                    <Typography component="span" variant="body2" className="text-gray-500 ml-2" sx={{ color: 'text.secondary', ml: 1 }}>
                      °C
                    </Typography>
                  </Typography>
                </CardContent>
              </Card>
            </motion.div>
          </Grid>

          {/* Oxygen Saturation Card */}
          <Grid item xs={12} sm={6} md={3}>
            <motion.div
              variants={staggerItem}
              {...hoverLift}
            >
              <Card 
                className="rounded-3xl border border-gray-200 shadow-md hover:shadow-xl transition-all duration-300"
                sx={{ borderRadius: 6, border: '1px solid #e2e8f0', boxShadow: '0 4px 12px rgba(0,0,0,0.03)' }}
              >
                <CardContent className="p-6" sx={{ p: 4 }}>
                  <Box className="flex items-center justify-between mb-4" sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', mb: 2 }}>
                    <Box className="p-3 bg-blue-50 rounded-2xl" sx={{ p: 1.5, bgcolor: 'rgba(25, 118, 210, 0.1)', borderRadius: 4 }}>
                      <Droplets 
                        size={32} 
                        className="text-blue-600"
                        style={{ color: '#1976d2' }}
                        strokeWidth={2}
                      />
                    </Box>
                    {latestIotData?.oxygenSaturation && (
                      <Chip
                        label={
                          latestIotData.oxygenSaturation < 95
                            ? t('digitalTwin.low', 'Düşük')
                            : t('digitalTwin.normal', 'Normal')
                        }
                        size="small"
                        color={latestIotData.oxygenSaturation < 95 ? 'error' : 'success'}
                        className="font-bold"
                        sx={{ fontWeight: 700 }}
                      />
                    )}
                  </Box>
                  <Typography variant="body2" color="text.secondary" className="font-semibold" sx={{ fontWeight: 600 }}>
                    {t('digitalTwin.oxygenSaturation', 'Oksijen Satürasyonu')}
                  </Typography>
                  <Typography variant="h4" className="font-extrabold mt-2" sx={{ fontWeight: 800, mt: 1 }}>
                    {latestIotData?.oxygenSaturation || 'N/A'}
                    <Typography component="span" variant="body2" className="text-gray-500 ml-2" sx={{ color: 'text.secondary', ml: 1 }}>
                      %
                    </Typography>
                  </Typography>
                </CardContent>
              </Card>
            </motion.div>
          </Grid>

          {/* Recovery Score Card */}
          <Grid item xs={12} sm={6} md={3}>
            <motion.div
              variants={staggerItem}
              {...hoverLift}
            >
              <Card 
                className="rounded-3xl border border-gray-200 shadow-md hover:shadow-xl transition-all duration-300"
                sx={{ borderRadius: 6, border: '1px solid #e2e8f0', boxShadow: '0 4px 12px rgba(0,0,0,0.03)' }}
              >
                <CardContent className="p-6" sx={{ p: 4 }}>
                  <Box className="flex items-center justify-between mb-4" sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', mb: 2 }}>
                    <Box className="p-3 bg-green-50 rounded-2xl" sx={{ p: 1.5, bgcolor: 'rgba(76, 175, 80, 0.1)', borderRadius: 4 }}>
                      <ActivityIcon 
                        size={32} 
                        className="text-green-600"
                        style={{ color: '#4caf50' }}
                        strokeWidth={2}
                      />
                    </Box>
                    <Chip
                      label={currentScore >= 80 ? t('digitalTwin.excellent', 'Mükemmel') : currentScore >= 60 ? t('digitalTwin.good', 'İyi') : t('digitalTwin.fair', 'Orta')}
                      size="small"
                      color={currentScore >= 80 ? 'success' : currentScore >= 60 ? 'warning' : 'error'}
                      className="font-bold"
                      sx={{ fontWeight: 700 }}
                    />
                  </Box>
                  <Typography variant="body2" color="text.secondary" className="font-semibold" sx={{ fontWeight: 600 }}>
                    {t('digitalTwin.recoveryScore', 'İyileşme Skoru')}
                  </Typography>
                  <Typography variant="h4" className="font-extrabold mt-2" sx={{ fontWeight: 800, mt: 1 }}>
                    {currentScore}
                    <Typography component="span" variant="body2" className="text-gray-500 ml-2" sx={{ color: 'text.secondary', ml: 1 }}>
                      /100
                    </Typography>
                  </Typography>
                  <LinearProgress
                    variant="determinate"
                    value={currentScore}
                    color={currentScore >= 80 ? 'success' : currentScore >= 60 ? 'warning' : 'error'}
                    className="mt-4 rounded-full"
                    sx={{ mt: 2, height: 8, borderRadius: 4 }}
                  />
                </CardContent>
              </Card>
            </motion.div>
          </Grid>
        </Grid>
      </motion.div>

      {/* Digital Twin Visualization: Material-UI + Tailwind + Framer Motion */}
      <motion.div
        variants={fadeInUp}
        initial="initial"
        animate="animate"
        transition={{ delay: 0.3 }}
      >
        <Card 
          className="rounded-3xl border border-gray-200 shadow-lg overflow-hidden"
          sx={{ borderRadius: 6, border: '1px solid #e2e8f0', boxShadow: '0 10px 30px rgba(0,0,0,0.04)', mb: 4 }}
        >
          <CardContent className="p-6" sx={{ p: 4 }}>
            <Typography variant="h5" className="font-bold mb-4" sx={{ fontWeight: 700, mb: 2 }}>
              {t('digitalTwin.bodyModel', 'Vücut Modeli')}
            </Typography>
            
            <Grid container spacing={4}>
              {/* SVG Human Model */}
              <Grid item xs={12} md={8}>
                <Box
                  className="flex justify-center items-center bg-gray-50 rounded-3xl p-8 min-h-[500px]"
                  sx={{
                    display: 'flex',
                    justifyContent: 'center',
                    alignItems: 'center',
                    bgcolor: 'grey.50',
                    borderRadius: 6,
                    p: 4,
                    minHeight: 500,
                  }}
                >
                  <motion.svg
                    width="300"
                    height="600"
                    viewBox="0 0 200 400"
                    className="max-w-full h-auto"
                    style={{ maxWidth: '100%', height: 'auto' }}
                    initial={{ opacity: 0, scale: 0.9 }}
                    animate={{ opacity: 1, scale: 1 }}
                    transition={{ duration: 0.5 }}
                  >
                    {/* Head */}
                    <motion.ellipse
                      cx="100"
                      cy="50"
                      rx="30"
                      ry="40"
                      fill={bodyAreas.head.color}
                      stroke="#333"
                      strokeWidth="2"
                      opacity={selectedArea === 'head' ? 1 : 0.8}
                      onClick={() => setSelectedArea('head')}
                      className="cursor-pointer transition-opacity"
                      whileHover={{ scale: 1.05 }}
                      whileTap={{ scale: 0.95 }}
                    />

                    {/* Chest */}
                    <motion.rect
                      x="70"
                      y="90"
                      width="60"
                      height="80"
                      rx="10"
                      fill={bodyAreas.chest.color}
                      stroke="#333"
                      strokeWidth="2"
                      opacity={selectedArea === 'chest' ? 1 : 0.8}
                      onClick={() => setSelectedArea('chest')}
                      className="cursor-pointer transition-opacity"
                      whileHover={{ scale: 1.05 }}
                      whileTap={{ scale: 0.95 }}
                      animate={pulseAnimation && selectedArea === 'chest' ? { scale: 1.02 } : {}}
                    />

                    {/* Abdomen */}
                    <motion.rect
                      x="75"
                      y="170"
                      width="50"
                      height="60"
                      rx="8"
                      fill={bodyAreas.abdomen.color}
                      stroke="#333"
                      strokeWidth="2"
                      opacity={selectedArea === 'abdomen' ? 1 : 0.8}
                      onClick={() => setSelectedArea('abdomen')}
                      className="cursor-pointer transition-opacity"
                      whileHover={{ scale: 1.05 }}
                      whileTap={{ scale: 0.95 }}
                    />

                    {/* Left Arm */}
                    <motion.g
                      onClick={() => setSelectedArea('leftArm')}
                      className="cursor-pointer"
                      whileHover={{ scale: 1.05 }}
                      whileTap={{ scale: 0.95 }}
                    >
                      <rect
                        x="20"
                        y="100"
                        width="50"
                        height="15"
                        rx="7"
                        fill={bodyAreas.leftArm.color}
                        stroke="#333"
                        strokeWidth="2"
                        opacity={selectedArea === 'leftArm' ? 1 : 0.8}
                      />
                      <rect
                        x="10"
                        y="115"
                        width="15"
                        height="80"
                        rx="7"
                        fill={bodyAreas.leftArm.color}
                        stroke="#333"
                        strokeWidth="2"
                        opacity={selectedArea === 'leftArm' ? 1 : 0.8}
                      />
                    </motion.g>

                    {/* Right Arm */}
                    <motion.g
                      onClick={() => setSelectedArea('rightArm')}
                      className="cursor-pointer"
                      whileHover={{ scale: 1.05 }}
                      whileTap={{ scale: 0.95 }}
                    >
                      <rect
                        x="130"
                        y="100"
                        width="50"
                        height="15"
                        rx="7"
                        fill={bodyAreas.rightArm.color}
                        stroke="#333"
                        strokeWidth="2"
                        opacity={selectedArea === 'rightArm' ? 1 : 0.8}
                      />
                      <rect
                        x="175"
                        y="115"
                        width="15"
                        height="80"
                        rx="7"
                        fill={bodyAreas.rightArm.color}
                        stroke="#333"
                        strokeWidth="2"
                        opacity={selectedArea === 'rightArm' ? 1 : 0.8}
                      />
                    </motion.g>

                    {/* Left Leg */}
                    <motion.g
                      onClick={() => setSelectedArea('leftLeg')}
                      className="cursor-pointer"
                      whileHover={{ scale: 1.05 }}
                      whileTap={{ scale: 0.95 }}
                    >
                      <rect
                        x="75"
                        y="230"
                        width="20"
                        height="100"
                        rx="10"
                        fill={bodyAreas.leftLeg.color}
                        stroke="#333"
                        strokeWidth="2"
                        opacity={selectedArea === 'leftLeg' ? 1 : 0.8}
                      />
                      <ellipse
                        cx="85"
                        cy="340"
                        rx="15"
                        ry="20"
                        fill={bodyAreas.leftLeg.color}
                        stroke="#333"
                        strokeWidth="2"
                        opacity={selectedArea === 'leftLeg' ? 1 : 0.8}
                      />
                    </motion.g>

                    {/* Right Leg */}
                    <motion.g
                      onClick={() => setSelectedArea('rightLeg')}
                      className="cursor-pointer"
                      whileHover={{ scale: 1.05 }}
                      whileTap={{ scale: 0.95 }}
                    >
                      <rect
                        x="105"
                        y="230"
                        width="20"
                        height="100"
                        rx="10"
                        fill={bodyAreas.rightLeg.color}
                        stroke="#333"
                        strokeWidth="2"
                        opacity={selectedArea === 'rightLeg' ? 1 : 0.8}
                      />
                      <ellipse
                        cx="115"
                        cy="340"
                        rx="15"
                        ry="20"
                        fill={bodyAreas.rightLeg.color}
                        stroke="#333"
                        strokeWidth="2"
                        opacity={selectedArea === 'rightLeg' ? 1 : 0.8}
                      />
                    </motion.g>
                  </motion.svg>
                </Box>
              </Grid>

              {/* Health Status Sidebar */}
              <Grid item xs={12} md={4}>
                <Box>
                  <Typography variant="subtitle1" className="font-bold mb-4" sx={{ fontWeight: 700, mb: 2 }}>
                    {t('digitalTwin.healthStatus', 'Sağlık Durumu')}
                  </Typography>
                  <motion.div
                    variants={staggerContainer}
                    initial="hidden"
                    animate="show"
                  >
                    {Object.entries(bodyAreas).map(([area, data], index) => (
                      <motion.div
                        key={area}
                        variants={staggerItem}
                        {...hoverLift}
                      >
                        <Box
                          className={`p-4 mb-3 rounded-2xl border-2 cursor-pointer transition-all ${
                            selectedArea === area 
                              ? 'border-blue-500 bg-blue-50 shadow-md' 
                              : 'border-gray-200 bg-white hover:border-gray-300'
                          }`}
                          sx={{
                            p: 2,
                            mb: 2,
                            borderRadius: 4,
                            border: selectedArea === area ? '2px solid' : '1px solid',
                            borderColor: selectedArea === area ? 'primary.main' : 'divider',
                            bgcolor: selectedArea === area ? 'action.selected' : 'transparent',
                            cursor: 'pointer',
                          }}
                          onClick={() => setSelectedArea(area)}
                        >
                          <Box className="flex justify-between items-center mb-2" sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
                            <Typography variant="body2" className="font-semibold capitalize" sx={{ fontWeight: 600, textTransform: 'capitalize' }}>
                              {data.label}
                            </Typography>
                            <Typography variant="body2" className="font-bold" sx={{ fontWeight: 700 }}>
                              {data.score}%
                            </Typography>
                          </Box>
                          <Box
                            className="w-full h-2 bg-gray-200 rounded-full overflow-hidden"
                            sx={{
                              width: '100%',
                              height: 8,
                              bgcolor: 'grey.200',
                              borderRadius: 1,
                              overflow: 'hidden',
                            }}
                          >
                            <motion.div
                              initial={{ width: 0 }}
                              animate={{ width: `${data.score}%` }}
                              transition={{ duration: 0.5, delay: index * 0.1 }}
                              style={{
                                height: '100%',
                                backgroundColor: data.color,
                              }}
                            />
                          </Box>
                          {selectedArea === area && data.data && (
                            <Box className="mt-3 pt-3 border-t border-gray-200" sx={{ mt: 2, pt: 2, borderTop: '1px solid #e2e8f0' }}>
                              <Typography variant="caption" className="text-gray-600" sx={{ color: 'text.secondary' }}>
                                {t('digitalTwin.heartRate', 'Nabız')}: {data.data.heartRate} bpm
                              </Typography>
                              <br />
                              <Typography variant="caption" className="text-gray-600" sx={{ color: 'text.secondary' }}>
                                {t('digitalTwin.temperature', 'Ateş')}: {data.data.temperature} °C
                              </Typography>
                              <br />
                              <Typography variant="caption" className="text-gray-600" sx={{ color: 'text.secondary' }}>
                                {t('digitalTwin.oxygenSaturation', 'Oksijen')}: {data.data.oxygen}%
                              </Typography>
                            </Box>
                          )}
                        </Box>
                      </motion.div>
                    ))}
                  </motion.div>
                </Box>
              </Grid>
            </Grid>

            {/* Legend */}
            <Box className="mt-6 flex gap-4 justify-center flex-wrap" sx={{ mt: 3, display: 'flex', gap: 2, justifyContent: 'center', flexWrap: 'wrap' }}>
              <Box className="flex items-center gap-2" sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <Box className="w-5 h-5 bg-green-500 rounded" sx={{ width: 20, height: 20, bgcolor: '#4caf50', borderRadius: 1 }} />
                <Typography variant="caption" className="font-semibold" sx={{ fontWeight: 600 }}>
                  {t('digitalTwin.excellent', 'Mükemmel')} (80-100%)
                </Typography>
              </Box>
              <Box className="flex items-center gap-2" sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <Box className="w-5 h-5 bg-orange-500 rounded" sx={{ width: 20, height: 20, bgcolor: '#ff9800', borderRadius: 1 }} />
                <Typography variant="caption" className="font-semibold" sx={{ fontWeight: 600 }}>
                  {t('digitalTwin.good', 'İyi')} (60-79%)
                </Typography>
              </Box>
              <Box className="flex items-center gap-2" sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <Box className="w-5 h-5 bg-red-500 rounded" sx={{ width: 20, height: 20, bgcolor: '#ff5722', borderRadius: 1 }} />
                <Typography variant="caption" className="font-semibold" sx={{ fontWeight: 600 }}>
                  {t('digitalTwin.fair', 'Orta')} (40-59%)
                </Typography>
              </Box>
              <Box className="flex items-center gap-2" sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <Box className="w-5 h-5 bg-red-700 rounded" sx={{ width: 20, height: 20, bgcolor: '#d32f2f', borderRadius: 1 }} />
                <Typography variant="caption" className="font-semibold" sx={{ fontWeight: 600 }}>
                  {t('digitalTwin.poor', 'Zayıf')} (&lt;40%)
                </Typography>
              </Box>
            </Box>
          </CardContent>
        </Card>
      </motion.div>

      {/* Tabs for Historical Data */}
      <motion.div
        variants={fadeInUp}
        initial="initial"
        animate="animate"
        transition={{ delay: 0.4 }}
      >
        <Card 
          className="rounded-3xl border border-gray-200 shadow-lg"
          sx={{ borderRadius: 6, border: '1px solid #e2e8f0', boxShadow: '0 10px 30px rgba(0,0,0,0.04)' }}
        >
          <Box className="border-b border-gray-200" sx={{ borderBottom: 1, borderColor: 'divider' }}>
            <Tabs 
              value={tabValue} 
              onChange={(e, newValue) => setTabValue(newValue)}
              className="px-6"
              sx={{ px: 3 }}
            >
              <Tab 
                label={t('digitalTwin.realTime', 'Gerçek Zamanlı')} 
                className="font-semibold"
                sx={{ fontWeight: 600, textTransform: 'none' }}
              />
              <Tab 
                label={t('digitalTwin.historical', 'Geçmiş Veriler')} 
                className="font-semibold"
                sx={{ fontWeight: 600, textTransform: 'none' }}
              />
              <Tab 
                label={t('digitalTwin.trends', 'Trendler')} 
                className="font-semibold"
                sx={{ fontWeight: 600, textTransform: 'none' }}
              />
            </Tabs>
          </Box>

          <CardContent className="p-6" sx={{ p: 3 }}>
            <AnimatePresence mode="wait">
              {tabValue === 0 && (
                <motion.div
                  variants={scaleIn}
                  initial="initial"
                  animate="animate"
                  exit="exit"
                >
                  <Typography variant="h6" className="font-bold mb-4" sx={{ fontWeight: 700, mb: 2 }}>
                    {t('digitalTwin.latestReadings', 'Son Okumalar')}
                  </Typography>
                  {latestIotData ? (
                    <Paper variant="outlined" className="p-4 rounded-2xl" sx={{ p: 2, borderRadius: 4 }}>
                      <Grid container spacing={3}>
                        <Grid item xs={12} md={6}>
                          <Typography variant="body2" color="text.secondary" className="font-semibold" sx={{ fontWeight: 600 }}>
                            {t('digitalTwin.heartRate', 'Nabız')}
                          </Typography>
                          <Typography variant="h6" className="font-bold" sx={{ fontWeight: 700 }}>
                            {latestIotData.heartRate} bpm
                          </Typography>
                        </Grid>
                        <Grid item xs={12} md={6}>
                          <Typography variant="body2" color="text.secondary" className="font-semibold" sx={{ fontWeight: 600 }}>
                            {t('digitalTwin.temperature', 'Ateş')}
                          </Typography>
                          <Typography variant="h6" className="font-bold" sx={{ fontWeight: 700 }}>
                            {latestIotData.temperature} °C
                          </Typography>
                        </Grid>
                        {latestIotData.bloodPressure && (
                          <Grid item xs={12} md={6}>
                            <Typography variant="body2" color="text.secondary" className="font-semibold" sx={{ fontWeight: 600 }}>
                              {t('digitalTwin.bloodPressure', 'Tansiyon')}
                            </Typography>
                            <Typography variant="h6" className="font-bold" sx={{ fontWeight: 700 }}>
                              {latestIotData.bloodPressure} mmHg
                            </Typography>
                          </Grid>
                        )}
                        {latestIotData.oxygenSaturation && (
                          <Grid item xs={12} md={6}>
                            <Typography variant="body2" color="text.secondary" className="font-semibold" sx={{ fontWeight: 600 }}>
                              {t('digitalTwin.oxygenSaturation', 'Oksijen Satürasyonu')}
                            </Typography>
                            <Typography variant="h6" className="font-bold" sx={{ fontWeight: 700 }}>
                              {latestIotData.oxygenSaturation}%
                            </Typography>
                          </Grid>
                        )}
                        <Grid item xs={12}>
                          <Typography variant="body2" color="text.secondary" className="font-semibold" sx={{ fontWeight: 600 }}>
                            {t('digitalTwin.timestamp', 'Zaman Damgası')}
                          </Typography>
                          <Typography variant="body2" className="font-mono" sx={{ fontFamily: 'monospace' }}>
                            {latestIotData.timestamp
                              ? new Date(latestIotData.timestamp).toLocaleString('tr-TR')
                              : t('digitalTwin.now', 'Şimdi')}
                          </Typography>
                        </Grid>
                      </Grid>
                    </Paper>
                  ) : (
                    <Alert severity="info" className="rounded-2xl" sx={{ borderRadius: 4 }}>
                      {t('digitalTwin.noData', 'Henüz IoT verisi yok')}
                    </Alert>
                  )}
                </motion.div>
              )}

              {tabValue === 1 && (
                <motion.div
                  variants={scaleIn}
                  initial="initial"
                  animate="animate"
                  exit="exit"
                >
                  <Typography variant="h6" className="font-bold mb-4" sx={{ fontWeight: 700, mb: 2 }}>
                    {t('digitalTwin.last24Hours', 'Son 24 Saat')}
                  </Typography>
                  {recentIotData && recentIotData.length > 0 ? (
                    <Paper variant="outlined" className="p-4 rounded-2xl" sx={{ p: 2, borderRadius: 4 }}>
                      <Typography variant="body2" color="text.secondary" className="font-semibold" sx={{ fontWeight: 600 }}>
                        {t('digitalTwin.totalReadings', 'Toplam Okuma')}: <strong>{recentIotData.length}</strong>
                      </Typography>
                      <Typography variant="body2" color="text.secondary" className="mt-2" sx={{ mt: 1 }}>
                        {t('digitalTwin.historicalDataInfo', 'Geçmiş veri görselleştirmesi yakında eklenecek')}
                      </Typography>
                    </Paper>
                  ) : (
                    <Alert severity="info" className="rounded-2xl" sx={{ borderRadius: 4 }}>
                      {t('digitalTwin.noHistoricalData', 'Geçmiş veri yok')}
                    </Alert>
                  )}
                </motion.div>
              )}

              {tabValue === 2 && (
                <motion.div
                  variants={scaleIn}
                  initial="initial"
                  animate="animate"
                  exit="exit"
                >
                  <Typography variant="h6" className="font-bold mb-4" sx={{ fontWeight: 700, mb: 2 }}>
                    {t('digitalTwin.trends', 'Trendler')}
                  </Typography>
                  <Alert severity="info" className="rounded-2xl" sx={{ borderRadius: 4 }}>
                    {t('digitalTwin.trendsInfo', 'Trend analizi yakında eklenecek')}
                  </Alert>
                </motion.div>
              )}
            </AnimatePresence>
          </CardContent>
        </Card>
      </motion.div>
      </Container>
    </Box>
  );
};

export default DigitalTwin;
