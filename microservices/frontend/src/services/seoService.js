/**
 * SEO Service
 * Handles SEO optimization, sitemap generation, and structured data
 */

/**
 * Generate sitemap XML
 */
export const generateSitemap = (routes) => {
  const baseUrl = window.location.origin;
  const currentDate = new Date().toISOString().split('T')[0];
  
  const urls = routes.map(route => {
    const priority = route.priority || '0.8';
    const changefreq = route.changefreq || 'weekly';
    
    return `  <url>
    <loc>${baseUrl}${route.path}</loc>
    <lastmod>${route.lastmod || currentDate}</lastmod>
    <changefreq>${changefreq}</changefreq>
    <priority>${priority}</priority>
  </url>`;
  }).join('\n');
  
  return `<?xml version="1.0" encoding="UTF-8"?>
<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
${urls}
</urlset>`;
};

/**
 * Generate robots.txt
 */
export const generateRobotsTxt = (options = {}) => {
  const allow = options.allow !== false;
  const sitemapUrl = options.sitemapUrl || '/sitemap.xml';
  const disallowPaths = options.disallowPaths || ['/api/', '/admin/', '/private/'];
  
  const disallowRules = disallowPaths.map(path => `Disallow: ${path}`).join('\n');
  
  return `User-agent: *
${allow ? 'Allow: /' : 'Disallow: /'}
${disallowRules}

Sitemap: ${window.location.origin}${sitemapUrl}`;
};

/**
 * Generate structured data (JSON-LD)
 */
export const generateStructuredData = (type, data) => {
  const baseUrl = window.location.origin;
  
  switch (type) {
    case 'Organization':
      return {
        '@context': 'https://schema.org',
        '@type': 'Organization',
        name: data.name || 'Health Tourism',
        url: baseUrl,
        logo: `${baseUrl}${data.logo || '/logo.png'}`,
        contactPoint: {
          '@type': 'ContactPoint',
          telephone: data.phone || '+90-XXX-XXX-XXXX',
          contactType: 'customer service',
          areaServed: 'Worldwide',
          availableLanguage: data.languages || ['en', 'tr']
        },
        sameAs: data.socialMedia || []
      };
    
    case 'MedicalBusiness':
      return {
        '@context': 'https://schema.org',
        '@type': 'MedicalBusiness',
        name: data.name || 'Health Tourism Platform',
        description: data.description || 'Medical tourism platform connecting patients with healthcare providers',
        url: baseUrl,
        address: {
          '@type': 'PostalAddress',
          addressCountry: data.country || 'TR'
        },
        medicalSpecialty: data.specialties || []
      };
    
    case 'Service':
      return {
        '@context': 'https://schema.org',
        '@type': 'Service',
        serviceType: data.serviceType || 'Medical Tourism',
        provider: {
          '@type': 'Organization',
          name: data.providerName || 'Health Tourism'
        },
        areaServed: {
          '@type': 'Country',
          name: 'Worldwide'
        },
        description: data.description || ''
      };
    
    case 'BreadcrumbList':
      const items = data.items.map((item, index) => ({
        '@type': 'ListItem',
        position: index + 1,
        name: item.name,
        item: `${baseUrl}${item.url}`
      }));
      
      return {
        '@context': 'https://schema.org',
        '@type': 'BreadcrumbList',
        itemListElement: items
      };
    
    case 'Review':
      return {
        '@context': 'https://schema.org',
        '@type': 'Review',
        itemReviewed: {
          '@type': data.itemType || 'MedicalBusiness',
          name: data.itemName || ''
        },
        author: {
          '@type': 'Person',
          name: data.authorName || 'Anonymous'
        },
        reviewRating: {
          '@type': 'Rating',
          ratingValue: data.rating || 5,
          bestRating: 5,
          worstRating: 1
        },
        reviewBody: data.reviewText || ''
      };
    
    default:
      return null;
  }
};

/**
 * Add structured data to page
 */
export const addStructuredData = (data) => {
  const script = document.createElement('script');
  script.type = 'application/ld+json';
  script.text = JSON.stringify(data);
  document.head.appendChild(script);
};

/**
 * Update meta tags
 */
export const updateMetaTags = (tags) => {
  // Update or create title
  if (tags.title) {
    document.title = tags.title;
    updateMetaTag('og:title', tags.title);
    updateMetaTag('twitter:title', tags.title);
  }
  
  // Update or create description
  if (tags.description) {
    updateMetaTag('description', tags.description);
    updateMetaTag('og:description', tags.description);
    updateMetaTag('twitter:description', tags.description);
  }
  
  // Update or create keywords
  if (tags.keywords) {
    updateMetaTag('keywords', tags.keywords);
  }
  
  // Update or create image
  if (tags.image) {
    updateMetaTag('og:image', tags.image);
    updateMetaTag('twitter:image', tags.image);
  }
  
  // Update or create URL
  if (tags.url) {
    updateMetaTag('og:url', tags.url);
  }
  
  // Update or create type
  if (tags.type) {
    updateMetaTag('og:type', tags.type);
  }
};

/**
 * Update or create meta tag
 */
const updateMetaTag = (property, content) => {
  let meta = document.querySelector(`meta[property="${property}"]`) || 
             document.querySelector(`meta[name="${property}"]`);
  
  if (!meta) {
    meta = document.createElement('meta');
    if (property.startsWith('og:') || property.startsWith('twitter:')) {
      meta.setAttribute('property', property);
    } else {
      meta.setAttribute('name', property);
    }
    document.head.appendChild(meta);
  }
  
  meta.setAttribute('content', content);
};

/**
 * Generate canonical URL
 */
export const setCanonicalUrl = (url) => {
  let link = document.querySelector('link[rel="canonical"]');
  
  if (!link) {
    link = document.createElement('link');
    link.setAttribute('rel', 'canonical');
    document.head.appendChild(link);
  }
  
  link.setAttribute('href', url);
};

export default {
  generateSitemap,
  generateRobotsTxt,
  generateStructuredData,
  addStructuredData,
  updateMetaTags,
  setCanonicalUrl
};

