package ru.practikum.masters.authservice.mapper;

import ru.practikum.masters.authservice.dto.NewUserRequestDto;
import ru.practikum.masters.authservice.dto.UserDto;
import ru.practikum.masters.authservice.model.User;

public final class UserMapper {

    public static User toUserFromNewUser(NewUserRequestDto newUser) {
        User user = new User();
        user.setUsername(newUser.getUsername());
        user.setEmail(newUser.getEmail());
        user.setPassword(newUser.getPassword());
        return user;
    }

    public static UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setUserId(user.getUserId());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setCreatedAt(user.getCreatedAt());
        return userDto;
    }
}
