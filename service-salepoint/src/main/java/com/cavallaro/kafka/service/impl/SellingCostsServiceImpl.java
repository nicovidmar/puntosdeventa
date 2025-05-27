package com.cavallaro.kafka.service.impl;


import com.cavallaro.kafka.dto.ApiResponse;
import com.cavallaro.kafka.dto.SellingCostDTO;
import com.cavallaro.kafka.service.SellingCostsService;
import com.cavallaro.kafka.validation.SellingCostValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class SellingCostsServiceImpl implements SellingCostsService {


    private final CacheManager cacheManager;
    private  final SellingCostsCacheService sellingCostsCacheService;
    private final SellingCostValidator sellingCostValidator;

    @Override
    public ApiResponse<List<SellingCostDTO>> findAll() {
        final String ALL = "all";
        log.info("Buscando todos los costos de los puntos de venta...");


        Cache cache = cacheManager.getCache("sellingCosts");

        if (cache != null && cache.get(ALL) != null) {
            log.info("Obteniendo los costos de los puntos de venta {} desde el caché", ALL);
        } else {
            log.info("Consultando  los costos de los puntos de venta {} desde la base de datos", ALL);
        }

        List<SellingCostDTO> sellingPoints = sellingCostsCacheService.findAll();

        return  ApiResponse.success(sellingPoints);
    }

    @Override
    public  ApiResponse<Double> findCostById(Integer pointA, Integer pointB) {
        String index = String.format("%s-%s", pointA, pointB);
        log.info("Buscando el costo del punto de venta...");

        Cache cache = cacheManager.getCache("sellingCosts");

        if (cache != null && cache.get(index) != null) {
            log.info("Obteniendo el costo del punto de venta {} desde el caché", index);
        } else {
            log.info("Consultando  el costo del punto de venta {} desde la base de datos", index);
        }

        Optional<SellingCostDTO> sellingPoint = sellingCostsCacheService.findId( pointA,  pointB);

        return ApiResponse.success(
                sellingPoint.map(SellingCostDTO::getCost).orElse(0.0));

    }

    @Override
    public ApiResponse<SellingCostDTO> save(SellingCostDTO sellingCostDTO) {
        String index = String.format("%s-%s", sellingCostDTO.getId().getPointA(), sellingCostDTO.getId().getPointB());
        log.info("Iniciando la  creacoin de un nuevo costo para punto de venta...{} - {} - {}", sellingCostDTO.getId().getPointA() ,  sellingCostDTO.getId().getPointB(), sellingCostDTO.getCost());

        Cache cache = cacheManager.getCache("sellingCosts");

        if (cache != null && cache.get(index) != null) {
            log.info("Obteniendo el costo del punto de venta {} desde el caché", index);
        } else {
            log.info("Consultando  el costo del punto de venta {} desde la base de datos", index);
        }

        SellingCostDTO savedDTO = sellingCostsCacheService.save(sellingCostDTO);

        return  ApiResponse.success(savedDTO);
    }

    @Override
    public ApiResponse<Boolean> remove(Integer pointA, Integer pointB) {
        String index = String.format("%s-%s", pointA, pointB);

        log.info("El costo punto de venta {} {} va a ser eliminado", pointA, pointB);

        Cache cache = cacheManager.getCache("sellingCosts");

        if (cache != null && cache.get(index) != null) {
            log.info("Obteniendo el costo del punto de venta {} desde el caché", index);
        } else {
            log.info("Consultando  el costo del punto de venta {} desde la base de datos", index);
        }

        return  ApiResponse.success(sellingCostsCacheService.remove(pointA, pointB));
    }

    @Override
    public ApiResponse<Optional<SellingCostDTO>> update(Integer pointA, Integer pointB, Double cost) {
        String index = String.format("%s-%s", pointA, pointB);
        log.info("Iniciando actualización del costo para punto de venta: {}", index);

        Cache cache = cacheManager.getCache("sellingCosts");

        if (cache != null && cache.get(index) != null) {
            log.info("Obteniendo el costo del punto de venta {} desde el caché", index);
        } else {
            log.info("Consultando  el costo del punto de venta {} desde la base de datos", index);
        }

        Optional<SellingCostDTO> savedDTO = sellingCostsCacheService.update(pointA, pointB, cost);


        return  ApiResponse.success(savedDTO);
    }

    @Override
    public ApiResponse<List<SellingCostDTO>> getReachableSellingPoints(Integer pointA) {
        sellingCostValidator.validate(pointA);
        List<SellingCostDTO> reachableSellingPoints = this.findAll().getData().stream()
                .filter(a -> Objects.equals(a.getId().getPointA(), pointA))
                .collect(Collectors.toList());
        return ApiResponse.success(reachableSellingPoints);
    }


}
