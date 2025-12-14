// src/pages/HealthRecords.jsx
import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  Container, Box, Typography, Card, CardContent, Grid, Button,
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
  Paper, IconButton, Dialog, DialogTitle, DialogContent, DialogActions,
  TextField, Chip
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import DownloadIcon from '@mui/icons-material/Download';
import VisibilityIcon from '@mui/icons-material/Visibility';
import MedicalServicesIcon from '@mui/icons-material/MedicalServices';
import LoadingState from '../components/LoadingState/LoadingState';
import ErrorState from '../components/ErrorState/ErrorState';
import { healthRecordsService } from '../services/api';
import { toast } from 'react-toastify';
import { useTranslation } from 'react-i18next';

const HealthRecords = () => {
  const { t } = useTranslation();
  const [openAdd, setOpenAdd] = useState(false);
  const queryClient = useQueryClient();

  const { data: records = [], isLoading, error } = useQuery({
    queryKey: ['healthRecords'],
    queryFn: () => healthRecordsService.getAll(),
  });

  const createMutation = useMutation({
    mutationFn: healthRecordsService.create,
    onSuccess: () => {
      queryClient.invalidateQueries(['healthRecords']);
      toast.success('Sağlık kaydı başarıyla eklendi');
      setOpenAdd(false);
    },
    onError: (error) => {
      toast.error('Kayıt eklenirken bir hata oluştu');
    },
  });

  if (isLoading) {
    return <LoadingState message="Sağlık kayıtları yükleniyor..." />;
  }

  if (error) {
    return <ErrorState message="Sağlık kayıtları yüklenirken bir hata oluştu" />;
  }

  const handleSubmit = (formData) => {
    createMutation.mutate(formData);
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
        <Typography variant="h4">Sağlık Kayıtlarım</Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => setOpenAdd(true)}
        >
          Yeni Kayıt Ekle
        </Button>
      </Box>

      <Grid container spacing={3} sx={{ mb: 3 }}>
        <Grid item xs={12} sm={4}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                <MedicalServicesIcon sx={{ fontSize: 40, color: 'primary.main' }} />
                <Box>
                  <Typography variant="h4">12</Typography>
                  <Typography variant="body2" color="text.secondary">
                    Toplam Kayıt
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={4}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                <MedicalServicesIcon sx={{ fontSize: 40, color: 'success.main' }} />
                <Box>
                  <Typography variant="h4">5</Typography>
                  <Typography variant="body2" color="text.secondary">
                    Bu Ay
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={4}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                <MedicalServicesIcon sx={{ fontSize: 40, color: 'warning.main' }} />
                <Box>
                  <Typography variant="h4">3</Typography>
                  <Typography variant="body2" color="text.secondary">
                    Bekleyen Testler
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Tarih</TableCell>
              <TableCell>Tip</TableCell>
              <TableCell>Doktor</TableCell>
              <TableCell>Hastane</TableCell>
              <TableCell>Açıklama</TableCell>
              <TableCell>Belgeler</TableCell>
              <TableCell>İşlemler</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {records.length === 0 ? (
              <TableRow>
                <TableCell colSpan={7} align="center">
                  <Typography variant="body2" color="text.secondary">
                    Henüz sağlık kaydı bulunmuyor
                  </Typography>
                </TableCell>
              </TableRow>
            ) : (
              records.map((record) => (
              <TableRow key={record.id}>
                <TableCell>{record.date}</TableCell>
                <TableCell>
                  <Chip label={record.type} size="small" />
                </TableCell>
                <TableCell>{record.doctor}</TableCell>
                <TableCell>{record.hospital}</TableCell>
                <TableCell>{record.description}</TableCell>
                <TableCell>{record.documents}</TableCell>
                <TableCell>
                  <IconButton size="small">
                    <VisibilityIcon />
                  </IconButton>
                  <IconButton size="small">
                    <DownloadIcon />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))
            )}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={openAdd} onClose={() => setOpenAdd(false)} maxWidth="md" fullWidth>
        <DialogTitle>Yeni Sağlık Kaydı Ekle</DialogTitle>
        <DialogContent>
          <TextField
            fullWidth
            label="Kayıt Tipi"
            select
            SelectProps={{ native: true }}
            margin="normal"
          >
            <option value="">Tip seçin</option>
            <option value="consultation">Konsültasyon</option>
            <option value="test">Test</option>
            <option value="surgery">Ameliyat</option>
            <option value="medication">İlaç</option>
          </TextField>
          <TextField
            fullWidth
            label="Tarih"
            type="date"
            InputLabelProps={{ shrink: true }}
            margin="normal"
          />
          <TextField
            fullWidth
            label="Doktor"
            margin="normal"
          />
          <TextField
            fullWidth
            label="Hastane"
            margin="normal"
          />
          <TextField
            fullWidth
            label="Açıklama"
            multiline
            rows={4}
            margin="normal"
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenAdd(false)}>İptal</Button>
          <Button variant="contained" onClick={() => setOpenAdd(false)}>
            Kaydet
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default HealthRecords;

