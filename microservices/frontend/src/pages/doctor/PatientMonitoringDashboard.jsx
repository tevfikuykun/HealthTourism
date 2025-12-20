import React, { useState } from 'react';
import {
  Box,
  Container,
  Typography,
  Grid,
  Card,
  CardContent,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Chip,
  LinearProgress,
  Alert,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Tabs,
  Tab,
} from '@mui/material';
import {
  Warning,
  CheckCircle,
  TrendingUp,
  TrendingDown,
  Refresh,
  Visibility,
  LocalHospital,
} from '@mui/icons-material';
import { useTranslation } from 'react-i18next';
import { useQuery } from '@tanstack/react-query';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import api from '../../services/api';

/**
 * Doctor's Patient Monitoring Dashboard
 * Doktorun kendisine atanmış hastaların IoT verilerini,
 * AI Risk Skorlarını ve ameliyat sonrası iyileşme grafiklerini
 * "kuş bakışı" görebileceği sayfa
 */
const PatientMonitoringDashboard = () => {
  const { t } = useTranslation();
  const [selectedTab, setSelectedTab] = useState(0);
  const [selectedPatient, setSelectedPatient] = useState(null);
  const [openDetail, setOpenDetail] = useState(false);

  // Fetch assigned patients
  const { data: patients, isLoading, refetch } = useQuery({
    queryKey: ['doctor-patients'],
    queryFn: async () => {
      const response = await api.get('/api/doctors/me/patients');
      return response.data;
    },
    refetchInterval: 30000, // 30 saniyede bir güncelle
  });

  // Fetch IoT data for all patients
  const { data: iotData } = useQuery({
    queryKey: ['doctor-iot-data'],
    queryFn: async () => {
      const response = await api.get('/api/iot-monitoring/doctor/me');
      return response.data;
    },
    refetchInterval: 10000, // 10 saniyede bir IoT verisi çek
  });

  // Fetch risk scores
  const { data: riskScores } = useQuery({
    queryKey: ['doctor-risk-scores'],
    queryFn: async () => {
      const response = await api.get('/api/patient-risk-scoring/doctor/me');
      return response.data;
    },
    refetchInterval: 60000, // 1 dakikada bir risk skoru çek
  });

  // Sort patients by risk (critical first)
  const sortedPatients = React.useMemo(() => {
    if (!patients || !riskScores) return patients || [];
    
    return [...patients].sort((a, b) => {
      const scoreA = riskScores.find((s) => s.userId === a.id)?.recoveryScore || 100;
      const scoreB = riskScores.find((s) => s.userId === b.id)?.recoveryScore || 100;
      return scoreA - scoreB; // Lower score = higher risk = first
    });
  }, [patients, riskScores]);

  const getRiskLevel = (score) => {
    if (score < 40) return { level: 'CRITICAL', color: 'error' };
    if (score < 60) return { level: 'HIGH', color: 'warning' };
    if (score < 80) return { level: 'MEDIUM', color: 'info' };
    return { level: 'LOW', color: 'success' };
  };

  const getPatientIoTData = (patientId) => {
    return iotData?.filter((data) => data.userId === patientId) || [];
  };

  const getPatientRiskScore = (patientId) => {
    return riskScores?.find((s) => s.userId === patientId);
  };

  const handleViewDetails = (patient) => {
    setSelectedPatient(patient);
    setOpenDetail(true);
  };

  const criticalPatients = sortedPatients?.filter((p) => {
    const score = getPatientRiskScore(p.id);
    return score && score.recoveryScore < 40;
  }) || [];

  if (isLoading) {
    return (
      <Box sx={{ p: 3 }}>
        <LinearProgress />
      </Box>
    );
  }

  return (
    <Container maxWidth="xl" sx={{ py: 4 }}>
      <Box sx={{ mb: 4, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Typography variant="h4" component="h1">
          {t('doctor.patientMonitoring')}
        </Typography>
        <IconButton onClick={() => refetch()} color="primary">
          <Refresh />
        </IconButton>
      </Box>

      {/* Critical Patients Alert */}
      {criticalPatients.length > 0 && (
        <Alert severity="error" sx={{ mb: 3 }} icon={<Warning />}>
          <Typography variant="h6">
            {criticalPatients.length} {t('doctor.criticalPatients')}
          </Typography>
          <Typography variant="body2">
            {t('doctor.criticalPatientsDesc')}
          </Typography>
        </Alert>
      )}

      {/* Statistics Cards */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Typography color="text.secondary" gutterBottom>
                {t('doctor.totalPatients')}
              </Typography>
              <Typography variant="h4">{patients?.length || 0}</Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Typography color="text.secondary" gutterBottom>
                {t('doctor.critical')}
              </Typography>
              <Typography variant="h4" color="error">
                {criticalPatients.length}
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Typography color="text.secondary" gutterBottom>
                {t('doctor.activeMonitoring')}
              </Typography>
              <Typography variant="h4" color="primary">
                {iotData?.length || 0}
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Typography color="text.secondary" gutterBottom>
                {t('doctor.avgRecoveryScore')}
              </Typography>
              <Typography variant="h4" color="success.main">
                {riskScores?.length > 0
                  ? Math.round(
                      riskScores.reduce((sum, s) => sum + s.recoveryScore, 0) /
                        riskScores.length
                    )
                  : 0}
              </Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Tabs */}
      <Box sx={{ borderBottom: 1, borderColor: 'divider', mb: 3 }}>
        <Tabs value={selectedTab} onChange={(e, v) => setSelectedTab(v)}>
          <Tab label={t('doctor.allPatients')} />
          <Tab
            label={`${t('doctor.critical')} (${criticalPatients.length})`}
            icon={criticalPatients.length > 0 ? <Warning color="error" /> : null}
          />
        </Tabs>
      </Box>

      {/* Patients Table */}
      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>{t('doctor.patientName')}</TableCell>
              <TableCell>{t('doctor.procedure')}</TableCell>
              <TableCell>{t('doctor.recoveryScore')}</TableCell>
              <TableCell>{t('doctor.riskLevel')}</TableCell>
              <TableCell>{t('doctor.lastUpdate')}</TableCell>
              <TableCell>{t('doctor.status')}</TableCell>
              <TableCell>{t('common.actions')}</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {(selectedTab === 0 ? sortedPatients : criticalPatients)?.map((patient) => {
              const riskScore = getPatientRiskScore(patient.id);
              const risk = riskScore ? getRiskLevel(riskScore.recoveryScore) : null;
              const latestIoT = getPatientIoTData(patient.id)?.[0];

              return (
                <TableRow
                  key={patient.id}
                  sx={{
                    bgcolor: risk?.level === 'CRITICAL' ? 'error.light' : 'transparent',
                  }}
                >
                  <TableCell>
                    <Typography variant="subtitle2">{patient.name}</Typography>
                    <Typography variant="caption" color="text.secondary">
                      {patient.email}
                    </Typography>
                  </TableCell>
                  <TableCell>{patient.procedureType || '-'}</TableCell>
                  <TableCell>
                    {riskScore ? (
                      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                        <Typography variant="h6">{riskScore.recoveryScore}</Typography>
                        {riskScore.trend === 'UP' ? (
                          <TrendingUp color="success" />
                        ) : (
                          <TrendingDown color="error" />
                        )}
                      </Box>
                    ) : (
                      '-'
                    )}
                  </TableCell>
                  <TableCell>
                    {risk ? (
                      <Chip
                        label={t(`doctor.risk.${risk.level}`)}
                        color={risk.color}
                        size="small"
                      />
                    ) : (
                      '-'
                    )}
                  </TableCell>
                  <TableCell>
                    {latestIoT
                      ? new Date(latestIoT.timestamp).toLocaleString()
                      : '-'}
                  </TableCell>
                  <TableCell>
                    <Chip
                      icon={latestIoT ? <CheckCircle /> : <Warning />}
                      label={latestIoT ? t('doctor.active') : t('doctor.inactive')}
                      color={latestIoT ? 'success' : 'warning'}
                      size="small"
                    />
                  </TableCell>
                  <TableCell>
                    <IconButton
                      onClick={() => handleViewDetails(patient)}
                      color="primary"
                    >
                      <Visibility />
                    </IconButton>
                  </TableCell>
                </TableRow>
              );
            })}
          </TableBody>
        </Table>
      </TableContainer>

      {/* Patient Detail Dialog */}
      <Dialog
        open={openDetail}
        onClose={() => setOpenDetail(false)}
        maxWidth="lg"
        fullWidth
      >
        {selectedPatient && (
          <>
            <DialogTitle>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <Typography variant="h6">
                  {selectedPatient.name} - {t('doctor.details')}
                </Typography>
                <Button
                  variant="outlined"
                  startIcon={<LocalHospital />}
                  href={`/tele-consultation/${selectedPatient.id}`}
                >
                  {t('doctor.startConsultation')}
                </Button>
              </Box>
            </DialogTitle>
            <DialogContent>
              <PatientDetailView
                patient={selectedPatient}
                iotData={getPatientIoTData(selectedPatient.id)}
                riskScore={getPatientRiskScore(selectedPatient.id)}
              />
            </DialogContent>
            <DialogActions>
              <Button onClick={() => setOpenDetail(false)}>{t('common.close')}</Button>
            </DialogActions>
          </>
        )}
      </Dialog>
    </Container>
  );
};

// Patient Detail View Component
const PatientDetailView = ({ patient, iotData, riskScore }) => {
  const { t } = useTranslation();

  const chartData = iotData?.slice(-24).map((data) => ({
    time: new Date(data.timestamp).toLocaleTimeString(),
    heartRate: data.heartRate,
    temperature: data.temperature,
    bloodPressure: data.bloodPressure?.systolic || 0,
  })) || [];

  return (
    <Box>
      <Grid container spacing={3}>
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                {t('doctor.vitalSigns')}
              </Typography>
              {iotData && iotData.length > 0 ? (
                <ResponsiveContainer width="100%" height={300}>
                  <LineChart data={chartData}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="time" />
                    <YAxis />
                    <Tooltip />
                    <Legend />
                    <Line
                      type="monotone"
                      dataKey="heartRate"
                      stroke="#8884d8"
                      name={t('doctor.heartRate')}
                    />
                    <Line
                      type="monotone"
                      dataKey="temperature"
                      stroke="#82ca9d"
                      name={t('doctor.temperature')}
                    />
                  </LineChart>
                </ResponsiveContainer>
              ) : (
                <Typography>{t('doctor.noData')}</Typography>
              )}
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                {t('doctor.recoveryScore')}
              </Typography>
              {riskScore ? (
                <Box>
                  <Typography variant="h3" color="primary">
                    {riskScore.recoveryScore}/100
                  </Typography>
                  <LinearProgress
                    variant="determinate"
                    value={riskScore.recoveryScore}
                    sx={{ height: 20, borderRadius: 10, mt: 2 }}
                  />
                  <Typography variant="body2" sx={{ mt: 1 }}>
                    {t('doctor.lastCalculated')}:{' '}
                    {new Date(riskScore.calculatedAt).toLocaleString()}
                  </Typography>
                </Box>
              ) : (
                <Typography>{t('doctor.noRiskScore')}</Typography>
              )}
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};

export default PatientMonitoringDashboard;



