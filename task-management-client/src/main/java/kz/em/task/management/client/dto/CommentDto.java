package kz.em.task.management.client.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;
@Data
public class CommentDto {
    private UUID id;
    private String text;
    private UserDto author;
    private LocalDateTime createdAt;
}
