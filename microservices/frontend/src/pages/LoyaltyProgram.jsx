// src/pages/LoyaltyProgram.jsx
import React, { useState } from 'react';
import {
  Container, Box, Typography, Grid, Card, CardContent, LinearProgress,
  Button, Chip, Table, TableBody, TableCell, TableContainer, TableHead,
  TableRow, Paper, Avatar
} from '@mui/material';
import LoyaltyIcon from '@mui/icons-material/Loyalty';
import StarIcon from '@mui/icons-material/Star';
import CardGiftcardIcon from '@mui/icons-material/CardGiftcard';
import HistoryIcon from '@mui/icons-material/History';
import { useTranslation } from 'react-i18next';

const LoyaltyProgram = () => {
  const { t } = useTranslation();
  const [points, setPoints] = useState(1250);
  const [level, setLevel] = useState('Gold');
  const [nextLevelPoints, setNextLevelPoints] = useState(2000);
  const [progress, setProgress] = useState((points / nextLevelPoints) * 100);

  const levels = [
    { name: 'Bronze', minPoints: 0, benefits: ['%5 indirim'] },
    { name: 'Silver', minPoints: 500, benefits: ['%10 indirim', 'Öncelikli destek'] },
    { name: 'Gold', minPoints: 1000, benefits: ['%15 indirim', 'Öncelikli destek', 'Ücretsiz konsültasyon'] },
    { name: 'Platinum', minPoints: 2000, benefits: ['%20 indirim', 'VIP destek', 'Ücretsiz konsültasyon', 'Özel paketler'] },
  ];

  const rewards = [
    { id: 1, name: '100₺ İndirim', points: 500, available: true },
    { id: 2, name: 'Ücretsiz Konsültasyon', points: 750, available: true },
    { id: 3, name: 'Havaalanı Transferi', points: 1000, available: true },
  ];

  const history = [
    { id: 1, date: '2024-01-15', description: 'Rezervasyon', points: +250 },
    { id: 2, date: '2024-01-10', description: 'Ödül kullanımı', points: -500 },
    { id: 3, date: '2024-01-05', description: 'Rezervasyon', points: +300 },
  ];

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 4 }}>
        <LoyaltyIcon sx={{ fontSize: 40, color: 'primary.main' }} />
        <Box>
          <Typography variant="h4">{t('loyaltyProgram', 'Sadakat Programı')}</Typography>
          <Typography variant="body2" color="text.secondary">
            {t('loyaltyProgramDescription', 'Puanlarınızı toplayın, ödüller kazanın')}
          </Typography>
        </Box>
      </Box>

      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Box sx={{ textAlign: 'center' }}>
                <Avatar sx={{ width: 80, height: 80, bgcolor: 'primary.main', mx: 'auto', mb: 2 }}>
                  <StarIcon sx={{ fontSize: 40 }} />
                </Avatar>
                <Typography variant="h3">{points}</Typography>
                <Typography variant="body2" color="text.secondary">
                  {t('currentPoints', 'Mevcut Puanlar')}
                </Typography>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Box sx={{ textAlign: 'center' }}>
                <Chip label={level} color="primary" sx={{ mb: 2 }} />
                <Typography variant="h6">{t('level', 'Seviye')}</Typography>
                <LinearProgress
                  variant="determinate"
                  value={progress}
                  sx={{ mt: 2, height: 10, borderRadius: 5 }}
                />
                <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                  {nextLevelPoints - points} {t('pointsRemaining', 'puan kaldı')}
                </Typography>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Box sx={{ textAlign: 'center' }}>
                <CardGiftcardIcon sx={{ fontSize: 40, color: 'primary.main', mb: 2 }} />
                <Typography variant="h5">{rewards.length}</Typography>
                <Typography variant="body2" color="text.secondary">
                  {t('availableRewards', 'Mevcut Ödüller')}
                </Typography>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      <Paper sx={{ p: 3, mb: 3 }}>
        <Typography variant="h6" gutterBottom>
          {t('rewardCatalog', 'Ödül Kataloğu')}
        </Typography>
        <Grid container spacing={2}>
          {rewards.map((reward) => (
            <Grid item xs={12} sm={6} md={4} key={reward.id}>
              <Card>
                <CardContent>
                  <Typography variant="h6" gutterBottom>{reward.name}</Typography>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2 }}>
                    <StarIcon color="warning" />
                    <Typography variant="body1">{reward.points} {t('points', 'Puan')}</Typography>
                  </Box>
                  <Button
                    variant="contained"
                    fullWidth
                    disabled={!reward.available || points < reward.points}
                  >
                    {points >= reward.points ? t('use', 'Kullan') : t('insufficientPoints', 'Yetersiz Puan')}
                  </Button>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      </Paper>

      <Paper sx={{ p: 3 }}>
        <Typography variant="h6" gutterBottom>
          {t('pointsHistory', 'Puan Geçmişi')}
        </Typography>
        <TableContainer>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>{t('date')}</TableCell>
                <TableCell>{t('description')}</TableCell>
                <TableCell align="right">{t('points', 'Puan')}</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {history.map((item) => (
                <TableRow key={item.id}>
                  <TableCell>{item.date}</TableCell>
                  <TableCell>{item.description}</TableCell>
                  <TableCell align="right">
                    <Chip
                      label={item.points > 0 ? `+${item.points}` : item.points}
                      color={item.points > 0 ? 'success' : 'error'}
                      size="small"
                    />
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </Paper>
    </Container>
  );
};

export default LoyaltyProgram;

