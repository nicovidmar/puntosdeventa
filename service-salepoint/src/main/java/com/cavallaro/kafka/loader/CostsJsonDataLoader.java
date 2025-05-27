package com.cavallaro.kafka.loader;


import com.cavallaro.kafka.model.SellingCost;
import com.cavallaro.kafka.repository.SellingCostsRepository;
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
public class CostsJsonDataLoader implements ApplicationRunner {

    private final SellingCostsRepository sellingCostsRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        final String SELLING_COSTS_JSON = "costs.json";
        log.info("Intentando cargar {}...", SELLING_COSTS_JSON);


        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(SELLING_COSTS_JSON);

        if (inputStream == null) {
            log.error("El archivo {} no fue encontrado en el classpath.",  SELLING_COSTS_JSON );
            return;
        }

        if (sellingCostsRepository.count() == 0) {
            List<SellingCost> costs = objectMapper.readValue(inputStream, new TypeReference<>() {});
            sellingCostsRepository.saveAll(costs);
            log.info("Datos iniciales de {}  insertados desde JSON.", SELLING_COSTS_JSON);

        } else {
            log.info("Datos iniciales de {} ya fueron cargados desde JSON.", SELLING_COSTS_JSON);
        }



    }
}