import React, { useEffect, useState } from 'react';
import { Alert, Box, Typography } from '@mui/material';
import { Warning, CheckCircle } from '@mui/icons-material';
import { useTranslation } from '../../i18n';
import { useQuery } from '@tanstack/react-query';
import api from '../../services/api';

/**
 * IoT Alert Sync Component
 * IoT servisinin işlenmiş uyarı sonuçlarını AI Companion'a context olarak verir
 * Çelişki çözümü: AI servisi IoT ham verisini değil, işlenmiş "uyarı" sonucunu alır
 */
const IoTAlertSync = ({ userId }) => {
  const { t } = useTranslation();
  const [alerts, setAlerts] = useState([]);

  // Fetch processed IoT alerts (not raw data)
  const { data: processedAlerts } = useQuery({
    queryKey: ['iot-processed-alerts', userId],
    queryFn: async () => {
      const response = await api.get(`/api/iot-monitoring/user/${userId}/alerts`);
      return response.data;
    },
    refetchInterval: 30000,
    enabled: !!userId,
  });

  // Fetch AI context (which uses processed alerts)
  const { data: aiContext } = useQuery({
    queryKey: ['ai-context', userId],
    queryFn: async () => {
      const response = await api.get(`/api/ai-health-companion/context?userId=${userId}`);
      return response.data;
    },
    refetchInterval: 60000,
    enabled: !!userId,
  });

  useEffect(() => {
    if (processedAlerts) {
      setAlerts(processedAlerts.filter((a) => a.severity === 'HIGH' || a.severity === 'CRITICAL'));
    }
  }, [processedAlerts]);

  if (alerts.length === 0) {
    return (
      <Alert severity="success" icon={<CheckCircle />}>
        <Typography variant="body2">
          {t('iot.allClear')} {aiContext?.message && `- ${aiContext.message}`}
        </Typography>
      </Alert>
    );
  }

  return (
    <Box>
      {alerts.map((alert) => (
        <Alert
          key={alert.id}
          severity={alert.severity === 'CRITICAL' ? 'error' : 'warning'}
          icon={<Warning />}
          sx={{ mb: 1 }}
        >
          <Typography variant="subtitle2">{alert.title}</Typography>
          <Typography variant="body2">{alert.message}</Typography>
          {alert.aiRecommendation && (
            <Typography variant="caption" display="block" sx={{ mt: 1 }}>
              <strong>{t('iot.aiRecommendation')}:</strong> {alert.aiRecommendation}
            </Typography>
          )}
        </Alert>
      ))}
    </Box>
  );
};

export default IoTAlertSync;



