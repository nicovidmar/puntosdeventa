package com.cavallaro.kafka.controller;



import com.cavallaro.kafka.dto.ApiResponse;
import com.cavallaro.kafka.dto.PathNameResult;
import com.cavallaro.kafka.dto.SellingCostDTO;
import com.cavallaro.kafka.model.SellingCostId;
import com.cavallaro.kafka.pathfinder.service.SellingPathService;
import com.cavallaro.kafka.service.SellingCostsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class SellingCostsControllerTest {

    @InjectMocks
    private SellingCostsController sellingCostsController;

    @Mock
    private SellingCostsService sellingCostsService;

    @Mock
    private SellingPathService sellingPathService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddCost() {
        SellingCostDTO costDTO = new SellingCostDTO();
        costDTO.setId(new SellingCostId(1,2));
        costDTO.setCost(100.0);

        ApiResponse<SellingCostDTO> response = ApiResponse.success(costDTO);
        when(sellingCostsService.save(any(SellingCostDTO.class))).thenReturn(response);

        ResponseEntity<ApiResponse<SellingCostDTO>> result = sellingCostsController.addCost(costDTO);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    public void testGetCost() {
        Integer pointA = 1;
        Integer pointB = 2;
        ApiResponse<Double> response = ApiResponse.success(50.0);

        when(sellingCostsService.findCostById(eq(pointA), eq(pointB))).thenReturn(response);

        ResponseEntity<ApiResponse<Double>> result = sellingCostsController.getCost(pointA, pointB);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    public void testDeleteCost() {
        Integer pointA = 1;
        Integer pointB = 2;
        ApiResponse<Boolean> response = ApiResponse.success(true);

        when(sellingCostsService.remove(eq(pointA), eq(pointB))).thenReturn(response);

        ResponseEntity<ApiResponse<Boolean>> result = sellingCostsController.deleteCost(pointA, pointB);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    public void testFindAllCosts() {
        ApiResponse<List<SellingCostDTO>> response = ApiResponse.success(Collections.emptyList());

        when(sellingCostsService.findAll()).thenReturn(response);

        ResponseEntity<ApiResponse<List<SellingCostDTO>>> result = sellingCostsController.findAllCosts();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    public void testGetCheapestPath() {
        Integer pointA = 1;
        Integer pointB = 2;
        List<String> path = new ArrayList<>();
        double totalCost  =0.0;
        PathNameResult pathNameResult = new PathNameResult(path,totalCost);
        ApiResponse<PathNameResult> response = ApiResponse.success(pathNameResult);

        when(sellingPathService.getCheapestPath(any(String.class),eq(pointA), eq(pointB))).thenReturn(pathNameResult);

        ResponseEntity<ApiResponse<PathNameResult>> result = sellingCostsController.getCheapestPath(pointA, pointB);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response.getData(), result.getBody().getData());
    }
}
