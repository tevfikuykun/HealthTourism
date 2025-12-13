import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// PWA plugin will be added after npm install completes
// For now, using basic config to allow project to start

export default defineConfig({
  plugins: [
    react()
    // PWA plugin temporarily disabled - uncomment after: npm install vite-plugin-pwa
    // Uncomment the following after installing vite-plugin-pwa:
    /*
    VitePWA({
      registerType: 'autoUpdate',
      manifest: {
        name: 'Health Tourism',
        short_name: 'HealthTourism',
        theme_color: '#1976d2',
        icons: [
          {
            src: 'pwa-192x192.png',
            sizes: '192x192',
            type: 'image/png'
          }
        ]
      }
    })
    */
  ],
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
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
