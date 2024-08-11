package ru.danperad.effectivemobiletest.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.danperad.effectivemobiletest.dto.AuthResponseDto;
import ru.danperad.effectivemobiletest.services.AuthService;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(controllers = {AuthController.class})
@ContextConfiguration(classes = {AuthController.class})
public class AuthControllerTests extends BaseTestsControllers {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private AuthService authService;

    @Test
    public void loginTest() throws Exception {
        when(authService.authenticate("test@test.com", "testtest")).thenReturn(new AuthResponseDto("tttttttt"));
        mvc.perform(MockMvcRequestBuilders
                        .post("/auth/sign-in")
                        .content("{\"email\":\"test@test.com\",\"password\":\"testtest\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").isString());
    }

    @Test
    public void badCredentialsLoginTest() throws Exception {
        when(authService.authenticate("test@test.com", "testtest")).thenThrow(BadCredentialsException.class);
        mvc.perform(MockMvcRequestBuilders
                        .post("/auth/sign-in")
                        .content("{\"email\":\"test@test.com\",\"password\":\"testtest\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Неверная почта или пароль"));
    }

    @Test
    public void incorrectPasswordFieldTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post("/auth/sign-in")
                        .with(csrf())
                        .content("{\"email\":\"test@test.com\",\"password\":\"a\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail").value("Длина пароля должна быть от 8 до 255 символов"));
    }

    @Test
    public void withoutPasswordFieldTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post("/auth/sign-in")
                        .content("{\"email\":\"test@test.com\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail").value("Пароль не может быть пустыми"));
    }

    @Test
    public void incorrectEmailFormatFieldTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post("/auth/sign-in")
                        .content("{\"email\":\"tttt.c\",\"password\":\"00000000\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail").value("Email адрес должен быть в формате user@example.com"));
    }

    @Test
    public void withoutEmailFieldTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post("/auth/sign-in")
                        .content("{\"password\":\"00000000\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail").value("Адрес электронной почты не может быть пустыми"));
    }
}
