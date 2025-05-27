package com.cavallaro.kafka.controller;


import com.cavallaro.kafka.dto.SellingPointDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


@Tag(name = "Selling Points API", description = "API para gestionar puntos de venta.")
public interface SellingPointsApi {

    @Operation(summary = "Obtener todos los puntos de venta", description = "Devuelve un mapa de puntos de venta con su ID y nombre")
    @ApiResponses({

            @ApiResponse(responseCode = "200", description = "Lista de puntos de venta obtenida exitosamente",content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
            ,
            @ApiResponse(responseCode = "204", description = "No hay puntos de venta registrados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    ResponseEntity<com.cavallaro.kafka.dto.ApiResponse<List<SellingPointDTO>>>  getSellingPoints();

    @Operation(summary = "Agregar un nuevo punto de venta", description = "Añade un punto de venta con ID y nombre")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Punto de venta creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    ResponseEntity<com.cavallaro.kafka.dto.ApiResponse<SellingPointDTO>> addSellingPoint(SellingPointDTO sellingPointDTO);

    @Operation(summary = "Actualizar un punto de venta", description = "Modifica el nombre de un punto de venta existente")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Punto de venta actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud"),
            @ApiResponse(responseCode = "404", description = "Punto de venta no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    ResponseEntity<com.cavallaro.kafka.dto.ApiResponse<SellingPointDTO>> updateSellingPoint(Integer id, String name);

    @Operation(summary = "Eliminar un punto de venta", description = "Elimina un punto de venta por su ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Punto de venta eliminado exitosamente"),
            @ApiResponse(responseCode = "400", description = "ID inválido en la solicitud"),
            @ApiResponse(responseCode = "404", description = "Punto de venta no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<com.cavallaro.kafka.dto.ApiResponse<Boolean>> deleteSellingPoint(@Valid @PathVariable Integer id);



}
