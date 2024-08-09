package kz.em.task.management.api.service;

import kz.em.task.management.api.entity.RefreshTokenEntity;
import kz.em.task.management.api.entity.UserEntity;
import kz.em.task.management.api.exception.ResourceNotFoundException;
import kz.em.task.management.api.repository.RefreshTokenRepository;
import kz.em.task.management.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public String saveRefreshToken(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        RefreshTokenEntity token = refreshTokenRepository.findByUser(user);
        if (token != null){
            return token.getToken();
        }
        RefreshTokenEntity refreshToken = new RefreshTokenEntity();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(LocalDateTime.now().plusMonths(3));
        refreshToken.setUser(user);
        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }

    public String findUsername(String token) {
        RefreshTokenEntity refreshToken = refreshTokenRepository.findByToken(token);
        return refreshToken.getUser().getEmail();
    }


    public Boolean isExpired(String token) {
        RefreshTokenEntity refreshToken = refreshTokenRepository.findByToken(token);
        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.deleteByToken(token);
            return true;
        }
        return false;
    }
}
