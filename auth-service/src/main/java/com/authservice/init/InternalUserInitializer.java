package com.authservice.init;


import com.authservice.entity.User;
import com.authservice.repository.UserRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class InternalUserInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public InternalUserInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void createInternalUser() {
        userRepository.findByUsername("internal@gmail.com").ifPresentOrElse(
            user -> System.out.println("Usuario 'internal' ya existe."),
            () -> {
                User internal = new User();
                internal.setUsername("internal@gmail.com");
                internal.setPassword(passwordEncoder.encode("internalpassword"));
                userRepository.save(internal);
                System.out.println("Usuario 'internal' creado correctamente.");
            }
        );
    }
}
