package com.costservice;

import com.costservice.controller.CostController;
import com.costservice.dto.CostRequest;
import com.costservice.dto.ShortestPathResponse;
import com.costservice.service.CostService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(CostController.class)
public class CostControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CostService costService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testAddCost_OK() throws Exception {
        CostRequest request = new CostRequest(1, 2, 10);
        when(costService.save(1, 2, 10)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/costs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idA").value(1))
                .andExpect(jsonPath("$.idB").value(2))
                .andExpect(jsonPath("$.cost").value(10));
    }

    @Test
    void testUpdateCost_OK() throws Exception {
        CostRequest request = new CostRequest(1, 2, 10);
        when(costService.save(1, 2, 10)).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/costs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idA").value(1));
    }

    @Test
    void testDeleteCost_OK() throws Exception {
        doNothing().when(costService).deleteCost(1, 2);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/costs?idA=1&idB=2"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetConnections_OK() throws Exception {
        when(costService.getDirectConnectionsFrom(1)).thenReturn(Map.of(2, 10, 3, 20));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/costs/1/connections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.2").value(10))
                .andExpect(jsonPath("$.3").value(20));
    }

    @Test
    void testShortestPath_OK() throws Exception {
        ShortestPathResponse response = new ShortestPathResponse("POS A", "POS C", 25, List.of("POS A", "POS B", "POS C"));
        when(costService.getShortestPath(1, 3)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/costs/shortest-path?idA=1&idB=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.from").value("POS A"))
                .andExpect(jsonPath("$.to").value("POS C"))
                .andExpect(jsonPath("$.totalCost").value(25))
                .andExpect(jsonPath("$.path[0]").value("POS A"))
                .andExpect(jsonPath("$.path[2]").value("POS C"));
    }

    @Test
    void testAddCost_BadRequest() throws Exception {
        // costo negativo
        CostRequest request = new CostRequest(1, 2, -5);

        when(costService.save(anyInt(), anyInt(), anyInt()))
                .thenThrow(new IllegalArgumentException("El costo debe ser mayor o igual a 0"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/costs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El costo debe ser mayor o igual a 0"));
    }
}
