import { clsx } from 'clsx';
import { twMerge } from 'tailwind-merge';

/**
 * Tailwind CSS class'larını birleştirmek için utility fonksiyonu
 * Shadcn/ui pattern'i
 */
export function cn(...inputs) {
  return twMerge(clsx(inputs));
}

