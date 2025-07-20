package org.example.insuranceapi.model;


import jakarta.persistence.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "offers")
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String personalNumber;

    @ElementCollection
    @CollectionTable(name = "offer_loans", joinColumns = @JoinColumn(name = "offer_id"))
    @Column(name = "loan_amount")
    private List<Double> loans = new ArrayList<>();
    private double monthlyAmount;
    private double premium;

    @Enumerated(EnumType.STRING)
    private OfferStatus status;
    private LocalDateTime createdDate;
    private LocalDateTime updatedTime;
    private LocalDateTime acceptedDate;

    private static final long EXPIRATION_DAYS = 30;


    public Offer(long id, String personalNumber, List<Double> loans, double monthlyAmount) {
        this.id = id;
        this.personalNumber = personalNumber;
        this.loans = loans;
        this.monthlyAmount = monthlyAmount;
    }

    public Offer() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPersonalNumber() {
        return personalNumber;
    }

    public void setPersonalNumber(String personalNumber) {
        this.personalNumber = personalNumber;
    }

    public List<Double> getLoans() {
        return loans;
    }

    public void setLoans(List<Double> loans) {
        this.loans = loans;
    }

    public double getMonthlyAmount() {
        return monthlyAmount;
    }

    public void setMonthlyAmount(double monthlyAmount) {
        this.monthlyAmount = monthlyAmount;
    }

    public double getPremium() {
        return premium;
    }

    public void setPremium(double premium) {
        this.premium = premium;
    }

    public OfferStatus getStatus() {
        return status;
    }

    public void setStatus(OfferStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    public LocalDateTime getAcceptedDate() {
        return acceptedDate;
    }

    public void setAcceptedDate(LocalDateTime acceptedDate) {
        this.acceptedDate = acceptedDate;
    }

    public boolean isExpired(LocalDateTime referenceTime) {
        return this.status == OfferStatus.PENDING &&
                this.createdDate != null &&
                this.createdDate.isBefore(referenceTime.minusDays(EXPIRATION_DAYS));
    }
}
