import React, { useState, useEffect } from 'react';
import {
  FormControlLabel,
  Switch,
  Tooltip,
  Box,
  Chip,
} from '@mui/material';
import { Translate, TranslateOff } from '@mui/icons-material';
import { useTranslation } from '../../i18n';

/**
 * Real-time Translation Toggle
 * WebRTC görüşmelerinde veya chat'te tercüme özelliğini kontrol eder
 */
const TranslationToggle = ({ onToggle, compact = false }) => {
  const { t } = useTranslation();
  const [enabled, setEnabled] = useState(
    localStorage.getItem('translationEnabled') === 'true'
  );

  useEffect(() => {
    if (onToggle) {
      onToggle(enabled);
    }
  }, [enabled, onToggle]);

  const handleToggle = (event) => {
    const newValue = event.target.checked;
    setEnabled(newValue);
    localStorage.setItem('translationEnabled', newValue.toString());
  };

  if (compact) {
    return (
      <Tooltip title={enabled ? t('header.translationOn') : t('header.translationOff')}>
        <Chip
          icon={enabled ? <Translate /> : <TranslateOff />}
          label={enabled ? t('header.translate') : t('header.noTranslate')}
          size="small"
          color={enabled ? 'primary' : 'default'}
          variant={enabled ? 'filled' : 'outlined'}
          onClick={() => setEnabled(!enabled)}
          sx={{ cursor: 'pointer' }}
        />
      </Tooltip>
    );
  }

  return (
    <Tooltip title={t('header.realTimeTranslation')}>
      <FormControlLabel
        control={
          <Switch
            size="small"
            checked={enabled}
            onChange={handleToggle}
            icon={<TranslateOff />}
            checkedIcon={<Translate />}
          />
        }
        label={
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
            {enabled ? <Translate fontSize="small" /> : <TranslateOff fontSize="small" />}
            <span style={{ fontSize: '0.75rem' }}>
              {enabled ? t('header.translate') : t('header.noTranslate')}
            </span>
          </Box>
        }
        sx={{ m: 0 }}
      />
    </Tooltip>
  );
};

export default TranslationToggle;


