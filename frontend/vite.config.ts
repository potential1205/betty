/* eslint-disable import/no-extraneous-dependencies */
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    react(),
    tailwindcss()
  ],
  build: {
    target: "es2020",
    minify: 'esbuild',
  },
  define: {
    global: {},
    'process.env': {}
  },
  optimizeDeps: {
    include: ['react', 'react-dom']
  }
});