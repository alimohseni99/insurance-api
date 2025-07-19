package org.example.insuranceapi.controller;

import jakarta.validation.Valid;
import org.example.insuranceapi.model.Offer;
import org.example.insuranceapi.dto.OfferCreateDto;
import org.example.insuranceapi.service.InsuranceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offer")
public class InsuranceController {

    private final InsuranceService service;

    @Autowired
    public InsuranceController(InsuranceService service) {
        this.service = service;
    }

    @PostMapping
    public Offer createOffer(@Valid @RequestBody OfferCreateDto dto) {
       return service.createOffer(dto);
    }

    @PutMapping("/{id}")
    public Offer updateOffer(@PathVariable Long id, @Valid @RequestBody OfferCreateDto dto) {
        return service.updateOffer(id, dto);
    }

    @PostMapping("/{id}/accept")
    public Offer acceptOffer(@PathVariable Long id) {
        return service.acceptOffer(id);
    }

}
