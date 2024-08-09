package kz.em.task.management.api.repository;

import kz.em.task.management.api.entity.TaskEntity;
import kz.em.task.management.api.entity.UserEntity;
import kz.em.task.management.api.enums.TaskPriority;
import kz.em.task.management.api.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {
    Page<TaskEntity> findAllByAuthor(UserEntity author, Pageable pageable);
    Page<TaskEntity> findAllByAssignee(UserEntity assignee, Pageable pageable);

    @Modifying
    @Query("update TaskEntity t set t.title = :title where t.id = :id")
    void updateTitle(UUID id, String title);

    @Modifying
    @Query("update TaskEntity t set t.description = :description where t.id = :id")
    void updateDescription(UUID id, String description);

    @Modifying
    @Query("update TaskEntity t set t.status = :status where t.id = :id")
    void updateStatus(UUID id, TaskStatus status);

    @Modifying
    @Query("update TaskEntity t set t.priority = :priority where t.id = :id")
    void updatePriority(UUID id, TaskPriority priority);

    @Modifying
    @Query("update TaskEntity t set t.assignee = :assignee where t.id = :id")
    void updateAssignee(UUID id, UserEntity assignee);
}
