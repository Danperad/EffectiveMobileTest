package ru.danperad.effectivemobiletest.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.danperad.effectivemobiletest.dto.AuthResponseDto;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public AuthResponseDto authenticate(String email, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        UserDetails user = userService.userDetailsService().loadUserByUsername(email);
        String jwt = jwtService.generateToken(user);

        return new AuthResponseDto(jwt);
    }

}
