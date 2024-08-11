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
import ru.danperad.effectivemobiletest.dto.NewUserDto;
import ru.danperad.effectivemobiletest.dto.UserDto;
import ru.danperad.effectivemobiletest.services.UserService;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @Operation(summary = "Добавление нового пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно добавлен",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))}),
            @ApiResponse(responseCode = "403", description = "Пользователь не авторизован или недостаточно прав доступа",
                    content = {@Content(mediaType = "null")}),
            @ApiResponse(responseCode = "400", description = "Не верно введены данные",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))}),
    })
    public ResponseEntity<UserDto> addUser(@RequestBody @Valid NewUserDto newUserDto) {
        UserDto userDto = userService.addNewUser(newUserDto);
        return ResponseEntity.ok(userDto);
    }
}
