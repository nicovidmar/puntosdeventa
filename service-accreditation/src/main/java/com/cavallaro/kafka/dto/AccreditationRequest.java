package com.cavallaro.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class AccreditationRequest {

    @NonNull
    private Double amount;
    @NonNull
    private Integer sellingPointId;
}
