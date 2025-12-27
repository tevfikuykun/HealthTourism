import React from 'react';
import { useQuery } from '@tanstack/react-query';
import { motion } from 'framer-motion';
import { Link } from 'react-router-dom';
import { 
  MapPin, 
  Star, 
  Award, 
  Package,
  Heart,
  ExternalLink 
} from 'lucide-react';
import { reviewService, packageService } from '../../services/api';
import { cn } from '../../lib/utils';
import { Button } from '../ui/Button';
import { Skeleton } from '../ui/Skeleton';
import { cardHover } from '../../lib/motion';

/**
 * Enhanced Hospital Card Component
 * Review API entegrasyonu ve paket butonu ile
 * 
 * @param {object} hospital - Hastane verisi
 * @param {function} onPackageClick - Paketleri gör butonuna tıklandığında
 */
export const HospitalCardEnhanced = ({ hospital, onPackageClick }) => {
  // Review verilerini çek
  const { data: reviewsData, isLoading: reviewsLoading } = useQuery({
    queryKey: ['reviews', 'hospital', hospital.id],
    queryFn: () => reviewService.getByService(hospital.id, 'hospital'),
    enabled: !!hospital.id,
  });

  // Paket sayısını çek
  const { data: packagesData } = useQuery({
    queryKey: ['packages', 'hospital', hospital.id],
    queryFn: () => packageService.getAll({ hospitalId: hospital.id }),
    enabled: !!hospital.id,
  });

  const reviews = reviewsData?.data || [];
  const averageRating = reviews.length > 0
    ? reviews.reduce((sum, review) => sum + review.rating, 0) / reviews.length
    : hospital.rating || 0;
  
  const reviewCount = reviews.length;
  const packageCount = packagesData?.data?.length || 0;

  return (
    <motion.div
      variants={cardHover}
      whileHover="whileHover"
      whileTap="whileTap"
      className="group relative overflow-hidden rounded-xl border border-slate-200 bg-white shadow-sm transition-all hover:shadow-lg"
    >
      {/* Image */}
      <div className="relative h-48 w-full overflow-hidden bg-slate-100">
        {hospital.image ? (
          <img
            src={hospital.image}
            alt={hospital.name}
            className="h-full w-full object-cover transition-transform duration-300 group-hover:scale-105"
          />
        ) : (
          <div className="flex h-full w-full items-center justify-center bg-gradient-to-br from-primary-100 to-primary-200">
            <Award className="h-12 w-12 text-primary-400" />
          </div>
        )}

        {/* Badge */}
        {hospital.specialty && (
          <div className="absolute left-3 top-3">
            <span className="rounded-full bg-primary-600 px-3 py-1 text-xs font-semibold text-white">
              {hospital.specialty}
            </span>
          </div>
        )}

        {/* Favorite Button */}
        <button className="absolute right-3 top-3 rounded-full bg-white/90 p-2 shadow-md transition-all hover:bg-white">
          <Heart className="h-4 w-4 text-slate-600" />
        </button>
      </div>

      {/* Content */}
      <div className="p-5">
        {/* Title */}
        <h3 className="mb-2 text-lg font-bold text-slate-900 line-clamp-1">
          {hospital.name}
        </h3>

        {/* Location */}
        <div className="mb-3 flex items-center gap-1 text-sm text-slate-600">
          <MapPin className="h-4 w-4" />
          <span>
            {hospital.city}
            {hospital.district && `, ${hospital.district}`}
          </span>
        </div>

        {/* Rating & Reviews */}
        <div className="mb-4 flex items-center gap-2">
          {reviewsLoading ? (
            <Skeleton className="h-5 w-24" />
          ) : (
            <>
              <div className="flex items-center gap-1">
                <Star className="h-4 w-4 fill-warning-400 text-warning-400" />
                <span className="text-sm font-semibold text-slate-900">
                  {averageRating.toFixed(1)}
                </span>
              </div>
              {reviewCount > 0 && (
                <span className="text-sm text-slate-500">
                  ({reviewCount} {reviewCount === 1 ? 'değerlendirme' : 'değerlendirme'})
                </span>
              )}
            </>
          )}
        </div>

        {/* Accreditations */}
        {hospital.accreditations && hospital.accreditations.length > 0 && (
          <div className="mb-4 flex flex-wrap gap-2">
            {hospital.accreditations.slice(0, 3).map((acc, index) => (
              <span
                key={index}
                className="rounded-md bg-slate-100 px-2 py-1 text-xs text-slate-700"
              >
                {acc}
              </span>
            ))}
            {hospital.accreditations.length > 3 && (
              <span className="rounded-md bg-slate-100 px-2 py-1 text-xs text-slate-700">
                +{hospital.accreditations.length - 3}
              </span>
            )}
          </div>
        )}

        {/* Actions */}
        <div className="flex gap-2">
          <Button
            variant="outline"
            size="sm"
            asChild
            className="flex-1"
          >
            <Link to={`/hospitals/${hospital.slug || hospital.id}`}>
              Detayları Gör
              <ExternalLink className="ml-2 h-3 w-3" />
            </Link>
          </Button>
          
          {packageCount > 0 && (
            <Button
              size="sm"
              onClick={() => onPackageClick?.(hospital)}
              className="flex-1"
            >
              <Package className="mr-2 h-3 w-3" />
              Paketleri Gör ({packageCount})
            </Button>
          )}
        </div>
      </div>
    </motion.div>
  );
};

