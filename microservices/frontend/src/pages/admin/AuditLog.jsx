// src/pages/admin/AuditLog.jsx
import React, { useState } from 'react';
import {
  Container, Box, Typography, Table, TableBody, TableCell,
  TableContainer, TableHead, TableRow, Paper, TextField, Button,
  Chip, Select, MenuItem, FormControl, InputLabel, Pagination
} from '@mui/material';
import HistoryIcon from '@mui/icons-material/History';
import FilterListIcon from '@mui/icons-material/FilterList';
import { useTranslation } from 'react-i18next';

const AuditLog = () => {
  const { t } = useTranslation();
  const [logs, setLogs] = useState([
    {
      id: 1,
      user: 'Ahmet Yılmaz',
      action: 'LOGIN',
      resource: 'User',
      resourceId: 123,
      ipAddress: '192.168.1.1',
      timestamp: '2024-01-20 10:30:00',
      status: 'success',
    },
    {
      id: 2,
      user: 'Admin',
      action: 'DELETE',
      resource: 'Reservation',
      resourceId: 456,
      ipAddress: '192.168.1.2',
      timestamp: '2024-01-20 11:15:00',
      status: 'success',
    },
  ]);
  const [actionFilter, setActionFilter] = useState('all');

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 4 }}>
        <HistoryIcon sx={{ fontSize: 40, color: 'primary.main' }} />
        <Box>
          <Typography variant="h4">Audit Log</Typography>
          <Typography variant="body2" color="text.secondary">
            Tüm sistem işlemlerinin kaydı
          </Typography>
        </Box>
      </Box>

      <Paper sx={{ p: 2, mb: 3 }}>
        <Box sx={{ display: 'flex', gap: 2, alignItems: 'center' }}>
          <TextField
            size="small"
            label="Kullanıcı Ara"
            sx={{ flexGrow: 1 }}
          />
          <FormControl size="small" sx={{ minWidth: 150 }}>
            <InputLabel>İşlem Tipi</InputLabel>
            <Select
              value={actionFilter}
              onChange={(e) => setActionFilter(e.target.value)}
              label="İşlem Tipi"
            >
              <MenuItem value="all">Tümü</MenuItem>
              <MenuItem value="CREATE">Oluştur</MenuItem>
              <MenuItem value="UPDATE">Güncelle</MenuItem>
              <MenuItem value="DELETE">Sil</MenuItem>
              <MenuItem value="LOGIN">Giriş</MenuItem>
            </Select>
          </FormControl>
          <TextField
            size="small"
            label="Tarih"
            type="date"
            InputLabelProps={{ shrink: true }}
          />
          <Button variant="outlined" startIcon={<FilterListIcon />}>
            Filtrele
          </Button>
        </Box>
      </Paper>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>ID</TableCell>
              <TableCell>Kullanıcı</TableCell>
              <TableCell>İşlem</TableCell>
              <TableCell>Kaynak</TableCell>
              <TableCell>Kaynak ID</TableCell>
              <TableCell>IP Adresi</TableCell>
              <TableCell>Tarih/Saat</TableCell>
              <TableCell>Durum</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {logs.map((log) => (
              <TableRow key={log.id}>
                <TableCell>{log.id}</TableCell>
                <TableCell>{log.user}</TableCell>
                <TableCell>
                  <Chip label={log.action} size="small" color="primary" />
                </TableCell>
                <TableCell>{log.resource}</TableCell>
                <TableCell>{log.resourceId}</TableCell>
                <TableCell>{log.ipAddress}</TableCell>
                <TableCell>{log.timestamp}</TableCell>
                <TableCell>
                  <Chip
                    label={log.status === 'success' ? 'Başarılı' : 'Başarısız'}
                    color={log.status === 'success' ? 'success' : 'error'}
                    size="small"
                  />
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      <Box sx={{ display: 'flex', justifyContent: 'center', mt: 3 }}>
        <Pagination count={10} color="primary" />
      </Box>
    </Container>
  );
};

export default AuditLog;

