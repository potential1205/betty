/* eslint-disable import/no-extraneous-dependencies */
import react from "@vitejs/plugin-react";
import { defineConfig } from "vite";
import tailwindcss from '@tailwindcss/vite'

// https://vitejs.dev/config/
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
