package com.vito.kafka.oauth.services;

import com.vito.kafka.oauth.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {


    private final WebClient client;

    public UserService(WebClient client) {
        this.client = client;
    }

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        logger.info("llamada a un metodo del service UserService::loadUserByUsername");
        logger.info("Request Parameter: {}", username);

        Map<String, String> params = new HashMap<>();
        params.put("username",username);
        try{
            User user = client.get().uri("/username/{username}",params)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(User.class)
                    .block();

            List<GrantedAuthority> roles = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName()))
                    .collect(Collectors.toList());

            org.springframework.security.core.userdetails.User user1 = new org.springframework.security.core.userdetails.User(user.getUsername(),
                    user.getPassword(),
                    user.isEnabled(), true, true, true, roles);

            logger.info("Response Parameter: {}", user1);

            return user1;

        }catch (WebClientResponseException  e){
            logger.error("Error de autenticación al intentar loguear al usuario '{}'. Código de estado: {}, Cuerpo de la respuesta: {}", username, e.getStatusCode(), e.getResponseBodyAsString());
            throw new UsernameNotFoundException("Error en el login, no existe el users '" + username + "' en el sistema");
        }

    }
}
