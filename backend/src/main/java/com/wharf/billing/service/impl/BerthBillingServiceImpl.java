package com.wharf.billing.service.impl;

import com.wharf.billing.entity.ShipBerthRecord;
import com.wharf.billing.entity.WharfInvoice;
import com.wharf.billing.repository.ShipBerthRecordRepository;
import com.wharf.billing.repository.WharfInvoiceRepository;
import com.wharf.billing.service.BerthBillingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BerthBillingServiceImpl implements BerthBillingService {

    private static final BigDecimal TIER1_LIMIT = new BigDecimal("1000");
    private static final BigDecimal TIER2_LIMIT = new BigDecimal("5000");

    private static final BigDecimal RATE_TIER1 = new BigDecimal("5");
    private static final BigDecimal RATE_TIER2 = new BigDecimal("10");
    private static final BigDecimal RATE_TIER3 = new BigDecimal("20");

    private final ShipBerthRecordRepository berthRecordRepository;
    private final WharfInvoiceRepository invoiceRepository;

    public BerthBillingServiceImpl(ShipBerthRecordRepository berthRecordRepository,
                                   WharfInvoiceRepository invoiceRepository) {
        this.berthRecordRepository = berthRecordRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public List<ShipBerthRecord> listAllBerthRecords() {
        return berthRecordRepository.findAll();
    }

    @Override
    public ShipBerthRecord getBerthRecordById(Long id) {
        return berthRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("靠泊记录不存在, ID: " + id));
    }

    @Override
    public ShipBerthRecord saveBerthRecord(ShipBerthRecord record) {
        if (record.getCreateTime() == null) {
            record.setCreateTime(LocalDateTime.now());
        }
        return berthRecordRepository.save(record);
    }

    @Override
    @Transactional
    public WharfInvoice generateInvoice(Long berthRecordId) {
        ShipBerthRecord record = getBerthRecordById(berthRecordId);

        if (invoiceRepository.findByBerthRecordId(berthRecordId).isPresent()) {
            throw new IllegalStateException("该靠泊记录已生成过账单, berthRecordId: " + berthRecordId);
        }

        BigDecimal unitPrice = resolveUnitPrice(record.getTonnage());
        BigDecimal totalAmount = unitPrice.multiply(record.getBerthDurationHours())
                .setScale(2, RoundingMode.HALF_UP);

        WharfInvoice invoice = new WharfInvoice();
        invoice.setBerthRecordId(record.getId());
        invoice.setShipName(record.getShipName());
        invoice.setTonnage(record.getTonnage());
        invoice.setBerthDurationHours(record.getBerthDurationHours());
        invoice.setRateType(record.getRateType());
        invoice.setUnitPrice(unitPrice);
        invoice.setTotalAmount(totalAmount);
        invoice.setStatus("UNPAID");
        invoice.setCreateTime(LocalDateTime.now());

        return invoiceRepository.save(invoice);
    }

    @Override
    public Optional<WharfInvoice> getInvoiceById(Long id) {
        return invoiceRepository.findById(id);
    }

    @Override
    public List<WharfInvoice> listAllInvoices() {
        return invoiceRepository.findAll();
    }

    @Override
    public Optional<WharfInvoice> getInvoiceByBerthRecordId(Long berthRecordId) {
        return invoiceRepository.findByBerthRecordId(berthRecordId);
    }

    private BigDecimal resolveUnitPrice(BigDecimal tonnage) {
        if (tonnage == null) {
            throw new IllegalArgumentException("吨位不能为空");
        }
        if (tonnage.compareTo(TIER1_LIMIT) < 0) {
            return RATE_TIER1;
        }
        if (tonnage.compareTo(TIER2_LIMIT) < 0) {
            return RATE_TIER2;
        }
        return RATE_TIER3;
    }
}
