import React, { useState, useEffect } from 'react'
import { Container, Typography, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper } from '@mui/material'
import { paymentService } from '../services/api'

function Payments() {
  const [payments, setPayments] = useState([])

  useEffect(() => {
    // Örnek: userId = 1
    loadPayments(1)
  }, [])

  const loadPayments = async (userId) => {
    try {
      const response = await paymentService.getByUser(userId)
      setPayments(response.data)
    } catch (error) {
      console.error('Error loading payments:', error)
    }
  }

  return (
    <Container sx={{ py: 4 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        Ödemelerim
      </Typography>
      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Ödeme No</TableCell>
              <TableCell>Miktar</TableCell>
              <TableCell>Yöntem</TableCell>
              <TableCell>Durum</TableCell>
              <TableCell>Tarih</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {payments.map((payment) => (
              <TableRow key={payment.id}>
                <TableCell>{payment.paymentNumber}</TableCell>
                <TableCell>{payment.amount} {payment.currency}</TableCell>
                <TableCell>{payment.paymentMethod}</TableCell>
                <TableCell>{payment.status}</TableCell>
                <TableCell>{new Date(payment.createdAt).toLocaleDateString('tr-TR')}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </Container>
  )
}

export default Payments

