package org.example.insuranceapi.controller;

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

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/offers")
public class InsuranceController {

    private final InsuranceService service;

    @Autowired
    public InsuranceController(InsuranceService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Offer>  createOffer(@Valid @RequestBody OfferCreateDto dto) {
       return ResponseEntity.status(HttpStatus.CREATED).body(service.createOffer(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Offer> updateOffer(@PathVariable Long id, @Valid @RequestBody OfferCreateDto dto) {
        return ResponseEntity.status(HttpStatus.OK).body(service.updateOffer(id, dto));
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<Offer> acceptOffer(@PathVariable Long id) {
        return ResponseEntity.ok(service.acceptOffer(id));
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
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
