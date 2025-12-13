import React from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Avatar,
  Rating,
  Divider,
} from '@mui/material';

/**
 * ReviewList Component
 * 
 * Yorum listesini gösteren component.
 * 
 * @param {array} reviews - Yorum listesi
 * @param {boolean} loading - Yükleniyor mu
 */
export default function ReviewList({ reviews = [], loading = false }) {
  if (loading) {
    return <Typography>Yükleniyor...</Typography>;
  }

  if (reviews.length === 0) {
    return (
      <Box sx={{ textAlign: 'center', py: 4 }}>
        <Typography variant="body1" color="text.secondary">
          Henüz yorum bulunmamaktadır.
        </Typography>
      </Box>
    );
  }

  return (
    <Box>
      {reviews.map((review, index) => (
        <Card key={review.id || index} sx={{ mb: 2 }}>
          <CardContent>
            <Box sx={{ display: 'flex', gap: 2, mb: 2 }}>
              <Avatar sx={{ bgcolor: 'primary.main' }}>
                {review.userName?.charAt(0).toUpperCase() || 'U'}
              </Avatar>
              <Box sx={{ flex: 1 }}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 1 }}>
                  <Typography variant="subtitle1">{review.userName || 'Anonim'}</Typography>
                  <Typography variant="caption" color="text.secondary">
                    {review.createdAt ? new Date(review.createdAt).toLocaleDateString('tr-TR') : ''}
                  </Typography>
                </Box>
                <Rating value={review.rating || 0} readOnly size="small" />
              </Box>
            </Box>
            <Typography variant="body2" color="text.secondary">
              {review.comment}
            </Typography>
          </CardContent>
        </Card>
      ))}
    </Box>
  );
}

