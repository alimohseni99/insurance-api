package org.example.insuranceapi.service;


import org.example.insuranceapi.model.Offer;
import org.example.insuranceapi.dto.OfferCreateDto;
import org.example.insuranceapi.repository.InsuranceRepository;
import org.springframework.stereotype.Service;

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

    public Offer getOfferById(Long Id){
        return repository.findById(Id).orElseGet(()-> null);
    }

    public Offer createOffer(OfferCreateDto dto){
        Offer offer = new Offer();
        offer.setPersonalNumber(dto.personalNumber());
        offer.setLoans(dto.loans());
        offer.setMonthlyAmount(dto.monthlyPayment());
        offer.setStatus("PENDING");

        double sumOfLoans = dto.loans().stream().mapToDouble(Double::doubleValue).sum();
        double premium = sumOfLoans  * 0.038;
        offer.setPremium(premium);

        return repository.save(offer);
    }

    public Offer updateOffer(Long id, OfferCreateDto dto){
        Optional<Offer> optionalOffer = repository.findById(id);
        if(optionalOffer.isEmpty()){
            throw new IllegalArgumentException("Offer not found");
        }

        Offer offer = optionalOffer.orElseGet(()-> null);

        offer.setMonthlyAmount(dto.monthlyPayment());
        offer.setLoans(dto.loans());
        offer.setPersonalNumber(dto.personalNumber());

        double sumOfLoans = dto.loans().stream().mapToDouble(Double::doubleValue).sum();
        double premium = sumOfLoans * 0.038;
        offer.setPremium(premium);

        return repository.save(offer);

    }

    public void deleteOfferById(Long id)  {
        Optional<Offer> offer = repository.findById(id);

        if (offer.isEmpty()){
            throw new IllegalArgumentException("Offer not found");
        }
        repository.delete(offer.get());
    }
}
