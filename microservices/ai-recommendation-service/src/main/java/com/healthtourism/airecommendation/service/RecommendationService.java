package com.healthtourism.airecommendation.service;

import com.healthtourism.airecommendation.dto.RecommendationRequest;
import com.healthtourism.airecommendation.dto.RecommendationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * AI-Powered Smart Matching Service
 * Recommends best doctors and packages based on user symptoms and preferences
 */
@Service
public class RecommendationService {
    
    @Value("${hospital.service.url:http://localhost:8002}")
    private String hospitalServiceUrl;
    
    @Value("${doctor.service.url:http://localhost:8003}")
    private String doctorServiceUrl;
    
    @Value("${package.service.url:http://localhost:8006}")
    private String packageServiceUrl;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    public RecommendationResponse getRecommendations(RecommendationRequest request) {
        RecommendationResponse response = new RecommendationResponse();
        
        // Simulate AI-based matching algorithm
        // In production, this would use ML models or external AI services
        
        List<RecommendationResponse.DoctorRecommendation> doctors = findMatchingDoctors(request);
        List<RecommendationResponse.PackageRecommendation> packages = findMatchingPackages(request);
        
        response.setTopDoctors(doctors);
        response.setTopPackages(packages);
        response.setReasoning(generateReasoning(request, doctors, packages));
        response.setConfidenceScore(calculateConfidenceScore(doctors, packages));
        
        return response;
    }
    
    private List<RecommendationResponse.DoctorRecommendation> findMatchingDoctors(RecommendationRequest request) {
        List<RecommendationResponse.DoctorRecommendation> recommendations = new ArrayList<>();
        
        // Simulate AI matching - In production, call actual services and use ML models
        RecommendationResponse.DoctorRecommendation doc1 = new RecommendationResponse.DoctorRecommendation();
        doc1.setDoctorId(1L);
        doc1.setDoctorName("Dr. Ahmet Yılmaz");
        doc1.setSpecialization(request.getTreatmentType() != null ? request.getTreatmentType() : "General");
        doc1.setHospitalName("Ankara Medical Center");
        doc1.setMatchScore(calculateDoctorMatchScore(request, "DENTAL", "Ankara"));
        doc1.setMatchReason("Uzmanlık alanı ve konum uyumu");
        doc1.setEstimatedPrice(new BigDecimal("5000.00"));
        
        RecommendationResponse.DoctorRecommendation doc2 = new RecommendationResponse.DoctorRecommendation();
        doc2.setDoctorId(2L);
        doc2.setDoctorName("Dr. Ayşe Demir");
        doc2.setSpecialization(request.getTreatmentType() != null ? request.getTreatmentType() : "Cardiology");
        doc2.setHospitalName("Istanbul Health Center");
        doc2.setMatchScore(calculateDoctorMatchScore(request, "CARDIOLOGY", "Istanbul"));
        doc2.setMatchReason("Yüksek başarı oranı ve hasta memnuniyeti");
        doc2.setEstimatedPrice(new BigDecimal("8000.00"));
        
        RecommendationResponse.DoctorRecommendation doc3 = new RecommendationResponse.DoctorRecommendation();
        doc3.setDoctorId(3L);
        doc3.setDoctorName("Dr. Mehmet Kaya");
        doc3.setSpecialization(request.getTreatmentType() != null ? request.getTreatmentType() : "Orthopedics");
        doc3.setHospitalName("Izmir Medical Hospital");
        doc3.setMatchScore(calculateDoctorMatchScore(request, "ORTHOPEDICS", "Izmir"));
        doc3.setMatchReason("Bütçe uyumu ve erişilebilirlik");
        doc3.setEstimatedPrice(new BigDecimal("6000.00"));
        
        recommendations.add(doc1);
        recommendations.add(doc2);
        recommendations.add(doc3);
        
        // Sort by match score descending
        recommendations.sort((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()));
        
        return recommendations.subList(0, Math.min(3, recommendations.size()));
    }
    
    private List<RecommendationResponse.PackageRecommendation> findMatchingPackages(RecommendationRequest request) {
        List<RecommendationResponse.PackageRecommendation> recommendations = new ArrayList<>();
        
        // Simulate AI matching
        RecommendationResponse.PackageRecommendation pkg1 = new RecommendationResponse.PackageRecommendation();
        pkg1.setPackageId(1L);
        pkg1.setPackageName("Premium Dental Care Package");
        pkg1.setHospitalName("Ankara Medical Center");
        pkg1.setMatchScore(calculatePackageMatchScore(request, "DENTAL", 10000));
        pkg1.setMatchReason("Belirtilerinize en uygun paket");
        pkg1.setPrice(new BigDecimal("10000.00"));
        pkg1.setIncludedServices(Arrays.asList("Consultation", "Treatment", "Follow-up"));
        
        RecommendationResponse.PackageRecommendation pkg2 = new RecommendationResponse.PackageRecommendation();
        pkg2.setPackageId(2L);
        pkg2.setPackageName("Complete Health Check Package");
        pkg2.setHospitalName("Istanbul Health Center");
        pkg2.setMatchScore(calculatePackageMatchScore(request, "GENERAL", 15000));
        pkg2.setMatchReason("Kapsamlı sağlık kontrolü");
        pkg2.setPrice(new BigDecimal("15000.00"));
        pkg2.setIncludedServices(Arrays.asList("Full checkup", "Lab tests", "Consultation"));
        
        RecommendationResponse.PackageRecommendation pkg3 = new RecommendationResponse.PackageRecommendation();
        pkg3.setPackageId(3L);
        pkg3.setPackageName("Cardiac Care Package");
        pkg3.setHospitalName("Izmir Medical Hospital");
        pkg3.setMatchScore(calculatePackageMatchScore(request, "CARDIOLOGY", 20000));
        pkg3.setMatchReason("Uzmanlaşmış kardiyoloji paketi");
        pkg3.setPrice(new BigDecimal("20000.00"));
        pkg3.setIncludedServices(Arrays.asList("ECG", "Echocardiography", "Consultation"));
        
        recommendations.add(pkg1);
        recommendations.add(pkg2);
        recommendations.add(pkg3);
        
        // Sort by match score descending
        recommendations.sort((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()));
        
        return recommendations.subList(0, Math.min(3, recommendations.size()));
    }
    
    private Double calculateDoctorMatchScore(RecommendationRequest request, String specialization, String city) {
        double score = 50.0; // Base score
        
        // Specialization match
        if (request.getTreatmentType() != null && request.getTreatmentType().equalsIgnoreCase(specialization)) {
            score += 30.0;
        }
        
        // City match
        if (request.getPreferredCity() != null && request.getPreferredCity().equalsIgnoreCase(city)) {
            score += 20.0;
        }
        
        return Math.min(100.0, score);
    }
    
    private Double calculatePackageMatchScore(RecommendationRequest request, String packageType, Integer packagePrice) {
        double score = 50.0; // Base score
        
        // Treatment type match
        if (request.getTreatmentType() != null && request.getTreatmentType().equalsIgnoreCase(packageType)) {
            score += 30.0;
        }
        
        // Budget match
        if (request.getBudgetRange() != null && packagePrice != null) {
            if (packagePrice <= request.getBudgetRange()) {
                score += 20.0;
            } else if (packagePrice <= request.getBudgetRange() * 1.2) {
                score += 10.0;
            }
        }
        
        return Math.min(100.0, score);
    }
    
    private String generateReasoning(RecommendationRequest request, 
                                     List<RecommendationResponse.DoctorRecommendation> doctors,
                                     List<RecommendationResponse.PackageRecommendation> packages) {
        StringBuilder reasoning = new StringBuilder();
        reasoning.append("Belirtilerinize ve tercihlerinize göre ");
        reasoning.append(doctors.size()).append(" doktor ve ");
        reasoning.append(packages.size()).append(" paket önerilmiştir. ");
        
        if (request.getTreatmentType() != null) {
            reasoning.append("Öneriler, ").append(request.getTreatmentType()).append(" alanındaki uzmanlığa göre filtrelenmiştir. ");
        }
        
        if (request.getPreferredCity() != null) {
            reasoning.append("Konum tercihiniz (").append(request.getPreferredCity()).append(") dikkate alınmıştır. ");
        }
        
        reasoning.append("En yüksek eşleşme skoruna sahip seçenekler önceliklendirilmiştir.");
        
        return reasoning.toString();
    }
    
    private Double calculateConfidenceScore(List<RecommendationResponse.DoctorRecommendation> doctors,
                                           List<RecommendationResponse.PackageRecommendation> packages) {
        if (doctors.isEmpty() && packages.isEmpty()) {
            return 0.0;
        }
        
        double totalScore = 0.0;
        int count = 0;
        
        for (RecommendationResponse.DoctorRecommendation doctor : doctors) {
            totalScore += doctor.getMatchScore() / 100.0;
            count++;
        }
        
        for (RecommendationResponse.PackageRecommendation pkg : packages) {
            totalScore += pkg.getMatchScore() / 100.0;
            count++;
        }
        
        return count > 0 ? totalScore / count : 0.0;
    }
}
