package ru.danperad.effectivemobiletest.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.danperad.effectivemobiletest.dto.GetTasksParams;
import ru.danperad.effectivemobiletest.dto.NewCommentDto;
import ru.danperad.effectivemobiletest.dto.TaskDto;
import ru.danperad.effectivemobiletest.services.TaskService;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @GetMapping
    @Operation(summary = "Получение задач")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задачи с указанными фильтрами",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))}),
            @ApiResponse(responseCode = "403", description = "Пользователь не авторизован",
                    content = {@Content(mediaType = "null")}),
            @ApiResponse(responseCode = "400", description = "Не верно введены данные фильтрации",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))}),
    })
    public ResponseEntity<Page<TaskDto>> getTasks(
            @ParameterObject @Valid GetTasksParams params,
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(taskService.getTasks(params.getAuthorId(), params.getActorId(), params.getPriority(), params.getStatus(), pageable));
    }

    @PostMapping
    @Operation(summary = "Добавление и обновление задач")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задача успешно обновлена",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TaskDto.class))}),
            @ApiResponse(responseCode = "403", description = "Пользователь не авторизован",
                    content = {@Content(mediaType = "null")}),
            @ApiResponse(responseCode = "400", description = "Не верно введены данные фильтрации",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))}),
    })
    public ResponseEntity<TaskDto> createTask(@RequestBody @Valid TaskDto taskDto) {
        return ResponseEntity.ok(taskService.saveTask(taskDto));
    }

    @PutMapping("/{taskId}")
    @Operation(summary = "Добавление комментария к задаче")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Комментарий успешно добавлен",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TaskDto.class))}),
            @ApiResponse(responseCode = "403", description = "Пользователь не авторизован",
                    content = {@Content(mediaType = "null")}),
            @ApiResponse(responseCode = "400", description = "Не верно введены данные",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))}),
    })
    public ResponseEntity<TaskDto> addCommentToTask(
            @PathVariable @Schema(description = "Номер задачи", example = "1") @Min(value = 1, message = "Номер задачи не может быть меньше 1") Long taskId,
            @RequestBody @Valid NewCommentDto commentDto) {
        return ResponseEntity.ok(taskService.addCommentToTask(taskId, commentDto.getComment()));
    }

    @DeleteMapping("/{taskId}")
    @Operation(summary = "Удаление задачи")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задача успешно удалена",
                    content = {@Content(mediaType = "application/text", schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "403", description = "Пользователь не авторизован или не имеет доступа к удалению",
                    content = {@Content(mediaType = "null")}),
            @ApiResponse(responseCode = "400", description = "Не верно введены данные",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))}),
    })
    public ResponseEntity<String> deleteTask(
            @RequestParam @PathVariable @Schema(description = "Номер задачи", example = "1") @Min(value = 1, message = "Номер задачи не может быть меньше 1") Long taskId
    ) {
        taskService.deleteTask(taskId);
        return ResponseEntity.ok("OK");
    }
}
