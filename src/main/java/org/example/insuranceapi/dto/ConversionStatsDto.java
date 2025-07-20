package org.example.insuranceapi.dto;

public record ConversionStatsDto(long totalOffers, long acceptedWithinXDays, double conversionRatePercentage) {}
