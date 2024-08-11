package ru.danperad.effectivemobiletest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.danperad.effectivemobiletest.entity.User;

@Data
@Builder
@Schema(description = "Пользователь")
public class UserDto {
    @Schema(description = "Id пользователя", example = "1")
    @Min(value = 0, message = "Id пользователя не может быть отрицательным")
    protected Integer id;

    @Schema(description = "Фамилия", example = "Иванов")
    @Size(min = 2, max = 50, message = "Фамилия должна содержать от 2 до 50 символов")
    protected String lastName;

    @Schema(description = "Имя", example = "Иван")
    @Size(min = 2, max = 50, message = "Имя должно содержать от 2 до 50 символов")
    protected String firstName;

    public static UserDto fromUser(@NotNull User user) {
        UserDtoBuilder builder = UserDto.builder();
        builder.id(user.getId());
        builder.lastName(user.getLastName());
        builder.firstName(user.getFirstName());
        return builder.build();
    }
}
