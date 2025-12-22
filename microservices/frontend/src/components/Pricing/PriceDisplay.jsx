import React from 'react';
import { Box, Typography, Chip, Tooltip } from '@mui/material';
import { TrendingUp, Info } from '@mui/icons-material';
import { useTranslation } from '../../i18n';

/**
 * Price Display Component
 * AI tahminleri ve statik fiyatları tutarlı şekilde gösterir
 * Çelişki çözümü: AI tahminlerinin yanına "Tahmini Değer" ibaresi
 */
const PriceDisplay = ({ price, isAIEstimated = false, isStatic = false, currency = 'USD' }) => {
  const { t } = useTranslation();

  const formatPrice = (amount) => {
    const symbols = {
      USD: '$',
      EUR: '€',
      TRY: '₺',
      HT: 'HT',
    };
    return `${symbols[currency] || ''}${amount.toLocaleString()}`;
  };

  return (
    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
      <Typography variant="h5" component="span" fontWeight="bold">
        {formatPrice(price)}
      </Typography>
      {isAIEstimated && (
        <Tooltip title={t('pricing.aiEstimatedTooltip')}>
          <Chip
            icon={<TrendingUp />}
            label={t('pricing.estimated')}
            size="small"
            color="info"
            variant="outlined"
          />
        </Tooltip>
      )}
      {isStatic && (
        <Tooltip title={t('pricing.startingPriceTooltip')}>
          <Chip
            icon={<Info />}
            label={t('pricing.startingPrice')}
            size="small"
            color="default"
            variant="outlined"
          />
        </Tooltip>
      )}
    </Box>
  );
};

export default PriceDisplay;






