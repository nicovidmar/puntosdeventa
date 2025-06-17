package com.accreditationservice.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
  info = @Info(
    title = "Accreditation Service",
    version = "1.0",
    description = "Documentación de Accreditation Service"
  )
)
public class SwaggerConfig {}