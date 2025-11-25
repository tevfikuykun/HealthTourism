package com.healthtourism.searchservice.service;

import com.healthtourism.searchservice.document.DoctorDocument;
import com.healthtourism.searchservice.document.HospitalDocument;
import com.healthtourism.searchservice.repository.DoctorSearchRepository;
import com.healthtourism.searchservice.repository.HospitalSearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {
    
    @Autowired
    private HospitalSearchRepository hospitalRepository;
    
    @Autowired
    private DoctorSearchRepository doctorRepository;
    
    public List<HospitalDocument> searchHospitals(String query) {
        return hospitalRepository.findByNameContaining(query);
    }
    
    public List<HospitalDocument> searchHospitalsByCity(String city) {
        return hospitalRepository.findByCity(city);
    }
    
    public List<HospitalDocument> searchHospitalsByRating(Double minRating) {
        return hospitalRepository.findByRatingGreaterThanEqual(minRating);
    }
    
    public List<DoctorDocument> searchDoctors(String query) {
        return doctorRepository.findByFirstNameContainingOrLastNameContaining(query, query);
    }
    
    public List<DoctorDocument> searchDoctorsBySpecialty(String specialty) {
        return doctorRepository.findBySpecialty(specialty);
    }
    
    public List<DoctorDocument> searchDoctorsByRating(Double minRating) {
        return doctorRepository.findByRatingGreaterThanEqual(minRating);
    }
    
    public void indexHospital(HospitalDocument hospital) {
        hospitalRepository.save(hospital);
    }
    
    public void indexDoctor(DoctorDocument doctor) {
        doctorRepository.save(doctor);
    }
}


