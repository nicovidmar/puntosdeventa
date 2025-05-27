package com.cavallaro.kafka.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.util.Set;

@Data
@AllArgsConstructor
public class RegistroRequest {
    @NonNull
    private String email;

    @NonNull
    private String password;

    private Set<String> roles;
}
