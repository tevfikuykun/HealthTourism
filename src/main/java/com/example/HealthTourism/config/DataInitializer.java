package com.example.HealthTourism.config;

import com.example.HealthTourism.entity.*;
import com.example.HealthTourism.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private HospitalRepository hospitalRepository;
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
    private AccommodationRepository accommodationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CarRentalRepository carRentalRepository;
    
    @Autowired
    private FlightBookingRepository flightBookingRepository;
    
    @Autowired
    private TransferServiceRepository transferServiceRepository;
    
    @Autowired
    private TravelPackageRepository travelPackageRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // Sadece veritabanı boşsa örnek veri ekle
        if (hospitalRepository.count() == 0) {
            initializeData();
        }
    }
    
    private void initializeData() {
        // Örnek Kullanıcılar
        User user1 = new User();
        user1.setEmail("john.doe@example.com");
        user1.setPassword("password123");
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setPhone("+1-555-0100");
        user1.setCountry("USA");
        user1.setRole("USER");
        user1.setIsActive(true);
        user1 = userRepository.save(user1);
        
        // Örnek Hastaneler
        Hospital hospital1 = new Hospital();
        hospital1.setName("Acıbadem Maslak Hastanesi");
        hospital1.setAddress("Darüşşafaka Caddesi No:14");
        hospital1.setCity("İstanbul");
        hospital1.setDistrict("Sarıyer");
        hospital1.setPhone("+90 212 304 44 44");
        hospital1.setEmail("info@acibadem.com.tr");
        hospital1.setDescription("Türkiye'nin önde gelen özel hastanelerinden biri. Modern teknoloji ve deneyimli doktor kadrosu ile hizmet vermektedir.");
        hospital1.setLatitude(41.1086);
        hospital1.setLongitude(29.0100);
        hospital1.setAirportDistance(35.5);
        hospital1.setAirportDistanceMinutes(45);
        hospital1.setRating(4.8);
        hospital1.setTotalReviews(1250);
        hospital1.setIsActive(true);
        hospital1 = hospitalRepository.save(hospital1);
        
        Hospital hospital2 = new Hospital();
        hospital2.setName("Memorial Şişli Hastanesi");
        hospital2.setAddress("Piyalepaşa Bulvarı No:1");
        hospital2.setCity("İstanbul");
        hospital2.setDistrict("Şişli");
        hospital2.setPhone("+90 212 314 66 66");
        hospital2.setEmail("info@memorial.com.tr");
        hospital2.setDescription("Uluslararası standartlarda sağlık hizmeti sunan modern bir hastane.");
        hospital2.setLatitude(41.0600);
        hospital2.setLongitude(28.9800);
        hospital2.setAirportDistance(28.3);
        hospital2.setAirportDistanceMinutes(35);
        hospital2.setRating(4.7);
        hospital2.setTotalReviews(980);
        hospital2.setIsActive(true);
        hospital2 = hospitalRepository.save(hospital2);
        
        Hospital hospital3 = new Hospital();
        hospital3.setName("Anadolu Sağlık Merkezi");
        hospital3.setAddress("Cumhuriyet Mahallesi, Büyükdere Caddesi No:40");
        hospital3.setCity("İstanbul");
        hospital3.setDistrict("Şişli");
        hospital3.setPhone("+90 212 216 50 00");
        hospital3.setEmail("info@anadolusaglik.org");
        hospital3.setDescription("JCI akreditasyonlu, dünya standartlarında sağlık hizmeti.");
        hospital3.setLatitude(41.0550);
        hospital3.setLongitude(28.9850);
        hospital3.setAirportDistance(30.0);
        hospital3.setAirportDistanceMinutes(38);
        hospital3.setRating(4.6);
        hospital3.setTotalReviews(750);
        hospital3.setIsActive(true);
        hospital3 = hospitalRepository.save(hospital3);
        
        // Örnek Doktorlar - Hospital 1
        Doctor doctor1 = new Doctor();
        doctor1.setFirstName("Mehmet");
        doctor1.setLastName("Yılmaz");
        doctor1.setTitle("Prof. Dr.");
        doctor1.setSpecialization("Kardiyoloji");
        doctor1.setBio("25 yıllık deneyime sahip kardiyoloji uzmanı. Kalp ve damar hastalıkları konusunda uzman.");
        doctor1.setExperienceYears(25);
        doctor1.setLanguages("Türkçe, İngilizce, Almanca");
        doctor1.setRating(4.9);
        doctor1.setTotalReviews(320);
        doctor1.setConsultationFee(new BigDecimal("1500.00"));
        doctor1.setIsAvailable(true);
        doctor1.setHospital(hospital1);
        doctorRepository.save(doctor1);
        
        Doctor doctor2 = new Doctor();
        doctor2.setFirstName("Ayşe");
        doctor2.setLastName("Demir");
        doctor2.setTitle("Doç. Dr.");
        doctor2.setSpecialization("Ortopedi");
        doctor2.setBio("Eklem ve kemik cerrahisi konusunda uzman. Özellikle diz ve kalça protezi operasyonlarında deneyimli.");
        doctor2.setExperienceYears(18);
        doctor2.setLanguages("Türkçe, İngilizce, Fransızca");
        doctor2.setRating(4.8);
        doctor2.setTotalReviews(245);
        doctor2.setConsultationFee(new BigDecimal("1200.00"));
        doctor2.setIsAvailable(true);
        doctor2.setHospital(hospital1);
        doctorRepository.save(doctor2);
        
        // Örnek Doktorlar - Hospital 2
        Doctor doctor3 = new Doctor();
        doctor3.setFirstName("Ali");
        doctor3.setLastName("Kaya");
        doctor3.setTitle("Prof. Dr.");
        doctor3.setSpecialization("Onkoloji");
        doctor3.setBio("Kanser tedavisi ve kemoterapi konusunda uzman. 30 yıllık deneyime sahip.");
        doctor3.setExperienceYears(30);
        doctor3.setLanguages("Türkçe, İngilizce, Arapça");
        doctor3.setRating(4.9);
        doctor3.setTotalReviews(410);
        doctor3.setConsultationFee(new BigDecimal("1800.00"));
        doctor3.setIsAvailable(true);
        doctor3.setHospital(hospital2);
        doctorRepository.save(doctor3);
        
        Doctor doctor4 = new Doctor();
        doctor4.setFirstName("Zeynep");
        doctor4.setLastName("Arslan");
        doctor4.setTitle("Dr.");
        doctor4.setSpecialization("Plastik Cerrahi");
        doctor4.setBio("Estetik ve rekonstrüktif cerrahi uzmanı. 15 yıllık deneyim.");
        doctor4.setExperienceYears(15);
        doctor4.setLanguages("Türkçe, İngilizce");
        doctor4.setRating(4.7);
        doctor4.setTotalReviews(189);
        doctor4.setConsultationFee(new BigDecimal("2000.00"));
        doctor4.setIsAvailable(true);
        doctor4.setHospital(hospital2);
        doctorRepository.save(doctor4);
        
        // Örnek Konaklama - Hospital 1
        Accommodation acc1 = new Accommodation();
        acc1.setName("Hilton İstanbul Maslak");
        acc1.setType("Hotel");
        acc1.setAddress("Büyükdere Caddesi No:123");
        acc1.setCity("İstanbul");
        acc1.setDistrict("Sarıyer");
        acc1.setPhone("+90 212 315 60 00");
        acc1.setEmail("istanbul.maslak@hilton.com");
        acc1.setDescription("5 yıldızlı lüks otel. Hastaneye sadece 5 dakika mesafede.");
        acc1.setLatitude(41.1100);
        acc1.setLongitude(29.0120);
        acc1.setDistanceToHospital(0.8);
        acc1.setDistanceToHospitalMinutes(5);
        acc1.setAirportDistance(36.0);
        acc1.setAirportDistanceMinutes(48);
        acc1.setPricePerNight(new BigDecimal("450.00"));
        acc1.setStarRating(5);
        acc1.setRating(4.8);
        acc1.setTotalReviews(890);
        acc1.setHasWifi(true);
        acc1.setHasParking(true);
        acc1.setHasBreakfast(true);
        acc1.setIsActive(true);
        acc1.setHospital(hospital1);
        accommodationRepository.save(acc1);
        
        Accommodation acc2 = new Accommodation();
        acc2.setName("Radisson Blu Hotel Şişli");
        acc2.setType("Hotel");
        acc2.setAddress("Cumhuriyet Caddesi No:19");
        acc2.setCity("İstanbul");
        acc2.setDistrict("Şişli");
        acc2.setPhone("+90 212 231 18 00");
        acc2.setEmail("info.istanbul@radissonblu.com");
        acc2.setDescription("Modern ve konforlu 4 yıldızlı otel. Şehir merkezine yakın.");
        acc2.setLatitude(41.0580);
        acc2.setLongitude(28.9880);
        acc2.setDistanceToHospital(1.2);
        acc2.setDistanceToHospitalMinutes(8);
        acc2.setAirportDistance(29.5);
        acc2.setAirportDistanceMinutes(37);
        acc2.setPricePerNight(new BigDecimal("320.00"));
        acc2.setStarRating(4);
        acc2.setRating(4.6);
        acc2.setTotalReviews(650);
        acc2.setHasWifi(true);
        acc2.setHasParking(true);
        acc2.setHasBreakfast(true);
        acc2.setIsActive(true);
        acc2.setHospital(hospital2);
        accommodationRepository.save(acc2);
        
        Accommodation acc3 = new Accommodation();
        acc3.setName("DoubleTree by Hilton İstanbul");
        acc3.setType("Hotel");
        acc3.setAddress("Büyükdere Caddesi No:40");
        acc3.setCity("İstanbul");
        acc3.setDistrict("Şişli");
        acc3.setPhone("+90 212 315 50 00");
        acc3.setEmail("istanbul@doubletree.com");
        acc3.setDescription("Hastaneye yürüme mesafesinde modern otel.");
        acc3.setLatitude(41.0560);
        acc3.setLongitude(28.9870);
        acc3.setDistanceToHospital(0.5);
        acc3.setDistanceToHospitalMinutes(3);
        acc3.setAirportDistance(30.2);
        acc3.setAirportDistanceMinutes(38);
        acc3.setPricePerNight(new BigDecimal("380.00"));
        acc3.setStarRating(4);
        acc3.setRating(4.7);
        acc3.setTotalReviews(720);
        acc3.setHasWifi(true);
        acc3.setHasParking(true);
        acc3.setHasBreakfast(true);
        acc3.setIsActive(true);
        acc3.setHospital(hospital3);
        accommodationRepository.save(acc3);
        
        // Örnek Araç Kiralama
        CarRental car1 = new CarRental();
        car1.setCompanyName("Avis İstanbul");
        car1.setCarModel("Toyota Corolla");
        car1.setCarType("Standard");
        car1.setPassengerCapacity(5);
        car1.setLuggageCapacity(2);
        car1.setTransmission("Automatic");
        car1.setHasAirConditioning(true);
        car1.setHasGPS(true);
        car1.setPricePerDay(new BigDecimal("350.00"));
        car1.setPricePerWeek(new BigDecimal("2100.00"));
        car1.setPricePerMonth(new BigDecimal("8500.00"));
        car1.setPickupLocation("İstanbul Havalimanı");
        car1.setDropoffLocation("İstanbul Havalimanı");
        car1.setPhone("+90 212 555 0100");
        car1.setEmail("istanbul@avis.com");
        car1.setDescription("Güvenilir ve konforlu araç kiralama hizmeti.");
        car1.setRating(4.7);
        car1.setTotalReviews(450);
        car1.setIsAvailable(true);
        car1.setIncludesInsurance(true);
        car1.setIncludesDriver(false);
        carRentalRepository.save(car1);
        
        CarRental car2 = new CarRental();
        car2.setCompanyName("Hertz İstanbul");
        car2.setCarModel("Mercedes E-Class");
        car2.setCarType("Luxury");
        car2.setPassengerCapacity(5);
        car2.setLuggageCapacity(3);
        car2.setTransmission("Automatic");
        car2.setHasAirConditioning(true);
        car2.setHasGPS(true);
        car2.setPricePerDay(new BigDecimal("850.00"));
        car2.setPricePerWeek(new BigDecimal("5100.00"));
        car2.setPricePerMonth(new BigDecimal("20000.00"));
        car2.setPickupLocation("İstanbul Havalimanı");
        car2.setDropoffLocation("İstanbul Havalimanı");
        car2.setPhone("+90 212 555 0200");
        car2.setEmail("istanbul@hertz.com");
        car2.setDescription("Lüks araç kiralama, şoförlü seçenek mevcut.");
        car2.setRating(4.9);
        car2.setTotalReviews(320);
        car2.setIsAvailable(true);
        car2.setIncludesInsurance(true);
        car2.setIncludesDriver(true);
        carRentalRepository.save(car2);
        
        // Örnek Uçak Bileti
        FlightBooking flight1 = new FlightBooking();
        flight1.setAirlineName("Turkish Airlines");
        flight1.setFlightNumber("TK001");
        flight1.setDepartureCity("New York");
        flight1.setArrivalCity("İstanbul");
        flight1.setDepartureDateTime(LocalDateTime.now().plusDays(30).withHour(10).withMinute(0));
        flight1.setArrivalDateTime(LocalDateTime.now().plusDays(30).withHour(18).withMinute(30));
        flight1.setFlightClass("Economy");
        flight1.setAvailableSeats(45);
        flight1.setPrice(new BigDecimal("850.00"));
        flight1.setDurationMinutes(510);
        flight1.setHasMeal(true);
        flight1.setHasEntertainment(true);
        flight1.setBaggageAllowance(23);
        flight1.setIsDirectFlight(true);
        flight1.setDescription("New York'tan İstanbul'a direkt uçuş.");
        flight1.setRating(4.8);
        flight1.setTotalReviews(1250);
        flight1.setIsAvailable(true);
        flightBookingRepository.save(flight1);
        
        FlightBooking flight2 = new FlightBooking();
        flight2.setAirlineName("Turkish Airlines");
        flight2.setFlightNumber("TK002");
        flight2.setDepartureCity("London");
        flight2.setArrivalCity("İstanbul");
        flight2.setDepartureDateTime(LocalDateTime.now().plusDays(30).withHour(14).withMinute(0));
        flight2.setArrivalDateTime(LocalDateTime.now().plusDays(30).withHour(20).withMinute(30));
        flight2.setFlightClass("Business");
        flight2.setAvailableSeats(12);
        flight2.setPrice(new BigDecimal("2500.00"));
        flight2.setDurationMinutes(390);
        flight2.setHasMeal(true);
        flight2.setHasEntertainment(true);
        flight2.setBaggageAllowance(32);
        flight2.setIsDirectFlight(true);
        flight2.setDescription("Londra'dan İstanbul'a business class uçuş.");
        flight2.setRating(4.9);
        flight2.setTotalReviews(890);
        flight2.setIsAvailable(true);
        flightBookingRepository.save(flight2);
        
        // Örnek Transfer Hizmeti
        TransferService transfer1 = new TransferService();
        transfer1.setCompanyName("İstanbul VIP Transfer");
        transfer1.setVehicleType("SUV");
        transfer1.setPassengerCapacity(6);
        transfer1.setServiceType("Airport-Hospital");
        transfer1.setPickupLocation("İstanbul Havalimanı");
        transfer1.setDropoffLocation("Acıbadem Maslak Hastanesi");
        transfer1.setPrice(new BigDecimal("250.00"));
        transfer1.setDurationMinutes(45);
        transfer1.setDistanceKm(35.5);
        transfer1.setHasMeetAndGreet(true);
        transfer1.setHasLuggageAssistance(true);
        transfer1.setHasWifi(true);
        transfer1.setHasChildSeat(true);
        transfer1.setPhone("+90 212 555 0300");
        transfer1.setEmail("info@istanbulviptransfer.com");
        transfer1.setDescription("Havalimanından hastaneye konforlu transfer hizmeti.");
        transfer1.setRating(4.8);
        transfer1.setTotalReviews(650);
        transfer1.setIsAvailable(true);
        transferServiceRepository.save(transfer1);
        
        TransferService transfer2 = new TransferService();
        transfer2.setCompanyName("Premium Transfer Services");
        transfer2.setVehicleType("Sedan");
        transfer2.setPassengerCapacity(4);
        transfer2.setServiceType("Airport-Hotel");
        transfer2.setPickupLocation("İstanbul Havalimanı");
        transfer2.setDropoffLocation("Hilton İstanbul Maslak");
        transfer2.setPrice(new BigDecimal("180.00"));
        transfer2.setDurationMinutes(48);
        transfer2.setDistanceKm(36.0);
        transfer2.setHasMeetAndGreet(true);
        transfer2.setHasLuggageAssistance(true);
        transfer2.setHasWifi(true);
        transfer2.setHasChildSeat(false);
        transfer2.setPhone("+90 212 555 0400");
        transfer2.setEmail("info@premiumtransfer.com");
        transfer2.setDescription("Havalimanından otele transfer hizmeti.");
        transfer2.setRating(4.6);
        transfer2.setTotalReviews(420);
        transfer2.setIsAvailable(true);
        transferServiceRepository.save(transfer2);
        
        // Örnek Paket Tur
        TravelPackage package1 = new TravelPackage();
        package1.setPackageName("Premium Sağlık Paketi");
        package1.setPackageType("Premium");
        package1.setDurationDays(7);
        package1.setTotalPrice(new BigDecimal("15000.00"));
        package1.setDiscountPercentage(new BigDecimal("15.00"));
        package1.setFinalPrice(new BigDecimal("12750.00"));
        package1.setIncludesFlight(true);
        package1.setIncludesAccommodation(true);
        package1.setIncludesTransfer(true);
        package1.setIncludesCarRental(false);
        package1.setIncludesVisaService(true);
        package1.setIncludesTranslation(true);
        package1.setIncludesInsurance(true);
        package1.setDescription("7 günlük kapsamlı sağlık turizmi paketi. Uçak bileti, konaklama, transfer, vize hizmeti, tercüman ve sigorta dahil.");
        package1.setRating(4.9);
        package1.setTotalReviews(180);
        package1.setIsActive(true);
        package1.setHospital(hospital1);
        package1.setDoctor(doctor1);
        package1.setAccommodation(acc1);
        travelPackageRepository.save(package1);
        
        TravelPackage package2 = new TravelPackage();
        package2.setPackageName("Standart Sağlık Paketi");
        package2.setPackageType("Standard");
        package2.setDurationDays(5);
        package2.setTotalPrice(new BigDecimal("8000.00"));
        package2.setDiscountPercentage(new BigDecimal("10.00"));
        package2.setFinalPrice(new BigDecimal("7200.00"));
        package2.setIncludesFlight(false);
        package2.setIncludesAccommodation(true);
        package2.setIncludesTransfer(true);
        package2.setIncludesCarRental(false);
        package2.setIncludesVisaService(false);
        package2.setIncludesTranslation(true);
        package2.setIncludesInsurance(true);
        package2.setDescription("5 günlük standart sağlık turizmi paketi. Konaklama, transfer, tercüman ve sigorta dahil.");
        package2.setRating(4.7);
        package2.setTotalReviews(250);
        package2.setIsActive(true);
        package2.setHospital(hospital2);
        package2.setDoctor(doctor3);
        package2.setAccommodation(acc2);
        travelPackageRepository.save(package2);
        
        System.out.println("Örnek veriler başarıyla eklendi!");
    }
}

