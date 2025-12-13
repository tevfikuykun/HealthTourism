// src/App.jsx

import React, { lazy, Suspense, useMemo, useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { Box, CssBaseline, ThemeProvider, Typography, CircularProgress } from '@mui/material';

// 🚨 Yeni Importlar (Redux ve React Query)
import { Provider } from 'react-redux'; 
import store from './store'; 
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'; 

import { getTheme } from './theme';
import Header from './components/Header.jsx';
import Footer from './components/Footer.jsx';
import ChatWidget from './components/Chat/ChatWidget';
import Tour from './components/Onboarding/Tour';
import Breadcrumb from './components/Breadcrumb';
// Orijinal rota listenizi kullanmak için
import appRoutes from './config/routes.js'; 

// 🚨 Sayfaları Tembel Yükleme (Lazy Loading) ile içe aktarma
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
const LazyAboutUs = lazy(() => import('./pages/AboutUs.jsx'));
const LazyLogin = lazy(() => import('./pages/Login.jsx'));
const LazyRegister = lazy(() => import('./pages/Register.jsx'));
const LazyDashboard = lazy(() => import('./pages/Dashboard.jsx'));
const LazyForgotPassword = lazy(() => import('./pages/ForgotPassword.jsx'));
const LazyResetPassword = lazy(() => import('./pages/ResetPassword.jsx'));
const LazyVerifyEmail = lazy(() => import('./pages/VerifyEmail.jsx'));
const LazyReservationDetail = lazy(() => import('./pages/ReservationDetail.jsx'));
const LazyPaymentSuccess = lazy(() => import('./pages/PaymentSuccess.jsx'));
const LazyPaymentFailed = lazy(() => import('./pages/PaymentFailed.jsx'));
const LazyNotFound = lazy(() => import('./pages/errors/NotFound.jsx'));
const LazyServerError = lazy(() => import('./pages/errors/ServerError.jsx'));
const LazyForbidden = lazy(() => import('./pages/errors/Forbidden.jsx'));
const LazyUnauthorized = lazy(() => import('./pages/errors/Unauthorized.jsx'));
const LazyAdminLogin = lazy(() => import('./pages/admin/AdminLogin.jsx'));
const LazyAdminDashboard = lazy(() => import('./pages/admin/AdminDashboard.jsx'));
const LazyFavorites = lazy(() => import('./pages/Favorites.jsx'));
const LazyNotificationsPage = lazy(() => import('./pages/Notifications.jsx'));

const queryClient = new QueryClient(); // React Query Client

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
    '/about': LazyAboutUs,
};

// Ek Bilgi Sayfaları için yer tutucu bileşen (Orijinal kodunuzdan)
const PlaceholderPage = ({ title }) => (
    <Box sx={{ py: 10, textAlign: 'center' }}>
        <Typography variant="h4">{title}</Typography>
        <Typography variant="body1" color="text.secondary">Bu sayfa şu anda geliştirme aşamasındadır.</Typography>
    </Box>
);

function App() {
    const [mode, setMode] = useState(() => {
        return localStorage.getItem('theme') || 'light';
    });

    useEffect(() => {
        // Tema değişikliklerini dinle
        const handleStorageChange = () => {
            const newMode = localStorage.getItem('theme') || 'light';
            setMode(newMode);
        };
        window.addEventListener('storage', handleStorageChange);
        return () => window.removeEventListener('storage', handleStorageChange);
    }, []);

    const theme = useMemo(() => getTheme(mode), [mode]);

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
        // 🚨 Providerlar ile Sarma 🚨
        <QueryClientProvider client={queryClient}>
            <Provider store={store}>
                <ThemeProvider theme={theme}>
                    <CssBaseline />
                    <Router>
                        <Box sx={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>

                            <Header />

                            <Box component="main" sx={{ flexGrow: 1 }}>
                                <Breadcrumb />
                                <Routes>
                                    {/* Dinamik Ana Rotalar */}
                                    {appRoutes.map((route) => {
                                        const Element = routeMap[route.path];
                                        if (!Element) return null;

                                        return (
                                            <Route
                                                key={route.path}
                                                path={route.path}
                                                element={renderSuspense(Element)} 
                                            />
                                        );
                                    })}
                                    
                                    {/* Authentication Routes */}
                                    <Route path="/login" element={renderSuspense(LazyLogin)} />
                                    <Route path="/register" element={renderSuspense(LazyRegister)} />
                                    <Route path="/forgot-password" element={renderSuspense(LazyForgotPassword)} />
                                    <Route path="/reset-password/:token" element={renderSuspense(LazyResetPassword)} />
                                    <Route path="/verify-email" element={renderSuspense(LazyVerifyEmail)} />
                                    
                                    {/* User Dashboard */}
                                    <Route path="/dashboard" element={renderSuspense(LazyDashboard)} />
                                    <Route path="/favorites" element={renderSuspense(LazyFavorites)} />
                                    <Route path="/notifications" element={renderSuspense(LazyNotificationsPage)} />
                                    
                                    {/* Reservation Details */}
                                    <Route path="/reservations/:id" element={renderSuspense(LazyReservationDetail)} />
                                    
                                    {/* Admin Routes */}
                                    <Route path="/admin/login" element={renderSuspense(LazyAdminLogin)} />
                                    <Route path="/admin/dashboard" element={renderSuspense(LazyAdminDashboard)} />
                                    
                                    {/* Payment Routes */}
                                    <Route path="/payment/success" element={renderSuspense(LazyPaymentSuccess)} />
                                    <Route path="/payment/failed" element={renderSuspense(LazyPaymentFailed)} />
                                    
                                    {/* Footer ve Detay Rotaları (Manuel) */}
                                    <Route path="/about" element={renderSuspense(LazyAboutUs)} />
                                    <Route path="/why-us" element={<PlaceholderPage title="Neden Biz?" />} />
                                    <Route path="/privacy" element={<PlaceholderPage title="Gizlilik Politikası" />} />
                                    <Route path="/terms" element={<PlaceholderPage title="Kullanım Koşulları" />} />

                                    <Route path="/hospitals/:slug" element={<PlaceholderPage title="Hastane Detayları" />} />
                                    <Route path="/doctors/:slug" element={<PlaceholderPage title="Doktor Profili" />} />
                                    <Route path="/accommodations/:slug" element={<PlaceholderPage title="Konaklama Detayları" />} />
                                    <Route path="/packages/:slug" element={<PlaceholderPage title="Paket Teklif Sayfası" />} />

                                    {/* Error Pages */}
                                    <Route path="/404" element={renderSuspense(LazyNotFound)} />
                                    <Route path="/500" element={renderSuspense(LazyServerError)} />
                                    <Route path="/403" element={renderSuspense(LazyForbidden)} />
                                    <Route path="/401" element={renderSuspense(LazyUnauthorized)} />
                                    
                                    <Route path="*" element={renderSuspense(LazyNotFound)} />
                                </Routes>
                            </Box>

                            <Footer />

                        </Box>
                        <ChatWidget />
                        <Tour />
                    </Router>
                </ThemeProvider>
            </Provider>
        </QueryClientProvider>
    );
}

export default App;