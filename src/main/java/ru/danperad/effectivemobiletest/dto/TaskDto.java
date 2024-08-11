package ru.danperad.effectivemobiletest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.danperad.effectivemobiletest.entity.Task;

import java.util.List;

@Data
@Builder
public class TaskDto {
    @Schema(description = "Номер задачи. При добавлении оставлять пустым или указывать 0", example = "1")
    @Min(value = 0, message = "Номер задачи не может быть меньше 0")
    private long id;

    @Schema(description = "Название задачи", example = "Написать тесты")
    @NotBlank(message = "Название задачи не должно быть пустым")
    @Size(min = 1, max = 50, message = "Длинна название должна быть от 1 до 50 символов")
    private String title;

    @Builder.Default
    @Schema(description = "Описание задачи", example = "Описание всех схем валидации, в том числе...")
    @Size(max = 300, message = "Длинна описания должна быть от 0 до 300 символов")
    private String description = "";

    @Schema(description = "Статус задачи", example = "PENDING")
    private Task.Status status;

    @Schema(description = "Приоритет задачи", example = "PENDING")
    private Task.Priority priority;

    @Schema(description = "Автор задачи. При добавлении оставлять пустым, указывается автоматически")
    private UserDto author;

    @Schema(description = "Исполнитель задачи")
    private UserDto actor;

    @Schema(description = "Комментарии задачи")
    private List<CommentDto> comments;

    public static TaskDto fromTask(@NotNull Task task) {
        TaskDtoBuilder builder = TaskDto.builder();
        builder.id(task.getId());
        builder.title(task.getTitle());
        builder.description(task.getDescription());
        builder.status(task.getStatus());
        builder.priority(task.getPriority());
        builder.author(UserDto.fromUser(task.getAuthor()));
        if (task.getActor() != null) {
            builder.actor(UserDto.fromUser(task.getActor()));
        }
        builder.comments(task.getComments().stream().map(CommentDto::fromComment).toList());
        return builder.build();
    }
}
