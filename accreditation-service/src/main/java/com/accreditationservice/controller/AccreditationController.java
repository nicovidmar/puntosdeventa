package com.accreditationservice.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.accreditationservice.dto.AccreditationRequest;
import com.accreditationservice.dto.AccreditationResponse;
import com.accreditationservice.dto.ErrorResponse;
import com.accreditationservice.entity.Accreditation;
import com.accreditationservice.service.AccreditationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/accreditations")
public class AccreditationController {

    private final Logger logger = LoggerFactory.getLogger(AccreditationController.class);
    private final AccreditationService service;

    public AccreditationController(AccreditationService service) {
        this.service = service;
    }

    @Operation(summary = "Crear acreditación", responses = {
            @ApiResponse(responseCode = "200", description = "Acreditación creada"),
            @ApiResponse(responseCode = "400", description = "Petición inválida. Posibles mensajes:\n- Importe no puede ser nulo\n- Importe debe ser mayor a 0\n- El ID del punto de venta no puede ser nulo\n- No existe POS con ID", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<AccreditationResponse> create(
            @Valid @RequestBody AccreditationRequest request,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        Accreditation saved = service.create(request, token);
        AccreditationResponse response = mapToResponse(saved);
        logger.info("Creando nueva acreditación con id: {}", saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Obtener todas las acreditaciones", responses = {
            @ApiResponse(responseCode = "200", description = "Lista de acreditaciones")
    })
    @GetMapping
    public List<AccreditationResponse> getAll() {
        logger.info("Fetching Accreditations");
        return service.findAll().stream().map(this::mapToResponse).toList();
    }

    private AccreditationResponse mapToResponse(Accreditation acc) {
        AccreditationResponse r = new AccreditationResponse();
        r.setId(acc.getId());
        r.setAmount(acc.getAmount());
        r.setPointOfSaleId(acc.getPointOfSaleId());
        r.setPointOfSaleName(acc.getPointOfSaleName());
        r.setDate(acc.getDate());
        return r;
    }
}
