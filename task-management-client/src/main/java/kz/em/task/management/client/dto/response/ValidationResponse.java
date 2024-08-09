package kz.em.task.management.client.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidationResponse {
    private boolean valid;
    private String message;
}
