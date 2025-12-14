// src/components/Comparison/ComparisonTool.jsx
import React, { useState } from 'react';
import {
  Box, Card, CardContent, Typography, Button, Grid, Table, TableBody,
  TableCell, TableContainer, TableHead, TableRow, Paper, Chip, IconButton,
  Dialog, DialogTitle, DialogContent, DialogActions, TextField, Autocomplete
} from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import CompareArrowsIcon from '@mui/icons-material/CompareArrows';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import CancelIcon from '@mui/icons-material/Cancel';

const ComparisonTool = ({ type = 'hospitals', items = [], onCompare }) => {
  const [selectedItems, setSelectedItems] = useState([]);
  const [open, setOpen] = useState(false);

  const handleAddToCompare = (item) => {
    if (selectedItems.length < 3 && !selectedItems.find(i => i.id === item.id)) {
      setSelectedItems([...selectedItems, item]);
    }
  };

  const handleRemoveFromCompare = (itemId) => {
    setSelectedItems(selectedItems.filter(item => item.id !== itemId));
  };

  const handleCompare = () => {
    if (selectedItems.length >= 2) {
      setOpen(true);
      if (onCompare) onCompare(selectedItems);
    }
  };

  const getComparisonFields = () => {
    if (type === 'hospitals') {
      return [
        { label: 'İsim', key: 'name' },
        { label: 'Şehir', key: 'city' },
        { label: 'Havalimanı Mesafesi', key: 'airportDistance' },
        { label: 'Yıldız', key: 'rating' },
        { label: 'Yorum Sayısı', key: 'reviewCount' },
        { label: 'Fiyat', key: 'price' },
      ];
    } else if (type === 'doctors') {
      return [
        { label: 'İsim', key: 'name' },
        { label: 'Uzmanlık', key: 'specialization' },
        { label: 'Deneyim', key: 'experienceYears' },
        { label: 'Dil', key: 'languages' },
        { label: 'Puan', key: 'rating' },
        { label: 'Konsültasyon Ücreti', key: 'consultationFee' },
      ];
    } else if (type === 'packages') {
      return [
        { label: 'Paket Adı', key: 'name' },
        { label: 'Fiyat', key: 'price' },
        { label: 'Süre', key: 'duration' },
        { label: 'Hastane', key: 'hospital' },
        { label: 'Doktor', key: 'doctor' },
        { label: 'Konaklama', key: 'accommodation' },
      ];
    }
    return [];
  };

  const getFieldValue = (item, key) => {
    if (key === 'languages' && Array.isArray(item[key])) {
      return item[key].join(', ');
    }
    return item[key] || 'N/A';
  };

  return (
    <Box>
      <Box sx={{ mb: 2, display: 'flex', alignItems: 'center', gap: 2 }}>
        <Typography variant="h6">Karşılaştırma</Typography>
        {selectedItems.length > 0 && (
          <Chip
            label={`${selectedItems.length} öğe seçildi`}
            color="primary"
            onDelete={() => setSelectedItems([])}
          />
        )}
        {selectedItems.length >= 2 && (
          <Button
            variant="contained"
            startIcon={<CompareArrowsIcon />}
            onClick={handleCompare}
          >
            Karşılaştır
          </Button>
        )}
      </Box>

      {selectedItems.length > 0 && (
        <Box sx={{ mb: 2 }}>
          <Grid container spacing={2}>
            {selectedItems.map((item) => (
              <Grid item xs={12} sm={6} md={4} key={item.id}>
                <Card>
                  <CardContent>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start' }}>
                      <Typography variant="h6">{item.name}</Typography>
                      <IconButton
                        size="small"
                        onClick={() => handleRemoveFromCompare(item.id)}
                      >
                        <CloseIcon />
                      </IconButton>
                    </Box>
                  </CardContent>
                </Card>
              </Grid>
            ))}
          </Grid>
        </Box>
      )}

      <Dialog open={open} onClose={() => setOpen(false)} maxWidth="lg" fullWidth>
        <DialogTitle>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <Typography variant="h6">Karşılaştırma</Typography>
            <IconButton onClick={() => setOpen(false)}>
              <CloseIcon />
            </IconButton>
          </Box>
        </DialogTitle>
        <DialogContent>
          <TableContainer component={Paper}>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Özellik</TableCell>
                  {selectedItems.map((item) => (
                    <TableCell key={item.id} align="center">
                      {item.name}
                    </TableCell>
                  ))}
                </TableRow>
              </TableHead>
              <TableBody>
                {getComparisonFields().map((field) => (
                  <TableRow key={field.key}>
                    <TableCell component="th" scope="row">
                      {field.label}
                    </TableCell>
                    {selectedItems.map((item) => (
                      <TableCell key={item.id} align="center">
                        {getFieldValue(item, field.key)}
                      </TableCell>
                    ))}
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpen(false)}>Kapat</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default ComparisonTool;

