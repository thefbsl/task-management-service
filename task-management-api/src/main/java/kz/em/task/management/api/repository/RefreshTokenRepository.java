package kz.em.task.management.api.repository;

import jakarta.transaction.Transactional;
import kz.em.task.management.api.entity.RefreshTokenEntity;
import kz.em.task.management.api.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {
    RefreshTokenEntity findByToken(String token);
    @Query("select rt from RefreshTokenEntity rt where rt.user = ?1")
    RefreshTokenEntity findByUser(UserEntity user);
    @Query("delete from RefreshTokenEntity rt where rt.token = ?1")
    void deleteByToken(String token);
    @Transactional
    void deleteByUser(UserEntity user);
}
