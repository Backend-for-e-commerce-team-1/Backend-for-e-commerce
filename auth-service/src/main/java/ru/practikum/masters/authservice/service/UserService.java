package ru.practikum.masters.authservice.service;

import ru.practikum.masters.authservice.dto.*;

public interface UserService {

    UserDto addUser(NewUserRequestDto newUser);

    AuthUserResponseDto authUser(AuthUserRequestDto authUserDto);

    UserDto getUser(String token);

    UpdateUserResponseDto updateUser(UpdateUserRequestDto updateUser, String token);

}
