package com.wharf.billing.repository;

import com.wharf.billing.entity.ShipBerthRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipBerthRecordRepository extends JpaRepository<ShipBerthRecord, Long> {
}
