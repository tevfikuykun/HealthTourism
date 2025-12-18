import React, { useState, useEffect } from 'react';
import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Button,
  Chip,
  TextField,
  Box,
  Typography,
} from '@mui/material';
import { api } from '../services/api';

function Reservations() {
  const [reservations, setReservations] = useState([]);
  const [filter, setFilter] = useState('');

  useEffect(() => {
    fetchReservations();
  }, []);

  const fetchReservations = async () => {
    try {
      // Fetch reservations from API
      // Simplified - should fetch from actual API
      setReservations([
        {
          id: 1,
          reservationNumber: 'HT-202401-001',
          userId: 123,
          status: 'PENDING',
          totalPrice: 5000,
          createdAt: '2024-01-15T10:00:00Z',
        },
      ]);
    } catch (error) {
      console.error('Error fetching reservations:', error);
    }
  };

  const handleStatusUpdate = async (id, newStatus) => {
    try {
      // Update reservation status
      await api.reservationService.updateStatus(id, newStatus);
      fetchReservations();
    } catch (error) {
      console.error('Error updating status:', error);
    }
  };

  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        Reservation Management
      </Typography>
      
      <TextField
        label="Search"
        variant="outlined"
        fullWidth
        margin="normal"
        value={filter}
        onChange={(e) => setFilter(e.target.value)}
      />
      
      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Reservation Number</TableCell>
              <TableCell>User ID</TableCell>
              <TableCell>Status</TableCell>
              <TableCell>Total Price</TableCell>
              <TableCell>Created At</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {reservations.map((reservation) => (
              <TableRow key={reservation.id}>
                <TableCell>{reservation.reservationNumber}</TableCell>
                <TableCell>{reservation.userId}</TableCell>
                <TableCell>
                  <Chip
                    label={reservation.status}
                    color={
                      reservation.status === 'CONFIRMED' ? 'success' :
                      reservation.status === 'PENDING' ? 'warning' : 'error'
                    }
                  />
                </TableCell>
                <TableCell>${reservation.totalPrice}</TableCell>
                <TableCell>{new Date(reservation.createdAt).toLocaleString()}</TableCell>
                <TableCell>
                  <Button
                    size="small"
                    onClick={() => handleStatusUpdate(reservation.id, 'CONFIRMED')}
                  >
                    Confirm
                  </Button>
                  <Button
                    size="small"
                    color="error"
                    onClick={() => handleStatusUpdate(reservation.id, 'CANCELLED')}
                  >
                    Cancel
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  );
}

export default Reservations;
