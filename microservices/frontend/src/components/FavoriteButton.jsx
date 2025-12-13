import React, { useState } from 'react';
import { IconButton, Tooltip } from '@mui/material';
import FavoriteIcon from '@mui/icons-material/Favorite';
import FavoriteBorderIcon from '@mui/icons-material/FavoriteBorder';
import { useAuth } from '../hooks/useAuth';
import { toast } from 'react-toastify';

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
  const { isAuthenticated } = useAuth();
  const [isFavorite, setIsFavorite] = useState(initialIsFavorite);
  const [isLoading, setIsLoading] = useState(false);

  const handleToggle = async () => {
    if (!isAuthenticated) {
      toast.error('Favori eklemek için giriş yapmanız gerekmektedir.');
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

      toast.success(newFavoriteState ? 'Favorilere eklendi' : 'Favorilerden çıkarıldı');
    } catch (error) {
      toast.error('İşlem sırasında bir hata oluştu');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Tooltip title={isFavorite ? 'Favorilerden çıkar' : 'Favorilere ekle'}>
      <IconButton
        onClick={handleToggle}
        disabled={isLoading}
        color={isFavorite ? 'error' : 'default'}
        aria-label={isFavorite ? 'Remove from favorites' : 'Add to favorites'}
      >
        {isFavorite ? <FavoriteIcon /> : <FavoriteBorderIcon />}
      </IconButton>
    </Tooltip>
  );
}

