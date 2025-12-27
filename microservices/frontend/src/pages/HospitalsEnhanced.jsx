// src/pages/HospitalsEnhanced.jsx
// Gelişmiş filtreleme ve Query parametreleri ile entegre Hospitals sayfası
import React, { useState, useEffect, useMemo } from 'react';
import { useQuery } from '@tanstack/react-query';
import { useSearchParams } from 'react-router-dom';
import { hospitalService } from '../services/api';
import { Container, Box, Typography, Grid } from '@mui/material';
import { motion, AnimatePresence } from 'framer-motion';
import { SearchBar } from '../components/discovery/SearchBar';
import { FilterSidebar, FilterToggle } from '../components/discovery/FilterSidebar';
import { HospitalFilters } from '../components/discovery/HospitalFilters';
import { Skeleton, SkeletonList } from '../components/ui/Skeleton';
import { HospitalCardEnhanced } from '../components/discovery/HospitalCardEnhanced';
import Pagination from '../components/Pagination';
import EmptyState from '../components/EmptyState';
import { useTranslation } from 'react-i18next';
import { staggerContainer, staggerItem } from '../lib/motion';

function HospitalsEnhanced() {
  const { t } = useTranslation();
  const [searchParams, setSearchParams] = useSearchParams();
  const [isFilterOpen, setIsFilterOpen] = useState(false);
  const [page, setPage] = useState(1);
  const itemsPerPage = 12;

  // Query parametrelerinden filtreleri oku
  const filters = useMemo(() => ({
    search: searchParams.get('search') || '',
    city: searchParams.get('city') || '',
    district: searchParams.get('district') || '',
    specialty: searchParams.get('specialty') || '',
    airportDistance: searchParams.get('airportDistance') 
      ? Number(searchParams.get('airportDistance')) 
      : null,
  }), [searchParams]);

  // Filtre değişikliğini URL'e yaz
  const handleFilterChange = (key, value) => {
    const newParams = new URLSearchParams(searchParams);
    
    if (value === null || value === '' || value === undefined) {
      newParams.delete(key);
    } else {
      newParams.set(key, String(value));
    }

    // Şehir değiştiğinde ilçeyi temizle
    if (key === 'city') {
      newParams.delete('district');
    }

    setSearchParams(newParams, { replace: true });
    setPage(1); // Filtre değiştiğinde sayfayı sıfırla
  };

  // Tüm filtreleri temizle
  const handleResetFilters = () => {
    setSearchParams({}, { replace: true });
    setPage(1);
  };

  // Aktif filtre sayısını hesapla
  const activeFilterCount = useMemo(() => {
    return Object.values(filters).filter(
      (value) => value !== '' && value !== null && value !== undefined
    ).length;
  }, [filters]);

  // Backend API entegrasyonu - Query parametreleri ile
  const { data, isLoading, error, isFetching } = useQuery({
    queryKey: ['hospitals', filters, page],
    queryFn: () => {
      const params = {
        page: page - 1,
        size: itemsPerPage,
      };
      
      if (filters.search) params.search = filters.search;
      if (filters.city) params.city = filters.city;
      if (filters.district) params.district = filters.district;
      if (filters.specialty) params.specialty = filters.specialty;
      if (filters.airportDistance) params.airportDistance = filters.airportDistance;
      
      return hospitalService.getAll(params);
    },
    // SWR stratejisi: Cache'den göster, arka planda güncelle
    staleTime: 5 * 60 * 1000, // 5 dakika
  });

  const hospitals = data?.data?.content || data?.data || [];
  const totalPages = data?.data?.totalPages || 1;
  const totalItems = data?.data?.totalElements || hospitals.length;

  const handlePageChange = (newPage) => {
    setPage(newPage);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  return (
    <Container maxWidth="lg" sx={{ py: 5 }}>
      {/* Header */}
      <motion.div
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        className="mb-8"
      >
        <Typography variant="h3" component="h1" gutterBottom sx={{ fontWeight: 'bold', mb: 1 }}>
          {t('globalStandards')}
        </Typography>
        <Typography variant="h6" color="text.secondary">
          {t('discoverHospitals')}
        </Typography>
      </motion.div>

      {/* Search Bar ve Filter Toggle */}
      <div className="mb-6 flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
        <div className="flex-1">
          <SearchBar
            value={filters.search}
            onChange={(value) => handleFilterChange('search', value)}
            placeholder={t('searchHospital', 'Hastane ara...')}
          />
        </div>
        <FilterToggle
          onClick={() => setIsFilterOpen(!isFilterOpen)}
          filterCount={activeFilterCount}
        />
      </div>

      <div className="flex gap-6">
        {/* Filter Sidebar */}
        <FilterSidebar
          isOpen={isFilterOpen}
          onClose={() => setIsFilterOpen(false)}
          filters={filters}
          onFilterChange={handleFilterChange}
          onReset={handleResetFilters}
        >
          <HospitalFilters
            filters={filters}
            onFilterChange={handleFilterChange}
          />
        </FilterSidebar>

        {/* Main Content */}
        <div className="flex-1">
          {isLoading ? (
            <SkeletonList count={6} />
          ) : error ? (
            <EmptyState
              title="Hata oluştu"
              description={t('errorLoading', 'Veriler yüklenirken bir hata oluştu.')}
            />
          ) : (
            <>
              {/* Results Count */}
              <motion.div
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                className="mb-4"
              >
                <Typography variant="subtitle1" sx={{ fontWeight: 'bold' }}>
                  {totalItems} {t('hospitalsFound', 'Hastane Bulundu')}
                  {isFetching && (
                    <span className="ml-2 text-sm text-slate-500">
                      (Güncelleniyor...)
                    </span>
                  )}
                </Typography>
              </motion.div>

              {/* Hospitals Grid */}
              {hospitals.length > 0 ? (
                <motion.div
                  variants={staggerContainer}
                  initial="initial"
                  animate="animate"
                >
                  <Grid container spacing={4}>
                    <AnimatePresence>
                      {hospitals.map((hospital) => (
                        <Grid item xs={12} sm={6} lg={4} key={hospital.id}>
                          <motion.div variants={staggerItem}>
                            <HospitalCardEnhanced hospital={hospital} />
                          </motion.div>
                        </Grid>
                      ))}
                    </AnimatePresence>
                  </Grid>

                  {/* Pagination */}
                  {totalPages > 1 && (
                    <Box sx={{ mt: 4 }}>
                      <Pagination
                        currentPage={page}
                        totalPages={totalPages}
                        totalItems={totalItems}
                        itemsPerPage={itemsPerPage}
                        onPageChange={handlePageChange}
                      />
                    </Box>
                  )}
                </motion.div>
              ) : (
                <EmptyState
                  title={t('noHospitalsFound', 'Hastane bulunamadı')}
                  description={t('noResultsDescription', 'Aradığınız kriterlere uygun hastane bulunamadı. Filtreleri değiştirmeyi deneyin.')}
                />
              )}
            </>
          )}
        </div>
      </div>
    </Container>
  );
}

export default HospitalsEnhanced;

