package ru.danperad.effectivemobiletest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Модель запроса добавления нового комментария. Автором является авторизованный пользователь")
public class NewCommentDto {
    @Schema(description = "Текст комментария", example = "А где покупать?")
    @NotBlank(message = "Текст не должен быть пустым")
    @Size(min = 1, max = 300, message = "Текст комментария должен содержать от 1 до 300 символов")
    private String comment;
}
