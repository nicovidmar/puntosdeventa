package com.cavallaro.kafka.integracion;


import com.cavallaro.kafka.ServiceSalepointApplication;
import com.cavallaro.kafka.controller.SellingPointsController;
import com.cavallaro.kafka.dto.ApiResponse;
import com.cavallaro.kafka.dto.SellingPointDTO;
import com.cavallaro.kafka.service.SellingPointService;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SellingPointsController.class)
@ContextConfiguration(classes = {ServiceSalepointApplication.class, TestSecurityConfig.class})

public class SellingPointsControllerTest extends IntegracionAbstractTest {

    @MockitoBean
    private SellingPointService service;

    //@MockitoBean
    //private JwtService jwtService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetSellingPoints_Success() throws Exception {

        ApiResponse<List<SellingPointDTO>> respuestaMock =loadFileMock(
                "respuesta_selling_points.json",
                new TypeReference<ApiResponse<List<SellingPointDTO>>>() {}
        );

        when(service.findAll()).thenReturn(respuestaMock);

        this.mockMvc
                .perform(get("/api/v1/selling-points").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(respuestaMock)));

    }

    @Test
    public void testSaveSellingPoints_Success() throws Exception {
        ApiResponse<SellingPointDTO> respuestaMock =loadFileMock(
                "respuesta_save_selling_point.json",
                new TypeReference<ApiResponse<SellingPointDTO>>() {}
        );

        when(service.save(any(SellingPointDTO.class))).thenReturn(respuestaMock);


        SellingPointDTO request = new SellingPointDTO(101, "Punto de Venta 1");
        String requestJson = objectMapper.writeValueAsString(request);

        this.mockMvc
                .perform(post("/api/v1/selling-points").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(respuestaMock)));

    }


    @Test
    public void testUpdateSelling_Success() throws Exception {
        ApiResponse<SellingPointDTO> respuestaMock =loadFileMock(
                "respuesta_update_selling_point.json",
                new TypeReference<ApiResponse<SellingPointDTO>>() {}
        );

        when(service.update(any(Integer.class),any(String.class))).thenReturn(respuestaMock);


        String requestJson = objectMapper.writeValueAsString("Punto de Venta 1");

        this.mockMvc
                .perform(put("/api/v1/selling-points/101").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andDo(print())
                .andExpect(status().isAccepted())
                .andExpect(content().json(objectMapper.writeValueAsString(respuestaMock)));
    }





    @Test
    public void testRemoveSelling_Success() throws Exception {

        ApiResponse<Boolean> respuestaMock =loadFileMock(
                "respuesta_delete_selling_points.json",
                new TypeReference<ApiResponse<Boolean>>() {}
        );

        when(service.remove(any(Integer.class))).thenReturn(respuestaMock);

        this.mockMvc
                .perform(delete("/api/v1/selling-points/100").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isAccepted())
                .andExpect(content().json(objectMapper.writeValueAsString(respuestaMock)));
    }

    @Test
    public void testRemoveSelling_Not_found() throws Exception {

        ApiResponse<Boolean> respuestaMock =loadFileMock(
                "respuesta_delete_selling_points_not_found.json",
                new TypeReference<ApiResponse<Boolean>>() {}
        );

        when(service.remove(any(Integer.class))).thenReturn(respuestaMock);

        this.mockMvc
                .perform(delete("/api/v1/selling-points/100").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().json(objectMapper.writeValueAsString(respuestaMock)));
    }

}
