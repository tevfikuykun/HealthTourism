import React from 'react';
import { IconButton, Menu, MenuItem, Tooltip, Box, Typography } from '@mui/material';
import LanguageIcon from '@mui/icons-material/Language';
import { useTranslation } from '../i18n';
import i18n from '../i18n';

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
      console.log('Dil değiştiriliyor:', langCode);
      // Önce localStorage'a kaydet
      localStorage.setItem('i18nextLng', langCode);
      
      // i18n dilini değiştir
      await i18n.changeLanguage(langCode);
      
      // Backend'den yeni dil dosyasını yükle
      await i18n.reloadResources(langCode);
      
      console.log('Dil değiştirildi:', i18n.language);
      handleClose();
      
      // Component'lerin güncellenmesi için force update
      window.dispatchEvent(new Event('languagechange'));
    } catch (error) {
      console.error('Dil değiştirme hatası:', error);
      // Hata olsa bile localStorage'dan oku ve tekrar dene
      try {
        await i18n.changeLanguage(langCode);
      } catch (retryError) {
        console.error('Dil değiştirme retry hatası:', retryError);
      }
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

