package kz.em.task.management.api.util;

import jakarta.servlet.http.HttpServletRequest;
import kz.em.task.management.api.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    private final JwtService jwtService;
    public String extractUsername(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        String token = null;
        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
        } else {
            throw new RuntimeException("Token is not found");
        }
        return jwtService.extractUsername(token);
    }
}
