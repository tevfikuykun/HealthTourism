// src/pages/DiscoveryPage.jsx
// Enterprise Discovery Page - Tam entegre edilmiş versiyon
import React, { useState, useMemo, useEffect } from 'react';
import { useQuery } from '@tanstack/react-query';
import { useSearchParams } from 'react-router-dom';
import { hospitalService } from '../services/api';
import { Container } from '@mui/material';
import { motion, AnimatePresence } from 'framer-motion';
import { SearchBar } from '../components/discovery/SearchBar';
import { FilterSidebar, FilterToggle } from '../components/discovery/FilterSidebar';
import { HospitalFilters } from '../components/discovery/HospitalFilters';
import { HospitalCardEnhanced } from '../components/discovery/HospitalCardEnhanced';
import { EmptyStateEnhanced } from '../components/discovery/EmptyStateEnhanced';
import { SkeletonList } from '../components/ui/Skeleton';
import Pagination from '../components/Pagination';
import { BookingWizard } from '../components/booking/BookingWizard';
import { useTranslation } from 'react-i18next';
import { staggerContainer, staggerItem } from '../lib/motion';
import { useDebounce } from '../hooks/useDebounce';

/**
 * Discovery Page - Enterprise Özellikler ile
 * 
 * Özellikler:
 * - Debounced Search (500ms)
 * - URL Sync (State Management)
 * - Skeleton Loaders
 * - React Query & SWR Strategy
 * - Gelişmiş Filtreleme
 */
function DiscoveryPage() {
  const { t } = useTranslation();
  const [searchParams, setSearchParams] = useSearchParams();
  const [isFilterOpen, setIsFilterOpen] = useState(false);
  const [page, setPage] = useState(1);
  const [isBookingWizardOpen, setIsBookingWizardOpen] = useState(false);
  const [selectedHospital, setSelectedHospital] = useState(null);
  const itemsPerPage = 12;

  // URL'den filtreleri oku
  const filters = useMemo(() => ({
    search: searchParams.get('search') || '',
    city: searchParams.get('city') || '',
    district: searchParams.get('district') || '',
    specialty: searchParams.get('specialty') || '',
    airportDistance: searchParams.get('airportDistance')
      ? Number(searchParams.get('airportDistance'))
      : null,
  }), [searchParams]);

  // Search için local state (UI güncellemesi için)
  const [localSearch, setLocalSearch] = useState(filters.search);
  
  // Debounced search değeri
  const debouncedSearch = useDebounce(localSearch, 500);

  // Debounced search değiştiğinde URL'i güncelle
  useEffect(() => {
    if (debouncedSearch !== filters.search) {
      handleFilterChange('search', debouncedSearch);
    }
  }, [debouncedSearch]);

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
    setLocalSearch('');
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

  const handlePackageClick = (hospital) => {
    setSelectedHospital(hospital);
    setIsBookingWizardOpen(true);
  };

  return (
    <>
      <Container maxWidth="lg" sx={{ py: 5 }}>
        {/* Header */}
        <motion.div
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          className="mb-8"
        >
          <h1 className="mb-2 text-3xl font-bold text-slate-900">
            {t('globalStandards', 'Küresel Standartlarda Hastaneler')}
          </h1>
          <p className="text-lg text-slate-600">
            {t('discoverHospitals', 'Dünya standartlarında sağlık hizmeti sunan hastaneleri keşfedin')}
          </p>
        </motion.div>

        {/* Search Bar ve Filter Toggle */}
        <div className="mb-6 flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
          <div className="flex-1">
            <SearchBar
              value={localSearch}
              onImmediateChange={setLocalSearch}
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
            {/* Results Count & Loading Indicator */}
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              className="mb-4 flex items-center justify-between"
            >
              <div>
                <span className="text-sm font-semibold text-slate-900">
                  {isLoading ? (
                    <span className="text-slate-400">Yükleniyor...</span>
                  ) : (
                    <>
                      {totalItems} {t('hospitalsFound', 'Hastane Bulundu')}
                      {isFetching && (
                        <span className="ml-2 text-xs text-slate-500">
                          (Güncelleniyor...)
                        </span>
                      )}
                    </>
                  )}
                </span>
              </div>
            </motion.div>

            {/* Hospitals Grid */}
            {isLoading ? (
              <SkeletonList count={6} />
            ) : error ? (
              <EmptyStateEnhanced
                title="Hata oluştu"
                description={t('errorLoading', 'Veriler yüklenirken bir hata oluştu. Lütfen tekrar deneyin.')}
              />
            ) : hospitals.length > 0 ? (
              <motion.div
                variants={staggerContainer}
                initial="initial"
                animate="animate"
              >
                <div className="grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-3">
                  <AnimatePresence>
                    {hospitals.map((hospital) => (
                      <motion.div
                        key={hospital.id}
                        variants={staggerItem}
                        layout
                      >
                        <HospitalCardEnhanced
                          hospital={hospital}
                          onPackageClick={handlePackageClick}
                        />
                      </motion.div>
                    ))}
                  </AnimatePresence>
                </div>

                {/* Pagination */}
                {totalPages > 1 && (
                  <div className="mt-8">
                    <Pagination
                      currentPage={page}
                      totalPages={totalPages}
                      totalItems={totalItems}
                      itemsPerPage={itemsPerPage}
                      onPageChange={handlePageChange}
                    />
                  </div>
                )}
              </motion.div>
            ) : (
              <EmptyStateEnhanced
                title={t('noHospitalsFound', 'Hastane bulunamadı')}
                description={t(
                  'noResultsDescription',
                  'Aradığınız kriterlere uygun hastane bulunamadı. Filtreleri değiştirmeyi veya arama terimini güncellemeyi deneyin.'
                )}
              />
            )}
          </div>
        </div>
      </Container>

      {/* Booking Wizard */}
      {selectedHospital && (
        <BookingWizard
          isOpen={isBookingWizardOpen}
          onClose={() => {
            setIsBookingWizardOpen(false);
            setSelectedHospital(null);
          }}
          initialData={{ hospitalId: selectedHospital.id }}
        />
      )}
    </>
  );
}

export default DiscoveryPage;

