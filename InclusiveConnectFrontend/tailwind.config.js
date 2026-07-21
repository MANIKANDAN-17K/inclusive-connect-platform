/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/**/*.{html,ts}"],
  theme: {
    extend: {
      colors: {
        primary: {
          DEFAULT: "#2563EB",
          50:  "#EFF6FF",
          100: "#DBEAFE",
          200: "#BFDBFE",
          300: "#93C5FD",
          400: "#60A5FA",
          500: "#2563EB",
          600: "#1D4ED8",
          700: "#1E40AF",
          800: "#1E3A8A",
          900: "#172554",
          950: "#0F172A",
        },
        secondary: {
          DEFAULT: "#4F46E5",
          50:  "#EEF2FF",
          100: "#E0E7FF",
          200: "#C7D2FE",
          300: "#A5B4FC",
          400: "#818CF8",
          500: "#4F46E5",
          600: "#4338CA",
          700: "#3730A3",
          800: "#312E81",
          900: "#1E1B4B",
        },
        accent: {
          DEFAULT: "#10B981",
          50:  "#ECFDF5",
          100: "#D1FAE5",
          200: "#A7F3D0",
          300: "#6EE7B7",
          400: "#34D399",
          500: "#10B981",
          600: "#059669",
          700: "#047857",
          800: "#065F46",
          900: "#064E3B",
        },
        slate: {
          50:  "#F8FAFC",
          100: "#F1F5F9",
          200: "#E2E8F0",
          300: "#CBD5E1",
          400: "#94A3B8",
          500: "#64748B",
          600: "#475569",
          700: "#334155",
          800: "#1E293B",
          900: "#0F172A",
        },
        surface:        "#FFFFFF",
        background:     "#F8FAFC",
        "text-primary": "#111827",
        "text-secondary": "#6B7280",
        border:         "#E5E7EB",
        success:        "#22C55E",
        error:          "#EF4444",
        warning:        "#F59E0B",
      },

      fontFamily: {
        sans:    ["Inter", "system-ui", "sans-serif"],
        heading: ["Poppins", "Inter", "system-ui", "sans-serif"],
        mono:    ["JetBrains Mono", "Fira Code", "monospace"],
      },

      fontSize: {
        "2xs": ["0.625rem", { lineHeight: "0.875rem" }],
      },

      borderRadius: {
        card:   "16px",
        card2:  "20px",
        card3:  "24px",
        "4xl":  "2rem",
      },

      maxWidth: {
        content: "1440px",
        prose:   "68ch",
      },

      spacing: {
        18: "4.5rem",
        22: "5.5rem",
        88: "22rem",
        112: "28rem",
        128: "32rem",
      },

      boxShadow: {
        soft:        "0 2px 12px rgba(15, 23, 42, 0.05)",
        card:        "0 1px 3px rgba(15, 23, 42, 0.06), 0 8px 24px rgba(15, 23, 42, 0.04)",
        "card-hover":"0 4px 20px rgba(37, 99, 235, 0.10), 0 1px 4px rgba(15, 23, 42, 0.06)",
        "card-lift": "0 12px 40px rgba(37, 99, 235, 0.14), 0 2px 8px rgba(15, 23, 42, 0.06)",
        nav:         "0 1px 0 rgba(15, 23, 42, 0.08)",
        modal:       "0 24px 64px rgba(15, 23, 42, 0.16)",
        btn:         "0 1px 3px rgba(37, 99, 235, 0.3), 0 4px 12px rgba(37, 99, 235, 0.15)",
        "btn-hover": "0 2px 6px rgba(37, 99, 235, 0.4), 0 6px 20px rgba(37, 99, 235, 0.2)",
        "inner-sm":  "inset 0 1px 3px rgba(15, 23, 42, 0.08)",
      },

      backgroundImage: {
        "hero-gradient":    "linear-gradient(135deg, #EFF6FF 0%, #FFFFFF 50%, #EEF2FF 100%)",
        "cta-gradient":     "linear-gradient(135deg, #2563EB 0%, #4F46E5 100%)",
        "card-gradient":    "linear-gradient(145deg, #FFFFFF 0%, #F8FAFC 100%)",
        "primary-gradient": "linear-gradient(135deg, #2563EB, #4F46E5)",
        "accent-gradient":  "linear-gradient(135deg, #10B981, #059669)",
        "mesh-pattern":     "radial-gradient(circle at 20% 20%, rgba(37,99,235,0.08) 0%, transparent 50%), radial-gradient(circle at 80% 10%, rgba(79,70,229,0.06) 0%, transparent 45%), radial-gradient(circle at 50% 80%, rgba(16,185,129,0.04) 0%, transparent 40%)",
      },

      animation: {
        "fade-in":     "fadeIn 0.4s ease-out both",
        "fade-up":     "fadeUp 0.5s ease-out both",
        "fade-down":   "fadeDown 0.3s ease-out both",
        "scale-in":    "scaleIn 0.2s ease-out both",
        "slide-in-r":  "slideInRight 0.3s ease-out both",
        "slide-in-l":  "slideInLeft 0.3s ease-out both",
        "slide-up":    "slideUp 0.35s ease-out both",
        "pulse-slow":  "pulse 3s cubic-bezier(0.4, 0, 0.6, 1) infinite",
        "bounce-soft": "bounceSoft 1.5s ease-in-out infinite",
        "shimmer":     "shimmer 1.8s linear infinite",
        "spin-slow":   "spin 3s linear infinite",
      },

      keyframes: {
        fadeIn: {
          "0%":   { opacity: "0", transform: "translateY(6px)" },
          "100%": { opacity: "1", transform: "translateY(0)" },
        },
        fadeUp: {
          "0%":   { opacity: "0", transform: "translateY(16px)" },
          "100%": { opacity: "1", transform: "translateY(0)" },
        },
        fadeDown: {
          "0%":   { opacity: "0", transform: "translateY(-8px)" },
          "100%": { opacity: "1", transform: "translateY(0)" },
        },
        scaleIn: {
          "0%":   { opacity: "0", transform: "scale(0.94)" },
          "100%": { opacity: "1", transform: "scale(1)" },
        },
        slideInRight: {
          "0%":   { opacity: "0", transform: "translateX(20px)" },
          "100%": { opacity: "1", transform: "translateX(0)" },
        },
        slideInLeft: {
          "0%":   { opacity: "0", transform: "translateX(-20px)" },
          "100%": { opacity: "1", transform: "translateX(0)" },
        },
        slideUp: {
          "0%":   { opacity: "0", transform: "translateY(24px)" },
          "100%": { opacity: "1", transform: "translateY(0)" },
        },
        bounceSoft: {
          "0%, 100%": { transform: "translateY(0)" },
          "50%":      { transform: "translateY(-6px)" },
        },
        shimmer: {
          "0%":   { backgroundPosition: "-1000px 0" },
          "100%": { backgroundPosition: "1000px 0" },
        },
      },

      transitionDuration: {
        250: "250ms",
        350: "350ms",
        400: "400ms",
      },

      transitionTimingFunction: {
        "spring": "cubic-bezier(0.34, 1.56, 0.64, 1)",
        "smooth": "cubic-bezier(0.4, 0, 0.2, 1)",
      },

      backdropBlur: {
        xs: "2px",
      },

      zIndex: {
        60:  "60",
        70:  "70",
        80:  "80",
        90:  "90",
        100: "100",
      },
    },
  },
  plugins: [],
};
