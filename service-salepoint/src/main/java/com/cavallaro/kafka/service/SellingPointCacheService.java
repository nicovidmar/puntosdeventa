package com.cavallaro.kafka.service;



import com.cavallaro.kafka.dto.SellingPointDTO;

import java.util.List;

public interface SellingPointCacheService {
    List<SellingPointDTO> getCachedSellingPoints();

    boolean removeSellingPoint(Integer id);

    SellingPointDTO saveSellingPoint(SellingPointDTO sellingPointDTO);

    SellingPointDTO updateSellingPointDTO(Integer id, String name);
}
