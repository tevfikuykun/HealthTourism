// src/pages/WeatherWidget.jsx
import React, { useState, useEffect } from 'react';
import {
  Box, Card, CardContent, Typography, Chip
} from '@mui/material';
import WbSunnyIcon from '@mui/icons-material/WbSunny';
import CloudIcon from '@mui/icons-material/Cloud';
import AcUnitIcon from '@mui/icons-material/AcUnit';
import { useTranslation } from 'react-i18next';

const WeatherWidget = ({ city = 'Istanbul' }) => {
  const { t } = useTranslation();
  const [weather, setWeather] = useState({
    temperature: 15,
    condition: 'Sunny',
    humidity: 65,
    windSpeed: 10,
  });

  const getWeatherIcon = (condition) => {
    if (condition.toLowerCase().includes('sun')) {
      return <WbSunnyIcon sx={{ fontSize: 48, color: 'warning.main' }} />;
    } else if (condition.toLowerCase().includes('cloud')) {
      return <CloudIcon sx={{ fontSize: 48, color: 'grey.500' }} />;
    } else {
      return <AcUnitIcon sx={{ fontSize: 48, color: 'info.main' }} />;
    }
  };

  return (
    <Card>
      <CardContent>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Box>
            <Typography variant="h6">{city}</Typography>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mt: 1 }}>
              {getWeatherIcon(weather.condition)}
              <Typography variant="h4">{weather.temperature}°C</Typography>
            </Box>
            <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
              {weather.condition}
            </Typography>
          </Box>
          <Box sx={{ textAlign: 'right' }}>
            <Chip label={`${t('humidity', 'Nem')}: ${weather.humidity}%`} size="small" sx={{ mb: 1, display: 'block' }} />
            <Chip label={`${t('wind', 'Rüzgar')}: ${weather.windSpeed} km/h`} size="small" />
          </Box>
        </Box>
      </CardContent>
    </Card>
  );
};

export default WeatherWidget;

