package com.costservice.webclient;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

import com.costservice.dto.PointOfSaleDTO;

import reactor.core.publisher.Mono;

@Service
public class WebClientPosService {

    private final WebClient posClient;
    private final WebClient authClient;
    private final String jwtToken;

    public WebClientPosService(WebClient.Builder builder) {
        this.authClient = builder.baseUrl("http://localhost:8080/auth-service").build();
        this.posClient = builder.baseUrl("http://localhost:8080/pos-service/api/pos").build();
        this.jwtToken = authenticateAndGetToken("internal@gmail.com", "internalpassword");
    }

    // Constructor para tests con MockWebServer
   /*  public WebClientPosService(WebClient.Builder builder, String authUrl, String posUrl) {
        this.authClient = builder.baseUrl(authUrl).build();
        this.posClient = builder.baseUrl(posUrl).build();
        this.jwtToken = authenticateAndGetToken("internal@gmail.com", "internalpassword");
    } */

    private String authenticateAndGetToken(String username, String password) {
        Map<String, String> credentials = Map.of("username", username, "password", password);

        Map<String, String> response = authClient.post()
                .uri("/auth/login")
                .bodyValue(credentials)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {
                })
                .block();

        return response.get("token");
    }

    public PointOfSaleDTO findById(int id) {
        try {
            return posClient.get()
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
        } catch (Exception ex) {
            if (ex instanceof IllegalArgumentException) {
                throw (IllegalArgumentException) ex;
            }
            throw new RuntimeException("Fallo al consultar POS con ID " + id, ex);
        }
    }
}
