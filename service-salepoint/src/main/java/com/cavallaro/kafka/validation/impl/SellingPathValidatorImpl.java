package com.cavallaro.kafka.validation.impl;


import com.cavallaro.kafka.exception.SellingPathServiceValidator;
import com.cavallaro.kafka.validation.SellingPathValidator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class SellingPathValidatorImpl implements SellingPathValidator {

    @Override
    public void validate(Integer pointA, Integer pointB) {
        if (pointA < 0 || pointB < 0) {
            throw new SellingPathServiceValidator(HttpStatus.BAD_REQUEST.value(),"Los puntos no pueden ser negativos.");
        }
        if (pointA.equals(pointB) || pointA > pointB) {
            throw new SellingPathServiceValidator(HttpStatus.BAD_REQUEST.value(),"El punto A debe ser diferente y menor que el punto B.");
        }

    }
}
