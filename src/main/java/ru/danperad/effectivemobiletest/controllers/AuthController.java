package ru.danperad.effectivemobiletest.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.danperad.effectivemobiletest.dto.AuthRequestDto;
import ru.danperad.effectivemobiletest.dto.AuthResponseDto;
import ru.danperad.effectivemobiletest.services.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Авторизация")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь авторизован, выдан токен доступа",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponseDto.class))}),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован из-за неверных данных (почты или пароля)",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Не верно введены данные",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))}),
    })
    @PostMapping("/sign-in")
    public ResponseEntity<AuthResponseDto> signIn(@RequestBody @Valid final AuthRequestDto authRequestDto) {
        return ResponseEntity.ok(authService.authenticate(authRequestDto.getEmail(), authRequestDto.getPassword()));
    }

}
