import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { useQuery } from '@tanstack/react-query';
import { 
  hospitalService, 
  doctorService, 
  accommodationService, 
  transferService,
  reservationService 
} from '../../services/api';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from '../ui/Dialog';
import { Button } from '../ui/Button';
import { Skeleton } from '../ui/Skeleton';
import { ChevronRight, ChevronLeft, Check, Building2, User, Hotel, Car } from 'lucide-react';
import { cn } from '../../lib/utils';
import { slideUp, scale } from '../../lib/motion';

/**
 * Multi-Step Booking Wizard
 * 3 Adımlı Rezervasyon Sihirbazı
 * 
 * Adım 1: Tedavi/Doktor Seçimi
 * Adım 2: Konaklama & Transfer Detayları
 * Adım 3: Özet & Onay
 */
export const BookingWizard = ({ isOpen, onClose, initialData = {} }) => {
  const [currentStep, setCurrentStep] = useState(1);
  const [bookingData, setBookingData] = useState({
    hospitalId: initialData.hospitalId || null,
    doctorId: initialData.doctorId || null,
    treatmentType: initialData.treatmentType || '',
    accommodationId: null,
    transferId: null,
    checkIn: null,
    checkOut: null,
    notes: '',
  });
  const [isSubmitting, setIsSubmitting] = useState(false);

  const totalSteps = 3;

  const handleNext = () => {
    if (currentStep < totalSteps) {
      setCurrentStep(currentStep + 1);
    }
  };

  const handleBack = () => {
    if (currentStep > 1) {
      setCurrentStep(currentStep - 1);
    }
  };

  const handleDataChange = (key, value) => {
    setBookingData((prev) => ({ ...prev, [key]: value }));
  };

  const handleSubmit = async () => {
    setIsSubmitting(true);
    try {
      // Rezervasyon oluştur
      const reservationData = {
        hospitalId: bookingData.hospitalId,
        doctorId: bookingData.doctorId,
        treatmentType: bookingData.treatmentType,
        accommodationId: bookingData.accommodationId,
        transferId: bookingData.transferId,
        checkIn: bookingData.checkIn,
        checkOut: bookingData.checkOut,
        notes: bookingData.notes,
      };

      await reservationService.create(reservationData);
      
      // Başarılı - sonraki adıma geç veya kapat
      onClose();
      // Toast notification göster (react-toastify ile)
      if (window.toast) {
        window.toast.success('Rezervasyonunuz başarıyla oluşturuldu!');
      }
    } catch (error) {
      console.error('Rezervasyon hatası:', error);
      if (window.toast) {
        window.toast.error('Rezervasyon oluşturulurken bir hata oluştu.');
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleClose = () => {
    setCurrentStep(1);
    setBookingData({
      hospitalId: null,
      doctorId: null,
      treatmentType: '',
      accommodationId: null,
      transferId: null,
      checkIn: null,
      checkOut: null,
      notes: '',
    });
    onClose();
  };

  const canProceed = () => {
    switch (currentStep) {
      case 1:
        return bookingData.hospitalId && bookingData.doctorId && bookingData.treatmentType;
      case 2:
        return bookingData.accommodationId && bookingData.transferId && bookingData.checkIn && bookingData.checkOut;
      case 3:
        return true;
      default:
        return false;
    }
  };

  return (
    <Dialog open={isOpen} onOpenChange={handleClose}>
      <DialogContent className="max-w-4xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>Rezervasyon Sihirbazı</DialogTitle>
          <DialogDescription>
            3 adımda rezervasyonunuzu tamamlayın
          </DialogDescription>
        </DialogHeader>

        {/* Progress Steps */}
        <div className="mb-8">
          <div className="flex items-center justify-between">
            {[1, 2, 3].map((step) => (
              <React.Fragment key={step}>
                <div className="flex flex-col items-center">
                  <div
                    className={cn(
                      'flex h-10 w-10 items-center justify-center rounded-full border-2 transition-all',
                      currentStep >= step
                        ? 'border-primary-600 bg-primary-600 text-white'
                        : 'border-slate-300 bg-white text-slate-400'
                    )}
                  >
                    {currentStep > step ? (
                      <Check className="h-5 w-5" />
                    ) : (
                      <span className="text-sm font-semibold">{step}</span>
                    )}
                  </div>
                  <span
                    className={cn(
                      'mt-2 text-xs font-medium',
                      currentStep >= step ? 'text-primary-600' : 'text-slate-400'
                    )}
                  >
                    {step === 1 && 'Tedavi/Doktor'}
                    {step === 2 && 'Konaklama & Transfer'}
                    {step === 3 && 'Özet & Onay'}
                  </span>
                </div>
                {step < 3 && (
                  <div
                    className={cn(
                      'h-0.5 flex-1 mx-4 transition-all',
                      currentStep > step ? 'bg-primary-600' : 'bg-slate-300'
                    )}
                  />
                )}
              </React.Fragment>
            ))}
          </div>
        </div>

        {/* Step Content */}
        <AnimatePresence mode="wait">
          <motion.div
            key={currentStep}
            initial="initial"
            animate="animate"
            exit="exit"
            variants={slideUp}
            className="min-h-[400px]"
          >
            {currentStep === 1 && (
              <Step1Treatment
                bookingData={bookingData}
                onDataChange={handleDataChange}
              />
            )}
            {currentStep === 2 && (
              <Step2Accommodation
                bookingData={bookingData}
                onDataChange={handleDataChange}
                hospitalId={bookingData.hospitalId}
              />
            )}
            {currentStep === 3 && (
              <Step3Summary
                bookingData={bookingData}
                onDataChange={handleDataChange}
              />
            )}
          </motion.div>
        </AnimatePresence>

        {/* Navigation Buttons */}
        <div className="mt-8 flex justify-between border-t border-slate-200 pt-4">
          <Button
            variant="outline"
            onClick={handleBack}
            disabled={currentStep === 1}
          >
            <ChevronLeft className="mr-2 h-4 w-4" />
            Geri
          </Button>

          {currentStep < totalSteps ? (
            <Button
              onClick={handleNext}
              disabled={!canProceed()}
            >
              İleri
              <ChevronRight className="ml-2 h-4 w-4" />
            </Button>
          ) : (
            <Button
              onClick={handleSubmit}
              disabled={!canProceed() || isSubmitting}
            >
              {isSubmitting ? 'Gönderiliyor...' : 'Rezervasyonu Onayla'}
            </Button>
          )}
        </div>
      </DialogContent>
    </Dialog>
  );
};

// Step 1: Treatment/Doctor Selection
const Step1Treatment = ({ bookingData, onDataChange }) => {
  const { data: hospitals, isLoading: hospitalsLoading } = useQuery({
    queryKey: ['hospitals'],
    queryFn: () => hospitalService.getAll({ size: 100 }),
  });

  const { data: doctors, isLoading: doctorsLoading } = useQuery({
    queryKey: ['doctors', bookingData.hospitalId],
    queryFn: () => {
      if (bookingData.hospitalId) {
        return doctorService.getByHospital(bookingData.hospitalId);
      }
      return Promise.resolve({ data: [] });
    },
    enabled: !!bookingData.hospitalId,
  });

  const treatmentTypes = [
    'Kardiyoloji',
    'Estetik Cerrahi',
    'Diş Hekimliği',
    'Onkoloji',
    'Ortopedi',
    'Göz Hastalıkları',
    'Genel Cerrahi',
  ];

  return (
    <div className="space-y-6">
      <div className="flex items-center gap-2 text-lg font-semibold">
        <Building2 className="h-5 w-5 text-primary-600" />
        <span>Hastane Seçimi</span>
      </div>

      {hospitalsLoading ? (
        <Skeleton className="h-32 w-full" />
      ) : (
        <div className="grid grid-cols-1 gap-3 md:grid-cols-2">
          {(hospitals?.data?.content || hospitals?.data || []).map((hospital) => (
            <button
              key={hospital.id}
              onClick={() => onDataChange('hospitalId', hospital.id)}
              className={cn(
                'rounded-lg border-2 p-4 text-left transition-all',
                bookingData.hospitalId === hospital.id
                  ? 'border-primary-600 bg-primary-50'
                  : 'border-slate-200 hover:border-primary-300'
              )}
            >
              <div className="font-semibold">{hospital.name}</div>
              <div className="text-sm text-slate-500">{hospital.city}</div>
            </button>
          ))}
        </div>
      )}

      {bookingData.hospitalId && (
        <>
          <div className="flex items-center gap-2 text-lg font-semibold">
            <User className="h-5 w-5 text-primary-600" />
            <span>Doktor Seçimi</span>
          </div>

          {doctorsLoading ? (
            <Skeleton className="h-32 w-full" />
          ) : (
            <div className="grid grid-cols-1 gap-3 md:grid-cols-2">
              {(doctors?.data || []).map((doctor) => (
                <button
                  key={doctor.id}
                  onClick={() => onDataChange('doctorId', doctor.id)}
                  className={cn(
                    'rounded-lg border-2 p-4 text-left transition-all',
                    bookingData.doctorId === doctor.id
                      ? 'border-primary-600 bg-primary-50'
                      : 'border-slate-200 hover:border-primary-300'
                  )}
                >
                  <div className="font-semibold">{doctor.name}</div>
                  <div className="text-sm text-slate-500">{doctor.specialization}</div>
                </button>
              ))}
            </div>
          )}

          <div>
            <label className="mb-2 block text-sm font-medium">Tedavi Türü</label>
            <select
              value={bookingData.treatmentType}
              onChange={(e) => onDataChange('treatmentType', e.target.value)}
              className="w-full rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm focus:border-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-500/20"
            >
              <option value="">Tedavi türü seçin</option>
              {treatmentTypes.map((type) => (
                <option key={type} value={type}>
                  {type}
                </option>
              ))}
            </select>
          </div>
        </>
      )}
    </div>
  );
};

// Step 2: Accommodation & Transfer
const Step2Accommodation = ({ bookingData, onDataChange, hospitalId }) => {
  const { data: accommodations, isLoading: accommodationsLoading } = useQuery({
    queryKey: ['accommodations', hospitalId],
    queryFn: () => {
      if (hospitalId) {
        return accommodationService.getNearHospital(hospitalId);
      }
      return Promise.resolve({ data: [] });
    },
    enabled: !!hospitalId,
  });

  const { data: transfers, isLoading: transfersLoading } = useQuery({
    queryKey: ['transfers'],
    queryFn: () => transferService.getAll(),
  });

  return (
    <div className="space-y-6">
      <div className="flex items-center gap-2 text-lg font-semibold">
        <Hotel className="h-5 w-5 text-primary-600" />
        <span>Konaklama Seçimi</span>
      </div>

      {accommodationsLoading ? (
        <Skeleton className="h-32 w-full" />
      ) : (
        <div className="grid grid-cols-1 gap-3 md:grid-cols-2">
          {(accommodations?.data || []).map((accommodation) => (
            <button
              key={accommodation.id}
              onClick={() => onDataChange('accommodationId', accommodation.id)}
              className={cn(
                'rounded-lg border-2 p-4 text-left transition-all',
                bookingData.accommodationId === accommodation.id
                  ? 'border-primary-600 bg-primary-50'
                  : 'border-slate-200 hover:border-primary-300'
              )}
            >
              <div className="font-semibold">{accommodation.name}</div>
              <div className="text-sm text-slate-500">
                €{accommodation.pricePerNight}/gece
              </div>
            </button>
          ))}
        </div>
      )}

      <div className="flex items-center gap-2 text-lg font-semibold">
        <Car className="h-5 w-5 text-primary-600" />
        <span>Transfer Seçimi</span>
      </div>

      {transfersLoading ? (
        <Skeleton className="h-32 w-full" />
      ) : (
        <div className="grid grid-cols-1 gap-3 md:grid-cols-2">
          {(transfers?.data || []).map((transfer) => (
            <button
              key={transfer.id}
              onClick={() => onDataChange('transferId', transfer.id)}
              className={cn(
                'rounded-lg border-2 p-4 text-left transition-all',
                bookingData.transferId === transfer.id
                  ? 'border-primary-600 bg-primary-50'
                  : 'border-slate-200 hover:border-primary-300'
              )}
            >
              <div className="font-semibold">{transfer.serviceType}</div>
              <div className="text-sm text-slate-500">€{transfer.price}</div>
            </button>
          ))}
        </div>
      )}

      <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
        <div>
          <label className="mb-2 block text-sm font-medium">Giriş Tarihi</label>
          <input
            type="date"
            value={bookingData.checkIn || ''}
            onChange={(e) => onDataChange('checkIn', e.target.value)}
            className="w-full rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm focus:border-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-500/20"
          />
        </div>
        <div>
          <label className="mb-2 block text-sm font-medium">Çıkış Tarihi</label>
          <input
            type="date"
            value={bookingData.checkOut || ''}
            onChange={(e) => onDataChange('checkOut', e.target.value)}
            min={bookingData.checkIn || ''}
            className="w-full rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm focus:border-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-500/20"
          />
        </div>
      </div>
    </div>
  );
};

// Step 3: Summary
const Step3Summary = ({ bookingData, onDataChange }) => {
  return (
    <div className="space-y-6">
      <div className="text-lg font-semibold">Rezervasyon Özeti</div>

      <div className="rounded-lg border border-slate-200 bg-slate-50 p-4">
        <div className="space-y-3">
          <div className="flex justify-between">
            <span className="text-slate-600">Hastane:</span>
            <span className="font-medium">ID: {bookingData.hospitalId}</span>
          </div>
          <div className="flex justify-between">
            <span className="text-slate-600">Doktor:</span>
            <span className="font-medium">ID: {bookingData.doctorId}</span>
          </div>
          <div className="flex justify-between">
            <span className="text-slate-600">Tedavi Türü:</span>
            <span className="font-medium">{bookingData.treatmentType}</span>
          </div>
          <div className="flex justify-between">
            <span className="text-slate-600">Konaklama:</span>
            <span className="font-medium">ID: {bookingData.accommodationId}</span>
          </div>
          <div className="flex justify-between">
            <span className="text-slate-600">Transfer:</span>
            <span className="font-medium">ID: {bookingData.transferId}</span>
          </div>
          <div className="flex justify-between">
            <span className="text-slate-600">Giriş:</span>
            <span className="font-medium">{bookingData.checkIn || 'Belirtilmedi'}</span>
          </div>
          <div className="flex justify-between">
            <span className="text-slate-600">Çıkış:</span>
            <span className="font-medium">{bookingData.checkOut || 'Belirtilmedi'}</span>
          </div>
        </div>
      </div>

      <div>
        <label className="mb-2 block text-sm font-medium">Notlar (Opsiyonel)</label>
        <textarea
          value={bookingData.notes}
          onChange={(e) => onDataChange('notes', e.target.value)}
          rows={4}
          className="w-full rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm focus:border-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-500/20"
          placeholder="Rezervasyonunuz hakkında özel notlar..."
        />
      </div>
    </div>
  );
};

