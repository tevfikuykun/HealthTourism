/**
 * UI Helpers - Material-UI + Tailwind CSS + Framer Motion Integration
 * 
 * Bu dosya, üç teknolojiyi birlikte kullanmak için yardımcı fonksiyonlar ve pattern'ler sağlar.
 */

/**
 * Framer Motion animasyon variant'ları
 */
export const fadeInUp = {
  initial: { opacity: 0, y: 20 },
  animate: { opacity: 1, y: 0 },
  exit: { opacity: 0, y: -20 },
};

export const fadeIn = {
  initial: { opacity: 0 },
  animate: { opacity: 1 },
  exit: { opacity: 0 },
};

export const scaleIn = {
  initial: { opacity: 0, scale: 0.9 },
  animate: { opacity: 1, scale: 1 },
  exit: { opacity: 0, scale: 0.9 },
};

export const slideInRight = {
  initial: { opacity: 0, x: -20 },
  animate: { opacity: 1, x: 0 },
  exit: { opacity: 0, x: 20 },
};

export const staggerContainer = {
  initial: { opacity: 0 },
  animate: {
    opacity: 1,
    transition: {
      staggerChildren: 0.1,
    },
  },
};

export const staggerItem = {
  initial: { opacity: 0, y: 20 },
  animate: { opacity: 1, y: 0 },
};

/**
 * Tailwind CSS class'larını Material-UI sx prop ile birleştirir
 * @param {string} tailwindClasses - Tailwind CSS class'ları
 * @param {object} sxProps - Material-UI sx prop objesi
 * @returns {object} Birleştirilmiş sx prop objesi
 */
export const combineStyles = (tailwindClasses = '', sxProps = {}) => {
  return {
    className: tailwindClasses,
    sx: sxProps,
  };
};

/**
 * Material-UI Card component'ini Tailwind ve Framer Motion ile sarmalar
 */
export const createAnimatedCard = (motion, Card, delay = 0) => {
  return motion(Card);
};

/**
 * Hover animasyonları için yardımcı
 */
export const hoverScale = {
  whileHover: { scale: 1.02 },
  whileTap: { scale: 0.98 },
};

export const hoverLift = {
  whileHover: { y: -4, transition: { duration: 0.2 } },
};

/**
 * Stagger animasyonu için container ve item variant'ları
 */
export const staggerVariants = {
  container: {
    hidden: { opacity: 0 },
    show: {
      opacity: 1,
      transition: {
        staggerChildren: 0.1,
      },
    },
  },
  item: {
    hidden: { opacity: 0, y: 20 },
    show: { opacity: 1, y: 0 },
  },
};






