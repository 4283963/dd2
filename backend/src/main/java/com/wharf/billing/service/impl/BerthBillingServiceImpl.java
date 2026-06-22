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

    private static final BigDecimal NIGHT_SURCHARGE_RATE = new BigDecimal("0.2");
    private static final int NIGHT_START_HOUR = 22;
    private static final int NIGHT_END_HOUR = 6;

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
        BigDecimal nightHours = calculateNightHours(record.getBerthTime(), record.getLeaveTime());
        BigDecimal normalHours = record.getBerthDurationHours().subtract(nightHours);
        BigDecimal nightSurchargeAmount = unitPrice.multiply(nightHours)
                .multiply(NIGHT_SURCHARGE_RATE)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal baseAmount = unitPrice.multiply(record.getBerthDurationHours())
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = baseAmount.add(nightSurchargeAmount);

        WharfInvoice invoice = new WharfInvoice();
        invoice.setBerthRecordId(record.getId());
        invoice.setShipName(record.getShipName());
        invoice.setTonnage(record.getTonnage());
        invoice.setBerthDurationHours(record.getBerthDurationHours());
        invoice.setRateType(record.getRateType());
        invoice.setUnitPrice(unitPrice);
        invoice.setNightHours(nightHours);
        invoice.setNightSurchargeAmount(nightSurchargeAmount);
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

    private BigDecimal calculateNightHours(LocalDateTime berthTime, LocalDateTime leaveTime) {
        if (berthTime == null || leaveTime == null || !leaveTime.isAfter(berthTime)) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalNightHours = BigDecimal.ZERO;
        LocalDateTime cursor = berthTime;

        while (cursor.isBefore(leaveTime)) {
            LocalDateTime nextHour = cursor.plusHours(1).withMinute(0).withSecond(0).withNano(0);
            if (nextHour.isAfter(leaveTime)) {
                nextHour = leaveTime;
            }

            BigDecimal segmentHours = new BigDecimal(
                    nextHour.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
                            - cursor.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .divide(new BigDecimal(3600 * 1000), 4, RoundingMode.HALF_UP);

            int hourOfDay = cursor.getHour();
            if (hourOfDay >= NIGHT_START_HOUR || hourOfDay < NIGHT_END_HOUR) {
                totalNightHours = totalNightHours.add(segmentHours);
            }

            cursor = nextHour;
        }

        return totalNightHours.setScale(2, RoundingMode.HALF_UP);
    }
}
