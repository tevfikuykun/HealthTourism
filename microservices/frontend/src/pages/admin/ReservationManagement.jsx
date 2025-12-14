// src/pages/admin/ReservationManagement.jsx
import React, { useState } from 'react';
import {
  Container, Box, Typography, Table, TableBody, TableCell,
  TableContainer, TableHead, TableRow, Paper, Button, TextField,
  Chip, IconButton, Menu, MenuItem, Select, FormControl, InputLabel
} from '@mui/material';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import CancelIcon from '@mui/icons-material/Cancel';
import EditIcon from '@mui/icons-material/Edit';
import FilterListIcon from '@mui/icons-material/FilterList';
import { useTranslation } from 'react-i18next';

const ReservationManagement = () => {
  const { t } = useTranslation();
  const [reservations, setReservations] = useState([
    {
      id: 1,
      user: 'Ahmet Yılmaz',
      service: 'Kardiyoloji Konsültasyonu',
      doctor: 'Dr. Mehmet Demir',
      date: '2024-02-01',
      time: '10:00',
      status: 'pending',
      amount: 5000,
    },
  ]);
  const [statusFilter, setStatusFilter] = useState('all');

  const handleStatusChange = (id, newStatus) => {
    setReservations(reservations.map(r =>
      r.id === id ? { ...r, status: newStatus } : r
    ));
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
        <Typography variant="h4">Rezervasyon Yönetimi</Typography>
        <Box sx={{ display: 'flex', gap: 2 }}>
          <FormControl size="small" sx={{ minWidth: 150 }}>
            <InputLabel>Durum Filtresi</InputLabel>
            <Select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              label="Durum Filtresi"
            >
              <MenuItem value="all">Tümü</MenuItem>
              <MenuItem value="pending">Bekliyor</MenuItem>
              <MenuItem value="confirmed">Onaylandı</MenuItem>
              <MenuItem value="cancelled">İptal</MenuItem>
            </Select>
          </FormControl>
        </Box>
      </Box>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>ID</TableCell>
              <TableCell>Kullanıcı</TableCell>
              <TableCell>Hizmet</TableCell>
              <TableCell>Doktor</TableCell>
              <TableCell>Tarih</TableCell>
              <TableCell>Saat</TableCell>
              <TableCell>Tutar</TableCell>
              <TableCell>Durum</TableCell>
              <TableCell>İşlemler</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {reservations.map((reservation) => (
              <TableRow key={reservation.id}>
                <TableCell>{reservation.id}</TableCell>
                <TableCell>{reservation.user}</TableCell>
                <TableCell>{reservation.service}</TableCell>
                <TableCell>{reservation.doctor}</TableCell>
                <TableCell>{reservation.date}</TableCell>
                <TableCell>{reservation.time}</TableCell>
                <TableCell>₺{reservation.amount.toLocaleString()}</TableCell>
                <TableCell>
                  <Chip
                    label={
                      reservation.status === 'pending' ? 'Bekliyor' :
                      reservation.status === 'confirmed' ? 'Onaylandı' : 'İptal'
                    }
                    color={
                      reservation.status === 'pending' ? 'warning' :
                      reservation.status === 'confirmed' ? 'success' : 'error'
                    }
                    size="small"
                  />
                </TableCell>
                <TableCell>
                  <IconButton
                    size="small"
                    color="success"
                    onClick={() => handleStatusChange(reservation.id, 'confirmed')}
                  >
                    <CheckCircleIcon />
                  </IconButton>
                  <IconButton
                    size="small"
                    color="error"
                    onClick={() => handleStatusChange(reservation.id, 'cancelled')}
                  >
                    <CancelIcon />
                  </IconButton>
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

export default ReservationManagement;

