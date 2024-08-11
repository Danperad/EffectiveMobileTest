package ru.danperad.effectivemobiletest.services;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.danperad.effectivemobiletest.dto.NewUserDto;
import ru.danperad.effectivemobiletest.dto.UserDto;
import ru.danperad.effectivemobiletest.entity.User;
import ru.danperad.effectivemobiletest.exceptions.AuthUserNotFoundException;
import ru.danperad.effectivemobiletest.exceptions.DuplicateUniqueKeyException;
import ru.danperad.effectivemobiletest.exceptions.TokenUserNotFoundException;
import ru.danperad.effectivemobiletest.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private User getUserByEmail(String email) throws AuthUserNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(AuthUserNotFoundException::new);
    }

    public UserDetailsService userDetailsService() {
        return this::getUserByEmail;
    }

    public User getCurrentUser() throws TokenUserNotFoundException {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            return getUserByEmail(user);
        } catch (AuthUserNotFoundException e) {
            throw new TokenUserNotFoundException();
        }
    }

    public UserDto addNewUser(NewUserDto newUserDto) {
        User user = User.fromNewUserDto(newUserDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        try {
            user = userRepository.save(user);
            return UserDto.fromUser(user);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("user_email_key")) {
                throw new DuplicateUniqueKeyException();
            }
            throw e;
        }
    }
}
