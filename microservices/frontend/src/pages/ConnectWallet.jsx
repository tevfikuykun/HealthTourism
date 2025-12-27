import React, { useEffect } from 'react';
import { Box, Container, Typography, Paper, Button } from '@mui/material';
import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from '../i18n';
import { ConnectButton } from '@rainbow-me/rainbowkit';
import { useAccount } from 'wagmi';
import { Wallet, ArrowLeft } from 'lucide-react';
import { fadeInUp } from '../utils/ui-helpers';

const ConnectWallet = () => {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const { isConnected, address } = useAccount();

  // If wallet is already connected, redirect to health wallet page
  useEffect(() => {
    if (isConnected && address) {
      // Small delay to show success state
      const timer = setTimeout(() => {
        navigate('/health-wallet');
      }, 1000);
      return () => clearTimeout(timer);
    }
  }, [isConnected, address, navigate]);

  return (
    <Box
      sx={{
        minHeight: '100vh',
        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        py: 4,
        position: 'relative',
        overflow: 'hidden',
        '&::before': {
          content: '""',
          position: 'absolute',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          background: 'radial-gradient(circle at 50% 50%, rgba(255, 255, 255, 0.1) 0%, transparent 70%)',
          pointerEvents: 'none',
        },
      }}
    >
      <Container maxWidth="sm" sx={{ position: 'relative', zIndex: 1 }}>
        <motion.div
          variants={fadeInUp}
          initial="initial"
          animate="animate"
        >
          <Paper
            elevation={24}
            sx={{
              p: 5,
              borderRadius: 4,
              backdropFilter: 'blur(20px) saturate(180%)',
              WebkitBackdropFilter: 'blur(20px) saturate(180%)',
              backgroundColor: 'rgba(255, 255, 255, 0.95)',
              boxShadow: '0 20px 60px rgba(0, 0, 0, 0.3)',
            }}
          >
            {/* Back Button */}
            <Button
              startIcon={<ArrowLeft size={20} />}
              onClick={() => navigate(-1)}
              sx={{
                mb: 3,
                color: 'text.secondary',
                '&:hover': {
                  backgroundColor: 'rgba(0, 0, 0, 0.04)',
                },
              }}
            >
              {t('common.back', 'Geri')}
            </Button>

            {/* Header */}
            <Box sx={{ textAlign: 'center', mb: 4 }}>
              <Box
                sx={{
                  display: 'inline-flex',
                  p: 2,
                  borderRadius: '50%',
                  bgcolor: 'primary.main',
                  mb: 3,
                  boxShadow: '0 8px 32px rgba(79, 70, 229, 0.3)',
                }}
              >
                <Wallet size={48} style={{ color: 'white' }} />
              </Box>
              <Typography
                variant="h4"
                sx={{
                  fontWeight: 900,
                  mb: 1,
                  background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                  backgroundClip: 'text',
                  WebkitBackgroundClip: 'text',
                  WebkitTextFillColor: 'transparent',
                }}
              >
                {t('wallet.connectTitle', 'Cüzdanınızı Bağlayın')}
              </Typography>
              <Typography
                variant="body1"
                color="text.secondary"
                sx={{ maxWidth: '80%', mx: 'auto', lineHeight: 1.7 }}
              >
                {t('wallet.connectDescription', 'Blockchain tabanlı sağlık verilerinize erişmek için cüzdanınızı bağlayın.')}
              </Typography>
            </Box>

            {/* Success State */}
            {isConnected && address && (
              <motion.div
                initial={{ opacity: 0, scale: 0.9 }}
                animate={{ opacity: 1, scale: 1 }}
                transition={{ duration: 0.3 }}
              >
                <Box
                  sx={{
                    p: 3,
                    mb: 3,
                    borderRadius: 2,
                    bgcolor: 'success.light',
                    color: 'success.contrastText',
                    textAlign: 'center',
                  }}
                >
                  <Typography variant="h6" sx={{ fontWeight: 700, mb: 1 }}>
                    {t('wallet.connected', 'Cüzdan Bağlandı!')}
                  </Typography>
                  <Typography variant="body2" sx={{ fontFamily: 'monospace', opacity: 0.9 }}>
                    {address.slice(0, 6)}...{address.slice(-4)}
                  </Typography>
                  <Typography variant="body2" sx={{ mt: 2, opacity: 0.8 }}>
                    {t('wallet.redirecting', 'Yönlendiriliyor...')}
                  </Typography>
                </Box>
              </motion.div>
            )}

            {/* Connect Button Container */}
            <Box
              sx={{
                display: 'flex',
                justifyContent: 'center',
                mb: 4,
                '& button': {
                  borderRadius: '12px !important',
                  fontWeight: 700,
                  textTransform: 'none',
                  fontSize: '1rem',
                  px: 4,
                  py: 1.5,
                  minWidth: 280,
                  backdropFilter: 'blur(20px) saturate(180%)',
                  WebkitBackdropFilter: 'blur(20px) saturate(180%)',
                  backgroundColor: 'rgba(79, 70, 229, 0.15) !important',
                  border: '1px solid rgba(79, 70, 229, 0.3) !important',
                  boxShadow: '0 8px 32px rgba(79, 70, 229, 0.2) !important',
                  '&:hover': {
                    backgroundColor: 'rgba(79, 70, 229, 0.25) !important',
                    boxShadow: '0 12px 40px rgba(79, 70, 229, 0.3) !important',
                  },
                },
              }}
            >
              <ConnectButton
                chainStatus="icon"
                showBalance={true}
                accountStatus={{
                  smallScreen: 'avatar',
                  largeScreen: 'full',
                }}
              />
            </Box>

            {/* Info Section */}
            <Box
              sx={{
                p: 3,
                borderRadius: 2,
                bgcolor: 'rgba(79, 70, 229, 0.05)',
                border: '1px solid rgba(79, 70, 229, 0.1)',
              }}
            >
              <Typography
                variant="subtitle2"
                sx={{
                  fontWeight: 700,
                  mb: 1.5,
                  color: 'primary.main',
                }}
              >
                {t('wallet.whyConnect', 'Neden Cüzdan Bağlamalıyım?')}
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 1.5 }}>
                {t('wallet.connectReason1', '• Blockchain tabanlı sağlık verilerinize güvenli erişim')}
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 1.5 }}>
                {t('wallet.connectReason2', '• Sağlık belgelerinizi QR kod ile paylaşabilme')}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                {t('wallet.connectReason3', '• Tedavi geçmişinizin şeffaf ve güvenli saklanması')}
              </Typography>
            </Box>

            {/* Help Text */}
            <Typography
              variant="caption"
              color="text.secondary"
              sx={{
                display: 'block',
                textAlign: 'center',
                mt: 3,
                lineHeight: 1.6,
              }}
            >
              {t('wallet.connectHelp', 'Cüzdanınız yok mu? MetaMask, WalletConnect veya Coinbase Wallet gibi cüzdanlardan birini kurabilirsiniz.')}
            </Typography>
          </Paper>
        </motion.div>
      </Container>
    </Box>
  );
};

export default ConnectWallet;

