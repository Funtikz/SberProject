package org.example.sberproject.dto.user;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class UserRegistrationDto {
    private Long id;

    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 8, max = 60, message = "Пароль должен быть от 8 до 60 символов")
    private String password;

    @NotBlank(message = "Подтверждение пароля не может быть пустым")
    @Size(min = 8, max = 60, message = "Пароль должен быть от 8 до 60 символов")
    private String confirmPassword;

    @NotBlank(message = "Имя не может быть пустым")
    @Size(min = 2, max = 60, message = "Имя должно быть от 2 до 60 символов")
    private String firstName;


    @NotBlank(message = "Фамилия не может быть пустой")
    @Size(min = 2, max = 60, message = "Фамилия должна быть от 2 до 60 символов")
    private String lastName;

    @NotBlank(message = "Номер телефона не может быть пустым")
    @Pattern(regexp = "^\\+?[0-9]{10,13}$", message = "Номер телефона должен быть в формате +7XXXXXXXXXX")
    private String phoneNumber;

    @NotBlank(message = "Почта не может быть пустой")
    private String email;


    private Set<String> roles;
}