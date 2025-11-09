package ru.practikum.masters.authservice.mapper;

import org.mapstruct.*;
import ru.practikum.masters.authservice.dto.*;
import ru.practikum.masters.authservice.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUserFromNewUser(NewUserRequestDto newUser);

    UserDto toUserDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDto(UpdateUserRequestDto userDto, @MappingTarget User user);

    UpdateUserResponseDto toUpdateResponseFromUser(User user);
}