// src/components/CookieConsent/CookieConsent.jsx
import React, { useState, useEffect } from 'react';
import {
  Paper, Box, Typography, Button, Link, Dialog, DialogTitle,
  DialogContent, DialogActions, FormControlLabel, Checkbox
} from '@mui/material';
import CookieIcon from '@mui/icons-material/Cookie';

const CookieConsent = () => {
  const [open, setOpen] = useState(false);
  const [preferences, setPreferences] = useState({
    necessary: true,
    analytics: false,
    marketing: false,
  });

  useEffect(() => {
    const consent = localStorage.getItem('cookieConsent');
    if (!consent) {
      setOpen(true);
    }
  }, []);

  const handleAcceptAll = () => {
    localStorage.setItem('cookieConsent', JSON.stringify({
      necessary: true,
      analytics: true,
      marketing: true,
      timestamp: new Date().toISOString(),
    }));
    setOpen(false);
  };

  const handleAcceptSelected = () => {
    localStorage.setItem('cookieConsent', JSON.stringify({
      ...preferences,
      timestamp: new Date().toISOString(),
    }));
    setOpen(false);
  };

  const handleRejectAll = () => {
    localStorage.setItem('cookieConsent', JSON.stringify({
      necessary: true,
      analytics: false,
      marketing: false,
      timestamp: new Date().toISOString(),
    }));
    setOpen(false);
  };

  if (!open) return null;

  return (
    <Dialog open={open} maxWidth="sm" fullWidth>
      <DialogTitle>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <CookieIcon />
          <Typography variant="h6">Çerez Politikası</Typography>
        </Box>
      </DialogTitle>
      <DialogContent>
        <Typography variant="body2" sx={{ mb: 2 }}>
          Web sitemiz, size en iyi deneyimi sunmak için çerezler kullanmaktadır.
          Çerez tercihlerinizi yönetebilirsiniz.
        </Typography>
        <Box sx={{ mb: 2 }}>
          <FormControlLabel
            control={
              <Checkbox
                checked={preferences.necessary}
                disabled
              />
            }
            label="Gerekli Çerezler (Zorunlu)"
          />
          <Typography variant="caption" display="block" color="text.secondary" sx={{ ml: 4 }}>
            Site işlevselliği için gerekli
          </Typography>
        </Box>
        <Box sx={{ mb: 2 }}>
          <FormControlLabel
            control={
              <Checkbox
                checked={preferences.analytics}
                onChange={(e) => setPreferences({ ...preferences, analytics: e.target.checked })}
              />
            }
            label="Analitik Çerezler"
          />
          <Typography variant="caption" display="block" color="text.secondary" sx={{ ml: 4 }}>
            Kullanım istatistikleri için
          </Typography>
        </Box>
        <Box>
          <FormControlLabel
            control={
              <Checkbox
                checked={preferences.marketing}
                onChange={(e) => setPreferences({ ...preferences, marketing: e.target.checked })}
              />
            }
            label="Pazarlama Çerezleri"
          />
          <Typography variant="caption" display="block" color="text.secondary" sx={{ ml: 4 }}>
            Kişiselleştirilmiş reklamlar için
          </Typography>
        </Box>
        <Link href="/privacy" variant="body2" sx={{ mt: 2, display: 'block' }}>
          Gizlilik Politikası
        </Link>
      </DialogContent>
      <DialogActions>
        <Button onClick={handleRejectAll}>Tümünü Reddet</Button>
        <Button onClick={handleAcceptSelected} variant="outlined">
          Seçilenleri Kabul Et
        </Button>
        <Button onClick={handleAcceptAll} variant="contained">
          Tümünü Kabul Et
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default CookieConsent;

