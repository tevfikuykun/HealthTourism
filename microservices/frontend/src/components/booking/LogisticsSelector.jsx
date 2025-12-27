import React, { useState, useMemo } from 'react';
import { useQuery } from '@tanstack/react-query';
import { motion } from 'framer-motion';
import { 
  Hotel, 
  Plane, 
  Car, 
  Calendar,
  Euro,
  CheckCircle2,
  AlertCircle,
  Info
} from 'lucide-react';
import { 
  accommodationService, 
  flightService, 
  transferService 
} from '../../services/api';
import { cn } from '../../lib/utils';
import { Skeleton } from '../ui/Skeleton';
import { Button } from '../ui/Button';
import { useBookingStore } from '../../store/bookingStore';

/**
 * Logistics Selector Component
 * Uçuş, Otel ve Transfer tercihlerini API'dan çekip fiyatı dinamik güncelleyen bileşen
 * 
 * @param {object} hospital - Seçilen hastane (konaklama yakınlığı için)
 * @param {string} checkInDate - Giriş tarihi
 * @param {string} checkOutDate - Çıkış tarihi
 */
export const LogisticsSelector = ({ hospital, checkInDate, checkOutDate }) => {
  const {
    selectedAccommodation,
    selectedFlight,
    selectedTransfer,
    setSelectedAccommodation,
    setSelectedFlight,
    setSelectedTransfer,
    setCheckInDate,
    setCheckOutDate,
    calculateTotalPrice,
    totalPrice,
  } = useBookingStore();

  const [localCheckIn, setLocalCheckIn] = useState(checkInDate || '');
  const [localCheckOut, setLocalCheckOut] = useState(checkOutDate || '');

  // Gece sayısını hesapla
  const nights = useMemo(() => {
    if (!localCheckIn || !localCheckOut) return 0;
    const checkIn = new Date(localCheckIn);
    const checkOut = new Date(localCheckOut);
    const diff = checkOut - checkIn;
    return Math.ceil(diff / (1000 * 60 * 60 * 24));
  }, [localCheckIn, localCheckOut]);

  // Konaklama seçenekleri
  const { data: accommodations, isLoading: accommodationsLoading } = useQuery({
    queryKey: ['accommodations', hospital?.id],
    queryFn: () => {
      if (hospital?.id) {
        return accommodationService.getNearHospital(hospital.id);
      }
      return accommodationService.getAll();
    },
    enabled: !!hospital,
  });

  // Uçuş seçenekleri
  const { data: flights, isLoading: flightsLoading } = useQuery({
    queryKey: ['flights'],
    queryFn: () => flightService.getAll(),
  });

  // Transfer seçenekleri
  const { data: transfers, isLoading: transfersLoading } = useQuery({
    queryKey: ['transfers'],
    queryFn: () => transferService.getAll(),
  });

  // Tarih değişikliklerini handle et
  const handleCheckInChange = (date) => {
    setLocalCheckIn(date);
    setCheckInDate(date);
    calculateTotalPrice();
  };

  const handleCheckOutChange = (date) => {
    setLocalCheckOut(date);
    setCheckOutDate(date);
    calculateTotalPrice();
  };

  // Konaklama seçildiğinde fiyatı güncelle
  const handleAccommodationSelect = (accommodation) => {
    setSelectedAccommodation(accommodation);
    calculateTotalPrice();
  };

  // Uçuş seçildiğinde fiyatı güncelle
  const handleFlightSelect = (flight) => {
    setSelectedFlight(flight);
    calculateTotalPrice();
  };

  // Transfer seçildiğinde fiyatı güncelle
  const handleTransferSelect = (transfer) => {
    setSelectedTransfer(transfer);
    calculateTotalPrice();
  };

  // Toplam konaklama fiyatı
  const accommodationTotal = useMemo(() => {
    if (!selectedAccommodation || !nights) return 0;
    return (selectedAccommodation.pricePerNight || 0) * nights;
  }, [selectedAccommodation, nights]);

  return (
    <div className="space-y-6">
      {/* Date Selection */}
      <div className="rounded-lg border border-slate-200 bg-slate-50 p-4">
        <div className="mb-4 flex items-center gap-2">
          <Calendar className="h-5 w-5 text-primary-600" />
          <h3 className="text-lg font-semibold">Konaklama Tarihleri</h3>
        </div>
        <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
          <div>
            <label className="mb-2 block text-sm font-medium">Giriş Tarihi</label>
            <input
              type="date"
              value={localCheckIn}
              onChange={(e) => handleCheckInChange(e.target.value)}
              min={new Date().toISOString().split('T')[0]}
              className="w-full rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm focus:border-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-500/20"
            />
          </div>
          <div>
            <label className="mb-2 block text-sm font-medium">Çıkış Tarihi</label>
            <input
              type="date"
              value={localCheckOut}
              onChange={(e) => handleCheckOutChange(e.target.value)}
              min={localCheckIn || new Date().toISOString().split('T')[0]}
              className="w-full rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm focus:border-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-500/20"
            />
          </div>
        </div>
        {nights > 0 && (
          <div className="mt-3 flex items-center gap-2 text-sm text-slate-600">
            <Info className="h-4 w-4" />
            <span>{nights} gece konaklama</span>
          </div>
        )}
      </div>

      {/* Accommodation Selection */}
      <div>
        <div className="mb-4 flex items-center gap-2">
          <Hotel className="h-5 w-5 text-primary-600" />
          <h3 className="text-lg font-semibold">Konaklama Seçimi</h3>
        </div>

        {accommodationsLoading ? (
          <div className="space-y-3">
            {[1, 2, 3].map((i) => (
              <Skeleton key={i} className="h-24 w-full" />
            ))}
          </div>
        ) : (
          <div className="space-y-3">
            {(accommodations?.data || []).map((accommodation) => {
              const isSelected = selectedAccommodation?.id === accommodation.id;
              const totalPrice = (accommodation.pricePerNight || 0) * nights;

              return (
                <motion.button
                  key={accommodation.id}
                  onClick={() => handleAccommodationSelect(accommodation)}
                  className={cn(
                    'w-full rounded-lg border-2 p-4 text-left transition-all',
                    isSelected
                      ? 'border-primary-600 bg-primary-50'
                      : 'border-slate-200 hover:border-primary-300'
                  )}
                  whileHover={{ scale: 1.01 }}
                  whileTap={{ scale: 0.99 }}
                >
                  <div className="flex items-start justify-between">
                    <div className="flex-1">
                      <div className="flex items-center gap-2">
                        <h4 className="font-semibold">{accommodation.name}</h4>
                        {isSelected && (
                          <CheckCircle2 className="h-4 w-4 text-primary-600" />
                        )}
                      </div>
                      <p className="mt-1 text-sm text-slate-600">
                        {accommodation.address || accommodation.location}
                      </p>
                      {accommodation.rating && (
                        <div className="mt-2 flex items-center gap-1 text-sm">
                          <span className="font-medium">{accommodation.rating}</span>
                          <span className="text-slate-500">/ 5.0</span>
                        </div>
                      )}
                    </div>
                    <div className="ml-4 text-right">
                      <div className="text-sm text-slate-500">Gece başına</div>
                      <div className="text-lg font-bold text-primary-600">
                        €{accommodation.pricePerNight || 0}
                      </div>
                      {nights > 0 && (
                        <div className="mt-1 text-xs text-slate-500">
                          Toplam: €{totalPrice}
                        </div>
                      )}
                    </div>
                  </div>
                </motion.button>
              );
            })}
          </div>
        )}
      </div>

      {/* Flight Selection */}
      <div>
        <div className="mb-4 flex items-center gap-2">
          <Plane className="h-5 w-5 text-primary-600" />
          <h3 className="text-lg font-semibold">Uçuş Seçimi (Opsiyonel)</h3>
        </div>

        {flightsLoading ? (
          <div className="space-y-3">
            {[1, 2].map((i) => (
              <Skeleton key={i} className="h-20 w-full" />
            ))}
          </div>
        ) : (
          <div className="space-y-3">
            {(flights?.data || []).slice(0, 5).map((flight) => {
              const isSelected = selectedFlight?.id === flight.id;

              return (
                <motion.button
                  key={flight.id}
                  onClick={() => handleFlightSelect(flight)}
                  className={cn(
                    'w-full rounded-lg border-2 p-4 text-left transition-all',
                    isSelected
                      ? 'border-primary-600 bg-primary-50'
                      : 'border-slate-200 hover:border-primary-300'
                  )}
                  whileHover={{ scale: 1.01 }}
                  whileTap={{ scale: 0.99 }}
                >
                  <div className="flex items-center justify-between">
                    <div className="flex-1">
                      <div className="flex items-center gap-2">
                        <span className="font-semibold">
                          {flight.departureCity} → {flight.arrivalCity}
                        </span>
                        {isSelected && (
                          <CheckCircle2 className="h-4 w-4 text-primary-600" />
                        )}
                      </div>
                      <p className="mt-1 text-sm text-slate-600">
                        {flight.airline || 'Havayolu'} • {flight.flightClass || 'Economy'}
                      </p>
                    </div>
                    <div className="ml-4 text-right">
                      <div className="text-lg font-bold text-primary-600">
                        €{flight.price || 0}
                      </div>
                    </div>
                  </div>
                </motion.button>
              );
            })}
          </div>
        )}
      </div>

      {/* Transfer Selection */}
      <div>
        <div className="mb-4 flex items-center gap-2">
          <Car className="h-5 w-5 text-primary-600" />
          <h3 className="text-lg font-semibold">Transfer Seçimi</h3>
        </div>

        {transfersLoading ? (
          <div className="space-y-3">
            {[1, 2, 3].map((i) => (
              <Skeleton key={i} className="h-20 w-full" />
            ))}
          </div>
        ) : (
          <div className="space-y-3">
            {(transfers?.data || []).map((transfer) => {
              const isSelected = selectedTransfer?.id === transfer.id;

              return (
                <motion.button
                  key={transfer.id}
                  onClick={() => handleTransferSelect(transfer)}
                  className={cn(
                    'w-full rounded-lg border-2 p-4 text-left transition-all',
                    isSelected
                      ? 'border-primary-600 bg-primary-50'
                      : 'border-slate-200 hover:border-primary-300'
                  )}
                  whileHover={{ scale: 1.01 }}
                  whileTap={{ scale: 0.99 }}
                >
                  <div className="flex items-center justify-between">
                    <div className="flex-1">
                      <div className="flex items-center gap-2">
                        <span className="font-semibold">{transfer.serviceType}</span>
                        {isSelected && (
                          <CheckCircle2 className="h-4 w-4 text-primary-600" />
                        )}
                      </div>
                      <p className="mt-1 text-sm text-slate-600">
                        {transfer.description || 'Havalimanı transferi'}
                      </p>
                    </div>
                    <div className="ml-4 text-right">
                      <div className="text-lg font-bold text-primary-600">
                        €{transfer.price || 0}
                      </div>
                    </div>
                  </div>
                </motion.button>
              );
            })}
          </div>
        )}
      </div>

      {/* Price Summary */}
      {(selectedAccommodation || selectedFlight || selectedTransfer) && (
        <div className="rounded-lg border-2 border-primary-200 bg-primary-50 p-4">
          <div className="mb-2 flex items-center gap-2">
            <Euro className="h-5 w-5 text-primary-600" />
            <h3 className="text-lg font-semibold">Fiyat Özeti</h3>
          </div>
          <div className="space-y-2 text-sm">
            {selectedAccommodation && (
              <div className="flex justify-between">
                <span>Konaklama ({nights} gece)</span>
                <span className="font-medium">€{accommodationTotal}</span>
              </div>
            )}
            {selectedFlight && (
              <div className="flex justify-between">
                <span>Uçuş</span>
                <span className="font-medium">€{selectedFlight.price || 0}</span>
              </div>
            )}
            {selectedTransfer && (
              <div className="flex justify-between">
                <span>Transfer</span>
                <span className="font-medium">€{selectedTransfer.price || 0}</span>
              </div>
            )}
            <div className="mt-3 border-t border-primary-200 pt-2">
              <div className="flex justify-between text-lg font-bold">
                <span>Toplam</span>
                <span className="text-primary-600">€{totalPrice}</span>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

