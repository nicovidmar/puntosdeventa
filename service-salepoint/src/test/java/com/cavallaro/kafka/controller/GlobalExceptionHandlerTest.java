package com.cavallaro.kafka.controller;


import com.cavallaro.kafka.dto.ApiResponse;
import com.cavallaro.kafka.exception.SellingCostsServiceException;
import com.cavallaro.kafka.exception.SellingPointsServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void shouldHandleSellingCostsServiceException() {
        SellingCostsServiceException exception = new SellingCostsServiceException(400,"Error en costos" );

        ResponseEntity<ApiResponse<String>> response = exceptionHandler.handleSellingCostsServiceException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getHeader().getCode());
        assertEquals("Error en costos", response.getBody().getHeader().getMessage());
    }

    @Test
    void shouldHandleSellingPointsServiceException() {
        SellingPointsServiceException exception = new SellingPointsServiceException(404,"Error en puntos");

        ResponseEntity<ApiResponse<String>> response = exceptionHandler.handleSellingPointsServiceException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getHeader().getCode());
        assertEquals("Error en puntos", response.getBody().getHeader().getMessage());
    }

    @Test
    void shouldHandleGenericException() {
        Exception exception = new Exception("Error desconocido");

        ResponseEntity<ApiResponse<String>> response = exceptionHandler.handleException((exception));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getHeader().getCode());
        assertEquals("Error desconocido", response.getBody().getHeader().getMessage());
    }
}
