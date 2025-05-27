package com.cavallaro.kafka.controller;


import com.cavallaro.kafka.dto.PathNameResult;
import com.cavallaro.kafka.dto.SellingCostDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(name = "Selling Costs API", description = "API para gestionar costos asociados de los puntos de ventas.")
public interface SellingCostsApi {
    @Operation(summary = "Establecer costo entre dos puntos de venta", description = "Registra el costo entre dos puntos de venta")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Costo agregado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida - Datos incorrectos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    ResponseEntity<com.cavallaro.kafka.dto.ApiResponse<SellingCostDTO>>  addCost(SellingCostDTO costPoint);

    @Operation(summary = "Obtener el costo entre dos puntos de venta", description = "Devuelve el costo entre dos puntos de venta específicos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Costo encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Costo no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    ResponseEntity<com.cavallaro.kafka.dto.ApiResponse<Double>> getCost(Integer pointA, Integer pointB);

    @Operation(summary = "Eliminar un costo entre dos puntos de venta", description = "Elimina un costo por PuntoA y PuntoB")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Costo eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Costo no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    ResponseEntity<com.cavallaro.kafka.dto.ApiResponse<Boolean>> deleteCost(Integer pointA, Integer pointB);

    @Operation(summary = "Obtener todos los costos", description = "Devuelve un mapa con los costos entre todos los puntos de venta")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de costos obtenida exitosamente"),
            @ApiResponse(responseCode = "204", description = "No hay costos registrados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    ResponseEntity<com.cavallaro.kafka.dto.ApiResponse<List<SellingCostDTO>>> findAllCosts();

    @Operation(
            summary = "Consultar puntos de venta accesibles",
            description = "Obtiene una lista de puntos de venta accesibles desde un punto A, junto con los costos asociados.")

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de puntos de venta accesibles encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitud incorrecta"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No se encontraron puntos de venta accesibles"
            )
    }
    )
    ResponseEntity<com.cavallaro.kafka.dto.ApiResponse<List<SellingCostDTO>>> getReachableSellingPoints(
            @PathVariable Integer pointA);

    @Operation(summary = "Obtener el camino más barato entre dos puntos de venta",  description = "Este endpoint calcula el camino más barato entre dos puntos de venta A y B utilizando el algoritmo de Dijkstra.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Camino encontrado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos"),
            @ApiResponse(responseCode = "404", description = "No se encontró un camino entre los puntos de venta"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    ResponseEntity<com.cavallaro.kafka.dto.ApiResponse<PathNameResult>> getCheapestPath(Integer pointA, Integer pointB);

}
