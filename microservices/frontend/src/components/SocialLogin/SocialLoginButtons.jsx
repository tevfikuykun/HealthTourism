// src/components/SocialLogin/SocialLoginButtons.jsx
import React from 'react';
import { Button, Box, Divider, Typography } from '@mui/material';
import GoogleIcon from '@mui/icons-material/Google';
import FacebookIcon from '@mui/icons-material/Facebook';
import { authService } from '../../services/api';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';

export default function SocialLoginButtons() {
    const navigate = useNavigate();

    const handleGoogleLogin = async () => {
        try {
            // Google OAuth2 popup aç
            const googleAuthUrl = `https://accounts.google.com/o/oauth2/v2/auth?client_id=${import.meta.env.VITE_GOOGLE_CLIENT_ID || 'your_google_client_id'}&redirect_uri=${encodeURIComponent(window.location.origin + '/auth/google/callback')}&response_type=code&scope=email profile`;
            
            // Popup aç
            const popup = window.open(googleAuthUrl, 'google-login', 'width=500,height=600');
            
            // Popup'dan mesaj dinle
            window.addEventListener('message', async (event) => {
                if (event.origin !== window.location.origin) return;
                
                if (event.data.type === 'GOOGLE_AUTH_SUCCESS') {
                    const { id, email, firstName, lastName, profilePicture } = event.data;
                    
                    // Backend'e gönder
                    const response = await authService.socialLogin({
                        provider: 'google',
                        id,
                        email,
                        firstName,
                        lastName,
                        profilePicture
                    });
                    
                    // Token'ları kaydet
                    localStorage.setItem('token', response.data.accessToken);
                    localStorage.setItem('refreshToken', response.data.refreshToken);
                    localStorage.setItem('user', JSON.stringify({
                        id: response.data.userId,
                        email: response.data.email,
                        role: response.data.role
                    }));
                    
                    toast.success('Google ile giriş başarılı!');
                    navigate('/dashboard');
                    popup.close();
                }
            });
        } catch (error) {
            toast.error('Google ile giriş başarısız: ' + error.message);
        }
    };

    const handleFacebookLogin = async () => {
        try {
            // Facebook OAuth2 popup aç
            const facebookAuthUrl = `https://www.facebook.com/v18.0/dialog/oauth?client_id=${import.meta.env.VITE_FACEBOOK_APP_ID || 'your_facebook_app_id'}&redirect_uri=${encodeURIComponent(window.location.origin + '/auth/facebook/callback')}&scope=email,public_profile`;
            
            const popup = window.open(facebookAuthUrl, 'facebook-login', 'width=500,height=600');
            
            window.addEventListener('message', async (event) => {
                if (event.origin !== window.location.origin) return;
                
                if (event.data.type === 'FACEBOOK_AUTH_SUCCESS') {
                    const { id, email, firstName, lastName, profilePicture } = event.data;
                    
                    const response = await authService.socialLogin({
                        provider: 'facebook',
                        id,
                        email,
                        firstName,
                        lastName,
                        profilePicture
                    });
                    
                    localStorage.setItem('token', response.data.accessToken);
                    localStorage.setItem('refreshToken', response.data.refreshToken);
                    localStorage.setItem('user', JSON.stringify({
                        id: response.data.userId,
                        email: response.data.email,
                        role: response.data.role
                    }));
                    
                    toast.success('Facebook ile giriş başarılı!');
                    navigate('/dashboard');
                    popup.close();
                }
            });
        } catch (error) {
            toast.error('Facebook ile giriş başarısız: ' + error.message);
        }
    };

    return (
        <Box sx={{ mt: 3 }}>
            <Divider sx={{ my: 2 }}>
                <Typography variant="body2" color="text.secondary">
                    veya
                </Typography>
            </Divider>
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                <Button
                    fullWidth
                    variant="outlined"
                    startIcon={<GoogleIcon />}
                    onClick={handleGoogleLogin}
                    sx={{
                        borderColor: '#db4437',
                        color: '#db4437',
                        '&:hover': {
                            borderColor: '#db4437',
                            backgroundColor: 'rgba(219, 68, 55, 0.04)'
                        }
                    }}
                >
                    Google ile Giriş Yap
                </Button>
                <Button
                    fullWidth
                    variant="outlined"
                    startIcon={<FacebookIcon />}
                    onClick={handleFacebookLogin}
                    sx={{
                        borderColor: '#4267B2',
                        color: '#4267B2',
                        '&:hover': {
                            borderColor: '#4267B2',
                            backgroundColor: 'rgba(66, 103, 178, 0.04)'
                        }
                    }}
                >
                    Facebook ile Giriş Yap
                </Button>
            </Box>
        </Box>
    );
}

