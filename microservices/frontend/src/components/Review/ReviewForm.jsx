import React, { useState } from 'react';
import {
  Box,
  TextField,
  Button,
  Typography,
  Rating,
  Paper,
  Alert,
} from '@mui/material';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { useAuth } from '../../hooks/useAuth';
import Loading from '../Loading';

const reviewSchema = yup.object().shape({
  rating: yup.number().min(1, 'Lütfen puan verin').required('Puan gereklidir'),
  comment: yup.string().required('Yorum gereklidir').min(10, 'Yorum en az 10 karakter olmalıdır'),
});

export default function ReviewForm({ serviceId, serviceType, onSubmit: onSubmitCallback }) {
  const { isAuthenticated } = useAuth();
  const [rating, setRating] = useState(0);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm({
    resolver: yupResolver(reviewSchema),
    defaultValues: {
      rating: 0,
      comment: '',
    },
  });

  const onSubmit = async (data) => {
    if (!isAuthenticated) {
      setError('Yorum yapmak için giriş yapmanız gerekmektedir.');
      return;
    }

    try {
      setIsSubmitting(true);
      setError('');
      setSuccess(false);

      // TODO: Review API entegrasyonu
      // await reviewService.create({
      //   serviceId,
      //   serviceType,
      //   rating,
      //   comment: data.comment,
      // });

      setSuccess(true);
      reset();
      setRating(0);

      if (onSubmitCallback) {
        onSubmitCallback();
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Yorum eklenirken bir hata oluştu');
    } finally {
      setIsSubmitting(false);
    }
  };

  if (isSubmitting) {
    return <Loading />;
  }

  return (
    <Paper sx={{ p: 3, mt: 3 }}>
      <Typography variant="h6" gutterBottom>
        Yorum Yap
      </Typography>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      {success && (
        <Alert severity="success" sx={{ mb: 2 }}>
          Yorumunuz başarıyla eklendi!
        </Alert>
      )}

      <Box component="form" onSubmit={handleSubmit(onSubmit)}>
        <Box sx={{ mb: 2 }}>
          <Typography variant="body2" gutterBottom>
            Puanınız:
          </Typography>
          <Rating
            value={rating}
            onChange={(event, newValue) => {
              setRating(newValue);
              register('rating', { value: newValue });
            }}
            size="large"
          />
          {errors.rating && (
            <Typography variant="caption" color="error">
              {errors.rating.message}
            </Typography>
          )}
        </Box>

        <TextField
          {...register('comment')}
          fullWidth
          multiline
          rows={4}
          label="Yorumunuz"
          error={!!errors.comment}
          helperText={errors.comment?.message}
          sx={{ mb: 2 }}
        />

        <Button type="submit" variant="contained" disabled={!isAuthenticated || isSubmitting}>
          Yorumu Gönder
        </Button>
      </Box>
    </Paper>
  );
}

