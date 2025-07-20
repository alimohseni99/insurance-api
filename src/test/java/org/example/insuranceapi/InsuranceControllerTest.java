package org.example.insuranceapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.insuranceapi.dto.ConversionStatsDto;
import org.example.insuranceapi.dto.OfferCreateDto;
import org.example.insuranceapi.model.Offer;
import org.example.insuranceapi.model.OfferStatus;
import org.example.insuranceapi.service.InsuranceService;
import org.example.insuranceapi.service.StatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class InsuranceControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private InsuranceService service;

    @MockBean
    private StatsService statsService;

    @Autowired
    private ObjectMapper objectMapper;

    private OfferCreateDto dto;
    private Offer dummyOffer;

    private static final String API_CONTEXT_ROOT = "/api/v1/offers";

    @BeforeEach
    void setUp() {
        dto = new OfferCreateDto("199010101234", List.of(5000.0), 50.0);
        dummyOffer = new Offer("199010101234", List.of(5000.0), 50.0);
    }


    @Test
    void shouldCreateOffer() throws Exception {
        String requestBody = objectMapper.writeValueAsString(dto);

        Mockito.when(service.createOffer(any(OfferCreateDto.class))).thenReturn(dummyOffer);

        mvc.perform(post(API_CONTEXT_ROOT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldUpdateOffer() throws Exception {
        Long offerId = 1L;
        String requestBody = objectMapper.writeValueAsString(dto);

        Mockito.when(service.updateOffer(eq(offerId), any(OfferCreateDto.class))).thenReturn(dummyOffer);

        mvc.perform(put(API_CONTEXT_ROOT + "/" + offerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAcceptOffer() throws Exception {
        Long offerId = 1L;

        dummyOffer.setStatus(OfferStatus.ACCEPTED);

        Mockito.when(service.acceptOffer(offerId)).thenReturn(dummyOffer);

        mvc.perform(post(API_CONTEXT_ROOT + "/" + offerId + "/accept"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));
    }


    @Test
    void shouldReturnConversionStats() throws Exception {
        ConversionStatsDto stats = new ConversionStatsDto(100, 25, 25.0);
        Mockito.when(statsService.getConversionStats(30)).thenReturn(stats);

        mvc.perform(get("/api/v1/stats/conversion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalOffers").value(100))
                .andExpect(jsonPath("$.acceptedWithinXDays").value(25));
    }

}
