// src/features/langSlice.js
import { createSlice } from '@reduxjs/toolkit';

const initialState = {
  // Başlangıç değerleri i18n ile uyumlu olmalıdır
  currentLang: 'tr', 
  currency: 'TRY', // Örn: TL yerine TRY kullanmak daha evrensel
};

const langSlice = createSlice({
  name: 'lang',
  initialState,
  reducers: {
    setLanguage: (state, action) => {
      state.currentLang = action.payload;
    },
    setCurrency: (state, action) => {
      state.currency = action.payload;
    },
  },
});

export const { setLanguage, setCurrency } = langSlice.actions;
export default langSlice.reducer;