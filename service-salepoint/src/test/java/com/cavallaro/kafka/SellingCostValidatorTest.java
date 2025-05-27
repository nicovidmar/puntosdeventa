package com.cavallaro.kafka;


import com.cavallaro.kafka.exception.SellingCostsServiceException;
import com.cavallaro.kafka.model.SellingCost;
import com.cavallaro.kafka.model.SellingCostId;
import com.cavallaro.kafka.repository.SellingCostsRepository;
import com.cavallaro.kafka.validation.impl.SellingCostValidatorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class SellingCostValidatorTest {

    @Mock
    private SellingCostsRepository sellingCostsRepository;

    @InjectMocks
    private SellingCostValidatorImpl sellingCostValidator;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testValidate_NegativePoints_ShouldThrowException() {
        assertThrows(SellingCostsServiceException.class, () ->
                sellingCostValidator.validate(-1, 2, 10.0), "Los puntos no pueden ser negativos.");
    }

    @Test
    public void testValidate_SameOrGreaterPointA_ShouldThrowException() {
        assertThrows(SellingCostsServiceException.class, () ->
                sellingCostValidator.validate(3, 3, 10.0), "El punto A debe ser diferente y menor que el punto B.");
    }

    @Test
    public void testValidate_NegativeCost_ShouldThrowException() {
        assertThrows(SellingCostsServiceException.class, () ->
                sellingCostValidator.validate(1, 2, -10.0), "El costo no puede ser negativo.");
    }

    @Test
    public void testValidate_SamePointNonZeroCost_ShouldThrowException() {
        assertThrows(SellingCostsServiceException.class, () ->
                sellingCostValidator.validate(2, 2, 5.0), "El costo de ir al mismo punto de venta debe ser 0.");
    }

    @Test
    public void testValidate_InverseCostMismatch_ShouldThrowException() {
        SellingCostId inverseId = SellingCostId.builder().pointA(2).pointB(1).build();
        SellingCost inverseCost = SellingCost.builder().cost(15.0).build();

        when(sellingCostsRepository.findById(inverseId)).thenReturn(Optional.of(inverseCost));

        assertThrows(SellingCostsServiceException.class, () ->
                sellingCostValidator.validate(1, 2, 10.0), "El costo entre A y B debe ser el mismo que el camino inverso.");
    }

    @Test
    public void testValidate_DirectPathExists_ShouldThrowException() {
        SellingCostId id = SellingCostId.builder().pointA(1).pointB(2).build();
        when(sellingCostsRepository.existsById(id)).thenReturn(true);

        assertThrows(SellingCostsServiceException.class, () ->
                sellingCostValidator.validate(1, 2, 10.0), "Ya existe un camino directo entre estos puntos de venta.");
    }

    @Test
    public void testValidate_UnreachablePoint_ShouldThrowException() {
        when(sellingCostsRepository.existsByIdPointA(1)).thenReturn(false);
        when(sellingCostsRepository.existsByIdPointB(2)).thenReturn(false);

        assertThrows(SellingCostsServiceException.class, () ->
                sellingCostValidator.validate(1, 2, 10.0), "No existe conexión directa entre los puntos de venta A y B.");
    }

}
