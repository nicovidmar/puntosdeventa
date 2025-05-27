package com.cavallaro.kafka.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public  class ApiResponse<T> {
    private Header header;
    private T data;

    public ApiResponse() {
    }

    public ApiResponse(Header header, T data) {
        this.header = header;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(new Header(200, "Operación exitosa"), data);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(new Header(code, message), null);
    }
}

