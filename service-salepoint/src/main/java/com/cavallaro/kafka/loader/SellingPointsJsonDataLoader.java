package com.cavallaro.kafka.loader;


import com.cavallaro.kafka.model.SellingPoint;
import com.cavallaro.kafka.repository.SellingPointsRepository;
import com.cavallaro.kafka.service.SellingPointService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@DependsOn("mongoTemplate")
@Component
@AllArgsConstructor
@Slf4j
public class SellingPointsJsonDataLoader implements ApplicationRunner {

    private final SellingPointsRepository sellingPointsRepository;
    private final ObjectMapper objectMapper;
    private final SellingPointService sellingPointService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        final String SELLING_POINTS_JSON = "selling_points.json";
        log.info("Intentando cargar {}...", SELLING_POINTS_JSON);


        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(SELLING_POINTS_JSON);

        if (inputStream == null) {
            log.error("El archivo {} no fue encontrado en el classpath.",  SELLING_POINTS_JSON );
            return;
        }

        if (sellingPointsRepository.count() == 0) {
            List<SellingPoint> salesPoints = objectMapper.readValue(inputStream, new TypeReference<>() {});
            sellingPointsRepository.saveAll(salesPoints);
            log.info("Datos iniciales de {}  insertados desde JSON.", SELLING_POINTS_JSON);

        } else {
            log.info("Datos iniciales de {} ya fueron cargados desde JSON.", SELLING_POINTS_JSON);
        }

        this.sellingPointService.findAll();


    }
}