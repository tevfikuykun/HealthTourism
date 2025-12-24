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
    // Backend'i kaldırdık - manuel yükleme kullanacağız
    // .use(Backend)
    .use(LanguageDetector)
    .use(initReactI18next)
    .init({
      fallbackLng: "tr",
      defaultNS: "translation",
      debug: false, // Missing key uyarılarını kapat
      
      // Fallback resources - i18n yüklenemese bile çalışsın
      resources: fallbackResources,
      
      
      detection: {
        order: ['localStorage', 'navigator', 'htmlTag'],
        caches: ['localStorage'],
        lookupLocalStorage: 'i18nextLng',
      },

      interpolation: {
        escapeValue: false
      },
      
      // Key separator - nested key'ler için
      keySeparator: '.',
      nsSeparator: ':',
      
      react: {
        useSuspense: false,
        bindI18n: 'languageChanged loaded',
        bindI18nStore: 'added removed',
      },
      
      // Hata durumunda bile çalışmaya devam et
      partialBundledLanguages: true,
      load: 'languageOnly',
      
      // Hemen başlat (manuel yükleme kullanacağız)
      initImmediate: false,
    });
};

// Manuel olarak JSON yükle ve ekle (nested yapıyı korumak için)
const loadTranslations = async (lang) => {
  try {
    // Normalize language code (tr-TR -> tr)
    const langCode = lang.split('-')[0];
    
    // public/locales'den fetch ile yükle
    const response = await fetch(`/locales/${langCode}.json?t=${Date.now()}`, {
      cache: 'no-cache'
    });
    
    if (!response.ok) {
      console.warn(`Failed to load translations for ${langCode}: ${response.status}`);
      return;
    }
    
    const translations = await response.json();
    console.log('📦 Loaded translations for', lang, ':', Object.keys(translations));
    
    // Nested yapıyı koruyarak ekle - tüm namespace'leri ayrı ayrı ekle
    i18n.addResourceBundle(lang, 'translation', translations, true, true);
    
    // Namespace'leri de ayrı ayrı ekle (nav, header, footer, vb.)
    if (translations.nav) {
      i18n.addResourceBundle(lang, 'nav', translations.nav, true, true);
    }
    if (translations.header) {
      i18n.addResourceBundle(lang, 'header', translations.header, true, true);
    }
    if (translations.footer) {
      i18n.addResourceBundle(lang, 'footer', translations.footer, true, true);
    }
    if (translations.common) {
      i18n.addResourceBundle(lang, 'common', translations.common, true, true);
    }
    if (translations.home) {
      i18n.addResourceBundle(lang, 'home', translations.home, true, true);
    }
    
    console.log('✅ Translations added to i18n');
    const bundle = i18n.getResourceBundle(lang, 'translation');
    if (bundle && bundle.common) {
      console.log('✅ common namespace loaded:', Object.keys(bundle.common).length, 'keys');
    }
    if (bundle && bundle.home) {
      console.log('✅ home namespace loaded:', Object.keys(bundle.home).length, 'keys');
    }
    if (bundle && bundle.nav) {
      console.log('✅ nav namespace loaded:', Object.keys(bundle.nav).length, 'keys');
    }
    if (bundle && bundle.header) {
      console.log('✅ header namespace loaded:', Object.keys(bundle.header).length, 'keys');
    }
  } catch (error) {
    console.error('Error loading translations:', error);
  }
};

// Initialize i18n - hemen başlat
initI18n()
  .then(() => {
    console.log('✅ i18n initialized successfully');
    console.log('Current language:', i18n.language);
    
    // Backend'den JSON'u manuel olarak yükle
    const lang = i18n.language || 'tr';
    loadTranslations(lang);
    
    // Dil değiştiğinde de yükle
    i18n.on('languageChanged', (lng) => {
      loadTranslations(lng);
    });
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
    
    // Nested key'leri destekle (common.login, home.browseHospitals)
    if (key.includes('.') && !key.includes(':')) {
      const parts = key.split('.');
      if (parts.length === 2) {
        const namespace = parts[0];
        const keyName = parts[1];
        const lang = i18n.language || 'tr';
        
        // Önce i18n.getResourceBundle ile dene (en güvenilir yöntem)
        try {
          const bundle = i18n.getResourceBundle(lang, 'translation');
          if (bundle && bundle[namespace] && typeof bundle[namespace] === 'object') {
            const nested = bundle[namespace];
            if (nested && typeof nested[keyName] === 'string') {
              return nested[keyName];
            }
          }
        } catch (e) {
          // getResourceBundle başarısız olursa devam et
        }
        
        // Alternatif: i18n.store.data ile dene
        try {
          if (i18n.store && i18n.store.data) {
            const resources = i18n.store.data[lang];
            if (resources && resources.translation) {
              const nested = resources.translation[namespace];
              if (nested && typeof nested === 'object' && typeof nested[keyName] === 'string') {
                return nested[keyName];
              }
            }
          }
        } catch (e) {
          // store.data başarısız olursa devam et
        }
        
        // Son çare: Orijinal t fonksiyonunu dene (i18next'in kendi nested key desteği)
        const directResult = originalT(key, options);
        if (directResult !== key && directResult !== `${namespace}.${keyName}`) {
          return directResult;
        }
      }
    }
    
    // Orijinal t fonksiyonunu çağır - dil değişikliğini destekler
    let result = originalT(key, options);
    
    // Eğer key bulunamadıysa ve nested key değilse, common namespace'inde dene
    if (result === key && !key.includes('.')) {
      // common.dashboard, common.profile gibi key'leri dene
      const commonKey = `common.${key}`;
      try {
        if (i18n.store && i18n.store.data) {
          const lang = i18n.language || 'tr';
          const resources = i18n.store.data[lang];
          if (resources && resources.translation && resources.translation.common) {
            const commonValue = resources.translation.common[key];
            if (typeof commonValue === 'string') {
              return commonValue;
            }
          }
        }
      } catch (e) {
        // Hata durumunda devam et
      }
      
      // getResourceBundle ile de dene
      try {
        const bundle = i18n.getResourceBundle(i18n.language || 'tr', 'translation');
        if (bundle && bundle.common && typeof bundle.common[key] === 'string') {
          return bundle.common[key];
        }
      } catch (e) {
        // Hata durumunda devam et
      }
    }
    
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

// changeLanguage fonksiyonunu override et - backend'den yeni dil dosyasını yükle
const originalChangeLanguage = i18n.changeLanguage.bind(i18n);
i18n.changeLanguage = async function(lng, callback) {
  try {
    console.log('changeLanguage çağrıldı:', lng);
    const result = await originalChangeLanguage(lng, callback);
    // Manuel olarak yeni dil dosyasını yükle
    await loadTranslations(lng);
    // Event dispatch et - component'lerin güncellenmesi için
    window.dispatchEvent(new Event('languagechange'));
    return result;
  } catch (error) {
    console.error('changeLanguage hatası:', error);
    // Hata olsa bile devam et
    if (callback) callback(error, null);
    throw error;
  }
};

// react-i18next'in useTranslation hook'unu override et
import { useTranslation as useTranslationOriginal } from 'react-i18next';

// useTranslation hook'unu wrap et - her zaman güvenli t fonksiyonu döndürsün
export const useTranslation = (ns) => {
  try {
    const translation = useTranslationOriginal(ns);
    
    // Güvenli t fonksiyonu oluştur - dil değişikliğinde otomatik güncellenir
    // Direkt i18n.t kullan çünkü nested key desteği orada
    const safeT = (key, options) => {
      try {
        // Direkt i18n.t kullan - nested key desteği ile
        return i18n.t(key, options);
      } catch (e) {
        console.warn(`Translation error for key "${key}":`, e);
        const fallback = options?.defaultValue || (typeof options === 'string' ? options : null) || key;
        return typeof fallback === 'string' ? fallback : String(key);
      }
    };
    
    // react-i18next'in ready state'ini kullan - dil değişikliğinde otomatik re-render tetikler
    return {
      ...translation,
      t: safeT,
      // ready state'i component'lerin re-render olmasını sağlar
      ready: translation.ready !== undefined ? translation.ready : i18n.isInitialized,
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
