package ru.danperad.effectivemobiletest.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comment", schema = "public")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @Id
    @Column(name = "comment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Column(name = "comment_text", nullable = false)
    private String comment;

    @ManyToOne(optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "author_user_id", nullable = false)
    private User author;

    public Comment(String comment, User author) {
        this.comment = comment;
        this.author = author;
    }
}
