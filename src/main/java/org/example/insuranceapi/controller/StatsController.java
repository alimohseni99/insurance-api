package org.example.insuranceapi.controller;

import org.example.insuranceapi.dto.ConversionStatsDto;
import org.example.insuranceapi.service.StatsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stats")
public class StatsController {
    private final StatsService service;

    public StatsController(StatsService service) {
        this.service = service;
    }

    @GetMapping("conversion")
    public ResponseEntity<ConversionStatsDto> getConversionStats(@RequestParam(defaultValue = "30") int days){
        return new ResponseEntity<>(service.getConversionStats(days), HttpStatus.OK);
    }
}
