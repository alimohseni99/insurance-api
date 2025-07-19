package org.example.insuranceapi.controller;

import org.example.insuranceapi.dto.ConversionStatsDto;
import org.example.insuranceapi.service.StatsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
public class StatsController {
    private final StatsService service;

    public StatsController(StatsService service) {
        this.service = service;
    }

    @PostMapping("conversion")
    public ConversionStatsDto getConversionStats(@RequestParam(defaultValue = "30") int days){
        return service.getConversionStats(days);
    }
}
