/* eslint-disable import/no-extraneous-dependencies */
import react from "@vitejs/plugin-react";
import { defineConfig } from "vite";
import tailwindcss from '@tailwindcss/vite'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    react({
      babel: {
        plugins: [
          ['babel-plugin-styled-components', {
            displayName: true,
            fileName: false
          }]
        ]
      }
    }),
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
  },
  server: {
    proxy: {
      '/api': {
        target: 'https://j12a609.p.ssafy.io',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path
      }
    }
  }
});
