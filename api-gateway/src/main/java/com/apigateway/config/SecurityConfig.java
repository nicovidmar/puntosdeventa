package com.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import com.apigateway.entrypoint.CustomAuthenticationEntryPoint;
import com.apigateway.jwt.JwtAuthWebFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthWebFilter jwtAuthWebFilter;
    private final CustomAuthenticationEntryPoint entryPoint;

    public SecurityConfig(JwtAuthWebFilter jwtAuthWebFilter, CustomAuthenticationEntryPoint entryPoint) {
        this.jwtAuthWebFilter = jwtAuthWebFilter;
        this.entryPoint = entryPoint;
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/auth-service/**").permitAll()
                        .anyExchange().authenticated())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(entryPoint))
                .addFilterAt(jwtAuthWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}
