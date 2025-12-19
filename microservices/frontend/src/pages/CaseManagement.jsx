import React, { useState, useEffect } from 'react';
import {
  Box,
  Container,
  Typography,
  Card,
  CardContent,
  Grid,
  Chip,
  Avatar,
  Divider,
  LinearProgress,
  Alert,
  Timeline,
  TimelineItem,
  TimelineSeparator,
  TimelineConnector,
  TimelineContent,
  TimelineDot,
  TimelineOppositeContent,
} from '@mui/material';
import {
  LocalHospital,
  Person,
  TrendingUp,
  TrendingDown,
  CheckCircle,
  Warning,
  Assignment,
  Medication,
} from '@mui/icons-material';
import { useTranslation } from 'react-i18next';
import { useQuery } from '@tanstack/react-query';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import api from '../services/api';
import DigitalTwinVisualization from '../components/DigitalTwin/DigitalTwinVisualization';

/**
 * Case Management (Vaka Yönetimi)
 * Hastanın tüm tıbbi yolculuğunu sosyal medya akışı gibi gösterir
 * En üstte AI Risk skoru, altında IoT verileri, en altta doktor notları
 */
const CaseManagement = () => {
  const { t } = useTranslation();
  const [selectedCase, setSelectedCase] = useState(null);

  // Fetch user cases
  const { data: cases, isLoading } = useQuery({
    queryKey: ['user-cases'],
    queryFn: async () => {
      const response = await api.get('/api/cases/user/me');
      return response.data;
    },
  });

  // Fetch AI risk score
  const { data: riskScore } = useQuery({
    queryKey: ['ai-risk-score', selectedCase?.id],
    queryFn: async () => {
      if (!selectedCase?.id) return null;
      const response = await api.get(`/api/patient-risk-scoring/user/${selectedCase.userId}`);
      return response.data;
    },
    enabled: !!selectedCase?.id,
    refetchInterval: 60000,
  });

  // Fetch IoT data
  const { data: iotData } = useQuery({
    queryKey: ['iot-data', selectedCase?.id],
    queryFn: async () => {
      if (!selectedCase?.id) return null;
      const response = await api.get(`/api/iot-monitoring/user/${selectedCase.userId}/recent?days=7`);
      return response.data;
    },
    enabled: !!selectedCase?.id,
    refetchInterval: 30000,
  });

  // Fetch doctor notes
  const { data: doctorNotes } = useQuery({
    queryKey: ['doctor-notes', selectedCase?.id],
    queryFn: async () => {
      if (!selectedCase?.id) return null;
      const response = await api.get(`/api/cases/${selectedCase.id}/notes`);
      return response.data;
    },
    enabled: !!selectedCase?.id,
  });

  useEffect(() => {
    if (cases && cases.length > 0 && !selectedCase) {
      setSelectedCase(cases[0]);
    }
  }, [cases, selectedCase]);

  if (isLoading) {
    return (
      <Box sx={{ p: 3 }}>
        <LinearProgress />
      </Box>
    );
  }

  if (!selectedCase) {
    return (
      <Container maxWidth="md" sx={{ py: 4, textAlign: 'center' }}>
        <Alert severity="info">
          <Typography>{t('caseManagement.noCase')}</Typography>
        </Alert>
      </Container>
    );
  }

  const chartData = iotData?.map((data) => ({
    date: new Date(data.timestamp).toLocaleDateString(),
    heartRate: data.heartRate,
    temperature: data.temperature,
    bloodPressure: data.bloodPressure?.systolic || 0,
  })) || [];

  return (
    <Container maxWidth="xl" sx={{ py: 4 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        {t('caseManagement.title')}
      </Typography>
      <Typography variant="body1" color="text.secondary" paragraph>
        {t('caseManagement.subtitle')}
      </Typography>

      <Grid container spacing={3}>
        {/* Left Sidebar - Cases List */}
        <Grid item xs={12} md={3}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                {t('caseManagement.myCases')}
              </Typography>
              <Box sx={{ maxHeight: 600, overflow: 'auto' }}>
                {cases?.map((caseItem) => (
                  <Card
                    key={caseItem.id}
                    sx={{
                      mb: 2,
                      cursor: 'pointer',
                      bgcolor: selectedCase?.id === caseItem.id ? 'action.selected' : 'transparent',
                      border: selectedCase?.id === caseItem.id ? '2px solid' : '1px solid',
                      borderColor: selectedCase?.id === caseItem.id ? 'primary.main' : 'divider',
                    }}
                    onClick={() => setSelectedCase(caseItem)}
                  >
                    <CardContent>
                      <Typography variant="subtitle1" gutterBottom>
                        {caseItem.title}
                      </Typography>
                      <Typography variant="caption" color="text.secondary">
                        {new Date(caseItem.startDate).toLocaleDateString()}
                      </Typography>
                      <Chip
                        label={caseItem.status}
                        size="small"
                        color={caseItem.status === 'ACTIVE' ? 'success' : 'default'}
                        sx={{ mt: 1 }}
                      />
                    </CardContent>
                  </Card>
                ))}
              </Box>
            </CardContent>
          </Card>
        </Grid>

        {/* Main Content - Social Media Feed Style */}
        <Grid item xs={12} md={9}>
          {/* AI Risk Score Card (Top) */}
          {riskScore && (
            <Card sx={{ mb: 3 }}>
              <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 2 }}>
                  <LocalHospital color="primary" />
                  <Typography variant="h6">{t('caseManagement.aiRiskScore')}</Typography>
                </Box>
                <Grid container spacing={2}>
                  <Grid item xs={12} md={6}>
                    <Box sx={{ textAlign: 'center' }}>
                      <Typography variant="h2" color="primary">
                        {riskScore.recoveryScore}/100
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        {t('caseManagement.recoveryScore')}
                      </Typography>
                      <Box sx={{ display: 'flex', justifyContent: 'center', gap: 1, mt: 1 }}>
                        {riskScore.trend === 'UP' ? (
                          <TrendingUp color="success" />
                        ) : (
                          <TrendingDown color="error" />
                        )}
                        <Typography variant="caption">
                          {riskScore.trend === 'UP' ? t('caseManagement.improving') : t('caseManagement.declining')}
                        </Typography>
                      </Box>
                    </Box>
                  </Grid>
                  <Grid item xs={12} md={6}>
                    <DigitalTwinVisualization
                      recoveryScore={riskScore.recoveryScore}
                      iotData={iotData}
                    />
                  </Grid>
                </Grid>
              </CardContent>
            </Card>
          )}

          {/* IoT Data Card */}
          {iotData && iotData.length > 0 && (
            <Card sx={{ mb: 3 }}>
              <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 2 }}>
                  <Medication color="primary" />
                  <Typography variant="h6">{t('caseManagement.iotData')}</Typography>
                </Box>
                <ResponsiveContainer width="100%" height={300}>
                  <LineChart data={chartData}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="date" />
                    <YAxis />
                    <Tooltip />
                    <Legend />
                    <Line
                      type="monotone"
                      dataKey="heartRate"
                      stroke="#8884d8"
                      name={t('caseManagement.heartRate')}
                    />
                    <Line
                      type="monotone"
                      dataKey="temperature"
                      stroke="#82ca9d"
                      name={t('caseManagement.temperature')}
                    />
                  </LineChart>
                </ResponsiveContainer>
              </CardContent>
            </Card>
          )}

          {/* Doctor Notes Timeline (Bottom) */}
          {doctorNotes && doctorNotes.length > 0 && (
            <Card>
              <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 2 }}>
                  <Person color="primary" />
                  <Typography variant="h6">{t('caseManagement.doctorNotes')}</Typography>
                </Box>
                <Timeline>
                  {doctorNotes.map((note, idx) => (
                    <TimelineItem key={note.id}>
                      <TimelineOppositeContent sx={{ flex: 0.2 }}>
                        <Typography variant="caption" color="text.secondary">
                          {new Date(note.timestamp).toLocaleString()}
                        </Typography>
                      </TimelineOppositeContent>
                      <TimelineSeparator>
                        <TimelineDot color="primary">
                          <Assignment />
                        </TimelineDot>
                        {idx < doctorNotes.length - 1 && <TimelineConnector />}
                      </TimelineSeparator>
                      <TimelineContent>
                        <Card variant="outlined">
                          <CardContent>
                            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
                              <Avatar sx={{ width: 32, height: 32 }}>
                                {note.doctorName.charAt(0)}
                              </Avatar>
                              <Typography variant="subtitle2">{note.doctorName}</Typography>
                            </Box>
                            <Typography variant="body1">{note.content}</Typography>
                            {note.attachments && note.attachments.length > 0 && (
                              <Box sx={{ mt: 1 }}>
                                {note.attachments.map((att, attIdx) => (
                                  <Chip
                                    key={attIdx}
                                    label={att.name}
                                    size="small"
                                    sx={{ mr: 0.5 }}
                                  />
                                ))}
                              </Box>
                            )}
                          </CardContent>
                        </Card>
                      </TimelineContent>
                    </TimelineItem>
                  ))}
                </Timeline>
              </CardContent>
            </Card>
          )}
        </Grid>
      </Grid>
    </Container>
  );
};

export default CaseManagement;

