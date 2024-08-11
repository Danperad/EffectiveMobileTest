package ru.danperad.effectivemobiletest.services;

import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.NotSupportedException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.danperad.effectivemobiletest.dto.TaskDto;
import ru.danperad.effectivemobiletest.entity.Comment;
import ru.danperad.effectivemobiletest.entity.Task;
import ru.danperad.effectivemobiletest.entity.User;
import ru.danperad.effectivemobiletest.exceptions.NoAccessException;
import ru.danperad.effectivemobiletest.exceptions.TaskNotFoundException;
import ru.danperad.effectivemobiletest.exceptions.TokenUserNotFoundException;
import ru.danperad.effectivemobiletest.exceptions.UserNotFoundException;
import ru.danperad.effectivemobiletest.repositories.TaskRepository;
import ru.danperad.effectivemobiletest.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public Page<TaskDto> getTasks(Integer authorId, Integer actorId, Task.Priority priority, Task.Status status, Pageable limit) {
        Page<Task> tasks = taskRepository.findAll(((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (actorId != null && authorId != null) {
                try {
                    throw new NotSupportedException();
                } catch (NotSupportedException e) {
                    throw new RuntimeException(e);
                }
            }
            if (authorId != null) {
                predicates.add(criteriaBuilder.equal(root.get("author.id"), authorId));
            }
            if (actorId != null) {
                predicates.add(criteriaBuilder.equal(root.get("actor.id"), actorId));
            }
            if (priority != null) {
                predicates.add(criteriaBuilder.equal(root.get("priority"), priority));
            }
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }), limit);
        return new PageImpl<>(tasks.map(TaskDto::fromTask).toList(), tasks.getPageable(), tasks.getTotalElements());
    }


    public TaskDto saveTask(TaskDto taskDto) throws UserNotFoundException, TokenUserNotFoundException {
        User authorRequest = userService.getCurrentUser();
        Optional<Task> task = taskRepository.findById(taskDto.getId());
        if (task.isPresent()) {
            Task newTask = task.get();
            if (newTask.getAuthor().equals(authorRequest)) {
                updateTaskByAuthor(taskDto, task.get());
            } else if (newTask.getActor().equals(authorRequest)) {
                updateTaskByActor(taskDto, task.get());
            } else {
                throw new NoAccessException();
            }
            newTask = taskRepository.save(newTask);
            return TaskDto.fromTask(newTask);
        }

        Task newTask = new Task(taskDto.getTitle(), taskDto.getDescription(), taskDto.getStatus(), taskDto.getPriority(), authorRequest);
        if (taskDto.getActor() != null) {
            User actor = userRepository.findById(taskDto.getActor().getId()).orElseThrow(UserNotFoundException::new);
            newTask.setActor(actor);
        }
        newTask = taskRepository.save(newTask);
        return TaskDto.fromTask(newTask);
    }

    public TaskDto addCommentToTask(Long taskId, String comment) throws TokenUserNotFoundException {
        User user = userService.getCurrentUser();
        Task task = taskRepository.findById(taskId).orElseThrow(TaskNotFoundException::new);
        Comment.CommentBuilder commentBuilder = Comment.builder().comment(comment).author(user).task(task);
        task.getComments().add(commentBuilder.build());
        return TaskDto.fromTask(taskRepository.save(task));
    }

    private void updateTaskByAuthor(TaskDto taskDto, Task task) {
        if (!task.getTitle().equals(taskDto.getTitle())) {
            task.setTitle(taskDto.getTitle());
        }
        if (!task.getDescription().equals(taskDto.getDescription())) {
            task.setDescription(taskDto.getDescription());
        }
        if (!task.getPriority().equals(taskDto.getPriority())) {
            task.setPriority(taskDto.getPriority());
        }
        if ((task.getActor() == null && taskDto.getActor() != null) || Objects.requireNonNull(task.getActor()).getId() != taskDto.getActor().getId()) {
            User user = userRepository.findById(taskDto.getActor().getId()).orElseThrow(UserNotFoundException::new);
            task.setActor(user);
        }
        updateTaskByActor(taskDto, task);
    }

    private void updateTaskByActor(TaskDto taskDto, Task task) {
        if (!task.getStatus().equals(taskDto.getStatus())) {
            task.setStatus(taskDto.getStatus());
        }
    }

    public void deleteTask(long taskId) {
        User user = userService.getCurrentUser();
        Optional<Task> task = taskRepository.findById(taskId);
        if (task.isEmpty()) {
            return;
        }
        if (task.get().getAuthor().equals(user)) {
            taskRepository.deleteById(taskId);
            return;
        }
        throw new NoAccessException();
    }
}
