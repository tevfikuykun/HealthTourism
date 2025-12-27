/**
 * Network Error Handler
 * ERR_NETWORK_CHANGED ve diğer network hatalarını yönetir
 */

let reconnectAttempts = 0;
const MAX_RECONNECT_ATTEMPTS = 5;
const RECONNECT_DELAY = 2000; // 2 saniye

/**
 * Network hatası tespit edildiğinde çağrılır
 */
export const handleNetworkError = (error) => {
  // ERR_NETWORK_CHANGED hatası
  if (error?.message?.includes('ERR_NETWORK_CHANGED') || 
      error?.code === 'ERR_NETWORK_CHANGED' ||
      error?.name === 'NetworkError') {
    
    console.warn('Network connection changed. Attempting to reconnect...');
    
    // Vite HMR reconnect
    if (import.meta.hot) {
      reconnectAttempts++;
      
      if (reconnectAttempts <= MAX_RECONNECT_ATTEMPTS) {
        setTimeout(() => {
          try {
            // Vite HMR'i yeniden başlat
            if (import.meta.hot) {
              import.meta.hot.send('vite:reconnect');
            }
          } catch (e) {
            console.error('Failed to reconnect HMR:', e);
          }
        }, RECONNECT_DELAY * reconnectAttempts);
      } else {
        console.error('Max reconnection attempts reached. Please refresh the page.');
        // Kullanıcıya sayfayı yenilemesini söyle
        if (typeof window !== 'undefined') {
          const shouldReload = confirm(
            'Bağlantı sorunu tespit edildi. Sayfayı yenilemek ister misiniz?'
          );
          if (shouldReload) {
            window.location.reload();
          }
        }
      }
    }
    
    return true; // Hata işlendi
  }
  
  return false; // Hata işlenmedi
};

/**
 * Network durumunu izler
 */
export const setupNetworkMonitor = () => {
  if (typeof window === 'undefined') return;

  // Online/Offline event listeners
  window.addEventListener('online', () => {
    console.log('Network connection restored');
    reconnectAttempts = 0;
    
    // Vite HMR'i yeniden bağla
    if (import.meta.hot) {
      try {
        import.meta.hot.send('vite:reconnect');
      } catch (e) {
        console.error('Failed to reconnect HMR after network restore:', e);
      }
    }
  });

  window.addEventListener('offline', () => {
    console.warn('Network connection lost');
  });

  // Vite HMR disconnect event
  if (import.meta.hot) {
    import.meta.hot.on('vite:disconnect', () => {
      console.warn('Vite HMR disconnected');
      reconnectAttempts = 0;
    });

    import.meta.hot.on('vite:error', (error) => {
      console.error('Vite HMR error:', error);
      handleNetworkError(error);
    });
  }
};

/**
 * Error boundary için network error kontrolü
 */
export const isNetworkError = (error) => {
  if (!error) return false;
  
  const errorString = String(error);
  const networkErrorPatterns = [
    'ERR_NETWORK_CHANGED',
    'ERR_INTERNET_DISCONNECTED',
    'ERR_NETWORK_ACCESS_DENIED',
    'ERR_CONNECTION_REFUSED',
    'ERR_CONNECTION_RESET',
    'ERR_CONNECTION_CLOSED',
    'ERR_NAME_NOT_RESOLVED',
    'NetworkError',
    'Failed to fetch',
    'Network request failed',
  ];

  return networkErrorPatterns.some(pattern => 
    errorString.includes(pattern) || 
    error?.message?.includes(pattern) ||
    error?.code?.includes(pattern)
  );
};

// Otomatik setup (development modunda)
if (import.meta.env.DEV && typeof window !== 'undefined') {
  setupNetworkMonitor();
}

