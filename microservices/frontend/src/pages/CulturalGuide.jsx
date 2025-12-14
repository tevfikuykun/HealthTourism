// src/pages/CulturalGuide.jsx
import React, { useState } from 'react';
import {
    Container, Box, Typography, Grid, Card, CardContent, CardMedia,
    Tabs, Tab, Chip, List, ListItem, ListItemIcon, ListItemText
} from '@mui/material';
import RestaurantIcon from '@mui/icons-material/Restaurant';
import PlaceIcon from '@mui/icons-material/Place';
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';
import MosqueIcon from '@mui/icons-material/Mosque';
import SEOHead from '../components/SEO/SEOHead';
import { useTranslation } from 'react-i18next';

export default function CulturalGuide() {
    const { t } = useTranslation();
    const [tabValue, setTabValue] = useState(0);

    const culturalPlaces = [
        { name: 'Ayasofya', type: 'Müze', description: 'Bizans ve Osmanlı dönemlerinden kalma tarihi yapı' },
        { name: 'Topkapı Sarayı', type: 'Müze', description: 'Osmanlı İmparatorluğu\'nun eski sarayı' },
        { name: 'Sultanahmet Camii', type: 'Dini Mekan', description: 'Mavi Cami olarak bilinen tarihi cami' }
    ];

    const restaurants = [
        { name: 'Halal Restoranlar', description: 'Helal sertifikalı restoranlar listesi' },
        { name: 'Vejetaryen Seçenekler', description: 'Vejetaryen ve vegan dostu restoranlar' },
        { name: 'Geleneksel Türk Mutfağı', description: 'Otantik Türk yemekleri' }
    ];

    const shopping = [
        { name: 'Grand Bazaar', description: 'Tarihi kapalı çarşı, hediyelik eşya' },
        { name: 'Spice Bazaar', description: 'Baharat ve lokum çarşısı' },
        { name: 'Modern Alışveriş Merkezleri', description: 'AVM\'ler ve marka mağazalar' }
    ];

    return (
        <>
            <SEOHead
                title="Kültürel ve Yaşam Rehberi - Health Tourism"
                description="İstanbul kültürel rehberi, yemek rehberi, alışveriş ve dini mekanlar"
                keywords="kültürel rehber, İstanbul, yemek rehberi, alışveriş, dini mekanlar"
            />
            <Container maxWidth="lg" sx={{ py: 6 }}>
                <Typography variant="h3" component="h1" gutterBottom fontWeight="bold" align="center">
                    Kültürel ve Yaşam Rehberi
                </Typography>
                <Typography variant="body1" color="text.secondary" align="center" sx={{ mb: 4 }}>
                    İstanbul'da geçireceğiniz süre boyunca size rehberlik edecek bilgiler
                </Typography>

                <Tabs value={tabValue} onChange={(e, v) => setTabValue(v)} centered sx={{ mb: 4 }}>
                    <Tab label="Kültürel Yerler" icon={<PlaceIcon />} />
                    <Tab label="Yemek Rehberi" icon={<RestaurantIcon />} />
                    <Tab label="Alışveriş" icon={<ShoppingCartIcon />} />
                    <Tab label="Dini Mekanlar" icon={<MosqueIcon />} />
                </Tabs>

                {tabValue === 0 && (
                    <Grid container spacing={3}>
                        {culturalPlaces.map((place, index) => (
                            <Grid item xs={12} md={4} key={index}>
                                <Card>
                                    <CardContent>
                                        <Chip label={place.type} color="primary" sx={{ mb: 1 }} />
                                        <Typography variant="h6" gutterBottom>
                                            {place.name}
                                        </Typography>
                                        <Typography variant="body2" color="text.secondary">
                                            {place.description}
                                        </Typography>
                                    </CardContent>
                                </Card>
                            </Grid>
                        ))}
                    </Grid>
                )}

                {tabValue === 1 && (
                    <Grid container spacing={3}>
                        {restaurants.map((restaurant, index) => (
                            <Grid item xs={12} md={4} key={index}>
                                <Card>
                                    <CardContent>
                                        <Typography variant="h6" gutterBottom>
                                            {restaurant.name}
                                        </Typography>
                                        <Typography variant="body2" color="text.secondary">
                                            {restaurant.description}
                                        </Typography>
                                    </CardContent>
                                </Card>
                            </Grid>
                        ))}
                    </Grid>
                )}

                {tabValue === 2 && (
                    <Grid container spacing={3}>
                        {shopping.map((shop, index) => (
                            <Grid item xs={12} md={4} key={index}>
                                <Card>
                                    <CardContent>
                                        <Typography variant="h6" gutterBottom>
                                            {shop.name}
                                        </Typography>
                                        <Typography variant="body2" color="text.secondary">
                                            {shop.description}
                                        </Typography>
                                    </CardContent>
                                </Card>
                            </Grid>
                        ))}
                    </Grid>
                )}

                {tabValue === 3 && (
                    <Box>
                        <Typography variant="h5" gutterBottom>
                            Dini Mekanlar
                        </Typography>
                        <List>
                            <ListItem>
                                <ListItemIcon><MosqueIcon color="primary" /></ListItemIcon>
                                <ListItemText 
                                    primary="Sultanahmet Camii"
                                    secondary="Mavi Cami - Tarihi cami, ziyaret saatleri: 09:00-18:00"
                                />
                            </ListItem>
                            <ListItem>
                                <ListItemIcon><MosqueIcon color="primary" /></ListItemIcon>
                                <ListItemText 
                                    primary="Süleymaniye Camii"
                                    secondary="Osmanlı mimarisinin önemli örneklerinden"
                                />
                            </ListItem>
                        </List>
                    </Box>
                )}
            </Container>
        </>
    );
}

