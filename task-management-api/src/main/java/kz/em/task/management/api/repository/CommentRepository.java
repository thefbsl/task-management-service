package kz.em.task.management.api.repository;

import kz.em.task.management.api.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<CommentEntity, UUID> {
    List<CommentEntity> findByTaskId(UUID taskId);
    void deleteByTaskId(UUID taskId);
}
