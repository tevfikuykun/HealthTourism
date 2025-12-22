import React, { useState } from 'react';
import {
  Box,
  Container,
  Typography,
  Card,
  CardContent,
  Grid,
  Accordion,
  AccordionSummary,
  AccordionDetails,
  Chip,
  Button,
  LinearProgress,
  Alert,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  VideoLibrary,
  Image,
  Description,
} from '@mui/material';
import {
  ExpandMore,
  PlayCircle,
  CheckCircle,
  Warning,
  SmartToy,
  VideoCall,
} from '@mui/icons-material';
import { useTranslation } from 'react-i18next';
import { useQuery } from '@tanstack/react-query';
import api from '../services/api';

/**
 * Treatment Guide & Recovery Tips
 * AI destekli eğitim merkezi - Operasyon sonrası yapılacaklar
 */
const TreatmentGuide = () => {
  const { t } = useTranslation();
  const [expanded, setExpanded] = useState('pre-surgery');
  const [selectedCategory, setSelectedCategory] = useState('ALL');

  // Fetch treatment guides
  const { data: guides, isLoading } = useQuery({
    queryKey: ['treatment-guides', selectedCategory],
    queryFn: async () => {
      const response = await api.get(`/api/treatment-guides?category=${selectedCategory}`);
      return response.data;
    },
  });

  // Fetch AI recommendations
  const { data: aiRecommendations } = useQuery({
    queryKey: ['ai-recommendations'],
    queryFn: async () => {
      const response = await api.get('/ai-health-companion/personalized-guide');
      return response.data;
    },
  });

  const handleAccordionChange = (panel) => (event, isExpanded) => {
    setExpanded(isExpanded ? panel : false);
  };

  const categories = [
    { id: 'ALL', label: t('treatmentGuide.all') },
    { id: 'PRE_SURGERY', label: t('treatmentGuide.preSurgery') },
    { id: 'POST_SURGERY', label: t('treatmentGuide.postSurgery') },
    { id: 'RECOVERY', label: t('treatmentGuide.recovery') },
    { id: 'NUTRITION', label: t('treatmentGuide.nutrition') },
    { id: 'EXERCISE', label: t('treatmentGuide.exercise') },
  ];

  if (isLoading) {
    return (
      <Box sx={{ p: 3 }}>
        <LinearProgress />
      </Box>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        {t('treatmentGuide.title')}
      </Typography>
      <Typography variant="body1" color="text.secondary" paragraph>
        {t('treatmentGuide.subtitle')}
      </Typography>

      {/* AI Recommendations */}
      {aiRecommendations && (
        <Alert severity="info" icon={<SmartToy />} sx={{ mb: 3 }}>
          <Typography variant="subtitle2" gutterBottom>
            {t('treatmentGuide.aiRecommendation')}
          </Typography>
          <Typography variant="body2">
            {aiRecommendations.message}
          </Typography>
        </Alert>
      )}

      {/* Category Filter */}
      <Box sx={{ display: 'flex', gap: 1, mb: 3, flexWrap: 'wrap' }}>
        {categories.map((cat) => (
          <Chip
            key={cat.id}
            label={cat.label}
            onClick={() => setSelectedCategory(cat.id)}
            color={selectedCategory === cat.id ? 'primary' : 'default'}
            variant={selectedCategory === cat.id ? 'filled' : 'outlined'}
          />
        ))}
      </Box>

      {/* Treatment Guides */}
      <Box sx={{ mb: 4 }}>
        <Accordion
          expanded={expanded === 'pre-surgery'}
          onChange={handleAccordionChange('pre-surgery')}
        >
          <AccordionSummary expandIcon={<ExpandMore />}>
            <Typography variant="h6">{t('treatmentGuide.preSurgery')}</Typography>
          </AccordionSummary>
          <AccordionDetails>
            <Grid container spacing={2}>
              {guides?.preSurgery?.map((guide) => (
                <Grid item xs={12} md={6} key={guide.id}>
                  <Card>
                    <CardContent>
                      <Box sx={{ display: 'flex', alignItems: 'start', gap: 2 }}>
                        {guide.type === 'VIDEO' ? (
                          <VideoLibrary color="primary" />
                        ) : guide.type === 'IMAGE' ? (
                          <Image color="primary" />
                        ) : (
                          <Description color="primary" />
                        )}
                        <Box sx={{ flex: 1 }}>
                          <Typography variant="h6" gutterBottom>
                            {guide.title}
                          </Typography>
                          <Typography variant="body2" color="text.secondary" paragraph>
                            {guide.description}
                          </Typography>
                          {guide.videoUrl && (
                            <Button
                              startIcon={<PlayCircle />}
                              variant="outlined"
                              size="small"
                              onClick={() => window.open(guide.videoUrl, '_blank')}
                            >
                              {t('treatmentGuide.watchVideo')}
                            </Button>
                          )}
                        </Box>
                      </Box>
                    </CardContent>
                  </Card>
                </Grid>
              ))}
            </Grid>
          </AccordionDetails>
        </Accordion>

        <Accordion
          expanded={expanded === 'post-surgery'}
          onChange={handleAccordionChange('post-surgery')}
        >
          <AccordionSummary expandIcon={<ExpandMore />}>
            <Typography variant="h6">{t('treatmentGuide.postSurgery')}</Typography>
          </AccordionSummary>
          <AccordionDetails>
            <List>
              {guides?.postSurgery?.map((item) => (
                <ListItem key={item.id}>
                  <ListItemIcon>
                    {item.important ? (
                      <Warning color="warning" />
                    ) : (
                      <CheckCircle color="success" />
                    )}
                  </ListItemIcon>
                  <ListItemText
                    primary={item.title}
                    secondary={item.description}
                  />
                </ListItem>
              ))}
            </List>
          </AccordionDetails>
        </Accordion>

        <Accordion
          expanded={expanded === 'recovery'}
          onChange={handleAccordionChange('recovery')}
        >
          <AccordionSummary expandIcon={<ExpandMore />}>
            <Typography variant="h6">{t('treatmentGuide.recovery')}</Typography>
          </AccordionSummary>
          <AccordionDetails>
            <Grid container spacing={2}>
              {guides?.recovery?.map((guide) => (
                <Grid item xs={12} sm={6} key={guide.id}>
                  <Card>
                    <CardContent>
                      <Typography variant="h6" gutterBottom>
                        {guide.title}
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        {guide.description}
                      </Typography>
                      {guide.timeline && (
                        <Box sx={{ mt: 2 }}>
                          <Typography variant="caption" color="text.secondary">
                            {t('treatmentGuide.timeline')}: {guide.timeline}
                          </Typography>
                        </Box>
                      )}
                    </CardContent>
                  </Card>
                </Grid>
              ))}
            </Grid>
          </AccordionDetails>
        </Accordion>
      </Box>

      {/* Quick Actions */}
      <Card>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            {t('treatmentGuide.quickActions')}
          </Typography>
          <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap' }}>
            <Button
              variant="contained"
              startIcon={<VideoCall />}
              onClick={() => window.location.href = '/tele-consultation'}
            >
              {t('treatmentGuide.scheduleConsultation')}
            </Button>
            <Button
              variant="outlined"
              startIcon={<SmartToy />}
              onClick={() => window.location.href = '/ai-health-companion'}
            >
              {t('treatmentGuide.askAI')}
            </Button>
          </Box>
        </CardContent>
      </Card>
    </Container>
  );
};

export default TreatmentGuide;



