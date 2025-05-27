package com.cavallaro.kafka.loader;


import com.cavallaro.kafka.model.SellingCost;
import com.cavallaro.kafka.repository.SellingCostsRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.ApplicationArguments;

import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CostsJsonDataLoaderTest {

    private CostsJsonDataLoader costsJsonDataLoader;

    @Mock
    private SellingCostsRepository sellingCostsRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ApplicationArguments applicationArguments;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        costsJsonDataLoader = new CostsJsonDataLoader(sellingCostsRepository, objectMapper);
    }

    @Test
    void shouldNotLoadDataIfJsonFileNotFound() throws Exception {
        // 1. Simular que la BD está vacía
        when(sellingCostsRepository.count()).thenReturn(0L);

        // 2. No hace falta mockear getResourceAsStream(), simplemente no coloques el archivo
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("costs2.json");
        assertNull(inputStream, "El archivo costs.json existe en src/test/resources, pero no debería.");

        // 3. Ejecutar el método
        costsJsonDataLoader.run(applicationArguments);

        // 4. Verificar que NO se llamó a saveAll() porque el JSON no existe
        verify(sellingCostsRepository, never()).saveAll(anyList());
    }

    @Test
    void shouldNotLoadDataIfAlreadyExists() throws Exception {
        when(sellingCostsRepository.count()).thenReturn(10L);

        costsJsonDataLoader.run(applicationArguments);

        verify(sellingCostsRepository, never()).saveAll(anyList());
    }

    @Test
    void shouldLoadDataWhenRepositoryIsEmpty() throws Exception {
        // 1. Simular que la BD está vacía
        when(sellingCostsRepository.count()).thenReturn(0L);

        // 2. Leer el archivo real desde resources
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("costs.json");
        assertNotNull(inputStream, "El archivo costs.json no está en src/test/resources");

        // 3. Simular que el ObjectMapper convierte los datos correctamente
        List<SellingCost> mockSellingCosts = List.of(new SellingCost());
        when(objectMapper.readValue(any(InputStream.class), any(TypeReference.class)))
                .thenReturn(mockSellingCosts);

        // 4. Ejecutar el método
        costsJsonDataLoader.run(applicationArguments);

        // 5. Verificar que los datos fueron guardados en la BD
        verify(sellingCostsRepository, times(1)).saveAll(mockSellingCosts);


    }
}
