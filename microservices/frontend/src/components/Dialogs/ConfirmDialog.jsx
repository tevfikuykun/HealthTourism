import React from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions,
  Button,
} from '@mui/material';

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
  title = 'Onay',
  message = 'Bu işlemi yapmak istediğinizden emin misiniz?',
  confirmText = 'Onayla',
  cancelText = 'İptal',
}) {
  const handleConfirm = () => {
    onConfirm();
    onClose();
  };

  return (
    <Dialog open={open} onClose={onClose}>
      <DialogTitle>{title}</DialogTitle>
      <DialogContent>
        <DialogContentText>{message}</DialogContentText>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>{cancelText}</Button>
        <Button onClick={handleConfirm} variant="contained" autoFocus>
          {confirmText}
        </Button>
      </DialogActions>
    </Dialog>
  );
}

