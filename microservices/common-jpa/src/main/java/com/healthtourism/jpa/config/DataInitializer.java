package com.healthtourism.jpa.config;

import com.healthtourism.jpa.entity.seed.Country;
import com.healthtourism.jpa.entity.seed.Role;
import com.healthtourism.jpa.entity.seed.TreatmentBranch;
import com.healthtourism.jpa.repository.seed.CountryRepository;
import com.healthtourism.jpa.repository.seed.RoleRepository;
import com.healthtourism.jpa.repository.seed.TreatmentBranchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * Data Initializer
 * 
 * Seeds initial/reference data during application startup.
 * This runs automatically when the application starts.
 * 
 * Seeds:
 * - Countries (Ülkeler)
 * - Treatment Branches (Tedavi Branşları)
 * - Roles (Roller)
 * 
 * Only seeds data if the tables are empty (idempotent).
 */
@Component
@Order(1) // Run early in the startup process
public class DataInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    @Autowired
    private CountryRepository countryRepository;
    
    @Autowired
    private TreatmentBranchRepository treatmentBranchRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        logger.info("Starting data initialization...");
        
        if (countryRepository.countActive() == 0) {
            seedCountries();
            logger.info("Countries seeded successfully");
        } else {
            logger.info("Countries already exist, skipping seed");
        }
        
        if (treatmentBranchRepository.countActive() == 0) {
            seedTreatmentBranches();
            logger.info("Treatment branches seeded successfully");
        } else {
            logger.info("Treatment branches already exist, skipping seed");
        }
        
        if (roleRepository.countActive() == 0) {
            seedRoles();
            logger.info("Roles seeded successfully");
        } else {
            logger.info("Roles already exist, skipping seed");
        }
        
        logger.info("Data initialization completed");
    }
    
    /**
     * Seed Countries (Ülkeler)
     */
    private void seedCountries() {
        List<Country> countries = Arrays.asList(
            createCountry("US", "United States", "United States", "USA", "840", "+1", 1),
            createCountry("TR", "Turkey", "Türkiye", "TUR", "792", "+90", 2),
            createCountry("GB", "United Kingdom", "United Kingdom", "GBR", "826", "+44", 3),
            createCountry("DE", "Germany", "Deutschland", "DEU", "276", "+49", 4),
            createCountry("FR", "France", "France", "FRA", "250", "+33", 5),
            createCountry("IT", "Italy", "Italia", "ITA", "380", "+39", 6),
            createCountry("ES", "Spain", "España", "ESP", "724", "+34", 7),
            createCountry("NL", "Netherlands", "Nederland", "NLD", "528", "+31", 8),
            createCountry("BE", "Belgium", "België", "BEL", "56", "+32", 9),
            createCountry("CH", "Switzerland", "Schweiz", "CHE", "756", "+41", 10),
            createCountry("AT", "Austria", "Österreich", "AUT", "40", "+43", 11),
            createCountry("SE", "Sweden", "Sverige", "SWE", "752", "+46", 12),
            createCountry("NO", "Norway", "Norge", "NOR", "578", "+47", 13),
            createCountry("DK", "Denmark", "Danmark", "DNK", "208", "+45", 14),
            createCountry("FI", "Finland", "Suomi", "FIN", "246", "+358", 15),
            createCountry("PL", "Poland", "Polska", "POL", "616", "+48", 16),
            createCountry("CZ", "Czech Republic", "Česká republika", "CZE", "203", "+420", 17),
            createCountry("GR", "Greece", "Ελλάδα", "GRC", "300", "+30", 18),
            createCountry("PT", "Portugal", "Portugal", "PRT", "620", "+351", 19),
            createCountry("IE", "Ireland", "Éire", "IRL", "372", "+353", 20),
            createCountry("RU", "Russia", "Россия", "RUS", "643", "+7", 21),
            createCountry("UA", "Ukraine", "Україна", "UKR", "804", "+380", 22),
            createCountry("SA", "Saudi Arabia", "السعودية", "SAU", "682", "+966", 23),
            createCountry("AE", "United Arab Emirates", "الإمارات العربية المتحدة", "ARE", "784", "+971", 24),
            createCountry("QA", "Qatar", "قطر", "QAT", "634", "+974", 25),
            createCountry("KW", "Kuwait", "الكويت", "KWT", "414", "+965", 26),
            createCountry("BH", "Bahrain", "البحرين", "BHR", "48", "+973", 27),
            createCountry("OM", "Oman", "عُمان", "OMN", "512", "+968", 28),
            createCountry("JO", "Jordan", "الأردن", "JOR", "400", "+962", 29),
            createCountry("LB", "Lebanon", "لبنان", "LBN", "422", "+961", 30),
            createCountry("EG", "Egypt", "مصر", "EGY", "818", "+20", 31),
            createCountry("IL", "Israel", "ישראל", "ISR", "376", "+972", 32),
            createCountry("IR", "Iran", "ایران", "IRN", "364", "+98", 33),
            createCountry("IQ", "Iraq", "العراق", "IRQ", "368", "+964", 34),
            createCountry("CN", "China", "中国", "CHN", "156", "+86", 35),
            createCountry("JP", "Japan", "日本", "JPN", "392", "+81", 36),
            createCountry("KR", "South Korea", "대한민국", "KOR", "410", "+82", 37),
            createCountry("IN", "India", "भारत", "IND", "356", "+91", 38),
            createCountry("TH", "Thailand", "ประเทศไทย", "THA", "764", "+66", 39),
            createCountry("SG", "Singapore", "Singapore", "SGP", "702", "+65", 40),
            createCountry("MY", "Malaysia", "Malaysia", "MYS", "458", "+60", 41),
            createCountry("ID", "Indonesia", "Indonesia", "IDN", "360", "+62", 42),
            createCountry("PH", "Philippines", "Pilipinas", "PHL", "608", "+63", 43),
            createCountry("VN", "Vietnam", "Việt Nam", "VNM", "704", "+84", 44),
            createCountry("AU", "Australia", "Australia", "AUS", "36", "+61", 45),
            createCountry("NZ", "New Zealand", "New Zealand", "NZL", "554", "+64", 46),
            createCountry("CA", "Canada", "Canada", "CAN", "124", "+1", 47),
            createCountry("MX", "Mexico", "México", "MEX", "484", "+52", 48),
            createCountry("BR", "Brazil", "Brasil", "BRA", "76", "+55", 49),
            createCountry("AR", "Argentina", "Argentina", "ARG", "32", "+54", 50),
            createCountry("ZA", "South Africa", "South Africa", "ZAF", "710", "+27", 51)
        );
        
        countryRepository.saveAll(countries);
    }
    
    private Country createCountry(String code, String name, String nativeName, 
                                   String codeAlpha3, String numericCode, 
                                   String phoneCode, Integer displayOrder) {
        Country country = new Country();
        country.setCode(code);
        country.setName(name);
        country.setNativeName(nativeName);
        country.setCodeAlpha3(codeAlpha3);
        country.setNumericCode(numericCode);
        country.setPhoneCode(phoneCode);
        country.setDisplayOrder(displayOrder);
        country.setIsActive(true);
        return country;
    }
    
    /**
     * Seed Treatment Branches (Tedavi Branşları)
     */
    private void seedTreatmentBranches() {
        List<TreatmentBranch> branches = Arrays.asList(
            createBranch("CARDIO", "Cardiology", "Kardiyoloji", 
                        "Heart and cardiovascular system diseases", "Surgical", 1),
            createBranch("ORTHO", "Orthopedics", "Ortopedi", 
                        "Bone, joint, and muscle diseases", "Surgical", 2),
            createBranch("ONCO", "Oncology", "Onkoloji", 
                        "Cancer treatment", "Medical", 3),
            createBranch("NEURO", "Neurology", "Nöroloji", 
                        "Nervous system diseases", "Medical", 4),
            createBranch("NEUROSURG", "Neurosurgery", "Beyin Cerrahisi", 
                        "Brain and nervous system surgery", "Surgical", 5),
            createBranch("PLASTIC", "Plastic Surgery", "Plastik Cerrahi", 
                        "Reconstructive and aesthetic surgery", "Surgical", 6),
            createBranch("OPHTHAL", "Ophthalmology", "Göz Hastalıkları", 
                        "Eye diseases and surgery", "Surgical", 7),
            createBranch("ENT", "Ear, Nose, Throat", "Kulak Burun Boğaz", 
                        "Ear, nose, and throat diseases", "Surgical", 8),
            createBranch("DERMATO", "Dermatology", "Dermatoloji", 
                        "Skin diseases", "Medical", 9),
            createBranch("UROLO", "Urology", "Üroloji", 
                        "Urinary system diseases", "Surgical", 10),
            createBranch("GYNECO", "Gynecology", "Kadın Hastalıkları", 
                        "Women's health and reproductive system", "Surgical", 11),
            createBranch("PEDIAT", "Pediatrics", "Pediatri", 
                        "Children's diseases", "Medical", 12),
            createBranch("GENERAL", "General Surgery", "Genel Cerrahi", 
                        "General surgical procedures", "Surgical", 13),
            createBranch("INTERNAL", "Internal Medicine", "İç Hastalıkları", 
                        "Internal medicine and general health", "Medical", 14),
            createBranch("RADIOLOGY", "Radiology", "Radyoloji", 
                        "Medical imaging and diagnostics", "Diagnostic", 15),
            createBranch("PATHOLOGY", "Pathology", "Patoloji", 
                        "Disease diagnosis through laboratory analysis", "Diagnostic", 16),
            createBranch("ANESTH", "Anesthesiology", "Anesteziyoloji", 
                        "Anesthesia and pain management", "Medical", 17),
            createBranch("EMERGENCY", "Emergency Medicine", "Acil Tıp", 
                        "Emergency medical care", "Medical", 18),
            createBranch("PSYCHIATRY", "Psychiatry", "Psikiyatri", 
                        "Mental health and psychiatric treatment", "Medical", 19),
            createBranch("PHYSICAL", "Physical Therapy", "Fizik Tedavi", 
                        "Physical rehabilitation and therapy", "Medical", 20),
            createBranch("DENTAL", "Dentistry", "Diş Hekimliği", 
                        "Dental and oral health", "Surgical", 21),
            createBranch("TRANSPLANT", "Transplant Surgery", "Organ Nakli", 
                        "Organ transplantation", "Surgical", 22),
            createBranch("BARIATRIC", "Bariatric Surgery", "Obezite Cerrahisi", 
                        "Weight loss surgery", "Surgical", 23),
            createBranch("IVF", "IVF and Infertility", "Tüp Bebek", 
                        "In vitro fertilization and infertility treatment", "Medical", 24),
            createBranch("COSMETIC", "Cosmetic Surgery", "Estetik Cerrahi", 
                        "Aesthetic and cosmetic procedures", "Surgical", 25)
        );
        
        treatmentBranchRepository.saveAll(branches);
    }
    
    private TreatmentBranch createBranch(String code, String name, String nameTr, 
                                         String description, String category, Integer displayOrder) {
        TreatmentBranch branch = new TreatmentBranch();
        branch.setCode(code);
        branch.setName(name);
        branch.setNameTr(nameTr);
        branch.setDescription(description);
        branch.setCategory(category);
        branch.setDisplayOrder(displayOrder);
        branch.setIsActive(true);
        return branch;
    }
    
    /**
     * Seed Roles (Roller)
     */
    private void seedRoles() {
        List<Role> roles = Arrays.asList(
            createRole("USER", "User", "Regular system user", false, true, 100),
            createRole("ADMIN", "Administrator", "System administrator with full access", false, true, 10),
            createRole("DOCTOR", "Doctor", "Medical doctor/physician", false, false, 50),
            createRole("NURSE", "Nurse", "Medical nurse", false, false, 60),
            createRole("PATIENT", "Patient", "Patient user", true, false, 90),
            createRole("STAFF", "Staff", "Hospital/clinic staff", false, false, 70),
            createRole("MODERATOR", "Moderator", "Content moderator", false, false, 80),
            createRole("SUPER_ADMIN", "Super Administrator", "Super administrator with highest privileges", false, true, 1)
        );
        
        roleRepository.saveAll(roles);
    }
    
    private Role createRole(String code, String name, String description, 
                           boolean isDefault, boolean isSystem, Integer permissionLevel) {
        Role role = new Role();
        role.setCode(code);
        role.setName(name);
        role.setDescription(description);
        role.setIsDefault(isDefault);
        role.setIsSystem(isSystem);
        role.setPermissionLevel(permissionLevel);
        role.setIsActive(true);
        return role;
    }
}

