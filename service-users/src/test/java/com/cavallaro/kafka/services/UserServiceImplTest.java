package com.cavallaro.kafka.services;

import com.cavallaro.kafka.model.Role;
import com.cavallaro.kafka.model.User;
import com.cavallaro.kafka.repositories.RoleRepository;
import com.cavallaro.kafka.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindById_Found() {
        User user = User.builder().id(1L).username("user1").email( "usuario1@mail.com").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("user1", result.get().getUsername());
        verify(userRepository).findById(1L);
    }

    @Test
    void testFindById_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<User> result = userService.findById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void testFindByUsername() {
        User user =  User.builder().id(2L).username("admin").email( "admin@example.com").build();
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByUsername("admin");

        assertTrue(result.isPresent());
        assertEquals("admin", result.get().getUsername());
    }

    @Test
    void testFindAll() {

        List<User> users = List.of(User.builder().id(1L).username("user1").email( "email@example.com").build());
        when(userRepository.findAll()).thenReturn(users);

        Iterable<User> result = userService.findAll();

        assertEquals(users, result);
    }

    @Test
    void testSave_UserWithAdminRole() {
        User user = User.builder().id(null).username("admin").email( "admin@example.com").build();
        user.setPassword("password");
        user.setAdmin(true); // Se le asignará ROLE_ADMIN además de ROLE_USER

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        Role roleUser =  Role.builder().id(1L).name("ROLE_USER").build();
        Role roleAdmin = Role.builder().id(2L).name("ROLE_ADMIN").build();

        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(roleUser));
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(roleAdmin));

        User userToSave = User.builder().id(1L).username("admin").email( "admin@example.com").build();
        when(userRepository.save(any(User.class))).thenReturn(userToSave);

        User savedUser = userService.save(user);

        assertEquals("admin", savedUser.getUsername());
        verify(passwordEncoder).encode("password");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testUpdate_UserFound() {
        Long userId = 1L;
        User existingUser = User.builder().id(userId).username("oldUsername").email( "old@example.com").build();
        User updateData =  User.builder().id(null).username("newUsername").email( "new@example.com").build();


        updateData.setAdmin(false);
        updateData.setEnabled(false);

        Role roleUser =  Role.builder().id(1L).name("ROLE_USER").build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(roleUser));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<User> result = userService.update(updateData, userId);

        assertTrue(result.isPresent());
        assertEquals("newUsername", result.get().getUsername());
        assertEquals("new@example.com", result.get().getEmail());
        assertFalse(result.get().isEnabled());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testUpdate_UserNotFound() {
        User updateData = User.builder().id(null).username("any").email( "any@mail.com").build();
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<User> result = userService.update(updateData, 1L);

        assertTrue(result.isEmpty());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testDelete() {
        doNothing().when(userRepository).deleteById(1L);

        userService.delete(1L);

        verify(userRepository).deleteById(1L);
    }
}
