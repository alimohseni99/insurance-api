package org.example.insuranceapi;

import org.example.insuranceapi.dto.OfferCreateDto;
import org.example.insuranceapi.exception.ConflictException;
import org.example.insuranceapi.model.Offer;
import org.example.insuranceapi.model.OfferStatus;
import org.example.insuranceapi.repository.InsuranceRepository;
import org.example.insuranceapi.service.InsuranceService;
import org.example.insuranceapi.service.StatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class InsuranceServiceUnitTest {
    @Mock
    private InsuranceRepository repository;

    @InjectMocks
    private InsuranceService service;

    @InjectMocks
    private StatsService statsService;

    private OfferCreateDto createDto;
    private Offer dummyOffer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        createDto = new OfferCreateDto("199010101234", List.of(5000.0), 50.0);
        dummyOffer = new Offer(1L, "199010101234", List.of(5000.0), 50.0);
        dummyOffer.setStatus(OfferStatus.PENDING);
        dummyOffer.setCreatedDate(LocalDateTime.now());
    }

    @Test
    void createOffer_shouldSaveAndReturnOffer() {
        ArgumentCaptor<Offer> offerCaptor = ArgumentCaptor.forClass(Offer.class);
        when(repository.save(offerCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        Offer created = service.createOffer(createDto);

        assertEquals("199010101234", created.getPersonalNumber());
        assertEquals(OfferStatus.PENDING, created.getStatus());
        verify(repository).save(any(Offer.class));

        Offer savedOffer = offerCaptor.getValue();
        assertEquals("199010101234", savedOffer.getPersonalNumber());
        assertEquals(OfferStatus.PENDING, savedOffer.getStatus());
    }

    @Test
    void acceptOffer_shouldChangeStatusToAccepted() {
        dummyOffer.setStatus(OfferStatus.PENDING);

        when(repository.findById(1L)).thenReturn(Optional.of(dummyOffer));
        when(repository.save(dummyOffer)).thenReturn(dummyOffer);

        Offer result = service.acceptOffer(1L);

        assertEquals(OfferStatus.ACCEPTED, result.getStatus());
        verify(repository).save(dummyOffer);
    }

    @Test
    void acceptOffer_shouldThrowExceptionWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.acceptOffer(99L));
    }

    @Test
    void acceptOffer_shouldThrowConflictException_whenOfferAlreadyAccepted() {
        // Arrange
        Offer acceptedOffer = new Offer(1L, "199010101234", List.of(1000.0), 30.0);
        acceptedOffer.setStatus(OfferStatus.ACCEPTED);
        acceptedOffer.setCreatedDate(LocalDateTime.now().minusDays(1)); // Set creation date

        when(repository.findById(1L)).thenReturn(Optional.of(acceptedOffer));

        // Act & Assert
        ConflictException exception = assertThrows(ConflictException.class,
                () -> service.acceptOffer(1L));
        assertEquals("Offer has already been accepted", exception.getMessage());
    }

    @Test
    void acceptOffer_shouldThrowExceptionWhenOfferExpired() {
        Offer expiredOffer = new Offer(1L, "199010101234", List.of(1000.0), 30.0);
        expiredOffer.setStatus(OfferStatus.PENDING);
        expiredOffer.setCreatedDate(LocalDateTime.now().minusDays(31)); // Expired

        when(repository.findById(1L)).thenReturn(Optional.of(expiredOffer));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.acceptOffer(1L));
        // Adjust this assertion based on your actual exception message
        assertTrue(exception.getMessage().contains("expired") ||
                exception.getMessage().contains("Expired"));
    }
}