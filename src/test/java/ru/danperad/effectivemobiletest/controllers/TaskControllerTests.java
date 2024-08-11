package ru.danperad.effectivemobiletest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.danperad.effectivemobiletest.dto.TaskDto;
import ru.danperad.effectivemobiletest.dto.UserDto;
import ru.danperad.effectivemobiletest.entity.Task;
import ru.danperad.effectivemobiletest.services.TaskService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = TaskController.class)
@ContextConfiguration(classes = {TaskController.class})
public class TaskControllerTests extends BaseTestsControllers {
    private static Page<TaskDto> tasks;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private TaskService taskService;

    @BeforeAll
    public static void setUp() {
        List<TaskDto> taskList = new ArrayList<>();
        UserDto author = UserDto.builder().id(1).lastName("LastName").firstName("FirstName").build();
        taskList.add(TaskDto.builder().id(1).title("Title").description("Description").author(author).status(Task.Status.IN_PROGRESS).priority(Task.Priority.IMMEDIATELY).build());
        tasks = new PageImpl<>(taskList, PageRequest.of(1, 1), 1);
    }

    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @WithMockUser(username = "user")
    public void findAll() throws Exception {
        when(taskService.getTasks(nullable(Integer.class), nullable(Integer.class), nullable(Task.Priority.class), nullable(Task.Status.class), nullable(Pageable.class))).thenReturn(tasks);
        mvc.perform(MockMvcRequestBuilders
                        .get("/tasks")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].id").value(1));
    }

    @Test
    @WithMockUser
    public void saveTask() throws Exception {
        TaskDto taskToAdd = TaskDto.builder().title("Title").build();
        when(taskService.saveTask(taskToAdd)).thenReturn(TaskDto.builder().id(1).title("Title").build());
        mvc.perform(MockMvcRequestBuilders
                        .post("/tasks")
                        .content(asJsonString(taskToAdd))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1));

    }
}
