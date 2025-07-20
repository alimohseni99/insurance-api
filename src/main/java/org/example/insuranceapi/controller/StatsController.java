package org.example.insuranceapi.controller;

import org.example.insuranceapi.dto.ConversionStatsDto;
import org.example.insuranceapi.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/v1/stats")
@Tag(name = "Statistics", description = "Insurance conversion statistics API")
public class StatsController {

    private final StatsService service;

    @Autowired
    public StatsController(StatsService service) {
        this.service = service;
    }

    @GetMapping("/conversion")
    @Operation(
            summary = "Get conversion statistics",
            description = "Retrieves insurance conversion statistics for the specified number of days. " +
                    "Returns metrics like conversion rate, total leads, successful conversions, etc."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Conversion statistics retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ConversionStatsDto.class)
                    )
            ),
    })
    public ResponseEntity<ConversionStatsDto> getConversionStats(
            @Parameter(
                    description = "Number of days to calculate statistics for",
                    example = "30",
                    schema = @Schema(minimum = "1", maximum = "365", defaultValue = "30")
            )
            @RequestParam(defaultValue = "30") int days
    ) {
        return ResponseEntity.ok(service.getConversionStats(days));
    }
}