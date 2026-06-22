package com.wharf.billing.init;

import com.wharf.billing.entity.ShipBerthRecord;
import com.wharf.billing.repository.ShipBerthRecordRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ShipBerthRecordRepository berthRecordRepository;

    public DataInitializer(ShipBerthRecordRepository berthRecordRepository) {
        this.berthRecordRepository = berthRecordRepository;
    }

    @Override
    public void run(String... args) {
        if (berthRecordRepository.count() > 0) {
            return;
        }

        berthRecordRepository.save(buildRecord("顺丰168号", new BigDecimal("800"), new BigDecimal("6"), "TIER1", LocalDateTime.now().minusHours(6), LocalDateTime.now()));
        berthRecordRepository.save(buildRecord("海运之星", new BigDecimal("3200"), new BigDecimal("4"), "TIER2", LocalDateTime.now().minusHours(4), LocalDateTime.now()));
        berthRecordRepository.save(buildRecord("巨龙号", new BigDecimal("7500"), new BigDecimal("2"), "TIER3", LocalDateTime.now().minusHours(2), LocalDateTime.now()));
        berthRecordRepository.save(buildRecord("蓝鲸号", new BigDecimal("1200"), new BigDecimal("8"), "TIER2", LocalDateTime.now().minusHours(8), LocalDateTime.now()));
        berthRecordRepository.save(buildRecord("小渔船009", new BigDecimal("500"), new BigDecimal("3"), "TIER1", LocalDateTime.now().minusHours(3), LocalDateTime.now()));

        LocalDateTime tonight23 = LocalDateTime.now().withHour(23).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime tomorrow05 = tonight23.plusHours(6);
        berthRecordRepository.save(buildRecord("夜航者号", new BigDecimal("2000"), new BigDecimal("6"), "TIER2", tonight23, tomorrow05));

        LocalDateTime yesterday20 = LocalDateTime.now().minusDays(1).withHour(20).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime today04 = yesterday20.plusHours(8);
        berthRecordRepository.save(buildRecord("深夜运输船", new BigDecimal("6000"), new BigDecimal("8"), "TIER3", yesterday20, today04));
    }

    private ShipBerthRecord buildRecord(String shipName, BigDecimal tonnage, BigDecimal hours, String rateType,
                                        LocalDateTime berthTime, LocalDateTime leaveTime) {
        ShipBerthRecord record = new ShipBerthRecord();
        record.setShipName(shipName);
        record.setTonnage(tonnage);
        record.setBerthDurationHours(hours);
        record.setRateType(rateType);
        record.setBerthTime(berthTime);
        record.setLeaveTime(leaveTime);
        record.setCreateTime(LocalDateTime.now());
        return record;
    }
}
