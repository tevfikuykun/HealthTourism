// src/pages/ReferralProgram.jsx
import React, { useState } from 'react';
import {
  Container, Box, Typography, Card, CardContent, Grid, Button,
  TextField, Paper, Chip, IconButton, Alert
} from '@mui/material';
import ShareIcon from '@mui/icons-material/Share';
import ContentCopyIcon from '@mui/icons-material/ContentCopy';
import PeopleIcon from '@mui/icons-material/People';
import AttachMoneyIcon from '@mui/icons-material/AttachMoney';
import { useTranslation } from 'react-i18next';

const ReferralProgram = () => {
  const { t } = useTranslation();
  const [referralCode, setReferralCode] = useState('HEALTH2024');
  const [copied, setCopied] = useState(false);

  const stats = {
    totalReferrals: 12,
    activeReferrals: 8,
    totalEarnings: 1200,
    pendingEarnings: 300,
  };

  const handleCopy = () => {
    navigator.clipboard.writeText(referralCode);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4" gutterBottom>
        {t('referralProgram', 'Referans Programı')}
      </Typography>

      <Alert severity="info" sx={{ mt: 2, mb: 3 }}>
        {t('referralProgramDescription', 'Arkadaşlarınızı davet edin, her başarılı referans için %10 indirim kazanın!')}
      </Alert>

      <Grid container spacing={3} sx={{ mt: 2 }}>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                <PeopleIcon sx={{ fontSize: 40, color: 'primary.main' }} />
                <Box>
                  <Typography variant="h4">{stats.totalReferrals}</Typography>
                  <Typography variant="body2" color="text.secondary">
                    {t('totalReferrals', 'Toplam Referans')}
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                <PeopleIcon sx={{ fontSize: 40, color: 'success.main' }} />
                <Box>
                  <Typography variant="h4">{stats.activeReferrals}</Typography>
                  <Typography variant="body2" color="text.secondary">
                    {t('activeReferrals', 'Aktif Referans')}
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                <AttachMoneyIcon sx={{ fontSize: 40, color: 'warning.main' }} />
                <Box>
                  <Typography variant="h4">₺{stats.totalEarnings}</Typography>
                  <Typography variant="body2" color="text.secondary">
                    {t('totalEarnings', 'Toplam Kazanç')}
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                <AttachMoneyIcon sx={{ fontSize: 40, color: 'info.main' }} />
                <Box>
                  <Typography variant="h4">₺{stats.pendingEarnings}</Typography>
                  <Typography variant="body2" color="text.secondary">
                    {t('pendingEarnings', 'Bekleyen Kazanç')}
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      <Paper sx={{ p: 3, mt: 3 }}>
        <Typography variant="h6" gutterBottom>
          {t('yourReferralCode', 'Referans Kodunuz')}
        </Typography>
        <Box sx={{ display: 'flex', gap: 2, mt: 2 }}>
          <TextField
            fullWidth
            value={referralCode}
            InputProps={{
              readOnly: true,
            }}
          />
          <Button
            variant="outlined"
            startIcon={<ContentCopyIcon />}
            onClick={handleCopy}
          >
            {copied ? t('copied', 'Kopyalandı!') : t('copy', 'Kopyala')}
          </Button>
          <Button
            variant="contained"
            startIcon={<ShareIcon />}
          >
            {t('share', 'Paylaş')}
          </Button>
        </Box>
        <Typography variant="body2" color="text.secondary" sx={{ mt: 2 }}>
          {t('referralCodeDescription', 'Bu kodu arkadaşlarınızla paylaşın. Onlar kayıt olduğunda ve ilk rezervasyonlarını yaptığında, hem siz hem de onlar indirim kazanacaksınız!')}
        </Typography>
      </Paper>

      <Paper sx={{ p: 3, mt: 3 }}>
        <Typography variant="h6" gutterBottom>
          {t('yourReferralLink', 'Referans Linkiniz')}
        </Typography>
        <Box sx={{ display: 'flex', gap: 2, mt: 2 }}>
          <TextField
            fullWidth
            value={`https://healthtourism.com/register?ref=${referralCode}`}
            InputProps={{
              readOnly: true,
            }}
          />
          <Button
            variant="outlined"
            startIcon={<ContentCopyIcon />}
            onClick={() => {
              navigator.clipboard.writeText(`https://healthtourism.com/register?ref=${referralCode}`);
              setCopied(true);
            }}
          >
            {t('copy', 'Kopyala')}
          </Button>
        </Box>
      </Paper>
    </Container>
  );
};

export default ReferralProgram;

