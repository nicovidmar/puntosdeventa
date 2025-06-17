package com.authservice;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.authservice.dto.AuthRequest;
import com.authservice.entity.User;
import com.authservice.repository.UserRepository;
import com.authservice.security.JwtUtil;
import com.authservice.service.AuthService;
import com.authservice.service.CustomUserDetailsService;

class AuthServiceTests {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthService(authenticationManager, jwtUtil, customUserDetailsService,
                userRepository, passwordEncoder, rabbitTemplate);
    }

    @Test
    void testRegister_OK() {
        AuthRequest request = new AuthRequest("internal@gmail.com", "internalpassword");

        when(userRepository.findByUsername("internal@gmail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("internalpassword")).thenReturn("encodedPassword");

        authService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals("internal@gmail.com", userCaptor.getValue().getUsername());
        assertEquals("encodedPassword", userCaptor.getValue().getPassword());

        // verify(rabbitTemplate).convertAndSend(eq("registration.queue"), any());
    }

    @Test
    void testRegister_UserAlreadyExists() {
        AuthRequest request = new AuthRequest("internal@gmail.com", "internalpassword");

        when(userRepository.findByUsername("internal@gmail.com"))
                .thenReturn(Optional.of(new User()));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.register(request);
        });

        assertEquals("El usuario ya existe", exception.getMessage());
        // verify(userRepository, never()).save(any());
        // verify(rabbitTemplate, never()).convertAndSend(any(), any());
    }

    @Test
    void testLogin_OK() {
        AuthRequest request = new AuthRequest("internal@gmail.com", "internalpassword");

        UserDetails userDetails = mock(UserDetails.class);

        when(customUserDetailsService.loadUserByUsername("internal@gmail.com")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("jwt-token");

        String token = authService.login(request);

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("internal@gmail.com", "internalpassword"));
        verify(customUserDetailsService).loadUserByUsername("internal@gmail.com");
        verify(jwtUtil).generateToken(userDetails);
        assertEquals("jwt-token", token);
    }

    @Test
    void testLogin_UserNotExist() {
        AuthRequest request = new AuthRequest("asd", "asd");

        when(customUserDetailsService.loadUserByUsername("asd"))
                .thenThrow(new UsernameNotFoundException("Usuario no encontrado: asd"));

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            authService.login(request);
        });

        assertEquals("Usuario no encontrado: asd", exception.getMessage());
    }
}
