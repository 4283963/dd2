package com.wharf.billing.service;

import com.wharf.billing.entity.ShipBerthRecord;
import com.wharf.billing.entity.WharfInvoice;

import java.util.List;
import java.util.Optional;

public interface BerthBillingService {

    List<ShipBerthRecord> listAllBerthRecords();

    ShipBerthRecord getBerthRecordById(Long id);

    ShipBerthRecord saveBerthRecord(ShipBerthRecord record);

    WharfInvoice generateInvoice(Long berthRecordId);

    Optional<WharfInvoice> getInvoiceById(Long id);

    List<WharfInvoice> listAllInvoices();

    Optional<WharfInvoice> getInvoiceByBerthRecordId(Long berthRecordId);
}
