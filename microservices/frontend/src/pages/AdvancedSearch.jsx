// src/pages/AdvancedSearch.jsx
import React, { useState } from 'react';
import {
  Container, Box, Typography, TextField, Paper, Grid, Card, CardContent,
  Chip, Button, Autocomplete, Tabs, Tab
} from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import HistoryIcon from '@mui/icons-material/History';
import StarIcon from '@mui/icons-material/Star';
import VoiceSearchButton from '../components/VoiceSearch/VoiceSearchButton';
import { useTranslation } from 'react-i18next';

const AdvancedSearch = () => {
  const { t } = useTranslation();
  const [searchTerm, setSearchTerm] = useState('');
  const [searchHistory, setSearchHistory] = useState([
    'Kardiyoloji',
    'Acıbadem Hastanesi',
    'Dr. Ahmet Yılmaz',
  ]);
  const [suggestions, setSuggestions] = useState([
    'Kardiyoloji',
    'Dermatoloji',
    'Ortopedi',
  ]);
  const [activeTab, setActiveTab] = useState(0);

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 4 }}>
        <SearchIcon sx={{ fontSize: 40, color: 'primary.main' }} />
        <Typography variant="h4">Gelişmiş Arama</Typography>
      </Box>

      <Paper sx={{ p: 3, mb: 3 }}>
        <Autocomplete
          freeSolo
          options={suggestions}
          value={searchTerm}
          onInputChange={(e, newValue) => setSearchTerm(newValue)}
          renderInput={(params) => (
            <TextField
              {...params}
              label="Ara..."
              placeholder="Hastane, doktor, hizmet ara..."
              InputProps={{
                ...params.InputProps,
                startAdornment: <SearchIcon sx={{ mr: 1, color: 'text.secondary' }} />,
                endAdornment: (
                  <>
                    <VoiceSearchButton onTranscript={(text) => setSearchTerm(text)} />
                    {params.InputProps.endAdornment}
                  </>
                ),
              }}
            />
          )}
        />
        <Box sx={{ mt: 2, display: 'flex', gap: 1, flexWrap: 'wrap' }}>
          <Chip label="Hastane" onClick={() => {}} />
          <Chip label="Doktor" onClick={() => {}} />
          <Chip label="Hizmet" onClick={() => {}} />
          <Chip label="Paket" onClick={() => {}} />
        </Box>
      </Paper>

      <Paper>
        <Tabs value={activeTab} onChange={(e, v) => setActiveTab(v)}>
          <Tab label="Sonuçlar" />
          <Tab label="Arama Geçmişi" icon={<HistoryIcon />} />
          <Tab label="Kayıtlı Aramalar" />
        </Tabs>

        <Box sx={{ p: 3 }}>
          {activeTab === 0 && (
            <Grid container spacing={2}>
              <Grid item xs={12} sm={6} md={4}>
                <Card>
                  <CardContent>
                    <Typography variant="h6">Acıbadem Hastanesi</Typography>
                    <Typography variant="body2" color="text.secondary">
                      İstanbul
                    </Typography>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mt: 1 }}>
                      <StarIcon sx={{ fontSize: 16, color: 'warning.main' }} />
                      <Typography variant="body2">4.5</Typography>
                    </Box>
                  </CardContent>
                </Card>
              </Grid>
            </Grid>
          )}

          {activeTab === 1 && (
            <Box>
              <Typography variant="h6" gutterBottom>Arama Geçmişi</Typography>
              {searchHistory.map((item, index) => (
                <Chip
                  key={index}
                  label={item}
                  onClick={() => setSearchTerm(item)}
                  sx={{ m: 0.5 }}
                />
              ))}
            </Box>
          )}
        </Box>
      </Paper>
    </Container>
  );
};

export default AdvancedSearch;

