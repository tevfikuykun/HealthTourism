import { api } from '../services/api';

/**
 * Currency utility for multi-currency support
 */
let exchangeRates = {};
let currentCurrency = localStorage.getItem('currency') || 'TRY';

/**
 * Initialize currency service
 */
export const initCurrency = async () => {
  try {
    const response = await api.get('/currency/rates/TRY');
    exchangeRates = response.data;
  } catch (error) {
    console.warn('Failed to load exchange rates, using fallback');
    // Fallback rates
    exchangeRates = {
      TRY: 1,
      USD: 0.033,
      EUR: 0.030,
      GBP: 0.026,
    };
  }
};

/**
 * Get current currency
 */
export const getCurrentCurrency = () => currentCurrency;

/**
 * Set current currency
 */
export const setCurrency = (currency) => {
  currentCurrency = currency;
  localStorage.setItem('currency', currency);
};

/**
 * Convert amount to current currency
 */
export const convertToCurrentCurrency = async (amount, fromCurrency = 'TRY') => {
  if (fromCurrency === currentCurrency) {
    return amount;
  }

  try {
    const response = await api.get('/currency/convert', {
      params: {
        amount,
        from: fromCurrency,
        to: currentCurrency,
      },
    });
    return response.data;
  } catch (error) {
    console.warn('Currency conversion failed, returning original amount');
    return amount;
  }
};

/**
 * Format currency amount
 */
export const formatCurrency = (amount, currency = null) => {
  const curr = currency || currentCurrency;
  const formattedAmount = new Intl.NumberFormat('tr-TR', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  }).format(amount);

  const symbols = {
    TRY: '₺',
    USD: '$',
    EUR: '€',
    GBP: '£',
  };

  return `${formattedAmount} ${symbols[curr] || curr}`;
};

/**
 * Get currency symbol
 */
export const getCurrencySymbol = (currency = null) => {
  const curr = currency || currentCurrency;
  const symbols = {
    TRY: '₺',
    USD: '$',
    EUR: '€',
    GBP: '£',
  };
  return symbols[curr] || curr;
};

export default {
  initCurrency,
  getCurrentCurrency,
  setCurrency,
  convertToCurrentCurrency,
  formatCurrency,
  getCurrencySymbol,
};
