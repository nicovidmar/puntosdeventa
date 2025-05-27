package com.cavallaro.kafka.controller;


import com.cavallaro.kafka.dto.ApiResponse;
import com.cavallaro.kafka.exception.SellingCostsServiceException;
import com.cavallaro.kafka.exception.SellingPathServiceValidator;
import com.cavallaro.kafka.exception.SellingPointsServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SellingCostsServiceException.class)
    public ResponseEntity<ApiResponse<String>> handleSellingCostsServiceException(SellingCostsServiceException ex) {
        return ResponseEntity.status( HttpStatus.resolve(ex.getCode())).body(ApiResponse.error(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(SellingPointsServiceException.class)
    public ResponseEntity<ApiResponse<String>> handleSellingPointsServiceException(SellingPointsServiceException ex) {
        return ResponseEntity.status( HttpStatus.resolve(ex.getCode())).body(ApiResponse.error(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(SellingPathServiceValidator.class)
    public ResponseEntity<ApiResponse<String>> handleSellingPathServiceValidator(SellingPathServiceValidator ex) {
        return ResponseEntity.status( HttpStatus.resolve(ex.getCode())).body(ApiResponse.error(ex.getCode(), ex.getMessage()));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error desconocido"));
    }

}
