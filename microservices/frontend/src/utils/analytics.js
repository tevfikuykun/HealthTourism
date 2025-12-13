/**
 * Analytics Utilities
 * 
 * Analytics entegrasyonu için yardımcı fonksiyonlar.
 */

// Google Analytics measurement ID (örnek)
const GA_MEASUREMENT_ID = import.meta.env.VITE_GA_MEASUREMENT_ID || '';

/**
 * Google Analytics'i initialize et
 */
export const initAnalytics = () => {
  if (!GA_MEASUREMENT_ID) {
    console.warn('Google Analytics measurement ID not found');
    return;
  }

  // Google Analytics script'ini yükle
  const script1 = document.createElement('script');
  script1.async = true;
  script1.src = `https://www.googletagmanager.com/gtag/js?id=${GA_MEASUREMENT_ID}`;
  document.head.appendChild(script1);

  const script2 = document.createElement('script');
  script2.innerHTML = `
    window.dataLayer = window.dataLayer || [];
    function gtag(){dataLayer.push(arguments);}
    gtag('js', new Date());
    gtag('config', '${GA_MEASUREMENT_ID}');
  `;
  document.head.appendChild(script2);
};

/**
 * Page view tracking
 */
export const trackPageView = (path) => {
  if (typeof window.gtag !== 'undefined') {
    window.gtag('config', GA_MEASUREMENT_ID, {
      page_path: path,
    });
  }
};

/**
 * Event tracking
 */
export const trackEvent = (action, category, label, value) => {
  if (typeof window.gtag !== 'undefined') {
    window.gtag('event', action, {
      event_category: category,
      event_label: label,
      value: value,
    });
  }
};

/**
 * Custom event tracking
 */
export const trackCustomEvent = (eventName, eventParams) => {
  if (typeof window.gtag !== 'undefined') {
    window.gtag('event', eventName, eventParams);
  }
};

