package kz.em.task.management.api.mapper;

import kz.em.task.management.api.entity.UserEntity;
import kz.em.task.management.client.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDto toDto(UserEntity userEntity) {
        UserDto userDto = new UserDto();
        userDto.setId(userEntity.getId());
        userDto.setName(userEntity.getName());
        userDto.setEmail(userEntity.getEmail());
        userDto.setPassword(userEntity.getPassword());
        userDto.setCreatedAt(userEntity.getCreatedAt());
        return userDto;
    }
}
