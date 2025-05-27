package com.cavallaro.kafka.service.impl;

import com.cavallaro.kafka.dto.AccreditationRequest;
import com.cavallaro.kafka.dto.SellingPointDTO;
import com.cavallaro.kafka.exception.AccreditationServiceException;
import com.cavallaro.kafka.model.Accreditation;
import com.cavallaro.kafka.repository.AccreditationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class AccreditationService {

    @Autowired
    private AccreditationsRepository repository;

    @Autowired
    private  CacheManager cacheManager;

    private static final String CACHE_NAME = "sellingPoints";
    private static final String CACHE_KEY_ALL = "all";
    public List<Accreditation> findAll() {
        return repository.findAll();
    }

    public Optional<Accreditation> findById(String id) {
        return repository.findById(id);
    }

    public Accreditation save(AccreditationRequest request)  {


        List<SellingPointDTO> sellingPointsCache = cacheManager.getCache(CACHE_NAME).get(CACHE_KEY_ALL, List.class);

        Optional<SellingPointDTO> sellingPointDTO = Optional.ofNullable(sellingPointsCache).orElse(Collections.emptyList())
                .stream().filter(dto -> Objects.equals(dto.getId(), request.getSellingPointId())).findFirst();


        return sellingPointDTO.map(point -> {
            Accreditation entity = Accreditation.builder().amount(request.getAmount()).receptionDate(LocalDateTime.now()).sellingPointId(point.getId()).sellingPointName(point.getName()).build();
            return repository.save(entity);

        }).orElseThrow(() -> new AccreditationServiceException(HttpStatus.NOT_FOUND.value(), "selling_point_not_found" + request.getSellingPointId()));

    }

    public Accreditation update(String id, Accreditation accreditation) {
        Accreditation  accreditationDB =repository.findById(id).orElseThrow(()->new AccreditationServiceException(HttpStatus.NOT_FOUND.value(), "accreditation_not_found" + id));

        List<SellingPointDTO> sellingPointsCache = cacheManager.getCache(CACHE_NAME).get(CACHE_KEY_ALL, List.class);

        String  pointOfSellName= null;

        boolean  accreditationHasChange= false;

        if(accreditation.getSellingPointId() !=null && !accreditation.getSellingPointId().equals(accreditationDB.getSellingPointId())){
            Optional<SellingPointDTO> sellingPointDTO = Optional.ofNullable(sellingPointsCache).orElse(Collections.emptyList())
                    .stream().filter(dto -> Objects.equals(dto.getId(), accreditation.getSellingPointId())).findFirst();


            pointOfSellName = sellingPointDTO.map(SellingPointDTO::getName)
                    .orElseThrow(() -> new AccreditationServiceException(HttpStatus.NOT_FOUND.value(), "selling_point_not_found" + accreditation.getSellingPointId()));

        }

        if(pointOfSellName !=null){
            accreditationHasChange=true;
            accreditationDB.setSellingPointName(pointOfSellName);
            accreditationDB.setSellingPointId(accreditation.getSellingPointId());
        }

        if(accreditation.getAmount() != null && !accreditation.getAmount().equals(accreditationDB.getAmount())){
            accreditationHasChange=true;
            accreditationDB.setAmount(accreditation.getAmount());
        }

        return accreditationHasChange?repository.save(accreditationDB):accreditationDB;
    }

    public void deleteById(String id) {
        repository.deleteById(id);
    }
}
