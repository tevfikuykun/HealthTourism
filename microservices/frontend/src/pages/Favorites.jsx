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
import { useQuery } from '@tanstack/react-query';
import ProtectedRoute from '../components/ProtectedRoute';
import Loading from '../components/Loading';
import FavoriteButton from '../components/FavoriteButton';
import DeleteIcon from '@mui/icons-material/Delete';

export default function Favorites() {
  const [activeTab, setActiveTab] = React.useState(0);

  // TODO: Favorites API entegrasyonu
  const { data: favorites, isLoading } = useQuery({
    queryKey: ['favorites'],
    queryFn: async () => {
      // return await favoriteService.getAll();
      return { data: [] };
    },
  });

  if (isLoading) {
    return <Loading message="Favoriler yükleniyor..." />;
  }

  const handleTabChange = (event, newValue) => {
    setActiveTab(newValue);
  };

  return (
    <ProtectedRoute>
      <Container maxWidth="lg" sx={{ py: 4 }}>
        <Typography variant="h4" gutterBottom>
          Favorilerim
        </Typography>

        <Tabs value={activeTab} onChange={handleTabChange} sx={{ mb: 3 }}>
          <Tab label="Hastaneler" />
          <Tab label="Doktorlar" />
          <Tab label="Paketler" />
        </Tabs>

        {favorites?.data?.length === 0 ? (
          <Box sx={{ textAlign: 'center', py: 8 }}>
            <Typography variant="h6" color="text.secondary" gutterBottom>
              Henüz favoriniz bulunmamaktadır.
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Beğendiğiniz hastane, doktor veya paketleri favorilerinize ekleyebilirsiniz.
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
                      <Typography variant="h6">{item.name}</Typography>
                      <Box>
                        <FavoriteButton itemId={item.id} itemType={item.type} isFavorite={true} />
                        <IconButton color="error" size="small">
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

