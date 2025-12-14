import React from 'react';
import { Box, Pagination as MuiPagination, Typography } from '@mui/material';
import { useTranslation } from 'react-i18next';

/**
 * Pagination Component
 * 
 * Sayfalama için kullanılan component.
 * 
 * @param {number} currentPage - Mevcut sayfa numarası
 * @param {number} totalPages - Toplam sayfa sayısı
 * @param {number} totalItems - Toplam öğe sayısı
 * @param {number} itemsPerPage - Sayfa başına öğe sayısı
 * @param {function} onPageChange - Sayfa değiştiğinde çağrılacak fonksiyon
 * @param {boolean} showInfo - Sayfa bilgisi gösterilsin mi (default: true)
 */
export default function Pagination({
  currentPage = 1,
  totalPages = 1,
  totalItems = 0,
  itemsPerPage = 10,
  onPageChange,
  showInfo = true,
}) {
  const { t } = useTranslation();
  const handleChange = (event, value) => {
    if (onPageChange) {
      onPageChange(value);
    }
  };

  const startItem = totalItems === 0 ? 0 : (currentPage - 1) * itemsPerPage + 1;
  const endItem = Math.min(currentPage * itemsPerPage, totalItems);

  return (
    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mt: 3, mb: 2 }}>
      {showInfo && totalItems > 0 && (
        <Typography variant="body2" color="text.secondary">
          {t('showingResults', '{start}-{end} / {total} sonuç gösteriliyor', { start: startItem, end: endItem, total: totalItems })}
        </Typography>
      )}
      <MuiPagination
        count={totalPages}
        page={currentPage}
        onChange={handleChange}
        color="primary"
        showFirstButton
        showLastButton
      />
    </Box>
  );
}

