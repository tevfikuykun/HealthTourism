import React, { useState, useEffect } from 'react';
import { IconButton, Tooltip } from '@mui/material';
import Brightness4Icon from '@mui/icons-material/Brightness4';
import Brightness7Icon from '@mui/icons-material/Brightness7';

export default function ThemeToggle() {
  const [isDarkMode, setIsDarkMode] = useState(() => {
    return localStorage.getItem('theme') === 'dark';
  });

  useEffect(() => {
    // Theme değişikliğini dinle
    const storedTheme = localStorage.getItem('theme');
    if (storedTheme) {
      setIsDarkMode(storedTheme === 'dark');
    }
  }, []);

  const toggleTheme = () => {
    const newTheme = isDarkMode ? 'light' : 'dark';
    localStorage.setItem('theme', newTheme);
    setIsDarkMode(!isDarkMode);
    // Sayfayı yenilemeden tema değişikliği için window.location.reload() kullanıyoruz
    // İleride daha iyi bir yöntemle (context veya redux) yapılabilir
    window.location.reload();
  };

  return (
    <Tooltip title={isDarkMode ? 'Light mode' : 'Dark mode'}>
      <IconButton 
        onClick={toggleTheme} 
        size="small"
        sx={{ 
          color: 'text.primary',
          padding: '4px',
          '&:hover': {
            backgroundColor: 'action.hover'
          }
        }}
      >
        {isDarkMode ? <Brightness7Icon fontSize="small" /> : <Brightness4Icon fontSize="small" />}
      </IconButton>
    </Tooltip>
  );
}

