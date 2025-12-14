// src/components/AdvancedFilter/AdvancedFilter.jsx
import React, { useState } from 'react';
import {
  Box, Accordion, AccordionSummary, AccordionDetails, Typography,
  Slider, Checkbox, FormControlLabel, TextField, Button, Chip,
  Grid, Paper, IconButton
} from '@mui/material';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import FilterListIcon from '@mui/icons-material/FilterList';
import ClearIcon from '@mui/icons-material/Clear';

const AdvancedFilter = ({ filters, onFilterChange, onReset }) => {
  const [localFilters, setLocalFilters] = useState(filters || {});

  const handleFilterChange = (key, value) => {
    const newFilters = { ...localFilters, [key]: value };
    setLocalFilters(newFilters);
    if (onFilterChange) onFilterChange(newFilters);
  };

  const handleReset = () => {
    setLocalFilters({});
    if (onReset) onReset();
  };

  const activeFilterCount = Object.keys(localFilters).filter(
    key => localFilters[key] !== null && localFilters[key] !== undefined && localFilters[key] !== ''
  ).length;

  return (
    <Paper sx={{ p: 2, mb: 3 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <FilterListIcon />
          <Typography variant="h6">Gelişmiş Filtreleme</Typography>
          {activeFilterCount > 0 && (
            <Chip label={activeFilterCount} color="primary" size="small" />
          )}
        </Box>
        {activeFilterCount > 0 && (
          <Button
            size="small"
            startIcon={<ClearIcon />}
            onClick={handleReset}
          >
            Temizle
          </Button>
        )}
      </Box>

      <Accordion defaultExpanded>
        <AccordionSummary expandIcon={<ExpandMoreIcon />}>
          <Typography>Fiyat Aralığı</Typography>
        </AccordionSummary>
        <AccordionDetails>
          <Box sx={{ px: 2 }}>
            <Slider
              value={localFilters.priceRange || [0, 10000]}
              onChange={(e, newValue) => handleFilterChange('priceRange', newValue)}
              valueLabelDisplay="auto"
              min={0}
              max={10000}
              step={100}
              marks={[
                { value: 0, label: '0₺' },
                { value: 5000, label: '5000₺' },
                { value: 10000, label: '10000₺+' },
              ]}
            />
          </Box>
        </AccordionDetails>
      </Accordion>

      <Accordion>
        <AccordionSummary expandIcon={<ExpandMoreIcon />}>
          <Typography>Değerlendirme</Typography>
        </AccordionSummary>
        <AccordionDetails>
          <Box>
            {[5, 4, 3, 2, 1].map((rating) => (
              <FormControlLabel
                key={rating}
                control={
                  <Checkbox
                    checked={localFilters.ratings?.includes(rating) || false}
                    onChange={(e) => {
                      const ratings = localFilters.ratings || [];
                      const newRatings = e.target.checked
                        ? [...ratings, rating]
                        : ratings.filter(r => r !== rating);
                      handleFilterChange('ratings', newRatings);
                    }}
                  />
                }
                label={`${rating} yıldız ve üzeri`}
              />
            ))}
          </Box>
        </AccordionDetails>
      </Accordion>

      <Accordion>
        <AccordionSummary expandIcon={<ExpandMoreIcon />}>
          <Typography>Tarih Aralığı</Typography>
        </AccordionSummary>
        <AccordionDetails>
          <Grid container spacing={2}>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Başlangıç Tarihi"
                type="date"
                value={localFilters.startDate || ''}
                onChange={(e) => handleFilterChange('startDate', e.target.value)}
                InputLabelProps={{ shrink: true }}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Bitiş Tarihi"
                type="date"
                value={localFilters.endDate || ''}
                onChange={(e) => handleFilterChange('endDate', e.target.value)}
                InputLabelProps={{ shrink: true }}
              />
            </Grid>
          </Grid>
        </AccordionDetails>
      </Accordion>

      {localFilters.customFields?.map((field, index) => (
        <Accordion key={index}>
          <AccordionSummary expandIcon={<ExpandMoreIcon />}>
            <Typography>{field.label}</Typography>
          </AccordionSummary>
          <AccordionDetails>
            {field.type === 'checkbox' && (
              <Box>
                {field.options.map((option) => (
                  <FormControlLabel
                    key={option.value}
                    control={
                      <Checkbox
                        checked={localFilters[field.key]?.includes(option.value) || false}
                        onChange={(e) => {
                          const values = localFilters[field.key] || [];
                          const newValues = e.target.checked
                            ? [...values, option.value]
                            : values.filter(v => v !== option.value);
                          handleFilterChange(field.key, newValues);
                        }}
                      />
                    }
                    label={option.label}
                  />
                ))}
              </Box>
            )}
          </AccordionDetails>
        </Accordion>
      ))}
    </Paper>
  );
};

export default AdvancedFilter;

