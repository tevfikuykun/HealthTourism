import React, { useState, useEffect } from 'react';
import {
  Box,
  Container,
  Typography,
  Card,
  CardContent,
  Grid,
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
  Timeline,
  TimelineItem,
  TimelineSeparator,
  TimelineConnector,
  TimelineContent,
  TimelineDot,
  TimelineOppositeContent,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
} from '@mui/material';
import {
  CheckCircle,
  Warning,
  Error,
  Info,
  Refresh,
  TrendingDown,
  TrendingUp,
  AutoFixHigh,
  Speed,
  Security,
} from '@mui/icons-material';
import { useTranslation } from 'react-i18next';
import { useQuery } from '@tanstack/react-query';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, BarChart, Bar } from 'recharts';
import api from '../../services/api';

/**
 * Self-Healing Dashboard
 * Keptn entegrasyonu ile sistemin kendi kendini düzeltme loglarını gösterir
 * Yatırımcılar için "bakım maliyeti sıfıra yakın" mesajı
 */
const SelfHealingDashboard = () => {
  const { t } = useTranslation();
  const [selectedEvent, setSelectedEvent] = useState(null);
  const [openDetail, setOpenDetail] = useState(false);

  // Fetch self-healing events from Keptn
  const { data: healingEvents, isLoading, refetch } = useQuery({
    queryKey: ['self-healing-events'],
    queryFn: async () => {
      const response = await api.get('/api/keptn/self-healing-events');
      return response.data;
    },
    refetchInterval: 10000, // 10 saniyede bir güncelle
  });

  // Fetch system metrics
  const { data: metrics } = useQuery({
    queryKey: ['self-healing-metrics'],
    queryFn: async () => {
      const response = await api.get('/api/keptn/metrics');
      return response.data;
    },
    refetchInterval: 30000,
  });

  // Fetch cost savings
  const { data: costSavings } = useQuery({
    queryKey: ['cost-savings'],
    queryFn: async () => {
      const response = await api.get('/api/keptn/cost-savings');
      return response.data;
    },
  });

  const getEventIcon = (eventType) => {
    switch (eventType) {
      case 'AUTO_FIXED':
        return <CheckCircle color="success" />;
      case 'AUTO_SCALED':
        return <TrendingUp color="primary" />;
      case 'AUTO_ROLLBACK':
        return <TrendingDown color="warning" />;
      case 'AUTO_RESTART':
        return <Refresh color="info" />;
      default:
        return <Info />;
    }
  };

  const getEventColor = (eventType) => {
    switch (eventType) {
      case 'AUTO_FIXED':
        return 'success';
      case 'AUTO_SCALED':
        return 'primary';
      case 'AUTO_ROLLBACK':
        return 'warning';
      case 'AUTO_RESTART':
        return 'info';
      default:
        return 'default';
    }
  };

  const handleEventClick = (event) => {
    setSelectedEvent(event);
    setOpenDetail(true);
  };

  // Prepare chart data
  const chartData = healingEvents?.slice(-24).map((event) => ({
    time: new Date(event.timestamp).toLocaleTimeString(),
    fixed: event.type === 'AUTO_FIXED' ? 1 : 0,
    scaled: event.type === 'AUTO_SCALED' ? 1 : 0,
    rolledBack: event.type === 'AUTO_ROLLBACK' ? 1 : 0,
  })) || [];

  const eventTypeCounts = React.useMemo(() => {
    if (!healingEvents) return {};
    return healingEvents.reduce((acc, event) => {
      acc[event.type] = (acc[event.type] || 0) + 1;
      return acc;
    }, {});
  }, [healingEvents]);

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
          {t('selfHealing.title')}
        </Typography>
        <Button
          variant="outlined"
          startIcon={<Refresh />}
          onClick={() => refetch()}
        >
          {t('common.refresh')}
        </Button>
      </Box>

      <Typography variant="body1" color="text.secondary" paragraph>
        {t('selfHealing.subtitle')}
      </Typography>

      {/* Cost Savings Alert */}
      {costSavings && (
        <Alert severity="success" icon={<Security />} sx={{ mb: 3 }}>
          <Typography variant="h6">
            {t('selfHealing.costSavings')}: ${costSavings.totalSavings?.toLocaleString() || 0}
          </Typography>
          <Typography variant="body2">
            {t('selfHealing.costSavingsDesc', {
              incidents: costSavings.incidentsPrevented || 0,
              hours: costSavings.hoursSaved || 0,
            })}
          </Typography>
        </Alert>
      )}

      {/* Statistics Cards */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <Box>
                  <Typography color="text.secondary" gutterBottom>
                    {t('selfHealing.totalFixed')}
                  </Typography>
                  <Typography variant="h4" color="success.main">
                    {eventTypeCounts.AUTO_FIXED || 0}
                  </Typography>
                </Box>
                <CheckCircle sx={{ fontSize: 40, color: 'success.main' }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <Box>
                  <Typography color="text.secondary" gutterBottom>
                    {t('selfHealing.autoScaled')}
                  </Typography>
                  <Typography variant="h4" color="primary.main">
                    {eventTypeCounts.AUTO_SCALED || 0}
                  </Typography>
                </Box>
                <TrendingUp sx={{ fontSize: 40, color: 'primary.main' }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <Box>
                  <Typography color="text.secondary" gutterBottom>
                    {t('selfHealing.rollbacks')}
                  </Typography>
                  <Typography variant="h4" color="warning.main">
                    {eventTypeCounts.AUTO_ROLLBACK || 0}
                  </Typography>
                </Box>
                <TrendingDown sx={{ fontSize: 40, color: 'warning.main' }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <Box>
                  <Typography color="text.secondary" gutterBottom>
                    {t('selfHealing.uptime')}
                  </Typography>
                  <Typography variant="h4" color="success.main">
                    {metrics?.uptime || '99.9'}%
                  </Typography>
                </Box>
                <Speed sx={{ fontSize: 40, color: 'success.main' }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Charts */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                {t('selfHealing.eventsOverTime')}
              </Typography>
              <ResponsiveContainer width="100%" height={300}>
                <LineChart data={chartData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="time" />
                  <YAxis />
                  <Tooltip />
                  <Legend />
                  <Line type="monotone" dataKey="fixed" stroke="#4caf50" name={t('selfHealing.fixed')} />
                  <Line type="monotone" dataKey="scaled" stroke="#2196f3" name={t('selfHealing.scaled')} />
                  <Line type="monotone" dataKey="rolledBack" stroke="#ff9800" name={t('selfHealing.rolledBack')} />
                </LineChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                {t('selfHealing.eventTypes')}
              </Typography>
              <ResponsiveContainer width="100%" height={300}>
                <BarChart data={Object.entries(eventTypeCounts).map(([type, count]) => ({ type, count }))}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="type" />
                  <YAxis />
                  <Tooltip />
                  <Bar dataKey="count" fill="#2196f3" />
                </BarChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Self-Healing Events Timeline */}
      <Card>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            {t('selfHealing.recentEvents')}
          </Typography>
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>{t('selfHealing.timestamp')}</TableCell>
                  <TableCell>{t('selfHealing.eventType')}</TableCell>
                  <TableCell>{t('selfHealing.service')}</TableCell>
                  <TableCell>{t('selfHealing.action')}</TableCell>
                  <TableCell>{t('selfHealing.status')}</TableCell>
                  <TableCell>{t('common.actions')}</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {healingEvents?.slice(0, 20).map((event) => (
                  <TableRow key={event.id} hover>
                    <TableCell>
                      {new Date(event.timestamp).toLocaleString()}
                    </TableCell>
                    <TableCell>
                      <Chip
                        icon={getEventIcon(event.type)}
                        label={event.type}
                        color={getEventColor(event.type)}
                        size="small"
                      />
                    </TableCell>
                    <TableCell>{event.serviceName}</TableCell>
                    <TableCell>{event.action}</TableCell>
                    <TableCell>
                      <Chip
                        label={event.status}
                        color={event.status === 'SUCCESS' ? 'success' : 'error'}
                        size="small"
                      />
                    </TableCell>
                    <TableCell>
                      <Button
                        size="small"
                        onClick={() => handleEventClick(event)}
                      >
                        {t('common.viewDetails')}
                      </Button>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </CardContent>
      </Card>

      {/* Event Detail Dialog */}
      <Dialog
        open={openDetail}
        onClose={() => setOpenDetail(false)}
        maxWidth="md"
        fullWidth
      >
        {selectedEvent && (
          <>
            <DialogTitle>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                {getEventIcon(selectedEvent.type)}
                <Typography variant="h6">{selectedEvent.type}</Typography>
              </Box>
            </DialogTitle>
            <DialogContent>
              <List>
                <ListItem>
                  <ListItemIcon>
                    <Info />
                  </ListItemIcon>
                  <ListItemText
                    primary={t('selfHealing.service')}
                    secondary={selectedEvent.serviceName}
                  />
                </ListItem>
                <ListItem>
                  <ListItemIcon>
                    <AutoFixHigh />
                  </ListItemIcon>
                  <ListItemText
                    primary={t('selfHealing.action')}
                    secondary={selectedEvent.action}
                  />
                </ListItem>
                <ListItem>
                  <ListItemIcon>
                    <Info />
                  </ListItemIcon>
                  <ListItemText
                    primary={t('selfHealing.description')}
                    secondary={selectedEvent.description}
                  />
                </ListItem>
                <ListItem>
                  <ListItemIcon>
                    <CheckCircle />
                  </ListItemIcon>
                  <ListItemText
                    primary={t('selfHealing.result')}
                    secondary={selectedEvent.result}
                  />
                </ListItem>
                <ListItem>
                  <ListItemIcon>
                    <Speed />
                  </ListItemIcon>
                  <ListItemText
                    primary={t('selfHealing.responseTime')}
                    secondary={`${selectedEvent.responseTimeMs || 0}ms`}
                  />
                </ListItem>
              </List>
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

export default SelfHealingDashboard;

