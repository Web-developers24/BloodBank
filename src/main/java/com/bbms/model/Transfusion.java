package com.bbms.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "transfusions")
public class Transfusion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transfusions_seq")
    @SequenceGenerator(name = "transfusions_seq", sequenceName = "transfusions_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private Recipient recipient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blood_stock_id")
    private BloodStock bloodStock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blood_request_id")
    private BloodRequest bloodRequest;

    @Column(name = "blood_group", nullable = false, length = 5)
    private String bloodGroup;

    @Enumerated(EnumType.STRING)
    @Column(name = "component_type", length = 30)
    private BloodStock.ComponentType componentType = BloodStock.ComponentType.WHOLE_BLOOD;

    @Column(name = "units_transfused", nullable = false)
    private Integer unitsTransfused;

    @Column(name = "transfusion_date", nullable = false)
    private LocalDate transfusionDate;

    @Column(name = "administered_by", length = 100)
    private String administeredBy;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "reaction_observed")
    private Boolean reactionObserved = false;

    @Column(name = "reaction_details", length = 500)
    private String reactionDetails;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private TransfusionStatus status = TransfusionStatus.COMPLETED;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Transfusion() {}

    public Transfusion(Recipient recipient, String bloodGroup, Integer unitsTransfused) {
        this.recipient = recipient;
        this.bloodGroup = bloodGroup;
        this.unitsTransfused = unitsTransfused;
        this.transfusionDate = LocalDate.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Recipient getRecipient() { return recipient; }
    public void setRecipient(Recipient recipient) { this.recipient = recipient; }

    public BloodStock getBloodStock() { return bloodStock; }
    public void setBloodStock(BloodStock bloodStock) { this.bloodStock = bloodStock; }

    public BloodRequest getBloodRequest() { return bloodRequest; }
    public void setBloodRequest(BloodRequest bloodRequest) { this.bloodRequest = bloodRequest; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public BloodStock.ComponentType getComponentType() { return componentType; }
    public void setComponentType(BloodStock.ComponentType componentType) { this.componentType = componentType; }

    public Integer getUnitsTransfused() { return unitsTransfused; }
    public void setUnitsTransfused(Integer unitsTransfused) { this.unitsTransfused = unitsTransfused; }

    public LocalDate getTransfusionDate() { return transfusionDate; }
    public void setTransfusionDate(LocalDate transfusionDate) { this.transfusionDate = transfusionDate; }

    public String getAdministeredBy() { return administeredBy; }
    public void setAdministeredBy(String administeredBy) { this.administeredBy = administeredBy; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Boolean getReactionObserved() { return reactionObserved; }
    public void setReactionObserved(Boolean reactionObserved) { this.reactionObserved = reactionObserved; }

    public String getReactionDetails() { return reactionDetails; }
    public void setReactionDetails(String reactionDetails) { this.reactionDetails = reactionDetails; }

    public TransfusionStatus getStatus() { return status; }
    public void setStatus(TransfusionStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public enum TransfusionStatus {
        SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
    }
}
