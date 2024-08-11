package ru.danperad.effectivemobiletest.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

@Entity
@DynamicUpdate
@Table(name = "task", schema = "public")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    @Column(name = "task_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Setter
    @Column(name = "title", nullable = false)
    private String title;

    @NotNull
    @Setter
    @Column(name = "description", nullable = false)
    private String description;

    @NotNull
    @Setter
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @NotNull
    @Setter
    @Column(name = "priority", nullable = false)
    @Enumerated(EnumType.STRING)
    private Priority priority;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "author_user_id", nullable = false)
    @Enumerated(EnumType.STRING)
    private User author;

    @Setter
    @ManyToOne
    @JoinColumn(name = "actor_user_id")
    private User actor;

    @Builder.Default
    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "comment")
    private List<Comment> comments = new ArrayList<>();

    public Task(String title, String description, Status status, Priority priority, User author) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.author = author;
    }

    public enum Status {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED,
    }

    public enum Priority {
        IMMEDIATELY,
        HIGH,
        MEDIUM,
        LOW,
    }
}
