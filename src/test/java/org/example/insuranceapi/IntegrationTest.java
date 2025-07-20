package org.example.insuranceapi;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.example.insuranceapi.dto.OfferCreateDto;
import org.example.insuranceapi.model.Offer;
import org.example.insuranceapi.model.OfferStatus;
import org.example.insuranceapi.repository.InsuranceRepository;
import org.example.insuranceapi.service.InsuranceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import jakarta.validation.Validator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class IntegrationTest {

    @Autowired
    private InsuranceRepository repository;
    @Autowired
    private InsuranceService service;
    @Autowired
    private Validator validator;

    @BeforeEach
    void setup() {
        repository.deleteAll();

        Offer oldPending = new Offer();
        oldPending.setStatus(OfferStatus.PENDING);
        oldPending.setCreatedDate(LocalDateTime.now().minusDays(40));
        oldPending.setPersonalNumber("1234567890");
        repository.save(oldPending);

        Offer recentPending = new Offer();
        recentPending.setStatus(OfferStatus.PENDING);
        recentPending.setCreatedDate(LocalDateTime.now().minusDays(5));
        recentPending.setPersonalNumber("0987654321");
        repository.save(recentPending);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }


    @Test
    void checkOfferStatus_shouldExpireOldPendingOffers() {
        service.checkForExpiredOffers();

        List<Offer> allOffers = repository.findAll();

        Offer expired = allOffers.get(0);
        Offer notExpired = allOffers.get(1);

        assertEquals(OfferStatus.EXPIRED, expired.getStatus());
        assertEquals("",  expired.getPersonalNumber());

        assertEquals(OfferStatus.PENDING, notExpired.getStatus());
        assertNotNull(notExpired.getPersonalNumber());
    }

    @Test
    void shouldFailValidationWhenPersonalNumberIsNull() {
        OfferCreateDto dto = new OfferCreateDto(null, List.of(1000.0), 1500.0);
        Set<ConstraintViolation<OfferCreateDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("personalNumber")));
    }

    @Test
    void shouldThrowWhenPersonalNumberIsEmpty() {
        OfferCreateDto dto = new OfferCreateDto("", List.of(1000.0, 2000.0), 1500.0);
        Set<ConstraintViolation<OfferCreateDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("personalNumber")));
    }


    @Test
    void shouldFailValidationWhenMonthlyPaymentIsNegative() {
        OfferCreateDto dto = new OfferCreateDto("990214-1234", List.of(1000.0, 2000.0), -1.0);
        Set<ConstraintViolation<OfferCreateDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("monthlyPayment")));
    }

    @Test
    void shouldFailValidationWhenLoansListIsEmpty() {
        OfferCreateDto dto = new OfferCreateDto("990214-1234", List.of(), 1500.0);
        Set<ConstraintViolation<OfferCreateDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("loans")));
    }

    @Test
    void shouldFailValidationWhenLoansContainNegativeValues() {
        OfferCreateDto dto = new OfferCreateDto("990214-1234", List.of(-1000.0, 2000.0), 1500.0);
        Set<ConstraintViolation<OfferCreateDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(
                violations.stream().anyMatch(v -> v.getPropertyPath().toString().startsWith("loans"))
        );

    }


    @Test
    void shouldCalculatePremiumCorrectlyWithManyLoans() {
        List<Double> manyLoans = java.util.stream.DoubleStream.generate(() -> 1000.0).limit(1000).boxed().toList();
        OfferCreateDto dto = new OfferCreateDto("990214-1234", manyLoans, 1500.0);
        Offer offer = service.createOffer(dto);
        assertEquals(1000 * 1000.0 * 0.038, offer.getPremium());
    }
}
