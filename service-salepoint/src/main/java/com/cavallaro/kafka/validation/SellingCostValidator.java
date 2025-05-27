package com.cavallaro.kafka.validation;

public interface SellingCostValidator {
    void validate(Integer pointA, Integer pointB, Double cost);
    void validate(Integer point);
}
