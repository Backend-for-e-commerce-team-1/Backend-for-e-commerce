package ru.practikum.masters.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewUserRequestDto {

    @Length(min = 2, max = 50)
    @NotBlank
    @NotNull
    private String username;

    @Length(min = 6, max = 255)
    @NotBlank
    @Email
    private String email;

    @Length(min = 8, max = 255)
    @NotBlank
    @NotNull
    private String password;
}
