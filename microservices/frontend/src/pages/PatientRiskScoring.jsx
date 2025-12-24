// src/pages/PatientRiskScoring.jsx
import React, { useState } from 'react';
import {
  Container,
  Box,
  Typography,
  Grid,
  Card,
  CardContent,
  LinearProgress,
  Chip,
  Alert,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Divider,
} from '@mui/material';
import {
  Warning as WarningIcon,
  CheckCircle as CheckCircleIcon,
  Error as ErrorIcon,
  TrendingUp as TrendingUpIcon,
} from '@mui/icons-material';
import { useQuery } from '@tanstack/react-query';
import { patientRiskScoringService } from '../services/api';
import { useAuth } from '../hooks/useAuth';
import ProtectedRoute from '../components/ProtectedRoute';
import Loading from '../components/Loading';
import { useTranslation } from 'react-i18next';

export default function PatientRiskScoring() {
  const { t } = useTranslation();
  const { user, isAuthenticated } = useAuth();
  const [selectedReservationId, setSelectedReservationId] = useState(null);

  const { data: latestScore, isLoading } = useQuery({
    queryKey: ['patientRiskScore', user?.id, selectedReservationId],
    queryFn: () => patientRiskScoringService.getLatestScore(user?.id, selectedReservationId),
    enabled: isAuthenticated && !!user?.id,
  });

  const { data: scoreHistory } = useQuery({
    queryKey: ['patientRiskScoreHistory', user?.id, selectedReservationId],
    queryFn: () => patientRiskScoringService.getScoreHistory(user?.id, selectedReservationId),
    enabled: isAuthenticated && !!user?.id && !!selectedReservationId,
  });

  if (!isAuthenticated) {
    return (
      <ProtectedRoute>
        <div />
      </ProtectedRoute>
    );
  }

  if (isLoading) {
    return <Loading message={t('loadingRiskScore', 'Risk skoru yükleniyor...')} />;
  }

  const getRiskLevel = (score) => {
    if (score >= 80) return { level: 'HIGH', color: 'error', icon: <ErrorIcon />, label: t('highRisk', 'Yüksek Risk') };
    if (score >= 50) return { level: 'MEDIUM', color: 'warning', icon: <WarningIcon />, label: t('mediumRisk', 'Orta Risk') };
    return { level: 'LOW', color: 'success', icon: <CheckCircleIcon />, label: t('lowRisk', 'Düşük Risk') };
  };

  const riskInfo = latestScore?.data ? getRiskLevel(latestScore.data.riskScore) : null;

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" gutterBottom>
          {t('patientRiskScoring', 'Hasta Risk Skorlama')}
        </Typography>
        <Typography variant="body1" color="text.secondary">
          {t('riskScoringDescription', 'Sağlık durumunuzun risk analizi ve takibi')}
        </Typography>
      </Box>

      {latestScore?.data ? (
        <Grid container spacing={3}>
          {/* Risk Skoru Özeti */}
          <Grid item xs={12} md={4}>
            <Card>
              <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                  {riskInfo?.icon}
                  <Typography variant="h6" sx={{ ml: 1 }}>
                    {t('currentRiskScore', 'Mevcut Risk Skoru')}
                  </Typography>
                </Box>
                <Divider sx={{ mb: 2 }} />
                <Box sx={{ textAlign: 'center', mb: 2 }}>
                  <Typography variant="h2" color={`${riskInfo?.color}.main`}>
                    {latestScore.data.riskScore}
                  </Typography>
                  <Chip
                    label={riskInfo?.label}
                    color={riskInfo?.color}
                    sx={{ mt: 1 }}
                  />
                </Box>
                <LinearProgress
                  variant="determinate"
                  value={latestScore.data.riskScore}
                  color={riskInfo?.color}
                  sx={{ height: 10, borderRadius: 5 }}
                />
                <Typography variant="body2" color="text.secondary" sx={{ mt: 2 }}>
                  {t('lastUpdated', 'Son Güncelleme')}: {new Date(latestScore.data.calculatedAt).toLocaleDateString('tr-TR')}
                </Typography>
              </CardContent>
            </Card>
          </Grid>

          {/* Risk Faktörleri */}
          <Grid item xs={12} md={8}>
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  {t('riskFactors', 'Risk Faktörleri')}
                </Typography>
                <Divider sx={{ mb: 2 }} />
                {latestScore.data.riskFactors?.map((factor, index) => (
                  <Box key={index} sx={{ mb: 2 }}>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                      <Typography variant="body1">{factor.name}</Typography>
                      <Chip
                        label={`${factor.contribution}%`}
                        size="small"
                        color={factor.contribution > 30 ? 'error' : factor.contribution > 15 ? 'warning' : 'default'}
                      />
                    </Box>
                    <LinearProgress
                      variant="determinate"
                      value={factor.contribution}
                      color={factor.contribution > 30 ? 'error' : factor.contribution > 15 ? 'warning' : 'default'}
                    />
                  </Box>
                ))}
              </CardContent>
            </Card>
          </Grid>

          {/* Öneriler */}
          {latestScore.data.recommendations && (
            <Grid item xs={12}>
              <Card>
                <CardContent>
                  <Typography variant="h6" gutterBottom>
                    {t('recommendations', 'Öneriler')}
                  </Typography>
                  <Divider sx={{ mb: 2 }} />
                  <Alert severity={riskInfo?.level === 'HIGH' ? 'error' : riskInfo?.level === 'MEDIUM' ? 'warning' : 'info'}>
                    {latestScore.data.recommendations}
                  </Alert>
                </CardContent>
              </Card>
            </Grid>
          )}

          {/* Skor Geçmişi */}
          {scoreHistory?.data && scoreHistory.data.length > 0 && (
            <Grid item xs={12}>
              <Card>
                <CardContent>
                  <Typography variant="h6" gutterBottom>
                    {t('scoreHistory', 'Skor Geçmişi')}
                  </Typography>
                  <Divider sx={{ mb: 2 }} />
                  <TableContainer>
                    <Table>
                      <TableHead>
                        <TableRow>
                          <TableCell>{t('date', 'Tarih')}</TableCell>
                          <TableCell>{t('riskScore', 'Risk Skoru')}</TableCell>
                          <TableCell>{t('riskLevel', 'Risk Seviyesi')}</TableCell>
                          <TableCell>{t('trend', 'Trend')}</TableCell>
                        </TableRow>
                      </TableHead>
                      <TableBody>
                        {scoreHistory.data.map((score, index) => {
                          const trend = index > 0
                            ? score.riskScore - scoreHistory.data[index - 1].riskScore
                            : 0;
                          const trendInfo = getRiskLevel(score.riskScore);
                          return (
                            <TableRow key={index}>
                              <TableCell>{new Date(score.calculatedAt).toLocaleDateString('tr-TR')}</TableCell>
                              <TableCell>
                                <Typography variant="h6" color={`${trendInfo.color}.main`}>
                                  {score.riskScore}
                                </Typography>
                              </TableCell>
                              <TableCell>
                                <Chip label={trendInfo.label} color={trendInfo.color} size="small" />
                              </TableCell>
                              <TableCell>
                                {trend !== 0 && (
                                  <Chip
                                    icon={<TrendingUpIcon />}
                                    label={trend > 0 ? `+${trend}` : trend}
                                    color={trend > 0 ? 'error' : 'success'}
                                    size="small"
                                  />
                                )}
                              </TableCell>
                            </TableRow>
                          );
                        })}
                      </TableBody>
                    </Table>
                  </TableContainer>
                </CardContent>
              </Card>
            </Grid>
          )}
        </Grid>
      ) : (
        <Alert severity="info">
          {t('noRiskScore', 'Henüz risk skoru hesaplanmamış. Rezervasyon yaptıktan sonra risk skoru otomatik olarak hesaplanacaktır.')}
        </Alert>
      )}
    </Container>
  );
}

