package ru.practikum.masters.authservice.mapper;

import org.mapstruct.*;
import ru.practikum.masters.authservice.dto.*;
import ru.practikum.masters.authservice.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    User toUserFromNewUser(NewUserRequestDto newUser);


    UserDto toUserDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateUserFromDto(UpdateUserRequestDto userDto, @MappingTarget User user);

    @Mapping(target = "updatedAt", ignore = true)
    UpdateUserResponseDto toUpdateResponseFromUser(User user);
}