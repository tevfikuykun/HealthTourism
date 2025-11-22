package com.healthtourism.faqservice.service;
import com.healthtourism.faqservice.dto.FAQDTO;
import com.healthtourism.faqservice.entity.FAQ;
import com.healthtourism.faqservice.repository.FAQRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FAQService {
    @Autowired
    private FAQRepository faqRepository;
    
    public List<FAQDTO> getAllActiveFAQs() {
        return faqRepository.findAllActiveOrderByDisplayOrder()
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public List<FAQDTO> getFAQsByCategory(String category) {
        return faqRepository.findByCategoryAndIsActiveTrue(category)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public FAQDTO getFAQById(Long id) {
        FAQ faq = faqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SSS bulunamadı"));
        return convertToDTO(faq);
    }
    
    public FAQDTO createFAQ(FAQ faq) {
        faq.setIsActive(true);
        return convertToDTO(faqRepository.save(faq));
    }
    
    private FAQDTO convertToDTO(FAQ faq) {
        FAQDTO dto = new FAQDTO();
        dto.setId(faq.getId());
        dto.setQuestion(faq.getQuestion());
        dto.setAnswer(faq.getAnswer());
        dto.setCategory(faq.getCategory());
        dto.setDisplayOrder(faq.getDisplayOrder());
        dto.setIsActive(faq.getIsActive());
        return dto;
    }
}

