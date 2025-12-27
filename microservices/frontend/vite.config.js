import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import { VitePWA } from 'vite-plugin-pwa'

export default defineConfig({
  optimizeDeps: {
    include: ['react', 'react-dom', 'react-router-dom', 'react-datepicker'],
    exclude: [],
  },
  plugins: [
    react(),
    VitePWA({
      registerType: 'autoUpdate',
      // Disable service worker in development mode
      devOptions: {
        enabled: false,
        type: 'module',
      },
      manifest: {
        name: 'Health Tourism',
        short_name: 'HealthTourism',
        description: 'İstanbul Sağlık Turizmi Platformu',
        theme_color: '#1976d2',
        background_color: '#ffffff',
        display: 'standalone',
        icons: []
      },
      workbox: {
        globPatterns: ['**/*.{js,css,html,ico,png,svg}'],
        // Exclude Vite dev server files from cache
        runtimeCaching: [
          {
            urlPattern: /^https:\/\/localhost:3000\/.*/i,
            handler: 'NetworkOnly',
          },
          {
            urlPattern: /^https:\/\/.*\/@vite\/.*/i,
            handler: 'NetworkOnly',
          },
          {
            urlPattern: /^https:\/\/.*\/@react-refresh\/.*/i,
            handler: 'NetworkOnly',
          },
        ],
      }
    })
  ],
  server: {
    port: 3000,
    host: true, // Allow external connections
    strictPort: false, // Try next available port if 3000 is busy
    hmr: {
      // HMR (Hot Module Replacement) ayarları
      protocol: 'ws',
      host: 'localhost',
      port: 3000,
      clientPort: 3000,
      // Network değişikliklerinde otomatik reconnect
      overlay: true,
    },
    watch: {
      // File system watcher ayarları
      usePolling: false,
      interval: 100,
    },
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
        // WebSocket proxy için
        ws: true,
      }
    }
  },
  build: {
    sourcemap: false,
    minify: 'terser',
    terserOptions: {
      compress: {
        drop_console: true,
        drop_debugger: true
      }
    }
  }
})
