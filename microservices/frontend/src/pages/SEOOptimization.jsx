// src/pages/SEOOptimization.jsx
import React, { useState } from 'react';
import {
  Container, Box, Typography, Paper, Grid, TextField, Button,
  Card, CardContent, Alert
} from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import SaveIcon from '@mui/icons-material/Save';
import { useTranslation } from 'react-i18next';

const SEOOptimization = () => {
  const { t } = useTranslation();
  const [seoSettings, setSeoSettings] = useState({
    title: 'Health Tourism - İstanbul Sağlık Turizmi',
    description: 'İstanbul\'da sağlık turizmi hizmetleri. Hastaneler, doktorlar, konaklama ve rezervasyon.',
    keywords: 'sağlık turizmi, istanbul, hastane, doktor, tedavi',
    ogTitle: 'Health Tourism - İstanbul Sağlık Turizmi',
    ogDescription: 'İstanbul\'da sağlık turizmi hizmetleri',
    ogImage: '',
  });

  return (
    <Container maxWidth="md" sx={{ py: 4 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 4 }}>
        <SearchIcon sx={{ fontSize: 40, color: 'primary.main' }} />
        <Box>
          <Typography variant="h4">{t('seoOptimization', 'SEO Optimizasyonu')}</Typography>
          <Typography variant="body2" color="text.secondary">
            {t('seoSettingsDescription', 'Arama motoru optimizasyonu ayarları')}
          </Typography>
        </Box>
      </Box>

      <Alert severity="info" sx={{ mb: 3 }}>
        {t('seoSettingsInfo', 'SEO ayarları arama motorlarında daha iyi görünmenizi sağlar.')}
      </Alert>

      <Paper sx={{ p: 3 }}>
        <Grid container spacing={3}>
          <Grid item xs={12}>
            <TextField
              fullWidth
              label={t('pageTitle', 'Sayfa Başlığı (Title)')}
              value={seoSettings.title}
              onChange={(e) => setSeoSettings({ ...seoSettings, title: e.target.value })}
              helperText={`${seoSettings.title.length}/60 ${t('characters', 'karakter')}`}
            />
          </Grid>
          <Grid item xs={12}>
            <TextField
              fullWidth
              label={t('metaDescription', 'Meta Açıklama')}
              multiline
              rows={3}
              value={seoSettings.description}
              onChange={(e) => setSeoSettings({ ...seoSettings, description: e.target.value })}
              helperText={`${seoSettings.description.length}/160 ${t('characters', 'karakter')}`}
            />
          </Grid>
          <Grid item xs={12}>
            <TextField
              fullWidth
              label={t('keywords', 'Anahtar Kelimeler')}
              value={seoSettings.keywords}
              onChange={(e) => setSeoSettings({ ...seoSettings, keywords: e.target.value })}
              helperText={t('separateWithComma', 'Virgülle ayırın')}
            />
          </Grid>
          <Grid item xs={12}>
            <Typography variant="h6" gutterBottom>
              {t('openGraphSocialMedia', 'Open Graph (Sosyal Medya)')}
            </Typography>
          </Grid>
          <Grid item xs={12}>
            <TextField
              fullWidth
              label={t('ogTitle', 'OG Başlık')}
              value={seoSettings.ogTitle}
              onChange={(e) => setSeoSettings({ ...seoSettings, ogTitle: e.target.value })}
            />
          </Grid>
          <Grid item xs={12}>
            <TextField
              fullWidth
              label={t('ogDescription', 'OG Açıklama')}
              multiline
              rows={2}
              value={seoSettings.ogDescription}
              onChange={(e) => setSeoSettings({ ...seoSettings, ogDescription: e.target.value })}
            />
          </Grid>
          <Grid item xs={12}>
            <TextField
              fullWidth
              label={t('ogImageURL', 'OG Görsel URL')}
              value={seoSettings.ogImage}
              onChange={(e) => setSeoSettings({ ...seoSettings, ogImage: e.target.value })}
            />
          </Grid>
        </Grid>

        <Box sx={{ mt: 3, display: 'flex', justifyContent: 'flex-end' }}>
          <Button
            variant="contained"
            startIcon={<SaveIcon />}
          >
            {t('saveSEOSettings', 'SEO Ayarlarını Kaydet')}
          </Button>
        </Box>
      </Paper>

      <Card sx={{ mt: 3 }}>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            {t('preview', 'Önizleme')}
          </Typography>
          <Box sx={{ border: 1, borderColor: 'divider', p: 2, borderRadius: 1 }}>
            <Typography variant="h6" color="primary">
              {seoSettings.title}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              {seoSettings.description}
            </Typography>
          </Box>
        </CardContent>
      </Card>
    </Container>
  );
};

export default SEOOptimization;

