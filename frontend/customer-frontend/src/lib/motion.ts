// // // src / lib / motion.ts
export const motionConfig = {
  durationFast: 0.15,
  durationBase: 0.25,
  durationSlow: 0.4,

  easeOut: [0.16, 1, 0.3, 1],
  easeInOut: [0.65, 0, 0.35, 1],

  page: {
    initial: { opacity: 0, y: 8 },
    animate: { opacity: 1, y: 0 },
    exit: { opacity: 0, y: -6 },
  },

  card: {
    hover: { y: -4, scale: 1.01 },
    tap: { scale: 0.98 },
  },

  fade: {
    initial: { opacity: 0 },
    animate: { opacity: 1 },
  },
};