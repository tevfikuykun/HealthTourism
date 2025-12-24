// src/pages/InfluencerManagement.jsx
import React, { useState } from 'react';
import {
  Container,
  Box,
  Typography,
  Grid,
  Card,
  CardContent,
  Button,
  Chip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
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
  IconButton,
  LinearProgress,
} from '@mui/material';
import {
  PersonAdd as PersonAddIcon,
  Campaign as CampaignIcon,
  TrendingUp as TrendingUpIcon,
  CheckCircle as CheckCircleIcon,
  Close as CloseIcon,
} from '@mui/icons-material';
import { useAuth } from '../hooks/useAuth';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { influencerService } from '../services/api';
import ProtectedRoute from '../components/ProtectedRoute';
import Loading from '../components/Loading';
import { useTranslation } from '../i18n';
import { toast } from 'react-toastify';

export default function InfluencerManagement() {
  const { user } = useAuth();
  const { t } = useTranslation();
  const queryClient = useQueryClient();
  const [registerDialogOpen, setRegisterDialogOpen] = useState(false);
  const [campaignDialogOpen, setCampaignDialogOpen] = useState(false);
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    socialMediaHandle: '',
    platform: 'INSTAGRAM',
    followerCount: 0,
  });
  const [campaignData, setCampaignData] = useState({
    title: '',
    description: '',
    budget: 0,
    targetAudience: '',
  });

  // Register influencer mutation
  const registerMutation = useMutation({
    mutationFn: (data) => influencerService.register(data),
    onSuccess: () => {
      queryClient.invalidateQueries(['influencers']);
      toast.success(t('influencer.registered', 'Influencer kaydı başarılı'));
      setRegisterDialogOpen(false);
      setFormData({ name: '', email: '', socialMediaHandle: '', platform: 'INSTAGRAM', followerCount: 0 });
    },
    onError: (error) => {
      toast.error(error.message || t('influencer.registerFailed', 'Kayıt başarısız'));
    },
  });

  // Create campaign mutation
  const createCampaignMutation = useMutation({
    mutationFn: (data) => influencerService.createCampaign(data),
    onSuccess: () => {
      queryClient.invalidateQueries(['campaigns']);
      toast.success(t('influencer.campaignCreated', 'Kampanya oluşturuldu'));
      setCampaignDialogOpen(false);
      setCampaignData({ title: '', description: '', budget: 0, targetAudience: '' });
    },
    onError: (error) => {
      toast.error(error.message || t('influencer.campaignFailed', 'Kampanya oluşturma başarısız'));
    },
  });

  const handleRegister = () => {
    registerMutation.mutate(formData);
  };

  const handleCreateCampaign = () => {
    createCampaignMutation.mutate({
      ...campaignData,
      influencerId: user?.id,
    });
  };

  return (
    <ProtectedRoute>
      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        <Box sx={{ mb: 4, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Box>
            <Typography variant="h4" gutterBottom>
              {t('influencer.title', 'Influencer Yönetimi')}
            </Typography>
            <Typography variant="body1" color="text.secondary">
              {t('influencer.subtitle', 'Influencer kayıtları ve kampanya yönetimi')}
            </Typography>
          </Box>
          <Box sx={{ display: 'flex', gap: 2 }}>
            <Button
              variant="outlined"
              startIcon={<CampaignIcon />}
              onClick={() => setCampaignDialogOpen(true)}
            >
              {t('influencer.createCampaign', 'Kampanya Oluştur')}
            </Button>
            <Button
              variant="contained"
              startIcon={<PersonAddIcon />}
              onClick={() => setRegisterDialogOpen(true)}
            >
              {t('influencer.register', 'Influencer Kaydet')}
            </Button>
          </Box>
        </Box>

        <Grid container spacing={3}>
          {/* Statistics Cards */}
          <Grid item xs={12} sm={6} md={3}>
            <Card>
              <CardContent>
                <Typography variant="h6" color="text.secondary" gutterBottom>
                  {t('influencer.totalInfluencers', 'Toplam Influencer')}
                </Typography>
                <Typography variant="h4">-</Typography>
              </CardContent>
            </Card>
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <Card>
              <CardContent>
                <Typography variant="h6" color="text.secondary" gutterBottom>
                  {t('influencer.activeCampaigns', 'Aktif Kampanyalar')}
                </Typography>
                <Typography variant="h4">-</Typography>
              </CardContent>
            </Card>
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <Card>
              <CardContent>
                <Typography variant="h6" color="text.secondary" gutterBottom>
                  {t('influencer.totalClicks', 'Toplam Tıklama')}
                </Typography>
                <Typography variant="h4">-</Typography>
              </CardContent>
            </Card>
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <Card>
              <CardContent>
                <Typography variant="h6" color="text.secondary" gutterBottom>
                  {t('influencer.totalCommission', 'Toplam Komisyon')}
                </Typography>
                <Typography variant="h4">-</Typography>
              </CardContent>
            </Card>
          </Grid>
        </Grid>

        {/* Register Dialog */}
        <Dialog open={registerDialogOpen} onClose={() => setRegisterDialogOpen(false)} maxWidth="sm" fullWidth>
          <DialogTitle>{t('influencer.register', 'Influencer Kaydet')}</DialogTitle>
          <DialogContent>
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
              <TextField
                label={t('influencer.name', 'Ad Soyad')}
                value={formData.name}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                fullWidth
              />
              <TextField
                label={t('influencer.email', 'E-posta')}
                type="email"
                value={formData.email}
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                fullWidth
              />
              <TextField
                label={t('influencer.socialHandle', 'Sosyal Medya Kullanıcı Adı')}
                value={formData.socialMediaHandle}
                onChange={(e) => setFormData({ ...formData, socialMediaHandle: e.target.value })}
                fullWidth
              />
              <TextField
                select
                label={t('influencer.platform', 'Platform')}
                value={formData.platform}
                onChange={(e) => setFormData({ ...formData, platform: e.target.value })}
                fullWidth
                SelectProps={{ native: true }}
              >
                <option value="INSTAGRAM">Instagram</option>
                <option value="YOUTUBE">YouTube</option>
                <option value="TIKTOK">TikTok</option>
                <option value="TWITTER">Twitter</option>
              </TextField>
              <TextField
                label={t('influencer.followerCount', 'Takipçi Sayısı')}
                type="number"
                value={formData.followerCount}
                onChange={(e) => setFormData({ ...formData, followerCount: parseInt(e.target.value) || 0 })}
                fullWidth
              />
            </Box>
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setRegisterDialogOpen(false)}>
              {t('common.cancel', 'İptal')}
            </Button>
            <Button
              onClick={handleRegister}
              variant="contained"
              disabled={registerMutation.isLoading}
            >
              {registerMutation.isLoading ? (
                <CircularProgress size={24} />
              ) : (
                t('common.save', 'Kaydet')
              )}
            </Button>
          </DialogActions>
        </Dialog>

        {/* Campaign Dialog */}
        <Dialog open={campaignDialogOpen} onClose={() => setCampaignDialogOpen(false)} maxWidth="md" fullWidth>
          <DialogTitle>{t('influencer.createCampaign', 'Kampanya Oluştur')}</DialogTitle>
          <DialogContent>
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
              <TextField
                label={t('influencer.campaignTitle', 'Kampanya Başlığı')}
                value={campaignData.title}
                onChange={(e) => setCampaignData({ ...campaignData, title: e.target.value })}
                fullWidth
              />
              <TextField
                label={t('influencer.description', 'Açıklama')}
                value={campaignData.description}
                onChange={(e) => setCampaignData({ ...campaignData, description: e.target.value })}
                fullWidth
                multiline
                rows={4}
              />
              <TextField
                label={t('influencer.budget', 'Bütçe')}
                type="number"
                value={campaignData.budget}
                onChange={(e) => setCampaignData({ ...campaignData, budget: parseFloat(e.target.value) || 0 })}
                fullWidth
              />
              <TextField
                label={t('influencer.targetAudience', 'Hedef Kitle')}
                value={campaignData.targetAudience}
                onChange={(e) => setCampaignData({ ...campaignData, targetAudience: e.target.value })}
                fullWidth
              />
            </Box>
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setCampaignDialogOpen(false)}>
              {t('common.cancel', 'İptal')}
            </Button>
            <Button
              onClick={handleCreateCampaign}
              variant="contained"
              disabled={createCampaignMutation.isLoading}
            >
              {createCampaignMutation.isLoading ? (
                <CircularProgress size={24} />
              ) : (
                t('common.create', 'Oluştur')
              )}
            </Button>
          </DialogActions>
        </Dialog>
      </Container>
    </ProtectedRoute>
  );
}

