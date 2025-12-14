import React from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions,
  Button,
} from '@mui/material';
import { useTranslation } from 'react-i18next';

/**
 * ConfirmDialog Component
 * 
 * Onay dialog'u.
 * 
 * @param {boolean} open - Dialog açık mı
 * @param {function} onClose - Dialog kapatıldığında çağrılacak fonksiyon
 * @param {function} onConfirm - Onaylandığında çağrılacak fonksiyon
 * @param {string} title - Başlık
 * @param {string} message - Mesaj
 * @param {string} confirmText - Onay butonu metni
 * @param {string} cancelText - İptal butonu metni
 */
export default function ConfirmDialog({
  open,
  onClose,
  onConfirm,
  title,
  message,
  confirmText,
  cancelText,
}) {
  const { t } = useTranslation();
  const defaultTitle = title || t('confirm', 'Onay');
  const defaultMessage = message || t('confirmAction', 'Bu işlemi yapmak istediğinizden emin misiniz?');
  const defaultConfirmText = confirmText || t('confirm', 'Onayla');
  const defaultCancelText = cancelText || t('cancel', 'İptal');
  const handleConfirm = () => {
    onConfirm();
    onClose();
  };

  return (
    <Dialog open={open} onClose={onClose}>
      <DialogTitle>{defaultTitle}</DialogTitle>
      <DialogContent>
        <DialogContentText>{defaultMessage}</DialogContentText>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>{defaultCancelText}</Button>
        <Button onClick={handleConfirm} variant="contained" autoFocus>
          {defaultConfirmText}
        </Button>
      </DialogActions>
    </Dialog>
  );
}

