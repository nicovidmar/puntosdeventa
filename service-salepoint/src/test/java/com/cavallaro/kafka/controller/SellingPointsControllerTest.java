package com.cavallaro.kafka.controller;


import com.cavallaro.kafka.dto.ApiResponse;
import com.cavallaro.kafka.dto.SellingPointDTO;
import com.cavallaro.kafka.service.SellingPointService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class SellingPointsControllerTest {

    @Mock
    private SellingPointService sellingPointService;

    @InjectMocks
    private SellingPointsController sellingPointsController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetSellingPoints_NoContent() {
        when(sellingPointService.findAll()).thenReturn(new ApiResponse<>(null, null));
        ResponseEntity<ApiResponse<List<SellingPointDTO>>> response = sellingPointsController.getSellingPoints();
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void testGetSellingPoints_Success() {
        ApiResponse<List<SellingPointDTO>> mockResponse = ApiResponse.success(List.of(new SellingPointDTO()));
        when(sellingPointService.findAll()).thenReturn(mockResponse);
        ResponseEntity<ApiResponse<List<SellingPointDTO>>> response = sellingPointsController.getSellingPoints();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testAddSellingPoint_BadRequest() {
        ResponseEntity<ApiResponse<SellingPointDTO>> response = sellingPointsController.addSellingPoint(null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testAddSellingPoint_Success() {
        SellingPointDTO sellingPointDTO = new SellingPointDTO();
        sellingPointDTO.setId(1);
        ApiResponse<SellingPointDTO> mockResponse =ApiResponse.success(new SellingPointDTO());
        when(sellingPointService.save(any(SellingPointDTO.class))).thenReturn(mockResponse);
        ResponseEntity<ApiResponse<SellingPointDTO>> response = sellingPointsController.addSellingPoint(sellingPointDTO);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void testUpdateSellingPoint_BadRequest() {
        ResponseEntity<ApiResponse<SellingPointDTO>> response = sellingPointsController.updateSellingPoint(null, "");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testDeleteSellingPoint_BadRequest() {
        ResponseEntity<ApiResponse<Boolean>> response = sellingPointsController.deleteSellingPoint(null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
