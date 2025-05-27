package com.cavallaro.kafke.integracion;

import com.cavallaro.kafka.ServiceAccreditationApplication;
import com.cavallaro.kafka.controller.AccreditationsController;
import com.cavallaro.kafka.dto.AccreditationRequest;
import com.cavallaro.kafka.model.Accreditation;
import com.cavallaro.kafka.service.impl.AccreditationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AccreditationsController.class)
@ContextConfiguration(classes = {ServiceAccreditationApplication.class})

public class AccreditationsControllerTest {

    @MockitoBean
    private AccreditationService service;

    @MockitoBean
    private CircuitBreakerFactory circuitBreakerFactory;

    private CircuitBreaker circuitBreaker;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {

        MockitoAnnotations.openMocks(this);
        // Configurar el mock del CircuitBreaker
        circuitBreaker = mock(CircuitBreaker.class);
        when(circuitBreakerFactory.create(anyString())).thenReturn(circuitBreaker);

        // Configurar comportamiento por defecto del CircuitBreaker
        when(circuitBreaker.run(any(Supplier.class), any(Function.class))).thenAnswer(invocation -> {
            Supplier<?> supplier = invocation.getArgument(0);
            return supplier.get();
        });
    }

    @Test
    public void testObtenerTodas() throws Exception {
        Accreditation accreditation = new Accreditation();
        accreditation.setId("1");
        List<Accreditation> accreditations = Arrays.asList(accreditation);

        when(service.findAll()).thenReturn(accreditations);

        this.mockMvc
                .perform(get("/api/v1/accreditations").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(1));

    }

    @Test
    public void testObtenerPorId_Encontrado() throws Exception {
        Accreditation accreditation = new Accreditation();
        accreditation.setId("1");

        when(service.findById("1")).thenReturn(Optional.of(accreditation));

        this.mockMvc
                .perform(get("/api/v1/accreditations/1").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

    }

    @Test
    public void testObtenerPorId_NoEncontrado() throws Exception {
        when(service.findById("1")).thenReturn(Optional.empty());

        this.mockMvc
                .perform(get("/api/v1/accreditations/1").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGuardar() throws Exception {
        AccreditationRequest request = new AccreditationRequest(0.0,1);
        Accreditation accreditation = new Accreditation();
        accreditation.setId("1");

        when(service.save(any(AccreditationRequest.class))).thenReturn(accreditation);

        // Configurar comportamiento específico para este test
        when(circuitBreaker.run(any(Supplier.class), any(Function.class))).thenAnswer(invocation -> {
            Supplier<?> supplier = invocation.getArgument(0);
            return supplier.get();
        });

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);

        this.mockMvc
                .perform(post("/api/v1/accreditations").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

    }

    @Test
    public void testActualizar() throws Exception {
        Accreditation accreditation = new Accreditation();
        accreditation.setId("1");

        when(service.update(anyString(), any(Accreditation.class))).thenReturn(accreditation);

        // Convertir el request a JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(accreditation);

        this.mockMvc
                .perform(put("/api/v1/accreditations/1").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    public void testEliminar() throws Exception {
        doNothing().when(service).deleteById("1");

        this.mockMvc
                .perform(delete("/api/v1/accreditations/1").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
        verify(service, times(1)).deleteById("1");
        verifyNoMoreInteractions(service);
    }

    @Test
    public void testGuardarConFallback() throws Exception {
        AccreditationRequest request = new AccreditationRequest(0.0, 1);
        Accreditation fallbackAccreditation = new Accreditation();
        fallbackAccreditation.setId("fallback");

        when(service.save(any(AccreditationRequest.class))).thenThrow(new RuntimeException("Service error"));

        // Configurar comportamiento de fallback
        when(circuitBreaker.run(any(Supplier.class), any(Function.class))).thenAnswer(invocation -> {
            Function<Throwable, ResponseEntity<Accreditation>> fallback = invocation.getArgument(1);
            return fallback.apply(new RuntimeException("Service error"));
        });

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);

        this.mockMvc
                .perform(post("/api/v1/accreditations").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andDo(print())
                .andExpect(status().isServiceUnavailable());

        verify(circuitBreakerFactory).create(eq("save"));
        verify(circuitBreaker).run(any(Supplier.class), any(Function.class));
    }
}
