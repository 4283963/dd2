package com.wharf.billing.controller;

import com.wharf.billing.entity.ShipBerthRecord;
import com.wharf.billing.service.BerthBillingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/berth-records")
@CrossOrigin(origins = "http://localhost:3000")
public class ShipBerthRecordController {

    private final BerthBillingService billingService;

    public ShipBerthRecordController(BerthBillingService billingService) {
        this.billingService = billingService;
    }

    @GetMapping
    public ResponseEntity<List<ShipBerthRecord>> list() {
        return ResponseEntity.ok(billingService.listAllBerthRecords());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShipBerthRecord> getById(@PathVariable Long id) {
        return ResponseEntity.ok(billingService.getBerthRecordById(id));
    }

    @PostMapping
    public ResponseEntity<ShipBerthRecord> create(@RequestBody ShipBerthRecord record) {
        return ResponseEntity.ok(billingService.saveBerthRecord(record));
    }
}
