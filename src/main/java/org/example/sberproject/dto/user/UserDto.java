package org.example.sberproject.dto.user;
import lombok.Data;

import java.util.Set;

@Data
public class UserDto {
    private Long id;
    private String phoneNumber;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private Set<String> roles;
}
