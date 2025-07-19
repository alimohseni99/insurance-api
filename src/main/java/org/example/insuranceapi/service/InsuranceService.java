package org.example.insuranceapi.service;


import org.example.insuranceapi.exceptions.ConflictException;
import org.example.insuranceapi.exceptions.NotFound;
import org.example.insuranceapi.model.Offer;
import org.example.insuranceapi.dto.OfferCreateDto;
import org.example.insuranceapi.model.OfferStatus;
import org.example.insuranceapi.repository.InsuranceRepository;
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

    public List<Offer> getAllOffers(){
        return repository.findAll();
    }

    public Offer createOffer(OfferCreateDto dto){
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

        System.out.println("Created date: " + optionalOffer.get().getCreatedDate());
        System.out.println("Now minus 30 days: " + LocalDateTime.now().minusDays(30));

        Offer offer = optionalOffer.orElseGet(()-> null);

        offer.setStatus(OfferStatus.ACCEPTED);
        offer.setAcceptedDate(LocalDateTime.now());

        return repository.save(offer);
    }


}
