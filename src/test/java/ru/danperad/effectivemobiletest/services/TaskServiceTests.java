package ru.danperad.effectivemobiletest.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.danperad.effectivemobiletest.dto.TaskDto;
import ru.danperad.effectivemobiletest.dto.UserDto;
import ru.danperad.effectivemobiletest.entity.Task;
import ru.danperad.effectivemobiletest.entity.User;
import ru.danperad.effectivemobiletest.repositories.TaskRepository;
import ru.danperad.effectivemobiletest.repositories.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class TaskServiceTests {
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    private User authUser;

    @BeforeEach
    public void setUp() {
        authUser = User.builder().id(1).email("test@test.com").build();
        when(userService.getCurrentUser()).thenReturn(authUser);
    }

    @Test
    public void saveNewTaskWithoutActorTest() {
        TaskDto taskDto = TaskDto.builder().id(0).title("Task Title").description("Task Description").status(Task.Status.PENDING).priority(Task.Priority.HIGH).build();
        Task returnTask = Task.builder().id(1).title(taskDto.getTitle()).description(taskDto.getDescription()).status(taskDto.getStatus()).priority(taskDto.getPriority()).author(authUser).build();
        when(taskRepository.findById(taskDto.getId())).thenReturn(Optional.empty());
        when(taskRepository.save(Mockito.any(Task.class))).thenReturn(returnTask);

        TaskService taskService = new TaskService(taskRepository, userRepository, userService);
        TaskDto actual = taskService.saveTask(taskDto);

        assertNotNull(actual);
        assertEquals(1, actual.getId());
        assertEquals("Task Title", actual.getTitle());
        assertEquals("Task Description", actual.getDescription());
        assertEquals(Task.Status.PENDING, actual.getStatus());
        assertEquals(Task.Priority.HIGH, actual.getPriority());
        assertEquals(authUser.getId(), actual.getAuthor().getId());
    }

    @Test
    public void saveNewTaskWithActorTest() {
        User actor = User.builder().id(2).email("test@test2.com").lastName("test").firstName("test").build();
        TaskDto taskDto = TaskDto.builder().id(0).title("Task Title").description("Task Description").status(Task.Status.PENDING).priority(Task.Priority.HIGH).actor(UserDto.fromUser(actor)).build();
        Task returnTask = Task.builder().id(1).title(taskDto.getTitle()).description(taskDto.getDescription()).status(taskDto.getStatus()).priority(taskDto.getPriority()).author(authUser).actor(actor).build();
        when(taskRepository.findById(taskDto.getId())).thenReturn(Optional.empty());
        when(taskRepository.save(Mockito.any(Task.class))).thenReturn(returnTask);
        when(userRepository.findById(actor.getId())).thenReturn(Optional.of(actor));

        TaskService taskService = new TaskService(taskRepository, userRepository, userService);
        TaskDto actual = taskService.saveTask(taskDto);

        assertNotNull(actual);
        assertEquals(1, actual.getId());
        assertEquals("Task Title", actual.getTitle());
        assertEquals("Task Description", actual.getDescription());
        assertEquals(Task.Status.PENDING, actual.getStatus());
        assertEquals(Task.Priority.HIGH, actual.getPriority());
        assertEquals(authUser.getId(), actual.getAuthor().getId());
        assertEquals(actor.getId(), actual.getActor().getId());
    }
}
