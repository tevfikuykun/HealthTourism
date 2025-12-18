import React from 'react';
import { useQuery } from '@tanstack/react-query';
import {
  Container,
  Typography,
  Card,
  CardContent,
  Chip,
  Grid,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
} from '@mui/material';
import { crmService } from '../services/api';
import { useTranslation } from '../i18n';

const Leads = () => {
  const { t } = useTranslation();

  const { data: leads, isLoading } = useQuery({
    queryKey: ['leads'],
    queryFn: () => crmService.getAllLeads(),
  });

  const getStatusColor = (status) => {
    const colors = {
      NEW: 'default',
      CONTACTED: 'info',
      QUALIFIED: 'primary',
      DOCUMENT_PENDING: 'warning',
      QUOTE_SENT: 'info',
      QUOTE_ACCEPTED: 'success',
      PAYMENT_PENDING: 'warning',
      CONVERTED: 'success',
      LOST: 'error',
    };
    return colors[status] || 'default';
  };

  if (isLoading) {
    return <Container><Typography>Loading...</Typography></Container>;
  }

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4" gutterBottom>
        {t('leads', 'Aday Müşteriler')}
      </Typography>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>{t('name', 'İsim')}</TableCell>
              <TableCell>{t('email', 'E-posta')}</TableCell>
              <TableCell>{t('status', 'Durum')}</TableCell>
              <TableCell>{t('source', 'Kaynak')}</TableCell>
              <TableCell>{t('createdAt', 'Oluşturulma')}</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {leads?.map((lead) => (
              <TableRow key={lead.id}>
                <TableCell>{lead.firstName} {lead.lastName}</TableCell>
                <TableCell>{lead.email}</TableCell>
                <TableCell>
                  <Chip
                    label={lead.status}
                    color={getStatusColor(lead.status)}
                    size="small"
                  />
                </TableCell>
                <TableCell>{lead.source}</TableCell>
                <TableCell>{new Date(lead.createdAt).toLocaleDateString()}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </Container>
  );
};

export default Leads;
