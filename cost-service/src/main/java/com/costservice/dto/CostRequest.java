package com.costservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CostRequest {
    @NotNull(message = "El ID de origen no puede ser nulo")
    @Min(value = 1, message = "El ID de origen debe ser mayor que 0")
    private int idA;

    @NotNull(message = "El ID de destino no puede ser nulo")
    @Min(value = 1, message = "El ID de destino debe ser mayor que 0")
    private int idB;

    @NotNull(message = "El costo no puede ser nulo")
    @Min(value = 0, message = "El costo debe ser mayor o igual a 0")
    private int cost;
}
