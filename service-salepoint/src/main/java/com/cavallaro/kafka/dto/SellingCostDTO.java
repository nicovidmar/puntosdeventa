package com.cavallaro.kafka.dto;



import com.cavallaro.kafka.model.SellingCostId;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SellingCostDTO {
    private SellingCostId id;

    @NotNull
    @DecimalMin("0.0")
    private Double cost;


}
