import React from 'react';
import { FilterSection } from './FilterSidebar';
import { DistanceSlider } from './DistanceSlider';
import { cn } from '../../lib/utils';

/**
 * Hospital Filters Component
 * Şehir, ilçe, havalimanı mesafesi ve uzmanlık alanı filtreleri
 */
export const HospitalFilters = ({ filters, onFilterChange }) => {
  const cities = ['İstanbul', 'Ankara', 'İzmir', 'Antalya', 'Bursa', 'Muğla', 'Adana'];
  const districts = {
    'İstanbul': ['Kadıköy', 'Beşiktaş', 'Şişli', 'Beyoğlu', 'Üsküdar', 'Ataşehir'],
    'Ankara': ['Çankaya', 'Keçiören', 'Yenimahalle', 'Mamak'],
    'İzmir': ['Konak', 'Bornova', 'Karşıyaka', 'Buca'],
    'Antalya': ['Muratpaşa', 'Konyaaltı', 'Kepez'],
  };
  const specialties = ['Kardiyoloji', 'Estetik', 'Diş Hekimliği', 'Onkoloji', 'Ortopedi', 'Göz Hastalıkları', 'Genel Cerrahi'];

  const currentDistricts = filters.city ? districts[filters.city] || [] : [];

  return (
    <div className="space-y-6">
      {/* Şehir Filtresi */}
      <FilterSection title="Şehir">
        <select
          value={filters.city || ''}
          onChange={(e) => onFilterChange('city', e.target.value)}
          className="w-full rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm focus:border-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-500/20"
        >
          <option value="">Tüm Şehirler</option>
          {cities.map((city) => (
            <option key={city} value={city}>
              {city}
            </option>
          ))}
        </select>
      </FilterSection>

      {/* İlçe Filtresi */}
      {filters.city && currentDistricts.length > 0 && (
        <FilterSection title="İlçe">
          <select
            value={filters.district || ''}
            onChange={(e) => onFilterChange('district', e.target.value)}
            className="w-full rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm focus:border-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-500/20"
          >
            <option value="">Tüm İlçeler</option>
            {currentDistricts.map((district) => (
              <option key={district} value={district}>
                {district}
              </option>
            ))}
          </select>
        </FilterSection>
      )}

      {/* Havalimanı Mesafesi - Slider */}
      <FilterSection title="Havalimanı Mesafesi">
        <DistanceSlider
          value={filters.airportDistance || 50}
          onChange={(value) => onFilterChange('airportDistance', value === 50 ? null : value)}
          min={0}
          max={50}
          step={5}
        />
      </FilterSection>

      {/* Uzmanlık Alanı */}
      <FilterSection title="Uzmanlık Alanı">
        <select
          value={filters.specialty || ''}
          onChange={(e) => onFilterChange('specialty', e.target.value)}
          className="w-full rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm focus:border-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-500/20"
        >
          <option value="">Tüm Uzmanlıklar</option>
          {specialties.map((specialty) => (
            <option key={specialty} value={specialty}>
              {specialty}
            </option>
          ))}
        </select>
      </FilterSection>
    </div>
  );
};

