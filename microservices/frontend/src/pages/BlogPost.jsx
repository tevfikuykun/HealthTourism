// src/pages/BlogPost.jsx
import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Container,
  Box,
  Typography,
  Card,
  CardContent,
  Button,
  Chip,
  Divider,
  IconButton,
  Breadcrumbs,
  Link,
} from '@mui/material';
import {
  ArrowBack as ArrowBackIcon,
  CalendarToday as CalendarIcon,
  Person as PersonIcon,
  Visibility as VisibilityIcon,
  Share as ShareIcon,
} from '@mui/icons-material';
import { useQuery } from '@tanstack/react-query';
import { useTranslation } from '../i18n';
import Loading from '../components/Loading';
import SEOHead from '../components/SEO/SEOHead';
import { blogService } from '../services/api';
import { format, parseISO } from 'date-fns';

export default function BlogPost() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { t } = useTranslation();

  const { data: post, isLoading, error } = useQuery({
    queryKey: ['blogPost', id],
    queryFn: () => blogService.getById(id),
    select: (response) => response.data,
  });

  const handleShare = async () => {
    if (navigator.share) {
      try {
        await navigator.share({
          title: post?.title,
          text: post?.summary,
          url: window.location.href,
        });
      } catch (error) {
        // User cancelled share
      }
    } else {
      navigator.clipboard.writeText(window.location.href);
    }
  };

  if (isLoading) {
    return <Loading />;
  }

  if (error || !post) {
    return (
      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        <Box sx={{ textAlign: 'center', py: 4 }}>
          <Typography variant="h6" color="error">
            {t('blog.postNotFound', 'Blog yazısı bulunamadı')}
          </Typography>
          <Button onClick={() => navigate('/blog')} sx={{ mt: 2 }}>
            {t('blog.backToBlog', 'Blog\'a Dön')}
          </Button>
        </Box>
      </Container>
    );
  }

  return (
    <>
      <SEOHead
        title={`${post.title} - Health Tourism Blog`}
        description={post.summary || post.content?.substring(0, 160)}
        keywords={`${post.category}, sağlık turizmi, ${post.title}`}
        ogImage={post.imageUrl}
        type="article"
        structuredData={{
          '@context': 'https://schema.org',
          '@type': 'BlogPosting',
          headline: post.title,
          description: post.summary,
          image: post.imageUrl,
          author: {
            '@type': 'Person',
            name: post.author,
          },
          datePublished: post.publishedAt,
          dateModified: post.publishedAt,
        }}
      />
      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        <Breadcrumbs sx={{ mb: 2 }}>
          <Link
            component="button"
            variant="body1"
            onClick={() => navigate('/blog')}
            sx={{ cursor: 'pointer' }}
          >
            {t('blog.title', 'Blog')}
          </Link>
          <Typography color="text.primary">{post.title}</Typography>
        </Breadcrumbs>

        <Box sx={{ mb: 2 }}>
          <IconButton onClick={() => navigate('/blog')} sx={{ mb: 1 }}>
            <ArrowBackIcon />
          </IconButton>
        </Box>

        <Card>
          {post.imageUrl && (
            <Box
              component="img"
              src={post.imageUrl}
              alt={post.title}
              sx={{
                width: '100%',
                maxHeight: 400,
                objectFit: 'cover',
              }}
            />
          )}
          <CardContent>
            <Box sx={{ display: 'flex', gap: 1, mb: 2, flexWrap: 'wrap', alignItems: 'center' }}>
              <Chip
                label={t(`blog.category.${post.category}`, post.category)}
                color="primary"
                size="small"
              />
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5, ml: 'auto' }}>
                <IconButton size="small" onClick={handleShare}>
                  <ShareIcon />
                </IconButton>
              </Box>
            </Box>

            <Typography variant="h3" gutterBottom>
              {post.title}
            </Typography>

            <Box sx={{ display: 'flex', gap: 2, mb: 3, flexWrap: 'wrap' }}>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                <PersonIcon sx={{ fontSize: 16, color: 'text.secondary' }} />
                <Typography variant="body2" color="text.secondary">
                  {post.author}
                </Typography>
              </Box>
              {post.publishedAt && (
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                  <CalendarIcon sx={{ fontSize: 16, color: 'text.secondary' }} />
                  <Typography variant="body2" color="text.secondary">
                    {format(parseISO(post.publishedAt), 'dd MMMM yyyy')}
                  </Typography>
                </Box>
              )}
              {post.viewCount !== undefined && (
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                  <VisibilityIcon sx={{ fontSize: 16, color: 'text.secondary' }} />
                  <Typography variant="body2" color="text.secondary">
                    {post.viewCount} {t('blog.views', 'görüntülenme')}
                  </Typography>
                </Box>
              )}
            </Box>

            <Divider sx={{ mb: 3 }} />

            <Box
              sx={{
                '& p': { mb: 2 },
                '& img': { maxWidth: '100%', height: 'auto', mb: 2 },
              }}
              dangerouslySetInnerHTML={{ __html: post.content || '' }}
            />
          </CardContent>
        </Card>
      </Container>
    </>
  );
}

