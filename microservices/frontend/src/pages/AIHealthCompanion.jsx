// src/pages/AIHealthCompanion.jsx
import React, { useState } from 'react';
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
  Paper,
  Chip,
  Divider,
  List,
  ListItem,
  ListItemAvatar,
  ListItemText,
  IconButton,
  CircularProgress,
} from '@mui/material';
import {
  Send as SendIcon,
  SmartToy as BotIcon,
  Person as PersonIcon,
  Psychology as PsychologyIcon,
} from '@mui/icons-material';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { aiHealthCompanionService } from '../services/api';
import { useAuth } from '../hooks/useAuth';
import ProtectedRoute from '../components/ProtectedRoute';
import Loading from '../components/Loading';
import { useTranslation } from 'react-i18next';

export default function AIHealthCompanion() {
  const { t } = useTranslation();
  const { user, isAuthenticated } = useAuth();
  const queryClient = useQueryClient();
  const [question, setQuestion] = useState('');
  const [conversation, setConversation] = useState([]);

  const { data: context, isLoading: contextLoading } = useQuery({
    queryKey: ['aiHealthCompanionContext', user?.id],
    queryFn: () => aiHealthCompanionService.getByUser(user?.id),
    enabled: isAuthenticated && !!user?.id,
  });

  const askQuestionMutation = useMutation({
    mutationFn: (questionData) => aiHealthCompanionService.askQuestion(questionData),
    onSuccess: (response) => {
      const newMessage = {
        question,
        answer: response.data?.answer || response.data?.response || t('noAnswer', 'Cevap alınamadı'),
        timestamp: new Date(),
      };
      setConversation([...conversation, newMessage]);
      setQuestion('');
      queryClient.invalidateQueries(['aiHealthCompanionContext', user?.id]);
    },
  });

  if (!isAuthenticated) {
    return (
      <ProtectedRoute>
        <div />
      </ProtectedRoute>
    );
  }

  const handleSubmit = (e) => {
    e.preventDefault();
    if (question.trim()) {
      askQuestionMutation.mutate({
        userId: user?.id,
        question,
        context: context?.data,
      });
    }
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Box sx={{ mb: 4 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
          <PsychologyIcon sx={{ fontSize: 40, color: 'primary.main', mr: 2 }} />
          <Box>
            <Typography variant="h4" gutterBottom>
              {t('aiHealthCompanion', 'AI Sağlık Asistanı')}
            </Typography>
            <Typography variant="body1" color="text.secondary">
              {t('aiCompanionDescription', 'Sağlık sorularınızı sorun, kişiselleştirilmiş öneriler alın')}
            </Typography>
          </Box>
        </Box>
      </Box>

      <Grid container spacing={3}>
        {/* Sol Panel - Bağlam Bilgileri */}
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                {t('yourHealthContext', 'Sağlık Bağlamınız')}
              </Typography>
              <Divider sx={{ mb: 2 }} />
              {contextLoading ? (
                <CircularProgress size={24} />
              ) : context?.data ? (
                <Box>
                  <Typography variant="body2" color="text.secondary" gutterBottom>
                    {t('activeReservations', 'Aktif Rezervasyonlar')}
                  </Typography>
                  <Chip
                    label={context.data.activeReservations || 0}
                    color="primary"
                    size="small"
                    sx={{ mb: 2 }}
                  />
                  {context.data.recentHealthRecords && (
                    <>
                      <Typography variant="body2" color="text.secondary" gutterBottom sx={{ mt: 2 }}>
                        {t('recentHealthRecords', 'Son Sağlık Kayıtları')}
                      </Typography>
                      <Typography variant="body2">
                        {context.data.recentHealthRecords}
                      </Typography>
                    </>
                  )}
                </Box>
              ) : (
                <Typography variant="body2" color="text.secondary">
                  {t('noContext', 'Henüz sağlık bağlamı bilgisi yok')}
                </Typography>
              )}
            </CardContent>
          </Card>

          {/* Örnek Sorular */}
          <Card sx={{ mt: 2 }}>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                {t('exampleQuestions', 'Örnek Sorular')}
              </Typography>
              <Divider sx={{ mb: 2 }} />
              <List>
                {[
                  t('exampleQuestion1', 'Rezervasyonum için ne hazırlamalıyım?'),
                  t('exampleQuestion2', 'Tedavi sonrası bakım önerileri nelerdir?'),
                  t('exampleQuestion3', 'İlaç hatırlatıcılarımı nasıl ayarlayabilirim?'),
                ].map((example, index) => (
                  <ListItem
                    key={index}
                    button
                    onClick={() => setQuestion(example)}
                    sx={{ borderRadius: 2, mb: 1 }}
                  >
                    <ListItemText
                      primary={example}
                      primaryTypographyProps={{ variant: 'body2' }}
                    />
                  </ListItem>
                ))}
              </List>
            </CardContent>
          </Card>
        </Grid>

        {/* Ana Panel - Sohbet */}
        <Grid item xs={12} md={8}>
          <Card sx={{ height: '70vh', display: 'flex', flexDirection: 'column' }}>
            <CardContent sx={{ flexGrow: 1, overflow: 'auto', pb: 1 }}>
              {conversation.length === 0 ? (
                <Box sx={{ textAlign: 'center', py: 8 }}>
                  <BotIcon sx={{ fontSize: 80, color: 'primary.main', mb: 2 }} />
                  <Typography variant="h6" gutterBottom>
                    {t('welcomeToAICompanion', 'AI Sağlık Asistanına Hoş Geldiniz')}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    {t('askQuestion', 'Sağlık sorularınızı sorun, size yardımcı olayım')}
                  </Typography>
                </Box>
              ) : (
                <List>
                  {conversation.map((message, index) => (
                    <React.Fragment key={index}>
                      {/* Kullanıcı Sorusu */}
                      <ListItem sx={{ justifyContent: 'flex-end', mb: 1 }}>
                        <Paper
                          sx={{
                            p: 2,
                            bgcolor: 'primary.main',
                            color: 'white',
                            maxWidth: '70%',
                            borderRadius: 3,
                          }}
                        >
                          <Box sx={{ display: 'flex', alignItems: 'flex-start', gap: 1 }}>
                            <ListItemText
                              primary={message.question}
                              primaryTypographyProps={{ color: 'inherit' }}
                            />
                            <PersonIcon fontSize="small" />
                          </Box>
                        </Paper>
                      </ListItem>

                      {/* AI Cevabı */}
                      <ListItem sx={{ justifyContent: 'flex-start', mb: 1 }}>
                        <Paper
                          sx={{
                            p: 2,
                            bgcolor: 'grey.100',
                            maxWidth: '70%',
                            borderRadius: 3,
                          }}
                        >
                          <Box sx={{ display: 'flex', alignItems: 'flex-start', gap: 1 }}>
                            <BotIcon fontSize="small" color="primary" />
                            <ListItemText
                              primary={message.answer}
                              primaryTypographyProps={{ color: 'text.primary' }}
                            />
                          </Box>
                        </Paper>
                      </ListItem>
                    </React.Fragment>
                  ))}
                  {askQuestionMutation.isLoading && (
                    <ListItem>
                      <CircularProgress size={24} />
                    </ListItem>
                  )}
                </List>
              )}
            </CardContent>
            <Divider />
            <CardContent>
              <form onSubmit={handleSubmit}>
                <Box sx={{ display: 'flex', gap: 1 }}>
                  <TextField
                    fullWidth
                    placeholder={t('askQuestion', 'Sorunuzu yazın...')}
                    value={question}
                    onChange={(e) => setQuestion(e.target.value)}
                    disabled={askQuestionMutation.isLoading}
                    multiline
                    maxRows={3}
                  />
                  <IconButton
                    type="submit"
                    color="primary"
                    disabled={!question.trim() || askQuestionMutation.isLoading}
                    sx={{ alignSelf: 'flex-end' }}
                  >
                    <SendIcon />
                  </IconButton>
                </Box>
              </form>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Container>
  );
}


