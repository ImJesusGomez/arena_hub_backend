package com.jesus_dev.arena_hub.mapper;

import com.jesus_dev.arena_hub.dto.response.UserResponseDTO;
import com.jesus_dev.arena_hub.model.User;
import com.jesus_dev.arena_hub.security.dto.request.RegisterRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User registerDtoToUser(RegisterRequestDTO dto);

    UserResponseDTO toUserResponseDTO(User user);
}
