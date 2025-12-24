// src/pages/AffiliateProgram.jsx
import React, { useState } from 'react';
import {
  Container,
  Box,
  Typography,
  Grid,
  Card,
  CardContent,
  Button,
  TextField,
  Alert,
  CircularProgress,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Chip,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  InputAdornment,
  Tooltip,
} from '@mui/material';
import {
  ContentCopy as ContentCopyIcon,
  Share as ShareIcon,
  TrendingUp as TrendingUpIcon,
  People as PeopleIcon,
  AttachMoney as AttachMoneyIcon,
  CheckCircle as CheckCircleIcon,
} from '@mui/icons-material';
import { useAuth } from '../hooks/useAuth';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { affiliateService } from '../services/api';
import ProtectedRoute from '../components/ProtectedRoute';
import Loading from '../components/Loading';
import { useTranslation } from '../i18n';
import { toast } from 'react-toastify';

export default function AffiliateProgram() {
  const { user } = useAuth();
  const { t } = useTranslation();
  const queryClient = useQueryClient();
  const [registerDialogOpen, setRegisterDialogOpen] = useState(false);

  // Register affiliate mutation
  const registerMutation = useMutation({
    mutationFn: () => affiliateService.register(user?.id),
    onSuccess: (data) => {
      queryClient.setQueryData(['affiliate', user?.id], data.data);
      queryClient.invalidateQueries(['affiliate', user?.id]);
      toast.success(t('affiliate.registered', 'Affiliate programına kaydınız tamamlandı'));
      setRegisterDialogOpen(false);
    },
    onError: (error) => {
      toast.error(error.message || t('affiliate.registerFailed', 'Kayıt başarısız'));
    },
  });

  // Fetch affiliate data
  const { data: affiliate, isLoading } = useQuery({
    queryKey: ['affiliate', user?.id],
    queryFn: async () => {
      // First try to get by user, if not exists, return null
      try {
        const response = await affiliateService.getByUser(user?.id);
        return response.data;
      } catch (error) {
        return null;
      }
    },
    enabled: !!user?.id,
  });

  // Fetch referrals
  const { data: referrals = [] } = useQuery({
    queryKey: ['referrals', affiliate?.id],
    queryFn: () => affiliateService.getReferrals(affiliate?.id),
    enabled: !!affiliate?.id,
    select: (response) => response?.data || [],
  });

  const referralLink = affiliate
    ? `${window.location.origin}?ref=${affiliate.referralCode}`
    : '';

  const handleCopyLink = () => {
    navigator.clipboard.writeText(referralLink);
    toast.success(t('affiliate.linkCopied', 'Referans linki kopyalandı'));
  };

  const handleShare = async () => {
    if (navigator.share) {
      try {
        await navigator.share({
          title: t('affiliate.shareTitle', 'Sağlık Turizmi Referans Programı'),
          text: t('affiliate.shareText', 'Bu linki kullanarak kayıt olun ve indirim kazanın!'),
          url: referralLink,
        });
      } catch (error) {
        // User cancelled share
      }
    } else {
      handleCopyLink();
    }
  };

  const totalEarnings = referrals
    .filter((r) => r.status === 'CONVERTED')
    .reduce((sum, r) => sum + (r.commissionAmount || 0), 0);

  const totalClicks = referrals.length;
  const totalConversions = referrals.filter((r) => r.status === 'CONVERTED').length;
  const conversionRate = totalClicks > 0 ? ((totalConversions / totalClicks) * 100).toFixed(1) : 0;

  if (isLoading) {
    return <Loading />;
  }

  return (
    <ProtectedRoute>
      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        <Box sx={{ mb: 4 }}>
          <Typography variant="h4" gutterBottom>
            {t('affiliate.title', 'Affiliate Programı')}
          </Typography>
          <Typography variant="body1" color="text.secondary">
            {t('affiliate.subtitle', 'Arkadaşlarınızı davet edin, komisyon kazanın')}
          </Typography>
        </Box>

        {!affiliate ? (
          <Card>
            <CardContent>
              <Box sx={{ textAlign: 'center', py: 4 }}>
                <Typography variant="h6" gutterBottom>
                  {t('affiliate.notRegistered', 'Henüz affiliate programına kayıtlı değilsiniz')}
                </Typography>
                <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
                  {t('affiliate.registerDesc', 'Kayıt olarak referans linkinizi alın ve komisyon kazanmaya başlayın')}
                </Typography>
                <Button
                  variant="contained"
                  onClick={() => setRegisterDialogOpen(true)}
                  disabled={registerMutation.isLoading}
                >
                  {registerMutation.isLoading ? (
                    <CircularProgress size={24} />
                  ) : (
                    t('affiliate.register', 'Kayıt Ol')
                  )}
                </Button>
              </Box>
            </CardContent>
          </Card>
        ) : (
          <>
            {/* Statistics Cards */}
            <Grid container spacing={3} sx={{ mb: 4 }}>
              <Grid item xs={12} sm={6} md={3}>
                <Card>
                  <CardContent>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
                      <PeopleIcon color="primary" />
                      <Typography variant="h6" color="text.secondary">
                        {t('affiliate.totalClicks', 'Toplam Tıklama')}
                      </Typography>
                    </Box>
                    <Typography variant="h4">{totalClicks}</Typography>
                  </CardContent>
                </Card>
              </Grid>
              <Grid item xs={12} sm={6} md={3}>
                <Card>
                  <CardContent>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
                      <CheckCircleIcon color="success" />
                      <Typography variant="h6" color="text.secondary">
                        {t('affiliate.conversions', 'Dönüşümler')}
                      </Typography>
                    </Box>
                    <Typography variant="h4">{totalConversions}</Typography>
                  </CardContent>
                </Card>
              </Grid>
              <Grid item xs={12} sm={6} md={3}>
                <Card>
                  <CardContent>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
                      <TrendingUpIcon color="info" />
                      <Typography variant="h6" color="text.secondary">
                        {t('affiliate.conversionRate', 'Dönüşüm Oranı')}
                      </Typography>
                    </Box>
                    <Typography variant="h4">{conversionRate}%</Typography>
                  </CardContent>
                </Card>
              </Grid>
              <Grid item xs={12} sm={6} md={3}>
                <Card>
                  <CardContent>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
                      <AttachMoneyIcon color="warning" />
                      <Typography variant="h6" color="text.secondary">
                        {t('affiliate.totalEarnings', 'Toplam Kazanç')}
                      </Typography>
                    </Box>
                    <Typography variant="h4">${totalEarnings.toFixed(2)}</Typography>
                  </CardContent>
                </Card>
              </Grid>
            </Grid>

            {/* Referral Link Card */}
            <Card sx={{ mb: 4 }}>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  {t('affiliate.referralLink', 'Referans Linkiniz')}
                </Typography>
                <Box sx={{ display: 'flex', gap: 1, mt: 2 }}>
                  <TextField
                    value={referralLink}
                    fullWidth
                    InputProps={{
                      readOnly: true,
                      endAdornment: (
                        <InputAdornment position="end">
                          <Tooltip title={t('affiliate.copy', 'Kopyala')}>
                            <IconButton onClick={handleCopyLink}>
                              <ContentCopyIcon />
                            </IconButton>
                          </Tooltip>
                          <Tooltip title={t('affiliate.share', 'Paylaş')}>
                            <IconButton onClick={handleShare}>
                              <ShareIcon />
                            </IconButton>
                          </Tooltip>
                        </InputAdornment>
                      ),
                    }}
                  />
                </Box>
                <Typography variant="body2" color="text.secondary" sx={{ mt: 2 }}>
                  {t('affiliate.referralCode', 'Referans Kodunuz')}: <strong>{affiliate.referralCode}</strong>
                </Typography>
              </CardContent>
            </Card>

            {/* Referrals Table */}
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  {t('affiliate.referrals', 'Referanslar')}
                </Typography>
                <TableContainer component={Paper} variant="outlined">
                  <Table>
                    <TableHead>
                      <TableRow>
                        <TableCell>{t('affiliate.date', 'Tarih')}</TableCell>
                        <TableCell>{t('affiliate.status', 'Durum')}</TableCell>
                        <TableCell>{t('affiliate.commission', 'Komisyon')}</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {referrals.length === 0 ? (
                        <TableRow>
                          <TableCell colSpan={3} align="center">
                            <Typography variant="body2" color="text.secondary">
                              {t('affiliate.noReferrals', 'Henüz referansınız bulunmamaktadır')}
                            </Typography>
                          </TableCell>
                        </TableRow>
                      ) : (
                        referrals.map((referral) => (
                          <TableRow key={referral.id}>
                            <TableCell>
                              {new Date(referral.createdAt).toLocaleDateString('tr-TR')}
                            </TableCell>
                            <TableCell>
                              <Chip
                                label={
                                  referral.status === 'CONVERTED'
                                    ? t('affiliate.converted', 'Dönüştü')
                                    : t('affiliate.pending', 'Beklemede')
                                }
                                color={referral.status === 'CONVERTED' ? 'success' : 'default'}
                                size="small"
                              />
                            </TableCell>
                            <TableCell>
                              {referral.commissionAmount
                                ? `$${referral.commissionAmount.toFixed(2)}`
                                : '-'}
                            </TableCell>
                          </TableRow>
                        ))
                      )}
                    </TableBody>
                  </Table>
                </TableContainer>
              </CardContent>
            </Card>
          </>
        )}

        {/* Register Dialog */}
        <Dialog open={registerDialogOpen} onClose={() => setRegisterDialogOpen(false)} maxWidth="sm" fullWidth>
          <DialogTitle>{t('affiliate.register', 'Affiliate Programına Kayıt Ol')}</DialogTitle>
          <DialogContent>
            <Alert severity="info" sx={{ mb: 2 }}>
              {t('affiliate.registerInfo', 'Kayıt olduktan sonra size özel bir referans kodu ve link verilecektir.')}
            </Alert>
            <Typography variant="body2" color="text.secondary">
              {t('affiliate.terms', 'Kayıt olarak şartları kabul etmiş olursunuz.')}
            </Typography>
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setRegisterDialogOpen(false)}>
              {t('common.cancel', 'İptal')}
            </Button>
            <Button
              onClick={() => registerMutation.mutate()}
              variant="contained"
              disabled={registerMutation.isLoading}
            >
              {registerMutation.isLoading ? (
                <CircularProgress size={24} />
              ) : (
                t('affiliate.register', 'Kayıt Ol')
              )}
            </Button>
          </DialogActions>
        </Dialog>
      </Container>
    </ProtectedRoute>
  );
}

