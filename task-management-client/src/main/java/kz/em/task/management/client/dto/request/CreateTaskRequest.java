package kz.em.task.management.client.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateTaskRequest {
    @NotEmpty(message = "Title should not be empty or null")
    private String title;
    @NotEmpty(message = "Description should not be empty or null")
    private String description;
    @Pattern(regexp = "in progress|pending|done",
            message = "Status should be either 'in progress' or pending' or 'done'")
    private String status;
    @Pattern(regexp = "low|medium|high",
            message = "Priority should be either 'low' or 'medium' or 'high'")
    private String priority;
    @NotEmpty(message = "Email address should not be empty or null")
    @Email(message = "Email address should be valid")
    private String assigneeEmail;
}
