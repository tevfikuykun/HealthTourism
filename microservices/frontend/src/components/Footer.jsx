import React, { useMemo, useState } from 'react';
import {
  Box, Typography, Container, Grid, Link,
  Divider, IconButton, useTheme, Chip,
  TextField, Button, InputAdornment,
} from '@mui/material';
import { Send, CheckCircle } from '@mui/icons-material';
import { toast } from 'react-toastify';
import {
  Facebook, Twitter, Instagram, LinkedIn,
  Email, Phone, LocationOn, Verified,
} from '@mui/icons-material';
import { Mail, Phone as PhoneIcon, MapPin, Shield, Heart, Sparkles } from 'lucide-react';
import { motion } from 'framer-motion';
import { Link as RouterLink } from 'react-router-dom';
import { useTranslation } from '../i18n';
import { fadeInUp, staggerContainer, staggerItem } from '../utils/ui-helpers';

/**
 * Modern Footer Component
 * Material-UI + Tailwind CSS + Framer Motion + Lucide-React entegrasyonu
 */
function Footer() {
  const { t } = useTranslation();
  const theme = useTheme();
  const [newsletterEmail, setNewsletterEmail] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isSubmitted, setIsSubmitted] = useState(false);

  const handleNewsletterSubmit = async (e) => {
    e.preventDefault();
    if (!newsletterEmail || !newsletterEmail.includes('@')) {
      toast.error(t('footer.invalidEmail', 'Lütfen geçerli bir e-posta adresi girin'));
      return;
    }

    setIsSubmitting(true);
    try {
      // TODO: Backend API'ye newsletter kaydı gönder
      // await api.post('/newsletter/subscribe', { email: newsletterEmail });
      
      // Şimdilik local storage'a kaydet veya mock response
      const existingSubscriptions = JSON.parse(localStorage.getItem('newsletter_subscriptions') || '[]');
      if (!existingSubscriptions.includes(newsletterEmail)) {
        existingSubscriptions.push(newsletterEmail);
        localStorage.setItem('newsletter_subscriptions', JSON.stringify(existingSubscriptions));
      }
      
      toast.success(t('footer.newsletterSuccess', 'Bülten kaydınız başarıyla oluşturuldu!'));
      setIsSubmitted(true);
      setNewsletterEmail('');
      
      setTimeout(() => {
        setIsSubmitted(false);
      }, 3000);
    } catch (error) {
      toast.error(t('footer.newsletterError', 'Bir hata oluştu. Lütfen tekrar deneyin.'));
    } finally {
      setIsSubmitting(false);
    }
  };
  
  const companyLinks = useMemo(() => [
    { title: t('about', 'Hakkımızda'), to: '/about' },
    { title: t('whyUs', 'Neden Biz'), to: '/why-us' },
    { title: t('privacyPolicy', 'Gizlilik Politikası'), to: '/privacy' },
    { title: t('termsOfService', 'Kullanım Koşulları'), to: '/terms' },
  ], [t]);
  
  const serviceLinks = useMemo(() => [
    { title: t('hospitals', 'Hastaneler'), to: '/hospitals' },
    { title: t('expertDoctors', 'Uzman Doktorlar'), to: '/doctors' },
    { title: t('packages', 'Paketler'), to: '/packages' },
    { title: t('requestReservation', 'Rezervasyon Talebi'), to: '/reservations' },
  ], [t]);

  const socialLinks = [
    { icon: Facebook, label: 'Facebook', href: 'https://facebook.com', color: '#1877F2' },
    { icon: Twitter, label: 'Twitter', href: 'https://twitter.com', color: '#1DA1F2' },
    { icon: Instagram, label: 'Instagram', href: 'https://instagram.com', color: '#E4405F' },
    { icon: LinkedIn, label: 'LinkedIn', href: 'https://linkedin.com', color: '#0077B5' },
  ];

  return (
    <Box
      component="footer"
      className="bg-slate-900 text-white relative overflow-hidden"
      sx={{
        bgcolor: '#0f172a',
        color: 'white',
        py: { xs: 6, md: 8 },
        position: 'relative',
        overflow: 'hidden',
        borderTop: '4px solid',
        borderColor: 'primary.main',
      }}
    >
      {/* Background Pattern */}
      <Box
        sx={{
          position: 'absolute',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          opacity: 0.05,
          backgroundImage: 'radial-gradient(circle at 2px 2px, white 1px, transparent 0)',
          backgroundSize: '40px 40px',
        }}
      />

      <Container maxWidth="lg" sx={{ position: 'relative', zIndex: 1 }}>
        <motion.div
          variants={staggerContainer}
          initial="hidden"
          whileInView="show"
          viewport={{ once: true }}
        >
          <Grid container spacing={4}>
            {/* Kolon 1: Şirket Logosu ve İletişim - Material-UI + Tailwind + Framer Motion */}
            <Grid item xs={12} md={4}>
              <motion.div variants={staggerItem}>
                <Box className="mb-6" sx={{ mb: 3 }}>
                  <Typography 
                    variant="h5" 
                    className="font-black text-indigo-400 mb-3"
                    sx={{ 
                      fontWeight: 900, 
                      mb: 2, 
                      color: '#818cf8',
                      fontSize: { xs: '1.5rem', md: '1.75rem' },
                    }}
                  >
                    HEALTH<span className="text-white" style={{ color: 'white' }}>CHAIN</span>
                  </Typography>
                  <Typography 
                    variant="body2" 
                    className="text-slate-300 leading-relaxed"
                    sx={{ 
                      mb: 2, 
                      color: '#cbd5e1',
                      lineHeight: 1.7,
                    }}
                  >
                    {t('footer.description', 'Sağlık turizminde global çözüm ortağınız. Tedavi, konaklama ve transfer dahil tüm süreçlerinizde yanınızdayız.')}
                  </Typography>
                  
                  {/* Trust Badges */}
                  <Box className="flex flex-wrap gap-2 mt-4" sx={{ display: 'flex', flexWrap: 'wrap', gap: 1, mt: 2 }}>
                    <Chip
                      icon={<Shield size={16} strokeWidth={2} style={{ marginLeft: 4 }} />}
                      label="Blockchain Verified"
                      size="small"
                      className="bg-indigo-500/20 text-indigo-300 border border-indigo-400/30 rounded-xl font-bold"
                      sx={{
                        bgcolor: 'rgba(99, 102, 241, 0.2)',
                        color: '#c7d2fe',
                        border: '1px solid rgba(99, 102, 241, 0.3)',
                        borderRadius: 3,
                        fontWeight: 700,
                        fontSize: '0.7rem',
                      }}
                    />
                    <Chip
                      icon={<Heart size={16} strokeWidth={2} style={{ marginLeft: 4 }} />}
                      label="ISO Certified"
                      size="small"
                      className="bg-green-500/20 text-green-300 border border-green-400/30 rounded-xl font-bold"
                      sx={{
                        bgcolor: 'rgba(34, 197, 94, 0.2)',
                        color: '#86efac',
                        border: '1px solid rgba(34, 197, 94, 0.3)',
                        borderRadius: 3,
                        fontWeight: 700,
                        fontSize: '0.7rem',
                      }}
                    />
                  </Box>
                </Box>

                {/* Contact Information - Lucide-React Icons */}
                <Box className="space-y-3" sx={{ '& > *': { mb: 1.5 } }}>
                  <motion.div
                    variants={staggerItem}
                    whileHover={{ x: 5 }}
                    className="flex items-center gap-3 text-slate-300 hover:text-indigo-400 transition-colors cursor-pointer"
                    sx={{ display: 'flex', alignItems: 'center', gap: 1.5, color: '#cbd5e1', cursor: 'pointer' }}
                  >
                    <Box className="p-2 bg-indigo-500/20 rounded-xl" sx={{ p: 1, bgcolor: 'rgba(99, 102, 241, 0.2)', borderRadius: 3 }}>
                      <Mail size={18} strokeWidth={2} style={{ color: '#818cf8' }} />
                    </Box>
                    <Link 
                      href="mailto:info@healthturizm.com" 
                      className="text-slate-300 hover:text-indigo-400 no-underline font-medium"
                      sx={{ 
                        color: '#cbd5e1',
                        textDecoration: 'none',
                        fontWeight: 500,
                        '&:hover': { color: '#818cf8' }
                      }}
                    >
                      info@healthturizm.com
                    </Link>
                  </motion.div>

                  <motion.div
                    variants={staggerItem}
                    whileHover={{ x: 5 }}
                    className="flex items-center gap-3 text-slate-300 hover:text-indigo-400 transition-colors cursor-pointer"
                    sx={{ display: 'flex', alignItems: 'center', gap: 1.5, color: '#cbd5e1', cursor: 'pointer' }}
                  >
                    <Box className="p-2 bg-indigo-500/20 rounded-xl" sx={{ p: 1, bgcolor: 'rgba(99, 102, 241, 0.2)', borderRadius: 3 }}>
                      <PhoneIcon size={18} strokeWidth={2} style={{ color: '#818cf8' }} />
                    </Box>
                    <Typography 
                      variant="body2" 
                      className="font-medium"
                      sx={{ fontWeight: 500 }}
                    >
                      +90 555 123 45 67 (WhatsApp)
                    </Typography>
                  </motion.div>

                  <motion.div
                    variants={staggerItem}
                    whileHover={{ x: 5 }}
                    className="flex items-center gap-3 text-slate-300 hover:text-indigo-400 transition-colors"
                    sx={{ display: 'flex', alignItems: 'center', gap: 1.5, color: '#cbd5e1' }}
                  >
                    <Box className="p-2 bg-indigo-500/20 rounded-xl" sx={{ p: 1, bgcolor: 'rgba(99, 102, 241, 0.2)', borderRadius: 3 }}>
                      <MapPin size={18} strokeWidth={2} style={{ color: '#818cf8' }} />
                    </Box>
                    <Typography 
                      variant="body2" 
                      className="font-medium"
                      sx={{ fontWeight: 500 }}
                    >
                      İstanbul, Türkiye
                    </Typography>
                  </motion.div>
                </Box>
              </motion.div>
            </Grid>

            {/* Kolon 2: Kurumsal Linkler - Material-UI + Tailwind + Framer Motion */}
            <Grid item xs={6} sm={4} md={2}>
              <motion.div variants={staggerItem}>
                <Typography 
                  variant="h6" 
                  className="font-bold mb-4 text-indigo-400"
                  sx={{ 
                    fontWeight: 700, 
                    mb: 2, 
                    color: '#818cf8',
                    fontSize: '1rem',
                  }}
                >
                  {t('company', 'Şirket')}
                </Typography>
                <Box component="ul" className="space-y-2" sx={{ listStyle: 'none', p: 0, '& > *': { mb: 1 } }}>
                  {companyLinks.map((link, index) => (
                    <motion.li
                      key={link.title}
                      variants={staggerItem}
                      whileHover={{ x: 5 }}
                    >
                      <Link 
                        component={RouterLink} 
                        to={link.to} 
                        className="text-slate-300 hover:text-indigo-400 no-underline font-medium transition-colors"
                        sx={{ 
                          color: '#cbd5e1',
                          textDecoration: 'none',
                          fontWeight: 500,
                          display: 'block',
                          transition: 'color 0.2s ease',
                          '&:hover': { 
                            color: '#818cf8',
                          }
                        }}
                      >
                        {link.title}
                      </Link>
                    </motion.li>
                  ))}
                </Box>
              </motion.div>
            </Grid>

            {/* Kolon 3: Hizmetlerimiz - Material-UI + Tailwind + Framer Motion */}
            <Grid item xs={6} sm={4} md={3}>
              <motion.div variants={staggerItem}>
                <Typography 
                  variant="h6" 
                  className="font-bold mb-4 text-indigo-400"
                  sx={{ 
                    fontWeight: 700, 
                    mb: 2, 
                    color: '#818cf8',
                    fontSize: '1rem',
                  }}
                >
                  {t('services', 'Hizmetler')}
                </Typography>
                <Box component="ul" className="space-y-2" sx={{ listStyle: 'none', p: 0, '& > *': { mb: 1 } }}>
                  {serviceLinks.map((link, index) => (
                    <motion.li
                      key={link.title}
                      variants={staggerItem}
                      whileHover={{ x: 5 }}
                    >
                      <Link 
                        component={RouterLink} 
                        to={link.to} 
                        className="text-slate-300 hover:text-indigo-400 no-underline font-medium transition-colors"
                        sx={{ 
                          color: '#cbd5e1',
                          textDecoration: 'none',
                          fontWeight: 500,
                          display: 'block',
                          transition: 'color 0.2s ease',
                          '&:hover': { 
                            color: '#818cf8',
                          }
                        }}
                      >
                        {link.title}
                      </Link>
                    </motion.li>
                  ))}
                </Box>
              </motion.div>
            </Grid>

            {/* Kolon 4: Sosyal Medya - Material-UI + Tailwind + Framer Motion + Lucide-React */}
            <Grid item xs={12} sm={4} md={3} sx={{ width: '100%', maxWidth: '100%', boxSizing: 'border-box' }}>
              <motion.div variants={staggerItem} style={{ width: '100%', maxWidth: '100%' }}>
                <Typography 
                  variant="h6" 
                  className="font-bold mb-4 text-indigo-400"
                  sx={{ 
                    fontWeight: 700, 
                    mb: 2, 
                    color: '#818cf8',
                    fontSize: '1rem',
                  }}
                >
                  {t('followUs', 'Bizi Takip Edin')}
                </Typography>
                <Box className="flex gap-3 mb-6" sx={{ display: 'flex', gap: 1.5, mb: 4 }}>
                  {socialLinks.map((social, index) => (
                    <motion.div
                      key={social.label}
                      variants={staggerItem}
                      whileHover={{ scale: 1.1, y: -2 }}
                      whileTap={{ scale: 0.95 }}
                    >
                      <IconButton
                        href={social.href}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="bg-slate-800 hover:bg-indigo-600 rounded-xl transition-all"
                        sx={{
                          bgcolor: '#1e293b',
                          color: 'white',
                          borderRadius: 3,
                          transition: 'all 0.2s ease',
                          '&:hover': {
                            bgcolor: '#4f46e5',
                            transform: 'translateY(-2px)',
                            boxShadow: '0 10px 20px rgba(79, 70, 229, 0.3)',
                          }
                        }}
                      >
                        <social.icon />
                      </IconButton>
                    </motion.div>
                  ))}
                </Box>

                {/* Newsletter Subscription */}
                <Box
                  sx={{
                    mt: 4,
                    p: 2.5,
                    bgcolor: 'rgba(30, 41, 59, 0.5)',
                    borderRadius: 3,
                    border: '1px solid #334155',
                    width: '100%',
                    maxWidth: '100%',
                    boxSizing: 'border-box',
                  }}
                >
                  <Typography
                    variant="subtitle2"
                    sx={{
                      fontWeight: 700,
                      mb: 1,
                      color: 'white',
                      fontSize: '0.875rem',
                    }}
                  >
                    {t('footer.newsletter', 'Bülten')}
                  </Typography>
                  <Typography
                    variant="caption"
                    sx={{
                      color: '#94a3b8',
                      fontSize: '0.75rem',
                      mb: 2,
                      display: 'block',
                      lineHeight: 1.4,
                    }}
                  >
                    {t('footer.newsletterDesc', 'Yeni hizmetler ve kampanyalardan haberdar olun')}
                  </Typography>

                  {isSubmitted ? (
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, color: '#86efac' }}>
                      <CheckCircle fontSize="small" />
                      <Typography variant="caption" sx={{ color: '#86efac', fontSize: '0.75rem' }}>
                        {t('footer.subscribed', 'Kayıt başarılı!')}
                      </Typography>
                    </Box>
                  ) : (
                    <Box component="form" onSubmit={handleNewsletterSubmit} sx={{ width: '100%' }}>
                      <TextField
                        fullWidth
                        size="small"
                        type="email"
                        placeholder={t('footer.emailPlaceholder', 'E-posta adresiniz')}
                        value={newsletterEmail}
                        onChange={(e) => setNewsletterEmail(e.target.value)}
                        disabled={isSubmitting}
                        InputProps={{
                          endAdornment: (
                            <InputAdornment position="end">
                              <Button
                                type="submit"
                                size="small"
                                variant="contained"
                                disabled={isSubmitting || !newsletterEmail}
                                sx={{
                                  minWidth: '36px',
                                  width: '36px',
                                  height: '36px',
                                  p: 0,
                                  bgcolor: 'primary.main',
                                  borderRadius: '8px',
                                  '&:hover': {
                                    bgcolor: 'primary.dark',
                                  },
                                  '&:disabled': {
                                    bgcolor: 'rgba(255, 255, 255, 0.1)',
                                  },
                                }}
                              >
                                <Send sx={{ fontSize: '18px' }} />
                              </Button>
                            </InputAdornment>
                          ),
                        }}
                        sx={{
                          width: '100%',
                          '& .MuiOutlinedInput-root': {
                            bgcolor: 'rgba(15, 23, 42, 0.5)',
                            color: 'white',
                            fontSize: '0.875rem',
                            '& fieldset': {
                              borderColor: '#334155',
                            },
                            '&:hover fieldset': {
                              borderColor: '#475569',
                            },
                            '&.Mui-focused fieldset': {
                              borderColor: '#818cf8',
                            },
                            '& input': {
                              py: 1,
                              fontSize: '0.875rem',
                              '&::placeholder': {
                                color: '#64748b',
                                opacity: 1,
                                fontSize: '0.875rem',
                              },
                            },
                          },
                        }}
                      />
                    </Box>
                  )}
                </Box>
              </motion.div>
            </Grid>
          </Grid>
        </motion.div>

        {/* Telif Hakkı ve Alt Bar - Material-UI + Tailwind + Framer Motion */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          transition={{ delay: 0.3 }}
        >
          <Divider 
            className="my-8 border-slate-700" 
            sx={{ 
              mt: 4, 
              mb: 3, 
              bgcolor: '#334155',
            }} 
          />
          <Box className="flex flex-col md:flex-row justify-between items-center gap-4" sx={{ display: 'flex', flexDirection: { xs: 'column', md: 'row' }, justifyContent: 'space-between', alignItems: 'center', gap: 2 }}>
            <Typography 
              variant="body2" 
              className="text-slate-400 text-center md:text-left"
              sx={{ 
                color: '#94a3b8',
                textAlign: { xs: 'center', md: 'left' },
                fontSize: '0.875rem',
              }}
            >
              © {new Date().getFullYear()} Health Turizm. {t('allRightsReserved', 'Tüm hakları saklıdır')}.
            </Typography>
            
            <Box className="flex items-center gap-4" sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
              <Link 
                component={RouterLink} 
                to="/privacy" 
                className="text-slate-400 hover:text-indigo-400 text-sm font-medium transition-colors"
                sx={{ 
                  color: '#94a3b8',
                  textDecoration: 'none',
                  fontSize: '0.875rem',
                  fontWeight: 500,
                  '&:hover': { color: '#818cf8' }
                }}
              >
                {t('privacyPolicy', 'Gizlilik')}
              </Link>
              <Link 
                component={RouterLink} 
                to="/terms" 
                className="text-slate-400 hover:text-indigo-400 text-sm font-medium transition-colors"
                sx={{ 
                  color: '#94a3b8',
                  textDecoration: 'none',
                  fontSize: '0.875rem',
                  fontWeight: 500,
                  '&:hover': { color: '#818cf8' }
                }}
              >
                {t('termsOfService', 'Koşullar')}
              </Link>
              <Chip
                icon={<Sparkles size={14} strokeWidth={2} style={{ marginLeft: 4 }} />}
                label="v2.5"
                size="small"
                className="bg-indigo-500/20 text-indigo-300 border border-indigo-400/30 rounded-xl font-bold"
                sx={{
                  bgcolor: 'rgba(99, 102, 241, 0.2)',
                  color: '#c7d2fe',
                  border: '1px solid rgba(99, 102, 241, 0.3)',
                  borderRadius: 3,
                  fontWeight: 700,
                  fontSize: '0.7rem',
                }}
              />
            </Box>
          </Box>
        </motion.div>
      </Container>
    </Box>
  );
}

export default Footer;
