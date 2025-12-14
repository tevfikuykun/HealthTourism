package com.healthtourism.invoiceservice.service;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class InvoiceService {
    public List<Map<String, Object>> getAll(Map<String, Object> params) {
        return new ArrayList<>();
    }

    public Map<String, Object> getById(Long id) {
        Map<String, Object> invoice = new HashMap<>();
        invoice.put("id", id);
        invoice.put("amount", 1000.0);
        invoice.put("status", "paid");
        return invoice;
    }

    public byte[] downloadPDF(Long id) {
        return new byte[0];
    }
}

