import React, { useState, useEffect } from 'react';
import {
  TextField,
  InputAdornment,
  Popper,
  Paper,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Typography,
  Box,
  Divider,
} from '@mui/material';
import {
  Search,
  LocalHospital,
  Person,
  Assignment,
  Event,
  Clear,
} from '@mui/icons-material';
import { IconButton } from '@mui/material';
import { useTranslation } from '../../i18n';
import { useQuery } from '@tanstack/react-query';
import api from '../../services/api';
import { useNavigate } from 'react-router-dom';

/**
 * Global Search Component
 * Hastalar, raporlar, randevular için global arama
 */
const GlobalSearch = () => {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [query, setQuery] = useState('');
  const [anchorEl, setAnchorEl] = useState(null);
  const [open, setOpen] = useState(false);

  const { data: searchResults, isLoading } = useQuery({
    queryKey: ['global-search', query],
    queryFn: async () => {
      if (!query || query.length < 2) return null;
      const response = await api.get(`/api/search/global?q=${encodeURIComponent(query)}`);
      return response.data;
    },
    enabled: query.length >= 2,
  });

  useEffect(() => {
    setOpen(query.length >= 2 && Boolean(anchorEl));
  }, [query, anchorEl]);

  const handleSearch = (e) => {
    e.preventDefault();
    if (query.trim()) {
      navigate(`/search?q=${encodeURIComponent(query)}`);
      setQuery('');
      setOpen(false);
    }
  };

  const handleResultClick = (result) => {
    navigate(result.url);
    setQuery('');
    setOpen(false);
  };

  const getResultIcon = (type) => {
    const icons = {
      PATIENT: <Person />,
      HOSPITAL: <LocalHospital />,
      DOCTOR: <Person />,
      RESERVATION: <Event />,
      DOCUMENT: <Assignment />,
    };
    return icons[type] || <Search />;
  };

  return (
    <Box sx={{ position: 'relative', flexGrow: 1, maxWidth: 500 }}>
      <form onSubmit={handleSearch}>
        <TextField
          fullWidth
          size="small"
          placeholder={t('header.searchPlaceholder')}
          value={query}
          onChange={(e) => {
            setQuery(e.target.value);
            if (e.target.value && !anchorEl) {
              setAnchorEl(e.currentTarget);
            }
          }}
          InputProps={{
            startAdornment: (
              <InputAdornment position="start">
                <Search />
              </InputAdornment>
            ),
            endAdornment: query && (
              <InputAdornment position="end">
                <IconButton size="small" onClick={() => setQuery('')}>
                  <Clear fontSize="small" />
                </IconButton>
              </InputAdornment>
            ),
          }}
          sx={{
            bgcolor: 'action.hover',
            borderRadius: 2,
            '& .MuiOutlinedInput-root': {
              '& fieldset': { border: 'none' },
            },
          }}
        />
      </form>

      <Popper
        open={open}
        anchorEl={anchorEl}
        placement="bottom-start"
        sx={{ zIndex: 1300, width: anchorEl?.offsetWidth }}
      >
        <Paper
          sx={{
            mt: 1,
            maxHeight: 400,
            overflow: 'auto',
            boxShadow: 4,
          }}
        >
          {isLoading ? (
            <Box sx={{ p: 2, textAlign: 'center' }}>
              <Typography variant="body2" color="text.secondary">
                {t('common.searching')}...
              </Typography>
            </Box>
          ) : searchResults && searchResults.length > 0 ? (
            <>
              <Box sx={{ p: 1, bgcolor: 'action.hover' }}>
                <Typography variant="caption" color="text.secondary">
                  {searchResults.length} {t('header.resultsFound')}
                </Typography>
              </Box>
              <List dense>
                {searchResults.slice(0, 8).map((result, idx) => (
                  <React.Fragment key={result.id || idx}>
                    <ListItem
                      button
                      onClick={() => handleResultClick(result)}
                    >
                      <ListItemIcon>{getResultIcon(result.type)}</ListItemIcon>
                      <ListItemText
                        primary={result.title}
                        secondary={result.subtitle}
                      />
                    </ListItem>
                    {idx < searchResults.length - 1 && <Divider />}
                  </React.Fragment>
                ))}
              </List>
              {searchResults.length > 8 && (
                <Box sx={{ p: 1, textAlign: 'center', borderTop: 1, borderColor: 'divider' }}>
                  <Typography variant="caption" color="primary">
                    {t('header.viewAllResults')}
                  </Typography>
                </Box>
              )}
            </>
          ) : query.length >= 2 ? (
            <Box sx={{ p: 2, textAlign: 'center' }}>
              <Typography variant="body2" color="text.secondary">
                {t('header.noResults')}
              </Typography>
            </Box>
          ) : null}
        </Paper>
      </Popper>
    </Box>
  );
};

export default GlobalSearch;

