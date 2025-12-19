/**
 * Theme Colors Utility
 * Projeye özel renk paletini kullanmak için yardımcı fonksiyonlar
 * Material-UI theme'inden custom renkleri almak için
 */

/**
 * Custom theme renklerini alır
 * @param {object} theme - Material-UI theme objesi
 * @returns {object} Custom renkler
 */
export const getCustomColors = (theme) => {
  return theme.custom?.colors || {
    ai: {
      main: '#8B5CF6',
      light: '#A78BFA',
      dark: '#7C3AED',
    },
    iot: {
      main: '#F43F5E',
      light: '#FB7185',
      dark: '#E11D48',
    },
    blockchain: {
      main: '#4F46E5',
      light: '#818CF8',
      dark: '#3730A3',
      accent: '#6366F1',
    },
    glass: {
      light: 'rgba(255, 255, 255, 0.05)',
      medium: 'rgba(255, 255, 255, 0.1)',
      dark: 'rgba(0, 0, 0, 0.2)',
    },
  };
};

/**
 * Neon efekt renklerini alır
 * @param {object} theme - Material-UI theme objesi
 * @returns {object} Neon renkler
 */
export const getNeonColors = (theme) => {
  return theme.custom?.neon || {
    indigo: '#818CF8',
    purple: '#A78BFA',
    sky: '#38BDF8',
  };
};

/**
 * AI teşhisleri için renk
 */
export const AI_COLOR = {
  main: '#8B5CF6',
  light: '#A78BFA',
  dark: '#7C3AED',
};

/**
 * IoT hayati bulgular için renk (yumuşak kırmızı)
 */
export const IOT_COLOR = {
  main: '#F43F5E',
  light: '#FB7185',
  dark: '#E11D48',
};

/**
 * Blockchain verileri için renk (vibrant indigo)
 */
export const BLOCKCHAIN_COLOR = {
  main: '#4F46E5',
  light: '#818CF8',
  dark: '#3730A3',
  accent: '#6366F1',
};

/**
 * Glass effect için renkler
 */
export const GLASS_COLOR = {
  light: 'rgba(255, 255, 255, 0.05)',
  medium: 'rgba(255, 255, 255, 0.1)',
  dark: 'rgba(0, 0, 0, 0.2)',
};

/**
 * Neon efekt renkleri (dark mode için)
 */
export const NEON_COLOR = {
  indigo: '#818CF8', // 3D model glow için
  purple: '#A78BFA', // AI highlights için
  sky: '#38BDF8', // Health indicators için
};


