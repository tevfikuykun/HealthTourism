// src/components/SocialLogin/SocialLogin.jsx
import React from 'react';
import { Button, Box, Divider, Typography } from '@mui/material';
import GoogleIcon from '@mui/icons-material/Google';
import FacebookIcon from '@mui/icons-material/Facebook';
import AppleIcon from '@mui/icons-material/Apple';

const SocialLogin = ({ onGoogleLogin, onFacebookLogin, onAppleLogin }) => {
  const handleGoogleLogin = () => {
    // Google OAuth entegrasyonu
    if (onGoogleLogin) {
      onGoogleLogin();
    } else {
      window.location.href = '/api/auth/google';
    }
  };

  const handleFacebookLogin = () => {
    // Facebook OAuth entegrasyonu
    if (onFacebookLogin) {
      onFacebookLogin();
    } else {
      window.location.href = '/api/auth/facebook';
    }
  };

  const handleAppleLogin = () => {
    // Apple Sign In entegrasyonu
    if (onAppleLogin) {
      onAppleLogin();
    } else {
      window.location.href = '/api/auth/apple';
    }
  };

  return (
    <Box>
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
          sx={{ textTransform: 'none' }}
        >
          Google ile Giriş Yap
        </Button>
        <Button
          fullWidth
          variant="outlined"
          startIcon={<FacebookIcon />}
          onClick={handleFacebookLogin}
          sx={{ textTransform: 'none' }}
        >
          Facebook ile Giriş Yap
        </Button>
        <Button
          fullWidth
          variant="outlined"
          startIcon={<AppleIcon />}
          onClick={handleAppleLogin}
          sx={{ textTransform: 'none' }}
        >
          Apple ile Giriş Yap
        </Button>
      </Box>
    </Box>
  );
};

export default SocialLogin;

