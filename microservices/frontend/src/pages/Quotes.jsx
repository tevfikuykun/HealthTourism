import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  Container,
  Typography,
  Card,
  CardContent,
  Button,
  Chip,
  Grid,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Alert,
} from '@mui/material';
import { quoteService } from '../services/api';
import { useAuth } from '../hooks/useAuth';
import { useTranslation } from '../i18n';
import { formatCurrency } from '../utils/currency';

const Quotes = () => {
  const { t } = useTranslation();
  const { user } = useAuth();
  const queryClient = useQueryClient();
  const [selectedQuote, setSelectedQuote] = useState(null);
  const [rejectDialogOpen, setRejectDialogOpen] = useState(false);
  const [rejectReason, setRejectReason] = useState('');

  const { data: quotes, isLoading } = useQuery({
    queryKey: ['quotes', user?.id],
    queryFn: () => quoteService.getByUser(user?.id),
    enabled: !!user?.id,
  });

  const acceptMutation = useMutation({
    mutationFn: (id) => quoteService.accept(id),
    onSuccess: () => {
      queryClient.invalidateQueries(['quotes']);
      setSelectedQuote(null);
    },
  });

  const rejectMutation = useMutation({
    mutationFn: ({ id, reason }) => quoteService.reject(id, reason),
    onSuccess: () => {
      queryClient.invalidateQueries(['quotes']);
      setRejectDialogOpen(false);
      setRejectReason('');
      setSelectedQuote(null);
    },
  });

  const getStatusColor = (status) => {
    const colors = {
      DRAFT: 'default',
      PENDING: 'warning',
      SENT: 'info',
      ACCEPTED: 'success',
      REJECTED: 'error',
      EXPIRED: 'error',
      CONVERTED: 'success',
    };
    return colors[status] || 'default';
  };

  const handleAccept = (quote) => {
    acceptMutation.mutate(quote.id);
  };

  const handleReject = (quote) => {
    setSelectedQuote(quote);
    setRejectDialogOpen(true);
  };

  const handleRejectConfirm = () => {
    if (selectedQuote && rejectReason) {
      rejectMutation.mutate({ id: selectedQuote.id, reason: rejectReason });
    }
  };

  if (isLoading) {
    return <Container><Typography>Loading...</Typography></Container>;
  }

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4" gutterBottom>
        {t('quotes', 'Tekliflerim')}
      </Typography>

      {quotes && quotes.length === 0 && (
        <Alert severity="info">
          {t('noQuotes', 'Henüz teklif bulunmamaktadır.')}
        </Alert>
      )}

      <Grid container spacing={3}>
        {quotes?.map((quote) => (
          <Grid item xs={12} md={6} key={quote.id}>
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  {t('quoteNumber', 'Teklif No')}: {quote.quoteNumber}
                </Typography>
                <Chip
                  label={quote.status}
                  color={getStatusColor(quote.status)}
                  sx={{ mb: 2 }}
                />
                <Typography variant="body1" gutterBottom>
                  {t('totalPrice', 'Toplam Fiyat')}: {formatCurrency(quote.totalPrice, quote.currency)}
                </Typography>
                {quote.description && (
                  <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                    {quote.description}
                  </Typography>
                )}
                <Typography variant="caption" display="block" sx={{ mb: 2 }}>
                  {t('validUntil', 'Geçerlilik')}: {new Date(quote.validUntil).toLocaleDateString()}
                </Typography>
                {quote.status === 'SENT' && (
                  <div>
                    <Button
                      variant="contained"
                      color="success"
                      sx={{ mr: 1 }}
                      onClick={() => handleAccept(quote)}
                    >
                      {t('accept', 'Kabul Et')}
                    </Button>
                    <Button
                      variant="outlined"
                      color="error"
                      onClick={() => handleReject(quote)}
                    >
                      {t('reject', 'Reddet')}
                    </Button>
                  </div>
                )}
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>

      <Dialog open={rejectDialogOpen} onClose={() => setRejectDialogOpen(false)}>
        <DialogTitle>{t('rejectQuote', 'Teklifi Reddet')}</DialogTitle>
        <DialogContent>
          <TextField
            fullWidth
            multiline
            rows={4}
            label={t('rejectionReason', 'Red Nedeni')}
            value={rejectReason}
            onChange={(e) => setRejectReason(e.target.value)}
            sx={{ mt: 2 }}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setRejectDialogOpen(false)}>
            {t('cancel', 'İptal')}
          </Button>
          <Button onClick={handleRejectConfirm} color="error" variant="contained">
            {t('reject', 'Reddet')}
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default Quotes;
