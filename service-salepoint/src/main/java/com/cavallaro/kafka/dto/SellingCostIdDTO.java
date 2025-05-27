package com.cavallaro.kafka.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class SellingCostIdDTO implements Serializable {

    @NotNull
    @Min(1)
    private Integer pointA;

    @NotNull
    @Min(1)
    private Integer pointB;
}
