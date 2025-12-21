package com.bbms.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "blood_requests")
public class BloodRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "blood_requests_seq")
    @SequenceGenerator(name = "blood_requests_seq", sequenceName = "blood_requests_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private Recipient recipient;

    @Column(name = "blood_group", nullable = false, length = 5)
    private String bloodGroup;

    @Enumerated(EnumType.STRING)
    @Column(name = "component_type", length = 30)
    private BloodStock.ComponentType componentType = BloodStock.ComponentType.WHOLE_BLOOD;

    @Column(name = "units_requested", nullable = false)
    private Integer unitsRequested;

    @Column(name = "units_fulfilled")
    private Integer unitsFulfilled = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", length = 20)
    private Priority priority = Priority.NORMAL;

    @Column(name = "request_date")
    private LocalDate requestDate;

    @Column(name = "required_by_date")
    private LocalDate requiredByDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private RequestStatus status = RequestStatus.PENDING;

    @Column(name = "hospital_name", length = 200)
    private String hospitalName;

    @Column(name = "doctor_name", length = 100)
    private String doctorName;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public BloodRequest() {}

    public BloodRequest(Recipient recipient, String bloodGroup, Integer unitsRequested) {
        this.recipient = recipient;
        this.bloodGroup = bloodGroup;
        this.unitsRequested = unitsRequested;
        this.requestDate = LocalDate.now();
    }

    // Check if request is fully fulfilled
    public boolean isFulfilled() {
        return unitsFulfilled != null && unitsFulfilled >= unitsRequested;
    }

    // Get remaining units needed
    public int getRemainingUnits() {
        return unitsRequested - (unitsFulfilled != null ? unitsFulfilled : 0);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Recipient getRecipient() { return recipient; }
    public void setRecipient(Recipient recipient) { this.recipient = recipient; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public BloodStock.ComponentType getComponentType() { return componentType; }
    public void setComponentType(BloodStock.ComponentType componentType) { this.componentType = componentType; }

    public Integer getUnitsRequested() { return unitsRequested; }
    public void setUnitsRequested(Integer unitsRequested) { this.unitsRequested = unitsRequested; }

    public Integer getUnitsFulfilled() { return unitsFulfilled; }
    public void setUnitsFulfilled(Integer unitsFulfilled) { this.unitsFulfilled = unitsFulfilled; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public LocalDate getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDate requestDate) { this.requestDate = requestDate; }

    public LocalDate getRequiredByDate() { return requiredByDate; }
    public void setRequiredByDate(LocalDate requiredByDate) { this.requiredByDate = requiredByDate; }

    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }

    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public enum Priority {
        LOW, NORMAL, HIGH, EMERGENCY
    }

    public enum RequestStatus {
        PENDING, APPROVED, PARTIALLY_FULFILLED, FULFILLED, CANCELLED
    }
}
