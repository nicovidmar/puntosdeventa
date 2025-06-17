/* package com.costservice;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import com.costservice.dto.PointOfSaleDTO;
import com.costservice.webclient.WebClientPosService;

import static org.junit.jupiter.api.Assertions.*;

public class WebClientTest {

    public static MockWebServer authServer;
    public static MockWebServer posServer;

    @BeforeAll
    public static void setUp() throws IOException {
        authServer = new MockWebServer();
        authServer.start();

        posServer = new MockWebServer();
        posServer.start();
    }

    @AfterAll
    public static void shutdown() throws IOException {
        authServer.shutdown();
        posServer.shutdown();
    }

    @Test
    public void testFindById_OK() {
        // Simular login
        authServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"token\": \"token\"}")
                .addHeader("Content-Type", "application/json"));

        // Simular respuesta de POS
        posServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"id\":1,\"name\":\"POS 1\"}")
                .addHeader("Content-Type", "application/json"));

        WebClient.Builder builder = WebClient.builder();
        String authUrl = authServer.url("/auth-service").toString();
        String posUrl = posServer.url("/pos-service/api/pos").toString();

        WebClientPosService service = new WebClientPosService(builder, authUrl, posUrl);
        PointOfSaleDTO dto = service.findById(1);

        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals("POS 1", dto.getName());
    }

    @Test
    void testFindById_BadRequest() {
        authServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"token\":\"token\"}")
                .addHeader("Content-Type", "application/json"));

        posServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody("Bad Request"));

        WebClientPosService service = new WebClientPosService(
                WebClient.builder(),
                authServer.url("/auth-service").toString(),
                posServer.url("/pos-service/api/pos").toString());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.findById(1));
        assertTrue(ex.getMessage().contains("No existe PointOfSale"));
    }

    @Test
    void testFindById_NotFound() {
        authServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"token\":\"token\"}")
                .addHeader("Content-Type", "application/json"));

        posServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("Not Found"));

        WebClientPosService service = new WebClientPosService(
                WebClient.builder(),
                authServer.url("/auth-service").toString(),
                posServer.url("/pos-service/api/pos").toString());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> service.findById(99));
        assertTrue(ex.getMessage().contains("Fallo al consultar POS"));
    }

}
 */