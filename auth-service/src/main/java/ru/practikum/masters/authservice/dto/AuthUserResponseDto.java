package ru.practikum.masters.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthUserResponseDto {

    private String token;
    private Long expires_in;
}
