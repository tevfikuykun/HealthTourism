import React from 'react';
import { motion } from 'framer-motion';
import { X, Filter } from 'lucide-react';
import { cn } from '../../lib/utils';
import { Button } from '../ui/Button';

/**
 * Premium Filter Sidebar Component
 * Şehir, ilçe, havalimanı mesafesi ve diğer filtreler için
 */
export const FilterSidebar = ({
  isOpen,
  onClose,
  filters,
  onFilterChange,
  onReset,
  children,
  className,
}) => {
  return (
    <>
      {/* Overlay */}
      {isOpen && (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
          onClick={onClose}
          className="fixed inset-0 z-40 bg-black/50 backdrop-blur-sm lg:hidden"
        />
      )}

      {/* Sidebar */}
      <motion.aside
        initial={{ x: '-100%' }}
        animate={{ x: isOpen ? 0 : '-100%' }}
        transition={{ type: 'spring', damping: 25, stiffness: 200 }}
        className={cn(
          'fixed left-0 top-0 z-50 h-full w-80 bg-white shadow-xl',
          'lg:relative lg:z-auto lg:block lg:translate-x-0',
          !isOpen && 'hidden lg:block',
          className
        )}
      >
        <div className="flex h-full flex-col">
          {/* Header */}
          <div className="flex items-center justify-between border-b border-slate-200 p-4">
            <div className="flex items-center gap-2">
              <Filter className="h-5 w-5 text-primary-600" />
              <h2 className="text-lg font-semibold">Filtreler</h2>
            </div>
            <button
              onClick={onClose}
              className="rounded-lg p-2 text-slate-400 hover:bg-slate-100 hover:text-slate-600 lg:hidden"
            >
              <X className="h-5 w-5" />
            </button>
          </div>

          {/* Filter Content */}
          <div className="flex-1 overflow-y-auto p-4">
            {children}
          </div>

          {/* Footer */}
          <div className="border-t border-slate-200 p-4">
            <Button
              variant="outline"
              onClick={onReset}
              className="w-full"
            >
              Filtreleri Temizle
            </Button>
          </div>
        </div>
      </motion.aside>
    </>
  );
};

/**
 * Filter Section Component
 */
export const FilterSection = ({ title, children, className }) => {
  return (
    <div className={cn('mb-6', className)}>
      <h3 className="mb-3 text-sm font-semibold text-slate-700">{title}</h3>
      <div className="space-y-3">{children}</div>
    </div>
  );
};

/**
 * Filter Toggle Button (Mobile)
 */
export const FilterToggle = ({ onClick, filterCount = 0 }) => {
  return (
    <Button
      variant="outline"
      onClick={onClick}
      className="lg:hidden"
    >
      <Filter className="mr-2 h-4 w-4" />
      Filtreler
      {filterCount > 0 && (
        <span className="ml-2 rounded-full bg-primary-600 px-2 py-0.5 text-xs text-white">
          {filterCount}
        </span>
      )}
    </Button>
  );
};

