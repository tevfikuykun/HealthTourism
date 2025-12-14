// src/pages/admin/RateLimiting.jsx
import React, { useState } from 'react';
import {
  Container, Box, Typography, Paper, Grid, TextField, Button,
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
  Switch, FormControlLabel, Chip, IconButton
} from '@mui/material';
import SpeedIcon from '@mui/icons-material/Speed';
import EditIcon from '@mui/icons-material/Edit';
import SaveIcon from '@mui/icons-material/Save';
import { useTranslation } from 'react-i18next';

const RateLimiting = () => {
  const { t } = useTranslation();
  const [limits, setLimits] = useState([
    {
      id: 1,
      endpoint: '/api/auth/login',
      method: 'POST',
      limit: 5,
      window: 60,
      enabled: true,
    },
    {
      id: 2,
      endpoint: '/api/reservations',
      method: 'GET',
      limit: 100,
      window: 60,
      enabled: true,
    },
  ]);

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 4 }}>
        <SpeedIcon sx={{ fontSize: 40, color: 'primary.main' }} />
        <Box>
          <Typography variant="h4">{t('rateLimiting', 'Rate Limiting')}</Typography>
          <Typography variant="body2" color="text.secondary">
            API istek limitlerini yönetin
          </Typography>
        </Box>
      </Box>

      <Paper sx={{ p: 3, mb: 3 }}>
        <Typography variant="h6" gutterBottom>
          Genel Ayarlar
        </Typography>
        <Grid container spacing={2}>
          <Grid item xs={12} sm={6}>
            <FormControlLabel
              control={<Switch defaultChecked />}
              label="Rate Limiting Aktif"
            />
          </Grid>
          <Grid item xs={12} sm={6}>
            <TextField
              fullWidth
              label="Varsayılan Limit"
              type="number"
              defaultValue={100}
            />
          </Grid>
        </Grid>
      </Paper>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Endpoint</TableCell>
              <TableCell>Method</TableCell>
              <TableCell>Limit</TableCell>
              <TableCell>Pencere (saniye)</TableCell>
              <TableCell>Durum</TableCell>
              <TableCell>İşlemler</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {limits.map((limit) => (
              <TableRow key={limit.id}>
                <TableCell>{limit.endpoint}</TableCell>
                <TableCell>
                  <Chip label={limit.method} size="small" />
                </TableCell>
                <TableCell>{limit.limit}</TableCell>
                <TableCell>{limit.window}</TableCell>
                <TableCell>
                  <Chip
                    label={limit.enabled ? 'Aktif' : 'Pasif'}
                    color={limit.enabled ? 'success' : 'default'}
                    size="small"
                  />
                </TableCell>
                <TableCell>
                  <IconButton size="small">
                    <EditIcon />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </Container>
  );
};

export default RateLimiting;

