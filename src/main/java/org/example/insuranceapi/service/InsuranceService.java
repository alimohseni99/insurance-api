package org.example.insuranceapi.service;


import jakarta.transaction.Transactional;
import org.example.insuranceapi.exceptions.ConflictException;
import org.example.insuranceapi.exceptions.NotFound;
import org.example.insuranceapi.model.Offer;
import org.example.insuranceapi.dto.OfferCreateDto;
import org.example.insuranceapi.model.OfferStatus;
import org.example.insuranceapi.repository.InsuranceRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InsuranceService {

    private final InsuranceRepository repository;
    public InsuranceService(InsuranceRepository repository) {
        this.repository = repository;
    }

    public Offer createOffer(OfferCreateDto dto){

        if (dto.personalNumber() == null || dto.personalNumber().isBlank()) {
            throw new IllegalArgumentException("Personal number cannot be null or empty");
        }
        if (dto.monthlyPayment() <= 0 ){
            throw new IllegalArgumentException("Monthly payment cannot be negative or zero");
        }

        if (dto.loans() == null || dto.loans().isEmpty()){
            throw new IllegalArgumentException("Loans cannot be null or empty");
        }

        boolean hasInvalidLoan = dto.loans().stream().anyMatch(loan -> loan == null || loan <= 0);
        if (hasInvalidLoan) {
            throw new IllegalArgumentException("Loans cannot contain null, negative or zero values");
        }

        Offer offer = new Offer();
        offer.setPersonalNumber(dto.personalNumber());
        offer.setLoans(dto.loans());
        offer.setMonthlyAmount(dto.monthlyPayment());
        offer.setStatus(OfferStatus.PENDING);
        offer.setCreatedDate(LocalDateTime.now());

        double sumOfLoans = dto.loans().stream().mapToDouble(Double::doubleValue).sum();
        double premium = sumOfLoans  * 0.038;
        offer.setPremium(premium);

        return repository.save(offer);
    }

    public Offer updateOffer(Long id, OfferCreateDto dto){
        Optional<Offer> optionalOffer = repository.findById(id);
        if(optionalOffer.isEmpty()){
            throw new NotFound("Could not find offer with id: " + id);
        }

        Offer offer = optionalOffer.orElseGet(()-> null);

        offer.setMonthlyAmount(dto.monthlyPayment());
        offer.setLoans(dto.loans());
        offer.setPersonalNumber(dto.personalNumber());
        offer.setUpdatedTime(LocalDateTime.now());

        double sumOfLoans = dto.loans().stream().mapToDouble(Double::doubleValue).sum();
        double premium = sumOfLoans * 0.038;
        offer.setPremium(premium);

        return repository.save(offer);

    }

    public Offer acceptOffer(Long id){
        Optional<Offer> optionalOffer = repository.findById(id);

        if(optionalOffer.isEmpty()){
            throw new NotFound("Could not find offer with id: " + id);
        }

        if (optionalOffer.get().getStatus().equals(OfferStatus.ACCEPTED)) {
            throw new ConflictException("Offer has already been accepted");
        }

        if (optionalOffer.get().getCreatedDate().isBefore(LocalDateTime.now().minusDays(30)) ||
                optionalOffer.get().getStatus().equals(OfferStatus.EXPIRED)) {
            throw new ConflictException("Offer with id: " + id + " has expired");
        }

        Offer offer = optionalOffer.orElseGet(()-> null);

        offer.setStatus(OfferStatus.ACCEPTED);
        offer.setAcceptedDate(LocalDateTime.now());

        return repository.save(offer);
    }

    @Transactional
    @Scheduled(cron = "*/30 * * * * *") // This should be set (cron = "0 0 0 * * *" to run every day at midnight but for testing purposes we are going for every 30 sekund
    public void checkForExpiredOffers(){
        List<Offer> offers = repository.findAll();

        LocalDateTime now = LocalDateTime.now();
        for (Offer offer : offers) {
            if (offer.getStatus() == OfferStatus.PENDING &&
                offer.getCreatedDate().isBefore(now.minusDays(30))) {
                offer.setStatus(OfferStatus.EXPIRED);
                offer.setPersonalNumber("");
                repository.save(offer);
                System.out.println("This offers has been expired and now will be deleted" + offer);
            }
        }
    }


}
