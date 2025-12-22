// src/App.jsx

import React, { lazy, Suspense, useMemo, useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { Box, CssBaseline, ThemeProvider, Typography, CircularProgress } from '@mui/material';

// 🚨 Yeni Importlar (Redux ve React Query)
import { Provider } from 'react-redux'; 
import store from './store'; 
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { useTranslation } from './i18n';
import i18n from './i18n'; 

import { getTheme } from './theme';
import Header from './components/Header/EnhancedHeader.jsx';
import Footer from './components/Footer.jsx';
// RainbowKit & Wagmi for Web3 Wallet Connection
import { RainbowKitProvider } from '@rainbow-me/rainbowkit';
import { WagmiProvider } from 'wagmi';
import { wagmiConfig } from './config/wagmi';
import ChatWidget from './components/Chat/ChatWidget';
// Chatbot removed - duplicate chat button, using ChatWidget instead
import Tour from './components/Onboarding/Tour';
// Breadcrumb removed - user requested removal
import OfflineIndicator from './components/OfflineIndicator/OfflineIndicator';
import CookieConsent from './components/CookieConsent/CookieConsent';
import AccessibilityMenu from './components/Accessibility/AccessibilityMenu';
import SmartConcierge from './components/SmartConcierge/SmartConcierge';
// Orijinal rota listenizi kullanmak için
import appRoutes from './config/routes.js'; 

// 🚨 Sayfaları Tembel Yükleme (Lazy Loading) ile içe aktarma
const LazyHome = lazy(() => import('./pages/Home.jsx'));
const LazyHospitals = lazy(() => import('./pages/Hospitals.jsx'));
const LazyDoctors = lazy(() => import('./pages/Doctors.jsx'));
const LazyHospitalDetail = lazy(() => import('./pages/HospitalDetail.jsx'));
const LazyDoctorDetail = lazy(() => import('./pages/DoctorDetail.jsx'));
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
const LazyDashboard = lazy(() => import('./pages/Dashboard'));
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
const LazyVideoConsultation = lazy(() => import('./pages/VideoConsultation.jsx'));
const LazyTravelPlanner = lazy(() => import('./pages/TravelPlanner.jsx'));
const LazyForum = lazy(() => import('./pages/Forum.jsx'));
const LazyTwoFactorAuth = lazy(() => import('./pages/TwoFactorAuth.jsx'));
const LazyAnalytics = lazy(() => import('./pages/Analytics.jsx'));
const LazyHealthRecords = lazy(() => import('./pages/HealthRecords.jsx'));
const LazyMedicationReminder = lazy(() => import('./pages/MedicationReminder.jsx'));
const LazyReferralProgram = lazy(() => import('./pages/ReferralProgram.jsx'));
const LazyLocalGuide = lazy(() => import('./pages/LocalGuide.jsx'));
const LazyCoupons = lazy(() => import('./pages/Coupons.jsx'));
const LazyPaymentHistory = lazy(() => import('./pages/PaymentHistory.jsx'));
const LazyInstallmentPlans = lazy(() => import('./pages/InstallmentPlans.jsx'));
const LazyCryptoPayment = lazy(() => import('./pages/CryptoPayment.jsx'));
const LazySmartCalendar = lazy(() => import('./pages/SmartCalendar.jsx'));
const LazyBulkReservation = lazy(() => import('./pages/BulkReservation.jsx'));
const LazyWaitingList = lazy(() => import('./pages/WaitingList.jsx'));
const LazyBiometricAuth = lazy(() => import('./pages/BiometricAuth.jsx'));
const LazySecurityAlerts = lazy(() => import('./pages/SecurityAlerts.jsx'));
const LazyAIRecommendations = lazy(() => import('./pages/AIRecommendations.jsx'));
const LazyLoyaltyProgram = lazy(() => import('./pages/LoyaltyProgram.jsx'));
const LazyHospitalsWithMap = lazy(() => import('./pages/HospitalsWithMap.jsx'));
const LazyRealTimeNotifications = lazy(() => import('./pages/RealTimeNotifications.jsx'));
const LazyUserManagement = lazy(() => import('./pages/admin/UserManagement.jsx'));
const LazyReports = lazy(() => import('./pages/admin/Reports.jsx'));
const LazyInvoices = lazy(() => import('./pages/Invoices.jsx'));
const LazyGDPRDataExport = lazy(() => import('./pages/GDPRDataExport.jsx'));
const LazyContentManagement = lazy(() => import('./pages/admin/ContentManagement.jsx'));
const LazyReservationManagement = lazy(() => import('./pages/admin/ReservationManagement.jsx'));
const LazyFinancialManagement = lazy(() => import('./pages/admin/FinancialManagement.jsx'));
const LazySystemSettings = lazy(() => import('./pages/admin/SystemSettings.jsx'));
const LazyAuditLog = lazy(() => import('./pages/admin/AuditLog.jsx'));
const LazyRateLimiting = lazy(() => import('./pages/admin/RateLimiting.jsx'));
const LazyNotificationPreferences = lazy(() => import('./pages/NotificationPreferences.jsx'));
const LazyAdvancedSearch = lazy(() => import('./pages/AdvancedSearch.jsx'));
const LazyCurrencyConverter = lazy(() => import('./pages/CurrencyConverter.jsx'));
const LazyTaxCalculator = lazy(() => import('./pages/TaxCalculator.jsx'));
const LazyContentModeration = lazy(() => import('./pages/admin/ContentModeration.jsx'));
const LazySEOOptimization = lazy(() => import('./pages/SEOOptimization.jsx'));
const LazyWhyUs = lazy(() => import('./pages/WhyUs.jsx'));
const LazyPrivacy = lazy(() => import('./pages/Privacy.jsx'));
const LazyTerms = lazy(() => import('./pages/Terms.jsx'));
const LazyAccommodationDetail = lazy(() => import('./pages/AccommodationDetail.jsx'));
const LazyPackageDetail = lazy(() => import('./pages/PackageDetail.jsx'));
const LazyCulturalGuide = lazy(() => import('./pages/CulturalGuide.jsx'));
const LazyQuotes = lazy(() => import('./pages/Quotes.jsx'));
const LazyLeads = lazy(() => import('./pages/Leads.jsx'));
const LazySuperApp = lazy(() => import('./pages/SuperApp.jsx'));
const LazyJourneyTimeline = lazy(() => import('./pages/JourneyTimeline.jsx'));
const LazyMedicalPassport = lazy(() => import('./pages/MedicalPassport.jsx'));
const LazyEmergencyHelp = lazy(() => import('./pages/EmergencyHelp.jsx'));
const LazyTeleConsultation = lazy(() => import('./pages/TeleConsultation.jsx'));
const LazyPatientMonitoringDashboard = lazy(() => import('./pages/doctor/PatientMonitoringDashboard.jsx'));
const LazyPartnerPortal = lazy(() => import('./pages/PartnerPortal.jsx'));
const LazyLegalTransparency = lazy(() => import('./pages/LegalTransparency.jsx'));
const LazyBeforeAfterGallery = lazy(() => import('./pages/BeforeAfterGallery.jsx'));
const LazyMeetCoordinator = lazy(() => import('./pages/MeetCoordinator.jsx'));
const LazyTreatmentGuide = lazy(() => import('./pages/TreatmentGuide.jsx'));
const LazyCaseManagement = lazy(() => import('./pages/CaseManagement.jsx'));
const LazySelfHealingDashboard = lazy(() => import('./pages/admin/SelfHealingDashboard.jsx'));
const LazyHealthWallet = lazy(() => import('./pages/HealthWallet.jsx'));
const LazyClinicalDecisionSupport = lazy(() => import('./pages/doctor/ClinicalDecisionSupport.jsx'));
const LazyTransparencyAuditCenter = lazy(() => import('./pages/admin/TransparencyAuditCenter.jsx'));
const LazySecurityCenter = lazy(() => import('./pages/SecurityCenter.jsx'));
const LazyPatientJourney = lazy(() => import('./pages/PatientJourney.jsx'));
const LazyDigitalTwin = lazy(() => import('./pages/DigitalTwin.jsx'));

// React Query Client with optimized retry settings
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      // Reduce retries for network errors to prevent spam
      retry: (failureCount, error) => {
        // Don't retry on network errors (0 status code)
        if (error?.code === 'NETWORK_ERROR' || error?.statusCode === 0) {
          return false;
        }
        // Retry other errors max 1 time
        return failureCount < 1;
      },
      retryDelay: (attemptIndex) => Math.min(1000 * 2 ** attemptIndex, 30000),
      // Disable refetch on window focus to reduce unnecessary requests
      refetchOnWindowFocus: false,
      // Disable refetch on reconnect to prevent spam
      refetchOnReconnect: false,
      // Stale time to reduce unnecessary refetches
      staleTime: 5 * 60 * 1000, // 5 minutes
    },
    mutations: {
      // Don't retry mutations on network errors
      retry: (failureCount, error) => {
        if (error?.code === 'NETWORK_ERROR' || error?.statusCode === 0) {
          return false;
        }
        return failureCount < 1;
      },
    },
  },
});

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
    '/super-app': LazySuperApp, // AI Health Companion Super-App
};

// PlaceholderPage artık kullanılmıyor, tüm sayfalar tamamlandı

// Inner App Component - i18n ready kontrolü için
function AppContent() {
    // Hook'ları her zaman çağırmalıyız - React kuralları
    const { ready: translationReady } = useTranslation();
    const [mode, setMode] = useState(() => {
        return localStorage.getItem('theme') || 'light';
    });
    const [i18nReady, setI18nReady] = useState(i18n.isInitialized);

    useEffect(() => {
        // Tema değişikliklerini dinle
        const handleStorageChange = () => {
            const newMode = localStorage.getItem('theme') || 'light';
            setMode(newMode);
        };
        window.addEventListener('storage', handleStorageChange);
        return () => window.removeEventListener('storage', handleStorageChange);
    }, []);

    useEffect(() => {
        // i18n durumunu kontrol et
        const checkI18nReady = () => {
            if (i18n.isInitialized) {
                setI18nReady(true);
                return;
            }
            
            // i18n initialized event'ini dinle
            const handleInitialized = () => {
                console.log('i18n initialized event received');
                setI18nReady(true);
            };
            
            i18n.on('initialized', handleInitialized);
            
            // Timeout ile fallback - 500ms sonra hazır kabul et (daha hızlı)
            const timeout = setTimeout(() => {
                console.warn('i18n initialization timeout, continuing anyway');
                if (!i18n.isInitialized) {
                    i18n.isInitialized = true;
                }
                setI18nReady(true);
            }, 500); // Reduced from 1500ms to 500ms
            
            return () => {
                i18n.off('initialized', handleInitialized);
                clearTimeout(timeout);
            };
        };
        
        checkI18nReady();
    }, []);

    const theme = useMemo(() => getTheme(mode), [mode]);

    // i18n henüz hazır değilse loading göster (max 500ms)
    // Ama i18n.isInitialized true ise devam et
    // Also check if i18n has resources loaded (more reliable check)
    const hasI18nResources = i18n.hasResourceBundle(i18n.language || 'tr', 'translation');
    if (!i18n.isInitialized && !i18nReady && !translationReady && !hasI18nResources) {
        return (
            <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh' }}>
                <CircularProgress />
            </Box>
        );
    }

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
                <WagmiProvider config={wagmiConfig}>
                    <RainbowKitProvider>
                        <ThemeProvider theme={theme}>
                            <CssBaseline />
                            <Router>
                    <Box sx={{ 
                      display: 'flex', 
                      flexDirection: 'column', 
                      minHeight: '100vh',
                      height: 'auto',
                      overflowY: 'visible',
                      overflowX: 'hidden',
                      position: 'relative'
                    }}>

                        <Header />

                        <Box component="main" sx={{ 
                          flexGrow: 1, 
                          overflowY: 'visible',
                          overflowX: 'hidden', 
                          width: '100%',
                          minHeight: 'calc(100vh - 72px)',
                          position: 'relative'
                        }}>
                            {/* Breadcrumb removed - user requested removal */}
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
                                    <Route path="/quotes" element={renderSuspense(LazyQuotes)} />
                                    <Route path="/admin/leads" element={renderSuspense(LazyLeads)} />
                                    
                                    {/* Super-App: AI Health Companion Centered */}
                                    <Route path="/super-app" element={renderSuspense(LazySuperApp)} />
                                    
                                    {/* Patient Journey Pages */}
                                    <Route path="/journey-timeline" element={renderSuspense(LazyJourneyTimeline)} />
                                    <Route path="/patient-journey" element={renderSuspense(LazyPatientJourney)} />
                                    <Route path="/medical-passport" element={renderSuspense(LazyMedicalPassport)} />
                                    <Route path="/emergency-help" element={renderSuspense(LazyEmergencyHelp)} />
                                    <Route path="/health-wallet" element={renderSuspense(LazyHealthWallet)} />
                                    <Route path="/digital-twin" element={renderSuspense(LazyDigitalTwin)} />
                                    <Route path="/security-center" element={renderSuspense(LazySecurityCenter)} />
                                    
                                    {/* Doctor Pages */}
                                    <Route path="/doctor/patients" element={renderSuspense(LazyPatientMonitoringDashboard)} />
                                    <Route path="/doctor/clinical-decision-support" element={renderSuspense(LazyClinicalDecisionSupport)} />
                                    <Route path="/clinical-decision" element={renderSuspense(LazyClinicalDecisionSupport)} />
                                    
                                    {/* Partner & Legal Pages */}
                                    <Route path="/partner-portal" element={renderSuspense(LazyPartnerPortal)} />
                                    <Route path="/legal-transparency" element={renderSuspense(LazyLegalTransparency)} />
                                    
                                    {/* Admin Pages */}
                                    <Route path="/admin/transparency-audit" element={renderSuspense(LazyTransparencyAuditCenter)} />
                                    <Route path="/audit-logs" element={renderSuspense(LazyTransparencyAuditCenter)} />
                                    
                                    {/* Strategic Pages */}
                                    <Route path="/before-after" element={renderSuspense(LazyBeforeAfterGallery)} />
                                    <Route path="/meet-coordinator" element={renderSuspense(LazyMeetCoordinator)} />
                                    <Route path="/treatment-guide" element={renderSuspense(LazyTreatmentGuide)} />
                                    <Route path="/case-management" element={renderSuspense(LazyCaseManagement)} />
                                    
                                    {/* New Features */}
                                    <Route path="/video-consultation" element={renderSuspense(LazyVideoConsultation)} />
                                    <Route path="/tele-consultation/:patientId?" element={renderSuspense(LazyTeleConsultation)} />
                                    <Route path="/travel-planner" element={renderSuspense(LazyTravelPlanner)} />
                                    <Route path="/forum" element={renderSuspense(LazyForum)} />
                                    <Route path="/security/2fa" element={renderSuspense(LazyTwoFactorAuth)} />
                                    <Route path="/analytics" element={renderSuspense(LazyAnalytics)} />
                                    <Route path="/health-records" element={renderSuspense(LazyHealthRecords)} />
                                    <Route path="/medication-reminder" element={renderSuspense(LazyMedicationReminder)} />
                                    <Route path="/referral-program" element={renderSuspense(LazyReferralProgram)} />
                                    <Route path="/local-guide" element={renderSuspense(LazyLocalGuide)} />
                                    <Route path="/coupons" element={renderSuspense(LazyCoupons)} />
                                    <Route path="/payment-history" element={renderSuspense(LazyPaymentHistory)} />
                                    <Route path="/installment-plans" element={renderSuspense(LazyInstallmentPlans)} />
                                    <Route path="/crypto-payment" element={renderSuspense(LazyCryptoPayment)} />
                                    <Route path="/smart-calendar" element={renderSuspense(LazySmartCalendar)} />
                                    <Route path="/bulk-reservation" element={renderSuspense(LazyBulkReservation)} />
                                    <Route path="/waiting-list" element={renderSuspense(LazyWaitingList)} />
                                    <Route path="/security/biometric" element={renderSuspense(LazyBiometricAuth)} />
                                    <Route path="/security/alerts" element={renderSuspense(LazySecurityAlerts)} />
                                    <Route path="/ai-recommendations" element={renderSuspense(LazyAIRecommendations)} />
                                    <Route path="/loyalty-program" element={renderSuspense(LazyLoyaltyProgram)} />
                                    <Route path="/hospitals-map" element={renderSuspense(LazyHospitalsWithMap)} />
                                    <Route path="/realtime-notifications" element={renderSuspense(LazyRealTimeNotifications)} />
                                    
                                    {/* Admin Routes */}
                                    <Route path="/admin/users" element={renderSuspense(LazyUserManagement)} />
                                    <Route path="/admin/reports" element={renderSuspense(LazyReports)} />
                                    <Route path="/admin/self-healing" element={renderSuspense(LazySelfHealingDashboard)} />
                                    
                                    {/* User Routes */}
                                    <Route path="/invoices" element={renderSuspense(LazyInvoices)} />
                                    <Route path="/gdpr/data-export" element={renderSuspense(LazyGDPRDataExport)} />
                                    
                                    {/* Additional Admin Routes */}
                                    <Route path="/admin/content" element={renderSuspense(LazyContentManagement)} />
                                    <Route path="/admin/reservations" element={renderSuspense(LazyReservationManagement)} />
                                    <Route path="/admin/financial" element={renderSuspense(LazyFinancialManagement)} />
                                    <Route path="/admin/settings" element={renderSuspense(LazySystemSettings)} />
                                    <Route path="/admin/audit-log" element={renderSuspense(LazyAuditLog)} />
                                    <Route path="/admin/rate-limiting" element={renderSuspense(LazyRateLimiting)} />
                                    <Route path="/admin/moderation" element={renderSuspense(LazyContentModeration)} />
                                    
                                    {/* User Features */}
                                    <Route path="/notification-preferences" element={renderSuspense(LazyNotificationPreferences)} />
                                    <Route path="/advanced-search" element={renderSuspense(LazyAdvancedSearch)} />
                                    <Route path="/currency-converter" element={renderSuspense(LazyCurrencyConverter)} />
                                    <Route path="/tax-calculator" element={renderSuspense(LazyTaxCalculator)} />
                                    <Route path="/seo-optimization" element={renderSuspense(LazySEOOptimization)} />
                                    <Route path="/cultural-guide" element={renderSuspense(LazyCulturalGuide)} />
                                    
                                    {/* Reservation Details */}
                                    <Route path="/reservations/:id" element={renderSuspense(LazyReservationDetail)} />
                                    
                                    {/* Admin Routes */}
                                    <Route path="/admin/login" element={renderSuspense(LazyAdminLogin)} />
                                    <Route path="/admin/dashboard" element={renderSuspense(LazyAdminDashboard)} />
                                    
                                    {/* Payment Routes */}
                                    <Route path="/payment/success" element={renderSuspense(LazyPaymentSuccess)} />
                                    <Route path="/payment/failed" element={renderSuspense(LazyPaymentFailed)} />
                                    
                                    {/* Footer ve Detay Rotaları */}
                                    <Route path="/about" element={renderSuspense(LazyAboutUs)} />
                                    <Route path="/why-us" element={renderSuspense(LazyWhyUs)} />
                                    <Route path="/privacy" element={renderSuspense(LazyPrivacy)} />
                                    <Route path="/terms" element={renderSuspense(LazyTerms)} />

                                    {/* Detail Routes with Slug */}
                                    <Route path="/hospitals/:slug" element={renderSuspense(LazyHospitalDetail)} />
                                    <Route path="/doctors/:slug" element={renderSuspense(LazyDoctorDetail)} />
                                    <Route path="/accommodations/:slug" element={renderSuspense(LazyAccommodationDetail)} />
                                    <Route path="/packages/:slug" element={renderSuspense(LazyPackageDetail)} />

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
                        {/* Chatbot removed - duplicate chat button */}
                        <Tour />
                        <OfflineIndicator />
                        <CookieConsent />
                        <AccessibilityMenu />
                        <SmartConcierge />
                    </Router>
                        </ThemeProvider>
                    </RainbowKitProvider>
                </WagmiProvider>
            </Provider>
        </QueryClientProvider>
    );
}

function App() {
    return <AppContent />;
}

export default App;