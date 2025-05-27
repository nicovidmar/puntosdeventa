package com.cavallaro.kafka.validation.impl;


import com.cavallaro.kafka.exception.SellingCostsServiceException;
import com.cavallaro.kafka.model.SellingCost;
import com.cavallaro.kafka.model.SellingCostId;
import com.cavallaro.kafka.repository.SellingCostsRepository;
import com.cavallaro.kafka.service.SellingPointCacheService;
import com.cavallaro.kafka.validation.SellingCostValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SellingCostValidatorImpl implements SellingCostValidator {

    private final SellingCostsRepository sellingCostsRepository;

    private final SellingPointCacheService sellingPointCacheService;

    @Override
    public void validate(Integer pointA, Integer pointB, Double cost) {


        if (pointA < 0 || pointB < 0) {
            throw new SellingCostsServiceException(HttpStatus.BAD_REQUEST.value(),"Los puntos no pueden ser negativos.");
        }
        if (pointA.equals(pointB) || pointA > pointB) {
            throw new SellingCostsServiceException(HttpStatus.BAD_REQUEST.value(),"El punto A debe ser diferente y menor que el punto B.");
        }

        // El costo nunca podría ser menor a cero
        if (cost < 0) {
            throw new SellingCostsServiceException(HttpStatus.BAD_REQUEST.value(),"El costo no puede ser negativo.");
        }

        // El costo de ir a un punto de venta a sí mismo debe ser 0
        if (Objects.equals(pointA, pointB) && cost != 0) {
            throw new SellingCostsServiceException(HttpStatus.BAD_REQUEST.value(),"El costo de ir al mismo punto de venta debe ser 0.");
        }

        // Verificar si el camino inverso tiene el mismo costo
        SellingCostId inverseId = SellingCostId.builder().pointA(pointB).pointB(pointA).build();
        Optional<SellingCost> inverseCost = sellingCostsRepository.findById(inverseId);

        if (inverseCost.isPresent() && !Objects.equals(inverseCost.get().getCost(), cost)) {
            throw new SellingCostsServiceException(HttpStatus.BAD_REQUEST.value(),"El costo entre A y B debe ser el mismo que el camino inverso.");
        }

        // 4️⃣ Verificar si existe un camino directo entre A y B (no puede haber caminos paralelos)
        if (sellingCostsRepository.existsById(SellingCostId.builder().pointA(pointA).pointB(pointB).build())) {
            throw new SellingCostsServiceException(HttpStatus.CONFLICT.value(),"Ya existe un camino directo entre estos puntos de venta.");
        }

        // 5️⃣ Validar si B es inalcanzable desde A
        if (!isReachable(pointA, pointB)) {
            throw new SellingCostsServiceException(HttpStatus.NOT_FOUND.value(),"No existe conexión directa entre los puntos de venta A y B.");
        }
    }

    @Override
    public void validate(Integer point) {
        if (point < 0 ) {
            throw new SellingCostsServiceException(HttpStatus.BAD_REQUEST.value(),"El  punto no puede ser negativo.");
        }
        sellingPointCacheService.getCachedSellingPoints().stream()
        .filter( p -> p.getId().equals(point))
        .findFirst()
                .orElseThrow(()-> new SellingCostsServiceException(HttpStatus.NOT_FOUND.value(),"No existe el puntos de venta " + point));

    }

    private boolean isReachable(Integer pointA, Integer pointB) {
        return sellingCostsRepository.existsByIdPointA(pointA) || sellingCostsRepository.existsByIdPointB(pointB);
    }
}
