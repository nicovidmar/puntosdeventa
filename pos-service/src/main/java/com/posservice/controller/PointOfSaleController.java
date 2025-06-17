package com.posservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.posservice.dto.ErrorResponse;
import com.posservice.dto.PointOfSaleRequest;
import com.posservice.entity.PointOfSale;
import com.posservice.service.PointOfSaleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/pos")
public class PointOfSaleController {

    private final Logger logger = LoggerFactory.getLogger(PointOfSaleController.class);

    private final PointOfSaleService service;

    public PointOfSaleController(PointOfSaleService service) {
        this.service = service;
    }

    @Operation(summary = "Obtener lista de POS", responses = {
            @ApiResponse(responseCode = "200", description = "Lista de POS encontrados")
    })
    @GetMapping
    public List<PointOfSale> getAll() {
        logger.info("Fetching Point Of Sales");
        return service.findAll();
    }

    @Operation(summary = "Obtener usuario por ID", responses = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "400", description = "No existe PointOfSale", content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<PointOfSale> getById(@PathVariable int id) {
        PointOfSale pos = service.findById(id);
        logger.info("Fetching Point Of Sale: {}", id);
        return ResponseEntity.ok(pos);
    }

    @Operation(summary = "Crear nuevo POS", responses = {
            @ApiResponse(responseCode = "201", description = "POS creado"),
            @ApiResponse(responseCode = "400", description = "Petición inválida. Posibles mensajes:\n- El nombre no puede estar vacío\n- Ya existe un PointOfSale con ese nombre", content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<PointOfSale> create(@Valid @RequestBody PointOfSaleRequest req) {
        PointOfSale pos = new PointOfSale();
        pos.setName(req.getName());
        PointOfSale newPos = service.save(pos);
        logger.info("New Point Of Sale: {}", newPos.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(newPos);
    }

    @Operation(summary = "Actualizar POS", responses = {
            @ApiResponse(responseCode = "200", description = "POS actualizado"),
            @ApiResponse(responseCode = "400", description = "Petición inválida. Posibles mensajes:\n- El nombre no puede estar vacío\n- Ya existe un PointOfSale con ese nombre\n- No existe PointOfSale", content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<PointOfSale> update(@PathVariable int id, @Valid @RequestBody PointOfSaleRequest request) {
        PointOfSale updatedPos = service.update(id, request);
        logger.info("Updated Point Of Sale: {}", id);
        return ResponseEntity.ok(updatedPos);
    }

    @Operation(summary = "Eliminar POS", responses = {
            @ApiResponse(responseCode = "200", description = "POS eliminado"),
            @ApiResponse(responseCode = "400", description = "No existe PointOfSale", content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        service.delete(id);
        logger.info("Deleting Point Of Sale: {}", id);
        return ResponseEntity.ok().build();
    }
}
