// src/App.jsx

import React, { lazy, Suspense } from 'react'; // 'lazy' ve 'Suspense' eklendi
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { Box, CssBaseline, ThemeProvider, Typography, CircularProgress } from '@mui/material'; // CircularProgress eklendi

import theme from './theme';
import Header from './components/Header.jsx';
import Footer from './components/Footer.jsx';
import appRoutes from './config/routes.js'; // Rota listesini içe aktar

// 🚨 SAYFALARI TEMBEL YÜKLEME (LAZY LOADING) İLE TANIMLAMA 🚨
const LazyHome = lazy(() => import('./pages/Home.jsx'));
const LazyHospitals = lazy(() => import('./pages/Hospitals.jsx'));
const LazyDoctors = lazy(() => import('./pages/Doctors.jsx'));
const LazyAccommodations = lazy(() => import('./pages/Accommodations.jsx'));
const LazyFlights = lazy(() => import('./pages/Flights.jsx'));
const LazyCarRentals = lazy(() => import('./pages/CarRentals.jsx'));
const LazyTransfers = lazy(() => import('./pages/Transfers.jsx'));
const LazyPackages = lazy(() => import('./pages/Packages.jsx'));
const LazyReservations = lazy(() => import('./pages/Reservations.jsx'));
const LazyPayments = lazy(() => import('./pages/Payments.jsx'));
const LazyAboutUs = lazy(() => import('./pages/AboutUs.jsx')); // AboutUs da tembel yüklendi

// Rota ve bileşen eşleşmesini dinamik hale getiren harita
const routeMap = {
    '/': LazyHome,
    '/hospitals': LazyHospitals,
    '/doctors': LazyDoctors,
    '/accommodations': LazyAccommodations,
    '/flights': LazyFlights,
    '/car-rentals': LazyCarRentals,
    '/transfers': LazyTransfers,
    '/packages': LazyPackages,
    '/reservations': LazyReservations,
    '/payments': LazyPayments,
    '/about': LazyAboutUs, // Footer linki için
};

// Ek Bilgi Sayfaları için yer tutucu bileşen (Bu sabit kalabilir)
const PlaceholderPage = ({ title }) => (
    <Box sx={{ py: 10, textAlign: 'center' }}>
        <Typography variant="h4">{title}</Typography>
        <Typography variant="body1" color="text.secondary">Bu sayfa şu anda geliştirme aşamasındadır.</Typography>
    </Box>
);

function App() {
    // Yükleme sırasında gösterilecek ortak yükleyici bileşeni
    const renderSuspense = (Element) => (
        <Suspense
            fallback={
                <Box sx={{ display: 'flex', justifyContent: 'center', py: 10 }}>
                    <CircularProgress color="primary" />
                </Box>
            }
        >
            <Element />
        </Suspense>
    );

    return (
        <ThemeProvider theme={theme}>
            <CssBaseline />
            <Router>
                <Box sx={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>

                    <Header />

                    <Box component="main" sx={{ flexGrow: 1 }}>
                        <Routes>
                            {/* 🚀 DİNAMİK ROTA TANIMI: appRoutes listesini kullanma */}
                            {appRoutes.map((route) => {
                                const Element = routeMap[route.path];
                                if (!Element) return null;

                                return (
                                    <Route
                                        key={route.path}
                                        path={route.path}
                                        // Tembel yüklenmiş bileşeni Suspense içine sarar
                                        element={renderSuspense(Element)}
                                    />
                                );
                            })}

                            {/* Footer ve Detay Rotaları (Manuel olarak Placeholder veya Detay rotaları) */}
                            <Route path="/about" element={renderSuspense(LazyAboutUs)} />
                            <Route path="/why-us" element={<PlaceholderPage title="Neden Biz?" />} />
                            <Route path="/privacy" element={<PlaceholderPage title="Gizlilik Politikası" />} />
                            <Route path="/terms" element={<PlaceholderPage title="Kullanım Koşulları" />} />

                            <Route path="/hospitals/:slug" element={<PlaceholderPage title="Hastane Detayları" />} />
                            <Route path="/doctors/:slug" element={<PlaceholderPage title="Doktor Profili" />} />
                            <Route path="/accommodations/:slug" element={<PlaceholderPage title="Konaklama Detayları" />} />
                            <Route path="/packages/:slug" element={<PlaceholderPage title="Paket Teklif Sayfası" />} />

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