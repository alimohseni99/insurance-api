package org.example.insuranceapi;


import jakarta.persistence.*;
import org.example.insuranceapi.dto.LoanDto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(nullable = false)
    private String personnummer;

    @ElementCollection
    @CollectionTable(name = "offer_loans", joinColumns = @JoinColumn(name = "offer_id"))
    @Column(name = "loan_amount")

    private List<Double> loans = new ArrayList<>();
    private double monthlyAmount;
    private double premium;
    private String status;


    public Offer(UUID id, String personnummer, List<Double> loans, double monthlyAmount) {
        this.id = id;
        this.personnummer = personnummer;
        this.loans = loans;
        this.monthlyAmount = monthlyAmount;
    }

    public Offer(){}


    public String getPersonnummer() {
        return personnummer;
    }

    public void setPersonnummer(String personnummer) {
        this.personnummer = personnummer;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
