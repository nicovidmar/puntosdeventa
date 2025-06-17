package com.accreditationservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class AccreditationRequest {
    @NotNull(message = "El importe no puede ser nulo")
    @Min(value = 1, message = "El importe debe ser mayor a 0")
    private double amount;

    @NotNull(message = "El ID del punto de venta no puede ser nulo")
    private int pointOfSaleId;
}
