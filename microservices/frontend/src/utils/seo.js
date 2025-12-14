// SEO Utility Functions

export const generateMetaTags = (title, description, keywords, image, url) => {
  return {
    title: `${title} | Health Tourism`,
    description: description || 'İstanbul Sağlık Turizmi Platformu - En iyi hastaneler, doktorlar ve sağlık paketleri',
    keywords: keywords || 'sağlık turizmi, medical tourism, istanbul, hastane, doktor',
    image: image || '/og-image.jpg',
    url: url || window.location.href,
    type: 'website',
    siteName: 'Health Tourism',
  };
};

export const generateStructuredData = (type, data) => {
  const baseSchema = {
    '@context': 'https://schema.org',
    '@type': type,
  };

  switch (type) {
    case 'Organization':
      return {
        ...baseSchema,
        name: 'Health Tourism',
        url: 'https://healthtourism.com',
        logo: 'https://healthtourism.com/logo.png',
        contactPoint: {
          '@type': 'ContactPoint',
          telephone: '+90-212-XXX-XXXX',
          contactType: 'customer service',
        },
      };
    case 'MedicalBusiness':
      return {
        ...baseSchema,
        name: data.name,
        image: data.image,
        address: {
          '@type': 'PostalAddress',
          addressLocality: data.city,
          addressCountry: 'TR',
        },
        aggregateRating: {
          '@type': 'AggregateRating',
          ratingValue: data.rating,
          reviewCount: data.reviewCount,
        },
      };
    case 'BreadcrumbList':
      return {
        ...baseSchema,
        itemListElement: data.items.map((item, index) => ({
          '@type': 'ListItem',
          position: index + 1,
          name: item.name,
          item: item.url,
        })),
      };
    default:
      return baseSchema;
  }
};

export const generateCanonicalUrl = (path) => {
  const baseUrl = 'https://healthtourism.com';
  return `${baseUrl}${path}`;
};

