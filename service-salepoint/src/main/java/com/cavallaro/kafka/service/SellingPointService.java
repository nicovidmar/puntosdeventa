package com.cavallaro.kafka.service;


import com.cavallaro.kafka.dto.ApiResponse;
import com.cavallaro.kafka.dto.SellingPointDTO;

import java.util.List;

public interface SellingPointService {

    ApiResponse<List<SellingPointDTO>> findAll();
    ApiResponse<SellingPointDTO> save(SellingPointDTO sellingPointDTO);
    ApiResponse<Boolean> remove(Integer id);
    ApiResponse<SellingPointDTO> update(Integer id, String name);

}
