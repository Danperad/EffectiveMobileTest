package ru.danperad.effectivemobiletest.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.danperad.effectivemobiletest.dto.NewUserDto;
import ru.danperad.effectivemobiletest.dto.UserDto;
import ru.danperad.effectivemobiletest.entity.User;
import ru.danperad.effectivemobiletest.exceptions.AuthUserNotFoundException;
import ru.danperad.effectivemobiletest.exceptions.TokenUserNotFoundException;
import ru.danperad.effectivemobiletest.repositories.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class UserServiceTests {
    @Mock
    private UserRepository userRepository;

    @Test
    @WithMockUser(username = "test@test.com")
    public void getCurrentUser() {
        String email = "test@test.com";
        User user = User.builder().id(1).email(email).firstName("test").lastName("test").role(User.Role.ROLE_USER).password("1").build();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        UserService userService = new UserService(userRepository, new BCryptPasswordEncoder());
        assertEquals(user, userService.getCurrentUser());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    public void tokenUserNotFound() {
        when(userRepository.findByEmail("test@test.com")).thenThrow(AuthUserNotFoundException.class);
        UserService userService = new UserService(userRepository, new BCryptPasswordEncoder());
        assertThrows(TokenUserNotFoundException.class, userService::getCurrentUser);
    }

    @Test
    public void addNewUser() {
        NewUserDto newUserDto = new NewUserDto(0, "Test", "Test", "test@test.com", "11111111", User.Role.ROLE_USER);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User user = User.fromNewUserDto(newUserDto).toBuilder().id(5).password(passwordEncoder.encode(newUserDto.getPassword())).build();
        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
        UserService userService = new UserService(userRepository, passwordEncoder);
        UserDto actualUser = userService.addNewUser(newUserDto);
        assertEquals(5, actualUser.getId());
        assertEquals("Test", actualUser.getFirstName());
    }

    @Test
    public void addUserWithExistsEmail() {
        NewUserDto newUserDto = new NewUserDto(0, "Test", "Test", "test@test.com", "11111111", User.Role.ROLE_USER);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        when(userRepository.save(Mockito.any(User.class))).thenThrow(new DataIntegrityViolationException("Error [user_email_key]"));
        UserService userService = new UserService(userRepository, passwordEncoder);
        assertThrows(DuplicateKeyException.class, () -> userService.addNewUser(newUserDto));
    }
}
