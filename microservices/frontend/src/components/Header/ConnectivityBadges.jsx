import React from 'react';
import { Box, Chip, Tooltip, IconButton, LinearProgress } from '@mui/material';
import {
  CheckCircle,
  Warning,
  Sync,
  SyncDisabled,
  CloudDone,
  CloudOff,
} from '@mui/icons-material';
import { useTranslation } from '../../i18n';

/**
 * Connectivity Badges Component
 * Blockchain ve IoT bağlantı durumlarını gösterir
 */
const ConnectivityBadges = ({ blockchainStatus, iotStatus, compact = false }) => {
  const { t } = useTranslation();

  const getBlockchainStatus = () => {
    if (!blockchainStatus) {
      return {
        connected: false,
        label: t('status.disconnected'),
        color: 'error',
        icon: <CloudOff fontSize="small" />,
      };
    }

    if (blockchainStatus.connected) {
      return {
        connected: true,
        label: `${blockchainStatus.network || 'Polygon'} - ${t('status.connected')}`,
        color: 'success',
        icon: <CloudDone fontSize="small" />,
      };
    }

    return {
      connected: false,
      label: t('status.disconnected'),
      color: 'error',
      icon: <Warning fontSize="small" />,
    };
  };

  const getIoTStatus = () => {
    if (!iotStatus) {
      return {
        syncing: false,
        label: t('status.noData'),
        color: 'default',
        icon: <SyncDisabled fontSize="small" />,
      };
    }

    const timeSinceLastUpdate = new Date() - new Date(iotStatus.timestamp);
    const isRecent = timeSinceLastUpdate < 60000; // 1 minute

    if (isRecent) {
      return {
        syncing: true,
        label: t('status.live'),
        color: 'success',
        icon: <Sync fontSize="small" sx={{ animation: 'spin 2s linear infinite' }} />,
      };
    }

    return {
      syncing: false,
      label: t('status.syncing'),
      color: 'warning',
      icon: <Sync fontSize="small" />,
    };
  };

  const blockchain = getBlockchainStatus();
  const iot = getIoTStatus();

  if (compact) {
    return (
      <Box sx={{ display: 'flex', gap: 0.5 }}>
        <Tooltip title={blockchain.label}>
          <IconButton size="small" color={blockchain.color}>
            {blockchain.icon}
          </IconButton>
        </Tooltip>
        <Tooltip title={iot.label}>
          <IconButton size="small" color={iot.color}>
            {iot.icon}
          </IconButton>
        </Tooltip>
      </Box>
    );
  }

  return (
    <Box sx={{ display: 'flex', gap: 1, alignItems: 'center' }}>
      <Tooltip title={blockchain.label}>
        <Chip
          icon={blockchain.icon}
          label={blockchain.network || 'Blockchain'}
          size="small"
          color={blockchain.color}
          variant="outlined"
        />
      </Tooltip>
      <Tooltip title={iot.label}>
        <Chip
          icon={iot.icon}
          label={iot.label}
          size="small"
          color={iot.color}
          variant="outlined"
        />
      </Tooltip>
    </Box>
  );
};

export default ConnectivityBadges;

