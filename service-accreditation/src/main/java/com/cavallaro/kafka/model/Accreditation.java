package com.cavallaro.kafka.model;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@Document(collection = "accreditationsV2")
public class Accreditation {
    @Id
    private String id;
    private Double amount;
    private Integer sellingPointId;
    private String  sellingPointName;
    private LocalDateTime receptionDate;
}
