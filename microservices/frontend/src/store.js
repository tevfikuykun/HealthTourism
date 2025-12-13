// src/store.js
import { configureStore } from '@reduxjs/toolkit';
import langReducer from './features/langSlice';
import userReducer from './features/userSlice';

const store = configureStore({
  reducer: {
    lang: langReducer,
    user: userReducer,
  },
});

export default store;