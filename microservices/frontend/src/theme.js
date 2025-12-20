import { createTheme } from '@mui/material/styles';

/**
 * Deep-Trust Color Palette - Modern Health Tourism Theme
 * Material-UI + Tailwind CSS + Framer Motion Integration
 * 
 * Primary: #4F46E5 (Indigo Blue) - Modern technology, Blockchain, Professionalism
 * Secondary: #0EA5E9 (Sky Blue) - Health and cleanliness perception
 * Success: #10B981 (Emerald) - Healthy data, confirmed transactions, gains
 * Warning: #F59E0B (Amber) - Escrow locked amounts, pending appointments
 * Surface: #F8FAFC (Slate 50) - Eye-friendly, sterile and spacious workspace
 * 
 * Dark Mode:
 * Background: #0F172A (Slate 900) - Deep space blue
 * Neon Accent: #818CF8 (Indigo 400) - For 3D model glow
 * Glass Effect: rgba(255, 255, 255, 0.05) + backdrop-filter: blur(10px)
 * 
 * Functional Colors:
 * AI Diagnostics: #8B5CF6 (Purple) - Smart processing indicator
 * IoT Vital Signs: #F43F5E (Rose) - Soft red for vital signs (non-panic)
 * Blockchain: Vibrant Indigo tones - Digital asset representation
 */

const getTheme = (mode = 'light') => {
  const isDark = mode === 'dark';

  return createTheme({
    palette: {
      mode,
      // Primary - Indigo Blue (#4F46E5)
      primary: {
        main: '#4F46E5', // Indigo 600
        light: '#818CF8', // Indigo 400 (Neon accent for dark mode)
        dark: '#3730A3', // Indigo 700
        contrastText: '#FFFFFF',
      },
      // Secondary - Sky Blue (#0EA5E9)
      secondary: {
        main: '#0EA5E9', // Sky Blue 500
        light: '#38BDF8', // Sky Blue 400
        dark: '#0284C7', // Sky Blue 600
        contrastText: '#FFFFFF',
      },
      // Success - Emerald (#10B981)
      success: {
        main: '#10B981', // Emerald 500
        light: '#34D399', // Emerald 400
        dark: '#059669', // Emerald 600
        contrastText: '#FFFFFF',
      },
      // Warning - Amber (#F59E0B)
      warning: {
        main: '#F59E0B', // Amber 500
        light: '#FBBF24', // Amber 400
        dark: '#D97706', // Amber 600
        contrastText: '#FFFFFF',
      },
      // Error - Rose (#F43F5E) for IoT vital signs
      error: {
        main: '#F43F5E', // Rose 500 (Soft red for vital signs)
        light: '#FB7185', // Rose 400
        dark: '#E11D48', // Rose 600
        contrastText: '#FFFFFF',
      },
      // Info - Purple (#8B5CF6) for AI diagnostics
      info: {
        main: '#8B5CF6', // Purple 500 (AI diagnostics)
        light: '#A78BFA', // Purple 400
        dark: '#7C3AED', // Purple 600
        contrastText: '#FFFFFF',
      },
      // Background
      background: {
        default: isDark ? '#0F172A' : '#F8FAFC', // Slate 900 (dark) / Slate 50 (light)
        paper: isDark ? '#1E293B' : '#FFFFFF', // Slate 800 (dark) / White (light)
      },
      // Text
      text: {
        primary: isDark ? '#F1F5F9' : '#0F172A', // Slate 100 (dark) / Slate 900 (light)
        secondary: isDark ? '#94A3B8' : '#64748B', // Slate 400 (dark) / Slate 500 (light)
      },
      // Divider
      divider: isDark ? 'rgba(255, 255, 255, 0.1)' : 'rgba(0, 0, 0, 0.08)',
      // Action
      action: {
        hover: isDark ? 'rgba(255, 255, 255, 0.08)' : 'rgba(0, 0, 0, 0.04)',
        selected: isDark ? 'rgba(79, 70, 229, 0.2)' : 'rgba(79, 70, 229, 0.08)',
        disabled: isDark ? 'rgba(255, 255, 255, 0.3)' : 'rgba(0, 0, 0, 0.26)',
      },
    },
    // Typography - Inter Font (Modern, Premium)
    typography: {
      fontFamily: [
        'Inter',
        '-apple-system',
        'BlinkMacSystemFont',
        '"Segoe UI"',
        'Roboto',
        '"Helvetica Neue"',
        'Arial',
        'sans-serif',
        '"Apple Color Emoji"',
        '"Segoe UI Emoji"',
        '"Segoe UI Symbol"',
      ].join(','),
      h1: {
        fontWeight: 900,
        letterSpacing: '-0.02em',
        lineHeight: 1.2,
      },
      h2: {
        fontWeight: 800,
        letterSpacing: '-0.01em',
        lineHeight: 1.3,
      },
      h3: {
        fontWeight: 800,
        letterSpacing: '-0.01em',
        lineHeight: 1.3,
      },
      h4: {
        fontWeight: 700,
        lineHeight: 1.4,
      },
      h5: {
        fontWeight: 700,
        lineHeight: 1.4,
      },
      h6: {
        fontWeight: 700,
        lineHeight: 1.4,
      },
      button: {
        fontWeight: 600,
        textTransform: 'none',
        letterSpacing: '0.01em',
      },
      body1: {
        lineHeight: 1.6,
      },
      body2: {
        lineHeight: 1.5,
      },
    },
    // Shape - Modern rounded corners
    shape: {
      borderRadius: 16, // Modern, yuvarlak köşeler (16px = 1rem)
    },
    // Shadows - Soft, modern shadows (Material-UI requires 25 shadows)
    shadows: [
      'none',
      '0 1px 2px 0 rgb(0 0 0 / 0.05)',
      '0 1px 3px 0 rgb(0 0 0 / 0.1), 0 1px 2px -1px rgb(0 0 0 / 0.1)',
      '0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1)',
      '0 10px 15px -3px rgb(0 0 0 / 0.1), 0 4px 6px -4px rgb(0 0 0 / 0.1)',
      '0 20px 25px -5px rgb(0 0 0 / 0.1), 0 8px 10px -6px rgb(0 0 0 / 0.1)',
      '0 25px 50px -12px rgb(0 0 0 / 0.25)',
      '0 25px 50px -12px rgb(0 0 0 / 0.25)',
      '0 25px 50px -12px rgb(0 0 0 / 0.25)',
      '0 25px 50px -12px rgb(0 0 0 / 0.25)',
      '0 25px 50px -12px rgb(0 0 0 / 0.25)',
      '0 25px 50px -12px rgb(0 0 0 / 0.25)',
      '0 25px 50px -12px rgb(0 0 0 / 0.25)',
      '0 25px 50px -12px rgb(0 0 0 / 0.25)',
      '0 25px 50px -12px rgb(0 0 0 / 0.25)',
      '0 25px 50px -12px rgb(0 0 0 / 0.25)',
      '0 25px 50px -12px rgb(0 0 0 / 0.25)',
      '0 25px 50px -12px rgb(0 0 0 / 0.25)',
      '0 25px 50px -12px rgb(0 0 0 / 0.25)',
      '0 25px 50px -12px rgb(0 0 0 / 0.25)',
      '0 25px 50px -12px rgb(0 0 0 / 0.25)',
      '0 25px 50px -12px rgb(0 0 0 / 0.25)',
      '0 25px 50px -12px rgb(0 0 0 / 0.25)',
      '0 25px 50px -12px rgb(0 0 0 / 0.25)',
      '0 25px 50px -12px rgb(0 0 0 / 0.25)',
      '0 25px 50px -12px rgb(0 0 0 / 0.25)',
    ],
    // Components - Modern styling
    components: {
      // Button
      MuiButton: {
        styleOverrides: {
          root: {
            textTransform: 'none',
            borderRadius: 12, // Rounded-xl
            fontWeight: 600,
            padding: '10px 24px',
            boxShadow: 'none',
            '&:hover': {
              boxShadow: '0 10px 15px -3px rgba(79, 70, 229, 0.3)',
            },
          },
          contained: {
            background: 'linear-gradient(135deg, #4F46E5 0%, #7C3AED 100%)',
            '&:hover': {
              background: 'linear-gradient(135deg, #7C3AED 0%, #4F46E5 100%)',
              boxShadow: '0 15px 25px -5px rgba(79, 70, 229, 0.4)',
            },
          },
          outlined: {
            borderWidth: '2px',
            '&:hover': {
              borderWidth: '2px',
            },
          },
        },
      },
      // Card
      MuiCard: {
        styleOverrides: {
          root: {
            borderRadius: 24, // Rounded-3xl
            boxShadow: '0 4px 12px rgba(0, 0, 0, 0.03)',
            border: isDark ? '1px solid rgba(255, 255, 255, 0.1)' : '1px solid rgba(0, 0, 0, 0.05)',
            transition: 'all 0.3s ease',
            '&:hover': {
              boxShadow: isDark 
                ? '0 20px 25px -5px rgba(129, 140, 248, 0.2)' 
                : '0 10px 30px rgba(0, 0, 0, 0.08)',
            },
          },
        },
      },
      // Paper
      MuiPaper: {
        styleOverrides: {
          root: {
            borderRadius: 16,
            backgroundImage: 'none',
          },
          elevation1: {
            boxShadow: '0 1px 3px 0 rgb(0 0 0 / 0.1), 0 1px 2px -1px rgb(0 0 0 / 0.1)',
          },
          elevation2: {
            boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1)',
          },
          elevation3: {
            boxShadow: '0 10px 15px -3px rgb(0 0 0 / 0.1), 0 4px 6px -4px rgb(0 0 0 / 0.1)',
          },
        },
      },
      // Chip
      MuiChip: {
        styleOverrides: {
          root: {
            borderRadius: 12,
            fontWeight: 600,
            fontSize: '0.75rem',
          },
        },
      },
      // TextField
      MuiTextField: {
        styleOverrides: {
          root: {
            '& .MuiOutlinedInput-root': {
              borderRadius: 12,
              '&:hover fieldset': {
                borderColor: '#4F46E5',
              },
              '&.Mui-focused fieldset': {
                borderWidth: '2px',
                borderColor: '#4F46E5',
              },
            },
          },
        },
      },
      // AppBar
      MuiAppBar: {
        styleOverrides: {
          root: {
            boxShadow: '0 4px 24px rgba(0, 0, 0, 0.06)',
            backdropFilter: 'blur(20px) saturate(180%)',
            WebkitBackdropFilter: 'blur(20px) saturate(180%)',
          },
        },
      },
      // Dialog
      MuiDialog: {
        styleOverrides: {
          paper: {
            borderRadius: 24,
            boxShadow: '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
          },
        },
      },
      // LinearProgress
      MuiLinearProgress: {
        styleOverrides: {
          root: {
            borderRadius: 8,
            height: 8,
          },
        },
      },
      // Tab
      MuiTab: {
        styleOverrides: {
          root: {
            textTransform: 'none',
            fontWeight: 600,
            fontSize: '0.95rem',
            minHeight: 48,
            borderRadius: 12,
            '&.Mui-selected': {
              color: '#4F46E5',
            },
          },
        },
      },
      // Tabs
      MuiTabs: {
        styleOverrides: {
          indicator: {
            borderRadius: 2,
            height: 3,
          },
        },
      },
      // Alert
      MuiAlert: {
        styleOverrides: {
          root: {
            borderRadius: 16,
            fontWeight: 500,
          },
        },
      },
      // Avatar
      MuiAvatar: {
        styleOverrides: {
          root: {
            borderRadius: 12,
          },
        },
      },
      // IconButton
      MuiIconButton: {
        styleOverrides: {
          root: {
            borderRadius: 12,
            transition: 'all 0.2s ease',
            '&:hover': {
              transform: 'scale(1.05)',
            },
          },
        },
      },
      // CssBaseline - Fix scroll issues
      MuiCssBaseline: {
        styleOverrides: {
          html: {
            overflowY: 'scroll !important',
            overflowX: 'hidden !important',
            height: '100%',
            WebkitOverflowScrolling: 'touch',
          },
          body: {
            overflowY: 'scroll !important',
            overflowX: 'hidden !important',
            height: '100%',
            position: 'relative',
          },
          '#root': {
            minHeight: '100%',
            height: 'auto',
            overflowY: 'visible !important',
            overflowX: 'hidden !important',
            position: 'relative',
          },
        },
      },
    },
    // Custom theme properties for project-specific colors
    custom: {
      colors: {
        // AI Diagnostics - Purple
        ai: {
          main: '#8B5CF6', // Purple 500
          light: '#A78BFA', // Purple 400
          dark: '#7C3AED', // Purple 600
        },
        // IoT Vital Signs - Rose (Soft red)
        iot: {
          main: '#F43F5E', // Rose 500
          light: '#FB7185', // Rose 400
          dark: '#E11D48', // Rose 600
        },
        // Blockchain - Vibrant Indigo
        blockchain: {
          main: '#4F46E5', // Indigo 600
          light: '#818CF8', // Indigo 400
          dark: '#3730A3', // Indigo 700
          accent: '#6366F1', // Indigo 500
        },
        // Glass effect for dark mode
        glass: {
          light: 'rgba(255, 255, 255, 0.05)',
          medium: 'rgba(255, 255, 255, 0.1)',
          dark: 'rgba(0, 0, 0, 0.2)',
        },
      },
      // Neon effects for dark mode
      neon: {
        indigo: '#818CF8', // Indigo 400 - For 3D model glow
        purple: '#A78BFA', // Purple 400 - For AI highlights
        sky: '#38BDF8', // Sky Blue 400 - For health indicators
      },
    },
  });
};

// localStorage'dan tema tercihini al
const savedTheme = localStorage.getItem('theme') || 'light';
const theme = getTheme(savedTheme);

export default theme;
export { getTheme };
