package com.cavallaro.kafka.controllers;

import com.cavallaro.kafka.model.User;
import com.cavallaro.kafka.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userController = new UserController(userService,"Texto de configuración", "8080");
    }

    @Test
    void testCreateUser() {
        User user = User.builder().id(1L).username("usuario1").email( "usuario1@mail.com").build();
        when(userService.save(user)).thenReturn(user);

        ResponseEntity<User> response = userController.createUser(user);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(user, response.getBody());
        verify(userService).save(user);
    }

    @Test
    void testUpdateUser_Found() {
        User user = User.builder().id(1L).username("usuario1").email( "usuario1@mail.com").build();
        when(userService.update(user, 1L)).thenReturn(Optional.of(user));

        ResponseEntity<User> response = userController.updateUser(user, 1L);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(user, response.getBody());
        verify(userService).update(user, 1L);
    }

    @Test
    void testUpdateUser_NotFound() {
        User user = User.builder().id(1L).username("usuario1").email( "usuario1@mail.com").build();
        when(userService.update(user, 1L)).thenReturn(Optional.empty());

        ResponseEntity<User> response = userController.updateUser(user, 1L);

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testGetUserById_Found() {
        User user = User.builder().id(1L).username("usuario1").email( "usuario1@mail.com").build();
        when(userService.findById(1L)).thenReturn(Optional.of(user));

        ResponseEntity<User> response = userController.getUserById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(user, response.getBody());
    }

    @Test
    void testGetUserById_NotFound() {
        when(userService.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<User> response = userController.getUserById(1L);

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testGetUserByUsername_Found() {
        User user = User.builder().id(1L).username("usuario1").email( "usuario1@mail.com").build();
        when(userService.findByUsername("usuario1")).thenReturn(Optional.of(user));

        ResponseEntity<User> response = userController.getUserByUsername("usuario1");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(user, response.getBody());
    }

    @Test
    void testGetUserByUsername_NotFound() {
        when(userService.findByUsername("usuario1")).thenReturn(Optional.empty());

        ResponseEntity<User> response = userController.getUserByUsername("usuario1");

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testGetAllUsers() {
        List<User> users = List.of(User.builder().id(1L).username("usuario1").email( "usuario1@mail.com").build());
        when(userService.findAll()).thenReturn(users);

        ResponseEntity<Iterable<User>> response = userController.getAllUsers();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(users, response.getBody());
    }

    @Test
    void testDeleteUser() {
        doNothing().when(userService).delete(1L);

        ResponseEntity<Void> response = userController.deleteUser(1L);

        assertEquals(204, response.getStatusCodeValue());
        verify(userService).delete(1L);
    }

    @Test
    void testFetchConfigs() {
        ResponseEntity<?> response = userController.fetchConfigs();

        assertEquals(200, response.getStatusCodeValue());
        Map<String, String> expected = new HashMap<>();
        expected.put("text", "Texto de configuración");
        expected.put("port", "8080");

        assertEquals(expected, response.getBody());
    }
}
