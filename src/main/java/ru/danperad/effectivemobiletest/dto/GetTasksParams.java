package ru.danperad.effectivemobiletest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import ru.danperad.effectivemobiletest.entity.Task;

@Data
public class GetTasksParams {
    @Schema(description = "Фильтрация по автору задачи. Id автора", example = "2")
    @Positive(message = "Id автора должно быть больше 0")
    private Integer authorId;

    @Schema(description = "Фильтрация по исполнителю задачи. Id исполнителя", example = "2")
    @Positive(message = "Id исполнителя должно быть больше 0")
    private Integer actorId;

    @Schema(description = "Фильтрация по приоритету задач", example = "IMMEDIATELY")
    private Task.Priority priority;

    @Schema(description = "Фильтрация по статусу задач", example = "PENDING")
    private Task.Status status;
}
