package ru.practikum.masters.authservice.mapper;

import org.mapstruct.*;
import ru.practikum.masters.authservice.dto.*;
import ru.practikum.masters.authservice.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User toUserFromNewUser(RegisterRequest newUser);


    RegisterResponse toUserDto(User user);

    UserDetails toUserDtoDetails(User user);

    UserProfileResponse toUserProfileResponse(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "roles", ignore = true)
    void updateUserFromDto(UpdateProfileRequest userDto, @MappingTarget User user);

    UpdateUserResponseDto toUpdateResponseFromUser(User user);
}