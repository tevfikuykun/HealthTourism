// hooks/useSafeTranslation.js
import { useTranslation } from 'react-i18next';
import i18n from '../i18n';

/**
 * Güvenli translation hook - i18n hazır olmasa bile çalışır
 */
export function useSafeTranslation() {
  let t;
  let ready = false;
  let i18nInstance = null;
  
  try {
    const translation = useTranslation();
    t = translation.t;
    ready = translation.ready || false;
    i18nInstance = translation.i18n || i18n;
  } catch (e) {
    console.warn('useTranslation hook error, using fallback:', e);
    // Fallback: direkt i18n instance'ını kullan
    i18nInstance = i18n;
    ready = i18n.isInitialized || false;
  }
  
  // Güvenli t fonksiyonu - her zaman çalışır
  const safeT = (key, fallback) => {
    if (!ready || !i18nInstance) {
      return fallback || key;
    }
    
    try {
      const result = i18nInstance.t(key, fallback);
      // Eğer key dönerse (çeviri bulunamadı), fallback kullan
      return result === key ? (fallback || key) : result;
    } catch (e) {
      console.warn(`Translation error for key "${key}":`, e);
      return fallback || key;
    }
  };
  
  return {
    t: safeT,
    ready: ready || i18n.isInitialized,
    i18n: i18nInstance || i18n,
    language: i18nInstance?.language || i18n.language || 'tr',
  };
}

