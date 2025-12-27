import React from 'react';
import { motion } from 'framer-motion';
import { Link } from 'react-router-dom';
import { Search, Home, Package, ArrowRight } from 'lucide-react';
import { cn } from '../../lib/utils';
import { Button } from '../ui/Button';
import { fadeIn } from '../../lib/motion';

/**
 * Enhanced Empty State Component
 * Arama sonucu bulunamadığında kullanıcıyı yönlendiren nazik bir uyarı
 * 
 * @param {string} title - Başlık
 * @param {string} description - Açıklama
 * @param {boolean} showActions - Aksiyon butonlarını göster
 */
export const EmptyStateEnhanced = ({
  title = 'Sonuç bulunamadı',
  description = 'Aradığınız kriterlere uygun sonuç bulunamadı. Filtreleri değiştirmeyi deneyin.',
  showActions = true,
  className,
}) => {
  return (
    <motion.div
      initial="initial"
      animate="animate"
      variants={fadeIn}
      className={cn(
        'flex flex-col items-center justify-center rounded-xl border border-slate-200 bg-white p-12 text-center',
        className
      )}
    >
      {/* Icon */}
      <motion.div
        initial={{ scale: 0 }}
        animate={{ scale: 1 }}
        transition={{ delay: 0.2, type: 'spring' }}
        className="mb-6 rounded-full bg-slate-100 p-6"
      >
        <Search className="h-12 w-12 text-slate-400" />
      </motion.div>

      {/* Title */}
      <h3 className="mb-2 text-xl font-bold text-slate-900">{title}</h3>

      {/* Description */}
      <p className="mb-8 max-w-md text-slate-600">{description}</p>

      {/* Actions */}
      {showActions && (
        <div className="flex flex-wrap items-center justify-center gap-3">
          <Button variant="outline" asChild>
            <Link to="/">
              <Home className="mr-2 h-4 w-4" />
              Ana Sayfaya Dön
            </Link>
          </Button>

          <Button asChild>
            <Link to="/packages">
              <Package className="mr-2 h-4 w-4" />
              Popüler Paketleri Gör
              <ArrowRight className="ml-2 h-4 w-4" />
            </Link>
          </Button>
        </div>
      )}
    </motion.div>
  );
};

