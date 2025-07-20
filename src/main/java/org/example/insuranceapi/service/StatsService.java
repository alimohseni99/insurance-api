package org.example.insuranceapi.service;

import org.example.insuranceapi.dto.ConversionStatsDto;
import org.example.insuranceapi.model.Offer;
import org.example.insuranceapi.repository.InsuranceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatsService {
    private final InsuranceRepository repository;

    public StatsService(InsuranceRepository repository) {
        this.repository = repository;
    }

    public ConversionStatsDto getConversionStats(int days) {
        List<Offer> allOffers = repository.findAll();

        long total = allOffers.size();

        long acceptedWithinDays = allOffers.stream().filter(o -> o.getAcceptedDate() != null).filter(o -> o.getAcceptedDate().isAfter(LocalDateTime.now().minusDays(days))).count();

        double conversionRate = total == 0 ? 0 : (acceptedWithinDays * 100.0) / total;

        return new ConversionStatsDto(total, acceptedWithinDays, conversionRate);
    }
}
