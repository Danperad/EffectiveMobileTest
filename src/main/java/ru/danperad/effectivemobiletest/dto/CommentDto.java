package ru.danperad.effectivemobiletest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.danperad.effectivemobiletest.entity.Comment;
import ru.danperad.effectivemobiletest.entity.User;

@Data
@Builder
@Schema(description = "Комментарий задачи")
public class CommentDto {
    @Schema(description = "Текст комментария", example = "А кто должен купить?")
    private String comment;
    @Schema(description = "Фамилия и имя автора", example = "Иванов Иван")
    private String author;

    public static CommentDto fromComment(@NotNull Comment comment) {
        User author = comment.getAuthor();
        CommentDtoBuilder builder = CommentDto.builder();
        builder.comment(comment.getComment());
        builder.author(author.getLastName() + " " + author.getFirstName());
        return builder.build();
    }
}
