// src/pages/LocalGuide.jsx
import React, { useState } from 'react';
import {
  Container, Box, Typography, Grid, Card, CardContent, CardMedia,
  Chip, Button, Tabs, Tab, Paper, Rating
} from '@mui/material';
import RestaurantIcon from '@mui/icons-material/Restaurant';
import LocalActivityIcon from '@mui/icons-material/LocalActivity';
import DirectionsCarIcon from '@mui/icons-material/DirectionsCar';
import HotelIcon from '@mui/icons-material/Hotel';
import LocationOnIcon from '@mui/icons-material/LocationOn';
import { useTranslation } from 'react-i18next';

const LocalGuide = () => {
  const { t } = useTranslation();
  const [activeTab, setActiveTab] = useState(0);

  const categories = [
    { id: 'restaurants', label: 'Restoranlar', icon: <RestaurantIcon /> },
    { id: 'attractions', label: 'Turistik Yerler', icon: <LocalActivityIcon /> },
    { id: 'transport', label: 'Ulaşım', icon: <DirectionsCarIcon /> },
    { id: 'shopping', label: 'Alışveriş', icon: <HotelIcon /> },
  ];

  const restaurants = [
    {
      id: 1,
      name: 'Türk Mutfağı Restoranı',
      category: 'Türk',
      rating: 4.5,
      price: '₺₺',
      image: null,
      address: 'Sultanahmet, İstanbul',
      description: 'Geleneksel Türk mutfağı',
    },
  ];

  const attractions = [
    {
      id: 1,
      name: 'Ayasofya',
      category: 'Tarihi Yer',
      rating: 4.8,
      image: null,
      address: 'Sultanahmet, İstanbul',
      description: 'Bizans ve Osmanlı dönemlerinden kalma tarihi yapı',
    },
  ];

  const getItems = () => {
    switch (activeTab) {
      case 0:
        return restaurants;
      case 1:
        return attractions;
      default:
        return [];
    }
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4" gutterBottom>
        Yerel Rehber
      </Typography>
      <Typography variant="body1" color="text.secondary" sx={{ mb: 4 }}>
        İstanbul'da gezinirken ihtiyacınız olan her şey
      </Typography>

      <Paper sx={{ mb: 3 }}>
        <Tabs value={activeTab} onChange={(e, v) => setActiveTab(v)}>
          {categories.map((cat, index) => (
            <Tab key={cat.id} label={cat.label} icon={cat.icon} iconPosition="start" />
          ))}
        </Tabs>
      </Paper>

      <Grid container spacing={3}>
        {getItems().map((item) => (
          <Grid item xs={12} sm={6} md={4} key={item.id}>
            <Card>
              {item.image && (
                <CardMedia
                  component="img"
                  height="200"
                  image={item.image}
                  alt={item.name}
                />
              )}
              <CardContent>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start', mb: 1 }}>
                  <Typography variant="h6">{item.name}</Typography>
                  <Chip label={item.category} size="small" />
                </Box>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
                  <Rating value={item.rating} readOnly size="small" />
                  <Typography variant="body2" color="text.secondary">
                    {item.rating}
                  </Typography>
                </Box>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
                  <LocationOnIcon fontSize="small" color="action" />
                  <Typography variant="body2" color="text.secondary">
                    {item.address}
                  </Typography>
                </Box>
                <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                  {item.description}
                </Typography>
                <Button variant="outlined" fullWidth>
                  Detaylar
                </Button>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>
    </Container>
  );
};

export default LocalGuide;

