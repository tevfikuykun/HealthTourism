// src/pages/Invoices.jsx
import React, { useState } from 'react';
import {
  Container, Box, Typography, Table, TableBody, TableCell,
  TableContainer, TableHead, TableRow, Paper, Button, IconButton,
  Chip, Dialog, DialogContent
} from '@mui/material';
import PictureAsPdfIcon from '@mui/icons-material/PictureAsPdf';
import PrintIcon from '@mui/icons-material/Print';
import DownloadIcon from '@mui/icons-material/Download';
import ReceiptIcon from '@mui/icons-material/Receipt';
import { useTranslation } from 'react-i18next';

const Invoices = () => {
  const { t } = useTranslation();
  const [invoices, setInvoices] = useState([
    {
      id: 1,
      invoiceNumber: 'INV-2024-001',
      date: '2024-01-15',
      amount: 5000,
      status: 'paid',
      description: 'Kardiyoloji Konsültasyonu',
    },
  ]);
  const [openPreview, setOpenPreview] = useState(false);
  const [selectedInvoice, setSelectedInvoice] = useState(null);

  const handlePrint = (invoice) => {
    window.print();
  };

  const handleDownloadPDF = (invoice) => {
    // PDF indirme
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 4 }}>
        <ReceiptIcon sx={{ fontSize: 40, color: 'primary.main' }} />
        <Box>
          <Typography variant="h4">{t('myInvoices', 'Faturalarım')}</Typography>
          <Typography variant="body2" color="text.secondary">
            {t('invoicesDescription', 'Tüm faturalarınızı buradan görüntüleyebilir ve indirebilirsiniz')}
          </Typography>
        </Box>
      </Box>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>{t('invoiceNumber', 'Fatura No')}</TableCell>
              <TableCell>{t('date')}</TableCell>
              <TableCell>{t('description')}</TableCell>
              <TableCell>{t('amount')}</TableCell>
              <TableCell>{t('status')}</TableCell>
              <TableCell>{t('actions')}</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {invoices.map((invoice) => (
              <TableRow key={invoice.id}>
                <TableCell>{invoice.invoiceNumber}</TableCell>
                <TableCell>{invoice.date}</TableCell>
                <TableCell>{invoice.description}</TableCell>
                <TableCell>₺{invoice.amount.toLocaleString()}</TableCell>
                <TableCell>
                  <Chip
                    label={invoice.status === 'paid' ? t('paid', 'Ödendi') : t('pending', 'Bekliyor')}
                    color={invoice.status === 'paid' ? 'success' : 'warning'}
                    size="small"
                  />
                </TableCell>
                <TableCell>
                  <IconButton
                    size="small"
                    onClick={() => {
                      setSelectedInvoice(invoice);
                      setOpenPreview(true);
                    }}
                  >
                    <PictureAsPdfIcon />
                  </IconButton>
                  <IconButton
                    size="small"
                    onClick={() => handleDownloadPDF(invoice)}
                  >
                    <DownloadIcon />
                  </IconButton>
                  <IconButton
                    size="small"
                    onClick={() => handlePrint(invoice)}
                  >
                    <PrintIcon />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={openPreview} onClose={() => setOpenPreview(false)} maxWidth="md" fullWidth>
        <DialogContent>
          {selectedInvoice && (
            <Paper sx={{ p: 3 }}>
              <Typography variant="h5" gutterBottom>
                {t('invoice')}: {selectedInvoice.invoiceNumber}
              </Typography>
              <Box sx={{ mt: 3 }}>
                <Typography variant="body1"><strong>{t('date')}:</strong> {selectedInvoice.date}</Typography>
                <Typography variant="body1"><strong>{t('description')}:</strong> {selectedInvoice.description}</Typography>
                <Typography variant="body1"><strong>{t('amount')}:</strong> ₺{selectedInvoice.amount.toLocaleString()}</Typography>
              </Box>
              <Box sx={{ mt: 3, display: 'flex', gap: 2 }}>
                <Button
                  variant="contained"
                  startIcon={<DownloadIcon />}
                  onClick={() => handleDownloadPDF(selectedInvoice)}
                >
                  {t('downloadPDF', 'PDF İndir')}
                </Button>
                <Button
                  variant="outlined"
                  startIcon={<PrintIcon />}
                  onClick={() => handlePrint(selectedInvoice)}
                >
                  {t('print', 'Yazdır')}
                </Button>
              </Box>
            </Paper>
          )}
        </DialogContent>
      </Dialog>
    </Container>
  );
};

export default Invoices;

