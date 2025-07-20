package org.example.insuranceapi.dto;


import java.util.List;

import jakarta.validation.constraints.*;

public record OfferCreateDto(

        @NotNull(message = "Personal number cannot be null")
        @NotBlank(message = "Personal number cannot be blank")
        @Pattern(regexp = "\\d{10,12}", message = "Personal number must be 10 to 12 digits")
        String personalNumber,

        @NotNull(message = "Loans cannot be null")
        @NotEmpty(message = "Loans cannot be empty")
        List<@Positive(message = "Loan amounts must be positive") Double> loans,


        @NotNull(message = "Monthly payment is required")
        @Positive(message = "Monthly payment must be positive")
        Double monthlyPayment

) {
}
