package com.cavallaro.kafka.pathfinder.service;


import com.cavallaro.kafka.dto.PathNameResult;
import com.cavallaro.kafka.dto.SellingPointDTO;
import com.cavallaro.kafka.exception.SellingCostsServiceException;
import com.cavallaro.kafka.pathfinder.dto.PathResult;
import com.cavallaro.kafka.service.SellingPointCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SellingPathServiceImpl implements SellingPathService {

    private final SellingPointCacheService sellingPointCacheService;
    private final PathFindingService pathFindingService;

    @Override
    public PathNameResult getCheapestPath(String algoritmo, Integer pointA, Integer pointB) {


        PathResult result = pathFindingService.calculatePathFinder( algoritmo, pointA, pointB);

        if (result.getPath().isEmpty()) {
            throw new SellingCostsServiceException(HttpStatus.NOT_FOUND.value(), "No existe un camino entre los puntos " + pointA + " y " + pointB);
        }
        List<String> pathWithNames = null;

        // Convertir los IDs de los puntos a nombres usando el caché
        pathWithNames = result.getPath().stream()
                .map(id -> sellingPointCacheService.getCachedSellingPoints().stream()
                        .filter(p -> p.getId().equals(id))
                        .findFirst()
                        .map(SellingPointDTO::getName)
                        .orElse("No se encontro Punto de venta"))
                .collect(Collectors.toList());

        log.warn("El path en contrado es  {} " , pathWithNames);
        return new PathNameResult(pathWithNames, result.getTotalCost());
    }
}
