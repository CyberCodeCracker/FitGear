/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    extend: {
      colors: {
        bg:       '#111827',
        card:     '#1F2937',
        'card-2': '#374151',
        accent:   '#22C55E',
        'accent-hover': '#16A34A',
        danger:   '#EF4444',
        warning:  '#F59E0B',
        info:     '#3B82F6',
      },
      fontFamily: {
        sans: ['Inter', 'ui-sans-serif', 'system-ui'],
      },
    },
  },
  plugins: [],
};
