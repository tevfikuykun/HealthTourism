package com.healthtourism.invoiceservice.controller;
import com.healthtourism.invoiceservice.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "*")
public class InvoiceController {
    @Autowired
    private InvoiceService service;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAll(@RequestParam Map<String, Object> params) {
        return ResponseEntity.ok(service.getAll(params));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPDF(@PathVariable Long id) {
        return ResponseEntity.ok(service.downloadPDF(id));
    }
}

