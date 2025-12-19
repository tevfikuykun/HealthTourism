import React, { useState } from 'react';
import {
  Box,
  Container,
  Typography,
  Grid,
  Card,
  CardContent,
  CardMedia,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Chip,
  IconButton,
  LinearProgress,
  Alert,
  Tabs,
  Tab,
} from '@mui/material';
import {
  Verified,
  Image,
  Close,
  ArrowBack,
  ArrowForward,
  Lock,
  CheckCircle,
} from '@mui/icons-material';
import { useTranslation } from 'react-i18next';
import { useQuery } from '@tanstack/react-query';
import api from '../services/api';

/**
 * Before & After Gallery
 * Blockchain mühürlü (manipüle edilmediği kanıtlanmış) vaka sonuçları
 */
const BeforeAfterGallery = () => {
  const { t } = useTranslation();
  const [selectedCase, setSelectedCase] = useState(null);
  const [openDialog, setOpenDialog] = useState(false);
  const [selectedTab, setSelectedTab] = useState(0);

  // Fetch cases
  const { data: cases, isLoading } = useQuery({
    queryKey: ['before-after-cases'],
    queryFn: async () => {
      const response = await api.get('/api/cases/before-after');
      return response.data;
    },
  });

  // Filter by category
  const filteredCases = React.useMemo(() => {
    if (!cases) return [];
    const categories = ['ALL', 'COSMETIC', 'DENTAL', 'CARDIAC', 'ORTHOPEDIC'];
    const category = categories[selectedTab];
    if (category === 'ALL') return cases;
    return cases.filter((c) => c.category === category);
  }, [cases, selectedTab]);

  const handleCaseClick = (caseItem) => {
    setSelectedCase(caseItem);
    setOpenDialog(true);
  };

  const verifyBlockchain = (caseItem) => {
    return caseItem.blockchainHash && caseItem.blockchainVerified;
  };

  return (
    <Container maxWidth="xl" sx={{ py: 4 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        {t('beforeAfter.title')}
      </Typography>
      <Typography variant="body1" color="text.secondary" paragraph>
        {t('beforeAfter.subtitle')}
      </Typography>

      {/* Blockchain Verification Notice */}
      <Alert severity="info" sx={{ mb: 3 }} icon={<Verified />}>
        <Typography variant="body2">
          {t('beforeAfter.blockchainNotice')}
        </Typography>
      </Alert>

      {/* Category Tabs */}
      <Box sx={{ borderBottom: 1, borderColor: 'divider', mb: 3 }}>
        <Tabs value={selectedTab} onChange={(e, v) => setSelectedTab(v)}>
          <Tab label={t('beforeAfter.all')} />
          <Tab label={t('beforeAfter.cosmetic')} />
          <Tab label={t('beforeAfter.dental')} />
          <Tab label={t('beforeAfter.cardiac')} />
          <Tab label={t('beforeAfter.orthopedic')} />
        </Tabs>
      </Box>

      {isLoading ? (
        <LinearProgress />
      ) : (
        <Grid container spacing={3}>
          {filteredCases.map((caseItem) => (
            <Grid item xs={12} sm={6} md={4} key={caseItem.id}>
              <Card
                sx={{
                  cursor: 'pointer',
                  transition: 'transform 0.2s',
                  '&:hover': {
                    transform: 'scale(1.02)',
                  },
                }}
                onClick={() => handleCaseClick(caseItem)}
              >
                <Box sx={{ position: 'relative' }}>
                  <CardMedia
                    component="img"
                    height="200"
                    image={caseItem.beforeImage || '/placeholder.jpg'}
                    alt={caseItem.title}
                  />
                  {verifyBlockchain(caseItem) && (
                    <Chip
                      icon={<Verified />}
                      label={t('beforeAfter.verified')}
                      color="success"
                      size="small"
                      sx={{
                        position: 'absolute',
                        top: 8,
                        right: 8,
                      }}
                    />
                  )}
                </Box>
                <CardContent>
                  <Typography variant="h6" gutterBottom>
                    {caseItem.title}
                  </Typography>
                  <Typography variant="body2" color="text.secondary" gutterBottom>
                    {caseItem.procedure}
                  </Typography>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 2 }}>
                    <Chip
                      label={caseItem.category}
                      size="small"
                      color="primary"
                      variant="outlined"
                    />
                    <Typography variant="caption" color="text.secondary">
                      {new Date(caseItem.date).toLocaleDateString()}
                    </Typography>
                  </Box>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}

      {/* Case Detail Dialog */}
      <Dialog
        open={openDialog}
        onClose={() => setOpenDialog(false)}
        maxWidth="lg"
        fullWidth
      >
        {selectedCase && (
          <>
            <DialogTitle>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <Typography variant="h6">{selectedCase.title}</Typography>
                <IconButton onClick={() => setOpenDialog(false)}>
                  <Close />
                </IconButton>
              </Box>
            </DialogTitle>
            <DialogContent>
              <Grid container spacing={3}>
                <Grid item xs={12} md={6}>
                  <Typography variant="subtitle1" gutterBottom>
                    {t('beforeAfter.before')}
                  </Typography>
                  <Box
                    component="img"
                    src={selectedCase.beforeImage}
                    alt="Before"
                    sx={{
                      width: '100%',
                      borderRadius: 2,
                      mb: 2,
                    }}
                  />
                </Grid>
                <Grid item xs={12} md={6}>
                  <Typography variant="subtitle1" gutterBottom>
                    {t('beforeAfter.after')}
                  </Typography>
                  <Box
                    component="img"
                    src={selectedCase.afterImage}
                    alt="After"
                    sx={{
                      width: '100%',
                      borderRadius: 2,
                      mb: 2,
                    }}
                  />
                </Grid>
              </Grid>

              <Box sx={{ mt: 3 }}>
                <Typography variant="h6" gutterBottom>
                  {t('beforeAfter.caseDetails')}
                </Typography>
                <Typography variant="body2" paragraph>
                  {selectedCase.description}
                </Typography>

                <Box sx={{ display: 'flex', gap: 2, mb: 2 }}>
                  <Chip
                    label={selectedCase.procedure}
                    color="primary"
                    size="small"
                  />
                  <Chip
                    label={selectedCase.duration}
                    size="small"
                  />
                  <Chip
                    label={selectedCase.recoveryTime}
                    size="small"
                  />
                </Box>

                {/* Blockchain Verification */}
                {verifyBlockchain(selectedCase) && (
                  <Alert severity="success" icon={<CheckCircle />} sx={{ mt: 2 }}>
                    <Typography variant="body2">
                      <strong>{t('beforeAfter.blockchainVerified')}</strong>
                    </Typography>
                    <Typography variant="caption" display="block">
                      {t('beforeAfter.blockchainHash')}: {selectedCase.blockchainHash?.substring(0, 16)}...
                    </Typography>
                    <Typography variant="caption" display="block">
                      {t('beforeAfter.verifiedDate')}: {new Date(selectedCase.verifiedDate).toLocaleString()}
                    </Typography>
                  </Alert>
                )}
              </Box>
            </DialogContent>
            <DialogActions>
              <Button onClick={() => setOpenDialog(false)}>{t('common.close')}</Button>
            </DialogActions>
          </>
        )}
      </Dialog>
    </Container>
  );
};

export default BeforeAfterGallery;
