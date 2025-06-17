package com.authservice.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.authservice.dto.AuthRequest;
import com.authservice.dto.ErrorResponse;
import com.authservice.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Registrar usuario", responses = {
            @ApiResponse(responseCode = "200", description = "Usuario registrado"),
            @ApiResponse(responseCode = "400", description = "Petición inválida. Posibles mensajes:\n- El usuario ya existe\n- El email no puede estar vacío\n- El email debe tener un formato válido\n- Contraseña no puede estar vacía", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody AuthRequest request) {
        try {
            authService.register(request);
            logger.info("Registrando nuevo usuario: {}", request.getUsername());
            return ResponseEntity.ok("Usuario registrado con éxito");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Logearse", responses = {
            @ApiResponse(responseCode = "200", description = "Login exitoso", content = @Content(mediaType = "application/json", schema = @Schema(type = "object", example = "{ \"token\": \"123456789123456789123456789...\" }"))),
            @ApiResponse(responseCode = "403", description = "Usuario no existe", content = @Content()),
            @ApiResponse(responseCode = "400", description = "Petición inválida. Posibles mensajes:\n- El email no puede estar vacío\n- El email debe tener un formato válido\n- Contraseña no puede estar vacía", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody AuthRequest request) {
        String token = authService.login(request);
        logger.info("Ingresando con usuario: {}", request.getUsername());
        return ResponseEntity.ok(Map.of("token", token));
    }
}
