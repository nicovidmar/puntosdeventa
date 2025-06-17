package com.costservice.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
  info = @Info(
    title = "Cost Service",
    version = "1.0",
    description = "Documentación de Cost Service"
  )
)
public class SwaggerConfig {}