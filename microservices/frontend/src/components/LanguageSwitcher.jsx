import React from 'react';
import { IconButton, Menu, MenuItem, Tooltip, Box, Typography } from '@mui/material';
import LanguageIcon from '@mui/icons-material/Language';
import { useTranslation } from 'react-i18next';

const languages = [
  { code: 'tr', label: 'Türkçe', flag: '🇹🇷', nativeName: 'Türkçe' },
  { code: 'en', label: 'English', flag: '🇺🇸', nativeName: 'English' },
  { code: 'ru', label: 'Русский', flag: '🇷🇺', nativeName: 'Русский' },
  { code: 'ar', label: 'العربية', flag: '🇸🇦', nativeName: 'العربية' },
  { code: 'de', label: 'Deutsch', flag: '🇩🇪', nativeName: 'Deutsch' },
  { code: 'fr', label: 'Français', flag: '🇫🇷', nativeName: 'Français' },
  { code: 'es', label: 'Español', flag: '🇪🇸', nativeName: 'Español' },
];

export default function LanguageSwitcher() {
  const { i18n, t } = useTranslation();
  const [anchorEl, setAnchorEl] = React.useState(null);

  const handleClick = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const handleLanguageChange = async (langCode) => {
    try {
      await i18n.changeLanguage(langCode);
      localStorage.setItem('i18nextLng', langCode);
      handleClose();
      // Component'lerin güncellenmesi için state değişikliği yeterli
      // window.location.reload() yerine i18n otomatik günceller
    } catch (error) {
      console.error('Dil değiştirme hatası:', error);
    }
  };

  return (
    <>
      <Tooltip title={t('selectLanguage', 'Dil Seçin')}>
        <IconButton 
          onClick={handleClick} 
          size="small"
          sx={{ 
            color: 'text.primary',
            padding: '4px',
            '&:hover': {
              backgroundColor: 'action.hover'
            }
          }}
        >
          <LanguageIcon fontSize="small" />
        </IconButton>
      </Tooltip>
      <Menu 
        anchorEl={anchorEl} 
        open={Boolean(anchorEl)} 
        onClose={handleClose}
        PaperProps={{
          sx: {
            maxHeight: 400,
            width: 200,
          }
        }}
      >
        {languages.map((lang) => (
          <MenuItem
            key={lang.code}
            onClick={() => handleLanguageChange(lang.code)}
            selected={i18n.language === lang.code || i18n.language.startsWith(lang.code)}
            sx={{
              display: 'flex',
              alignItems: 'center',
              gap: 1,
            }}
          >
            <span style={{ fontSize: '1.2em' }}>{lang.flag}</span>
            <Box sx={{ display: 'flex', flexDirection: 'column', flex: 1 }}>
              <Typography variant="body2" sx={{ fontWeight: i18n.language === lang.code ? 600 : 400 }}>
                {lang.label}
              </Typography>
              <Typography variant="caption" color="text.secondary">
                {lang.nativeName}
              </Typography>
            </Box>
          </MenuItem>
        ))}
      </Menu>
    </>
  );
}

