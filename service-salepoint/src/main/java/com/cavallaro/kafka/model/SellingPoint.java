package com.cavallaro.kafka.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@Document(collection = "selling_points")
public class SellingPoint {
     @Id
     private Integer id;
     private String name;
}
