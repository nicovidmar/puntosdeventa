package com.cavallaro.kafka.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class SellingCostId implements Serializable {
    @NotNull
    @Min(1)
    private Integer pointA;

    @NotNull
    @Min(1)
    private Integer pointB;
}
