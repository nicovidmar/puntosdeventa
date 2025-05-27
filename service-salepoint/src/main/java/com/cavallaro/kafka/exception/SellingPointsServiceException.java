package com.cavallaro.kafka.exception;

public class SellingPointsServiceException extends RuntimeException {

    private int code;

    public SellingPointsServiceException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public SellingPointsServiceException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
