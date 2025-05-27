package com.cavallaro.kafka.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
@Document(collection = "selling_costs")
public class SellingCost {

    @Id
    private SellingCostId id;
    private Double cost;
}
