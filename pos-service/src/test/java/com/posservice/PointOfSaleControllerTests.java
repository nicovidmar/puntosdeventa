package com.posservice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.posservice.controller.PointOfSaleController;
import com.posservice.dto.PointOfSaleRequest;
import com.posservice.entity.PointOfSale;
import com.posservice.service.PointOfSaleService;

@WebMvcTest(PointOfSaleController.class)
class PointOfSaleControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PointOfSaleService pointOfSaleService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAll_OK() throws Exception {
        List<PointOfSale> posList = List.of(new PointOfSale(1, "pos"));
        when(pointOfSaleService.findAll()).thenReturn(posList);

        mockMvc.perform(get("/api/pos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("pos"));
    }

    @Test
    void testGetById_OK() throws Exception {
        PointOfSale pos = new PointOfSale(1, "pos");
        when(pointOfSaleService.findById(1)).thenReturn(pos);

        mockMvc.perform(get("/api/pos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("pos"));
    }

    @Test
    void testGetById_NotFound() throws Exception {
        when(pointOfSaleService.findById(99))
                .thenThrow(new IllegalArgumentException("No existe PointOfSale con ID 99"));

        mockMvc.perform(get("/api/pos/99"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("No existe PointOfSale con ID 99"));
    }

    @Test
    void testCreate_OK() throws Exception {
        PointOfSaleRequest request = new PointOfSaleRequest("pos");
        PointOfSale saved = new PointOfSale(1, "pos");
        when(pointOfSaleService.save(any(PointOfSale.class))).thenReturn(saved);

        mockMvc.perform(post("/api/pos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("pos"));
    }

    @Test
    void testCreate_POSAlreadyExist() throws Exception {
        PointOfSaleRequest request = new PointOfSaleRequest("pos1");
        when(pointOfSaleService.save(any(PointOfSale.class)))
                .thenThrow(new IllegalArgumentException("Ya existe un PointOfSale con el nombre pos1"));

        mockMvc.perform(post("/api/pos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ya existe un PointOfSale con el nombre pos1"));
    }

    @Test
    void testCreate_EmptyName() throws Exception {
        PointOfSaleRequest request = new PointOfSaleRequest("");

        mockMvc.perform(post("/api/pos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void testUpdate_OK() throws Exception {
        PointOfSaleRequest request = new PointOfSaleRequest("pos1");
        PointOfSale updated = new PointOfSale(1, "pos2");
        when(pointOfSaleService.update(eq(1), any(PointOfSaleRequest.class))).thenReturn(updated);

        mockMvc.perform(put("/api/pos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("pos2"));
    }

    @Test
    void testUpdate_POSAlreadyExist() throws Exception {
        PointOfSaleRequest request = new PointOfSaleRequest("pos1");
        when(pointOfSaleService.update(eq(1), any(PointOfSaleRequest.class)))
                .thenThrow(new IllegalArgumentException("Ya existe un PointOfSale con el nombre pos1"));

        mockMvc.perform(put("/api/pos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ya existe un PointOfSale con el nombre pos1"));
    }

    @Test
    void testUpdate_BadRequest() throws Exception {
        PointOfSaleRequest request = new PointOfSaleRequest(null);

        mockMvc.perform(put("/api/pos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void testDelete_OK() throws Exception {
        doNothing().when(pointOfSaleService).delete(1);

        mockMvc.perform(delete("/api/pos/1"))
                .andExpect(status().isOk());
    }
}
