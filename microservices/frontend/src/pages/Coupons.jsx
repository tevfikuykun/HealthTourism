// src/pages/Coupons.jsx
import React, { useState } from 'react';
import {
  Container, Box, Typography, Grid, Card, CardContent, Button,
  Chip, TextField, Dialog, DialogTitle, DialogContent, DialogActions
} from '@mui/material';
import LocalOfferIcon from '@mui/icons-material/LocalOffer';
import ContentCopyIcon from '@mui/icons-material/ContentCopy';
import { useTranslation } from 'react-i18next';

const Coupons = () => {
  const { t } = useTranslation();
  const [coupons, setCoupons] = useState([
    {
      id: 1,
      code: 'HEALTH20',
      discount: 20,
      type: 'percentage',
      description: 'Tüm hizmetlerde %20 indirim',
      validUntil: '2024-12-31',
      used: false,
    },
    {
      id: 2,
      code: 'WELCOME50',
      discount: 50,
      type: 'fixed',
      description: 'İlk rezervasyonda 50₺ indirim',
      validUntil: '2024-06-30',
      used: true,
    },
  ]);

  const [openRedeem, setOpenRedeem] = useState(false);
  const [redeemCode, setRedeemCode] = useState('');

  const handleCopyCode = (code) => {
    navigator.clipboard.writeText(code);
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
        <Typography variant="h4">{t('couponsAndDiscounts', 'Kuponlar ve İndirimler')}</Typography>
        <Button
          variant="contained"
          startIcon={<LocalOfferIcon />}
          onClick={() => setOpenRedeem(true)}
        >
          {t('useCoupon', 'Kupon Kullan')}
        </Button>
      </Box>

      <Grid container spacing={3}>
        {coupons.map((coupon) => (
          <Grid item xs={12} sm={6} md={4} key={coupon.id}>
            <Card
              sx={{
                background: coupon.used
                  ? 'linear-gradient(135deg, #f5f5f5 0%, #e0e0e0 100%)'
                  : 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                color: coupon.used ? 'text.primary' : 'white',
              }}
            >
              <CardContent>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start', mb: 2 }}>
                  <Box>
                    <Typography variant="h5" sx={{ fontWeight: 'bold', mb: 1 }}>
                      {coupon.type === 'percentage' ? `%${coupon.discount}` : `${coupon.discount}₺`}
                    </Typography>
                    <Typography variant="body2" sx={{ opacity: 0.9 }}>
                      {coupon.description}
                    </Typography>
                  </Box>
                  {coupon.used && (
                    <Chip label={t('used', 'Kullanıldı')} size="small" color="default" />
                  )}
                </Box>
                <Box sx={{ mt: 2, p: 2, bgcolor: 'rgba(255,255,255,0.2)', borderRadius: 1 }}>
                  <Typography variant="body2" sx={{ mb: 1, opacity: 0.9 }}>
                    {t('couponCode', 'Kupon Kodu')}
                  </Typography>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                    <Typography variant="h6" sx={{ fontFamily: 'monospace', flexGrow: 1 }}>
                      {coupon.code}
                    </Typography>
                    <Button
                      size="small"
                      startIcon={<ContentCopyIcon />}
                      onClick={() => handleCopyCode(coupon.code)}
                      sx={{ color: 'white' }}
                    >
                      {t('copy', 'Kopyala')}
                    </Button>
                  </Box>
                </Box>
                <Typography variant="caption" sx={{ mt: 1, display: 'block', opacity: 0.8 }}>
                  {t('validUntil', 'Geçerlilik')}: {coupon.validUntil}
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>

      <Dialog open={openRedeem} onClose={() => setOpenRedeem(false)} maxWidth="sm" fullWidth>
        <DialogTitle>{t('useCoupon', 'Kupon Kullan')}</DialogTitle>
        <DialogContent>
          <TextField
            fullWidth
            label={t('couponCode', 'Kupon Kodu')}
            value={redeemCode}
            onChange={(e) => setRedeemCode(e.target.value)}
            margin="normal"
            placeholder={t('enterCouponCode', 'Kupon kodunu girin')}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenRedeem(false)}>{t('cancel')}</Button>
          <Button variant="contained" onClick={() => setOpenRedeem(false)}>
            {t('use', 'Kullan')}
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default Coupons;

