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

        Offer offer = new Offer();
        offer.setPersonalNumber(dto.personalNumber());
        offer.setLoans(dto.loans());
        offer.setMonthlyAmount(dto.monthlyPayment());
        offer.setStatus(OfferStatus.PENDING);
        offer.setCreatedDate(LocalDateTime.now());

        offer.setPremium(calculatePremium(dto.loans()));

        return repository.save(offer);
    }

    public Offer updateOffer(Long id, OfferCreateDto dto) {
        Offer offer = repository.findById(id)
                .orElseThrow(() -> new NotFound("Could not find offer with id: " + id));

        offer.setMonthlyAmount(dto.monthlyPayment());
        offer.setLoans(dto.loans());
        offer.setPersonalNumber(dto.personalNumber());
        offer.setUpdatedTime(LocalDateTime.now());

        offer.setPremium(calculatePremium(dto.loans()));

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

    private double calculatePremium(List<Double> loans) {
        double sumOfLoans = loans.stream().mapToDouble(Double::doubleValue).sum();
        return sumOfLoans * 0.038;
    }

    @Transactional
    @Scheduled(cron = "*/30 * * * * *") // kör var 30:e sekund för testning
    public void checkForExpiredOffers() {
        LocalDateTime now = LocalDateTime.now();

        List<Offer> expiredOffers = repository.findAll().stream()
                .filter(offer -> offer.isExpired(now))
                .toList();

        for (Offer offer : expiredOffers) {
            offer.setStatus(OfferStatus.EXPIRED);
            offer.setPersonalNumber("");
            logger.info("Offer expired and updated: {}", offer);
        }

        repository.saveAll(expiredOffers);
    }
}
