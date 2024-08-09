package kz.em.task.management.api.mapper;

import kz.em.task.management.api.entity.TaskEntity;
import kz.em.task.management.api.repository.CommentRepository;
import kz.em.task.management.client.dto.CommentDto;
import kz.em.task.management.client.dto.TaskDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TaskMapper {
    private final UserMapper userMapper;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;

    public TaskDto toDto(TaskEntity taskEntity, Boolean includeAuthor, Boolean includeAssignee) {
        TaskDto taskDto = new TaskDto();
        taskDto.setId(taskEntity.getId());
        taskDto.setTitle(taskEntity.getTitle());
        taskDto.setDescription(taskEntity.getDescription());
        taskDto.setStatus(taskEntity.getStatus().name());
        taskDto.setPriority(taskEntity.getPriority().name());
        if (includeAuthor)
            taskDto.setAuthor(userMapper.toDto(taskEntity.getAuthor()));

        if (includeAssignee)
            taskDto.setAssignee(userMapper.toDto(taskEntity.getAssignee()));

        taskDto.setCreatedAt(taskEntity.getCreatedAt());

        List<CommentDto> comments = commentRepository.findByTaskId(taskEntity.getId())
                .stream().map(commentMapper::toDto).toList();
        taskDto.setComments(comments);
        return taskDto;
    }
}
