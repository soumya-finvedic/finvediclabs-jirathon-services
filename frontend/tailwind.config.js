module.exports = {
  content: [
    "./src/**/*.{js,vue,jsx,cjs,mjs,ts,tsx,cts,mtsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          50: '#f0f9ff',
          100: '#e0f2fe',
          500: '#0ea5e9',
          600: '#0284c7',
          700: '#0369a1',
          900: '#082f49',
        },
        dark: {
          50: '#f9fafb',
          100: '#f3f4f6',
          200: '#e5e7eb',
          300: '#d1d5db',
          400: '#9ca3af',
          500: '#6b7280',
          600: '#374151',
          700: '#2d3748',
          800: '#1f2937',
          900: '#111827',
          950: '#030712',
        }
      },
      spacing: {
        '128': '32rem',
      }
    },
  },
  plugins: [],
  darkMode: 'class'
}
