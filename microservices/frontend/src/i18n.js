// src/i18n.js
import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import LanguageDetector from 'i18next-browser-languagedetector';
import Backend from 'i18next-http-backend';

// Fallback translations - i18n yüklenemese bile çalışsın
const fallbackResources = {
  tr: {
    translation: {
      welcome: "Hoş Geldiniz",
      hospitals: "Hastaneler",
      doctors: "Doktorlar",
      somethingWentWrong: "Bir şeyler yanlış gitti",
      tryAgain: "Tekrar Dene",
      goToHomepage: "Ana Sayfaya Dön",
      heroTitle: "Kişiselleştirilmiş Sağlık Yolculuğunuz: Profesyonel Tedavi, Konforlu Konaklama",
      heroSubtitle: "En seçkin hastaneler, kişiselleştirilmiş tedavi planları ve A'dan Z'ye tüm seyahat organizasyonu.",
      getFreeQuote: "Ücretsiz Teklif Alın",
      whyUs: "Neden Biz?",
      company: "Şirket",
      services: "Hizmetler",
      followUs: "Bizi Takip Edin",
      allRightsReserved: "Tüm hakları saklıdır",
      explore: "Keşfet",
    }
  }
};

// Lazy loading için backend kullan - projeyi kasmaz
// JSON dosyaları runtime'da yüklenecek

const initI18n = () => {
  return i18n
    .use(Backend) // Lazy loading için backend
    .use(LanguageDetector)
    .use(initReactI18next)
    .init({
      fallbackLng: "tr",
      defaultNS: "translation",
      debug: true,
      
      // Fallback resources - i18n yüklenemese bile çalışsın
      resources: fallbackResources,
      
      // Backend configuration - JSON dosyalarını lazy load et
      backend: {
        loadPath: '/locales/{{lng}}.json',
        crossDomain: false,
        requestOptions: {
          cache: 'no-cache'
        },
        // Hata durumunda fallback kullan
        allowMultiLoading: false,
      },
      
      detection: {
        order: ['localStorage', 'navigator', 'htmlTag'],
        caches: ['localStorage'],
        lookupLocalStorage: 'i18nextLng',
      },

      interpolation: {
        escapeValue: false
      },
      
      react: {
        useSuspense: false,
      },
      
      // Hata durumunda bile çalışmaya devam et
      partialBundledLanguages: true,
      load: 'languageOnly',
      
      // Backend hata olsa bile devam et
      initImmediate: false,
    });
};

// Initialize i18n - hemen başlat
initI18n()
  .then(() => {
    console.log('✅ i18n initialized successfully');
    console.log('Current language:', i18n.language);
    console.log('Available languages:', i18n.languages);
    console.log('i18n.isInitialized:', i18n.isInitialized);
  })
  .catch((err) => {
    console.error('❌ i18n initialization failed:', err);
    // Hata olsa bile devam et - fallback kullanılacak
    console.warn('Continuing with fallback translations...');
    // Manuel olarak initialized olarak işaretle
    if (!i18n.isInitialized) {
      i18n.isInitialized = true;
    }
  });

// Güvenli t fonksiyonu - her zaman çalışır ve her zaman string döndürür
const originalT = i18n.t.bind(i18n);
i18n.t = function(key, options) {
  try {
    if (!i18n.isInitialized) {
      // Fallback kullan
      const fallback = options?.defaultValue || (typeof options === 'string' ? options : null) || key;
      return typeof fallback === 'string' ? fallback : String(key);
    }
    const result = originalT(key, options);
    // Eğer key ile aynı dönerse (çeviri bulunamadı), fallback kullan
    if (result === key && options?.defaultValue) {
      return options.defaultValue;
    }
    // Her zaman string döndür
    return typeof result === 'string' ? result : String(result || key);
  } catch (e) {
    console.warn(`Translation error for key "${key}":`, e);
    const fallback = options?.defaultValue || (typeof options === 'string' ? options : null) || key;
    return typeof fallback === 'string' ? fallback : String(key);
  }
};

// react-i18next'in useTranslation hook'unu override et
import { useTranslation as useTranslationOriginal } from 'react-i18next';

// useTranslation hook'unu wrap et - her zaman güvenli t fonksiyonu döndürsün
export const useTranslation = (ns) => {
  try {
    const translation = useTranslationOriginal(ns);
    
    // Güvenli t fonksiyonu oluştur
    const safeT = (key, options) => {
      try {
        if (translation.t && typeof translation.t === 'function') {
          const result = translation.t(key, options);
          // Eğer key ile aynı dönerse ve defaultValue varsa, onu kullan
          if (result === key && options?.defaultValue) {
            return options.defaultValue;
          }
          // Her zaman string döndür
          return typeof result === 'string' ? result : String(result || key);
        }
        // translation.t yoksa i18n.t kullan
        return i18n.t(key, options);
      } catch (e) {
        console.warn(`Translation error for key "${key}":`, e);
        const fallback = options?.defaultValue || (typeof options === 'string' ? options : null) || key;
        return typeof fallback === 'string' ? fallback : String(key);
      }
    };
    
    return {
      ...translation,
      t: safeT,
    };
  } catch (e) {
    console.warn('useTranslation hook error:', e);
    // Fallback: direkt i18n kullan
    return {
      t: (key, options) => i18n.t(key, options),
      i18n: i18n,
      ready: i18n.isInitialized,
      language: i18n.language || 'tr',
    };
  }
};

export default i18n;
