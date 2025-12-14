// src/pages/Forum.jsx
import React, { useState } from 'react';
import {
  Container, Box, Typography, Card, CardContent, CardActions,
  Button, TextField, Avatar, Chip, Grid, Tabs, Tab, Paper,
  IconButton, Dialog, DialogTitle, DialogContent, DialogActions
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import ThumbUpIcon from '@mui/icons-material/ThumbUp';
import CommentIcon from '@mui/icons-material/Comment';
import PersonIcon from '@mui/icons-material/Person';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';

const Forum = () => {
  const { t } = useTranslation();
  const [activeTab, setActiveTab] = useState(0);
  const [openNewPost, setOpenNewPost] = useState(false);
  const [posts, setPosts] = useState([
    {
      id: 1,
      title: 'Kardiyoloji tedavisi deneyimlerim',
      content: 'İstanbul\'da kardiyoloji tedavisi aldım, çok memnun kaldım...',
      author: 'Ahmet Y.',
      category: 'Deneyimler',
      likes: 15,
      comments: 8,
      date: '2024-01-15',
    },
  ]);

  const categories = ['Tümü', 'Deneyimler', 'Sorular', 'Öneriler', 'Yorumlar'];

  const handleTabChange = (event, newValue) => {
    setActiveTab(newValue);
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
        <Typography variant="h4">Topluluk Forumu</Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => setOpenNewPost(true)}
        >
          Yeni Gönderi
        </Button>
      </Box>

      <Paper sx={{ mb: 3 }}>
        <Tabs value={activeTab} onChange={handleTabChange}>
          {categories.map((category) => (
            <Tab key={category} label={category} />
          ))}
        </Tabs>
      </Paper>

      <Grid container spacing={3}>
        {posts.map((post) => (
          <Grid item xs={12} key={post.id}>
            <Card>
              <CardContent>
                <Box sx={{ display: 'flex', gap: 2, mb: 2 }}>
                  <Avatar>
                    <PersonIcon />
                  </Avatar>
                  <Box sx={{ flexGrow: 1 }}>
                    <Typography variant="h6">{post.title}</Typography>
                    <Box sx={{ display: 'flex', gap: 1, alignItems: 'center', mt: 1 }}>
                      <Typography variant="body2" color="text.secondary">
                        {post.author}
                      </Typography>
                      <Chip label={post.category} size="small" />
                      <Typography variant="body2" color="text.secondary">
                        {post.date}
                      </Typography>
                    </Box>
                  </Box>
                </Box>
                <Typography variant="body1">{post.content}</Typography>
              </CardContent>
              <CardActions>
                <IconButton size="small">
                  <ThumbUpIcon />
                  <Typography variant="body2" sx={{ ml: 1 }}>
                    {post.likes}
                  </Typography>
                </IconButton>
                <IconButton size="small">
                  <CommentIcon />
                  <Typography variant="body2" sx={{ ml: 1 }}>
                    {post.comments}
                  </Typography>
                </IconButton>
                <Button size="small">Devamını Oku</Button>
              </CardActions>
            </Card>
          </Grid>
        ))}
      </Grid>

      <Dialog open={openNewPost} onClose={() => setOpenNewPost(false)} maxWidth="md" fullWidth>
        <DialogTitle>Yeni Gönderi Oluştur</DialogTitle>
        <DialogContent>
          <TextField
            fullWidth
            label="Başlık"
            margin="normal"
          />
          <TextField
            fullWidth
            label="Kategori"
            select
            SelectProps={{ native: true }}
            margin="normal"
          >
            <option value="">Kategori seçin</option>
            {categories.slice(1).map((cat) => (
              <option key={cat} value={cat}>{cat}</option>
            ))}
          </TextField>
          <TextField
            fullWidth
            label="İçerik"
            multiline
            rows={6}
            margin="normal"
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenNewPost(false)}>İptal</Button>
          <Button variant="contained" onClick={() => setOpenNewPost(false)}>
            Yayınla
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default Forum;

