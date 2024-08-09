package kz.em.task.management.client;

import jakarta.validation.Valid;
import kz.em.task.management.client.dto.IdDto;
import kz.em.task.management.client.dto.request.AuthRequest;
import kz.em.task.management.client.dto.request.RegistrationRequest;
import kz.em.task.management.client.dto.response.AccessTokenResponse;
import kz.em.task.management.client.dto.response.AuthResponse;
import kz.em.task.management.client.dto.response.ValidationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/api/auth")
public interface AuthClient {
    @PostMapping("/register")
    ResponseEntity<IdDto> registerUser(@Valid @RequestBody RegistrationRequest request);

    @PostMapping("/login")
    ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request);

    @GetMapping("/validate")
    ResponseEntity<ValidationResponse> validateToken(@RequestParam("token") String token);

    @PostMapping("/refresh-token")
    ResponseEntity<AccessTokenResponse> refreshAccessToken(@RequestParam("refreshToken") String refreshToken);
}
