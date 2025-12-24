// src/pages/Profile.jsx
import React, { useState, useEffect } from 'react';
import {
  Container,
  Box,
  Typography,
  Grid,
  Card,
  CardContent,
  TextField,
  Button,
  Avatar,
  Divider,
  Tabs,
  Tab,
  Paper,
  IconButton,
  Alert,
  Snackbar,
} from '@mui/material';
import {
  Person as PersonIcon,
  Email as EmailIcon,
  Phone as PhoneIcon,
  LocationOn as LocationIcon,
  Edit as EditIcon,
  Save as SaveIcon,
  Cancel as CancelIcon,
  Lock as LockIcon,
  Security as SecurityIcon,
  Notifications as NotificationsIcon,
  PhotoCamera as PhotoCameraIcon,
} from '@mui/icons-material';
import { useAuth } from '../hooks/useAuth';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { userService } from '../services/api';
import ProtectedRoute from '../components/ProtectedRoute';
import Loading from '../components/Loading';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';

function TabPanel({ children, value, index }) {
  return (
    <div role="tabpanel" hidden={value !== index}>
      {value === index && <Box sx={{ p: 3 }}>{children}</Box>}
    </div>
  );
}

export default function Profile() {
  const { t } = useTranslation();
  const { user, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [activeTab, setActiveTab] = useState(0);
  const [isEditing, setIsEditing] = useState(false);
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });

  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    country: '',
    city: '',
    address: '',
    dateOfBirth: '',
    gender: '',
    profilePicture: '',
  });

  const { data: profile, isLoading, error: profileError } = useQuery({
    queryKey: ['userProfile'],
    queryFn: async () => {
      try {
        const response = await userService.getProfile();
        // Axios automatically unwraps response.data
        // userService.getProfile() returns api.get() which returns { data: UserDTO }
        // So response is already the UserDTO object (axios unwraps it)
        console.log('=== PROFILE API RESPONSE DEBUG ===');
        console.log('Full response:', response);
        console.log('Response type:', typeof response);
        console.log('Response keys:', response ? Object.keys(response) : 'null');
        console.log('Response.data:', response?.data);
        console.log('Response.email:', response?.email);
        console.log('Response.firstName:', response?.firstName);
        console.log('Response.lastName:', response?.lastName);
        console.log('=== END DEBUG ===');
        
        // Return the data directly (axios already unwrapped it)
        return response;
      } catch (error) {
        console.error('Profile API error:', error);
        console.error('Error response:', error?.response);
        console.error('Error response data:', error?.response?.data);
        throw error;
      }
    },
    enabled: isAuthenticated,
    retry: 1,
    refetchOnWindowFocus: false,
  });

  const updateProfileMutation = useMutation({
    mutationFn: (data) => userService.updateProfile(data),
    onSuccess: () => {
      queryClient.invalidateQueries(['userProfile']);
      setIsEditing(false);
      setSnackbar({ open: true, message: t('profileUpdated', 'Profil başarıyla güncellendi'), severity: 'success' });
    },
    onError: (error) => {
      setSnackbar({ open: true, message: error.message || t('updateFailed', 'Güncelleme başarısız'), severity: 'error' });
    },
  });

  // Update formData when profile data is loaded
  useEffect(() => {
    if (isLoading) return; // Don't update while loading

    // Priority 1: Use profile data from API
    if (profile) {
      // Profile is already the UserDTO object (axios unwrapped response.data)
      const profileData = profile;
      console.log('=== SETTING FORMDATA FROM PROFILE ===');
      console.log('Profile object:', profileData);
      console.log('Profile keys:', Object.keys(profileData || {}));
      console.log('firstName:', profileData?.firstName);
      console.log('lastName:', profileData?.lastName);
      console.log('email:', profileData?.email);
      console.log('phone:', profileData?.phone);
      
      // Always set formData if we have profile data
      // Even if some fields are null/undefined, set them as empty strings
      if (profileData) {
        console.log('Setting formData from profile API');
        setFormData({
          firstName: profileData.firstName || '',
          lastName: profileData.lastName || '',
          email: profileData.email || '',
          phone: profileData.phone || '',
          country: profileData.country || '',
          city: profileData.city || '',
          address: profileData.address || '',
          dateOfBirth: profileData.dateOfBirth || '',
          gender: profileData.gender || '',
          profilePicture: profileData.profilePicture || '',
        });
        return;
      }
    }

    // Priority 2: Fallback to user data from auth context (only if no profile data)
    if (user && !profile && !isLoading) {
      console.log('Setting formData from user context (fallback):', user);
      setFormData(prev => {
        // Only update if formData is empty
        if (prev.firstName || prev.email) {
          return prev;
        }
        return {
          firstName: user.firstName || '',
          lastName: user.lastName || '',
          email: user.email || '',
          phone: user.phone || '',
          country: user.country || '',
          city: user.city || '',
          address: user.address || '',
          dateOfBirth: user.dateOfBirth || '',
          gender: user.gender || '',
          profilePicture: user.profilePicture || '',
        };
      });
    }
  }, [profile, isLoading, user]);

  // Show error message if profile failed to load
  useEffect(() => {
    if (profileError && !isLoading) {
      console.error('Profile load error:', profileError);
      setSnackbar({ 
        open: true, 
        message: t('profileLoadError', 'Profil yüklenirken bir hata oluştu. Kullanıcı bilgileri gösteriliyor.'), 
        severity: 'warning' 
      });
    }
  }, [profileError, isLoading, t]);

  if (!isAuthenticated) {
    return (
      <ProtectedRoute>
        <div />
      </ProtectedRoute>
    );
  }

  if (isLoading) {
    return <Loading message={t('loadingProfile', 'Profil yükleniyor...')} />;
  }

  const handleTabChange = (event, newValue) => {
    setActiveTab(newValue);
  };

  const handleInputChange = (field) => (event) => {
    setFormData({ ...formData, [field]: event.target.value });
  };

  const handleSave = () => {
    updateProfileMutation.mutate(formData);
  };

  const handleCancel = () => {
    setIsEditing(false);
    // Reset form data
    const profileData = profile?.data || profile;
    if (profileData) {
      setFormData({
        firstName: profileData.firstName || '',
        lastName: profileData.lastName || '',
        email: profileData.email || '',
        phone: profileData.phone || '',
        country: profileData.country || '',
        city: profileData.city || '',
        address: profileData.address || '',
        dateOfBirth: profileData.dateOfBirth || '',
        gender: profileData.gender || '',
        profilePicture: profileData.profilePicture || '',
      });
    }
  };

  const handleImageUpload = (event) => {
    const file = event.target.files[0];
    if (file) {
      // In production, upload to file storage service
      const reader = new FileReader();
      reader.onloadend = () => {
        setFormData({ ...formData, profilePicture: reader.result });
      };
      reader.readAsDataURL(file);
    }
  };

  // Get profile data for display (prioritize API data, fallback to user context)
  const profileData = profile || user || {};

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" gutterBottom>
          {t('profile', 'Profil')}
        </Typography>
        <Typography variant="body1" color="text.secondary">
          {t('manageProfile', 'Profil bilgilerinizi yönetin')}
        </Typography>
      </Box>

      <Grid container spacing={3}>
        {/* Sol Sidebar - Profil Özeti */}
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', mb: 2 }}>
                <Box sx={{ position: 'relative', mb: 2 }}>
                  <Avatar
                    src={formData.profilePicture || profileData.profilePicture}
                    sx={{ width: 120, height: 120, bgcolor: 'primary.main', fontSize: '3rem' }}
                  >
                    {(formData.firstName || profileData.firstName || 'U')?.charAt(0).toUpperCase()}
                  </Avatar>
                  {isEditing && (
                    <IconButton
                      sx={{
                        position: 'absolute',
                        bottom: 0,
                        right: 0,
                        bgcolor: 'primary.main',
                        color: 'white',
                        '&:hover': { bgcolor: 'primary.dark' },
                      }}
                      component="label"
                    >
                      <PhotoCameraIcon />
                      <input type="file" hidden accept="image/*" onChange={handleImageUpload} />
                    </IconButton>
                  )}
                </Box>
                <Typography variant="h5">
                  {formData.firstName || profileData.firstName} {formData.lastName || profileData.lastName}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  {formData.email || profileData.email}
                </Typography>
              </Box>
              <Divider sx={{ my: 2 }} />
              <Box>
                {!isEditing ? (
                  <Button
                    fullWidth
                    variant="contained"
                    startIcon={<EditIcon />}
                    onClick={() => setIsEditing(true)}
                  >
                    {t('editProfile', 'Profili Düzenle')}
                  </Button>
                ) : (
                  <>
                    <Button
                      fullWidth
                      variant="contained"
                      startIcon={<SaveIcon />}
                      onClick={handleSave}
                      sx={{ mb: 1 }}
                      disabled={updateProfileMutation.isLoading}
                    >
                      {t('save', 'Kaydet')}
                    </Button>
                    <Button
                      fullWidth
                      variant="outlined"
                      startIcon={<CancelIcon />}
                      onClick={handleCancel}
                    >
                      {t('cancel', 'İptal')}
                    </Button>
                  </>
                )}
              </Box>
            </CardContent>
          </Card>

          {/* Hızlı Erişim */}
          <Card sx={{ mt: 2 }}>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                {t('quickAccess', 'Hızlı Erişim')}
              </Typography>
              <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
                <Button
                  startIcon={<SecurityIcon />}
                  onClick={() => navigate('/security/2fa')}
                  sx={{ justifyContent: 'flex-start' }}
                >
                  {t('twoFactorAuth', 'İki Faktörlü Kimlik Doğrulama')}
                </Button>
                <Button
                  startIcon={<LockIcon />}
                  onClick={() => navigate('/reset-password')}
                  sx={{ justifyContent: 'flex-start' }}
                >
                  {t('changePassword', 'Şifre Değiştir')}
                </Button>
                <Button
                  startIcon={<NotificationsIcon />}
                  onClick={() => navigate('/notification-preferences')}
                  sx={{ justifyContent: 'flex-start' }}
                >
                  {t('notificationSettings', 'Bildirim Ayarları')}
                </Button>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        {/* Ana İçerik Alanı */}
        <Grid item xs={12} md={8}>
          <Paper>
            <Tabs value={activeTab} onChange={handleTabChange} variant="scrollable" scrollButtons="auto">
              <Tab icon={<PersonIcon />} label={t('personalInfo', 'Kişisel Bilgiler')} />
              <Tab icon={<SecurityIcon />} label={t('profile.security', 'Güvenlik')} />
              <Tab icon={<NotificationsIcon />} label={t('notifications', 'Bildirimler')} />
            </Tabs>

            <TabPanel value={activeTab} index={0}>
              <Typography variant="h6" gutterBottom>
                {t('personalInfo', 'Kişisel Bilgiler')}
              </Typography>
              <Grid container spacing={2} sx={{ mt: 1 }}>
                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    label={t('firstName', 'Ad')}
                    value={formData.firstName}
                    onChange={handleInputChange('firstName')}
                    disabled={!isEditing}
                    InputProps={{
                      startAdornment: <PersonIcon sx={{ mr: 1, color: 'text.secondary' }} />,
                    }}
                  />
                </Grid>
                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    label={t('lastName', 'Soyad')}
                    value={formData.lastName}
                    onChange={handleInputChange('lastName')}
                    disabled={!isEditing}
                    InputProps={{
                      startAdornment: <PersonIcon sx={{ mr: 1, color: 'text.secondary' }} />,
                    }}
                  />
                </Grid>
                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    label={t('email', 'E-posta')}
                    value={formData.email}
                    onChange={handleInputChange('email')}
                    disabled={!isEditing}
                    type="email"
                    InputProps={{
                      startAdornment: <EmailIcon sx={{ mr: 1, color: 'text.secondary' }} />,
                    }}
                  />
                </Grid>
                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    label={t('phone', 'Telefon')}
                    value={formData.phone}
                    onChange={handleInputChange('phone')}
                    disabled={!isEditing}
                    InputProps={{
                      startAdornment: <PhoneIcon sx={{ mr: 1, color: 'text.secondary' }} />,
                    }}
                  />
                </Grid>
                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    label={t('country', 'Ülke')}
                    value={formData.country}
                    onChange={handleInputChange('country')}
                    disabled={!isEditing}
                    InputProps={{
                      startAdornment: <LocationIcon sx={{ mr: 1, color: 'text.secondary' }} />,
                    }}
                  />
                </Grid>
                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    label={t('city', 'Şehir')}
                    value={formData.city}
                    onChange={handleInputChange('city')}
                    disabled={!isEditing}
                    InputProps={{
                      startAdornment: <LocationIcon sx={{ mr: 1, color: 'text.secondary' }} />,
                    }}
                  />
                </Grid>
                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    label={t('address', 'Adres')}
                    value={formData.address}
                    onChange={handleInputChange('address')}
                    disabled={!isEditing}
                    multiline
                    rows={3}
                  />
                </Grid>
                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    label={t('dateOfBirth', 'Doğum Tarihi')}
                    value={formData.dateOfBirth}
                    onChange={handleInputChange('dateOfBirth')}
                    disabled={!isEditing}
                    type="date"
                    InputLabelProps={{ shrink: true }}
                  />
                </Grid>
                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    select
                    label={t('gender', 'Cinsiyet')}
                    value={formData.gender}
                    onChange={handleInputChange('gender')}
                    disabled={!isEditing}
                    SelectProps={{ native: true }}
                  >
                    <option value="">{t('select', 'Seçiniz')}</option>
                    <option value="MALE">{t('male', 'Erkek')}</option>
                    <option value="FEMALE">{t('female', 'Kadın')}</option>
                    <option value="OTHER">{t('other', 'Diğer')}</option>
                  </TextField>
                </Grid>
              </Grid>
            </TabPanel>

            <TabPanel value={activeTab} index={1}>
              <Typography variant="h6" gutterBottom>
                {t('profile.security', 'Güvenlik')}
              </Typography>
              <Box sx={{ mt: 2 }}>
                <Card sx={{ mb: 2 }}>
                  <CardContent>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                      <Box>
                        <Typography variant="h6">{t('twoFactorAuth', 'İki Faktörlü Kimlik Doğrulama')}</Typography>
                        <Typography variant="body2" color="text.secondary">
                          {t('twoFactorAuthDesc', 'Hesabınızı ekstra güvenlik katmanı ile koruyun')}
                        </Typography>
                      </Box>
                      <Button variant="outlined" onClick={() => navigate('/security/2fa')}>
                        {t('configure', 'Yapılandır')}
                      </Button>
                    </Box>
                  </CardContent>
                </Card>
                <Card sx={{ mb: 2 }}>
                  <CardContent>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                      <Box>
                        <Typography variant="h6">{t('changePassword', 'Şifre Değiştir')}</Typography>
                        <Typography variant="body2" color="text.secondary">
                          {t('changePasswordDesc', 'Düzenli olarak şifrenizi güncelleyin')}
                        </Typography>
                      </Box>
                      <Button variant="outlined" onClick={() => navigate('/reset-password')}>
                        {t('change', 'Değiştir')}
                      </Button>
                    </Box>
                  </CardContent>
                </Card>
                <Card>
                  <CardContent>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                      <Box>
                        <Typography variant="h6">{t('biometricAuth', 'Biyometrik Kimlik Doğrulama')}</Typography>
                        <Typography variant="body2" color="text.secondary">
                          {t('biometricAuthDesc', 'Parmak izi veya yüz tanıma ile giriş yapın')}
                        </Typography>
                      </Box>
                      <Button variant="outlined" onClick={() => navigate('/security/biometric')}>
                        {t('configure', 'Yapılandır')}
                      </Button>
                    </Box>
                  </CardContent>
                </Card>
              </Box>
            </TabPanel>

            <TabPanel value={activeTab} index={2}>
              <Typography variant="h6" gutterBottom>
                {t('notifications', 'Bildirimler')}
              </Typography>
              <Box sx={{ mt: 2 }}>
                <Card>
                  <CardContent>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                      <Box>
                        <Typography variant="h6">{t('notificationPreferences', 'Bildirim Tercihleri')}</Typography>
                        <Typography variant="body2" color="text.secondary">
                          {t('manageNotifications', 'Bildirim ayarlarınızı yönetin')}
                        </Typography>
                      </Box>
                      <Button variant="outlined" onClick={() => navigate('/notification-preferences')}>
                        {t('manage', 'Yönet')}
                      </Button>
                    </Box>
                  </CardContent>
                </Card>
              </Box>
            </TabPanel>
          </Paper>
        </Grid>
      </Grid>

      <Snackbar
        open={snackbar.open}
        autoHideDuration={6000}
        onClose={() => setSnackbar({ ...snackbar, open: false })}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
      >
        <Alert severity={snackbar.severity} onClose={() => setSnackbar({ ...snackbar, open: false })}>
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Container>
  );
}

