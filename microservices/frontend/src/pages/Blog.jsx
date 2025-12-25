// src/pages/Blog.jsx
import React, { useState } from 'react';
import {
  Container,
  Box,
  Typography,
  Grid,
  Card,
  CardContent,
  CardMedia,
  Button,
  Chip,
  Tabs,
  Tab,
  Pagination,
  TextField,
  InputAdornment,
} from '@mui/material';
import {
  Search as SearchIcon,
  CalendarToday as CalendarIcon,
  Visibility as VisibilityIcon,
  Person as PersonIcon,
} from '@mui/icons-material';
import { useQuery } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from '../i18n';
import Loading from '../components/Loading';
import SEOHead from '../components/SEO/SEOHead';
import { blogService } from '../services/api';
import { format, parseISO } from 'date-fns';

function TabPanel({ children, value, index }) {
  return (
    <div role="tabpanel" hidden={value !== index}>
      {value === index && <Box sx={{ py: 3 }}>{children}</Box>}
    </div>
  );
}

export default function Blog() {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [selectedTab, setSelectedTab] = useState(0);
  const [searchQuery, setSearchQuery] = useState('');
  const [page, setPage] = useState(1);
  const postsPerPage = 9;

  const categories = [
    { label: t('blog.all', 'Tümü'), value: 'ALL' },
    { label: t('blog.health', 'Sağlık'), value: 'HEALTH' },
    { label: t('blog.news', 'Haberler'), value: 'NEWS' },
    { label: t('blog.tips', 'İpuçları'), value: 'TIPS' },
    { label: t('blog.treatment', 'Tedavi'), value: 'TREATMENT' },
  ];

  // Fetch blog posts
  const { data: posts = [], isLoading, error } = useQuery({
    queryKey: ['blogPosts', selectedTab],
    queryFn: async () => {
      if (selectedTab === 0) {
        const response = await blogService.getAll();
        return response.data || [];
      } else {
        const category = categories[selectedTab].value;
        const response = await blogService.getByCategory(category);
        return response.data || [];
      }
    },
  });

  const handleTabChange = (event, newValue) => {
    setSelectedTab(newValue);
    setPage(1);
  };

  const handlePostClick = (postId) => {
    navigate(`/blog/${postId}`);
  };

  // Filter posts by search query
  const filteredPosts = posts.filter((post) =>
    post.title?.toLowerCase().includes(searchQuery.toLowerCase()) ||
    post.summary?.toLowerCase().includes(searchQuery.toLowerCase())
  );

  // Pagination
  const totalPages = Math.ceil(filteredPosts.length / postsPerPage);
  const paginatedPosts = filteredPosts.slice(
    (page - 1) * postsPerPage,
    page * postsPerPage
  );

  if (isLoading) {
    return <Loading />;
  }

  return (
    <>
      <SEOHead
        title={t('blog.seoTitle', 'Sağlık Turizmi Blog - İpuçları, Haberler ve Rehberler')}
        description={t('blog.seoDescription', 'Sağlık turizmi hakkında güncel haberler, ipuçları ve rehberler. İstanbul\'da sağlık hizmetleri ve tedavi seçenekleri.')}
        keywords={t('blog.seoKeywords', 'sağlık turizmi blog, istanbul sağlık, tedavi rehberleri, sağlık ipuçları')}
      />
      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        <Box sx={{ mb: 4 }}>
          <Typography variant="h4" gutterBottom>
            {t('blog.title', 'Blog')}
          </Typography>
          <Typography variant="body1" color="text.secondary">
            {t('blog.subtitle', 'Sağlık turizmi hakkında güncel haberler, ipuçları ve rehberler')}
          </Typography>
        </Box>

        {/* Search Bar */}
        <Box sx={{ mb: 4 }}>
          <TextField
            fullWidth
            placeholder={t('blog.searchPlaceholder', 'Blog yazılarında ara...')}
            value={searchQuery}
            onChange={(e) => {
              setSearchQuery(e.target.value);
              setPage(1);
            }}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <SearchIcon />
                </InputAdornment>
              ),
            }}
          />
        </Box>

        {/* Category Tabs */}
        <Tabs value={selectedTab} onChange={handleTabChange} sx={{ mb: 3 }}>
          {categories.map((category, index) => (
            <Tab key={index} label={category.label} />
          ))}
        </Tabs>

        {error ? (
          <Box sx={{ textAlign: 'center', py: 4 }}>
            <Typography color="error">
              {t('blog.loadError', 'Blog yazıları yüklenirken bir hata oluştu')}
            </Typography>
          </Box>
        ) : filteredPosts.length === 0 ? (
          <Card>
            <CardContent>
              <Box sx={{ textAlign: 'center', py: 4 }}>
                <Typography variant="h6" color="text.secondary">
                  {searchQuery
                    ? t('blog.noSearchResults', 'Arama sonucu bulunamadı')
                    : t('blog.noPosts', 'Henüz blog yazısı bulunmamaktadır')}
                </Typography>
              </Box>
            </CardContent>
          </Card>
        ) : (
          <>
            <Grid container spacing={3}>
              {paginatedPosts.map((post) => (
                <Grid item xs={12} sm={6} md={4} key={post.id}>
                  <Card
                    sx={{
                      height: '100%',
                      display: 'flex',
                      flexDirection: 'column',
                      cursor: 'pointer',
                      transition: 'transform 0.2s, box-shadow 0.2s',
                      '&:hover': {
                        transform: 'translateY(-4px)',
                        boxShadow: 4,
                      },
                    }}
                    onClick={() => handlePostClick(post.id)}
                  >
                    {post.imageUrl && (
                      <CardMedia
                        component="img"
                        height="200"
                        image={post.imageUrl}
                        alt={post.title}
                        sx={{ objectFit: 'cover' }}
                      />
                    )}
                    <CardContent sx={{ flexGrow: 1, display: 'flex', flexDirection: 'column' }}>
                      <Box sx={{ display: 'flex', gap: 1, mb: 1, flexWrap: 'wrap' }}>
                        <Chip
                          label={t(`blog.category.${post.category}`, post.category)}
                          size="small"
                          color="primary"
                        />
                      </Box>
                      <Typography variant="h6" gutterBottom sx={{ flexGrow: 1 }}>
                        {post.title}
                      </Typography>
                      <Typography
                        variant="body2"
                        color="text.secondary"
                        sx={{ mb: 2 }}
                        dangerouslySetInnerHTML={{
                          __html: post.summary?.substring(0, 150) + '...' || '',
                        }}
                      />
                      <Box
                        sx={{
                          display: 'flex',
                          alignItems: 'center',
                          gap: 2,
                          mt: 'auto',
                          pt: 2,
                          borderTop: '1px solid',
                          borderColor: 'divider',
                        }}
                      >
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                          <PersonIcon sx={{ fontSize: 16, color: 'text.secondary' }} />
                          <Typography variant="caption" color="text.secondary">
                            {post.author}
                          </Typography>
                        </Box>
                        {post.publishedAt && (
                          <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                            <CalendarIcon sx={{ fontSize: 16, color: 'text.secondary' }} />
                            <Typography variant="caption" color="text.secondary">
                              {format(parseISO(post.publishedAt), 'dd.MM.yyyy')}
                            </Typography>
                          </Box>
                        )}
                        {post.viewCount !== undefined && (
                          <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                            <VisibilityIcon sx={{ fontSize: 16, color: 'text.secondary' }} />
                            <Typography variant="caption" color="text.secondary">
                              {post.viewCount}
                            </Typography>
                          </Box>
                        )}
                      </Box>
                    </CardContent>
                  </Card>
                </Grid>
              ))}
            </Grid>

            {/* Pagination */}
            {totalPages > 1 && (
              <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
                <Pagination
                  count={totalPages}
                  page={page}
                  onChange={(event, value) => setPage(value)}
                  color="primary"
                />
              </Box>
            )}
          </>
        )}
      </Container>
    </>
  );
}



