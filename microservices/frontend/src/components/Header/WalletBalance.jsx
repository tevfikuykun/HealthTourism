import React from 'react';
import { Chip, Tooltip, Box, Typography } from '@mui/material';
import { AccountBalanceWallet, TrendingUp, TrendingDown } from '@mui/icons-material';
import { useTranslation } from '../../i18n';
import { useNavigate } from 'react-router-dom';

/**
 * Wallet Balance Component
 * Health Token bakiyesini gösterir
 * Blockchain entegrasyonunu görsel olarak tesciller
 */
const WalletBalance = ({ balance, trend, compact = false }) => {
  const { t } = useTranslation();
  const navigate = useNavigate();

  if (!balance) return null;

  const handleClick = () => {
    navigate('/health-wallet');
  };

  if (compact) {
    return (
      <Tooltip title={`${balance.balance} Health Tokens`}>
        <Chip
          icon={<AccountBalanceWallet />}
          label={`${balance.balance} HT`}
          size="small"
          color="primary"
          variant="outlined"
          onClick={handleClick}
          sx={{ cursor: 'pointer' }}
        />
      </Tooltip>
    );
  }

  return (
    <Tooltip title={t('header.walletTooltip')}>
      <Chip
        icon={<AccountBalanceWallet />}
        label={
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
            <Typography variant="body2" fontWeight="bold">
              {balance.balance}
            </Typography>
            <Typography variant="caption" color="text.secondary">
              HT
            </Typography>
            {trend && (
              trend === 'UP' ? (
                <TrendingUp fontSize="small" color="success" />
              ) : (
                <TrendingDown fontSize="small" color="error" />
              )
            )}
          </Box>
        }
        size="small"
        color="primary"
        variant="outlined"
        onClick={handleClick}
        sx={{ cursor: 'pointer' }}
      />
    </Tooltip>
  );
};

export default WalletBalance;






