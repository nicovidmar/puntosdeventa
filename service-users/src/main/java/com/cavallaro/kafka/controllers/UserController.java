package com.cavallaro.kafka.controllers;


import com.cavallaro.kafka.model.User;
import com.cavallaro.kafka.services.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RefreshScope
@RestController
public class UserController {


    private final UserService userService;

    private final String text;

    private final String port;

    public UserController(UserService userService, @Value("${configuracion.text}") String text, @Value("${server.port}") String port) {
        this.userService = userService;
        this.port = port;
        this.text = text;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid  @RequestBody User user) {
        log.info("llamada a un metodo del controller UserController::createUser ");

        log.info("Request Parameter: user {}", user.toString());
        User savedUser = userService.save(user);
        ResponseEntity<User> userResponseEntity = new ResponseEntity<>(savedUser, HttpStatus.CREATED);

        log.info("Response Parameter: {}", userResponseEntity);

        return userResponseEntity;
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user, @Valid @PathVariable Long id) {

        log.info("llamada a un metodo del controller UserController::updateUser ");

        Optional<User> userUpdatedOptional = userService.update(user, id);

        return userUpdatedOptional
        .map(userUpdated -> ResponseEntity.status(HttpStatus.CREATED).body(userUpdated))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@Valid @PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@Valid @PathVariable String username) {
        Optional<User> user = userService.findByUsername(username);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Iterable<User>> getAllUsers() {
        log.info("llamada a un metodo del controller UserController::getAllUsers ");
        log.info("Request Parameter: {}", "NA");
        ResponseEntity<Iterable<User>> responseOk = ResponseEntity.ok(userService.findAll());
        log.info("Response Parameter: {}", responseOk);
        return responseOk;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@Valid @PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/fetch-confis")
    public ResponseEntity<?> fetchConfigs( ){
        Map<String, String> json = new HashMap<>();
        json.put("text",text);
        json.put("port",port);

        return ResponseEntity.ok(json);

    }

}
