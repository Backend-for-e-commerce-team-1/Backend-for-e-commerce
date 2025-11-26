package ru.practikum.masters.authservice.service;

import ru.practikum.masters.authservice.dto.*;

public interface UserService {

    RegisterResponse registerUser(RegisterRequest newUser);

    LoginResponse authenticate(LoginRequest authUserDto);

    UserProfileResponse getUserProfile(String token);

    UpdateUserResponseDto updateUserProfile(UpdateProfileRequest updateUser, String token);

}
