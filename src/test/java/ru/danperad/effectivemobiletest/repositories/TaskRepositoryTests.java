package ru.danperad.effectivemobiletest.repositories;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.danperad.effectivemobiletest.entity.Comment;
import ru.danperad.effectivemobiletest.entity.Task;
import ru.danperad.effectivemobiletest.entity.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = {
        "spring.test.database.replace=none",
        "spring.datasource.url=jdbc:tc:postgresql:16-alpine:///tasks"
})
@Testcontainers
public class TaskRepositoryTests {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    private List<User> users;

    private List<Task> tasks;

    @BeforeEach
    public void setUp() {
        users = new ArrayList<>();
        prepareUsers();
        tasks = new ArrayList<>();
        prepareTasks();
    }

    private void prepareUsers() {
        User.UserBuilder builder = User.builder();
        builder.firstName("user").lastName("user").email("user@email.com").password(passwordEncoder.encode("00000000")).role(User.Role.ROLE_USER);
        User user = userRepository.save(builder.build());
        users.add(user);
        builder = User.builder();
        builder.firstName("admin").lastName("admin").email("admin@email.com").password(passwordEncoder.encode("admin000")).role(User.Role.ROLE_ADMIN);
        User admin = userRepository.save(builder.build());
        users.add(admin);
    }

    private void prepareTasks() {
        Task.TaskBuilder builder = Task.builder();
        Comment.CommentBuilder builder1 = Comment.builder();
        builder.author(users.getFirst()).title("Test").description("Test").status(Task.Status.PENDING).priority(Task.Priority.HIGH);
        Task task = taskRepository.save(builder.build());
        builder1.author(users.getLast()).comment("Test").task(task);
        Comment comment = builder1.build();
        task.getComments().add(comment);
        tasks.add(task);
        taskRepository.save(task);
    }

    @AfterEach
    public void tearDown() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void findAllTasks() {
        List<Task> actual = taskRepository.findAll();
        assertFalse(actual.isEmpty());
    }

    @Test
    void getCommentInTasks() {
        Task actual = taskRepository.findById(tasks.getFirst().getId()).orElse(null);
        assertNotNull(actual);
        assertFalse(actual.getComments().isEmpty());
        assertNotEquals(0, actual.getComments().getFirst().getId());
        assertNotNull(actual.getComments().getFirst().getComment());
        assertEquals(tasks.getFirst().getComments().getFirst().getComment(), actual.getComments().getFirst().getComment());
    }
}
