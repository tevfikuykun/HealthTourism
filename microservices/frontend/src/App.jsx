// src/App.jsx
import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { Box, CssBaseline, ThemeProvider, Typography } from '@mui/material';

// Temayı Import Ediyoruz
import theme from './theme';

// Bileşenleri Import Ediyoruz
import Header from './components/Header.jsx';
import Footer from './components/Footer.jsx';

// Sayfaları Import Ediyoruz
import Home from './pages/Home.jsx';
import Hospitals from './pages/Hospitals.jsx';
import Doctors from './pages/Doctors.jsx';
import Accommodations from './pages/Accommodations.jsx';
import Flights from './pages/Flights.jsx';
import CarRentals from './pages/CarRentals.jsx';
import Transfers from './pages/Transfers.jsx';
import Packages from './pages/Packages.jsx';
import Reservations from './pages/Reservations.jsx';
import Payments from './pages/Payments.jsx';

// Ek Bilgi Sayfaları için yer tutucu bileşen
const PlaceholderPage = ({ title }) => (
    <Box sx={{ py: 10, textAlign: 'center' }}>
        <Typography variant="h4">{title}</Typography>
        <Typography variant="body1" color="text.secondary">Bu sayfa şu anda geliştirme aşamasındadır.</Typography>
    </Box>
);

function App() {
    return (
        <ThemeProvider theme={theme}>
            <CssBaseline />
            <Router>
                <Box sx={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>

                    <Header />

                    <Box component="main" sx={{ flexGrow: 1 }}>
                        <Routes>
                            {/* Ana Sayfa */}
                            <Route path="/" element={<Home />} />

                            {/* Ana Listeleme Sayfaları */}
                            <Route path="/hospitals" element={<Hospitals />} />
                            <Route path="/doctors" element={<Doctors />} />
                            <Route path="/accommodations" element={<Accommodations />} />
                            <Route path="/packages" element={<Packages />} />

                            {/* Seyahat Hizmetleri */}
                            <Route path="/flights" element={<Flights />} />
                            <Route path="/car-rentals" element={<CarRentals />} />
                            <Route path="/transfers" element={<Transfers />} />

                            {/* İşlem Sayfaları */}
                            <Route path="/reservations" element={<Reservations />} />
                            <Route path="/payments" element={<Payments />} />

                            {/* Footer Linkleri için Geçici Sayfalar */}
                            <Route path="/about" element={<PlaceholderPage title="Hakkımızda" />} />
                            <Route path="/why-us" element={<PlaceholderPage title="Neden Biz?" />} />
                            <Route path="/privacy" element={<PlaceholderPage title="Gizlilik Politikası" />} />
                            <Route path="/terms" element={<PlaceholderPage title="Kullanım Koşulları" />} />

                            {/* Detay Sayfaları için yer tutucular */}
                            <Route path="/hospitals/:slug" element={<PlaceholderPage title="Hastane Detayları" />} />
                            <Route path="/doctors/:slug" element={<PlaceholderPage title="Doktor Profili" />} />
                            <Route path="/accommodations/:slug" element={<PlaceholderPage title="Konaklama Detayları" />} />
                            <Route path="/packages/:slug" element={<PlaceholderPage title="Paket Teklif Sayfası" />} />

                            {/* 404 Sayfası */}
                            <Route path="*" element={<PlaceholderPage title="404 - Sayfa Bulunamadı" />} />
                        </Routes>
                    </Box>

                    <Footer />

                </Box>
            </Router>
        </ThemeProvider>
    );
}

export default App;


