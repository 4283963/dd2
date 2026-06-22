package com.wharf.billing.controller;

import com.wharf.billing.entity.WharfInvoice;
import com.wharf.billing.service.BerthBillingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "http://localhost:3000")
public class WharfInvoiceController {

    private final BerthBillingService billingService;

    public WharfInvoiceController(BerthBillingService billingService) {
        this.billingService = billingService;
    }

    @PostMapping("/generate/{berthRecordId}")
    public ResponseEntity<WharfInvoice> generate(@PathVariable Long berthRecordId) {
        return ResponseEntity.ok(billingService.generateInvoice(berthRecordId));
    }

    @GetMapping
    public ResponseEntity<List<WharfInvoice>> list() {
        return ResponseEntity.ok(billingService.listAllInvoices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WharfInvoice> getById(@PathVariable Long id) {
        return ResponseEntity.ok(billingService.getInvoiceById(id)
                .orElseThrow(() -> new IllegalArgumentException("账单不存在, ID: " + id)));
    }
}
