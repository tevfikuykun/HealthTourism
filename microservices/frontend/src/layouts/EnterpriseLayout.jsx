import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Box, Container } from '@mui/material';
import { useAuth } from '../hooks/useAuth';
import { useNavigate } from 'react-router-dom';
import Navigation from '../components/layout/Navigation';
import UserMenu from '../components/layout/UserMenu';
import NotificationDrawer from '../components/layout/NotificationDrawer';
import Footer from '../components/Footer';
import { Cpu } from 'lucide-react';

/**
 * EnterpriseLayout
 * Projenin kurumsal kimliğini ve navigasyon akışını yöneten ana çerçeve.
 * Glassmorphism sticky header, mega menu, notification center ve responsive footer içerir.
 */
const EnterpriseLayout = ({ children }) => {
  const [isScrolled, setIsScrolled] = useState(false);
  const { user, isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();

  // Header animasyonu için scroll takibi
  useEffect(() => {
    const handleScroll = () => {
      setIsScrolled(window.scrollY > 50);
    };
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  return (
    <Box className="min-h-screen flex flex-col bg-slate-50" sx={{ minHeight: '100vh', display: 'flex', flexDirection: 'column', bgcolor: '#f8fafc' }}>
      {/* --- HEADER (Glassmorphism Sticky Header) --- */}
      <header 
        className={`fixed top-0 w-full z-50 transition-all duration-300 ${
          isScrolled 
            ? 'bg-white/80 backdrop-blur-md shadow-sm py-2' 
            : 'bg-transparent py-4'
        }`}
        style={{
          position: 'fixed',
          top: 0,
          width: '100%',
          zIndex: 1300,
          transition: 'all 0.3s ease',
          backgroundColor: isScrolled ? 'rgba(255, 255, 255, 0.8)' : 'transparent',
          backdropFilter: isScrolled ? 'blur(12px)' : 'none',
          WebkitBackdropFilter: isScrolled ? 'blur(12px)' : 'none',
          paddingTop: isScrolled ? '8px' : '16px',
          paddingBottom: isScrolled ? '8px' : '16px',
          boxShadow: isScrolled ? '0 1px 3px rgba(0, 0, 0, 0.1)' : 'none',
        }}
      >
        <Container maxWidth="xl" sx={{ px: { xs: 2, md: 4 } }}>
          <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            {/* Logo */}
            <motion.div
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              whileHover={{ scale: 1.02 }}
              onClick={() => navigate('/')}
              style={{ cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '12px' }}
            >
              <Box
                sx={{
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  bgcolor: 'primary.main',
                  color: 'white',
                  p: 1,
                  borderRadius: 2,
                  boxShadow: '0 4px 12px rgba(79, 70, 229, 0.3)',
                }}
              >
                <Cpu size={24} strokeWidth={2.5} />
              </Box>
              <Box
                sx={{
                  fontWeight: 900,
                  fontSize: { xs: '1.25rem', md: '1.5rem' },
                  background: 'linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%)',
                  WebkitBackgroundClip: 'text',
                  WebkitTextFillColor: 'transparent',
                  backgroundClip: 'text',
                }}
              >
                HEALTH<span style={{ color: '#4f46e5' }}>CHAIN</span>
              </Box>
            </motion.div>

            {/* Mega Menu & Navigasyon */}
            <Navigation />

            {/* Action Center */}
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
              <NotificationDrawer />
              {isAuthenticated && user ? (
                <UserMenu user={user} onLogout={logout} />
              ) : (
                <motion.button
                  whileHover={{ scale: 1.05 }}
                  whileTap={{ scale: 0.95 }}
                  onClick={() => navigate('/login')}
                  style={{
                    backgroundColor: '#4f46e5',
                    color: 'white',
                    padding: '10px 20px',
                    borderRadius: '24px',
                    fontWeight: 600,
                    border: 'none',
                    cursor: 'pointer',
                    fontSize: '0.875rem',
                    transition: 'all 0.2s ease',
                  }}
                  onMouseEnter={(e) => {
                    e.currentTarget.style.backgroundColor = '#4338ca';
                  }}
                  onMouseLeave={(e) => {
                    e.currentTarget.style.backgroundColor = '#4f46e5';
                  }}
                >
                  Sign In
                </motion.button>
              )}
            </Box>
          </Box>
        </Container>
      </header>

      {/* --- MAIN CONTENT (Sayfa İçeriği) --- */}
      <Box 
        component="main" 
        className="flex-grow pt-24 pb-12"
        sx={{ 
          flexGrow: 1, 
          paddingTop: { xs: '80px', md: '96px' },
          paddingBottom: '48px',
        }}
      >
        <AnimatePresence mode="wait">
          <motion.div
            key={window.location.pathname}
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -10 }}
            transition={{ duration: 0.4 }}
          >
            {children}
          </motion.div>
        </AnimatePresence>
      </Box>

      {/* --- FOOTER (Kurumsal Güven) --- */}
      <Footer />
    </Box>
  );
};

export default EnterpriseLayout;


