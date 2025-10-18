package org.example.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transfusion")
public class Transfusion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private Recipient recipient;

    private String bloodGroupUsed;
    private int units;
    private LocalDateTime transfusedAt = LocalDateTime.now();

    public Transfusion() {}

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Recipient getRecipient() { return recipient; }
    public void setRecipient(Recipient recipient) { this.recipient = recipient; }

    public String getBloodGroupUsed() { return bloodGroupUsed; }
    public void setBloodGroupUsed(String bloodGroupUsed) { this.bloodGroupUsed = bloodGroupUsed; }

    public int getUnits() { return units; }
    public void setUnits(int units) { this.units = units; }

    public LocalDateTime getTransfusedAt() { return transfusedAt; }
    public void setTransfusedAt(LocalDateTime transfusedAt) { this.transfusedAt = transfusedAt; }
}
