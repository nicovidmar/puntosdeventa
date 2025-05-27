package com.cavallaro.kafka.controller;

import com.cavallaro.kafka.dto.AccreditationRequest;
import com.cavallaro.kafka.model.Accreditation;
import com.cavallaro.kafka.service.impl.AccreditationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AccreditationsControllerTest {

    @Mock
    private AccreditationService service;

    @Mock
    private CircuitBreakerFactory circuitBreakerFactory;

    @Mock
    private CircuitBreaker circuitBreaker;

    @InjectMocks
    private AccreditationsController controller;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Configurar el mock del CircuitBreaker
        when(circuitBreakerFactory.create(anyString())).thenReturn(circuitBreaker);

        // Configurar el comportamiento por defecto del CircuitBreaker
        when(circuitBreaker.run(any(Supplier.class), any(Function.class))).thenAnswer(invocation -> {
            Supplier<?> supplier = invocation.getArgument(0);
            return supplier.get();
        });
    }

    @Test
    public void testObtenerTodas() {
        Accreditation accreditation = new Accreditation();
        accreditation.setId("1");
        List<Accreditation> accreditations = Arrays.asList(accreditation);

        when(service.findAll()).thenReturn(accreditations);

        ResponseEntity<List<Accreditation>> result = controller.findAll();

        assertEquals(1, result.getBody().size());
        assertEquals("1", result.getBody().get(0).getId());
    }

    @Test
    public void testObtenerPorId_Encontrado() {
        Accreditation accreditation = new Accreditation();
        accreditation.setId("1");

        when(service.findById("1")).thenReturn(Optional.of(accreditation));

        ResponseEntity<Accreditation> response = controller.findById("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("1", response.getBody().getId());
    }

    @Test
    public void testObtenerPorId_NoEncontrado() {
        when(service.findById("1")).thenReturn(Optional.empty());

        ResponseEntity<Accreditation> response = controller.findById("1");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGuardar() {
        AccreditationRequest request = new AccreditationRequest(0.0, 1);
        Accreditation accreditation = new Accreditation();
        accreditation.setId("1");

        when(service.save(any(AccreditationRequest.class))).thenReturn(accreditation);

        // Simular el comportamiento del CircuitBreaker para este test específico
        when(circuitBreaker.run(any(Supplier.class), any(Function.class))).thenAnswer(invocation -> {
            Supplier<?> supplier = invocation.getArgument(0);
            return supplier.get();
        });

        ResponseEntity<Accreditation> response = controller.save(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("1", response.getBody().getId());
        verify(circuitBreakerFactory).create(eq("save"));
    }

    @Test
    public void testGuardarConFallback() {
        AccreditationRequest request = new AccreditationRequest(0.0, 1);
        Accreditation fallbackAccreditation = new Accreditation();
        fallbackAccreditation.setId("fallback");

        // Simular un error en el servicio
        when(service.save(any(AccreditationRequest.class))).thenThrow(new RuntimeException("Service error"));

        // Configurar el CircuitBreaker para que ejecute el fallback
        when(circuitBreaker.run(any(Supplier.class), any(Function.class))).thenAnswer(invocation -> {
            Function<Throwable, ResponseEntity<Accreditation>> fallback = invocation.getArgument(1);
            return fallback.apply(new RuntimeException("Service error"));
        });

        // Asumiendo que tu fallbackMethod devuelve una respuesta con status SERVICE_UNAVAILABLE
        ResponseEntity<Accreditation> response = controller.save(request);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        verify(circuitBreakerFactory).create(eq("save"));
    }

    @Test
    public void testActualizar() {
        Accreditation accreditation = new Accreditation();
        accreditation.setId("1");

        when(service.update(anyString(), any(Accreditation.class))).thenReturn(accreditation);

        ResponseEntity<Accreditation> response = controller.update("1", accreditation);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("1", response.getBody().getId());
    }

    @Test
    public void testEliminar() {
        doNothing().when(service).deleteById("1");

        ResponseEntity<Void> response = controller.deleteById("1");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service, times(1)).deleteById("1");
    }
}