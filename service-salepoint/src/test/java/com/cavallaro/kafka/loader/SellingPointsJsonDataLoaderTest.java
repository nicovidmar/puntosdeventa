package com.cavallaro.kafka.loader;


import com.cavallaro.kafka.model.SellingPoint;
import com.cavallaro.kafka.repository.SellingPointsRepository;
import com.cavallaro.kafka.service.SellingPointService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.ApplicationArguments;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class SellingPointsJsonDataLoaderTest {

    @Mock
    private SellingPointsRepository sellingPointsRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ApplicationArguments applicationArguments;

    @Mock
    private SellingPointService sellingPointService;

    private SellingPointsJsonDataLoader sellingPointsJsonDataLoader;

    private static final String JSON_FILE = "selling_points.json";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sellingPointsJsonDataLoader = new SellingPointsJsonDataLoader(sellingPointsRepository, objectMapper,sellingPointService);
    }

    @Test
    void shouldLoadDataIfJsonFileExistsAndDBIsEmpty() throws Exception {
        when(sellingPointsRepository.count()).thenReturn(0L);
        InputStream mockInputStream = getClass().getClassLoader().getResourceAsStream(JSON_FILE);
        List<SellingPoint> mockSellingPoints = Collections.singletonList(new SellingPoint(1, "Punto de Venta Test"));

       // when(objectMapper.readValue(mockInputStream, new TypeReference<List<SellingPoint>>() {}))
        //        .thenReturn(mockSellingPoints);

        when(objectMapper.readValue(any(InputStream.class), any(TypeReference.class)))
                .thenReturn(mockSellingPoints);
       // when(sellingPointsRepository.count()).thenReturn(1L);

        sellingPointsJsonDataLoader.run(applicationArguments);

        verify(sellingPointsRepository, times(1)).saveAll(mockSellingPoints);
    }

    @Test
    void shouldNotLoadDataIfJsonFileNotFound() throws Exception {
        when(sellingPointsRepository.count()).thenReturn(0L);
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("NoExiste.json");
        assertNull(inputStream, "El archivo " + JSON_FILE + " no debería existir en src/test/resources");

        sellingPointsJsonDataLoader.run(applicationArguments);

        verify(sellingPointsRepository, never()).saveAll(anyList());
    }

    @Test
    void shouldNotLoadDataIfDBIsNotEmpty() throws Exception {
        when(sellingPointsRepository.count()).thenReturn(5L);

        sellingPointsJsonDataLoader.run(applicationArguments);

        verify(sellingPointsRepository, never()).saveAll(anyList());
    }
}
