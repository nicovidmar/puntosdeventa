package com.cavallaro.kafka.service.impl;



import com.cavallaro.kafka.cache.CacheableService;
import com.cavallaro.kafka.dto.SellingPointDTO;
import com.cavallaro.kafka.exception.SellingPointsServiceException;
import com.cavallaro.kafka.model.SellingPoint;
import com.cavallaro.kafka.repository.SellingPointsRepository;
import com.cavallaro.kafka.service.SellingPointCacheService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service

@Slf4j
public class SellingPointServiceCacheImpl implements SellingPointCacheService {

    private final SellingPointsRepository sellingPointsRepository;
    private final CacheableService<SellingPointDTO, Integer> sellingPointCache;
    private final ModelMapper modelMapper;

    @Autowired
    public SellingPointServiceCacheImpl(SellingPointsRepository sellingPointsRepository,
                                        @Qualifier("SellingPointsCache") CacheableService<SellingPointDTO, Integer> sellingPointCache,
                                        ModelMapper modelMapper) {
        this.sellingPointsRepository = sellingPointsRepository;
        this.sellingPointCache =  sellingPointCache;
        this.modelMapper = modelMapper;

    }

    @Cacheable(value = "sellingPoints", key = "'all'")
    @Override
    public List<SellingPointDTO> getCachedSellingPoints() {
        log.info("Buscando todos los  puntos de venta...");
        return sellingPointsRepository.findAll().stream()
                .map(sellingCost -> modelMapper.map(sellingCost, SellingPointDTO.class))
                .collect(Collectors.toList());
    }


    @CacheEvict(value = "sellingPoints", key = "#id")
    @Override
    public boolean removeSellingPoint(Integer id) {
        log.info("El punto de venta {} va a ser eliminado", id);

        Optional<SellingPoint> existing = sellingPointsRepository.findById(id);

        if (existing.isEmpty()) {
            log.info("El punto de venta {} no fue encontrado", id);
            return false;
        } else {
            sellingPointsRepository.deleteById(id);
            log.info("El punto de venta {} fue eliminado de la base de datos", id);

            sellingPointCache.remove(id);

            return true;
        }
    }

    @CachePut(value = "sellingPoints", key = "#sellingPointDTO.id")
    @Override
    public SellingPointDTO saveSellingPoint(SellingPointDTO sellingPointDTO) {


        SellingPoint bean = SellingPoint.builder().id(sellingPointDTO.getId()).name(sellingPointDTO.getName()).build();
        Integer id = sellingPointDTO.getId();

        Optional<SellingPoint> existing = sellingPointsRepository.findById(id);
        SellingPoint saved = null;
        SellingPointDTO savedDTO = null;
        if (existing.isEmpty()) {
            saved = sellingPointsRepository.save(bean);

            log.info("El puntos de venta...{} - {} fue creado en la base de datos", sellingPointDTO.getId() , sellingPointDTO.getName());

            savedDTO =modelMapper.map( saved, SellingPointDTO.class);
            sellingPointCache.save(savedDTO);

        } else{
            throw new SellingPointsServiceException(HttpStatus.CONFLICT.value(),
                    "Se encontró el punto de venta con ID=" + sellingPointDTO.getId());

        }


        return savedDTO;
    }

    @Override
    @CachePut(value = "sellingPoints", key = "#id")
    public SellingPointDTO updateSellingPointDTO(Integer id, String name) {

        Optional<SellingPoint> optionalSellingPoint = sellingPointsRepository.findById(id);

        if (optionalSellingPoint.isEmpty()) {
            log.warn("No se encontró el punto de venta con ID={}", id);
            throw new SellingPointsServiceException(HttpStatus.NOT_FOUND.value(),
                    "No se encontró el punto de venta con ID=" + id);
        }

        SellingPoint existing = optionalSellingPoint.get();
        existing.setName(name);
        SellingPoint saved = sellingPointsRepository.save(existing);
        SellingPointDTO savedDTO = modelMapper.map(saved, SellingPointDTO.class);

        sellingPointCache.update(id, savedDTO);


        log.info("Punto de venta con ID={} actualizado exitosamente.", id);

        return savedDTO;
    }
}
