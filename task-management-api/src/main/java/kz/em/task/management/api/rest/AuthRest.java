package kz.em.task.management.api.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kz.em.task.management.api.exception.InvalidAccessException;
import kz.em.task.management.api.exception.InvalidPasswordException;
import kz.em.task.management.api.service.AuthService;
import kz.em.task.management.api.service.JwtService;
import kz.em.task.management.api.service.RefreshTokenService;
import kz.em.task.management.client.AuthClient;
import kz.em.task.management.client.dto.IdDto;
import kz.em.task.management.client.dto.request.AuthRequest;
import kz.em.task.management.client.dto.request.RegistrationRequest;
import kz.em.task.management.client.dto.response.AccessTokenResponse;
import kz.em.task.management.client.dto.response.AuthResponse;
import kz.em.task.management.client.dto.response.ValidationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "REST API для Аутентификации",
        description = "CRUD операции: Регистрация пользователя, Вход, Проверка токена, Обновление токена доступа")
@RestController
@RequiredArgsConstructor
public class AuthRest implements AuthClient {
    private final AuthService authService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    @Operation(summary = "REST API для Регистрации Пользователя")
    @Override
    public ResponseEntity<IdDto> registerUser(RegistrationRequest request) {
        return ResponseEntity.ok(authService.registerUser(request));
    }

    @Operation(summary = "REST API для Входа в Систему")
    @Override
    public ResponseEntity<AuthResponse> login(AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate
                    (new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            if (authentication.isAuthenticated())
                if (authService.isUserVerified(request.getEmail())) {
                    AuthResponse response = new AuthResponse();
                    response.setAccessToken(jwtService.generateToken(request.getEmail()));
                    response.setRefreshToken(refreshTokenService.saveRefreshToken(request.getEmail()));
                    return ResponseEntity.ok(response);
                } else {
                    throw new InvalidAccessException("User is not verified");
                }
            else {
                throw new InvalidAccessException("Invalid access");
            }
        } catch (BadCredentialsException exception){
            throw new InvalidPasswordException("Invalid password");
        }
    }

    @Operation(summary = "REST API для Проверки Токена")
    @Override
    public ResponseEntity<ValidationResponse> validateToken(String token) {
        if(jwtService.validateToken(token)) {
            return ResponseEntity.ok(new ValidationResponse(true, "Token is valid"));
        }
        return ResponseEntity.ok(new ValidationResponse(false, "Token is invalid"));
    }

    @Operation(summary = "REST API для Обновления Токена Доступа")
    @Override
    public ResponseEntity<AccessTokenResponse> refreshAccessToken(String refreshToken) {
        if (refreshTokenService.isExpired(refreshToken)){
            throw new InvalidAccessException("Refresh token is expired");
        }
        String username = refreshTokenService.findUsername(refreshToken);
        AccessTokenResponse response = new AccessTokenResponse();
        response.setAccessToken(jwtService.generateToken(username));
        return ResponseEntity.ok(response);
    }
}
