import React, { useState } from 'react';
import {
  Box,
  Container,
  Typography,
  Card,
  CardContent,
  Grid,
  Chip,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  LinearProgress,
  Alert,
  Tabs,
  Tab,
  Button,
  IconButton,
  Tooltip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  List,
  ListItem,
  ListItemText,
  Divider,
  Accordion,
  AccordionSummary,
  AccordionDetails,
} from '@mui/material';
import {
  Security,
  History,
  Timeline,
  VerifiedUser,
  Warning,
  CheckCircle,
  Info,
  Refresh,
  Download,
  Visibility,
  ExpandMore,
  FilterList,
  Search,
} from '@mui/icons-material';
import { useTranslation } from '../../i18n';
import { useQuery } from '@tanstack/react-query';
import { auditService } from '../../services/api';
import { useAuth } from '../../hooks/useAuth';

/**
 * Transparency & Audit Center - Şeffaflık ve Denetim Merkezi
 * Hibernate Envers ve Blockchain loglarının kullanıcıya sunulması
 */
const TransparencyAuditCenter = () => {
  const { t } = useTranslation();
  const { user } = useAuth();
  const [tabValue, setTabValue] = useState(0);
  const [selectedLog, setSelectedLog] = useState(null);
  const [openDialog, setOpenDialog] = useState(false);
  const [filters, setFilters] = useState({
    resourceType: '',
    action: '',
    userId: '',
    dateFrom: '',
    dateTo: '',
  });
  const [searchTerm, setSearchTerm] = useState('');

  // Audit Logs
  const { data: auditLogs, isLoading: logsLoading, refetch: refetchLogs } = useQuery({
    queryKey: ['audit-logs', filters],
    queryFn: async () => {
      const response = await auditService.getAuditLogs(filters);
      return response.data;
    },
    refetchInterval: 60000, // 1 dakikada bir güncelle
  });

  // Data Access Logs
  const { data: dataAccessLogs, isLoading: accessLoading } = useQuery({
    queryKey: ['data-access-logs', filters],
    queryFn: async () => {
      const response = await auditService.getDataAccessLogs(filters);
      return response.data;
    },
    refetchInterval: 60000,
  });

  // User Traces
  const { data: userTraces, isLoading: tracesLoading } = useQuery({
    queryKey: ['user-traces', user?.id],
    queryFn: async () => {
      if (!user?.id) return null;
      const response = await auditService.getTracesByUser(user.id);
      return response.data;
    },
    enabled: !!user?.id,
  });

  const handleViewLog = (log) => {
    setSelectedLog(log);
    setOpenDialog(true);
  };

  const handleExportLogs = async () => {
    try {
      const response = await auditService.exportAuditLogs(filters);
      const blob = new Blob([response.data], { type: 'application/json' });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `audit-logs-${new Date().toISOString()}.json`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
    } catch (error) {
      console.error('Export failed:', error);
    }
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleString();
  };

  const getActionColor = (action) => {
    switch (action) {
      case 'CREATE':
        return 'success';
      case 'UPDATE':
        return 'info';
      case 'DELETE':
        return 'error';
      case 'READ':
        return 'default';
      default:
        return 'default';
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'SUCCESS':
        return 'success';
      case 'FAILED':
        return 'error';
      case 'PENDING':
        return 'warning';
      default:
        return 'default';
    }
  };

  if (logsLoading || accessLoading || tracesLoading) {
    return (
      <Box sx={{ py: 4 }}>
        <LinearProgress />
      </Box>
    );
  }

  return (
    <Container maxWidth="xl" sx={{ py: 4 }}>
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" sx={{ fontWeight: 700, mb: 1 }}>
          {t('audit.title', 'Şeffaflık ve Denetim Merkezi')}
        </Typography>
        <Typography variant="body1" color="text.secondary">
          {t('audit.subtitle', 'Verilerinizin kim tarafından, ne zaman işlendiğini görüntüleyin')}
        </Typography>
      </Box>

      {/* Security Overview */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <Security sx={{ fontSize: 40, color: 'primary.main', mr: 2 }} />
                <Box>
                  <Typography variant="body2" color="text.secondary">
                    {t('audit.totalAccess', 'Toplam Erişim')}
                  </Typography>
                  <Typography variant="h5" sx={{ fontWeight: 700 }}>
                    {auditLogs?.total || 0}
                  </Typography>
                </Box>
              </Box>
              <Typography variant="caption" color="text.secondary">
                {t('audit.last30Days', 'Son 30 Gün')}
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <VerifiedUser sx={{ fontSize: 40, color: 'success.main', mr: 2 }} />
                <Box>
                  <Typography variant="body2" color="text.secondary">
                    {t('audit.verifiedChanges', 'Doğrulanmış Değişiklikler')}
                  </Typography>
                  <Typography variant="h5" sx={{ fontWeight: 700 }}>
                    {auditLogs?.verified || 0}
                  </Typography>
                </Box>
              </Box>
              <Typography variant="caption" color="text.secondary">
                {t('audit.blockchainVerified', 'Blockchain Doğrulandı')}
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <Timeline sx={{ fontSize: 40, color: 'info.main', mr: 2 }} />
                <Box>
                  <Typography variant="body2" color="text.secondary">
                    {t('audit.traceRecords', 'Trace Kayıtları')}
                  </Typography>
                  <Typography variant="h5" sx={{ fontWeight: 700 }}>
                    {userTraces?.length || 0}
                  </Typography>
                </Box>
              </Box>
              <Typography variant="caption" color="text.secondary">
                {t('audit.distributedTracing', 'Dağıtık İzleme')}
              </Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Filters */}
      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Grid container spacing={2} alignItems="center">
            <Grid item xs={12} md={3}>
              <TextField
                fullWidth
                size="small"
                label={t('audit.search', 'Ara')}
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                InputProps={{
                  startAdornment: <Search sx={{ mr: 1, color: 'text.secondary' }} />,
                }}
              />
            </Grid>
            <Grid item xs={12} md={2}>
              <FormControl fullWidth size="small">
                <InputLabel>{t('audit.resourceType', 'Kaynak Türü')}</InputLabel>
                <Select
                  value={filters.resourceType}
                  onChange={(e) => setFilters({ ...filters, resourceType: e.target.value })}
                >
                  <MenuItem value="">{t('common.all', 'Tümü')}</MenuItem>
                  <MenuItem value="PATIENT">Patient</MenuItem>
                  <MenuItem value="RESERVATION">Reservation</MenuItem>
                  <MenuItem value="PAYMENT">Payment</MenuItem>
                  <MenuItem value="MEDICAL_RECORD">Medical Record</MenuItem>
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12} md={2}>
              <FormControl fullWidth size="small">
                <InputLabel>{t('audit.action', 'İşlem')}</InputLabel>
                <Select
                  value={filters.action}
                  onChange={(e) => setFilters({ ...filters, action: e.target.value })}
                >
                  <MenuItem value="">{t('common.all', 'Tümü')}</MenuItem>
                  <MenuItem value="CREATE">Create</MenuItem>
                  <MenuItem value="UPDATE">Update</MenuItem>
                  <MenuItem value="DELETE">Delete</MenuItem>
                  <MenuItem value="READ">Read</MenuItem>
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12} md={2}>
              <TextField
                fullWidth
                size="small"
                type="date"
                label={t('audit.dateFrom', 'Başlangıç')}
                value={filters.dateFrom}
                onChange={(e) => setFilters({ ...filters, dateFrom: e.target.value })}
                InputLabelProps={{ shrink: true }}
              />
            </Grid>
            <Grid item xs={12} md={2}>
              <TextField
                fullWidth
                size="small"
                type="date"
                label={t('audit.dateTo', 'Bitiş')}
                value={filters.dateTo}
                onChange={(e) => setFilters({ ...filters, dateTo: e.target.value })}
                InputLabelProps={{ shrink: true }}
              />
            </Grid>
            <Grid item xs={12} md={1}>
              <Button
                fullWidth
                variant="contained"
                startIcon={<Download />}
                onClick={handleExportLogs}
              >
                {t('audit.export', 'Dışa Aktar')}
              </Button>
            </Grid>
          </Grid>
        </CardContent>
      </Card>

      {/* Tabs */}
      <Card>
        <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
          <Tabs value={tabValue} onChange={(e, newValue) => setTabValue(newValue)}>
            <Tab label={t('audit.auditLogs', 'Denetim Logları')} />
            <Tab label={t('audit.dataAccess', 'Veri Erişim Logları')} />
            <Tab label={t('audit.traces', 'Trace Kayıtları')} />
            <Tab label={t('audit.blockchain', 'Blockchain Hash')} />
          </Tabs>
        </Box>

        <CardContent>
          {/* Audit Logs Tab */}
          {tabValue === 0 && (
            <TableContainer component={Paper} variant="outlined">
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell>{t('audit.timestamp', 'Zaman Damgası')}</TableCell>
                    <TableCell>{t('audit.user', 'Kullanıcı')}</TableCell>
                    <TableCell>{t('audit.action', 'İşlem')}</TableCell>
                    <TableCell>{t('audit.resource', 'Kaynak')}</TableCell>
                    <TableCell>{t('audit.status', 'Durum')}</TableCell>
                    <TableCell>{t('audit.blockchainHash', 'Blockchain Hash')}</TableCell>
                    <TableCell align="right">{t('audit.actions', 'İşlemler')}</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {auditLogs?.logs?.map((log) => (
                    <TableRow key={log.id}>
                      <TableCell>{formatDate(log.timestamp)}</TableCell>
                      <TableCell>{log.userEmail || log.userId}</TableCell>
                      <TableCell>
                        <Chip
                          label={log.action}
                          size="small"
                          color={getActionColor(log.action)}
                        />
                      </TableCell>
                      <TableCell>
                        <Box>
                          <Typography variant="body2">{log.resourceType}</Typography>
                          <Typography variant="caption" color="text.secondary">
                            ID: {log.resourceId}
                          </Typography>
                        </Box>
                      </TableCell>
                      <TableCell>
                        <Chip
                          icon={log.status === 'SUCCESS' ? <CheckCircle /> : <Warning />}
                          label={log.status}
                          size="small"
                          color={getStatusColor(log.status)}
                        />
                      </TableCell>
                      <TableCell>
                        {log.blockchainHash ? (
                          <Tooltip title={log.blockchainHash}>
                            <Typography variant="body2" sx={{ fontFamily: 'monospace', fontSize: '0.7rem' }}>
                              {log.blockchainHash.substring(0, 10)}...
                            </Typography>
                          </Tooltip>
                        ) : (
                          <Chip label={t('audit.pending', 'Beklemede')} size="small" />
                        )}
                      </TableCell>
                      <TableCell align="right">
                        <Tooltip title={t('audit.viewDetails', 'Detayları Gör')}>
                          <IconButton size="small" onClick={() => handleViewLog(log)}>
                            <Visibility />
                          </IconButton>
                        </Tooltip>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          )}

          {/* Data Access Logs Tab */}
          {tabValue === 1 && (
            <TableContainer component={Paper} variant="outlined">
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell>{t('audit.timestamp', 'Zaman Damgası')}</TableCell>
                    <TableCell>{t('audit.user', 'Kullanıcı')}</TableCell>
                    <TableCell>{t('audit.dataType', 'Veri Türü')}</TableCell>
                    <TableCell>{t('audit.purpose', 'Amaç')}</TableCell>
                    <TableCell>{t('audit.ipAddress', 'IP Adresi')}</TableCell>
                    <TableCell>{t('audit.status', 'Durum')}</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {dataAccessLogs?.map((log) => (
                    <TableRow key={log.id}>
                      <TableCell>{formatDate(log.timestamp)}</TableCell>
                      <TableCell>{log.userEmail || log.userId}</TableCell>
                      <TableCell>{log.dataType}</TableCell>
                      <TableCell>{log.purpose}</TableCell>
                      <TableCell>
                        <Typography variant="body2" sx={{ fontFamily: 'monospace' }}>
                          {log.ipAddress}
                        </Typography>
                      </TableCell>
                      <TableCell>
                        <Chip
                          label={log.status}
                          size="small"
                          color={getStatusColor(log.status)}
                        />
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          )}

          {/* Traces Tab */}
          {tabValue === 2 && (
            <Box>
              {userTraces?.map((trace) => (
                <Accordion key={trace.traceId} sx={{ mb: 1 }}>
                  <AccordionSummary expandIcon={<ExpandMore />}>
                    <Box sx={{ display: 'flex', alignItems: 'center', width: '100%' }}>
                      <Timeline sx={{ mr: 2, color: 'primary.main' }} />
                      <Box sx={{ flexGrow: 1 }}>
                        <Typography variant="subtitle1" sx={{ fontWeight: 600 }}>
                          {trace.traceId}
                        </Typography>
                        <Typography variant="caption" color="text.secondary">
                          {formatDate(trace.startTime)} - {trace.duration}ms
                        </Typography>
                      </Box>
                      <Chip
                        label={trace.status}
                        size="small"
                        color={trace.status === 'SUCCESS' ? 'success' : 'error'}
                        sx={{ mr: 2 }}
                      />
                    </Box>
                  </AccordionSummary>
                  <AccordionDetails>
                    <List>
                      {trace.spans?.map((span, index) => (
                        <React.Fragment key={index}>
                          <ListItem>
                            <ListItemText
                              primary={span.serviceName}
                              secondary={
                                <Box>
                                  <Typography variant="caption" display="block">
                                    {span.operation} - {span.duration}ms
                                  </Typography>
                                  {span.tags && (
                                    <Box sx={{ mt: 1 }}>
                                      {Object.entries(span.tags).map(([key, value]) => (
                                        <Chip
                                          key={key}
                                          label={`${key}: ${value}`}
                                          size="small"
                                          sx={{ mr: 0.5, mb: 0.5 }}
                                        />
                                      ))}
                                    </Box>
                                  )}
                                </Box>
                              }
                            />
                          </ListItem>
                          {index < trace.spans.length - 1 && <Divider />}
                        </React.Fragment>
                      ))}
                    </List>
                  </AccordionDetails>
                </Accordion>
              ))}
            </Box>
          )}

          {/* Blockchain Hash Tab */}
          {tabValue === 3 && (
            <Alert severity="info">
              <Typography variant="subtitle2" sx={{ mb: 1 }}>
                {t('audit.blockchainVerification', 'Blockchain Doğrulama')}
              </Typography>
              <Typography variant="body2">
                {t('audit.blockchainInfo', 'Tüm kritik veri değişiklikleri blockchain üzerinde mühürlenir ve değiştirilemez hale getirilir.')}
              </Typography>
            </Alert>
          )}
        </CardContent>
      </Card>

      {/* Log Detail Dialog */}
      <Dialog open={openDialog} onClose={() => setOpenDialog(false)} maxWidth="md" fullWidth>
        <DialogTitle>
          {t('audit.logDetails', 'Log Detayları')}
        </DialogTitle>
        <DialogContent>
          {selectedLog && (
            <Box>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                {t('audit.timestamp', 'Zaman Damgası')}
              </Typography>
              <Typography variant="body1" sx={{ mb: 2 }}>
                {formatDate(selectedLog.timestamp)}
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                {t('audit.user', 'Kullanıcı')}
              </Typography>
              <Typography variant="body1" sx={{ mb: 2 }}>
                {selectedLog.userEmail || selectedLog.userId}
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                {t('audit.action', 'İşlem')}
              </Typography>
              <Typography variant="body1" sx={{ mb: 2 }}>
                {selectedLog.action}
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                {t('audit.resource', 'Kaynak')}
              </Typography>
              <Typography variant="body1" sx={{ mb: 2 }}>
                {selectedLog.resourceType} - {selectedLog.resourceId}
              </Typography>
              {selectedLog.blockchainHash && (
                <>
                  <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                    {t('audit.blockchainHash', 'Blockchain Hash')}
                  </Typography>
                  <Typography variant="body1" sx={{ fontFamily: 'monospace', fontSize: '0.875rem', mb: 2 }}>
                    {selectedLog.blockchainHash}
                  </Typography>
                </>
              )}
              {selectedLog.changes && (
                <>
                  <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                    {t('audit.changes', 'Değişiklikler')}
                  </Typography>
                  <Paper variant="outlined" sx={{ p: 2, mb: 2 }}>
                    <pre style={{ margin: 0, fontSize: '0.875rem' }}>
                      {JSON.stringify(selectedLog.changes, null, 2)}
                    </pre>
                  </Paper>
                </>
              )}
            </Box>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDialog(false)}>
            {t('common.close', 'Kapat')}
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default TransparencyAuditCenter;

