import React, { useState, useEffect } from 'react';
import { Box, Typography, Card, CardContent, Slider, Grid } from '@mui/material';
import { useTranslation } from 'react-i18next';

/**
 * Digital Twin Visualization
 * Hastanın vücudundaki iyileşmeyi gösteren 3D/2D insan modeli
 * SVG üzerinden renklendirilmiş
 */
const DigitalTwinVisualization = ({ recoveryScore, iotData }) => {
  const { t } = useTranslation();
  const [selectedArea, setSelectedArea] = useState(null);

  // Calculate health status for each body area
  const bodyAreas = {
    head: { score: 85, color: getHealthColor(85) },
    chest: { score: recoveryScore || 75, color: getHealthColor(recoveryScore || 75) },
    abdomen: { score: 70, color: getHealthColor(70) },
    leftArm: { score: 80, color: getHealthColor(80) },
    rightArm: { score: 80, color: getHealthColor(80) },
    leftLeg: { score: 75, color: getHealthColor(75) },
    rightLeg: { score: 75, color: getHealthColor(75) },
  };

  function getHealthColor(score) {
    if (score >= 80) return '#4caf50'; // Green
    if (score >= 60) return '#ff9800'; // Orange
    if (score >= 40) return '#ff5722'; // Red
    return '#d32f2f'; // Dark Red
  }

  const handleAreaClick = (area) => {
    setSelectedArea(area);
  };

  return (
    <Card>
      <CardContent>
        <Typography variant="h6" gutterBottom>
          {t('digitalTwin.title')}
        </Typography>
        <Typography variant="body2" color="text.secondary" paragraph>
          {t('digitalTwin.subtitle')}
        </Typography>

        <Grid container spacing={3}>
          <Grid item xs={12} md={8}>
            <Box
              sx={{
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
                bgcolor: 'grey.50',
                borderRadius: 2,
                p: 4,
                minHeight: 400,
              }}
            >
              <svg
                width="200"
                height="400"
                viewBox="0 0 200 400"
                style={{ maxWidth: '100%', height: 'auto' }}
              >
                {/* Head */}
                <ellipse
                  cx="100"
                  cy="50"
                  rx="30"
                  ry="40"
                  fill={bodyAreas.head.color}
                  stroke="#333"
                  strokeWidth="2"
                  opacity={selectedArea === 'head' ? 1 : 0.8}
                  onClick={() => handleAreaClick('head')}
                  style={{ cursor: 'pointer' }}
                />

                {/* Chest */}
                <rect
                  x="70"
                  y="90"
                  width="60"
                  height="80"
                  rx="10"
                  fill={bodyAreas.chest.color}
                  stroke="#333"
                  strokeWidth="2"
                  opacity={selectedArea === 'chest' ? 1 : 0.8}
                  onClick={() => handleAreaClick('chest')}
                  style={{ cursor: 'pointer' }}
                />

                {/* Abdomen */}
                <rect
                  x="75"
                  y="170"
                  width="50"
                  height="60"
                  rx="8"
                  fill={bodyAreas.abdomen.color}
                  stroke="#333"
                  strokeWidth="2"
                  opacity={selectedArea === 'abdomen' ? 1 : 0.8}
                  onClick={() => handleAreaClick('abdomen')}
                  style={{ cursor: 'pointer' }}
                />

                {/* Left Arm */}
                <rect
                  x="20"
                  y="100"
                  width="50"
                  height="15"
                  rx="7"
                  fill={bodyAreas.leftArm.color}
                  stroke="#333"
                  strokeWidth="2"
                  opacity={selectedArea === 'leftArm' ? 1 : 0.8}
                  onClick={() => handleAreaClick('leftArm')}
                  style={{ cursor: 'pointer' }}
                />
                <rect
                  x="10"
                  y="115"
                  width="15"
                  height="80"
                  rx="7"
                  fill={bodyAreas.leftArm.color}
                  stroke="#333"
                  strokeWidth="2"
                  opacity={selectedArea === 'leftArm' ? 1 : 0.8}
                  onClick={() => handleAreaClick('leftArm')}
                  style={{ cursor: 'pointer' }}
                />

                {/* Right Arm */}
                <rect
                  x="130"
                  y="100"
                  width="50"
                  height="15"
                  rx="7"
                  fill={bodyAreas.rightArm.color}
                  stroke="#333"
                  strokeWidth="2"
                  opacity={selectedArea === 'rightArm' ? 1 : 0.8}
                  onClick={() => handleAreaClick('rightArm')}
                  style={{ cursor: 'pointer' }}
                />
                <rect
                  x="175"
                  y="115"
                  width="15"
                  height="80"
                  rx="7"
                  fill={bodyAreas.rightArm.color}
                  stroke="#333"
                  strokeWidth="2"
                  opacity={selectedArea === 'rightArm' ? 1 : 0.8}
                  onClick={() => handleAreaClick('rightArm')}
                  style={{ cursor: 'pointer' }}
                />

                {/* Left Leg */}
                <rect
                  x="75"
                  y="230"
                  width="20"
                  height="100"
                  rx="10"
                  fill={bodyAreas.leftLeg.color}
                  stroke="#333"
                  strokeWidth="2"
                  opacity={selectedArea === 'leftLeg' ? 1 : 0.8}
                  onClick={() => handleAreaClick('leftLeg')}
                  style={{ cursor: 'pointer' }}
                />
                <ellipse
                  cx="85"
                  cy="340"
                  rx="15"
                  ry="20"
                  fill={bodyAreas.leftLeg.color}
                  stroke="#333"
                  strokeWidth="2"
                  opacity={selectedArea === 'leftLeg' ? 1 : 0.8}
                  onClick={() => handleAreaClick('leftLeg')}
                  style={{ cursor: 'pointer' }}
                />

                {/* Right Leg */}
                <rect
                  x="105"
                  y="230"
                  width="20"
                  height="100"
                  rx="10"
                  fill={bodyAreas.rightLeg.color}
                  stroke="#333"
                  strokeWidth="2"
                  opacity={selectedArea === 'rightLeg' ? 1 : 0.8}
                  onClick={() => handleAreaClick('rightLeg')}
                  style={{ cursor: 'pointer' }}
                />
                <ellipse
                  cx="115"
                  cy="340"
                  rx="15"
                  ry="20"
                  fill={bodyAreas.rightLeg.color}
                  stroke="#333"
                  strokeWidth="2"
                  opacity={selectedArea === 'rightLeg' ? 1 : 0.8}
                  onClick={() => handleAreaClick('rightLeg')}
                  style={{ cursor: 'pointer' }}
                />
              </svg>
            </Box>
          </Grid>

          <Grid item xs={12} md={4}>
            <Box>
              <Typography variant="subtitle1" gutterBottom>
                {t('digitalTwin.healthStatus')}
              </Typography>
              {Object.entries(bodyAreas).map(([area, data]) => (
                <Box
                  key={area}
                  sx={{
                    mb: 2,
                    p: 2,
                    border: selectedArea === area ? '2px solid' : '1px solid',
                    borderColor: selectedArea === area ? 'primary.main' : 'divider',
                    borderRadius: 2,
                    bgcolor: selectedArea === area ? 'action.selected' : 'transparent',
                    cursor: 'pointer',
                  }}
                  onClick={() => handleAreaClick(area)}
                >
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                    <Typography variant="body2" sx={{ textTransform: 'capitalize' }}>
                      {t(`digitalTwin.${area}`)}
                    </Typography>
                    <Typography variant="body2" fontWeight="bold">
                      {data.score}%
                    </Typography>
                  </Box>
                  <Box
                    sx={{
                      width: '100%',
                      height: 8,
                      bgcolor: 'grey.200',
                      borderRadius: 1,
                      overflow: 'hidden',
                    }}
                  >
                    <Box
                      sx={{
                        width: `${data.score}%`,
                        height: '100%',
                        bgcolor: data.color,
                        transition: 'width 0.3s',
                      }}
                    />
                  </Box>
                </Box>
              ))}
            </Box>
          </Grid>
        </Grid>

        {/* Legend */}
        <Box sx={{ mt: 3, display: 'flex', gap: 2, justifyContent: 'center' }}>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            <Box sx={{ width: 20, height: 20, bgcolor: '#4caf50', borderRadius: 1 }} />
            <Typography variant="caption">{t('digitalTwin.excellent')} (80-100%)</Typography>
          </Box>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            <Box sx={{ width: 20, height: 20, bgcolor: '#ff9800', borderRadius: 1 }} />
            <Typography variant="caption">{t('digitalTwin.good')} (60-79%)</Typography>
          </Box>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            <Box sx={{ width: 20, height: 20, bgcolor: '#ff5722', borderRadius: 1 }} />
            <Typography variant="caption">{t('digitalTwin.fair')} (40-59%)</Typography>
          </Box>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            <Box sx={{ width: 20, height: 20, bgcolor: '#d32f2f', borderRadius: 1 }} />
            <Typography variant="caption">{t('digitalTwin.poor')} (&lt;40%)</Typography>
          </Box>
        </Box>
      </CardContent>
    </Card>
  );
};

export default DigitalTwinVisualization;

