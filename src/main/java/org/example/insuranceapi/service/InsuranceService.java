package org.example.insuranceapi.service;

import jakarta.transaction.Transactional;
import org.example.insuranceapi.exceptions.ConflictException;
import org.example.insuranceapi.exceptions.NotFound;
import org.example.insuranceapi.model.Offer;
import org.example.insuranceapi.dto.OfferCreateDto;
import org.example.insuranceapi.model.OfferStatus;
import org.example.insuranceapi.repository.InsuranceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InsuranceService {

    private static final Logger logger = LoggerFactory.getLogger(InsuranceService.class);

    private final InsuranceRepository repository;

    public InsuranceService(InsuranceRepository repository) {
        this.repository = repository;
    }

    public Offer createOffer(OfferCreateDto dto) {
        if (dto.personalNumber() == null || dto.personalNumber().isBlank()) {
            throw new IllegalArgumentException("Personal number cannot be null or empty");
        }
        if (dto.monthlyPayment() <= 0) {
            throw new IllegalArgumentException("Monthly payment cannot be negative or zero");
        }
        if (dto.loans() == null || dto.loans().isEmpty()) {
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
        double premium = sumOfLoans * 0.038;
        offer.setPremium(premium);

        return repository.save(offer);
    }

    public Offer updateOffer(Long id, OfferCreateDto dto) {
        Offer offer = repository.findById(id)
                .orElseThrow(() -> new NotFound("Could not find offer with id: " + id));

        offer.setMonthlyAmount(dto.monthlyPayment());
        offer.setLoans(dto.loans());
        offer.setPersonalNumber(dto.personalNumber());
        offer.setUpdatedTime(LocalDateTime.now());

        double sumOfLoans = dto.loans().stream().mapToDouble(Double::doubleValue).sum();
        double premium = sumOfLoans * 0.038;
        offer.setPremium(premium);

        return repository.save(offer);
    }

    public Offer acceptOffer(Long id) {
        Offer offer = repository.findById(id)
                .orElseThrow(() -> new NotFound("Could not find offer with id: " + id));

        if (offer.getStatus() == OfferStatus.ACCEPTED) {
            throw new ConflictException("Offer has already been accepted");
        }

        if (offer.getCreatedDate().isBefore(LocalDateTime.now().minusDays(30)) ||
                offer.getStatus() == OfferStatus.EXPIRED) {
            throw new ConflictException("Offer with id: " + id + " has expired");
        }

        offer.setStatus(OfferStatus.ACCEPTED);
        offer.setAcceptedDate(LocalDateTime.now());

        return repository.save(offer);
    }

    private boolean isOfferExpired(Offer offer, LocalDateTime referenceTime) {
        return offer.getStatus() == OfferStatus.PENDING &&
                offer.getCreatedDate().isBefore(referenceTime.minusDays(30));
    }

    @Transactional
    @Scheduled(cron = "*/30 * * * * *") // kör var 30:e sekund för testning
    public void checkForExpiredOffers() {
        LocalDateTime now = LocalDateTime.now();

        List<Offer> offers = repository.findAll();
        for (Offer offer : offers) {
            if (isOfferExpired(offer, now)) {
                offer.setStatus(OfferStatus.EXPIRED);
                offer.setPersonalNumber("");
                repository.save(offer);
                logger.info("These offers has been expired: {}", offer);
            }
        }
    }
}
