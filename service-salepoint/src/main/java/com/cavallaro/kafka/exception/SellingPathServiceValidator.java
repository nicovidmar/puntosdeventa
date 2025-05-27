package com.cavallaro.kafka.exception;

public class SellingPathServiceValidator extends RuntimeException {

    private int code;

    public SellingPathServiceValidator(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public SellingPathServiceValidator(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
