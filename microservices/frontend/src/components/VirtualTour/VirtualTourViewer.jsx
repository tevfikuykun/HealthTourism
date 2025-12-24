// src/components/VirtualTour/VirtualTourViewer.jsx
import React, { useEffect, useRef, useState } from 'react';
import { Box, Button, IconButton, Typography, CircularProgress } from '@mui/material';
import FullscreenIcon from '@mui/icons-material/Fullscreen';
import FullscreenExitIcon from '@mui/icons-material/FullscreenExit';
import ZoomInIcon from '@mui/icons-material/ZoomIn';
import ZoomOutIcon from '@mui/icons-material/ZoomOut';
import RotateLeftIcon from '@mui/icons-material/RotateLeft';
import RotateRightIcon from '@mui/icons-material/RotateRight';

/**
 * Virtual Tour Viewer Component
 * Uses A-Frame for 360° panorama viewing
 */
const VirtualTourViewer = ({ tourUrl, panoramaImageUrl, title, onClose }) => {
  const containerRef = useRef(null);
  const [isFullscreen, setIsFullscreen] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!panoramaImageUrl && !tourUrl) {
      setError('No tour content available');
      setIsLoading(false);
      return;
    }

    // Initialize A-Frame scene if panorama image is available
    if (panoramaImageUrl && containerRef.current) {
      const scene = document.createElement('a-scene');
      scene.setAttribute('embedded', '');
      scene.setAttribute('vr-mode-ui', 'enabled: false');
      
      const sky = document.createElement('a-sky');
      sky.setAttribute('src', panoramaImageUrl);
      sky.setAttribute('rotation', '0 -130 0');
      
      const camera = document.createElement('a-camera');
      camera.setAttribute('look-controls', '');
      camera.setAttribute('wasd-controls', 'enabled: false');
      
      scene.appendChild(sky);
      scene.appendChild(camera);
      
      containerRef.current.innerHTML = '';
      containerRef.current.appendChild(scene);
      
      scene.addEventListener('loaded', () => {
        setIsLoading(false);
      });
      
      return () => {
        if (containerRef.current) {
          containerRef.current.innerHTML = '';
        }
      };
    } else if (tourUrl) {
      // If tour URL is provided, use iframe
      const iframe = document.createElement('iframe');
      iframe.src = tourUrl;
      iframe.style.width = '100%';
      iframe.style.height = '100%';
      iframe.style.border = 'none';
      iframe.allowFullscreen = true;
      
      iframe.onload = () => {
        setIsLoading(false);
      };
      
      iframe.onerror = () => {
        setError('Failed to load tour');
        setIsLoading(false);
      };
      
      containerRef.current.innerHTML = '';
      containerRef.current.appendChild(iframe);
    }
  }, [panoramaImageUrl, tourUrl]);

  const handleFullscreen = () => {
    if (!document.fullscreenElement) {
      containerRef.current?.requestFullscreen().then(() => {
        setIsFullscreen(true);
      });
    } else {
      document.exitFullscreen().then(() => {
        setIsFullscreen(false);
      });
    }
  };

  if (error) {
    return (
      <Box sx={{ p: 3, textAlign: 'center' }}>
        <Typography color="error">{error}</Typography>
        {onClose && (
          <Button onClick={onClose} sx={{ mt: 2 }}>
            Close
          </Button>
        )}
      </Box>
    );
  }

  return (
    <Box sx={{ position: 'relative', width: '100%', height: '600px', bgcolor: 'black' }}>
      {isLoading && (
        <Box
          sx={{
            position: 'absolute',
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            bgcolor: 'rgba(0,0,0,0.7)',
            zIndex: 1000,
          }}
        >
          <CircularProgress />
        </Box>
      )}
      
      <Box
        ref={containerRef}
        sx={{
          width: '100%',
          height: '100%',
          position: 'relative',
        }}
      />
      
      <Box
        sx={{
          position: 'absolute',
          top: 16,
          right: 16,
          display: 'flex',
          gap: 1,
          zIndex: 100,
        }}
      >
        {title && (
          <Typography
            variant="h6"
            sx={{
              bgcolor: 'rgba(0,0,0,0.7)',
              color: 'white',
              px: 2,
              py: 1,
              borderRadius: 1,
            }}
          >
            {title}
          </Typography>
        )}
        <IconButton
          onClick={handleFullscreen}
          sx={{ bgcolor: 'rgba(0,0,0,0.7)', color: 'white' }}
        >
          {isFullscreen ? <FullscreenExitIcon /> : <FullscreenIcon />}
        </IconButton>
        {onClose && (
          <IconButton
            onClick={onClose}
            sx={{ bgcolor: 'rgba(0,0,0,0.7)', color: 'white' }}
          >
            ×
          </IconButton>
        )}
      </Box>
    </Box>
  );
};

export default VirtualTourViewer;

