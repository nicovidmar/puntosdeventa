package com.cavallaro.kafka.exception;

public class AccreditationServiceException extends RuntimeException {

    private int code;

    public AccreditationServiceException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public AccreditationServiceException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}