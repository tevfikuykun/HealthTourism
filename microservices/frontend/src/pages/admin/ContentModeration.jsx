// src/pages/admin/ContentModeration.jsx
import React, { useState } from 'react';
import {
  Container, Box, Typography, Tabs, Tab, Paper, Card, CardContent,
  Button, Chip, IconButton, Dialog, DialogTitle, DialogContent,
  DialogActions, TextField
} from '@mui/material';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import CancelIcon from '@mui/icons-material/Cancel';
import VisibilityIcon from '@mui/icons-material/Visibility';
import CommentIcon from '@mui/icons-material/Comment';
import RateReviewIcon from '@mui/icons-material/RateReview';
import { useTranslation } from 'react-i18next';

const ContentModeration = () => {
  const { t } = useTranslation();
  const [activeTab, setActiveTab] = useState(0);
  const [openDialog, setOpenDialog] = useState(false);
  const [selectedItem, setSelectedItem] = useState(null);
  const [comments, setComments] = useState([
    {
      id: 1,
      user: 'Ahmet Yılmaz',
      content: 'Harika bir deneyimdi!',
      rating: 5,
      status: 'pending',
      createdAt: '2024-01-20',
    },
  ]);

  const handleApprove = (id) => {
    setComments(comments.map(c => c.id === id ? { ...c, status: 'approved' } : c));
  };

  const handleReject = (id) => {
    setComments(comments.map(c => c.id === id ? { ...c, status: 'rejected' } : c));
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4" gutterBottom>
        {t('contentModeration', 'İçerik Moderasyonu')}
        İçerik Moderation
      </Typography>

      <Paper>
        <Tabs value={activeTab} onChange={(e, v) => setActiveTab(v)}>
          <Tab label="Yorumlar" icon={<CommentIcon />} />
          <Tab label="Değerlendirmeler" icon={<RateReviewIcon />} />
          <Tab label="Bekleyen İçerik" />
        </Tabs>

        <Box sx={{ p: 3 }}>
          {activeTab === 0 && (
            <Box>
              {comments.map((comment) => (
                <Card key={comment.id} sx={{ mb: 2 }}>
                  <CardContent>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start' }}>
                      <Box>
                        <Typography variant="h6">{comment.user}</Typography>
                        <Typography variant="body2" color="text.secondary">
                          {comment.content}
                        </Typography>
                        <Chip
                          label={comment.status === 'pending' ? 'Bekliyor' : comment.status === 'approved' ? 'Onaylandı' : 'Reddedildi'}
                          color={comment.status === 'pending' ? 'warning' : comment.status === 'approved' ? 'success' : 'error'}
                          size="small"
                          sx={{ mt: 1 }}
                        />
                      </Box>
                      <Box>
                        <IconButton
                          size="small"
                          color="success"
                          onClick={() => handleApprove(comment.id)}
                        >
                          <CheckCircleIcon />
                        </IconButton>
                        <IconButton
                          size="small"
                          color="error"
                          onClick={() => handleReject(comment.id)}
                        >
                          <CancelIcon />
                        </IconButton>
                        <IconButton
                          size="small"
                          onClick={() => {
                            setSelectedItem(comment);
                            setOpenDialog(true);
                          }}
                        >
                          <VisibilityIcon />
                        </IconButton>
                      </Box>
                    </Box>
                  </CardContent>
                </Card>
              ))}
            </Box>
          )}
        </Box>
      </Paper>

      <Dialog open={openDialog} onClose={() => setOpenDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle>İçerik Detayı</DialogTitle>
        <DialogContent>
          {selectedItem && (
            <Box>
              <Typography variant="body1"><strong>Kullanıcı:</strong> {selectedItem.user}</Typography>
              <Typography variant="body1" sx={{ mt: 2 }}><strong>İçerik:</strong></Typography>
              <Typography variant="body2" sx={{ mt: 1 }}>{selectedItem.content}</Typography>
            </Box>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDialog(false)}>Kapat</Button>
          <Button variant="contained" color="success" onClick={() => {
            if (selectedItem) handleApprove(selectedItem.id);
            setOpenDialog(false);
          }}>
            Onayla
          </Button>
          <Button variant="contained" color="error" onClick={() => {
            if (selectedItem) handleReject(selectedItem.id);
            setOpenDialog(false);
          }}>
            Reddet
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default ContentModeration;

