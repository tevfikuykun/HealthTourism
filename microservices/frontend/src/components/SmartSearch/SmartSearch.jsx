import React, { useState } from 'react';
import {
  Box,
  TextField,
  Button,
  Autocomplete,
  Paper,
  InputAdornment,
  MenuItem,
  Select,
  FormControl,
  Typography,
} from '@mui/material';
import { Search, LocationOn, LocalHospital, MedicalServices as MedicalServicesIcon } from '@mui/icons-material';
import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from '../../i18n';
import { useQuery } from '@tanstack/react-query';
import api from '../../services/api';

/**
 * SmartSearch Component
 * Hero section için optimize edilmiş akıllı arama çubuğu
 * Tedavi, şehir ve hastane araması yapar
 */
const SmartSearch = ({ variant = 'hero' }) => {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [treatmentType, setTreatmentType] = useState('');
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCity, setSelectedCity] = useState('');

  // Treatment types
  const treatmentTypes = [
    { value: 'cardiology', label: t('treatments.cardiology', 'Kardiyoloji') },
    { value: 'orthopedics', label: t('treatments.orthopedics', 'Ortopedi') },
    { value: 'oncology', label: t('treatments.oncology', 'Onkoloji') },
    { value: 'dermatology', label: t('treatments.dermatology', 'Dermatoloji') },
    { value: 'plastic-surgery', label: t('treatments.plasticSurgery', 'Estetik Cerrahi') },
    { value: 'dental', label: t('treatments.dental', 'Diş Hekimliği') },
    { value: 'ophthalmology', label: t('treatments.ophthalmology', 'Göz Hastalıkları') },
    { value: 'neurology', label: t('treatments.neurology', 'Nöroloji') },
  ];

  // Cities
  const cities = [
    { value: 'istanbul', label: 'İstanbul' },
    { value: 'ankara', label: 'Ankara' },
    { value: 'izmir', label: 'İzmir' },
    { value: 'antalya', label: 'Antalya' },
    { value: 'bursa', label: 'Bursa' },
  ];

  // Search suggestions (can be enhanced with API)
  const { data: searchSuggestions } = useQuery({
    queryKey: ['search-suggestions', searchQuery],
    queryFn: async () => {
      if (!searchQuery || searchQuery.length < 2) return [];
      try {
        // You can add API endpoint here for suggestions
        // const response = await api.get(`/search/suggestions?q=${encodeURIComponent(searchQuery)}`);
        // return response.data;
        return [];
      } catch {
        return [];
      }
    },
    enabled: searchQuery.length >= 2,
  });

  const handleSearch = (e) => {
    e.preventDefault();
    
    const params = new URLSearchParams();
    if (treatmentType) params.append('treatment', treatmentType);
    if (selectedCity) params.append('city', selectedCity);
    if (searchQuery) params.append('q', searchQuery);
    
    const queryString = params.toString();
    navigate(`/hospitals${queryString ? `?${queryString}` : ''}`);
  };

  const isHeroVariant = variant === 'hero';

  if (isHeroVariant) {
    return (
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.4, duration: 0.5 }}
      >
        <Box
          component="form"
          onSubmit={handleSearch}
          sx={{
            maxWidth: '1000px',
            mx: 'auto',
            p: { xs: 2, md: 3 },
            bgcolor: 'rgba(255, 255, 255, 0.98)',
            backdropFilter: 'blur(20px) saturate(180%)',
            WebkitBackdropFilter: 'blur(20px) saturate(180%)',
            borderRadius: '24px',
            boxShadow: '0 8px 32px rgba(0, 0, 0, 0.12), 0 2px 8px rgba(0, 0, 0, 0.08)',
            border: '1px solid rgba(255, 255, 255, 0.8)',
            display: 'flex',
            flexDirection: { xs: 'column', md: 'row' },
            gap: { xs: 2, md: 0 },
            alignItems: 'stretch',
            overflow: 'visible',
            position: 'relative',
            '&::before': {
              content: '""',
              position: 'absolute',
              top: 0,
              left: 0,
              right: 0,
              height: '3px',
              background: 'linear-gradient(90deg, #4f46e5 0%, #7c3aed 50%, #ec4899 100%)',
            },
          }}
        >
          {/* Treatment Type Selector */}
          <Box
            sx={{
              flex: { xs: '1 1 100%', md: '0 0 200px' },
              position: 'relative',
              display: 'flex',
              alignItems: 'center',
              '&::after': {
                content: '""',
                position: 'absolute',
                right: 0,
                top: '50%',
                transform: 'translateY(-50%)',
                width: '1px',
                height: '60%',
                bgcolor: 'divider',
                display: { xs: 'none', md: 'block' },
              },
            }}
          >
            <FormControl
              fullWidth
              sx={{
                '& .MuiOutlinedInput-root': {
                  borderRadius: '16px',
                  bgcolor: 'rgba(79, 70, 229, 0.05)',
                  border: '1px solid rgba(79, 70, 229, 0.1)',
                  transition: 'all 0.3s ease',
                  height: '56px',
                  '&:hover': {
                    bgcolor: 'rgba(79, 70, 229, 0.08)',
                    borderColor: 'rgba(79, 70, 229, 0.2)',
                  },
                  '&.Mui-focused': {
                    bgcolor: 'rgba(79, 70, 229, 0.1)',
                    borderColor: 'primary.main',
                  },
                  '& fieldset': {
                    border: 'none',
                  },
                },
              }}
            >
              <Select
                value={treatmentType}
                onChange={(e) => setTreatmentType(e.target.value)}
                displayEmpty
                sx={{
                  height: '56px',
                  color: 'text.primary',
                  fontWeight: 600,
                  fontSize: '0.95rem',
                  '& .MuiSelect-select': {
                    py: 1.5,
                    height: '56px',
                    boxSizing: 'border-box',
                    display: 'flex',
                    alignItems: 'center',
                  },
                }}
                MenuProps={{
                  disablePortal: false,
                  container: typeof document !== 'undefined' ? document.body : undefined,
                  PaperProps: {
                    style: {
                      maxHeight: 300,
                      zIndex: 1300,
                    },
                    sx: {
                      zIndex: '1300 !important',
                    },
                  },
                }}
                renderValue={(selected) => {
                  if (!selected) {
                    return (
                      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                        <MedicalServicesIcon sx={{ color: 'primary.main', fontSize: 20 }} />
                        <Typography variant="body2" color="text.secondary">
                          {t('smartSearch.selectTreatment', 'Tedavi Seçin')}
                        </Typography>
                      </Box>
                    );
                  }
                  const selectedType = treatmentTypes.find(t => t.value === selected);
                  return (
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                      <MedicalServicesIcon sx={{ color: 'primary.main', fontSize: 20 }} />
                      <Typography variant="body2" fontWeight={600}>
                        {selectedType?.label || selected}
                      </Typography>
                    </Box>
                  );
                }}
              >
                <MenuItem value="" disabled>
                  <em>{t('smartSearch.selectTreatment', 'Tedavi Seçin')}</em>
                </MenuItem>
                {treatmentTypes.map((type) => (
                  <MenuItem key={type.value} value={type.value} sx={{ py: 1.5 }}>
                    {type.label}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Box>

          {/* City Selector */}
          <Box
            sx={{
              flex: { xs: '1 1 100%', md: '0 0 180px' },
              position: 'relative',
              display: 'flex',
              alignItems: 'center',
              px: { xs: 0, md: 2 },
              '&::after': {
                content: '""',
                position: 'absolute',
                right: 0,
                top: '50%',
                transform: 'translateY(-50%)',
                width: '1px',
                height: '60%',
                bgcolor: 'divider',
                display: { xs: 'none', md: 'block' },
              },
            }}
          >
            <FormControl
              fullWidth
              sx={{
                '& .MuiOutlinedInput-root': {
                  borderRadius: '16px',
                  bgcolor: 'rgba(34, 197, 94, 0.05)',
                  border: '1px solid rgba(34, 197, 94, 0.1)',
                  transition: 'all 0.3s ease',
                  height: '56px',
                  '&:hover': {
                    bgcolor: 'rgba(34, 197, 94, 0.08)',
                    borderColor: 'rgba(34, 197, 94, 0.2)',
                  },
                  '&.Mui-focused': {
                    bgcolor: 'rgba(34, 197, 94, 0.1)',
                    borderColor: 'success.main',
                  },
                  '& fieldset': {
                    border: 'none',
                  },
                },
              }}
            >
              <Select
                value={selectedCity}
                onChange={(e) => setSelectedCity(e.target.value)}
                displayEmpty
                sx={{
                  height: '56px',
                  color: 'text.primary',
                  fontWeight: 600,
                  fontSize: '0.95rem',
                  '& .MuiSelect-select': {
                    py: 1.5,
                    height: '56px',
                    boxSizing: 'border-box',
                    display: 'flex',
                    alignItems: 'center',
                  },
                }}
                MenuProps={{
                  disablePortal: false,
                  container: typeof document !== 'undefined' ? document.body : undefined,
                  PaperProps: {
                    style: {
                      maxHeight: 300,
                      zIndex: 1300,
                    },
                    sx: {
                      zIndex: '1300 !important',
                    },
                  },
                }}
                renderValue={(selected) => {
                  if (!selected) {
                    return (
                      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                        <LocationOn sx={{ color: 'success.main', fontSize: 20 }} />
                        <Typography variant="body2" color="text.secondary">
                          {t('smartSearch.selectCity', 'Şehir')}
                        </Typography>
                      </Box>
                    );
                  }
                  const selectedCityData = cities.find(c => c.value === selected);
                  return (
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                      <LocationOn sx={{ color: 'success.main', fontSize: 20 }} />
                      <Typography variant="body2" fontWeight={600}>
                        {selectedCityData?.label || selected}
                      </Typography>
                    </Box>
                  );
                }}
              >
                <MenuItem value="" disabled>
                  <em>{t('smartSearch.selectCity', 'Şehir')}</em>
                </MenuItem>
                {cities.map((city) => (
                  <MenuItem key={city.value} value={city.value} sx={{ py: 1.5 }}>
                    {city.label}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Box>

          {/* Search Input */}
          <Box
            sx={{
              flex: { xs: '1 1 100%', md: '1 1 auto' },
              px: { xs: 0, md: 2 },
              display: 'flex',
              alignItems: 'center',
            }}
          >
            <TextField
              fullWidth
              placeholder={t('smartSearch.searchPlaceholder', 'Hastane veya doktor ara...')}
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              sx={{
                '& .MuiOutlinedInput-root': {
                  borderRadius: '16px',
                  bgcolor: 'rgba(99, 102, 241, 0.05)',
                  border: '1px solid rgba(99, 102, 241, 0.1)',
                  transition: 'all 0.3s ease',
                  height: '56px',
                  '&:hover': {
                    bgcolor: 'rgba(99, 102, 241, 0.08)',
                    borderColor: 'rgba(99, 102, 241, 0.2)',
                  },
                  '&.Mui-focused': {
                    bgcolor: 'rgba(99, 102, 241, 0.1)',
                    borderColor: 'primary.main',
                    boxShadow: '0 0 0 3px rgba(99, 102, 241, 0.1)',
                  },
                  '& fieldset': {
                    border: 'none',
                  },
                },
                '& .MuiInputBase-input': {
                  py: 1.5,
                  fontSize: '0.95rem',
                  fontWeight: 500,
                  height: '56px',
                  boxSizing: 'border-box',
                },
              }}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start" sx={{ ml: 1.5 }}>
                    <LocalHospital sx={{ color: 'primary.main', fontSize: 20 }} />
                  </InputAdornment>
                ),
              }}
            />
          </Box>

          {/* Search Button */}
          <Box
            sx={{
              flex: { xs: '1 1 100%', md: '0 0 auto' },
              pt: { xs: 0, md: 0 },
            }}
          >
            <Button
              type="submit"
              variant="contained"
              size="large"
              fullWidth
              sx={{
                height: '100%',
                minHeight: '56px',
                borderRadius: '16px',
                px: 4,
                py: 2,
                fontWeight: 700,
                fontSize: '1rem',
                textTransform: 'none',
                background: 'linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%)',
                boxShadow: '0 4px 14px rgba(79, 70, 229, 0.4), 0 2px 4px rgba(0, 0, 0, 0.1)',
                transition: 'all 0.3s ease',
                '&:hover': {
                  background: 'linear-gradient(135deg, #4338ca 0%, #6d28d9 100%)',
                  boxShadow: '0 6px 20px rgba(79, 70, 229, 0.5), 0 4px 8px rgba(0, 0, 0, 0.15)',
                  transform: 'translateY(-1px)',
                },
                '&:active': {
                  transform: 'translateY(0)',
                },
              }}
              endIcon={
                <Box
                  component={Search}
                  sx={{
                    fontSize: 22,
                    ml: 0.5,
                  }}
                />
              }
            >
              {t('smartSearch.search', 'Bul')}
            </Button>
          </Box>
        </Box>
      </motion.div>
    );
  }

  // Compact variant for other uses
  return (
    <Paper
      component="form"
      onSubmit={handleSearch}
      sx={{
        p: 1.5,
        display: 'flex',
        gap: 1,
        alignItems: 'center',
        borderRadius: 2,
      }}
    >
      <FormControl size="small" sx={{ minWidth: 150 }}>
        <Select
          value={treatmentType}
          onChange={(e) => setTreatmentType(e.target.value)}
          displayEmpty
          MenuProps={{
            disablePortal: false,
            container: typeof document !== 'undefined' ? document.body : undefined,
            PaperProps: {
              style: {
                maxHeight: 300,
                zIndex: 1300,
              },
              sx: {
                zIndex: '1300 !important',
              },
            },
          }}
        >
          <MenuItem value="">{t('smartSearch.allTreatments', 'Tüm Tedaviler')}</MenuItem>
          {treatmentTypes.map((type) => (
            <MenuItem key={type.value} value={type.value}>
              {type.label}
            </MenuItem>
          ))}
        </Select>
      </FormControl>
      <TextField
        fullWidth
        size="small"
        placeholder={t('smartSearch.searchPlaceholder', 'Ara...')}
        value={searchQuery}
        onChange={(e) => setSearchQuery(e.target.value)}
        InputProps={{
          startAdornment: (
            <InputAdornment position="start">
              <Search />
            </InputAdornment>
          ),
        }}
      />
      <Button type="submit" variant="contained" size="small">
        {t('smartSearch.search', 'Ara')}
      </Button>
    </Paper>
  );
};

export default SmartSearch;

