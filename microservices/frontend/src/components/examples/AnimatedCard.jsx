/**
 * AnimatedCard - Material-UI + Tailwind CSS + Framer Motion Örnek Component
 * 
 * Bu component, üç teknolojiyi birlikte nasıl kullanacağınızı gösterir.
 */

import React from 'react';
import { motion } from 'framer-motion';
import { Card, CardContent, Typography, Button } from '@mui/material';
import { fadeInUp, hoverLift } from '../../utils/ui-helpers';

/**
 * AnimatedCard Component
 * 
 * @param {React.ReactNode} children - Card içeriği
 * @param {string} title - Card başlığı
 * @param {string} description - Card açıklaması
 * @param {number} delay - Animasyon gecikmesi (saniye)
 * @param {function} onClick - Tıklama handler'ı
 */
const AnimatedCard = ({ 
  children, 
  title, 
  description, 
  delay = 0,
  onClick 
}) => {
  return (
    <motion.div
      variants={fadeInUp}
      initial="initial"
      animate="animate"
      transition={{ delay }}
      {...hoverLift}
      className="h-full"
    >
      <Card
        className="shadow-lg hover:shadow-xl transition-shadow duration-300 cursor-pointer"
        sx={{
          borderRadius: 3,
          height: '100%',
          display: 'flex',
          flexDirection: 'column',
        }}
        onClick={onClick}
      >
        <CardContent sx={{ p: 3, flexGrow: 1 }}>
          {title && (
            <Typography 
              variant="h6" 
              className="font-bold mb-2"
              sx={{ fontWeight: 700, mb: 2 }}
            >
              {title}
            </Typography>
          )}
          {description && (
            <Typography 
              variant="body2" 
              color="text.secondary"
              className="mb-4"
            >
              {description}
            </Typography>
          )}
          {children}
        </CardContent>
      </Card>
    </motion.div>
  );
};

export default AnimatedCard;


