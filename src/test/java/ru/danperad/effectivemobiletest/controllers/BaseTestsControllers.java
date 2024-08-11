package ru.danperad.effectivemobiletest.controllers;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import ru.danperad.effectivemobiletest.configuration.AppConfig;
import ru.danperad.effectivemobiletest.configuration.JwtAuthenticationFilter;
import ru.danperad.effectivemobiletest.configuration.SecurityConfig;
import ru.danperad.effectivemobiletest.repositories.UserRepository;
import ru.danperad.effectivemobiletest.services.JwtService;
import ru.danperad.effectivemobiletest.services.UserService;

@ContextConfiguration(classes = {SecurityConfig.class, JwtAuthenticationFilter.class, AppConfig.class, UserService.class, RestResponseEntityExceptionHandler.class})
public abstract class BaseTestsControllers {
    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtService jwtService;
}
