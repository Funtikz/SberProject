package org.example.sberproject.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.sberproject.dto.user.UserAuthDto;
import org.example.sberproject.dto.user.UserResponseDto;
import org.example.sberproject.dto.user.UserRegistrationDto;
import org.example.sberproject.service.impl.UserServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private UserServiceImpl userServiceImpl;

    @PostMapping("/registration")
    public ResponseEntity<UserRegistrationDto> createUser(@Valid @RequestBody UserRegistrationDto userRegistrationDto){
        return new ResponseEntity<>(userServiceImpl.addUser(userRegistrationDto), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> login(@RequestBody UserAuthDto userCredentialDto , HttpServletResponse response) {
        return new ResponseEntity<>(userServiceImpl.login(userCredentialDto, response), HttpStatus.OK);
    }

    @GetMapping("/current-user")
    public ResponseEntity<UserResponseDto> getCurrentUser(@AuthenticationPrincipal Object principal) {
        return  new ResponseEntity<>(userServiceImpl.toDto(userServiceImpl.getCurrentUser(principal)), HttpStatus.OK);
    }

    @RequestMapping(value = "/**", method = RequestMethod.OPTIONS)
    public ResponseEntity<?> handleOptions() {
        return ResponseEntity.ok().build();
    }

}
