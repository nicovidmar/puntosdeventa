package com.cavallaro.kafka.integracion;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class IntegracionAbstractTest {

    protected   final ObjectMapper objectMapper = new ObjectMapper();

    protected <T> T loadFileMock(String file, TypeReference<T> tipo) throws Exception {
        String json = new String(Files.readAllBytes(Paths.get("src/test/resources/mocks/" + file)));
        return objectMapper.readValue(json, tipo);
    }
}
