// main.js (veya index.js)
import React from 'react';
import ReactDOM from 'react-dom/client';
import { ThemeProvider } from '@mui/material/styles'; // Tema sağlayıcıyı import edin
import theme from './theme'; // Oluşturduğunuz temayı import edin
import App from './App';
import './index.css';

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    {/* TÜM UYGULAMAYI TEMA SAĞLAYICI İLE SARIN */}
    <ThemeProvider theme={theme}>
      <App />
    </ThemeProvider>
  </React.StrictMode>,
);