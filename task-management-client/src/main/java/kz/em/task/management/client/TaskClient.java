package kz.em.task.management.client;

import jakarta.validation.Valid;
import kz.em.task.management.client.dto.DataDto;
import kz.em.task.management.client.dto.IdDto;
import kz.em.task.management.client.dto.PagedDataDto;
import kz.em.task.management.client.dto.TaskDto;
import kz.em.task.management.client.dto.request.CreateTaskRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@RequestMapping("/api/tasks")
public interface TaskClient {
    @PostMapping
    ResponseEntity<IdDto> createTask(@Valid @RequestBody CreateTaskRequest request);

    @GetMapping("/{id}")
    ResponseEntity<TaskDto> getTaskById(@PathVariable("id") UUID id);

    @GetMapping
    ResponseEntity<PagedDataDto<TaskDto>> getTasks(
            @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize,
            @RequestParam(required = false, defaultValue = "createdAt") String sort,
            @RequestParam(required = false, defaultValue = "desc") String order);

    @GetMapping("/author")
    ResponseEntity<PagedDataDto<TaskDto>> getTasksByAuthor(
            @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize,
            @RequestParam(required = false, defaultValue = "createdAt") String sort,
            @RequestParam(required = false, defaultValue = "desc") String order,
            @RequestParam("authorEmail") String authorEmail);

    @GetMapping("/assignee")
    ResponseEntity<PagedDataDto<TaskDto>> getTasksByAssignee(
            @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize,
            @RequestParam(required = false, defaultValue = "createdAt") String sort,
            @RequestParam(required = false, defaultValue = "desc") String order,
            @RequestParam("assigneeEmail") String assigneeEmail);

    @PutMapping("/{id}/title")
    ResponseEntity<Void> updateTaskTitle(@PathVariable UUID id, @Valid @RequestBody DataDto data);

    @PutMapping("/{id}/description")
    ResponseEntity<Void> updateTaskDescription(@PathVariable UUID id, @Valid @RequestBody DataDto data);

    @PutMapping("/{id}/status")
    ResponseEntity<Void> updateTaskStatus(@PathVariable UUID id, @Valid @RequestBody DataDto data);

    @PutMapping("/{id}/priority")
    ResponseEntity<Void> updateTaskPriority(@PathVariable UUID id, @Valid @RequestBody DataDto data);

    @PutMapping("/{id}/assignee")
    ResponseEntity<Void> setAssignee(@PathVariable UUID id, @Valid @RequestBody DataDto data);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteTask(@PathVariable UUID id);

    @PostMapping("/{id}/comment")
    ResponseEntity<IdDto> addComment(@PathVariable UUID id, @Valid @RequestBody DataDto data);

    @DeleteMapping("/{id}/comment/{commentId}")
    ResponseEntity<Void> deleteComment(@PathVariable UUID id, @PathVariable UUID commentId);
}