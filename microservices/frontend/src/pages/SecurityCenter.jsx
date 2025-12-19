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
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Divider,
  Switch,
  FormControlLabel,
} from '@mui/material';
import {
  Security,
  VpnKey,
  Lock,
  VerifiedUser,
  Warning,
  CheckCircle,
  Info,
  Refresh,
  Delete,
  Visibility,
  VisibilityOff,
  LockOpen,
  Shield,
  Quantum,
} from '@mui/icons-material';
import { useTranslation } from '../i18n';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { securityService } from '../services/api';
import { useAuth } from '../hooks/useAuth';

/**
 * Security Center - Güvenlik Merkezi
 * Quantum-Safe anahtar yönetimi, aktif oturumlar (Zero-Trust), veriye erişim yetkileri
 */
const SecurityCenter = () => {
  const { t } = useTranslation();
  const { user } = useAuth();
  const queryClient = useQueryClient();
  const [tabValue, setTabValue] = useState(0);
  const [selectedSession, setSelectedSession] = useState(null);
  const [openDialog, setOpenDialog] = useState(false);
  const [showKeys, setShowKeys] = useState({});

  // Active Sessions
  const { data: activeSessions, isLoading: sessionsLoading, refetch: refetchSessions } = useQuery({
    queryKey: ['security-sessions', user?.id],
    queryFn: async () => {
      const response = await securityService.getActiveSessions(user?.id);
      return response.data;
    },
    enabled: !!user?.id,
    refetchInterval: 30000, // 30 saniyede bir güncelle
  });

  // Access Permissions
  const { data: permissions, isLoading: permissionsLoading } = useQuery({
    queryKey: ['security-permissions', user?.id],
    queryFn: async () => {
      const response = await securityService.getAccessPermissions(user?.id);
      return response.data;
    },
    enabled: !!user?.id,
  });

  // Key Rotation Status
  const { data: keyRotationStatus, isLoading: keyRotationLoading } = useQuery({
    queryKey: ['security-key-rotation'],
    queryFn: async () => {
      const response = await securityService.getKeyRotationStatus();
      return response.data;
    },
    refetchInterval: 60000, // 1 dakikada bir güncelle
  });

  // Quantum-Safe Keys
  const { data: quantumKeys, isLoading: quantumKeysLoading } = useQuery({
    queryKey: ['security-quantum-keys'],
    queryFn: async () => {
      const response = await securityService.getQuantumSafeKeys();
      return response.data;
    },
  });

  // Zero-Trust Status
  const { data: zeroTrustStatus, isLoading: zeroTrustLoading } = useQuery({
    queryKey: ['security-zero-trust'],
    queryFn: async () => {
      const response = await securityService.getZeroTrustStatus();
      return response.data;
    },
    refetchInterval: 30000,
  });

  // Revoke Session Mutation
  const revokeSessionMutation = useMutation({
    mutationFn: securityService.revokeSession,
    onSuccess: () => {
      queryClient.invalidateQueries(['security-sessions']);
    },
  });

  // Revoke All Sessions Mutation
  const revokeAllSessionsMutation = useMutation({
    mutationFn: () => securityService.revokeAllSessions(user?.id),
    onSuccess: () => {
      queryClient.invalidateQueries(['security-sessions']);
    },
  });

  // Rotate Key Mutation
  const rotateKeyMutation = useMutation({
    mutationFn: securityService.rotateKey,
    onSuccess: () => {
      queryClient.invalidateQueries(['security-key-rotation']);
      queryClient.invalidateQueries(['security-quantum-keys']);
    },
  });

  const handleRevokeSession = (sessionId) => {
    if (window.confirm(t('security.confirmRevoke', 'Bu oturumu sonlandırmak istediğinizden emin misiniz?'))) {
      revokeSessionMutation.mutate(sessionId);
    }
  };

  const handleRevokeAllSessions = () => {
    if (window.confirm(t('security.confirmRevokeAll', 'Tüm oturumları sonlandırmak istediğinizden emin misiniz?'))) {
      revokeAllSessionsMutation.mutate();
    }
  };

  const handleRotateKey = (keyId) => {
    if (window.confirm(t('security.confirmRotate', 'Anahtarı döndürmek istediğinizden emin misiniz?'))) {
      rotateKeyMutation.mutate(keyId);
    }
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleString();
  };

  const getSessionStatusColor = (status) => {
    switch (status) {
      case 'ACTIVE':
        return 'success';
      case 'EXPIRED':
        return 'error';
      case 'REVOKED':
        return 'warning';
      default:
        return 'default';
    }
  };

  if (sessionsLoading || permissionsLoading || keyRotationLoading || quantumKeysLoading || zeroTrustLoading) {
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
          {t('security.title', 'Güvenlik Merkezi')}
        </Typography>
        <Typography variant="body1" color="text.secondary">
          {t('security.subtitle', 'Quantum-Safe anahtar yönetimi, aktif oturumlar ve veri erişim yetkileri')}
        </Typography>
      </Box>

      {/* Zero-Trust Status */}
      {zeroTrustStatus && (
        <Alert
          severity={zeroTrustStatus.enabled ? 'success' : 'warning'}
          sx={{ mb: 3 }}
          icon={<Shield />}
        >
          <Typography variant="subtitle2" sx={{ fontWeight: 600 }}>
            {zeroTrustStatus.enabled
              ? t('security.zeroTrustEnabled', 'Zero-Trust Güvenlik Aktif')
              : t('security.zeroTrustDisabled', 'Zero-Trust Güvenlik Pasif')}
          </Typography>
          <Typography variant="body2">
            {zeroTrustStatus.description || t('security.zeroTrustDesc', 'Her istek doğrulanır ve yetkilendirilir')}
          </Typography>
        </Alert>
      )}

      {/* Security Overview Cards */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <VerifiedUser sx={{ fontSize: 40, color: 'primary.main', mr: 2 }} />
                <Box>
                  <Typography variant="body2" color="text.secondary">
                    {t('security.activeSessions', 'Aktif Oturumlar')}
                  </Typography>
                  <Typography variant="h5" sx={{ fontWeight: 700 }}>
                    {activeSessions?.filter((s) => s.status === 'ACTIVE').length || 0}
                  </Typography>
                </Box>
              </Box>
              <Typography variant="caption" color="text.secondary">
                {t('security.totalSessions', 'Toplam')}: {activeSessions?.length || 0}
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <VpnKey sx={{ fontSize: 40, color: 'success.main', mr: 2 }} />
                <Box>
                  <Typography variant="body2" color="text.secondary">
                    {t('security.quantumSafeKeys', 'Quantum-Safe Anahtarlar')}
                  </Typography>
                  <Typography variant="h5" sx={{ fontWeight: 700 }}>
                    {quantumKeys?.length || 0}
                  </Typography>
                </Box>
              </Box>
              <Typography variant="caption" color="text.secondary">
                {t('security.lastRotation', 'Son Rotasyon')}: {keyRotationStatus?.lastRotation ? formatDate(keyRotationStatus.lastRotation) : t('security.never', 'Hiç')}
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <Lock sx={{ fontSize: 40, color: 'info.main', mr: 2 }} />
                <Box>
                  <Typography variant="body2" color="text.secondary">
                    {t('security.accessPermissions', 'Erişim Yetkileri')}
                  </Typography>
                  <Typography variant="h5" sx={{ fontWeight: 700 }}>
                    {permissions?.length || 0}
                  </Typography>
                </Box>
              </Box>
              <Typography variant="caption" color="text.secondary">
                {t('security.resources', 'Kaynak')}: {permissions?.filter((p) => p.granted).length || 0} {t('security.granted', 'İzinli')}
              </Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Tabs */}
      <Card>
        <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
          <Tabs value={tabValue} onChange={(e, newValue) => setTabValue(newValue)}>
            <Tab label={t('security.activeSessions', 'Aktif Oturumlar')} />
            <Tab label={t('security.quantumKeys', 'Quantum-Safe Anahtarlar')} />
            <Tab label={t('security.accessPermissions', 'Erişim Yetkileri')} />
            <Tab label={t('security.keyRotation', 'Anahtar Rotasyonu')} />
          </Tabs>
        </Box>

        <CardContent>
          {/* Active Sessions Tab */}
          {tabValue === 0 && (
            <Box>
              <Box sx={{ display: 'flex', justifyContent: 'flex-end', mb: 2 }}>
                <Button
                  variant="outlined"
                  color="error"
                  startIcon={<Delete />}
                  onClick={handleRevokeAllSessions}
                  disabled={revokeAllSessionsMutation.isPending}
                >
                  {t('security.revokeAll', 'Tümünü Sonlandır')}
                </Button>
              </Box>
              <TableContainer component={Paper} variant="outlined">
                <Table>
                  <TableHead>
                    <TableRow>
                      <TableCell>{t('security.device', 'Cihaz')}</TableCell>
                      <TableCell>{t('security.ipAddress', 'IP Adresi')}</TableCell>
                      <TableCell>{t('security.location', 'Konum')}</TableCell>
                      <TableCell>{t('security.lastActivity', 'Son Aktivite')}</TableCell>
                      <TableCell>{t('security.status', 'Durum')}</TableCell>
                      <TableCell align="right">{t('security.actions', 'İşlemler')}</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {activeSessions?.map((session) => (
                      <TableRow key={session.id}>
                        <TableCell>
                          <Box>
                            <Typography variant="body2">{session.deviceName || session.userAgent}</Typography>
                            <Typography variant="caption" color="text.secondary">
                              {session.browser || 'Unknown'}
                            </Typography>
                          </Box>
                        </TableCell>
                        <TableCell>
                          <Typography variant="body2" sx={{ fontFamily: 'monospace' }}>
                            {session.ipAddress}
                          </Typography>
                        </TableCell>
                        <TableCell>{session.location || t('security.unknown', 'Bilinmiyor')}</TableCell>
                        <TableCell>{formatDate(session.lastActivity)}</TableCell>
                        <TableCell>
                          <Chip
                            label={session.status}
                            size="small"
                            color={getSessionStatusColor(session.status)}
                          />
                        </TableCell>
                        <TableCell align="right">
                          <Tooltip title={t('security.revoke', 'Oturumu Sonlandır')}>
                            <IconButton
                              size="small"
                              color="error"
                              onClick={() => handleRevokeSession(session.id)}
                              disabled={revokeSessionMutation.isPending}
                            >
                              <Delete />
                            </IconButton>
                          </Tooltip>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            </Box>
          )}

          {/* Quantum-Safe Keys Tab */}
          {tabValue === 1 && (
            <TableContainer component={Paper} variant="outlined">
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell>{t('security.keyName', 'Anahtar Adı')}</TableCell>
                    <TableCell>{t('security.algorithm', 'Algoritma')}</TableCell>
                    <TableCell>{t('security.keyType', 'Tür')}</TableCell>
                    <TableCell>{t('security.lastRotation', 'Son Rotasyon')}</TableCell>
                    <TableCell>{t('security.nextRotation', 'Sonraki Rotasyon')}</TableCell>
                    <TableCell align="right">{t('security.actions', 'İşlemler')}</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {quantumKeys?.map((key) => (
                    <TableRow key={key.id}>
                      <TableCell>
                        <Box sx={{ display: 'flex', alignItems: 'center' }}>
                          <Quantum sx={{ fontSize: 20, color: 'primary.main', mr: 1 }} />
                          {key.name}
                        </Box>
                      </TableCell>
                      <TableCell>
                        <Chip label={key.algorithm} size="small" color="primary" />
                      </TableCell>
                      <TableCell>
                        <Chip
                          label={key.type}
                          size="small"
                          color={key.type === 'QUANTUM_SAFE' ? 'success' : 'default'}
                        />
                      </TableCell>
                      <TableCell>{formatDate(key.lastRotation)}</TableCell>
                      <TableCell>{formatDate(key.nextRotation)}</TableCell>
                      <TableCell align="right">
                        <Tooltip title={t('security.rotate', 'Anahtarı Döndür')}>
                          <IconButton
                            size="small"
                            color="primary"
                            onClick={() => handleRotateKey(key.id)}
                            disabled={rotateKeyMutation.isPending}
                          >
                            <Refresh />
                          </IconButton>
                        </Tooltip>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          )}

          {/* Access Permissions Tab */}
          {tabValue === 2 && (
            <TableContainer component={Paper} variant="outlined">
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell>{t('security.resource', 'Kaynak')}</TableCell>
                    <TableCell>{t('security.permission', 'Yetki')}</TableCell>
                    <TableCell>{t('security.granted', 'İzinli')}</TableCell>
                    <TableCell>{t('security.grantedAt', 'İzin Tarihi')}</TableCell>
                    <TableCell>{t('security.expiresAt', 'Son Geçerlilik')}</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {permissions?.map((permission) => (
                    <TableRow key={permission.id}>
                      <TableCell>{permission.resourceType}</TableCell>
                      <TableCell>
                        <Chip label={permission.permission} size="small" />
                      </TableCell>
                      <TableCell>
                        <Chip
                          icon={permission.granted ? <CheckCircle /> : <Warning />}
                          label={permission.granted ? t('security.yes', 'Evet') : t('security.no', 'Hayır')}
                          size="small"
                          color={permission.granted ? 'success' : 'error'}
                        />
                      </TableCell>
                      <TableCell>{formatDate(permission.grantedAt)}</TableCell>
                      <TableCell>{permission.expiresAt ? formatDate(permission.expiresAt) : t('security.never', 'Hiç')}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          )}

          {/* Key Rotation Tab */}
          {tabValue === 3 && (
            <Box>
              {keyRotationStatus && (
                <Card variant="outlined" sx={{ mb: 3 }}>
                  <CardContent>
                    <Typography variant="h6" sx={{ mb: 2 }}>
                      {t('security.rotationStatus', 'Rotasyon Durumu')}
                    </Typography>
                    <Grid container spacing={2}>
                      <Grid item xs={12} md={6}>
                        <Typography variant="body2" color="text.secondary">
                          {t('security.lastRotation', 'Son Rotasyon')}
                        </Typography>
                        <Typography variant="body1" sx={{ fontWeight: 600 }}>
                          {keyRotationStatus.lastRotation ? formatDate(keyRotationStatus.lastRotation) : t('security.never', 'Hiç')}
                        </Typography>
                      </Grid>
                      <Grid item xs={12} md={6}>
                        <Typography variant="body2" color="text.secondary">
                          {t('security.nextRotation', 'Sonraki Rotasyon')}
                        </Typography>
                        <Typography variant="body1" sx={{ fontWeight: 600 }}>
                          {keyRotationStatus.nextRotation ? formatDate(keyRotationStatus.nextRotation) : t('security.notScheduled', 'Planlanmamış')}
                        </Typography>
                      </Grid>
                      <Grid item xs={12}>
                        <Typography variant="body2" color="text.secondary">
                          {t('security.rotationInterval', 'Rotasyon Aralığı')}
                        </Typography>
                        <Typography variant="body1" sx={{ fontWeight: 600 }}>
                          {keyRotationStatus.interval || '30 days'}
                        </Typography>
                      </Grid>
                    </Grid>
                  </CardContent>
                </Card>
              )}
              <Alert severity="info">
                {t('security.rotationInfo', 'Anahtar rotasyonu otomatik olarak yapılır. Manuel rotasyon için yukarıdaki Quantum-Safe Anahtarlar sekmesini kullanın.')}
              </Alert>
            </Box>
          )}
        </CardContent>
      </Card>
    </Container>
  );
};

export default SecurityCenter;


