package org.example.sberproject.dto.user;

import lombok.Data;

@Data
public class UserAuthDto {
    private String phoneNumber;
    private String password;
}