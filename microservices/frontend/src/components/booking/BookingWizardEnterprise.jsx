import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { useQuery, useMutation } from '@tanstack/react-query';
import { 
  packageService,
  hospitalService,
  doctorService,
  reservationService,
  visaService,
  translationService,
} from '../../services/api';
import { useBookingStore } from '../../store/bookingStore';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from '../ui/Dialog';
import { Button } from '../ui/Button';
import { Skeleton } from '../ui/Skeleton';
import { AvailabilityCalendar } from './AvailabilityCalendar';
import { LogisticsSelector } from './LogisticsSelector';
import { 
  ChevronRight, 
  ChevronLeft, 
  Check, 
  Package,
  Calendar,
  ShoppingCart,
  FileCheck,
  Shield,
  Languages,
  CreditCard,
  AlertCircle,
} from 'lucide-react';
import { cn } from '../../lib/utils';
import { slideUp } from '../../lib/motion';
import { toast } from 'react-toastify';

/**
 * Enterprise Booking Wizard
 * 3 Adımlı Rezervasyon Sihirbazı
 * 
 * Step 1: Treatment & Date - Paket seçimi ve uygun tarih aralığı
 * Step 2: Logistics Selection - Konaklama, uçuş ve transfer
 * Step 3: Verification & Summary - Vize/Tercüme hizmeti ve son onay
 * 
 * Backend entegrasyonları:
 * - ReservationConflictException mekanizması
 * - BaseEntity auditing
 * - Dynamic pricing
 */
export const BookingWizardEnterprise = ({ isOpen, onClose, initialData = {} }) => {
  const [currentStep, setCurrentStep] = useState(1);
  const [isSubmitting, setIsSubmitting] = useState(false);
  
  const {
    // Step 1
    selectedPackage,
    selectedHospital,
    selectedDoctor,
    treatmentType,
    appointmentDate,
    appointmentTime,
    // Step 2
    selectedAccommodation,
    selectedFlight,
    selectedTransfer,
    checkInDate,
    checkOutDate,
    // Step 3
    visaService: visaEnabled,
    translationService: translationEnabled,
    insuranceService: insuranceEnabled,
    // Summary
    totalPrice,
    notes,
    // Actions
    setSelectedPackage,
    setSelectedHospital,
    setSelectedDoctor,
    setTreatmentType,
    setAppointmentDate,
    setAppointmentTime,
    setVisaService,
    setTranslationService,
    setInsuranceService,
    setNotes,
    clearBooking,
    isStep1Valid,
    isStep2Valid,
    isStep3Valid,
    calculateTotalPrice,
  } = useBookingStore();

  const totalSteps = 3;

  // Initial data setup
  useEffect(() => {
    if (initialData.hospitalId) {
      setSelectedHospital(initialData.hospital);
    }
    if (initialData.doctorId) {
      setSelectedDoctor(initialData.doctor);
    }
    if (initialData.packageId) {
      setSelectedPackage(initialData.package);
    }
  }, [initialData]);

  // Price calculation on data change
  useEffect(() => {
    calculateTotalPrice();
  }, [
    selectedPackage,
    selectedAccommodation,
    selectedFlight,
    selectedTransfer,
    checkInDate,
    checkOutDate,
    visaEnabled,
    translationEnabled,
    insuranceEnabled,
  ]);

  // Packages query
  const { data: packagesData, isLoading: packagesLoading } = useQuery({
    queryKey: ['packages', selectedHospital?.id],
    queryFn: () => {
      if (selectedHospital?.id) {
        return packageService.getAll({ hospitalId: selectedHospital.id });
      }
      return packageService.getAll();
    },
  });

  // Hospitals query
  const { data: hospitalsData } = useQuery({
    queryKey: ['hospitals'],
    queryFn: () => hospitalService.getAll({ size: 50 }),
  });

  // Doctors query
  const { data: doctorsData } = useQuery({
    queryKey: ['doctors', selectedHospital?.id],
    queryFn: () => {
      if (selectedHospital?.id) {
        return doctorService.getByHospital(selectedHospital.id);
      }
      return Promise.resolve({ data: [] });
    },
    enabled: !!selectedHospital?.id,
  });

  // Reservation mutation
  const reservationMutation = useMutation({
    mutationFn: async (reservationData) => {
      // Final conflict check before submission
      const conflictCheck = await reservationService.checkConflict({
        doctorId: reservationData.doctorId,
        hospitalId: reservationData.hospitalId,
        date: reservationData.appointmentDate,
        time: reservationData.appointmentTime,
      });

      if (conflictCheck.data?.hasConflict) {
        throw new Error('Seçilen tarih ve saat için çakışma tespit edildi. Lütfen başka bir zaman seçin.');
      }

      return reservationService.create(reservationData);
    },
    onSuccess: () => {
      toast.success('Rezervasyonunuz başarıyla oluşturuldu!');
      clearBooking();
      onClose();
    },
    onError: (error) => {
      toast.error(error.message || 'Rezervasyon oluşturulurken bir hata oluştu.');
    },
  });

  const handleNext = () => {
    if (currentStep < totalSteps) {
      // Validation
      if (currentStep === 1 && !isStep1Valid()) {
        toast.error('Lütfen tüm gerekli alanları doldurun.');
        return;
      }
      if (currentStep === 2 && !isStep2Valid()) {
        toast.error('Lütfen konaklama bilgilerini tamamlayın.');
        return;
      }
      
      setCurrentStep(currentStep + 1);
    }
  };

  const handleBack = () => {
    if (currentStep > 1) {
      setCurrentStep(currentStep - 1);
    }
  };

  const handleSubmit = async () => {
    if (!isStep1Valid() || !isStep2Valid()) {
      toast.error('Lütfen tüm gerekli alanları doldurun.');
      return;
    }

    setIsSubmitting(true);

    try {
      const reservationData = {
        // Step 1
        hospitalId: selectedHospital?.id,
        doctorId: selectedDoctor?.id,
        packageId: selectedPackage?.id,
        treatmentType,
        appointmentDate,
        appointmentTime,
        // Step 2
        accommodationId: selectedAccommodation?.id,
        flightId: selectedFlight?.id,
        transferId: selectedTransfer?.id,
        checkInDate,
        checkOutDate,
        // Step 3
        visaService: visaEnabled,
        translationService: translationEnabled,
        insuranceService: insuranceEnabled,
        // Additional
        notes,
        totalPrice,
      };

      await reservationMutation.mutateAsync(reservationData);
    } catch (error) {
      console.error('Reservation error:', error);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleClose = () => {
    setCurrentStep(1);
    onClose();
  };

  const packages = packagesData?.data?.content || packagesData?.data || [];
  const hospitals = hospitalsData?.data?.content || hospitalsData?.data || [];
  const doctors = doctorsData?.data || [];

  return (
    <Dialog open={isOpen} onOpenChange={handleClose}>
      <DialogContent className="max-w-5xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>Rezervasyon Sihirbazı</DialogTitle>
          <DialogDescription>
            Enterprise seviyesinde rezervasyon sistemi - 3 adımda tamamlayın
          </DialogDescription>
        </DialogHeader>

        {/* Progress Steps */}
        <div className="mb-8">
          <div className="flex items-center justify-between">
            {[
              { step: 1, label: 'Tedavi & Tarih', icon: Calendar },
              { step: 2, label: 'Lojistik', icon: ShoppingCart },
              { step: 3, label: 'Onay', icon: FileCheck },
            ].map(({ step, label, icon: Icon }) => (
              <React.Fragment key={step}>
                <div className="flex flex-col items-center">
                  <div
                    className={cn(
                      'flex h-12 w-12 items-center justify-center rounded-full border-2 transition-all',
                      currentStep >= step
                        ? 'border-primary-600 bg-primary-600 text-white'
                        : 'border-slate-300 bg-white text-slate-400'
                    )}
                  >
                    {currentStep > step ? (
                      <Check className="h-6 w-6" />
                    ) : (
                      <Icon className="h-6 w-6" />
                    )}
                  </div>
                  <span
                    className={cn(
                      'mt-2 text-xs font-medium',
                      currentStep >= step ? 'text-primary-600' : 'text-slate-400'
                    )}
                  >
                    {label}
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
            className="min-h-[500px]"
          >
            {currentStep === 1 && (
              <Step1TreatmentDate
                packages={packages}
                packagesLoading={packagesLoading}
                hospitals={hospitals}
                doctors={doctors}
                selectedPackage={selectedPackage}
                selectedHospital={selectedHospital}
                selectedDoctor={selectedDoctor}
                treatmentType={treatmentType}
                appointmentDate={appointmentDate}
                appointmentTime={appointmentTime}
                onPackageSelect={setSelectedPackage}
                onHospitalSelect={setSelectedHospital}
                onDoctorSelect={setSelectedDoctor}
                onTreatmentTypeChange={setTreatmentType}
                onDateSelect={setAppointmentDate}
                onTimeSelect={setAppointmentTime}
              />
            )}
            {currentStep === 2 && (
              <Step2Logistics
                hospital={selectedHospital}
                checkInDate={checkInDate}
                checkOutDate={checkOutDate}
              />
            )}
            {currentStep === 3 && (
              <Step3Verification
                visaEnabled={visaEnabled}
                translationEnabled={translationEnabled}
                insuranceEnabled={insuranceEnabled}
                notes={notes}
                totalPrice={totalPrice}
                onVisaChange={setVisaService}
                onTranslationChange={setTranslationService}
                onInsuranceChange={setInsuranceService}
                onNotesChange={setNotes}
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
              disabled={
                (currentStep === 1 && !isStep1Valid()) ||
                (currentStep === 2 && !isStep2Valid())
              }
            >
              İleri
              <ChevronRight className="ml-2 h-4 w-4" />
            </Button>
          ) : (
            <Button
              onClick={handleSubmit}
              disabled={!isStep1Valid() || !isStep2Valid() || isSubmitting}
            >
              {isSubmitting ? (
                'Gönderiliyor...'
              ) : (
                <>
                  <CreditCard className="mr-2 h-4 w-4" />
                  Rezervasyonu Onayla
                </>
              )}
            </Button>
          )}
        </div>
      </DialogContent>
    </Dialog>
  );
};

// Step 1: Treatment & Date
const Step1TreatmentDate = ({
  packages,
  packagesLoading,
  hospitals,
  doctors,
  selectedPackage,
  selectedHospital,
  selectedDoctor,
  treatmentType,
  appointmentDate,
  appointmentTime,
  onPackageSelect,
  onHospitalSelect,
  onDoctorSelect,
  onTreatmentTypeChange,
  onDateSelect,
  onTimeSelect,
}) => {
  return (
    <div className="space-y-6">
      {/* Package Selection */}
      <div>
        <div className="mb-4 flex items-center gap-2">
          <Package className="h-5 w-5 text-primary-600" />
          <h3 className="text-lg font-semibold">Paket Seçimi (Opsiyonel)</h3>
        </div>

        {packagesLoading ? (
          <div className="grid grid-cols-1 gap-3 md:grid-cols-2">
            {[1, 2].map((i) => (
              <Skeleton key={i} className="h-32 w-full" />
            ))}
          </div>
        ) : (
          <div className="grid grid-cols-1 gap-3 md:grid-cols-2">
            {packages.slice(0, 4).map((pkg) => {
              const isSelected = selectedPackage?.id === pkg.id;
              return (
                <motion.button
                  key={pkg.id}
                  onClick={() => onPackageSelect(pkg)}
                  className={cn(
                    'rounded-lg border-2 p-4 text-left transition-all',
                    isSelected
                      ? 'border-primary-600 bg-primary-50'
                      : 'border-slate-200 hover:border-primary-300'
                  )}
                  whileHover={{ scale: 1.02 }}
                  whileTap={{ scale: 0.98 }}
                >
                  <div className="font-semibold">{pkg.name}</div>
                  <div className="mt-1 text-sm text-slate-600">{pkg.category}</div>
                  <div className="mt-2 text-lg font-bold text-primary-600">
                    €{pkg.price}
                  </div>
                </motion.button>
              );
            })}
          </div>
        )}
      </div>

      {/* Hospital & Doctor Selection */}
      {!selectedPackage && (
        <>
          <div>
            <label className="mb-2 block text-sm font-medium">Hastane</label>
            <select
              value={selectedHospital?.id || ''}
              onChange={(e) => {
                const hospital = hospitals.find((h) => h.id === Number(e.target.value));
                onHospitalSelect(hospital);
              }}
              className="w-full rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm"
            >
              <option value="">Hastane seçin</option>
              {hospitals.map((hospital) => (
                <option key={hospital.id} value={hospital.id}>
                  {hospital.name}
                </option>
              ))}
            </select>
          </div>

          {selectedHospital && (
            <div>
              <label className="mb-2 block text-sm font-medium">Doktor</label>
              <select
                value={selectedDoctor?.id || ''}
                onChange={(e) => {
                  const doctor = doctors.find((d) => d.id === Number(e.target.value));
                  onDoctorSelect(doctor);
                }}
                className="w-full rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm"
              >
                <option value="">Doktor seçin</option>
                {doctors.map((doctor) => (
                  <option key={doctor.id} value={doctor.id}>
                    {doctor.name} - {doctor.specialization}
                  </option>
                ))}
              </select>
            </div>
          )}

          <div>
            <label className="mb-2 block text-sm font-medium">Tedavi Türü</label>
            <select
              value={treatmentType}
              onChange={(e) => onTreatmentTypeChange(e.target.value)}
              className="w-full rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm"
            >
              <option value="">Tedavi türü seçin</option>
              <option value="Kardiyoloji">Kardiyoloji</option>
              <option value="Estetik">Estetik</option>
              <option value="Diş Hekimliği">Diş Hekimliği</option>
              <option value="Onkoloji">Onkoloji</option>
              <option value="Ortopedi">Ortopedi</option>
              <option value="Göz Hastalıkları">Göz Hastalıkları</option>
            </select>
          </div>
        </>
      )}

      {/* Availability Calendar */}
      {(selectedPackage || (selectedHospital && selectedDoctor)) && (
        <AvailabilityCalendar
          doctor={selectedDoctor}
          hospital={selectedPackage?.hospital || selectedHospital}
          onDateSelect={onDateSelect}
          onTimeSelect={onTimeSelect}
          selectedDate={appointmentDate}
          selectedTime={appointmentTime}
        />
      )}
    </div>
  );
};

// Step 2: Logistics
const Step2Logistics = ({ hospital, checkInDate, checkOutDate }) => {
  return (
    <LogisticsSelector
      hospital={hospital}
      checkInDate={checkInDate}
      checkOutDate={checkOutDate}
    />
  );
};

// Step 3: Verification & Summary
const Step3Verification = ({
  visaEnabled,
  translationEnabled,
  insuranceEnabled,
  notes,
  totalPrice,
  onVisaChange,
  onTranslationChange,
  onInsuranceChange,
  onNotesChange,
}) => {
  return (
    <div className="space-y-6">
      {/* Additional Services */}
      <div>
        <h3 className="mb-4 text-lg font-semibold">Ek Hizmetler</h3>
        <div className="space-y-3">
          {/* Visa Service */}
          <motion.button
            onClick={() => onVisaChange(!visaEnabled)}
            className={cn(
              'w-full rounded-lg border-2 p-4 text-left transition-all',
              visaEnabled
                ? 'border-primary-600 bg-primary-50'
                : 'border-slate-200 hover:border-primary-300'
            )}
            whileHover={{ scale: 1.01 }}
            whileTap={{ scale: 0.99 }}
          >
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-3">
                <Shield className="h-5 w-5 text-primary-600" />
                <div>
                  <div className="font-semibold">Vize Desteği</div>
                  <div className="text-sm text-slate-600">
                    Vize başvuru sürecinde yardım
                  </div>
                </div>
              </div>
              <div className="text-right">
                <div className="font-bold text-primary-600">€150</div>
                {visaEnabled && <Check className="mt-1 h-5 w-5 text-primary-600" />}
              </div>
            </div>
          </motion.button>

          {/* Translation Service */}
          <motion.button
            onClick={() => onTranslationChange(!translationEnabled)}
            className={cn(
              'w-full rounded-lg border-2 p-4 text-left transition-all',
              translationEnabled
                ? 'border-primary-600 bg-primary-50'
                : 'border-slate-200 hover:border-primary-300'
            )}
            whileHover={{ scale: 1.01 }}
            whileTap={{ scale: 0.99 }}
          >
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-3">
                <Languages className="h-5 w-5 text-primary-600" />
                <div>
                  <div className="font-semibold">Tercüme Hizmeti</div>
                  <div className="text-sm text-slate-600">
                    Tıbbi tercüme ve rehberlik
                  </div>
                </div>
              </div>
              <div className="text-right">
                <div className="font-bold text-primary-600">€200</div>
                {translationEnabled && <Check className="mt-1 h-5 w-5 text-primary-600" />}
              </div>
            </div>
          </motion.button>

          {/* Insurance Service */}
          <motion.button
            onClick={() => onInsuranceChange(!insuranceEnabled)}
            className={cn(
              'w-full rounded-lg border-2 p-4 text-left transition-all',
              insuranceEnabled
                ? 'border-primary-600 bg-primary-50'
                : 'border-slate-200 hover:border-primary-300'
            )}
            whileHover={{ scale: 1.01 }}
            whileTap={{ scale: 0.99 }}
          >
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-3">
                <Shield className="h-5 w-5 text-primary-600" />
                <div>
                  <div className="font-semibold">Seyahat Sigortası</div>
                  <div className="text-sm text-slate-600">
                    Kapsamlı seyahat sigortası
                  </div>
                </div>
              </div>
              <div className="text-right">
                <div className="font-bold text-primary-600">€100</div>
                {insuranceEnabled && <Check className="mt-1 h-5 w-5 text-primary-600" />}
              </div>
            </div>
          </motion.button>
        </div>
      </div>

      {/* Notes */}
      <div>
        <label className="mb-2 block text-sm font-medium">Notlar (Opsiyonel)</label>
        <textarea
          value={notes}
          onChange={(e) => onNotesChange(e.target.value)}
          rows={4}
          className="w-full rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm"
          placeholder="Özel istekler veya notlar..."
        />
      </div>

      {/* Final Summary */}
      <div className="rounded-lg border-2 border-primary-200 bg-primary-50 p-6">
        <h3 className="mb-4 text-lg font-semibold">Rezervasyon Özeti</h3>
        <div className="space-y-2 text-sm">
          <div className="flex justify-between">
            <span className="text-slate-600">Toplam Tutar</span>
            <span className="text-2xl font-bold text-primary-600">€{totalPrice}</span>
          </div>
        </div>
        <div className="mt-4 flex items-center gap-2 rounded-lg bg-white p-3 text-xs text-slate-600">
          <AlertCircle className="h-4 w-4" />
          <span>
            Rezervasyonunuz onaylandıktan sonra e-posta ile bilgilendirileceksiniz.
          </span>
        </div>
      </div>
    </div>
  );
};

