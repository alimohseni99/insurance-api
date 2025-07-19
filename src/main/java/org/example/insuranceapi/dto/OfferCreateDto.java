package org.example.insuranceapi.dto;

import org.example.insuranceapi.model.Offer;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import java.util.List;

public record OfferCreateDto(
        @NotBlank(message = "Personal number cannot be blank") String personalNumber,
        @NotEmpty(message = "Loans cannot be empty") List<@Positive(message = "Loan amounts must be positive") Double> loans,
        @Positive(message = "Monthly payment must be positive") double monthlyPayment
)
 {

    public static Offer fromDto(OfferCreateDto dto){
        return new Offer(dto.personalNumber, dto.loans, dto.monthlyPayment);
    }

}
