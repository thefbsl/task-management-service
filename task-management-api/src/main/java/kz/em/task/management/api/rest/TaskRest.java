package kz.em.task.management.api.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import kz.em.task.management.api.service.TaskService;
import kz.em.task.management.api.util.JwtUtil;
import kz.em.task.management.client.TaskClient;
import kz.em.task.management.client.dto.DataDto;
import kz.em.task.management.client.dto.IdDto;
import kz.em.task.management.client.dto.PagedDataDto;
import kz.em.task.management.client.dto.TaskDto;
import kz.em.task.management.client.dto.request.CreateTaskRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


@Tag(
        name = "REST API для Управления Задачами",
        description = "CRUD операции: Создание задачи, Получение задачи по ID, Получение всех задач, " +
                "Обновление названия, описания, статуса, приоритета задачи, Установка исполнителя, Удаление задачи, " +
                "Добавление комментария к задаче"
)
@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class TaskRest implements TaskClient {
    private final TaskService taskService;
    private final HttpServletRequest httpServletRequest;
    private final JwtUtil jwtUtil;

    @Operation(summary = "REST API для Создания Задачи")
    @Override
    public ResponseEntity<IdDto> createTask(CreateTaskRequest request) {
        return ResponseEntity.ok(taskService.createTask(request, jwtUtil.extractUsername(httpServletRequest)));
    }

    @Operation(summary = "REST API для Получения Задачи по ID")
    @Override
    public ResponseEntity<TaskDto> getTaskById(UUID id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @Operation(summary = "REST API для Получения всех Задач")
    @Override
    public ResponseEntity<PagedDataDto<TaskDto>> getTasks(Integer pageNumber, Integer pageSize,
                                                          String sort, String order) {
        return ResponseEntity.ok(taskService.getTasks(pageNumber, pageSize, sort, order));
    }

    @Operation(summary = "REST API для Получения Задач по Автору")
    @Override
    public ResponseEntity<PagedDataDto<TaskDto>> getTasksByAuthor(Integer pageNumber, Integer pageSize,
                                                                  String sort, String order, String authorEmail) {
        return ResponseEntity.ok(taskService.getTasksByAuthor(pageNumber, pageSize, sort, order, authorEmail));
    }

    @Operation(summary = "REST API для Получения Задач по Исполнителю")
    @Override
    public ResponseEntity<PagedDataDto<TaskDto>> getTasksByAssignee(Integer pageNumber, Integer pageSize,
                                                                    String sort, String order,
                                                                    String assigneeEmail) {
        return ResponseEntity.ok(taskService.getTasksByAssignee(pageNumber, pageSize, sort, order, assigneeEmail));
    }

    @Operation(summary = "REST API для Обновления Названия Задачи")
    @Override
    public ResponseEntity<Void> updateTaskTitle(UUID id, DataDto data) {
        taskService.updateTaskTitle(id, data, jwtUtil.extractUsername(httpServletRequest));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "REST API для Обновления Описания Задачи")
    @Override
    public ResponseEntity<Void> updateTaskDescription(UUID id, DataDto data) {
        taskService.updateTaskDescription(id, data, jwtUtil.extractUsername(httpServletRequest));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "REST API для Обновления Статуса Задачи")
    @Override
    public ResponseEntity<Void> updateTaskStatus(UUID id, DataDto data) {
        taskService.updateTaskStatus(id, data, jwtUtil.extractUsername(httpServletRequest));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "REST API для Обновления Приоритета Задачи")
    @Override
    public ResponseEntity<Void> updateTaskPriority(UUID id, DataDto data) {
        taskService.updateTaskPriority(id, data, jwtUtil.extractUsername(httpServletRequest));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "REST API для Установки Исполнителя Задачи")
    @Override
    public ResponseEntity<Void> setAssignee(UUID id, DataDto data) {
        taskService.setAssignee(id, data, jwtUtil.extractUsername(httpServletRequest));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "REST API для Удаления Задачи")
    @Override
    public ResponseEntity<Void> deleteTask(UUID id) {
        taskService.deleteTask(id, jwtUtil.extractUsername(httpServletRequest));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "REST API для Добавления Комментария к Задаче")
    @Override
    public ResponseEntity<IdDto> addComment(UUID id, DataDto data) {
        return ResponseEntity.ok(taskService.addComment(id, data, jwtUtil.extractUsername(httpServletRequest)));
    }

    @Operation(summary = "REST API для Удаления Комментария с Задачи")
    @Override
    public ResponseEntity<Void> deleteComment(UUID id, UUID commentId) {
        taskService.deleteComment(id, commentId, jwtUtil.extractUsername(httpServletRequest));
        return null;
    }
}
