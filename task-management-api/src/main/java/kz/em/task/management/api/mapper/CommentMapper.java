package kz.em.task.management.api.mapper;

import kz.em.task.management.api.entity.CommentEntity;
import kz.em.task.management.client.dto.CommentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentMapper {
    private final UserMapper userMapper;

    public CommentDto toDto(CommentEntity commentEntity) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(commentEntity.getId());
        commentDto.setText(commentEntity.getText());
        commentDto.setAuthor(userMapper.toDto(commentEntity.getAuthor()));
        commentDto.setCreatedAt(commentEntity.getCreatedAt());
        return commentDto;
    }
}
