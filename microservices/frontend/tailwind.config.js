/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      // Deep-Trust Color Palette
      colors: {
        // Primary - Indigo Blue
        primary: {
          50: '#EEF2FF',
          100: '#E0E7FF',
          200: '#C7D2FE',
          300: '#A5B4FC',
          400: '#818CF8', // Neon accent for dark mode
          500: '#6366F1',
          600: '#4F46E5', // Main primary color
          700: '#4338CA',
          800: '#3730A3',
          900: '#312E81',
        },
        // Secondary - Sky Blue
        secondary: {
          50: '#F0F9FF',
          100: '#E0F2FE',
          200: '#BAE6FD',
          300: '#7DD3FC',
          400: '#38BDF8',
          500: '#0EA5E9', // Main secondary color
          600: '#0284C7',
          700: '#0369A1',
          800: '#075985',
          900: '#0C4A6E',
        },
        // Success - Emerald
        success: {
          50: '#ECFDF5',
          100: '#D1FAE5',
          200: '#A7F3D0',
          300: '#6EE7B7',
          400: '#34D399',
          500: '#10B981', // Main success color
          600: '#059669',
          700: '#047857',
          800: '#065F46',
          900: '#064E3B',
        },
        // Warning - Amber
        warning: {
          50: '#FFFBEB',
          100: '#FEF3C7',
          200: '#FDE68A',
          300: '#FCD34D',
          400: '#FBBF24',
          500: '#F59E0B', // Main warning color
          600: '#D97706',
          700: '#B45309',
          800: '#92400E',
          900: '#78350F',
        },
        // Error - Rose (Soft red for IoT vital signs)
        error: {
          50: '#FFF1F2',
          100: '#FFE4E6',
          200: '#FECDD3',
          300: '#FDA4AF',
          400: '#FB7185',
          500: '#F43F5E', // Main error/IoT color
          600: '#E11D48',
          700: '#BE123C',
          800: '#9F1239',
          900: '#881337',
        },
        // Info - Purple (AI diagnostics)
        info: {
          50: '#FAF5FF',
          100: '#F3E8FF',
          200: '#E9D5FF',
          300: '#D8B4FE',
          400: '#C084FC',
          500: '#A855F7',
          600: '#9333EA',
          700: '#7E22CE',
          800: '#6B21A8',
          900: '#581C87',
        },
        // AI - Purple (for AI diagnostics)
        ai: {
          main: '#8B5CF6',
          light: '#A78BFA',
          dark: '#7C3AED',
        },
        // IoT - Rose (for vital signs)
        iot: {
          main: '#F43F5E',
          light: '#FB7185',
          dark: '#E11D48',
        },
        // Blockchain - Vibrant Indigo
        blockchain: {
          main: '#4F46E5',
          light: '#818CF8',
          dark: '#3730A3',
          accent: '#6366F1',
        },
        // Slate (for surfaces and backgrounds)
        slate: {
          50: '#F8FAFC', // Surface color
          100: '#F1F5F9',
          200: '#E2E8F0',
          300: '#CBD5E1',
          400: '#94A3B8',
          500: '#64748B',
          600: '#475569',
          700: '#334155',
          800: '#1E293B',
          900: '#0F172A', // Dark mode background
        },
        // Glass effect colors
        glass: {
          light: 'rgba(255, 255, 255, 0.05)',
          medium: 'rgba(255, 255, 255, 0.1)',
          dark: 'rgba(0, 0, 0, 0.2)',
        },
        // Neon colors (for dark mode)
        neon: {
          indigo: '#818CF8', // For 3D model glow
          purple: '#A78BFA', // For AI highlights
          sky: '#38BDF8', // For health indicators
        },
      },
      // Border radius
      borderRadius: {
        'xl': '12px',
        '2xl': '16px',
        '3xl': '24px',
      },
      // Box shadows
      boxShadow: {
        'soft': '0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1)',
        'md': '0 10px 15px -3px rgb(0 0 0 / 0.1), 0 4px 6px -4px rgb(0 0 0 / 0.1)',
        'lg': '0 20px 25px -5px rgb(0 0 0 / 0.1), 0 8px 10px -6px rgb(0 0 0 / 0.1)',
        'xl': '0 25px 50px -12px rgb(0 0 0 / 0.25)',
        'indigo': '0 10px 25px -5px rgba(79, 70, 229, 0.3)',
        'neon': '0 0 20px rgba(129, 140, 248, 0.5)',
      },
      // Backdrop blur
      backdropBlur: {
        xs: '2px',
      },
    },
  },
  plugins: [],
  // Prevent conflicts with Material-UI
  corePlugins: {
    preflight: false,
  },
}
