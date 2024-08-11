package ru.danperad.effectivemobiletest.repositories;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.danperad.effectivemobiletest.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = {
        "spring.test.database.replace=none",
        "spring.datasource.url=jdbc:tc:postgresql:16-alpine:///users"
})
@Testcontainers
public class UserRepositoryTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    private UserRepository userRepository;
    private List<User> users;

    @BeforeEach
    public void setUp() {
        users = new ArrayList<>();
        User.UserBuilder builder = User.builder();
        builder.firstName("user").lastName("user").email("user@email.com").password(passwordEncoder.encode("00000000")).role(User.Role.ROLE_USER);
        User user = userRepository.save(builder.build());
        users.add(user);
        builder = User.builder();
        builder.firstName("admin").lastName("admin").email("admin@email.com").password(passwordEncoder.encode("admin000")).role(User.Role.ROLE_ADMIN);
        User admin = userRepository.save(builder.build());
        users.add(admin);
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
    }

    @ParameterizedTest
    @ValueSource(strings = {"user@email.com", "admin@email.com"})
    public void findByEmail(String email) {
        User actualUser = userRepository.findByEmail(email).orElse(null);
        assertNotNull(actualUser);
        User testUser = users.stream().filter(u -> u.getEmail().equals(email)).findFirst().get();
        assertEquals(testUser.getEmail(), actualUser.getEmail());
        assertEquals(testUser.getLastName(), actualUser.getLastName());
    }

    @Test
    public void findByEmailNotFound() {
        Optional<User> actual = userRepository.findByEmail("invalid@email.com");
        assertFalse(actual.isPresent());
    }
}
