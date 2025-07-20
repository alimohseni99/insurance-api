package org.example.insuranceapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.insuranceapi.model.Offer;
import org.example.insuranceapi.dto.OfferCreateDto;
import org.example.insuranceapi.service.InsuranceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/offers")
@Tag(name = "Insurance Offers", description = "API for managing insurance offers")
public class InsuranceController {

    private final InsuranceService service;

    @Autowired
    public InsuranceController(InsuranceService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(
            summary = "Create a new insurance offer",
            description = "Creates a new insurance offer for a customer with specified coverage amounts and monthly cost"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Offer created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Offer.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    value = "{\"personalNumber\": \"Personal number is required\", \"monthlyCost\": \"Monthly cost must be positive\"}"
                            )
                    )
            )
    })
    public ResponseEntity<Offer> createOffer(
            @Parameter(description = "Offer creation data", required = true)
            @Valid @RequestBody OfferCreateDto dto
    ) {
        Offer offer = service.createOffer(dto);
        URI location = URI.create("/api/v1/offers/" + offer.getId());
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update an existing insurance offer",
            description = "Updates an existing insurance offer with new coverage amounts and monthly cost"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Offer updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Offer.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Offer not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Offer with ID 123 not found\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class)
                    )
            )
    })
    public ResponseEntity<Offer> updateOffer(
            @Parameter(description = "Offer ID", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Updated offer data", required = true)
            @Valid @RequestBody OfferCreateDto dto
    ) {
        return ResponseEntity.ok(service.updateOffer(id, dto));
    }

    @PostMapping("/{id}/accept")
    @Operation(
            summary = "Accept an insurance offer",
            description = "Changes the status of a pending insurance offer to accepted. Only pending offers can be accepted."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Offer accepted successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Offer.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Offer not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Offer with ID 123 not found\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Offer already accepted or expired",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Offer has already been accepted\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Offer has expired",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Offer has expired and cannot be accepted\"}")
                    )
            )
    })
    public ResponseEntity<Offer> acceptOffer(
            @Parameter(description = "Offer ID to accept", required = true, example = "1")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok().body(service.acceptOffer(id));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @Operation(hidden = true)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}