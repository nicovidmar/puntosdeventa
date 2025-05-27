package com.cavallaro.kafka.controller;

import com.cavallaro.kafka.dto.ApiResponse;
import com.cavallaro.kafka.exception.AccreditationServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private MessageSource messageSource;

/*    @ExceptionHandler(SellingCostsServiceException.class)
    public ResponseEntity<ApiResponse<String>> handleSellingCostsServiceException(SellingCostsServiceException ex) {
        return ResponseEntity.status( HttpStatus.resolve(ex.getCode())).body(ApiResponse.error(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(SellingPointsServiceException.class)
    public ResponseEntity<ApiResponse<String>> handleSellingPointsServiceException(SellingPointsServiceException ex) {
        return ResponseEntity.status( HttpStatus.resolve(ex.getCode())).body(ApiResponse.error(ex.getCode(), ex.getMessage()));
    }
*/
    @ExceptionHandler(AccreditationServiceException.class)
    public ResponseEntity<ApiResponse<String>> handleSellingPathServiceValidator(AccreditationServiceException ex) {
        HttpStatus status = Optional.ofNullable(ex.getCode())
                .map(HttpStatus::resolve)
                .orElse(HttpStatus.INTERNAL_SERVER_ERROR);
        String localizedMessage = messageSource.getMessage(
                "error.selling_point_not_found", // Usar un código consistente
                null,
                LocaleContextHolder.getLocale()
        );
        return ResponseEntity.status( status).body(ApiResponse.error(ex.getCode(), localizedMessage));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error desconocido"));
    }

}
