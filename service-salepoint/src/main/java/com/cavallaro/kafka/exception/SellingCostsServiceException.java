package com.cavallaro.kafka.exception;

public class SellingCostsServiceException extends RuntimeException {

    private int code;

    public SellingCostsServiceException(int code ,String message, Throwable cause) {

        super(message, cause) ;
        this.code = code;
    }

    public SellingCostsServiceException(int code,String message) {

        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }



}
