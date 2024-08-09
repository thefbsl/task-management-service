package kz.em.task.management.client.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrationRequest {
    @NotEmpty(message = "Name should not be empty or null")
    private String name;
    @NotEmpty(message = "Email address should not be empty or null")
    @Email(message = "Email address should be valid")
    private String email;
    @NotEmpty(message = "Password should not be empty or null")
    @Size(min = 8, message = "Password should be at least 8 characters long")
    private String password;
}
