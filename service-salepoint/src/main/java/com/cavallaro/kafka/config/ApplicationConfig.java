package com.cavallaro.kafka.config;


import com.cavallaro.kafka.dto.SellingCostDTO;
import com.cavallaro.kafka.dto.SellingCostIdDTO;
import com.cavallaro.kafka.model.SellingCost;
import com.cavallaro.kafka.model.SellingCostId;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();


            modelMapper.typeMap(SellingCost.class, SellingCostDTO.class)
                    .addMappings(mapper -> mapper.map(SellingCost::getId, SellingCostDTO::setId));

            modelMapper.typeMap(SellingCostId.class, SellingCostIdDTO.class)
                    .addMappings(mapper -> {
                        mapper.map(SellingCostId::getPointA, SellingCostIdDTO::setPointA);
                        mapper.map(SellingCostId::getPointB, SellingCostIdDTO::setPointB);
                    });



        return modelMapper;
    }
}