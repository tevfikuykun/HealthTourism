// Service Worker for Health Tourism PWA
const CACHE_NAME = 'health-tourism-v1';
const urlsToCache = [
  '/',
  '/index.html',
  '/static/css/main.css',
  '/static/js/main.js',
];

// Install event - cache resources
self.addEventListener('install', (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME)
      .then((cache) => {
        console.log('Opened cache');
        return cache.addAll(urlsToCache);
      })
  );
});

// Fetch event - serve from cache, fallback to network
self.addEventListener('fetch', (event) => {
  // Skip non-GET requests and external URLs
  if (event.request.method !== 'GET' || !event.request.url.startsWith(self.location.origin)) {
    return;
  }

  // Skip Vite dev server files in development
  const url = new URL(event.request.url);
  if (url.pathname.includes('@vite') || 
      url.pathname.includes('@react-refresh') || 
      url.pathname.includes('/src/') && url.hostname === 'localhost') {
    // Let Vite handle these files directly
    return;
  }

  event.respondWith(
    caches.match(event.request)
      .then((response) => {
        // Cache hit - return response
        if (response) {
          return response;
        }
        // Network request with error handling
        return fetch(event.request).catch((error) => {
          console.warn('Fetch failed for:', event.request.url, error);
          // Return a basic response for navigation requests
          if (event.request.mode === 'navigate') {
            return caches.match('/index.html');
          }
          throw error;
        });
      })
  );
});

// Activate event - clean up old caches
self.addEventListener('activate', (event) => {
  event.waitUntil(
    caches.keys().then((cacheNames) => {
      return Promise.all(
        cacheNames.map((cacheName) => {
          if (cacheName !== CACHE_NAME) {
            console.log('Deleting old cache:', cacheName);
            return caches.delete(cacheName);
          }
        })
      );
    })
  );
});

