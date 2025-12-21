package com.bbms.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "blood_stock")
public class BloodStock {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "blood_stock_seq")
    @SequenceGenerator(name = "blood_stock_seq", sequenceName = "blood_stock_seq", allocationSize = 1)
    private Long id;

    @Column(name = "blood_group", nullable = false, length = 5)
    private String bloodGroup;

    @Enumerated(EnumType.STRING)
    @Column(name = "component_type", nullable = false, length = 30)
    private ComponentType componentType = ComponentType.WHOLE_BLOOD;

    @Column(name = "units_available", nullable = false)
    private Integer unitsAvailable = 0;

    @Column(name = "unit_volume_ml")
    private Integer unitVolumeMl = 450;

    @Column(name = "collection_date")
    private LocalDate collectionDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "storage_location", length = 100)
    private String storageLocation;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private StockStatus status = StockStatus.AVAILABLE;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public BloodStock() {}

    public BloodStock(String bloodGroup, ComponentType componentType, Integer unitsAvailable) {
        this.bloodGroup = bloodGroup;
        this.componentType = componentType;
        this.unitsAvailable = unitsAvailable;
    }

    // Check if stock is expired
    public boolean isExpired() {
        if (expiryDate == null) return false;
        return expiryDate.isBefore(LocalDate.now());
    }

    // Check if expiring soon (within 7 days)
    public boolean isExpiringSoon() {
        if (expiryDate == null) return false;
        return expiryDate.isBefore(LocalDate.now().plusDays(7)) && !isExpired();
    }

    // Check if stock is low (less than 5 units)
    public boolean isLowStock() {
        return unitsAvailable != null && unitsAvailable < 5;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public ComponentType getComponentType() { return componentType; }
    public void setComponentType(ComponentType componentType) { this.componentType = componentType; }

    public Integer getUnitsAvailable() { return unitsAvailable; }
    public void setUnitsAvailable(Integer unitsAvailable) { this.unitsAvailable = unitsAvailable; }

    public Integer getUnitVolumeMl() { return unitVolumeMl; }
    public void setUnitVolumeMl(Integer unitVolumeMl) { this.unitVolumeMl = unitVolumeMl; }

    public LocalDate getCollectionDate() { return collectionDate; }
    public void setCollectionDate(LocalDate collectionDate) { this.collectionDate = collectionDate; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public String getStorageLocation() { return storageLocation; }
    public void setStorageLocation(String storageLocation) { this.storageLocation = storageLocation; }

    public StockStatus getStatus() { return status; }
    public void setStatus(StockStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public enum ComponentType {
        WHOLE_BLOOD, PLASMA, PLATELETS, RBC, WBC
    }

    public enum StockStatus {
        AVAILABLE, RESERVED, EXPIRED, DISCARDED
    }
}
