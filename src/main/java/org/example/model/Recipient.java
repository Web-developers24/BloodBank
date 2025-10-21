package org.example.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "recipient")
public class Recipient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String bloodGroupRequired;
    private String phone;
    private LocalDate requestDate;

    public Recipient() {}

    public Recipient(String fullName, String bloodGroupRequired, String phone) {
        this.fullName = fullName;
        this.bloodGroupRequired = bloodGroupRequired;
        this.phone = phone;
        this.requestDate = LocalDate.now(); // auto-set request date to today
    }


    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getBloodGroupRequired() { return bloodGroupRequired; }
    public void setBloodGroupRequired(String bloodGroupRequired) { this.bloodGroupRequired = bloodGroupRequired; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public LocalDate getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDate requestDate) { this.requestDate = requestDate; }
}
