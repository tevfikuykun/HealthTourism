import React, { useState, useEffect } from 'react';
import {
  Box,
  Container,
  Typography,
  Card,
  CardContent,
  Grid,
  Avatar,
  Chip,
  Button,
  Divider,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  LinearProgress,
  Alert,
} from '@mui/material';
import {
  Person,
  Email,
  Phone,
  Language,
  Verified,
  Star,
  Work,
  School,
  CheckCircle,
} from '@mui/icons-material';
import { useTranslation } from 'react-i18next';
import { useQuery } from '@tanstack/react-query';
import api from '../services/api';

/**
 * Meet Your Coordinator
 * Hastanın operasyon sorumlusuyla tanışacağı güven verici sayfa
 */
const MeetCoordinator = () => {
  const { t } = useTranslation();
  const [selectedCoordinator, setSelectedCoordinator] = useState(null);

  // Fetch coordinators
  const { data: coordinators, isLoading } = useQuery({
    queryKey: ['coordinators'],
    queryFn: async () => {
      const response = await api.get('/coordinators/assigned');
      return response.data;
    },
  });

  useEffect(() => {
    if (coordinators && coordinators.length > 0 && !selectedCoordinator) {
      setSelectedCoordinator(coordinators[0]);
    }
  }, [coordinators, selectedCoordinator]);

  if (isLoading) {
    return (
      <Box sx={{ p: 3 }}>
        <LinearProgress />
      </Box>
    );
  }

  if (!selectedCoordinator) {
    return (
      <Container maxWidth="md" sx={{ py: 4, textAlign: 'center' }}>
        <Alert severity="info">
          <Typography>{t('coordinator.noCoordinator')}</Typography>
        </Alert>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        {t('coordinator.title')}
      </Typography>
      <Typography variant="body1" color="text.secondary" paragraph>
        {t('coordinator.subtitle')}
      </Typography>

      <Grid container spacing={4}>
        {/* Coordinator Profile */}
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent sx={{ textAlign: 'center' }}>
              <Avatar
                src={selectedCoordinator.photo}
                sx={{
                  width: 150,
                  height: 150,
                  mx: 'auto',
                  mb: 2,
                }}
              >
                {selectedCoordinator.name.charAt(0)}
              </Avatar>
              <Typography variant="h5" gutterBottom>
                {selectedCoordinator.name}
              </Typography>
              <Typography variant="body2" color="text.secondary" gutterBottom>
                {selectedCoordinator.title}
              </Typography>
              <Box sx={{ display: 'flex', justifyContent: 'center', gap: 1, mt: 2 }}>
                <Chip
                  icon={<Verified />}
                  label={t('coordinator.verified')}
                  color="success"
                  size="small"
                />
                <Chip
                  icon={<Star />}
                  label={`${selectedCoordinator.rating}/5`}
                  color="primary"
                  size="small"
                />
              </Box>

              <Divider sx={{ my: 3 }} />

              {/* Contact Info */}
              <List>
                <ListItem>
                  <ListItemIcon>
                    <Email />
                  </ListItemIcon>
                  <ListItemText
                    primary={t('coordinator.email')}
                    secondary={selectedCoordinator.email}
                  />
                </ListItem>
                <ListItem>
                  <ListItemIcon>
                    <Phone />
                  </ListItemIcon>
                  <ListItemText
                    primary={t('coordinator.phone')}
                    secondary={selectedCoordinator.phone}
                  />
                </ListItem>
                <ListItem>
                  <ListItemIcon>
                    <Language />
                  </ListItemIcon>
                  <ListItemText
                    primary={t('coordinator.languages')}
                    secondary={selectedCoordinator.languages?.join(', ')}
                  />
                </ListItem>
              </List>

              <Button
                variant="contained"
                fullWidth
                sx={{ mt: 2 }}
                onClick={() => {
                  // Start chat or call
                  window.location.href = `/chat/${selectedCoordinator.id}`;
                }}
              >
                {t('coordinator.contact')}
              </Button>
            </CardContent>
          </Card>
        </Grid>

        {/* Coordinator Details */}
        <Grid item xs={12} md={8}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                {t('coordinator.about')}
              </Typography>
              <Typography variant="body1" paragraph>
                {selectedCoordinator.bio}
              </Typography>

              <Divider sx={{ my: 3 }} />

              {/* Experience */}
              <Typography variant="h6" gutterBottom>
                <Work sx={{ verticalAlign: 'middle', mr: 1 }} />
                {t('coordinator.experience')}
              </Typography>
              <List>
                {selectedCoordinator.experience?.map((exp, idx) => (
                  <ListItem key={idx}>
                    <ListItemIcon>
                      <CheckCircle color="success" />
                    </ListItemIcon>
                    <ListItemText
                      primary={exp.role}
                      secondary={`${exp.company} - ${exp.period}`}
                    />
                  </ListItem>
                ))}
              </List>

              <Divider sx={{ my: 3 }} />

              {/* Education */}
              <Typography variant="h6" gutterBottom>
                <School sx={{ verticalAlign: 'middle', mr: 1 }} />
                {t('coordinator.education')}
              </Typography>
              <List>
                {selectedCoordinator.education?.map((edu, idx) => (
                  <ListItem key={idx}>
                    <ListItemIcon>
                      <School color="primary" />
                    </ListItemIcon>
                    <ListItemText
                      primary={edu.degree}
                      secondary={edu.institution}
                    />
                  </ListItem>
                ))}
              </List>

              <Divider sx={{ my: 3 }} />

              {/* Specializations */}
              <Typography variant="h6" gutterBottom>
                {t('coordinator.specializations')}
              </Typography>
              <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                {selectedCoordinator.specializations?.map((spec, idx) => (
                  <Chip key={idx} label={spec} color="primary" variant="outlined" />
                ))}
              </Box>

              <Divider sx={{ my: 3 }} />

              {/* Statistics */}
              <Grid container spacing={2}>
                <Grid item xs={6} sm={3}>
                  <Box sx={{ textAlign: 'center' }}>
                    <Typography variant="h4" color="primary">
                      {selectedCoordinator.stats?.patientsHelped || 0}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      {t('coordinator.patientsHelped')}
                    </Typography>
                  </Box>
                </Grid>
                <Grid item xs={6} sm={3}>
                  <Box sx={{ textAlign: 'center' }}>
                    <Typography variant="h4" color="primary">
                      {selectedCoordinator.stats?.yearsExperience || 0}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      {t('coordinator.yearsExperience')}
                    </Typography>
                  </Box>
                </Grid>
                <Grid item xs={6} sm={3}>
                  <Box sx={{ textAlign: 'center' }}>
                    <Typography variant="h4" color="primary">
                      {selectedCoordinator.stats?.responseTime || 'N/A'}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      {t('coordinator.avgResponseTime')}
                    </Typography>
                  </Box>
                </Grid>
                <Grid item xs={6} sm={3}>
                  <Box sx={{ textAlign: 'center' }}>
                    <Typography variant="h4" color="primary">
                      {selectedCoordinator.stats?.satisfactionRate || 0}%
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      {t('coordinator.satisfactionRate')}
                    </Typography>
                  </Box>
                </Grid>
              </Grid>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Container>
  );
};

export default MeetCoordinator;

