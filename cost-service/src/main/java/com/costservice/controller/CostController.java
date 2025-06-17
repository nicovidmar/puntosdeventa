package com.costservice.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.costservice.dto.CostRequest;
import com.costservice.dto.ErrorResponse;
import com.costservice.dto.ShortestPathResponse;
import com.costservice.service.CostService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/costs")
public class CostController {

    private final Logger logger = LoggerFactory.getLogger(CostController.class);
    private final CostService costService;

    public CostController(CostService costService) {
        this.costService = costService;
    }

    @Operation(summary = "Crear o actualizar costo entre POS", responses = {
            @ApiResponse(responseCode = "200", description = "Costo actualizado"),
            @ApiResponse(responseCode = "201", description = "Costo creado"),
            @ApiResponse(responseCode = "400", description = "Petición inválida. Posibles mensajes:\n- No existe PointOfSale\n- Costo no puede ser negativo\n- Costo de un punto al mismo punto no puede ser distinto de 0\n- ID puede ser nulo\n- ID debe ser mayor a 0\n-Costo no puede ser nulo", content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<CostRequest> addCost(@Valid @RequestBody CostRequest request) {
        boolean created = costService.save(request.getIdA(), request.getIdB(), request.getCost());

        if (created) {
            logger.info("Creando nuevo costo entre POS {} y {}", request.getIdA(), request.getIdB());
            return ResponseEntity.status(HttpStatus.CREATED).body(request);
        } else {
            logger.info("Actualizando costo entre POS {} y {}", request.getIdA(), request.getIdB());
            return ResponseEntity.ok(request);
        }
    }

    @Operation(summary = "Eliminar costo entre POS", responses = {
            @ApiResponse(responseCode = "200", description = "Costo eliminado"),
            @ApiResponse(responseCode = "400", description = "No existe PointOfSale", content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteCost(@RequestParam int idA, @RequestParam int idB) {
        logger.info("Borrando costo entre POS {} y {}", idA, idB);
        costService.deleteCost(idA, idB);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obtener conexiones directas desde POS", responses = {
            @ApiResponse(responseCode = "200", description = "Conexiones encontradas"),
            @ApiResponse(responseCode = "400", description = "No existe PointOfSale", content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}/connections")
    public ResponseEntity<Map<Integer, Integer>> getDirectConnectionsFrom(@PathVariable int id) {
        logger.info("Obteniendo direcciones desde: {}", id);
        return ResponseEntity.ok(costService.getDirectConnectionsFrom(id));
    }

    @Operation(summary = "Obtener camino más corto entre POS", responses = {
            @ApiResponse(responseCode = "200", description = "Camino encontrado"),
            @ApiResponse(responseCode = "400", description = "Petición inválida. Posibles mensajes:\n- No existe PointOfSale\n- No hay camino entre los puntos de venta", content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/shortest-path")
    public ResponseEntity<ShortestPathResponse> getShortestPath(
            @RequestParam int idA,
            @RequestParam int idB) {
        logger.info("Obteniendo camino más corto entre: {} y {}", idA, idB);

        ShortestPathResponse result = costService.getShortestPath(idA, idB);
        return ResponseEntity.ok(result);
    }

}