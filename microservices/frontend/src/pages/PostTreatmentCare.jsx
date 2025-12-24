// src/pages/PostTreatmentCare.jsx
import React, { useState } from 'react';
import {
  Container,
  Box,
  Typography,
  Grid,
  Card,
  CardContent,
  CardHeader,
  Button,
  Chip,
  LinearProgress,
  Divider,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Alert,
  CircularProgress,
  Accordion,
  AccordionSummary,
  AccordionDetails,
} from '@mui/material';
import {
  CheckCircle as CheckCircleIcon,
  RadioButtonUnchecked as RadioButtonUncheckedIcon,
  CalendarToday as CalendarIcon,
  LocalHospital as HospitalIcon,
  Assignment as AssignmentIcon,
  ExpandMore as ExpandMoreIcon,
  Schedule as ScheduleIcon,
} from '@mui/icons-material';
import { useAuth } from '../hooks/useAuth';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { postTreatmentService } from '../services/api';
import ProtectedRoute from '../components/ProtectedRoute';
import Loading from '../components/Loading';
import { useTranslation } from '../i18n';
import { toast } from 'react-toastify';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import { format, parseISO } from 'date-fns';

export default function PostTreatmentCare() {
  const { user } = useAuth();
  const { t } = useTranslation();
  const queryClient = useQueryClient();
  const [selectedPackage, setSelectedPackage] = useState(null);
  const [followUpDialogOpen, setFollowUpDialogOpen] = useState(false);
  const [followUpDate, setFollowUpDate] = useState(null);

  // Fetch care packages for current user
  const { data: carePackages, isLoading, error } = useQuery({
    queryKey: ['postTreatmentCare', user?.id],
    queryFn: () => postTreatmentService.getByUser(user?.id),
    enabled: !!user?.id,
    select: (response) => response.data || [],
  });

  // Update task status mutation
  const updateTaskMutation = useMutation({
    mutationFn: ({ packageId, taskIndex, isCompleted }) =>
      postTreatmentService.updateTaskStatus(packageId, taskIndex, isCompleted),
    onSuccess: () => {
      queryClient.invalidateQueries(['postTreatmentCare', user?.id]);
      toast.success(t('postTreatment.taskUpdated', 'Görev durumu güncellendi'));
    },
    onError: (error) => {
      toast.error(error.message || t('postTreatment.updateFailed', 'Güncelleme başarısız'));
    },
  });

  // Schedule follow-up mutation
  const scheduleFollowUpMutation = useMutation({
    mutationFn: ({ packageId, date }) =>
      postTreatmentService.scheduleFollowUp(packageId, date.toISOString()),
    onSuccess: () => {
      queryClient.invalidateQueries(['postTreatmentCare', user?.id]);
      toast.success(t('postTreatment.followUpScheduled', 'Kontrol randevusu planlandı'));
      setFollowUpDialogOpen(false);
      setFollowUpDate(null);
    },
    onError: (error) => {
      toast.error(error.message || t('postTreatment.scheduleFailed', 'Randevu planlama başarısız'));
    },
  });

  const handleTaskToggle = (packageId, taskIndex, currentStatus) => {
    updateTaskMutation.mutate({
      packageId,
      taskIndex,
      isCompleted: !currentStatus,
    });
  };

  const handleScheduleFollowUp = (packageId) => {
    setSelectedPackage(packageId);
    setFollowUpDialogOpen(true);
  };

  const handleConfirmFollowUp = () => {
    if (!followUpDate) {
      toast.error(t('postTreatment.selectDate', 'Lütfen bir tarih seçin'));
      return;
    }
    scheduleFollowUpMutation.mutate({
      packageId: selectedPackage,
      date: followUpDate,
    });
  };

  const getProgressPercentage = (tasks) => {
    if (!tasks || tasks.length === 0) return 0;
    const completed = tasks.filter((task) => task.isCompleted).length;
    return Math.round((completed / tasks.length) * 100);
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'ACTIVE':
        return 'primary';
      case 'COMPLETED':
        return 'success';
      case 'CANCELLED':
        return 'error';
      default:
        return 'default';
    }
  };

  if (isLoading) {
    return <Loading />;
  }

  if (error) {
    return (
      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        <Alert severity="error">
          {t('postTreatment.loadError', 'Bakım paketleri yüklenirken bir hata oluştu')}
        </Alert>
      </Container>
    );
  }

  return (
    <ProtectedRoute>
      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
          <Box sx={{ mb: 4 }}>
            <Typography variant="h4" gutterBottom>
              {t('postTreatment.title', 'Tedavi Sonrası Bakım')}
            </Typography>
            <Typography variant="body1" color="text.secondary">
              {t('postTreatment.subtitle', 'Tedavi sonrası bakım planınızı takip edin ve görevleri tamamlayın')}
            </Typography>
          </Box>

          {!carePackages || carePackages.length === 0 ? (
            <Card>
              <CardContent>
                <Box sx={{ textAlign: 'center', py: 4 }}>
                  <HospitalIcon sx={{ fontSize: 64, color: 'text.secondary', mb: 2 }} />
                  <Typography variant="h6" color="text.secondary">
                    {t('postTreatment.noPackages', 'Henüz bakım paketiniz bulunmamaktadır')}
                  </Typography>
                  <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                    {t('postTreatment.noPackagesDesc', 'Tedavi sonrası bakım paketiniz rezervasyonunuzdan sonra otomatik olarak oluşturulacaktır')}
                  </Typography>
                </Box>
              </CardContent>
            </Card>
          ) : (
            <Grid container spacing={3}>
              {carePackages.map((carePackage) => {
                const progress = getProgressPercentage(carePackage.tasks);
                return (
                  <Grid item xs={12} key={carePackage.id}>
                    <Card>
                      <CardHeader
                        avatar={<HospitalIcon color="primary" />}
                        title={
                          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                            <Typography variant="h6">
                              {t('postTreatment.package', 'Bakım Paketi')} #{carePackage.id}
                            </Typography>
                            <Chip
                              label={t(`postTreatment.status.${carePackage.status}`, carePackage.status)}
                              color={getStatusColor(carePackage.status)}
                              size="small"
                            />
                          </Box>
                        }
                        subheader={
                          <Box sx={{ mt: 1 }}>
                            <Typography variant="body2" color="text.secondary">
                              {t('postTreatment.treatmentType', 'Tedavi Tipi')}: {carePackage.treatmentType}
                            </Typography>
                            <Typography variant="body2" color="text.secondary">
                              {t('postTreatment.startDate', 'Başlangıç Tarihi')}:{' '}
                              {carePackage.startDate
                                ? format(parseISO(carePackage.startDate), 'dd.MM.yyyy')
                                : '-'}
                            </Typography>
                          </Box>
                        }
                      />
                      <CardContent>
                        {carePackage.carePlan && (
                          <Box sx={{ mb: 3 }}>
                            <Typography variant="subtitle2" gutterBottom>
                              {t('postTreatment.carePlan', 'Bakım Planı')}
                            </Typography>
                            <Typography variant="body2" color="text.secondary">
                              {carePackage.carePlan}
                            </Typography>
                          </Box>
                        )}

                        <Box sx={{ mb: 3 }}>
                          <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                            <Typography variant="subtitle2">
                              {t('postTreatment.progress', 'İlerleme')}
                            </Typography>
                            <Typography variant="body2" color="text.secondary">
                              {progress}%
                            </Typography>
                          </Box>
                          <LinearProgress variant="determinate" value={progress} sx={{ height: 8, borderRadius: 1 }} />
                        </Box>

                        {carePackage.tasks && carePackage.tasks.length > 0 && (
                          <Accordion>
                            <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                              <Typography variant="subtitle2">
                                {t('postTreatment.tasks', 'Görevler')} ({carePackage.tasks.length})
                              </Typography>
                            </AccordionSummary>
                            <AccordionDetails>
                              <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                                {carePackage.tasks.map((task, index) => (
                                  <Box
                                    key={index}
                                    sx={{
                                      display: 'flex',
                                      alignItems: 'flex-start',
                                      gap: 2,
                                      p: 2,
                                      border: '1px solid',
                                      borderColor: 'divider',
                                      borderRadius: 1,
                                      bgcolor: task.isCompleted ? 'action.selected' : 'background.paper',
                                    }}
                                  >
                                    <IconButton
                                      onClick={() =>
                                        handleTaskToggle(carePackage.id, index, task.isCompleted)
                                      }
                                      disabled={updateTaskMutation.isLoading}
                                      color={task.isCompleted ? 'success' : 'default'}
                                    >
                                      {task.isCompleted ? <CheckCircleIcon /> : <RadioButtonUncheckedIcon />}
                                    </IconButton>
                                    <Box sx={{ flex: 1 }}>
                                      <Typography
                                        variant="body1"
                                        sx={{
                                          textDecoration: task.isCompleted ? 'line-through' : 'none',
                                          color: task.isCompleted ? 'text.secondary' : 'text.primary',
                                        }}
                                      >
                                        {task.taskName}
                                      </Typography>
                                      {task.description && (
                                        <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5 }}>
                                          {task.description}
                                        </Typography>
                                      )}
                                      {task.dueDate && (
                                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5, mt: 1 }}>
                                          <CalendarIcon sx={{ fontSize: 16, color: 'text.secondary' }} />
                                          <Typography variant="caption" color="text.secondary">
                                            {t('postTreatment.dueDate', 'Son Tarih')}:{' '}
                                            {format(parseISO(task.dueDate), 'dd.MM.yyyy')}
                                          </Typography>
                                        </Box>
                                      )}
                                    </Box>
                                  </Box>
                                ))}
                              </Box>
                            </AccordionDetails>
                          </Accordion>
                        )}

                        <Divider sx={{ my: 2 }} />

                        <Box sx={{ display: 'flex', gap: 2, justifyContent: 'flex-end' }}>
                          <Button
                            variant="outlined"
                            startIcon={<ScheduleIcon />}
                            onClick={() => handleScheduleFollowUp(carePackage.id)}
                            disabled={scheduleFollowUpMutation.isLoading}
                          >
                            {t('postTreatment.scheduleFollowUp', 'Kontrol Randevusu Planla')}
                          </Button>
                        </Box>
                      </CardContent>
                    </Card>
                  </Grid>
                );
              })}
            </Grid>
          )}

          {/* Follow-up Dialog */}
          <Dialog open={followUpDialogOpen} onClose={() => setFollowUpDialogOpen(false)} maxWidth="sm" fullWidth>
            <DialogTitle>{t('postTreatment.scheduleFollowUp', 'Kontrol Randevusu Planla')}</DialogTitle>
            <DialogContent>
              <Box sx={{ mt: 2 }}>
                <DatePicker
                  selected={followUpDate}
                  onChange={(date) => setFollowUpDate(date)}
                  minDate={new Date()}
                  showTimeSelect
                  timeIntervals={15}
                  dateFormat="dd.MM.yyyy HH:mm"
                  placeholderText={t('postTreatment.selectDate', 'Tarih ve Saat Seçin')}
                  wrapperClassName="w-100"
                  customInput={
                    <TextField
                      fullWidth
                      label={t('postTreatment.selectDate', 'Tarih ve Saat Seçin')}
                      InputLabelProps={{ shrink: true }}
                    />
                  }
                />
              </Box>
            </DialogContent>
            <DialogActions>
              <Button onClick={() => setFollowUpDialogOpen(false)}>
                {t('common.cancel', 'İptal')}
              </Button>
              <Button
                onClick={handleConfirmFollowUp}
                variant="contained"
                disabled={!followUpDate || scheduleFollowUpMutation.isLoading}
              >
                {scheduleFollowUpMutation.isLoading ? (
                  <CircularProgress size={24} />
                ) : (
                  t('common.confirm', 'Onayla')
                )}
              </Button>
            </DialogActions>
          </Dialog>
        </Container>
    </ProtectedRoute>
  );
}

