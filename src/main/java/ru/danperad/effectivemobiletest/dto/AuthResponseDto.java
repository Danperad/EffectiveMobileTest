package ru.danperad.effectivemobiletest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Schema(description = "Ответ на запрос авторизации")
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {
    @Schema(description = "Токен доступа", example = "eyJhbGciO234Asf42da.eyJzdWIiOiJhZG1pbdg7Ket4cCI6MTYyMjUwNj...")
    private String token;
}
