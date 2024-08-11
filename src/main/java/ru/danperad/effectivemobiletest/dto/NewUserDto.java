package ru.danperad.effectivemobiletest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import ru.danperad.effectivemobiletest.entity.User;

@Getter
@Schema(description = "Модель запроса добавления нового пользователя")
public class NewUserDto extends UserDto {
    @Schema(description = "Почта пользователя", example = "user@example.com")
    @Size(min = 5, max = 255, message = "Адрес электронной почты должен содержать от 5 до 255 символов")
    @NotBlank(message = "Адрес электронной почты не может быть пустыми")
    @Email(message = "Email адрес должен быть в формате user@example.com")
    protected String email;

    @Schema(description = "Пароль", example = "my_str0ng_passw0rd")
    @NotBlank(message = "Пароль не может быть пустыми")
    @Size(min = 8, max = 255, message = "Длина пароля должна быть от 8 до 255 символов")
    protected String password;

    @Schema(description = "Роль пользователя", example = "ROLE_USER")
    @NotNull(message = "Роль должна быть указана")
    protected User.Role role;

    public NewUserDto(int id, String lastName, String firstName, String email, String password, User.Role role) {
        super(id, lastName, firstName);
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
