// src/components/Accessibility/AccessibilityMenu.jsx
import React, { useState } from 'react';
import {
  IconButton, Menu, MenuItem, ListItemIcon, ListItemText,
  Switch, FormControlLabel, Divider
} from '@mui/material';
import AccessibilityNewIcon from '@mui/icons-material/AccessibilityNew';
import TextIncreaseIcon from '@mui/icons-material/TextIncrease';
import TextDecreaseIcon from '@mui/icons-material/TextDecrease';
import ContrastIcon from '@mui/icons-material/Contrast';

const AccessibilityMenu = () => {
  const [anchorEl, setAnchorEl] = useState(null);
  const [highContrast, setHighContrast] = useState(false);
  const [fontSize, setFontSize] = useState(16);

  const handleClick = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const increaseFontSize = () => {
    const newSize = Math.min(fontSize + 2, 24);
    setFontSize(newSize);
    document.documentElement.style.fontSize = `${newSize}px`;
  };

  const decreaseFontSize = () => {
    const newSize = Math.max(fontSize - 2, 12);
    setFontSize(newSize);
    document.documentElement.style.fontSize = `${newSize}px`;
  };

  const toggleHighContrast = () => {
    setHighContrast(!highContrast);
    if (!highContrast) {
      document.body.classList.add('high-contrast');
    } else {
      document.body.classList.remove('high-contrast');
    }
  };

  return (
    <>
      <IconButton
        onClick={handleClick}
        sx={{
          position: 'fixed',
          bottom: 24,
          left: 24,
          bgcolor: 'primary.main',
          color: 'white',
          zIndex: 1000,
          '&:hover': {
            bgcolor: 'primary.dark',
          },
        }}
      >
        <AccessibilityNewIcon />
      </IconButton>
      <Menu
        anchorEl={anchorEl}
        open={Boolean(anchorEl)}
        onClose={handleClose}
      >
        <MenuItem onClick={increaseFontSize}>
          <ListItemIcon>
            <TextIncreaseIcon />
          </ListItemIcon>
          <ListItemText>Yazı Boyutunu Artır</ListItemText>
        </MenuItem>
        <MenuItem onClick={decreaseFontSize}>
          <ListItemIcon>
            <TextDecreaseIcon />
          </ListItemIcon>
          <ListItemText>Yazı Boyutunu Azalt</ListItemText>
        </MenuItem>
        <Divider />
        <MenuItem>
          <FormControlLabel
            control={
              <Switch
                checked={highContrast}
                onChange={toggleHighContrast}
              />
            }
            label="Yüksek Kontrast"
          />
        </MenuItem>
      </Menu>
    </>
  );
};

export default AccessibilityMenu;

