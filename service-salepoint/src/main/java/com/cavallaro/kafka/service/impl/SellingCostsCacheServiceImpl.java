package com.cavallaro.kafka.service.impl;


import com.cavallaro.kafka.cache.CacheableService;
import com.cavallaro.kafka.dto.SellingCostDTO;
import com.cavallaro.kafka.exception.SellingCostsServiceException;
import com.cavallaro.kafka.model.SellingCost;
import com.cavallaro.kafka.model.SellingCostId;
import com.cavallaro.kafka.repository.SellingCostsRepository;
import com.cavallaro.kafka.validation.SellingCostValidator;
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
public class SellingCostsCacheServiceImpl implements SellingCostsCacheService{

    private final SellingCostsRepository sellingCostsRepository;
    private final CacheableService<SellingCostDTO, SellingCostId> sellingcCostCache;
    private final ModelMapper modelMapper;
    private final SellingCostValidator sellingCostValidator;

    @Autowired
    public SellingCostsCacheServiceImpl(SellingCostsRepository sellingCostsRepository,
                                   @Qualifier("SellingCostsCache") CacheableService<SellingCostDTO, SellingCostId> sellingcCostCache,
                                   ModelMapper modelMapper, SellingCostValidator sellingCostValidator) {
        this.sellingCostsRepository = sellingCostsRepository;
        this.sellingcCostCache = sellingcCostCache;
        this.modelMapper = modelMapper;
        this.sellingCostValidator = sellingCostValidator;
    }

    @Cacheable(value = "sellingCosts", key = "'all'")
    @Override
    public List<SellingCostDTO> findAll() {
        log.info("Buscando todos los costos de los puntos de venta...");
        return  sellingCostsRepository.findAll().stream()
                .map(sellingCost -> modelMapper.map(sellingCost, SellingCostDTO.class))
                .collect(Collectors.toList());
    }

    @Cacheable(value = "sellingCosts", key = "#pointA + '-' + #pointB")
    @Override
    public Optional<SellingCostDTO> findId(Integer pointA, Integer pointB) {
        SellingCostId id = SellingCostId.builder().pointB(pointB).pointA(pointA).build();

       return sellingCostsRepository.findById(id)
                .map(sellingCost -> modelMapper.map(sellingCost, SellingCostDTO.class));


    }

    @CachePut(value = "sellingCosts", key = "#sellingCostDTO.id.pointA + '-' + #sellingCostDTO.id.pointB")
    @Override
    public SellingCostDTO save(SellingCostDTO sellingCostDTO) {
        log.info("Iniciar crear un nuevo costo para punto de venta...{} - {} - {}", sellingCostDTO.getId().getPointA() ,  sellingCostDTO.getId().getPointB(), sellingCostDTO.getCost());


        sellingCostValidator.validate(sellingCostDTO.getId().getPointA(), sellingCostDTO.getId().getPointB(), sellingCostDTO.getCost());
        log.info("El costo del punto de venta fue validado correctamente");

        SellingCostId id  = modelMapper.map(sellingCostDTO.getId(), SellingCostId.class);
        if(sellingCostsRepository.existsById(id)){
            throw  new SellingCostsServiceException(HttpStatus.CONFLICT.value(),"El costo para ese punto de venta con ID " + id.toString() + " ya existe.");
        }

        SellingCost bean = modelMapper.map(sellingCostDTO, SellingCost.class);

        SellingCost saved = sellingCostsRepository.save(bean);
        SellingCostDTO savedDTO = modelMapper.map(saved, SellingCostDTO.class);

        sellingcCostCache.save(savedDTO);

        return  savedDTO;
    }

    @CacheEvict(value = "sellingCosts", key = "#pointA + '-' + #pointB")
    @Override
    public boolean remove(Integer pointA, Integer pointB) {
        log.info("El costo punto de venta {} {} va a ser eliminado", pointA, pointB);
        SellingCostId id  = SellingCostId.builder().pointA(pointA).pointB(pointB).build();

        Optional<SellingCost> existing = sellingCostsRepository.findById(id);

        if (existing.isEmpty()) {
            log.info("El costo de punto de venta {} {} no fue encontrado", pointA, pointB);
            return false;
        } else {
            sellingCostsRepository.deleteById(id);
            log.info("El costo de punto de venta {} {} fue eliminado de la base de datos",pointA, pointB);

            sellingcCostCache.remove(id);

            return true;
        }
    }

    @CachePut(value = "sellingCosts", key = "#pointA + '-' + #pointB")
    @Override
    public Optional<SellingCostDTO> update(Integer pointA, Integer pointB, Double cost) {
        log.info("Iniciando actualización del costo para punto de venta: A={} B={}", pointA, pointB);

        SellingCostId id = new SellingCostId(pointA, pointB);

        SellingCost existing = sellingCostsRepository.findById(id)
                .orElseThrow(() -> new SellingCostsServiceException(HttpStatus.CONFLICT.value(), "El costo para el punto A=" + pointA + ", B=" + pointB + " no existe."));

        log.info("Punto de venta encontrado. Actualizando costo...");

        existing.setCost(cost);
        SellingCost saved = sellingCostsRepository.save(existing);
        SellingCostDTO savedDTO = modelMapper.map(saved, SellingCostDTO.class);

        sellingcCostCache.update(saved.getId(), savedDTO);

        log.info("Costo actualizado exitosamente para punto A={}, B={}", pointA, pointB);

        return Optional.of(savedDTO);
    }
}
