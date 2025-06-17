# Sistema de Puntos de Venta

Este proyecto es una arquitectura de microservicios construida con Spring Boot 3.3, diseñada para gestionar puntos de venta y sus costos (que viven en una caché Redis) y acreditaciones (que se persisten en una base de datos relacional); además cuenta con un sistema de usuarios para autenticación y autorización y notificaciones mediante eventos asincrónicos con RabbitMQ.
Todos los servicios se registran en Eureka y se exponen a través de un API Gateway, ambos parte de Spring Cloud.
También implementa trazabilidad con Zipkin y Micrometer.

---

## Microservicios

| Servicio         | Descripción                                                        |
|------------------|--------------------------------------------------------------------|
| **api-gateway**  | Punto de entrada (API Gateway) con seguridad JWT.                 |
| **auth-service** | Registro y login de usuarios. Emite tokens JWT.                   |
| **pos-service**  | Gestión de puntos de venta. Usa Redis como caché.                 |
| **cost-service** | Gestión conexiones y costos entre POS. Usa Redis como caché.      |
| **accreditation-service** | Procesa acreditaciones entre POS.                        |
| **notifier-service** | Envía emails por eventos de registro o acreditación.          |

---

## Tecnologías utilizadas 

- Java 17
- Spring Boot 3.x
- Spring Cloud (Eureka, Gateway)
- RabbitMQ (eventos async)
- Redis (caché POS y Costos)
- Zipkin + Micrometer (tracing)
- Spring Security (JWT)
- Maven

---

## Requisitos previos

- Java 17+
- Maven 3.8+
- RabbitMQ corriendo en `localhost:5672`
- Redis en `localhost:6379`
- Zipkin en `localhost:9411`
- Eureka en `localhost:8761`
(pendiente de preparar en Docker)

---

## Cómo levantar la aplicación

(pendiente de configurar Docker)
Manualmente, habría que levantar un servidor Redis e inicializar Zipkin y Rabbit y correr los servicios en este órden:
- Eureka
- Gateway
- Auth service
- Pos service
- Cost service
- Accreditation service
- Notifier service


## Comunicación entre servicios
Todos los servicios se comunican vía HTTP a través del gateway-service

##  Observabilidad
- Eureka: http://localhost:8761

- RabbitMQ: http://localhost:15672 (usuario: guest / guest)

- Zipkin: http://localhost:9411

## Seguridad
Se usa JWT emitido por auth-service.

El gateway-service valida el token con un filtro JwtAuthWebFilter.

## Testing y calidad
Tests unitarios con JUnit y Mockito.

Cobertura con JaCoCo (mvn test && mvn jacoco:report).

## Estructura del proyecto
pos/
├── api-gateway/
├── auth-service/
├── pos-service/
├── cost-service/
├── accreditation-service/
├── notifier-service/
├── eureka-server/
└── README.md 
