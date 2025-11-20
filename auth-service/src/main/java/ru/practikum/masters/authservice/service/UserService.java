package ru.practikum.masters.authservice.service;

import ru.practikum.masters.authservice.dto.*;

public interface UserService {

    RegisterResponse registerUser(RegisterRequest newUser);

    LoginResponse authenticate(LoginRequest authUserDto);

    RegisterResponse getUser(String token);

    UpdateUserResponseDto updateUser(UpdateUserRequestDto updateUser, String token);

}
