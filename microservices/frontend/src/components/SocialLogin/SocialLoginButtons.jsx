// src/components/SocialLogin/SocialLoginButtons.jsx
import React from 'react';
import { Button, Box, Divider, Typography } from '@mui/material';
import GoogleIcon from '@mui/icons-material/Google';
import FacebookIcon from '@mui/icons-material/Facebook';
import InstagramIcon from '@mui/icons-material/Instagram';
import AppleIcon from '@mui/icons-material/Apple';
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
                <Button
                    fullWidth
                    variant="outlined"
                    startIcon={<InstagramIcon />}
                    onClick={handleInstagramLogin}
                    sx={{
                        borderColor: '#E4405F',
                        color: '#E4405F',
                        '&:hover': {
                            borderColor: '#E4405F',
                            backgroundColor: 'rgba(228, 64, 95, 0.04)'
                        }
                    }}
                >
                    Instagram ile Giriş Yap
                </Button>
                <Button
                    fullWidth
                    variant="outlined"
                    startIcon={<AppleIcon />}
                    onClick={handleAppleLogin}
                    sx={{
                        borderColor: '#000000',
                        color: '#000000',
                        '&:hover': {
                            borderColor: '#000000',
                            backgroundColor: 'rgba(0, 0, 0, 0.04)'
                        }
                    }}
                >
                    Apple ile Giriş Yap
                </Button>
            </Box>
        </Box>
    );
    
    const handleInstagramLogin = async () => {
        try {
            // Instagram OAuth2 popup aç
            const instagramAuthUrl = `https://api.instagram.com/oauth/authorize?client_id=${import.meta.env.VITE_INSTAGRAM_CLIENT_ID || 'your_instagram_client_id'}&redirect_uri=${encodeURIComponent(window.location.origin + '/auth/instagram/callback')}&scope=user_profile,user_media&response_type=code`;
            
            const popup = window.open(instagramAuthUrl, 'instagram-login', 'width=500,height=600');
            
            window.addEventListener('message', async (event) => {
                if (event.origin !== window.location.origin) return;
                
                if (event.data.type === 'INSTAGRAM_AUTH_SUCCESS') {
                    const { id, email, firstName, lastName, profilePicture } = event.data;
                    
                    const response = await authService.socialLogin({
                        provider: 'instagram',
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
                    
                    toast.success('Instagram ile giriş başarılı!');
                    navigate('/dashboard');
                    popup.close();
                }
            });
        } catch (error) {
            toast.error('Instagram ile giriş başarısız: ' + error.message);
        }
    };
    
    const handleAppleLogin = async () => {
        try {
            // Apple Sign In popup aç
            const appleAuthUrl = `https://appleid.apple.com/auth/authorize?client_id=${import.meta.env.VITE_APPLE_CLIENT_ID || 'your_apple_client_id'}&redirect_uri=${encodeURIComponent(window.location.origin + '/auth/apple/callback')}&response_type=code id_token&scope=email name&response_mode=form_post`;
            
            const popup = window.open(appleAuthUrl, 'apple-login', 'width=500,height=600');
            
            window.addEventListener('message', async (event) => {
                if (event.origin !== window.location.origin) return;
                
                if (event.data.type === 'APPLE_AUTH_SUCCESS') {
                    const { id, email, firstName, lastName } = event.data;
                    
                    const response = await authService.socialLogin({
                        provider: 'apple',
                        id,
                        email,
                        firstName,
                        lastName
                    });
                    
                    localStorage.setItem('token', response.data.accessToken);
                    localStorage.setItem('refreshToken', response.data.refreshToken);
                    localStorage.setItem('user', JSON.stringify({
                        id: response.data.userId,
                        email: response.data.email,
                        role: response.data.role
                    }));
                    
                    toast.success('Apple ile giriş başarılı!');
                    navigate('/dashboard');
                    popup.close();
                }
            });
        } catch (error) {
            toast.error('Apple ile giriş başarısız: ' + error.message);
        }
    };
}

