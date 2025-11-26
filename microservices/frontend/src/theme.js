// src/theme.js
import { createTheme } from '@mui/material/styles';

const theme = createTheme({
    palette: {
        primary: {
            main: '#007A8A', // Derin Mavi/Turkuaz (Güven ve Profesyonellik)
            light: '#E0F7FA',
            dark: '#004D40',
        },
        secondary: {
            main: '#FF9800', // Parlak Turuncu (Aksiyon ve Sıcaklık)
            light: '#FFB74D',
            dark: '#F57C00',
        },
        error: {
            main: '#D32F2F',
        },
        background: {
            default: '#F8F9FA', // Açık Gri Arka Plan
            paper: '#FFFFFF',
        },
        text: {
            primary: '#212121',
            secondary: '#757575',
        }
    },
    typography: {
        fontFamily: [
            'Poppins',
            'Arial',
            'sans-serif',
        ].join(','),
        h3: {
            fontWeight: 700,
        },
        button: {
            textTransform: 'none',
            fontWeight: 600,
        }
    },
    components: {
        MuiButton: {
            defaultProps: {
                disableElevation: true,
            },
        },
        MuiCard: {
            styleOverrides: {
                root: {
                    borderRadius: 12,
                },
            },
        },
    }
});

export default theme;