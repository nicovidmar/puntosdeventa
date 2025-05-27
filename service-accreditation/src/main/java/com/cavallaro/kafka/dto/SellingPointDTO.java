package com.cavallaro.kafka.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
@JsonRootName(value = "sellingPoint")
public class SellingPointDTO {

    @NotNull
    @JsonProperty("id")
    private Integer id;

    @NotNull
    @NotBlank
    @JsonProperty("name")
    private String name;
}
