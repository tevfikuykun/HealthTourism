import React, { useState, useEffect } from 'react';
import {
  Dialog,
  DialogContent,
  Box,
  Typography,
  LinearProgress,
  CircularProgress,
  Alert,
  Button,
} from '@mui/material';
import {
  CheckCircle,
  Warning,
  Refresh,
} from '@mui/icons-material';
import { useTranslation } from '../../i18n';
import { useQuery } from '@tanstack/react-query';
import api from '../../services/api';

/**
 * Blockchain Confirmation Component
 * Rezervasyon sonrası Blockchain onayını gösterir
 * Çelişki çözümü: "İşleminiz Blockchain üzerinde onaylanıyor..." loading ekranı
 */
const BlockchainConfirmation = ({ transactionHash, onConfirmed, onError }) => {
  const { t } = useTranslation();
  const [status, setStatus] = useState('PENDING');

  // Poll blockchain transaction status
  const { data: transactionStatus, refetch } = useQuery({
    queryKey: ['blockchain-transaction', transactionHash],
    queryFn: async () => {
      const response = await api.get(`/api/blockchain/transaction/${transactionHash}/status`);
      return response.data;
    },
    refetchInterval: (data) => {
      if (data?.status === 'CONFIRMED' || data?.status === 'FAILED') {
        return false; // Stop polling
      }
      return 3000; // Poll every 3 seconds
    },
    enabled: !!transactionHash,
  });

  useEffect(() => {
    if (transactionStatus) {
      setStatus(transactionStatus.status);
      if (transactionStatus.status === 'CONFIRMED' && onConfirmed) {
        setTimeout(() => onConfirmed(), 1000);
      }
      if (transactionStatus.status === 'FAILED' && onError) {
        onError(transactionStatus.error);
      }
    }
  }, [transactionStatus, onConfirmed, onError]);

  const getStatusContent = () => {
    switch (status) {
      case 'PENDING':
        return (
          <Box sx={{ textAlign: 'center', py: 4 }}>
            <CircularProgress size={60} sx={{ mb: 2 }} />
            <Typography variant="h6" gutterBottom>
              {t('blockchain.confirming')}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              {t('blockchain.confirmingDesc')}
            </Typography>
            <Box sx={{ mt: 2 }}>
              <LinearProgress />
            </Box>
            <Typography variant="caption" color="text.secondary" sx={{ mt: 2, display: 'block' }}>
              {t('blockchain.transactionHash')}: {transactionHash?.substring(0, 16)}...
            </Typography>
          </Box>
        );
      case 'CONFIRMED':
        return (
          <Box sx={{ textAlign: 'center', py: 4 }}>
            <CheckCircle sx={{ fontSize: 60, color: 'success.main', mb: 2 }} />
            <Typography variant="h6" gutterBottom color="success.main">
              {t('blockchain.confirmed')}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              {t('blockchain.confirmedDesc')}
            </Typography>
            <Alert severity="success" sx={{ mt: 2 }}>
              <Typography variant="body2">
                {t('blockchain.blockNumber')}: {transactionStatus?.blockNumber}
              </Typography>
            </Alert>
          </Box>
        );
      case 'FAILED':
        return (
          <Box sx={{ textAlign: 'center', py: 4 }}>
            <Warning sx={{ fontSize: 60, color: 'error.main', mb: 2 }} />
            <Typography variant="h6" gutterBottom color="error.main">
              {t('blockchain.failed')}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              {transactionStatus?.error || t('blockchain.failedDesc')}
            </Typography>
            <Button
              variant="outlined"
              startIcon={<Refresh />}
              onClick={() => refetch()}
              sx={{ mt: 2 }}
            >
              {t('blockchain.retry')}
            </Button>
          </Box>
        );
      default:
        return null;
    }
  };

  return (
    <Dialog open={true} maxWidth="sm" fullWidth>
      <DialogContent>
        {getStatusContent()}
      </DialogContent>
    </Dialog>
  );
};

export default BlockchainConfirmation;

