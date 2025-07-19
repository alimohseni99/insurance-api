package org.example.insuranceapi.dto;

import org.example.insuranceapi.model.Offer;

import java.util.List;

public record OfferCreateDto( String personalNumber, List<Double> loans, double monthlyPayment) {
    public static Offer fromDto(OfferCreateDto dto){
        return new Offer(dto.personalNumber, dto.loans, dto.monthlyPayment);
    }
}
