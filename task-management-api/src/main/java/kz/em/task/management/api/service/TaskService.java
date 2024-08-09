package kz.em.task.management.api.service;

import kz.em.task.management.api.entity.CommentEntity;
import kz.em.task.management.api.entity.TaskEntity;
import kz.em.task.management.api.enums.TaskPriority;
import kz.em.task.management.api.enums.TaskStatus;
import kz.em.task.management.api.exception.AccessDeniedException;
import kz.em.task.management.api.exception.ResourceNotFoundException;
import kz.em.task.management.api.mapper.TaskMapper;
import kz.em.task.management.api.repository.CommentRepository;
import kz.em.task.management.api.repository.TaskRepository;
import kz.em.task.management.api.repository.UserRepository;
import kz.em.task.management.api.util.PaginationUtil;
import kz.em.task.management.client.dto.DataDto;
import kz.em.task.management.client.dto.IdDto;
import kz.em.task.management.client.dto.PagedDataDto;
import kz.em.task.management.client.dto.TaskDto;
import kz.em.task.management.client.dto.request.CreateTaskRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final TaskMapper taskMapper;

    public IdDto createTask(CreateTaskRequest request, String authorEmail) {
        TaskEntity task = new TaskEntity();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(TaskStatus.valueOf(request.getStatus().toUpperCase()));
        task.setPriority(TaskPriority.valueOf(request.getPriority().toUpperCase()));
        task.setAssignee(userRepository.findByEmail(request.getAssigneeEmail()).orElseThrow());
        task.setAuthor(userRepository.findByEmail(authorEmail).orElseThrow());
        taskRepository.save(task);
        return new IdDto(task.getId());
    }

    public TaskDto getTaskById(UUID id) {
        TaskEntity task = findTask(id);
        return taskMapper.toDto(task, true, true);
    }

    public PagedDataDto<TaskDto> getTasks(Integer pageNumber,
                                          Integer pageSize,
                                          String sort,
                                          String order) {
        Pageable pageable = PaginationUtil.toPageable(pageNumber, pageSize, sort, order);
        Page<TaskDto> tasksPage = taskRepository.findAll(pageable)
                .map(e -> taskMapper.toDto(e, true, true));
        return new PagedDataDto<>(tasksPage.getNumber(), tasksPage.getSize(), tasksPage.getTotalElements(), tasksPage.getContent(), tasksPage.getTotalPages());
    }



    public PagedDataDto<TaskDto> getTasksByAuthor(Integer pageNumber,
                                                  Integer pageSize,
                                                  String sort,
                                                  String order, String authorEmail) {
        Pageable pageable = PaginationUtil.toPageable(pageNumber, pageSize, sort, order);
        Page<TaskDto> tasksPage = taskRepository.findAllByAuthor(userRepository.findByEmail(authorEmail).orElseThrow(), pageable)
                .map(e -> taskMapper.toDto(e, false, true));
        return new PagedDataDto<>(tasksPage.getNumber(), tasksPage.getSize(), tasksPage.getTotalElements(), tasksPage.getContent(), tasksPage.getTotalPages());
    }

    public PagedDataDto<TaskDto> getTasksByAssignee(Integer pageNumber,
                                                    Integer pageSize,
                                                    String sort,
                                                    String order, String assigneeEmail) {
        Pageable pageable = PaginationUtil.toPageable(pageNumber, pageSize, sort, order);
        Page<TaskDto> tasksPage = taskRepository.findAllByAssignee(userRepository.findByEmail(assigneeEmail).orElseThrow(), pageable)
                .map(e -> taskMapper.toDto(e, true, false));
        return new PagedDataDto<>(tasksPage.getNumber(), tasksPage.getSize(), tasksPage.getTotalElements(), tasksPage.getContent(), tasksPage.getTotalPages());
    }

    @Transactional
    public void updateTaskTitle(UUID id, DataDto data, String currentUserEmail) {
        TaskEntity task = findTask(id);
        if (!task.getAuthor().getEmail().equals(currentUserEmail))
            throw new AccessDeniedException("You are not allowed to change title of this task");

        taskRepository.updateTitle(id, data.getData());
    }

    @Transactional
    public void updateTaskDescription(UUID id, DataDto data, String currentUserEmail) {
        TaskEntity task = findTask(id);
        if (!task.getAuthor().getEmail().equals(currentUserEmail))
            throw new AccessDeniedException("You are not allowed to change description of this task");

        taskRepository.updateDescription(id, data.getData());
    }

    @Transactional
    public void updateTaskStatus(UUID id, DataDto data, String currentUserEmail) {
        TaskEntity task = findTask(id);
        if (!task.getAssignee().getEmail().equals(currentUserEmail))
            throw new AccessDeniedException("You are not allowed to change status of this task");

        taskRepository.updateStatus(id, TaskStatus.valueOf(data.getData().toUpperCase()));
    }

    @Transactional
    public void updateTaskPriority(UUID id, DataDto data, String currentUserEmail) {
        TaskEntity task = findTask(id);
        if (!task.getAuthor().getEmail().equals(currentUserEmail))
            throw new AccessDeniedException("You are not allowed to change priority of this task");

        taskRepository.updatePriority(id, TaskPriority.valueOf(data.getData().toUpperCase()));
    }

    @Transactional
    public void setAssignee(UUID id, DataDto data, String currentUserEmail) {
        TaskEntity task = findTask(id);
        if (!task.getAuthor().getEmail().equals(currentUserEmail))
            throw new AccessDeniedException("You are not allowed to change assignee of this task");
        taskRepository.updateAssignee(id, userRepository.findByEmail(data.getData()).orElseThrow());
    }

    @Transactional
    public void deleteTask(UUID id, String currentUserEmail) {
        TaskEntity task = findTask(id);
        if (!task.getAuthor().getEmail().equals(currentUserEmail))
            throw new AccessDeniedException("You are not allowed to delete this task");
        commentRepository.deleteByTaskId(id);
        taskRepository.deleteById(id);
    }

    public IdDto addComment(UUID id, DataDto data, String authorEmail) {
        CommentEntity comment = new CommentEntity();
        comment.setText(data.getData());
        comment.setTask(taskRepository.findById(id).orElseThrow());
        comment.setAuthor(userRepository.findByEmail(authorEmail).orElseThrow());
        commentRepository.save(comment);
        return new IdDto(id);
    }

    public void deleteComment(UUID id, UUID commentId, String currentUserEmail) {
        findTask(id);
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId.toString()));
        if (!comment.getAuthor().getEmail().equals(currentUserEmail))
            throw new AccessDeniedException("You are not allowed to delete this comment");
        commentRepository.deleteById(commentId);
    }

    private TaskEntity findTask(UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id.toString()));
    }
}
