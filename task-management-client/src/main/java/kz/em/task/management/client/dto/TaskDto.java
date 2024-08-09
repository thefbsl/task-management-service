package kz.em.task.management.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskDto {
    private UUID id;
    private String title;
    private String description;
    private String status;
    private String priority;
    private UserDto author;
    private UserDto assignee;
    private LocalDateTime createdAt;
    private List<CommentDto> comments;
}
