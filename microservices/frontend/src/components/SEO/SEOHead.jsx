// src/components/SEO/SEOHead.jsx
import React from 'react';
import { Helmet } from 'react-helmet-async';

const SEOHead = ({ 
  title = 'Health Tourism - İstanbul Sağlık Turizmi',
  description = 'İstanbul\'da sağlık turizmi hizmetleri. Hastaneler, doktorlar, konaklama ve rezervasyon.',
  keywords = 'sağlık turizmi, istanbul, hastane, doktor, tedavi',
  ogImage = '/og-image.jpg',
  url = window.location.href
}) => {
  return (
    <Helmet>
      {/* Primary Meta Tags */}
      <title>{title}</title>
      <meta name="title" content={title} />
      <meta name="description" content={description} />
      <meta name="keywords" content={keywords} />
      <meta name="robots" content="index, follow" />
      <meta name="language" content="Turkish" />
      <meta name="author" content="Health Tourism" />

      {/* Open Graph / Facebook */}
      <meta property="og:type" content="website" />
      <meta property="og:url" content={url} />
      <meta property="og:title" content={title} />
      <meta property="og:description" content={description} />
      <meta property="og:image" content={ogImage} />

      {/* Twitter */}
      <meta property="twitter:card" content="summary_large_image" />
      <meta property="twitter:url" content={url} />
      <meta property="twitter:title" content={title} />
      <meta property="twitter:description" content={description} />
      <meta property="twitter:image" content={ogImage} />

      {/* Canonical URL */}
      <link rel="canonical" href={url} />
    </Helmet>
  );
};

export default SEOHead;

