package com.accreditationservice.webclient;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;

import com.accreditationservice.dto.PointOfSaleDTO;

import reactor.core.publisher.Mono;

@Service
public class WebClientPosService {

    private final WebClient webClient;

    public WebClientPosService(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://localhost:8080/pos-service/api/pos").build();
    }

    public PointOfSaleDTO findById(int id, String jwtToken) {
        return webClient.get()
                .uri("/{id}", id)
                .header("Authorization", "Bearer " + jwtToken)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    if (response.statusCode() == HttpStatus.BAD_REQUEST) {
                        return Mono.error(new IllegalArgumentException("No existe PointOfSale con ID " + id));
                    }
                    return Mono.error(new RuntimeException("Error al consultar POS"));
                })
                .bodyToMono(PointOfSaleDTO.class)
                .block(); 
    }
    

    public List<PointOfSaleDTO> findAll() {
        return webClient.get()
                .retrieve()
                .bodyToFlux(PointOfSaleDTO.class)
                .collectList()
                .block();
    }
}
