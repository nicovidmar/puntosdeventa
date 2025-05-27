package com.cavallaro.kafka.controller;


import com.cavallaro.kafka.dto.PathNameResult;
import com.cavallaro.kafka.dto.ApiResponse;
import com.cavallaro.kafka.dto.SellingCostDTO;
import com.cavallaro.kafka.pathfinder.service.SellingPathService;
import com.cavallaro.kafka.service.SellingCostsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value="/api/v1/costs")
@RequiredArgsConstructor
@Slf4j
public class SellingCostsController implements SellingCostsApi {
    private final SellingCostsService sellingCostsService;
    private final SellingPathService sellingPathService;

    @PostMapping()
    @Override
    public ResponseEntity<ApiResponse<SellingCostDTO>> addCost(@RequestBody @Valid  SellingCostDTO costPoint) {
        ResponseEntity<ApiResponse<SellingCostDTO>> response;
        log.info("llamada a un metodo del controller SellingCostsController::addCost ");

        log.info("Request Parameter: {}", costPoint);

        if (costPoint == null || costPoint.getId().getPointA() <= 0 || costPoint.getId().getPointB() <= 0 || costPoint.getCost() <= 0) {
            response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body( ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "El costo del Punto de Venta incorrecto"));
        } else {
            response = ResponseEntity.status(HttpStatus.CREATED).body(sellingCostsService.save(costPoint));
        }

        log.info("Response Parameter: {}", response);

        return response;
    }

    @GetMapping("/{pointA}/{pointB}")
    @Override
    public ResponseEntity<ApiResponse<Double>> getCost(@PathVariable Integer pointA, @PathVariable Integer pointB) {
        log.info("llamada a un metodo del controller SellingCostsController::getCost ");

        log.info("Request Parameter: {} {}", pointA ,pointB);
        ResponseEntity<ApiResponse<Double>> response = ResponseEntity.status(HttpStatus.OK).body(sellingCostsService.findCostById(pointA, pointB));
        log.info("Response Parameter: {}", response);
        return response;
    }

    @DeleteMapping("/{pointA}/{pointB}")
    @Override
    public ResponseEntity<ApiResponse<Boolean>> deleteCost(@PathVariable Integer pointA, @PathVariable Integer pointB) {
        log.info("llamada a un metodo del controller SellingCostsController::deleteCost ");

        log.info("Request Parameter: {} {}", pointA ,pointB);
        ResponseEntity<ApiResponse<Boolean>> response = ResponseEntity.status(HttpStatus.OK).body(sellingCostsService.remove(pointA, pointB));
        log.info("Response Parameter: {}", response);
        return response;
    }

    @GetMapping
    @Override
    public ResponseEntity<ApiResponse<List<SellingCostDTO>>> findAllCosts() {
        log.info("llamada a un metodo del controller SellingCostsController::findAllCosts ");

        log.info("Request Parameter: {} ", "NA");
        ResponseEntity<ApiResponse<List<SellingCostDTO>>> response = ResponseEntity.ok(sellingCostsService.findAll());
        log.info("Response Parameter: {}", response);
        return response;
    }

    @GetMapping("/selling-points/{pointA}/reachable")
    public ResponseEntity<ApiResponse<List<SellingCostDTO>>> getReachableSellingPoints(
            @PathVariable Integer pointA) {
        log.info("llamada a un metodo del controller SellingCostsController::getReachableSellingPoints ");

        log.info("Request Parameter: {} ", pointA);
        ResponseEntity<ApiResponse<List<SellingCostDTO>>> response = ResponseEntity.ok(sellingCostsService.getReachableSellingPoints(pointA));
        log.info("Response Parameter: {}", response);
        return response;
    }

    @GetMapping("/cheapest-path/{pointA}/{pointB}")
    @Override
    public ResponseEntity<ApiResponse<PathNameResult>> getCheapestPath(@PathVariable Integer pointA, @PathVariable Integer pointB) {

        log.info("llamada a un metodo del controller SellingCostsController::getCheapestPath ");

        log.info("Request Parameter: {} {}", pointA,pointB);
        ResponseEntity<ApiResponse<PathNameResult>> response = ResponseEntity.ok(ApiResponse.success(sellingPathService.getCheapestPath("dijkstra", pointA, pointB)));
        log.info("Response Parameter: {}", response);
        return response;

    }

}
