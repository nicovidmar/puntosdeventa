# api-gateway

Este servicio actúa como puerta de entrada al sistema. Redirige las peticiones a los microservicios registrados en Eureka y valida tokens JWT para seguridad.

Se usa un filtro JwtAuthWebFilter para validar tokens JWT antes de reenviar la petición.

## Tecnologías
- Spring Boot 3.3.1
- Spring Security + JWT
- Spring Cloud Eureka & Gateway
- Zipkin + Micrometer