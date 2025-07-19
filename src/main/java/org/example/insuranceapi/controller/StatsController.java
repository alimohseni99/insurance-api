package org.example.insuranceapi.controller;

import org.example.insuranceapi.dto.ConversionStatsDto;
import org.example.insuranceapi.service.StatsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stats")
public class StatsController {
    private final StatsService service;

    public StatsController(StatsService service) {
        this.service = service;
    }

    @GetMapping("conversion")
    public ConversionStatsDto getConversionStats(@RequestParam(defaultValue = "30") int days){
        return service.getConversionStats(days);
    }
}
