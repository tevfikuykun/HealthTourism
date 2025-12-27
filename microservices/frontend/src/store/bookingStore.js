import { create } from 'zustand';
import { persist } from 'zustand/middleware';

/**
 * Booking Wizard State Management Store
 * Enterprise seviyesinde rezervasyon sepeti yönetimi
 * 
 * Zustand ile hafif ve performanslı state management
 */
export const useBookingStore = create(
  persist(
    (set, get) => ({
      // Step 1: Treatment & Date
      selectedPackage: null,
      selectedHospital: null,
      selectedDoctor: null,
      treatmentType: '',
      appointmentDate: null,
      appointmentTime: null,
      
      // Step 2: Logistics
      selectedAccommodation: null,
      selectedFlight: null,
      selectedTransfer: null,
      checkInDate: null,
      checkOutDate: null,
      
      // Step 3: Additional Services
      visaService: false,
      translationService: false,
      insuranceService: false,
      
      // Cart/Summary
      totalPrice: 0,
      currency: 'EUR',
      notes: '',
      
      // Actions - Step 1
      setSelectedPackage: (pkg) => set({ selectedPackage: pkg }),
      setSelectedHospital: (hospital) => set({ selectedHospital: hospital }),
      setSelectedDoctor: (doctor) => set({ selectedDoctor: doctor }),
      setTreatmentType: (type) => set({ treatmentType: type }),
      setAppointmentDate: (date) => set({ appointmentDate: date }),
      setAppointmentTime: (time) => set({ appointmentTime: time }),
      
      // Actions - Step 2
      setSelectedAccommodation: (accommodation) => {
        set({ selectedAccommodation: accommodation });
        get().calculateTotalPrice();
      },
      setSelectedFlight: (flight) => {
        set({ selectedFlight: flight });
        get().calculateTotalPrice();
      },
      setSelectedTransfer: (transfer) => {
        set({ selectedTransfer: transfer });
        get().calculateTotalPrice();
      },
      setCheckInDate: (date) => set({ checkInDate: date }),
      setCheckOutDate: (date) => set({ checkOutDate: date }),
      
      // Actions - Step 3
      setVisaService: (enabled) => {
        set({ visaService: enabled });
        get().calculateTotalPrice();
      },
      setTranslationService: (enabled) => {
        set({ translationService: enabled });
        get().calculateTotalPrice();
      },
      setInsuranceService: (enabled) => {
        set({ insuranceService: enabled });
        get().calculateTotalPrice();
      },
      
      // Price Calculation
      calculateTotalPrice: () => {
        const state = get();
        let total = 0;
        
        // Package/Treatment price
        if (state.selectedPackage?.price) {
          total += state.selectedPackage.price;
        }
        
        // Accommodation price
        if (state.selectedAccommodation && state.checkInDate && state.checkOutDate) {
          const nights = Math.ceil(
            (new Date(state.checkOutDate) - new Date(state.checkInDate)) / (1000 * 60 * 60 * 24)
          );
          total += (state.selectedAccommodation.pricePerNight || 0) * nights;
        }
        
        // Flight price
        if (state.selectedFlight?.price) {
          total += state.selectedFlight.price;
        }
        
        // Transfer price
        if (state.selectedTransfer?.price) {
          total += state.selectedTransfer.price;
        }
        
        // Additional services (fixed prices)
        if (state.visaService) total += 150; // €150
        if (state.translationService) total += 200; // €200
        if (state.insuranceService) total += 100; // €100
        
        set({ totalPrice: total });
      },
      
      // Utility Actions
      setNotes: (notes) => set({ notes }),
      setCurrency: (currency) => set({ currency }),
      
      // Reset/Clear
      clearBooking: () => set({
        selectedPackage: null,
        selectedHospital: null,
        selectedDoctor: null,
        treatmentType: '',
        appointmentDate: null,
        appointmentTime: null,
        selectedAccommodation: null,
        selectedFlight: null,
        selectedTransfer: null,
        checkInDate: null,
        checkOutDate: null,
        visaService: false,
        translationService: false,
        insuranceService: false,
        totalPrice: 0,
        notes: '',
      }),
      
      // Validation
      isStep1Valid: () => {
        const state = get();
        return !!(
          (state.selectedPackage || (state.selectedHospital && state.selectedDoctor)) &&
          state.treatmentType &&
          state.appointmentDate &&
          state.appointmentTime
        );
      },
      
      isStep2Valid: () => {
        const state = get();
        return !!(
          state.selectedAccommodation &&
          state.checkInDate &&
          state.checkOutDate &&
          new Date(state.checkOutDate) > new Date(state.checkInDate)
        );
      },
      
      isStep3Valid: () => {
        // Step 3 is always valid (optional services)
        return true;
      },
    }),
    {
      name: 'booking-storage', // localStorage key
      partialize: (state) => ({
        // Only persist essential data
        selectedPackage: state.selectedPackage,
        selectedHospital: state.selectedHospital,
        selectedDoctor: state.selectedDoctor,
        treatmentType: state.treatmentType,
        appointmentDate: state.appointmentDate,
        selectedAccommodation: state.selectedAccommodation,
        selectedFlight: state.selectedFlight,
        selectedTransfer: state.selectedTransfer,
        checkInDate: state.checkInDate,
        checkOutDate: state.checkOutDate,
        notes: state.notes,
      }),
    }
  )
);

