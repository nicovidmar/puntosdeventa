package com.cavallaro.kafka;



import com.cavallaro.kafka.exception.SellingPathServiceValidator;
import com.cavallaro.kafka.validation.SellingPathValidator;
import com.cavallaro.kafka.validation.impl.SellingPathValidatorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SellingPathValidatorImplTest {

    private SellingPathValidator sellingPathValidator;

    @BeforeEach
    public void setUp() {
        sellingPathValidator = new SellingPathValidatorImpl();
    }

    @Test
    public void testValidate_WithNegativePoints_ShouldThrowException() {
        SellingPathServiceValidator exception = assertThrows(SellingPathServiceValidator.class, () -> {
            sellingPathValidator.validate(-1, 5);
        }, "Se esperaba una excepción cuando el punto A es negativo.");
        assertEquals("Los puntos no pueden ser negativos.", exception.getMessage());
    }

    @Test
    public void testValidate_WithSamePoints_ShouldThrowException() {
        SellingPathServiceValidator exception = assertThrows(SellingPathServiceValidator.class, () -> {
            sellingPathValidator.validate(5, 5);
        }, "Se esperaba una excepción cuando los puntos son iguales.");
        assertEquals("El punto A debe ser diferente y menor que el punto B.", exception.getMessage());
    }

    @Test
    public void testValidate_WithPointAGreaterThanPointB_ShouldThrowException() {
        SellingPathServiceValidator exception = assertThrows(SellingPathServiceValidator.class, () -> {
            sellingPathValidator.validate(10, 5);
        }, "Se esperaba una excepción cuando el punto A es mayor que el punto B.");
        assertEquals("El punto A debe ser diferente y menor que el punto B.", exception.getMessage());

    }

    @Test
    public void testValidate_WithValidPoints_ShouldNotThrowException() {
        assertDoesNotThrow(() -> sellingPathValidator.validate(1, 5));
    }
}
