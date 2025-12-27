import React, { useState, useEffect } from 'react';
import {
  Box, Container, Typography, Card, CardContent, Grid, Button, Chip,
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
  Paper, LinearProgress, Alert, Tabs, Tab, IconButton, Tooltip,
  Dialog, DialogTitle, DialogContent, DialogActions,
} from '@mui/material';
import {
  AccountBalanceWallet, Payment, Security, Receipt, TrendingUp, TrendingDown,
  Visibility, SettingsInputAntenna, ContentCopy, CheckCircle
} from '@mui/icons-material';
import { ShieldCheck, Wallet } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import { useTranslation } from '../i18n';
import { useQuery } from '@tanstack/react-query';
import { blockchainWalletService, healthWalletService, smartInsuranceService } from '../services/api';
import { useAuth } from '../hooks/useAuth';
import { fadeInUp, staggerContainer, staggerItem, hoverLift, scaleIn } from '../utils/ui-helpers';
// RainbowKit & Wagmi for Web3 Wallet Connection
import { useAccount, useBalance, useChainId, useDisconnect } from 'wagmi';
import { formatEther, isAddress, getAddress } from 'viem';
import { toast } from 'react-toastify';
import { normalizeAddress } from '../utils/walletSecurity';
import { useNavigate } from 'react-router-dom';

const HealthWallet = () => {
  const { t } = useTranslation();
  const { user } = useAuth();
  const navigate = useNavigate();
  const [tabValue, setTabValue] = useState(0);
  const [selectedTransaction, setSelectedTransaction] = useState(null);
  const [openDialog, setOpenDialog] = useState(false);
  const [copiedAddress, setCopiedAddress] = useState(false);

  // RainbowKit & Wagmi Hooks
  const { address, isConnected, connector } = useAccount();
  const { data: balance } = useBalance({
    address: address,
    watch: true,
  });
  const chainId = useChainId();
  const { disconnect } = useDisconnect();

  // Copy wallet address to clipboard (with security: normalize to checksum format)
  const copyAddress = () => {
    if (address && isAddress(address)) {
      try {
        // Security: Normalize to checksum format to prevent phishing
        const normalizedAddress = normalizeAddress(address);
        navigator.clipboard.writeText(normalizedAddress);
        setCopiedAddress(true);
        toast.success(t('wallet.addressCopied', 'Cüzdan adresi kopyalandı'));
        setTimeout(() => setCopiedAddress(false), 2000);
      } catch (error) {
        console.error('Address normalization error:', error);
        toast.error(t('wallet.addressError', 'Adres formatı geçersiz'));
      }
    }
  };

  // --- API Mantığın (Aynen Korundu) ---
  const { data: walletData, isLoading: walletLoading } = useQuery({
    queryKey: ['health-wallet', user?.id],
    queryFn: async () => (await healthWalletService.getCompleteData(user?.id)).data,
    enabled: !!user?.id,
  });

  const { data: blockchainData, isLoading: blockchainLoading } = useQuery({
    queryKey: ['blockchain-wallet', walletData?.blockchainAddress],
    queryFn: async () => {
      if (!walletData?.blockchainAddress) return null;
      const [balance, transactions, tokens, nfts] = await Promise.all([
        blockchainWalletService.getBalance(walletData.blockchainAddress),
        blockchainWalletService.getTransactions(walletData.blockchainAddress, { limit: 10 }),
        blockchainWalletService.getHealthTokens(walletData.blockchainAddress),
        blockchainWalletService.getNFTs(walletData.blockchainAddress),
      ]);
      return { balance: balance.data, transactions: transactions.data, tokens: tokens.data, nfts: nfts.data };
    },
    enabled: !!walletData?.blockchainAddress,
    refetchInterval: 30000,
  });

  const { data: networkStatus } = useQuery({
    queryKey: ['blockchain-network-status'],
    queryFn: async () => (await blockchainWalletService.getNetworkStatus()).data,
    refetchInterval: 60000,
  });

  const { data: insurancePolicies } = useQuery({
    queryKey: ['insurance-policies', user?.id],
    queryFn: async () => (await smartInsuranceService.getPoliciesByUser(user?.id)).data,
    enabled: !!user?.id,
  });

  // --- Yardımcı Fonksiyonlar ---
  const formatCurrency = (amount, currency = 'USD') => 
    new Intl.NumberFormat('en-US', { style: 'currency', currency }).format(amount);

  if (walletLoading || blockchainLoading) {
    return (
      <Box className="w-full mt-8" sx={{ width: '100%', mt: 4 }}>
        <LinearProgress />
      </Box>
    );
  }

  return (
    <Box
      sx={{
        minHeight: '100vh',
        background: 'linear-gradient(135deg, #f8fafc 0%, #e0e7ff 100%)',
        position: 'relative',
        overflowX: 'hidden',
        overflowY: 'auto',
        '&::before': {
          content: '""',
          position: 'absolute',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          background: 'radial-gradient(circle at 20% 50%, rgba(79, 70, 229, 0.1) 0%, transparent 50%), radial-gradient(circle at 80% 80%, rgba(14, 165, 233, 0.1) 0%, transparent 50%)',
          pointerEvents: 'none',
        },
      }}
    >
      <Container maxWidth="xl" sx={{ py: 6, position: 'relative', zIndex: 1 }}>
        {/* Header: FinTech Style with Glassmorphism */}
        <motion.div
          variants={fadeInUp}
          initial="initial"
          animate="animate"
        >
          <Box sx={{ mb: 6, display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: 2 }}>
            <Box>
              <Typography 
                variant="h3" 
                sx={{ 
                  fontWeight: 900, 
                  letterSpacing: '-0.03em', 
                  color: 'text.primary',
                  background: 'linear-gradient(135deg, #4F46E5 0%, #7C3AED 100%)',
                  backgroundClip: 'text',
                  WebkitBackgroundClip: 'text',
                  WebkitTextFillColor: 'transparent',
                  mb: 1,
                }}
              >
                {t('wallet.title', 'Cüzdanım')}
              </Typography>
              <Typography 
                variant="body1" 
                sx={{ color: 'text.secondary', fontWeight: 500, letterSpacing: '-0.01em' }}
              >
                {t('wallet.subtitle', 'Blockchain tabanlı sağlık varlıklarınızı yönetin.')}
              </Typography>
            </Box>
            
            {/* RainbowKit Connect Button & Wallet Info */}
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, flexWrap: 'wrap' }}>
              {/* Network Status - Glassmorphism */}
              {networkStatus && (
                <motion.div
                  initial={{ scale: 0.8, opacity: 0 }}
                  animate={{ scale: 1, opacity: 1 }}
                  transition={{ delay: 0.2 }}
                >
                  <Box
                    sx={{
                      backdropFilter: 'blur(20px) saturate(180%)',
                      WebkitBackdropFilter: 'blur(20px) saturate(180%)',
                      backgroundColor: networkStatus.connected 
                        ? 'rgba(16, 185, 129, 0.15)' 
                        : 'rgba(245, 158, 11, 0.15)',
                      border: `1px solid ${networkStatus.connected ? 'rgba(16, 185, 129, 0.3)' : 'rgba(245, 158, 11, 0.3)'}`,
                      borderRadius: 3,
                      px: 2,
                      py: 1,
                      display: 'flex',
                      alignItems: 'center',
                      gap: 1,
                      boxShadow: '0 8px 32px rgba(0, 0, 0, 0.1)',
                    }}
                  >
                    <SettingsInputAntenna sx={{ fontSize: 18, color: networkStatus.connected ? 'success.main' : 'warning.main' }} />
                    <Typography sx={{ fontWeight: 700, fontSize: '0.875rem', color: networkStatus.connected ? 'success.dark' : 'warning.dark' }}>
                      {networkStatus.network || 'Polygon Mainnet'}
                    </Typography>
                  </Box>
                </motion.div>
              )}

              {/* Connect Wallet Button - Redirects to Connect Wallet Page */}
              {!isConnected ? (
                <Button
                  variant="contained"
                  onClick={() => navigate('/connect-wallet')}
                  sx={{
                    borderRadius: '12px',
                    fontWeight: 700,
                    textTransform: 'none',
                    backdropFilter: 'blur(20px) saturate(180%)',
                    WebkitBackdropFilter: 'blur(20px) saturate(180%)',
                    backgroundColor: '#4F46E5',
                    border: '1px solid #4F46E5',
                    boxShadow: '0 8px 32px rgba(79, 70, 229, 0.4)',
                    color: '#FFFFFF',
                    '&:hover': {
                      backgroundColor: '#4338CA',
                      borderColor: '#4338CA',
                      boxShadow: '0 12px 40px rgba(79, 70, 229, 0.5)',
                      color: '#FFFFFF',
                    },
                  }}
                  startIcon={<Wallet size={20} style={{ color: '#FFFFFF' }} />}
                >
                  {t('wallet.connect', 'Cüzdan Bağla')}
                </Button>
              ) : null}

              {/* Connected Wallet Info */}
              {isConnected && address && (
                <motion.div
                  initial={{ opacity: 0, x: 20 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ delay: 0.3 }}
                >
                  <Card
                    sx={{
                      backdropFilter: 'blur(20px) saturate(180%)',
                      WebkitBackdropFilter: 'blur(20px) saturate(180%)',
                      backgroundColor: 'rgba(255, 255, 255, 0.7)',
                      border: '1px solid rgba(79, 70, 229, 0.3)',
                      borderRadius: 3,
                      px: 2,
                      py: 1.5,
                      boxShadow: '0 8px 32px rgba(0, 0, 0, 0.1)',
                    }}
                  >
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
                      <Wallet size={20} style={{ color: '#4F46E5' }} />
                      <Box>
                        <Typography variant="caption" sx={{ color: 'text.secondary', fontWeight: 600, display: 'block' }}>
                          {connector?.name || 'Wallet'}
                        </Typography>
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                          <Typography variant="body2" sx={{ fontFamily: 'monospace', fontWeight: 600, fontSize: '0.75rem' }}>
                            {address.slice(0, 6)}...{address.slice(-4)}
                          </Typography>
                          <Tooltip title={copiedAddress ? 'Kopyalandı!' : 'Kopyala'}>
                            <IconButton
                              size="small"
                              onClick={copyAddress}
                              sx={{
                                p: 0.5,
                                '&:hover': {
                                  backgroundColor: 'rgba(79, 70, 229, 0.1)',
                                },
                              }}
                            >
                              {copiedAddress ? (
                                <CheckCircle sx={{ fontSize: 16, color: 'success.main' }} />
                              ) : (
                                <ContentCopy sx={{ fontSize: 16, color: 'text.secondary' }} />
                              )}
                            </IconButton>
                          </Tooltip>
                        </Box>
                        {balance && (
                          <Typography variant="caption" sx={{ color: 'text.secondary', fontWeight: 500 }}>
                            {parseFloat(formatEther(balance.value)).toFixed(4)} {balance.symbol}
                          </Typography>
                        )}
                      </Box>
                    </Box>
                  </Card>
                </motion.div>
              )}
            </Box>
          </Box>
        </motion.div>

      {/* Ana Kartlar: Material-UI + Tailwind + Framer Motion + Lucide-React */}
      <motion.div
        variants={staggerContainer}
        initial="hidden"
        animate="show"
      >
        <Grid container spacing={4} className="mb-12" sx={{ mb: 6 }}>
          {/* Toplam Bakiye Kartı - FinTech Style with Glow */}
          <Grid item xs={12} md={4}>
            <motion.div
              variants={staggerItem}
              whileHover={{ scale: 1.02, y: -4 }}
              transition={{ type: 'spring', stiffness: 300 }}
              className="h-full"
            >
              <Card 
                sx={{ 
                  borderRadius: 6, 
                  background: 'linear-gradient(135deg, #4F46E5 0%, #7C3AED 50%, #3B82F6 100%)', 
                  color: 'white',
                  boxShadow: '0 25px 50px -12px rgba(79, 70, 229, 0.4), 0 0 0 1px rgba(255, 255, 255, 0.1)',
                  position: 'relative', 
                  overflow: 'hidden',
                  height: '100%',
                  '&::before': {
                    content: '""',
                    position: 'absolute',
                    top: '-50%',
                    right: '-50%',
                    width: '200%',
                    height: '200%',
                    background: 'radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%)',
                    animation: 'pulse 3s ease-in-out infinite',
                  },
                  '@keyframes pulse': {
                    '0%, 100%': { opacity: 0.3 },
                    '50%': { opacity: 0.6 },
                  },
                }}
              >
                <CardContent sx={{ p: 4, position: 'relative', zIndex: 1 }}>
                  <AccountBalanceWallet sx={{ position: 'absolute', right: -30, bottom: -30, fontSize: 180, opacity: 0.15 }} />
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
                    <ShieldCheck size={20} strokeWidth={2.5} style={{ opacity: 0.9 }} />
                    <Typography variant="body2" sx={{ opacity: 0.9, fontWeight: 600, letterSpacing: '0.05em', textTransform: 'uppercase', fontSize: '0.75rem' }}>
                      {t('wallet.totalBalance', 'Toplam Bakiye')}
                    </Typography>
                  </Box>
                  <Typography 
                    variant="h3" 
                    sx={{ 
                      fontWeight: 900, 
                      my: 2,
                      fontSize: '2.5rem',
                      letterSpacing: '-0.02em',
                      textShadow: '0 2px 10px rgba(0, 0, 0, 0.2)',
                    }}
                  >
                    {formatCurrency(blockchainData?.balance?.total || 0)}
                  </Typography>
                  <Box sx={{ display: 'flex', gap: 3, mt: 4, pt: 3, borderTop: '1px solid rgba(255, 255, 255, 0.2)' }}>
                    <Box>
                      <Typography variant="caption" sx={{ opacity: 0.8, fontSize: '0.7rem', letterSpacing: '0.05em' }}>
                        Kullanılabilir
                      </Typography>
                      <Typography variant="h6" sx={{ fontWeight: 800, mt: 0.5, fontSize: '1.1rem' }}>
                        {formatCurrency(blockchainData?.balance?.available || 0)}
                      </Typography>
                    </Box>
                    <Box sx={{ ml: 'auto', textAlign: 'right' }}>
                      <Typography variant="caption" sx={{ opacity: 0.8, fontSize: '0.7rem', letterSpacing: '0.05em' }}>
                        Escrow'da
                      </Typography>
                      <Typography variant="h6" sx={{ fontWeight: 800, mt: 0.5, fontSize: '1.1rem', color: '#FCD34D' }}>
                        {formatCurrency(blockchainData?.balance?.locked || 0)}
                      </Typography>
                    </Box>
                  </Box>
                </CardContent>
              </Card>
            </motion.div>
          </Grid>

          {/* Health Token Kartı - Glassmorphism */}
          <Grid item xs={12} md={4}>
            <motion.div
              variants={staggerItem}
              whileHover={{ scale: 1.02, y: -4 }}
              transition={{ type: 'spring', stiffness: 300 }}
              className="h-full"
            >
              <Card 
                sx={{ 
                  borderRadius: 6, 
                  backdropFilter: 'blur(20px) saturate(180%)',
                  WebkitBackdropFilter: 'blur(20px) saturate(180%)',
                  backgroundColor: 'rgba(255, 255, 255, 0.7)',
                  border: '1px solid rgba(255, 255, 255, 0.3)',
                  boxShadow: '0 8px 32px rgba(0, 0, 0, 0.08)',
                  height: '100%',
                  transition: 'all 0.3s ease',
                  '&:hover': {
                    boxShadow: '0 12px 40px rgba(16, 185, 129, 0.2)',
                    borderColor: 'rgba(16, 185, 129, 0.3)',
                  },
                }}
              >
                <CardContent sx={{ p: 4 }}>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 3 }}>
                    <Box 
                      sx={{ 
                        p: 2, 
                        background: 'linear-gradient(135deg, rgba(16, 185, 129, 0.15) 0%, rgba(34, 197, 94, 0.1) 100%)',
                        borderRadius: 3,
                        border: '1px solid rgba(16, 185, 129, 0.2)',
                      }}
                    >
                      <Payment sx={{ fontSize: 32, color: 'success.main' }} />
                    </Box>
                    <Chip 
                      label="+12% ↑" 
                      size="small" 
                      sx={{ 
                        fontWeight: 800,
                        background: 'linear-gradient(135deg, rgba(16, 185, 129, 0.2) 0%, rgba(34, 197, 94, 0.15) 100%)',
                        border: '1px solid rgba(16, 185, 129, 0.3)',
                        color: 'success.dark',
                      }} 
                    />
                  </Box>
                  <Typography variant="body2" sx={{ color: 'text.secondary', fontWeight: 600, letterSpacing: '0.05em', textTransform: 'uppercase', fontSize: '0.75rem' }}>
                    Health Token
                  </Typography>
                  <Typography variant="h4" sx={{ fontWeight: 900, mt: 1, letterSpacing: '-0.02em', color: 'success.dark' }}>
                    {blockchainData?.tokens?.total || 0} HT
                  </Typography>
                  <Typography variant="caption" sx={{ color: 'text.secondary', fontWeight: 500 }}>
                    ≈ {formatCurrency((blockchainData?.tokens?.total || 0) * 0.1)}
                  </Typography>
                </CardContent>
              </Card>
            </motion.div>
          </Grid>

          {/* Sigorta Kartı - Glassmorphism with ShieldCheck */}
          <Grid item xs={12} md={4}>
            <motion.div
              variants={staggerItem}
              whileHover={{ scale: 1.02, y: -4 }}
              transition={{ type: 'spring', stiffness: 300 }}
              className="h-full"
            >
              <Card 
                sx={{ 
                  borderRadius: 6, 
                  backdropFilter: 'blur(20px) saturate(180%)',
                  WebkitBackdropFilter: 'blur(20px) saturate(180%)',
                  backgroundColor: 'rgba(255, 255, 255, 0.7)',
                  border: '1px solid rgba(255, 255, 255, 0.3)',
                  boxShadow: '0 8px 32px rgba(0, 0, 0, 0.08)',
                  height: '100%',
                  transition: 'all 0.3s ease',
                  '&:hover': {
                    boxShadow: '0 12px 40px rgba(14, 165, 233, 0.2)',
                    borderColor: 'rgba(14, 165, 233, 0.3)',
                  },
                }}
              >
                <CardContent sx={{ p: 4 }}>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 3 }}>
                    <Box 
                      sx={{ 
                        p: 2, 
                        background: 'linear-gradient(135deg, rgba(14, 165, 233, 0.15) 0%, rgba(59, 130, 246, 0.1) 100%)',
                        borderRadius: 3,
                        border: '1px solid rgba(14, 165, 233, 0.2)',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                      }}
                    >
                      <ShieldCheck size={32} strokeWidth={2.5} style={{ color: '#0EA5E9' }} />
                    </Box>
                    <Typography 
                      variant="caption" 
                      sx={{ 
                        fontWeight: 800, 
                        color: 'secondary.main',
                        letterSpacing: '0.1em',
                        textTransform: 'uppercase',
                        fontSize: '0.7rem',
                      }}
                    >
                      AKILLI SİGORTA
                    </Typography>
                  </Box>
                  <Typography variant="body2" sx={{ color: 'text.secondary', fontWeight: 600, letterSpacing: '0.05em', textTransform: 'uppercase', fontSize: '0.75rem' }}>
                    Aktif Poliçeler
                  </Typography>
                  <Typography variant="h4" sx={{ fontWeight: 900, mt: 1, letterSpacing: '-0.02em', color: 'secondary.dark' }}>
                    {insurancePolicies?.filter(p => p.status === 'ACTIVE').length || 0}
                  </Typography>
                  <Typography variant="caption" sx={{ color: 'text.secondary', fontWeight: 500 }}>
                    Toplam {insurancePolicies?.length || 0} Kayıt
                  </Typography>
                </CardContent>
              </Card>
            </motion.div>
          </Grid>
        </Grid>
      </motion.div>

      {/* Detay Paneli: Material-UI + Tailwind + Framer Motion */}
      <motion.div
        variants={fadeInUp}
        initial="initial"
        animate="animate"
        transition={{ delay: 0.3 }}
      >
        <Paper 
          sx={{ 
            borderRadius: 6, 
            overflow: 'hidden', 
            backdropFilter: 'blur(20px) saturate(180%)',
            WebkitBackdropFilter: 'blur(20px) saturate(180%)',
            backgroundColor: 'rgba(255, 255, 255, 0.8)',
            border: '1px solid rgba(255, 255, 255, 0.3)',
            boxShadow: '0 8px 32px rgba(0, 0, 0, 0.1)',
          }}
        >
          <Tabs 
            value={tabValue} 
            onChange={(e, v) => setTabValue(v)}
            className="px-6 pt-4 bg-gray-50"
            sx={{ 
              px: 3, pt: 2, 
              background: 'linear-gradient(180deg, rgba(248, 250, 252, 0.5) 0%, rgba(241, 245, 249, 0.3) 100%)',
              backdropFilter: 'blur(10px)',
              '& .MuiTab-root': { 
                fontWeight: 700, 
                textTransform: 'none', 
                fontSize: '0.95rem',
                letterSpacing: '-0.01em',
                '&.Mui-selected': {
                  color: 'primary.main',
                },
              },
            }}
          >
            <Tab label="İşlemler" />
            <Tab label="NFT Raporlar" />
            <Tab label="Sigorta" />
          </Tabs>

          <Box className="p-6" sx={{ p: 3 }}>
            <AnimatePresence mode="wait">
              {tabValue === 0 && (
                <motion.div
                  variants={scaleIn}
                  initial="initial"
                  animate="animate"
                  exit="exit"
                >
                  <TableContainer className="rounded-lg overflow-hidden">
                    <Table className="min-w-full" sx={{ minWidth: 650 }}>
                      <TableHead className="bg-gray-100" sx={{ bgcolor: '#f1f5f9' }}>
                        <TableRow>
                          <TableCell className="font-bold" sx={{ fontWeight: 700 }}>İşlem Hash</TableCell>
                          <TableCell className="font-bold" sx={{ fontWeight: 700 }}>Tür</TableCell>
                          <TableCell className="font-bold" sx={{ fontWeight: 700 }}>Tutar</TableCell>
                          <TableCell className="font-bold" sx={{ fontWeight: 700 }}>Durum</TableCell>
                          <TableCell align="right" className="font-bold" sx={{ fontWeight: 700 }}>Detay</TableCell>
                        </TableRow>
                      </TableHead>
                      <TableBody>
                        {blockchainData?.transactions?.map((tx, index) => (
                          <TableRow 
                            key={tx.hash}
                            className="hover:bg-gray-50 transition-colors cursor-pointer"
                            hover 
                            sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
                          >
                            <TableCell className="font-mono text-blue-600" sx={{ fontFamily: 'monospace', color: 'primary.main' }}>
                              {tx.hash.substring(0, 12)}...
                            </TableCell>
                            <TableCell>
                              <Chip 
                                label={tx.type} 
                                size="small" 
                                className="font-semibold rounded-md"
                                sx={{ fontWeight: 600, borderRadius: '6px' }} 
                              />
                            </TableCell>
                            <TableCell className="font-bold" sx={{ fontWeight: 700 }}>
                              <Box className="flex items-center gap-1" sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                                {tx.amount > 0 ? <TrendingUp color="success" fontSize="small" /> : <TrendingDown color="error" fontSize="small" />}
                                {formatCurrency(Math.abs(tx.amount), tx.currency)}
                              </Box>
                            </TableCell>
                            <TableCell>
                              <Chip 
                                label={tx.status} 
                                color={tx.status === 'CONFIRMED' ? 'success' : 'warning'} 
                                size="small" 
                                className="font-bold"
                                sx={{ fontWeight: 700 }}
                              />
                            </TableCell>
                            <TableCell align="right">
                              <Tooltip title="Detayları Gör">
                                <IconButton 
                                  className="hover:bg-blue-50 transition-colors"
                                  onClick={() => { setSelectedTransaction(tx); setOpenDialog(true); }}
                                >
                                  <Visibility fontSize="small" />
                                </IconButton>
                              </Tooltip>
                            </TableCell>
                          </TableRow>
                        ))}
                      </TableBody>
                    </Table>
                  </TableContainer>
                </motion.div>
              )}
              {tabValue === 1 && (
                <motion.div
                  variants={scaleIn}
                  initial="initial"
                  animate="animate"
                  exit="exit"
                >
                  <motion.div
                    variants={staggerContainer}
                    initial="hidden"
                    animate="show"
                  >
                    <Grid container spacing={3}>
                      {blockchainData?.nfts && blockchainData.nfts.length > 0 ? (
                        blockchainData.nfts.map((nft, index) => (
                          <Grid item xs={12} sm={6} md={4} key={nft.tokenId}>
                            <motion.div
                              variants={staggerItem}
                              {...hoverLift}
                            >
                              <Card 
                                className="rounded-2xl border border-gray-200 shadow-md hover:shadow-xl transition-all duration-300"
                                sx={{ borderRadius: 4, border: '1px solid #e2e8f0' }}
                              >
                                <CardContent className="p-4">
                                  <Box className="flex items-center mb-4" sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                                    <Receipt sx={{ fontSize: 32, color: 'primary.main', mr: 1 }} />
                                    <Box>
                                      <Typography variant="subtitle2" className="font-bold" sx={{ fontWeight: 700 }}>
                                        {nft.name}
                                      </Typography>
                                      <Typography variant="caption" color="text.secondary">{nft.type}</Typography>
                                    </Box>
                                  </Box>
                                  <Chip 
                                    label={nft.verified ? 'Doğrulandı' : 'Doğrulanmadı'} 
                                    size="small" 
                                    color={nft.verified ? 'success' : 'default'} 
                                    className="mb-2"
                                    sx={{ mb: 1 }} 
                                  />
                                  <Typography 
                                    variant="body2" 
                                    color="text.secondary" 
                                    className="font-mono text-xs break-all"
                                    sx={{ fontFamily: 'monospace', fontSize: '0.7rem', wordBreak: 'break-all' }}
                                  >
                                    {nft.tokenId}
                                  </Typography>
                                </CardContent>
                              </Card>
                            </motion.div>
                          </Grid>
                        ))
                      ) : (
                        <Grid item xs={12}>
                          <Box className="text-center py-8" sx={{ textAlign: 'center', py: 4 }}>
                            <Typography variant="body1" color="text.secondary">
                              Henüz NFT raporu bulunmuyor
                            </Typography>
                          </Box>
                        </Grid>
                      )}
                    </Grid>
                  </motion.div>
                </motion.div>
              )}
              {tabValue === 2 && (
                <motion.div
                  variants={scaleIn}
                  initial="initial"
                  animate="animate"
                  exit="exit"
                >
                  <TableContainer className="rounded-lg overflow-hidden">
                    <Table className="min-w-full" sx={{ minWidth: 650 }}>
                      <TableHead className="bg-gray-100" sx={{ bgcolor: '#f1f5f9' }}>
                        <TableRow>
                          <TableCell className="font-bold" sx={{ fontWeight: 700 }}>Poliçe No</TableCell>
                          <TableCell className="font-bold" sx={{ fontWeight: 700 }}>Kapsam</TableCell>
                          <TableCell className="font-bold" sx={{ fontWeight: 700 }}>Prim</TableCell>
                          <TableCell className="font-bold" sx={{ fontWeight: 700 }}>Durum</TableCell>
                          <TableCell className="font-bold" sx={{ fontWeight: 700 }}>Son Geçerlilik</TableCell>
                          <TableCell align="right" className="font-bold" sx={{ fontWeight: 700 }}>İşlemler</TableCell>
                        </TableRow>
                      </TableHead>
                      <TableBody>
                        {insurancePolicies && insurancePolicies.length > 0 ? (
                          insurancePolicies.map((policy, index) => (
                            <TableRow 
                              key={policy.id}
                              className="hover:bg-gray-50 transition-colors"
                              hover 
                              sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
                            >
                              <TableCell>{policy.policyNumber || policy.id}</TableCell>
                              <TableCell>{policy.coverageType || 'Genel'}</TableCell>
                              <TableCell className="font-semibold" sx={{ fontWeight: 600 }}>
                                {formatCurrency(policy.premium || 0)}
                              </TableCell>
                              <TableCell>
                                <Chip 
                                  label={policy.status} 
                                  color={policy.status === 'ACTIVE' ? 'success' : 'default'} 
                                  size="small" 
                                  className="font-bold"
                                  sx={{ fontWeight: 700 }}
                                />
                              </TableCell>
                              <TableCell>
                                {policy.expiryDate ? new Date(policy.expiryDate).toLocaleDateString('tr-TR') : 'N/A'}
                              </TableCell>
                              <TableCell align="right">
                                <Button 
                                  size="small" 
                                  variant="outlined" 
                                  className="rounded-lg normal-case hover:bg-blue-50 transition-colors"
                                  sx={{ borderRadius: 2, textTransform: 'none' }}
                                >
                                  Talep Et
                                </Button>
                              </TableCell>
                            </TableRow>
                          ))
                        ) : (
                          <TableRow>
                            <TableCell colSpan={6} align="center" className="py-8" sx={{ py: 4 }}>
                              <Typography variant="body1" color="text.secondary">
                                Henüz sigorta poliçesi bulunmuyor
                              </Typography>
                            </TableCell>
                          </TableRow>
                        )}
                      </TableBody>
                    </Table>
                  </TableContainer>
                </motion.div>
              )}
            </AnimatePresence>
          </Box>
        </Paper>
      </motion.div>

      {/* Dialog: Material-UI + Tailwind + Framer Motion */}
      <AnimatePresence>
        {openDialog && (
          <Dialog 
            open={openDialog} 
            onClose={() => setOpenDialog(false)}
            PaperComponent={({ children, ...props }) => (
              <motion.div
                initial={{ opacity: 0, scale: 0.9 }}
                animate={{ opacity: 1, scale: 1 }}
                exit={{ opacity: 0, scale: 0.9 }}
                transition={{ duration: 0.2 }}
              >
                <Paper {...props} className="rounded-3xl p-4 backdrop-blur-lg bg-white/95 shadow-2xl">
                  {children}
                </Paper>
              </motion.div>
            )}
            PaperProps={{ sx: { borderRadius: 6, p: 2 } }}
          >
            <DialogTitle className="font-extrabold text-xl" sx={{ fontWeight: 800 }}>
              İşlem Ayrıntıları
            </DialogTitle>
            <DialogContent>
              {selectedTransaction && (
                <motion.div
                  initial={{ opacity: 0, y: 10 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: 0.1 }}
                >
                  <Box className="p-4 bg-gray-50 rounded-2xl" sx={{ p: 2, bgcolor: '#f8fafc', borderRadius: 4 }}>
                    <Typography variant="caption" color="text.secondary" className="font-bold text-xs uppercase" sx={{ fontWeight: 700 }}>
                      TX HASH
                    </Typography>
                    <Typography 
                      variant="body2" 
                      className="font-mono mb-4 break-all text-sm"
                      sx={{ fontFamily: 'monospace', mb: 2, wordBreak: 'break-all' }}
                    >
                      {selectedTransaction.hash}
                    </Typography>
                    <Grid container spacing={3}>
                      <Grid item xs={6}>
                        <Typography variant="caption" color="text.secondary" className="font-bold text-xs uppercase" sx={{ fontWeight: 700 }}>
                          BLOK
                        </Typography>
                        <Typography variant="body1" className="font-semibold mt-1" sx={{ fontWeight: 600 }}>
                          {selectedTransaction.blockNumber}
                        </Typography>
                      </Grid>
                      <Grid item xs={6}>
                        <Typography variant="caption" color="text.secondary" className="font-bold text-xs uppercase" sx={{ fontWeight: 700 }}>
                          GAS USED
                        </Typography>
                        <Typography variant="body1" className="font-semibold mt-1" sx={{ fontWeight: 600 }}>
                          {selectedTransaction.gasUsed}
                        </Typography>
                      </Grid>
                    </Grid>
                  </Box>
                </motion.div>
              )}
            </DialogContent>
            <DialogActions className="p-6" sx={{ p: 3 }}>
              <Button 
                onClick={() => setOpenDialog(false)} 
                variant="contained" 
                className="rounded-xl normal-case px-8 shadow-lg hover:shadow-xl transition-shadow"
                sx={{ borderRadius: 3, textTransform: 'none', px: 4 }}
              >
                Kapat
              </Button>
            </DialogActions>
          </Dialog>
        )}
      </AnimatePresence>
      </Container>
    </Box>
  );
};

export default HealthWallet;