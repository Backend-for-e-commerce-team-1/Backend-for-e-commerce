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
public class UpdateUserRequestDto {

    @Length(min = 2, max = 50)
    @NotBlank
    @NotNull
    private String username;

    @Length(min = 6, max = 255)
    @NotBlank
    @Email
    private String email;

    public boolean hasUsername() {
        return !(username == null || username.isBlank());
    }

    public boolean hasEmail() {
        return !(email == null || email.isBlank());
    }

}
