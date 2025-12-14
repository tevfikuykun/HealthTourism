// src/pages/admin/ContentManagement.jsx
import React, { useState } from 'react';
import {
  Container, Box, Typography, Tabs, Tab, Paper, Button, TextField,
  Grid, Card, CardContent, CardActions, IconButton, Dialog, DialogTitle,
  DialogContent, DialogActions, Select, MenuItem, FormControl, InputLabel
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import VisibilityIcon from '@mui/icons-material/Visibility';
import ArticleIcon from '@mui/icons-material/Article';
import WebIcon from '@mui/icons-material/Web';
import { useTranslation } from 'react-i18next';

const ContentManagement = () => {
  const { t } = useTranslation();
  const [activeTab, setActiveTab] = useState(0);
  const [openDialog, setOpenDialog] = useState(false);
  const [content, setContent] = useState([
    {
      id: 1,
      title: 'Sağlık Turizmi Hakkında',
      type: 'page',
      status: 'published',
      createdAt: '2024-01-15',
    },
    {
      id: 2,
      title: 'Yeni Teknoloji: Robotik Cerrahi',
      type: 'blog',
      status: 'draft',
      createdAt: '2024-01-20',
    },
  ]);

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
        <Typography variant="h4">İçerik Yönetimi</Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => setOpenDialog(true)}
        >
          Yeni İçerik
        </Button>
      </Box>

      <Paper>
        <Tabs value={activeTab} onChange={(e, v) => setActiveTab(v)}>
          <Tab label="Sayfalar" icon={<WebIcon />} />
          <Tab label="Blog Yazıları" icon={<ArticleIcon />} />
          <Tab label="Medya Kütüphanesi" />
        </Tabs>

        <Box sx={{ p: 3 }}>
          {activeTab === 0 && (
            <Grid container spacing={2}>
              {content.filter(c => c.type === 'page').map((item) => (
                <Grid item xs={12} sm={6} md={4} key={item.id}>
                  <Card>
                    <CardContent>
                      <Typography variant="h6">{item.title}</Typography>
                      <Typography variant="body2" color="text.secondary">
                        Durum: {item.status}
                      </Typography>
                    </CardContent>
                    <CardActions>
                      <IconButton size="small">
                        <EditIcon />
                      </IconButton>
                      <IconButton size="small">
                        <VisibilityIcon />
                      </IconButton>
                      <IconButton size="small" color="error">
                        <DeleteIcon />
                      </IconButton>
                    </CardActions>
                  </Card>
                </Grid>
              ))}
            </Grid>
          )}

          {activeTab === 1 && (
            <Grid container spacing={2}>
              {content.filter(c => c.type === 'blog').map((item) => (
                <Grid item xs={12} sm={6} md={4} key={item.id}>
                  <Card>
                    <CardContent>
                      <Typography variant="h6">{item.title}</Typography>
                      <Typography variant="body2" color="text.secondary">
                        Durum: {item.status}
                      </Typography>
                    </CardContent>
                    <CardActions>
                      <IconButton size="small">
                        <EditIcon />
                      </IconButton>
                      <IconButton size="small">
                        <VisibilityIcon />
                      </IconButton>
                      <IconButton size="small" color="error">
                        <DeleteIcon />
                      </IconButton>
                    </CardActions>
                  </Card>
                </Grid>
              ))}
            </Grid>
          )}
        </Box>
      </Paper>

      <Dialog open={openDialog} onClose={() => setOpenDialog(false)} maxWidth="md" fullWidth>
        <DialogTitle>Yeni İçerik</DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12}>
              <FormControl fullWidth>
                <InputLabel>İçerik Tipi</InputLabel>
                <Select defaultValue="page">
                  <MenuItem value="page">Sayfa</MenuItem>
                  <MenuItem value="blog">Blog</MenuItem>
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12}>
              <TextField fullWidth label="Başlık" />
            </Grid>
            <Grid item xs={12}>
              <TextField fullWidth label="İçerik" multiline rows={10} />
            </Grid>
            <Grid item xs={12}>
              <TextField fullWidth label="SEO Başlık" />
            </Grid>
            <Grid item xs={12}>
              <TextField fullWidth label="SEO Açıklama" multiline rows={2} />
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

export default ContentManagement;

