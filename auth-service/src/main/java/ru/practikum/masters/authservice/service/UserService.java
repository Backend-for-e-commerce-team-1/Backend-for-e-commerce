package ru.practikum.masters.authservice.service;

import ru.practikum.masters.authservice.dto.*;
import ru.practikum.masters.authservice.model.User;

public interface UserService {

    RegisterResponse registerUser(RegisterRequest newUser);

    LoginResponse authenticate(LoginRequest authUserDto);

    UserDetails getUser(String token);

    UpdateUserResponseDto updateUser(UpdateUserRequestDto updateUser, String token);

    User getUsernameFromToken(String token);
}
