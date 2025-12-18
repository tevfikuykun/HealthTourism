import React from 'react';
import {
  Container,
  Box,
  Typography,
  Grid,
  Card,
  CardContent,
  CardMedia,
  IconButton,
  Tabs,
  Tab,
} from '@mui/material';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import ProtectedRoute from '../components/ProtectedRoute';
import Loading from '../components/Loading';
import FavoriteButton from '../components/FavoriteButton';
import { favoriteService } from '../services/api';
import { useAuth } from '../hooks/useAuth';
import DeleteIcon from '@mui/icons-material/Delete';
import { useTranslation } from 'react-i18next';
import { toast } from 'react-toastify';

export default function Favorites() {
  const { t } = useTranslation();
  const { user } = useAuth();
  const queryClient = useQueryClient();
  const [activeTab, setActiveTab] = React.useState(0);

  const itemTypes = ['hospital', 'doctor', 'package'];
  const currentItemType = itemTypes[activeTab] || 'hospital';

  const { data: favorites, isLoading } = useQuery({
    queryKey: ['favorites', user?.id, currentItemType],
    queryFn: async () => {
      if (!user?.id) return { data: [] };
      const response = await favoriteService.getByType(user.id, currentItemType);
      return { data: response.data || [] };
    },
    enabled: !!user?.id,
  });

  const removeFavoriteMutation = useMutation({
    mutationFn: ({ itemId, itemType }) => {
      return favoriteService.remove(user.id, itemType, itemId);
    },
    onSuccess: () => {
      queryClient.invalidateQueries(['favorites']);
      toast.success(t('removedFromFavorites', 'Favorilerden çıkarıldı'));
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || t('errorOccurred', 'İşlem sırasında bir hata oluştu'));
    },
  });

  if (isLoading) {
    return <Loading message={t('loadingFavorites', 'Favoriler yükleniyor...')} />;
  }

  const handleTabChange = (event, newValue) => {
    setActiveTab(newValue);
  };

  return (
    <ProtectedRoute>
      <Container maxWidth="lg" sx={{ py: 4 }}>
        <Typography variant="h4" gutterBottom>
          {t('favorites')}
        </Typography>

        <Tabs value={activeTab} onChange={handleTabChange} sx={{ mb: 3 }}>
          <Tab label={t('hospitals')} />
          <Tab label={t('doctors')} />
          <Tab label={t('packages')} />
        </Tabs>

        {favorites?.data?.length === 0 ? (
          <Box sx={{ textAlign: 'center', py: 8 }}>
            <Typography variant="h6" color="text.secondary" gutterBottom>
              {t('noFavoritesYet', 'Henüz favoriniz bulunmamaktadır.')}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              {t('addToFavoritesDescription', 'Beğendiğiniz hastane, doktor veya paketleri favorilerinize ekleyebilirsiniz.')}
            </Typography>
          </Box>
        ) : (
          <Grid container spacing={3}>
            {favorites?.data?.map((item) => (
              <Grid item xs={12} sm={6} md={4} key={item.id}>
                <Card>
                  {item.image && (
                    <CardMedia component="img" height="200" image={item.image} alt={item.name} />
                  )}
                  <CardContent>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                      <Typography variant="h6">{item.name || item.title || `Item ${item.itemId}`}</Typography>
                      <Box>
                        <FavoriteButton itemId={item.itemId || item.id} itemType={item.itemType || item.type} isFavorite={true} />
                        <IconButton 
                          color="error" 
                          size="small"
                          onClick={() => handleRemove(item.itemId || item.id, item.itemType || item.type)}
                          disabled={removeFavoriteMutation.isPending}
                        >
                          <DeleteIcon />
                        </IconButton>
                      </Box>
                    </Box>
                    <Typography variant="body2" color="text.secondary">
                      {item.description}
                    </Typography>
                  </CardContent>
                </Card>
              </Grid>
            ))}
          </Grid>
        )}
      </Container>
    </ProtectedRoute>
  );
}

