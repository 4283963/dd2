package com.wharf.billing.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ship_berth_record")
public class ShipBerthRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ship_name", nullable = false)
    private String shipName;

    @Column(name = "tonnage", nullable = false)
    private BigDecimal tonnage;

    @Column(name = "berth_duration_hours", nullable = false)
    private BigDecimal berthDurationHours;

    @Column(name = "rate_type", nullable = false)
    private String rateType;

    @Column(name = "berth_time")
    private LocalDateTime berthTime;

    @Column(name = "leave_time")
    private LocalDateTime leaveTime;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShipName() {
        return shipName;
    }

    public void setShipName(String shipName) {
        this.shipName = shipName;
    }

    public BigDecimal getTonnage() {
        return tonnage;
    }

    public void setTonnage(BigDecimal tonnage) {
        this.tonnage = tonnage;
    }

    public BigDecimal getBerthDurationHours() {
        return berthDurationHours;
    }

    public void setBerthDurationHours(BigDecimal berthDurationHours) {
        this.berthDurationHours = berthDurationHours;
    }

    public String getRateType() {
        return rateType;
    }

    public void setRateType(String rateType) {
        this.rateType = rateType;
    }

    public LocalDateTime getBerthTime() {
        return berthTime;
    }

    public void setBerthTime(LocalDateTime berthTime) {
        this.berthTime = berthTime;
    }

    public LocalDateTime getLeaveTime() {
        return leaveTime;
    }

    public void setLeaveTime(LocalDateTime leaveTime) {
        this.leaveTime = leaveTime;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
