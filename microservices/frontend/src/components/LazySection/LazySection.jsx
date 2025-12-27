import React, { useRef } from 'react';
import { useInView, motion } from 'framer-motion';

/**
 * LazySection Component
 * Viewport'a girince render eden lazy loading wrapper
 * react-intersection-observer kullanmadan, Framer Motion'ın useInView hook'u ile
 */
const LazySection = ({ children, fallback = null, threshold = 0.1 }) => {
  const ref = useRef(null);
  const isInView = useInView(ref, { once: true, amount: threshold });

  return (
    <motion.div 
      ref={ref}
      initial={{ opacity: 0 }}
      animate={isInView ? { opacity: 1 } : { opacity: 0 }}
      transition={{ duration: 0.3 }}
    >
      {isInView ? children : fallback}
    </motion.div>
  );
};

export default LazySection;

