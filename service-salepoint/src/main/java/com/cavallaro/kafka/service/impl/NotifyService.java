package com.cavallaro.kafka.service.impl;

import com.cavallaro.kafka.dto.ApiResponse;
import com.cavallaro.kafka.dto.SellingPointDTO;
import com.cavallaro.kafka.service.SellingPointService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class NotifyService {

    private  final SellingPointService sellingPointService;

    public NotifyService(SellingPointService sellingPointService) {
        this.sellingPointService = sellingPointService;
    }

    @KafkaListener(topics = "t-notif-cache-empty")
    public void consume(String message){
        log.info("Se ha generedo una notificacion al metodo del SellingPointService::findAll ");

        log.info("mensaje de kafka es : {}", message );
        ApiResponse<List<SellingPointDTO>> response = sellingPointService.findAll();
        log.info("Response Parameter: {}", response.toString());
    }
}
