// src/components/Map/HospitalMap.jsx
import React, { useEffect, useRef } from 'react';
import { Box, Paper, Typography } from '@mui/material';

const HospitalMap = ({ hospitals = [], center = { lat: 41.0082, lng: 28.9784 }, zoom = 12 }) => {
  const mapRef = useRef(null);
  const mapInstanceRef = useRef(null);

  useEffect(() => {
    // Google Maps veya Mapbox entegrasyonu
    // Bu örnek Google Maps için
    if (window.google && window.google.maps) {
      if (!mapInstanceRef.current && mapRef.current) {
        mapInstanceRef.current = new window.google.maps.Map(mapRef.current, {
          center: center,
          zoom: zoom,
        });

        // Hastane marker'ları ekle
        hospitals.forEach((hospital) => {
          if (hospital.latitude && hospital.longitude) {
            new window.google.maps.Marker({
              position: { lat: hospital.latitude, lng: hospital.longitude },
              map: mapInstanceRef.current,
              title: hospital.name,
            });
          }
        });
      }
    } else {
      // Google Maps yüklenmemişse, fallback göster
      console.warn('Google Maps API not loaded');
    }
  }, [hospitals, center, zoom]);

  return (
    <Paper sx={{ p: 2 }}>
      <Typography variant="h6" gutterBottom>
        Hastane Konumları
      </Typography>
      <Box
        ref={mapRef}
        sx={{
          width: '100%',
          height: 400,
          bgcolor: 'grey.200',
          borderRadius: 1,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        }}
      >
        {!window.google && (
          <Typography variant="body2" color="text.secondary">
            Harita yükleniyor... (Google Maps API key gerekli)
          </Typography>
        )}
      </Box>
    </Paper>
  );
};

export default HospitalMap;

