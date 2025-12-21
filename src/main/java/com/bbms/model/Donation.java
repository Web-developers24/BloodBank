package com.bbms.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "donations")
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "donations_seq")
    @SequenceGenerator(name = "donations_seq", sequenceName = "donations_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donor_id", nullable = false)
    private Donor donor;

    @Column(name = "donation_date", nullable = false)
    private LocalDate donationDate;

    @Column(name = "blood_group", nullable = false, length = 5)
    private String bloodGroup;

    @Enumerated(EnumType.STRING)
    @Column(name = "component_type", length = 30)
    private BloodStock.ComponentType componentType = BloodStock.ComponentType.WHOLE_BLOOD;

    @Column(name = "volume_ml")
    private Integer volumeMl = 450;

    @Column(name = "hemoglobin_level")
    private Double hemoglobinLevel;

    @Column(name = "blood_pressure", length = 20)
    private String bloodPressure;

    @Column(name = "pulse_rate")
    private Integer pulseRate;

    @Column(name = "temperature_c")
    private Double temperatureC;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private DonationStatus status = DonationStatus.COMPLETED;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "collected_by", length = 100)
    private String collectedBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Donation() {}

    public Donation(Donor donor, LocalDate donationDate, String bloodGroup) {
        this.donor = donor;
        this.donationDate = donationDate;
        this.bloodGroup = bloodGroup;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Donor getDonor() { return donor; }
    public void setDonor(Donor donor) { this.donor = donor; }

    public LocalDate getDonationDate() { return donationDate; }
    public void setDonationDate(LocalDate donationDate) { this.donationDate = donationDate; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public BloodStock.ComponentType getComponentType() { return componentType; }
    public void setComponentType(BloodStock.ComponentType componentType) { this.componentType = componentType; }

    public Integer getVolumeMl() { return volumeMl; }
    public void setVolumeMl(Integer volumeMl) { this.volumeMl = volumeMl; }

    public Double getHemoglobinLevel() { return hemoglobinLevel; }
    public void setHemoglobinLevel(Double hemoglobinLevel) { this.hemoglobinLevel = hemoglobinLevel; }

    public String getBloodPressure() { return bloodPressure; }
    public void setBloodPressure(String bloodPressure) { this.bloodPressure = bloodPressure; }

    public Integer getPulseRate() { return pulseRate; }
    public void setPulseRate(Integer pulseRate) { this.pulseRate = pulseRate; }

    public Double getTemperatureC() { return temperatureC; }
    public void setTemperatureC(Double temperatureC) { this.temperatureC = temperatureC; }

    public DonationStatus getStatus() { return status; }
    public void setStatus(DonationStatus status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getCollectedBy() { return collectedBy; }
    public void setCollectedBy(String collectedBy) { this.collectedBy = collectedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public enum DonationStatus {
        SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED, REJECTED
    }
}
