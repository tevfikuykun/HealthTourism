import React, { useState } from 'react';
import { IconButton, Tooltip } from '@mui/material';
import FavoriteIcon from '@mui/icons-material/Favorite';
import FavoriteBorderIcon from '@mui/icons-material/FavoriteBorder';
import { useAuth } from '../hooks/useAuth';
import { toast } from 'react-toastify';
import { useTranslation } from 'react-i18next';

/**
 * FavoriteButton Component
 * 
 * Favori ekleme/çıkarma butonu.
 * 
 * @param {number|string} itemId - Favoriye eklenen öğenin ID'si
 * @param {string} itemType - Öğe tipi (hospital, doctor, package, etc.)
 * @param {boolean} isFavorite - Zaten favori mi (default: false)
 * @param {function} onToggle - Favori durumu değiştiğinde çağrılacak fonksiyon
 */
export default function FavoriteButton({ itemId, itemType, isFavorite: initialIsFavorite = false, onToggle }) {
  const { t } = useTranslation();
  const { isAuthenticated } = useAuth();
  const [isFavorite, setIsFavorite] = useState(initialIsFavorite);
  const [isLoading, setIsLoading] = useState(false);

  const handleToggle = async () => {
    if (!isAuthenticated) {
      toast.error(t('loginRequiredForFavorites', 'Favori eklemek için giriş yapmanız gerekmektedir.'));
      return;
    }

    try {
      setIsLoading(true);
      
      // TODO: Favorite API entegrasyonu
      // if (isFavorite) {
      //   await favoriteService.remove(itemId, itemType);
      // } else {
      //   await favoriteService.add(itemId, itemType);
      // }

      const newFavoriteState = !isFavorite;
      setIsFavorite(newFavoriteState);
      
      if (onToggle) {
        onToggle(newFavoriteState);
      }

      toast.success(newFavoriteState ? t('addedToFavorites', 'Favorilere eklendi') : t('removedFromFavorites', 'Favorilerden çıkarıldı'));
    } catch (error) {
      toast.error(t('errorOccurred', 'İşlem sırasında bir hata oluştu'));
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Tooltip title={isFavorite ? t('removeFromFavorites', 'Favorilerden çıkar') : t('addToFavorites', 'Favorilere ekle')}>
      <IconButton
        onClick={handleToggle}
        disabled={isLoading}
        color={isFavorite ? 'error' : 'default'}
        aria-label={isFavorite ? t('removeFromFavorites', 'Favorilerden çıkar') : t('addToFavorites', 'Favorilere ekle')}
      >
        {isFavorite ? <FavoriteIcon /> : <FavoriteBorderIcon />}
      </IconButton>
    </Tooltip>
  );
}

