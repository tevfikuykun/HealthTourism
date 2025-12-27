import React from 'react';
import { cn } from '../../lib/utils';
import { motion } from 'framer-motion';

const Skeleton = React.forwardRef(({ className, ...props }, ref) => {
  return (
    <motion.div
      ref={ref}
      className={cn('animate-pulse rounded-md bg-slate-200', className)}
      initial={{ opacity: 0.5 }}
      animate={{ opacity: [0.5, 1, 0.5] }}
      transition={{ duration: 1.5, repeat: Infinity, ease: 'easeInOut' }}
      {...props}
    />
  );
});
Skeleton.displayName = 'Skeleton';

// Pre-built skeleton components for common use cases
export const SkeletonCard = ({ className, ...props }) => (
  <div className={cn('rounded-lg border border-slate-200 p-4', className)} {...props}>
    <Skeleton className="h-4 w-3/4 mb-2" />
    <Skeleton className="h-4 w-1/2 mb-4" />
    <Skeleton className="h-32 w-full mb-2" />
    <Skeleton className="h-4 w-full" />
  </div>
);

export const SkeletonList = ({ count = 3, className, ...props }) => (
  <div className={cn('space-y-4', className)} {...props}>
    {Array.from({ length: count }).map((_, i) => (
      <SkeletonCard key={i} />
    ))}
  </div>
);

export { Skeleton };

