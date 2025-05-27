package com.cavallaro.kafka.service.impl;


import com.cavallaro.kafka.dto.ApiResponse;
import com.cavallaro.kafka.dto.SellingPointDTO;
import com.cavallaro.kafka.service.SellingPointCacheService;
import com.cavallaro.kafka.service.SellingPointService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
@AllArgsConstructor
public class SellingPointImpl implements SellingPointService {

    public static final String ALL = "all";
    private final CacheManager cacheManager;
    private  final SellingPointCacheService sellingPointCacheService;


    @Override
    public ApiResponse<List<SellingPointDTO>> findAll() {

        log.info("Buscando todos los puntos de venta...");
        Cache cache = cacheManager.getCache("sellingPoints");

        if (cache != null && cache.get(ALL) != null) {
            log.info("Obteniendo punto de venta {} desde el caché", ALL);
        } else {
            log.info("Consultando punto de venta {} desde la base de datos", ALL);
        }

        List<SellingPointDTO> sellingPoints = sellingPointCacheService.getCachedSellingPoints();

        return  ApiResponse.success(sellingPoints);
    }

    @Override
    public ApiResponse<SellingPointDTO> save(SellingPointDTO sellingPointDTO) {

        log.info("Iniciado un nuevo puntos de venta...");
        Cache cache = cacheManager.getCache("sellingPoints");

        if (cache != null && cache.get(sellingPointDTO.getId()) != null) {
            log.info("Obteniendo punto de venta  {} desde el caché", sellingPointDTO.getId());
        } else {
            log.info("Consultando punto de venta {} desde la base de datos", sellingPointDTO.getId());
        }

        SellingPointDTO savedDTO = sellingPointCacheService.saveSellingPoint(sellingPointDTO);
        return ApiResponse.success(savedDTO);
    }

    @Override
    public ApiResponse<Boolean> remove(Integer id) {
       if (sellingPointCacheService.removeSellingPoint(id)){
           return   ApiResponse.success(Boolean.TRUE);
        }else{
            return  ApiResponse.error(HttpStatus.NOT_FOUND.value(), "Punto de Venta no encontrado");
        }
    }

    @Override
    public ApiResponse<SellingPointDTO> update(Integer id, String name) {

        log.info("Iniciando actualización del punto de venta con ID={}", id);

        Cache cache = cacheManager.getCache("sellingPoints");

        if (cache != null && cache.get(id) != null) {
            log.info("Obteniendo punto de venta {} desde el caché", id);
        } else {
            log.info("Consultando punto de venta {} desde la base de datos", id);
        }

        SellingPointDTO savedDTO = sellingPointCacheService.updateSellingPointDTO(id, name);
        return   ApiResponse.success(savedDTO);
    }


}
