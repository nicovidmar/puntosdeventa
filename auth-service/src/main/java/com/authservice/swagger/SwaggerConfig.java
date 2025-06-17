package com.authservice.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
  info = @Info(
    title = "Auth Service",
    version = "1.0",
    description = "Documentación de Auth Service"
  )
)
public class SwaggerConfig {}