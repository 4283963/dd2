package com.wharf.billing.repository;

import com.wharf.billing.entity.WharfInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WharfInvoiceRepository extends JpaRepository<WharfInvoice, Long> {

    Optional<WharfInvoice> findByBerthRecordId(Long berthRecordId);
}
