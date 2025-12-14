// src/pages/admin/UserManagement.jsx
import React, { useState } from 'react';
import {
  Container, Box, Typography, Table, TableBody, TableCell,
  TableContainer, TableHead, TableRow, Paper, Button, TextField,
  IconButton, Chip, Dialog, DialogTitle, DialogContent, DialogActions,
  Grid, Menu, MenuItem, Checkbox, FormControlLabel
} from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import BlockIcon from '@mui/icons-material/Block';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import SearchIcon from '@mui/icons-material/Search';
import FilterListIcon from '@mui/icons-material/FilterList';
import DownloadIcon from '@mui/icons-material/Download';
import { useTranslation } from 'react-i18next';

const UserManagement = () => {
  const { t } = useTranslation();
  const [users, setUsers] = useState([
    {
      id: 1,
      name: 'Ahmet Yılmaz',
      email: 'ahmet@example.com',
      status: 'active',
      role: 'user',
      createdAt: '2024-01-15',
      lastLogin: '2024-01-20',
    },
  ]);
  const [openDialog, setOpenDialog] = useState(false);
  const [selectedUser, setSelectedUser] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedUsers, setSelectedUsers] = useState([]);

  const handleBulkDelete = () => {
    // Toplu silme işlemi
  };

  const handleExport = () => {
    // Excel export
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
        <Typography variant="h4">{t('userManagement', 'Kullanıcı Yönetimi')}</Typography>
        <Box sx={{ display: 'flex', gap: 2 }}>
          <Button
            variant="outlined"
            startIcon={<DownloadIcon />}
            onClick={handleExport}
          >
            Excel'e Aktar
          </Button>
          <Button
            variant="contained"
            onClick={() => setOpenDialog(true)}
          >
            Yeni Kullanıcı
          </Button>
        </Box>
      </Box>

      <Box sx={{ mb: 3, display: 'flex', gap: 2 }}>
        <TextField
          fullWidth
          placeholder="Kullanıcı ara..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          InputProps={{
            startAdornment: <SearchIcon sx={{ mr: 1, color: 'text.secondary' }} />,
          }}
        />
        <Button
          variant="outlined"
          startIcon={<FilterListIcon />}
        >
          Filtrele
        </Button>
      </Box>

      {selectedUsers.length > 0 && (
        <Paper sx={{ p: 2, mb: 2, bgcolor: 'action.selected' }}>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <Typography>{selectedUsers.length} kullanıcı seçildi</Typography>
            <Box>
              <Button
                variant="outlined"
                color="error"
                onClick={handleBulkDelete}
                sx={{ mr: 1 }}
              >
                Toplu Sil
              </Button>
              <Button
                variant="outlined"
                onClick={() => setSelectedUsers([])}
              >
                Seçimi Temizle
              </Button>
            </Box>
          </Box>
        </Paper>
      )}

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell padding="checkbox">
                <Checkbox
                  checked={selectedUsers.length === users.length}
                  onChange={(e) => {
                    if (e.target.checked) {
                      setSelectedUsers(users.map(u => u.id));
                    } else {
                      setSelectedUsers([]);
                    }
                  }}
                />
              </TableCell>
              <TableCell>İsim</TableCell>
              <TableCell>E-posta</TableCell>
              <TableCell>Durum</TableCell>
              <TableCell>Rol</TableCell>
              <TableCell>Kayıt Tarihi</TableCell>
              <TableCell>Son Giriş</TableCell>
              <TableCell>İşlemler</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {users.map((user) => (
              <TableRow key={user.id}>
                <TableCell padding="checkbox">
                  <Checkbox
                    checked={selectedUsers.includes(user.id)}
                    onChange={(e) => {
                      if (e.target.checked) {
                        setSelectedUsers([...selectedUsers, user.id]);
                      } else {
                        setSelectedUsers(selectedUsers.filter(id => id !== user.id));
                      }
                    }}
                  />
                </TableCell>
                <TableCell>{user.name}</TableCell>
                <TableCell>{user.email}</TableCell>
                <TableCell>
                  <Chip
                    label={user.status === 'active' ? 'Aktif' : 'Pasif'}
                    color={user.status === 'active' ? 'success' : 'default'}
                    size="small"
                  />
                </TableCell>
                <TableCell>{user.role}</TableCell>
                <TableCell>{user.createdAt}</TableCell>
                <TableCell>{user.lastLogin}</TableCell>
                <TableCell>
                  <IconButton size="small" onClick={() => { setSelectedUser(user); setOpenDialog(true); }}>
                    <EditIcon />
                  </IconButton>
                  <IconButton size="small" color={user.status === 'active' ? 'warning' : 'success'}>
                    {user.status === 'active' ? <BlockIcon /> : <CheckCircleIcon />}
                  </IconButton>
                  <IconButton size="small" color="error">
                    <DeleteIcon />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={openDialog} onClose={() => setOpenDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle>
          {selectedUser ? 'Kullanıcı Düzenle' : 'Yeni Kullanıcı'}
        </DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12}>
              <TextField fullWidth label="İsim" />
            </Grid>
            <Grid item xs={12}>
              <TextField fullWidth label="E-posta" type="email" />
            </Grid>
            <Grid item xs={12}>
              <TextField fullWidth label="Rol" select SelectProps={{ native: true }}>
                <option value="user">Kullanıcı</option>
                <option value="admin">Admin</option>
              </TextField>
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDialog(false)}>İptal</Button>
          <Button variant="contained" onClick={() => setOpenDialog(false)}>
            Kaydet
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default UserManagement;

