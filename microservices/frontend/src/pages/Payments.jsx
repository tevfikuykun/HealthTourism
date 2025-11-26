// src/pages/Payments.jsx
import React, { useState } from 'react';
import {
    Container, Box, Typography, Grid,
    TextField, Button,
    Paper, useTheme, Alert, Snackbar, Divider, CircularProgress
} from '@mui/material';
import PaymentIcon from '@mui/icons-material/Payment';
import AccountBalanceWalletIcon from '@mui/icons-material/AccountBalanceWallet';
import CreditCardIcon from '@mui/icons-material/CreditCard';
import VerifiedUserIcon from '@mui/icons-material/VerifiedUser';
import SearchIcon from '@mui/icons-material/Search';

// Simüle Edilmiş Fatura Bilgisi
const initialPaymentData = {
    code: 'HT-123456', // Test kodu
    clientName: 'Ayşe Yılmaz',
    service: 'Estetik Cerrahi Paketi',
    amount: 25500.00,
    dueDate: '30.11.2025',
};

function Payments() {
    const theme = useTheme();

    const [queryCode, setQueryCode] = useState('');
    const [paymentInfo, setPaymentInfo] = useState(null);
    const [isLoading, setIsLoading] = useState(false);
    const [isProcessing, setIsProcessing] = useState(false);

    // Kart Bilgileri State'i
    const [cardDetails, setCardDetails] = useState({
        cardNumber: '',
        cardHolder: '',
        expiryDate: '',
        cvv: '',
    });

    // Snackbar (Bildirim) State'i
    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [alertStatus, setAlertStatus] = useState({ severity: 'info', message: '' });

    // ------------------------------------
    // 1. FATURA SORGULAMA İŞLEMİ
    // ------------------------------------
    const handleQuery = (e) => {
        e.preventDefault();
        setPaymentInfo(null); // Yeni sorgulama öncesi önceki bilgiyi temizle
        setIsLoading(true);

        setTimeout(() => {
            setIsLoading(false);
            if (queryCode.trim() === initialPaymentData.code) {
                // Başarılı sorgulama
                setPaymentInfo(initialPaymentData);
                setAlertStatus({ severity: 'success', message: `Fatura bulundu. Toplam tutar: ${initialPaymentData.amount.toLocaleString()} ₺` });
            } else {
                // Başarısız sorgulama
                setPaymentInfo(null);
                setAlertStatus({ severity: 'error', message: 'Geçersiz Fatura/Rezervasyon Kodu. Lütfen kontrol ediniz.' });
            }
            setSnackbarOpen(true);
        }, 1000); // Simülasyon gecikmesi
    };

    // ------------------------------------
    // 2. ÖDEME İŞLEMİ
    // ------------------------------------
    const handlePayment = (e) => {
        e.preventDefault();
        if (!paymentInfo || isProcessing) return;

        setIsProcessing(true);
        // Gerçek ödeme API'si çağrısı burada yer alacaktır

        setTimeout(() => {
            setIsProcessing(false);
            // Başarılı Ödeme Simülasyonu
            setAlertStatus({ severity: 'success', message: 'Ödemeniz başarıyla tamamlanmıştır! Onay e-postası gönderilecektir.' });
            setSnackbarOpen(true);

            // Tüm state'leri sıfırla
            setPaymentInfo(null);
            setQueryCode('');
            setCardDetails({ cardNumber: '', cardHolder: '', expiryDate: '', cvv: '' });
        }, 3000);
    };

    // ------------------------------------
    // Yardımcı Fonksiyonlar
    // ------------------------------------
    const handleCardChange = (e) => {
        const { name, value } = e.target;
        setCardDetails(prev => ({ ...prev, [name]: value }));
    };

    const handleSnackbarClose = () => {
        setSnackbarOpen(false);
    };

    return (
        <Container maxWidth="md" sx={{ py: 5 }}>
            <Typography variant="h3" component="h1" align="center" gutterBottom sx={{ fontWeight: 'bold', mb: 2 }}>
                Güvenli Ödeme Merkezi
            </Typography>
            <Typography variant="h6" align="center" color="text.secondary" sx={{ mb: 4 }}>
                Fatura veya rezervasyon kodunuzu girerek ödemenizi kolayca tamamlayın.
            </Typography>

            <Grid container spacing={4}>
                {/* Sol Taraf: Fatura Sorgulama Alanı */}
                <Grid item xs={12} md={5}>
                    <Paper sx={{ p: 4, height: '100%', border: `1px solid ${theme.palette.primary.main}` }}>
                        <Typography variant="h5" sx={{ fontWeight: 'bold', mb: 3, display: 'flex', alignItems: 'center' }}>
                            <AccountBalanceWalletIcon color="primary" sx={{ mr: 1 }} /> Fatura Sorgula
                        </Typography>
                        <Box component="form" onSubmit={handleQuery} sx={{ mb: 3 }}>
                            <TextField
                                fullWidth
                                label="Fatura / Rezervasyon Kodu"
                                name="queryCode"
                                value={queryCode}
                                onChange={(e) => setQueryCode(e.target.value)}
                                required
                                sx={{ mb: 2 }}
                            />
                            <Button
                                fullWidth
                                type="submit"
                                variant="contained"
                                color="primary"
                                startIcon={isLoading ? <CircularProgress size={20} color="inherit" /> : <SearchIcon />}
                                disabled={isLoading}
                            >
                                {isLoading ? 'Sorgulanıyor...' : 'Sorgula'}
                            </Button>
                        </Box>

                        {/* Fatura Detayı (Sorgu Başarılıysa) */}
                        {paymentInfo && (
                            <Box sx={{ mt: 3, p: 2, border: `1px solid ${theme.palette.secondary.main}`, borderRadius: 1, backgroundColor: theme.palette.action.hover }}>
                                <Typography variant="subtitle1" sx={{ fontWeight: 'bold', mb: 1, color: theme.palette.secondary.main }}>
                                    Ödeme Yapılacak Fatura
                                </Typography>
                                <Divider sx={{ mb: 1 }}/>
                                <Typography variant="body1">Müşteri: **{paymentInfo.clientName}**</Typography>
                                <Typography variant="body1">Hizmet: {paymentInfo.service}</Typography>
                                <Typography variant="body1">Vade Tarihi: {paymentInfo.dueDate}</Typography>
                                <Typography variant="h5" color="error" sx={{ mt: 1, fontWeight: 'bold' }}>
                                    Tutar: {paymentInfo.amount.toLocaleString()} ₺
                                </Typography>
                            </Box>
                        )}
                    </Paper>
                </Grid>

                {/* Sağ Taraf: Kart Bilgileri ve Ödeme Formu */}
                <Grid item xs={12} md={7}>
                    <Paper sx={{ p: 4, height: '100%', border: `1px solid ${paymentInfo ? theme.palette.secondary.main : theme.palette.divider}` }}>
                        <Typography variant="h5" sx={{ fontWeight: 'bold', mb: 3, display: 'flex', alignItems: 'center' }}>
                            <CreditCardIcon color="primary" sx={{ mr: 1 }} /> Kart Bilgileri
                        </Typography>

                        {!paymentInfo && (
                             <Alert severity="warning" icon={<VerifiedUserIcon />} sx={{ mb: 3 }}>
                                Ödeme formunu aktif etmek için lütfen önce geçerli bir fatura kodu sorgulayınız.
                             </Alert>
                        )}

                        <Box component="form" onSubmit={handlePayment}>
                            <Grid container spacing={2}>
                                {/* Kart Bilgisi Alanları */}
                                <Grid item xs={12}>
                                    <TextField fullWidth label="Kart Numarası" name="cardNumber" value={cardDetails.cardNumber} onChange={handleCardChange} inputProps={{ maxLength: 16 }} required disabled={!paymentInfo || isProcessing} />
                                </Grid>
                                <Grid item xs={12}>
                                    <TextField fullWidth label="Kart Üzerindeki Ad Soyad" name="cardHolder" value={cardDetails.cardHolder} onChange={handleCardChange} required disabled={!paymentInfo || isProcessing} />
                                </Grid>
                                <Grid item xs={6}>
                                    <TextField fullWidth label="Son Kullanma Tarihi (AA/YY)" name="expiryDate" value={cardDetails.expiryDate} onChange={handleCardChange} inputProps={{ maxLength: 5 }} required disabled={!paymentInfo || isProcessing} />
                                </Grid>
                                <Grid item xs={6}>
                                    <TextField fullWidth label="CVV Kodu" name="cvv" type="password" value={cardDetails.cvv} onChange={handleCardChange} inputProps={{ maxLength: 3 }} required disabled={!paymentInfo || isProcessing} />
                                </Grid>

                                {/* Ödeme Butonu */}
                                <Grid item xs={12}>
                                    <Button
                                        fullWidth
                                        type="submit"
                                        variant="contained"
                                        color="secondary"
                                        size="large"
                                        startIcon={isProcessing ? <CircularProgress size={20} color="inherit" /> : <PaymentIcon />}
                                        disabled={!paymentInfo || isProcessing}
                                        sx={{ py: 1.5, mt: 1 }}
                                    >
                                        {isProcessing ? 'İşleniyor...' : `${paymentInfo ? paymentInfo.amount.toLocaleString() : '0'} ₺ Ödeme Yap`}
                                    </Button>
                                </Grid>

                                <Grid item xs={12} sx={{ textAlign: 'center', mt: 2 }}>
                                    <Typography variant="caption" color="text.secondary">
                                        <VerifiedUserIcon fontSize="small" color="success" sx={{ verticalAlign: 'middle', mr: 0.5 }} />
                                        3D Secure ve SSL ile güvenli ödeme.
                                    </Typography>
                                </Grid>
                            </Grid>
                        </Box>
                    </Paper>
                </Grid>
            </Grid>

            <Snackbar open={snackbarOpen} autoHideDuration={6000} onClose={handleSnackbarClose} anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}>
                <Alert onClose={handleSnackbarClose} severity={alertStatus.severity} sx={{ width: '100%' }}>
                    {alertStatus.message}
                </Alert>
            </Snackbar>
        </Container>
    );
}

export default Payments;