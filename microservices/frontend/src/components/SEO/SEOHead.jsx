// src/components/SEO/SEOHead.jsx
import React, { useEffect } from 'react';
import { Helmet } from 'react-helmet-async';
import seoService from '../../services/seoService';

const SEOHead = ({ 
  title = 'Health Tourism - İstanbul Sağlık Turizmi',
  description = 'İstanbul\'da sağlık turizmi hizmetleri. Hastaneler, doktorlar, konaklama ve rezervasyon.',
  keywords = 'sağlık turizmi, istanbul, hastane, doktor, tedavi',
  ogImage = '/og-image.jpg',
  url = typeof window !== 'undefined' ? window.location.href : '',
  structuredData = null,
  type = 'website'
}) => {
  useEffect(() => {
    // Update meta tags using seoService
    seoService.updateMetaTags({
      title,
      description,
      keywords,
      image: ogImage,
      url,
      type
    });

    // Set canonical URL
    if (url) {
      seoService.setCanonicalUrl(url);
    }

    // Add structured data if provided
    if (structuredData) {
      seoService.addStructuredData(structuredData);
    }
  }, [title, description, keywords, ogImage, url, structuredData, type]);

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
      <meta name="viewport" content="width=device-width, initial-scale=1.0" />

      {/* Open Graph / Facebook */}
      <meta property="og:type" content={type} />
      <meta property="og:url" content={url} />
      <meta property="og:title" content={title} />
      <meta property="og:description" content={description} />
      <meta property="og:image" content={ogImage} />
      <meta property="og:site_name" content="Health Tourism" />

      {/* Twitter */}
      <meta name="twitter:card" content="summary_large_image" />
      <meta name="twitter:url" content={url} />
      <meta name="twitter:title" content={title} />
      <meta name="twitter:description" content={description} />
      <meta name="twitter:image" content={ogImage} />

      {/* Canonical URL */}
      <link rel="canonical" href={url} />
    </Helmet>
  );
};

export default SEOHead;

