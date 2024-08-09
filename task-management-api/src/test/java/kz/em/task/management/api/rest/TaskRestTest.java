package kz.em.task.management.api.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kz.em.task.management.api.entity.CommentEntity;
import kz.em.task.management.api.entity.TaskEntity;
import kz.em.task.management.api.entity.UserEntity;
import kz.em.task.management.api.enums.TaskPriority;
import kz.em.task.management.api.enums.TaskStatus;
import kz.em.task.management.api.repository.CommentRepository;
import kz.em.task.management.api.repository.RefreshTokenRepository;
import kz.em.task.management.api.repository.TaskRepository;
import kz.em.task.management.api.repository.UserRepository;
import kz.em.task.management.client.dto.DataDto;
import kz.em.task.management.client.dto.IdDto;
import kz.em.task.management.client.dto.PagedDataDto;
import kz.em.task.management.client.dto.TaskDto;
import kz.em.task.management.client.dto.request.AuthRequest;
import kz.em.task.management.client.dto.request.CreateTaskRequest;
import kz.em.task.management.client.dto.response.AuthResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaskRestTest {
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private String accessToken;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
        this.accessToken = "Bearer " + getAccessToken();
    }

    @AfterEach
    void tearDown() {
        refreshTokenRepository.deleteAll();
        commentRepository.deleteAll();
        taskRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void createTaskTest() throws Exception {
        UserEntity assignee = new UserEntity();
        assignee.setEmail("assignee@gmail.com");
        assignee.setPassword(passwordEncoder.encode("12345678"));
        userRepository.save(assignee);

        UserEntity author = userRepository.findByEmail("test@gmail.com").orElseThrow();

        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("Documentation");
        request.setDescription("Write REST API documentation");
        request.setStatus("pending");
        request.setPriority("high");
        request.setAssigneeEmail(assignee.getEmail());

        MvcResult mvcResult = mockMvc.perform(post("/api/tasks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        IdDto idDto = objectMapper.readValue(content, IdDto.class);
        UUID taskId = idDto.getId();
        assertThat(taskRepository.findById(taskId)).isPresent();
        TaskEntity task = taskRepository.findById(taskId).orElseThrow();
        assertThat(task.getTitle()).isEqualTo(request.getTitle());
        assertThat(task.getDescription()).isEqualTo(request.getDescription());
        assertThat(task.getStatus()).isEqualTo(TaskStatus.PENDING);
        assertThat(task.getPriority()).isEqualTo(TaskPriority.HIGH);
        assertThat(task.getAuthor().getId()).isEqualTo(author.getId());
        assertThat(task.getAssignee().getId()).isEqualTo(assignee.getId());
    }

    @Test
    void getTaskByIdTest() throws Exception {
        UserEntity author = createAuthor();

        UserEntity assignee = createAssignee();

        UserEntity user = new UserEntity();
        user.setEmail("comment@gmail.com");
        user.setPassword(passwordEncoder.encode("12345678"));
        userRepository.save(user);

        TaskEntity task = new TaskEntity();
        task.setTitle("Documentation");
        task.setDescription("Write REST API documentation");
        task.setStatus(TaskStatus.PENDING);
        task.setPriority(TaskPriority.HIGH);
        task.setAuthor(author);
        task.setAssignee(assignee);
        taskRepository.save(task);

        CommentEntity comment = new CommentEntity();
        comment.setText("Comment 1");
        comment.setTask(task);
        comment.setAuthor(user);
        commentRepository.save(comment);

        MvcResult mvcResult = mockMvc.perform(get("/api/tasks/{id}", task.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        TaskDto taskDto = objectMapper.readValue(content, TaskDto.class);
        assertThat(taskDto.getId()).isEqualTo(task.getId());
        assertThat(taskDto.getTitle()).isEqualTo(task.getTitle());
        assertThat(taskDto.getDescription()).isEqualTo(task.getDescription());
        assertThat(taskDto.getStatus()).isEqualTo(task.getStatus().name());
        assertThat(taskDto.getPriority()).isEqualTo(task.getPriority().name());
        assertThat(taskDto.getAuthor().getId()).isEqualTo(task.getAuthor().getId());
        assertThat(taskDto.getAssignee().getId()).isEqualTo(task.getAssignee().getId());
        assertThat(taskDto.getComments()).hasSize(1);
        assertThat(taskDto.getComments().get(0).getText()).isEqualTo(comment.getText());
    }

    @Test
    void getTasksTest() throws Exception {
        UserEntity author = createAuthor();
        UserEntity assignee = createAssignee();
        TaskEntity task1 = new TaskEntity();
        task1.setTitle("Documentation1");
        task1.setStatus(TaskStatus.PENDING);
        task1.setPriority(TaskPriority.HIGH);
        task1.setAuthor(author);
        task1.setAssignee(assignee);

        TaskEntity task2 = new TaskEntity();
        task2.setTitle("Documentation2");
        task2.setStatus(TaskStatus.IN_PROCESS);
        task2.setPriority(TaskPriority.MEDIUM);
        task2.setAuthor(author);
        task2.setAssignee(assignee);


        TaskEntity task3 = new TaskEntity();
        task3.setTitle("Documentation3");
        task3.setStatus(TaskStatus.DONE);
        task3.setPriority(TaskPriority.LOW);
        task3.setAuthor(author);
        task3.setAssignee(assignee);

        taskRepository.saveAll(List.of(task1, task2, task3));

        MvcResult mvcResult = mockMvc.perform(get("/api/tasks"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        PagedDataDto<TaskDto> pagedDataDto = objectMapper.readValue(content, new TypeReference<>() {});

        List<TaskDto> tasks = pagedDataDto.getContent();
        assertThat(tasks).hasSize(3);
        assertThat(tasks.get(0).getId()).isEqualTo(task3.getId());
        assertThat(tasks.get(1).getId()).isEqualTo(task2.getId());
        assertThat(tasks.get(2).getId()).isEqualTo(task1.getId());
    }

    @Test
    void getTasksByAuthorTest() throws Exception {
        UserEntity author = createAuthor();
        UserEntity assignee = createAssignee();
        TaskEntity task1 = new TaskEntity();
        task1.setTitle("Documentation1");
        task1.setStatus(TaskStatus.PENDING);
        task1.setPriority(TaskPriority.HIGH);
        task1.setAuthor(author);
        task1.setAssignee(assignee);

        TaskEntity task2 = new TaskEntity();
        task2.setTitle("Documentation2");
        task2.setStatus(TaskStatus.IN_PROCESS);
        task2.setPriority(TaskPriority.MEDIUM);
        task2.setAuthor(author);
        task2.setAssignee(assignee);

        TaskEntity task3 = new TaskEntity();
        task3.setTitle("Documentation3");
        task3.setStatus(TaskStatus.DONE);
        task3.setPriority(TaskPriority.LOW);
        task3.setAuthor(assignee);
        task3.setAssignee(author);

        taskRepository.saveAll(List.of(task1, task2, task3));

        MvcResult mvcResult = mockMvc.perform(get("/api/tasks/author")
                        .param("authorEmail", author.getEmail()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        PagedDataDto<TaskDto> pagedDataDto = objectMapper.readValue(content, new TypeReference<>() {});
        List<TaskDto> tasks = pagedDataDto.getContent();
        assertThat(tasks.size()).isEqualTo(2);
        assertThat(tasks.get(0).getId()).isEqualTo(task2.getId());
        assertThat(tasks.get(1).getId()).isEqualTo(task1.getId());
    }

    @Test
    void getTasksByAssigneeTest() throws Exception {
        UserEntity author = createAuthor();
        UserEntity assignee = createAssignee();
        TaskEntity task1 = new TaskEntity();
        task1.setTitle("Documentation1");
        task1.setStatus(TaskStatus.PENDING);
        task1.setPriority(TaskPriority.HIGH);
        task1.setAuthor(author);
        task1.setAssignee(assignee);

        TaskEntity task2 = new TaskEntity();
        task2.setTitle("Documentation2");
        task2.setStatus(TaskStatus.IN_PROCESS);
        task2.setPriority(TaskPriority.MEDIUM);
        task2.setAuthor(assignee);
        task2.setAssignee(author);

        TaskEntity task3 = new TaskEntity();
        task3.setTitle("Documentation3");
        task3.setStatus(TaskStatus.DONE);
        task3.setPriority(TaskPriority.LOW);
        task3.setAuthor(author);
        task3.setAssignee(assignee);

        taskRepository.saveAll(List.of(task1, task2, task3));

        MvcResult mvcResult = mockMvc.perform(get("/api/tasks/assignee")
                        .param("assigneeEmail", assignee.getEmail()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        PagedDataDto<TaskDto> pagedDataDto = objectMapper.readValue(content, new TypeReference<>() {});
        List<TaskDto> tasks = pagedDataDto.getContent();
        assertThat(tasks.size()).isEqualTo(2);
        assertThat(tasks.get(0).getId()).isEqualTo(task3.getId());
        assertThat(tasks.get(1).getId()).isEqualTo(task1.getId());
    }

    @Test
    void updateTaskTitleTest() throws Exception {
        UserEntity author = userRepository.findByEmail("test@gmail.com").orElseThrow();
        UserEntity assignee = createAssignee();
        TaskEntity task = new TaskEntity();
        task.setTitle("Documentation");
        task.setDescription("Write REST API documentation");
        task.setStatus(TaskStatus.PENDING);
        task.setPriority(TaskPriority.HIGH);
        task.setAuthor(author);
        task.setAssignee(assignee);
        taskRepository.save(task);

        String newTitle = "Documentation updated";
        DataDto data = new DataDto(newTitle);

        mockMvc.perform(put("/api/tasks/{id}/title", task.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data))
                .header("Authorization", accessToken))
                .andDo(print())
                .andExpect(status().isOk());

        task = taskRepository.findById(task.getId()).orElseThrow();
        assertThat(task.getTitle()).isEqualTo(newTitle);
    }

    @Test
    void updateTaskDescriptionTest() throws Exception {
        UserEntity author = userRepository.findByEmail("test@gmail.com").orElseThrow();
        UserEntity assignee = createAssignee();
        TaskEntity task = new TaskEntity();
        task.setTitle("Documentation");
        task.setDescription("Write REST API documentation");
        task.setStatus(TaskStatus.PENDING);
        task.setPriority(TaskPriority.HIGH);
        task.setAuthor(author);
        task.setAssignee(assignee);
        taskRepository.save(task);

        String newDescription = "Write REST API documentation updated";
        DataDto data = new DataDto(newDescription);

        mockMvc.perform(put("/api/tasks/{id}/description", task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data))
                        .header("Authorization", accessToken))
                .andDo(print())
                .andExpect(status().isOk());

        task = taskRepository.findById(task.getId()).orElseThrow();
        assertThat(task.getDescription()).isEqualTo(newDescription);
    }

    @Test
    void updateTaskStatusTest() throws Exception {
        UserEntity author = createAuthor();
        UserEntity assignee = userRepository.findByEmail("test@gmail.com").orElseThrow();

        TaskEntity task = new TaskEntity();
        task.setTitle("Documentation");
        task.setDescription("Write REST API documentation");
        task.setStatus(TaskStatus.PENDING);
        task.setPriority(TaskPriority.HIGH);
        task.setAuthor(author);
        task.setAssignee(assignee);
        taskRepository.save(task);

        String newStatus = "done";
        DataDto data = new DataDto(newStatus);
        mockMvc.perform(put("/api/tasks/{id}/status", task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data))
                        .header("Authorization", accessToken))
                .andDo(print())
                .andExpect(status().isOk());

        task = taskRepository.findById(task.getId()).orElseThrow();
        assertThat(task.getStatus()).isEqualTo(TaskStatus.DONE);
    }

    @Test
    void updateTaskPriorityTest() throws Exception {
        UserEntity author = userRepository.findByEmail("test@gmail.com").orElseThrow();
        UserEntity assignee = createAssignee();

        TaskEntity task = new TaskEntity();
        task.setTitle("Documentation");
        task.setDescription("Write REST API documentation");
        task.setStatus(TaskStatus.PENDING);
        task.setPriority(TaskPriority.HIGH);
        task.setAuthor(author);
        task.setAssignee(assignee);
        taskRepository.save(task);
        String newPriority = "low";
        DataDto data = new DataDto(newPriority);
        mockMvc.perform(put("/api/tasks/{id}/priority", task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data))
                        .header("Authorization", accessToken))
                .andDo(print())
                .andExpect(status().isOk());
        task = taskRepository.findById(task.getId()).orElseThrow();
        assertThat(task.getPriority()).isEqualTo(TaskPriority.LOW);
    }

    @Test
    void setAssigneeTest() throws Exception {
        UserEntity author = userRepository.findByEmail("test@gmail.com").orElseThrow();
        UserEntity assignee = createAssignee();

        TaskEntity task = new TaskEntity();
        task.setTitle("Documentation");
        task.setDescription("Write REST API documentation");
        task.setStatus(TaskStatus.PENDING);
        task.setPriority(TaskPriority.HIGH);
        task.setAuthor(author);
        task.setAssignee(author);
        taskRepository.save(task);

        String newAssignee = assignee.getEmail();
        DataDto data = new DataDto(newAssignee);
        mockMvc.perform(put("/api/tasks/{id}/assignee", task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data))
                        .header("Authorization", accessToken))
                .andDo(print())
                .andExpect(status().isOk());

        task = taskRepository.findById(task.getId()).orElseThrow();
        assertThat(task.getAssignee().getEmail()).isEqualTo(assignee.getEmail());
    }

    @Test
    void deleteTaskTest() throws Exception {
        UserEntity author = userRepository.findByEmail("test@gmail.com").orElseThrow();
        UserEntity assignee = createAssignee();
        TaskEntity task = new TaskEntity();
        task.setTitle("Documentation");
        task.setDescription("Write REST API documentation");
        task.setStatus(TaskStatus.PENDING);
        task.setPriority(TaskPriority.HIGH);
        task.setAuthor(author);
        task.setAssignee(assignee);
        taskRepository.save(task);

        mockMvc.perform(delete("/api/tasks/{id}", task.getId())
                        .header("Authorization", accessToken))
                .andDo(print())
                .andExpect(status().isOk());

        assertThat(taskRepository.findById(task.getId())).isEmpty();
    }

    @Test
    void addCommentTest() throws Exception {
        UserEntity author = createAuthor();
        UserEntity assignee = createAssignee();
        TaskEntity task = new TaskEntity();

        task.setTitle("Documentation");
        task.setDescription("Write REST API documentation");
        task.setStatus(TaskStatus.PENDING);
        task.setPriority(TaskPriority.HIGH);
        task.setAuthor(author);
        task.setAssignee(assignee);

        taskRepository.save(task);

        DataDto data = new DataDto("Comment 1");
        mockMvc.perform(post("/api/tasks/{id}/comment", task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data))
                        .header("Authorization", accessToken))
                .andDo(print())
                .andExpect(status().isOk());

        List<CommentEntity> comments = commentRepository.findByTaskId(task.getId());
        assertThat(comments).hasSize(1);
        assertThat(comments.get(0).getText()).isEqualTo("Comment 1");
        assertThat(comments.get(0).getAuthor().getEmail()).isEqualTo("test@gmail.com");
    }

    @Test
    void deleteCommentTest() throws Exception {
        UserEntity author = createAuthor();
        UserEntity assignee = createAssignee();
        TaskEntity task = new TaskEntity();
        task.setTitle("Documentation");
        task.setDescription("Write REST API documentation");
        task.setStatus(TaskStatus.PENDING);
        task.setPriority(TaskPriority.HIGH);
        task.setAuthor(author);
        task.setAssignee(assignee);
        taskRepository.save(task);

        CommentEntity comment = new CommentEntity();
        comment.setText("Comment 1");
        comment.setTask(task);
        comment.setAuthor(userRepository.findByEmail("test@gmail.com").orElseThrow());
        commentRepository.save(comment);

        mockMvc.perform(delete("/api/tasks/{id}/comment/{commentId}", task.getId(), comment.getId())
                        .header("Authorization", accessToken))
                .andDo(print())
                .andExpect(status().isOk());

        assertThat(commentRepository.findById(comment.getId())).isEmpty();
    }


    private String getAccessToken() throws Exception {
        UserEntity user = new UserEntity();
        user.setEmail("test@gmail.com");
        user.setPassword(passwordEncoder.encode("12345678"));
        userRepository.save(user);

        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail(user.getEmail());
        authRequest.setPassword("12345678");
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponse authResponse = objectMapper.readValue(result.getResponse().getContentAsString(), AuthResponse.class);
        return authResponse.getAccessToken();
    }

    private UserEntity createAuthor(){
        UserEntity author = new UserEntity();
        author.setEmail("author@gmail.com");
        author.setPassword(passwordEncoder.encode("12345678"));
        return userRepository.save(author);
    }

    private UserEntity createAssignee(){
        UserEntity assignee = new UserEntity();
        assignee.setEmail("assignee@gmail.com");
        assignee.setPassword(passwordEncoder.encode("12345678"));
        return userRepository.save(assignee);
    }
}
