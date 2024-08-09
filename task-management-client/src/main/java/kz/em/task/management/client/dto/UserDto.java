package kz.em.task.management.client.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserDto {
    private UUID id;
    private String name;
    private String email;
    private String password;
    private LocalDateTime createdAt;
}
