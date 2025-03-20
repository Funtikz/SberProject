package org.example.sberproject.controller;

import lombok.AllArgsConstructor;
import org.example.sberproject.entity.User;
import org.example.sberproject.service.impl.UserServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/admin")
@AllArgsConstructor
public class AdminController {
    private final UserServiceImpl userServiceImpl;

    @GetMapping("get-all")
    public ResponseEntity<List<User>> getAll(){
        return  new ResponseEntity<>(userServiceImpl.getAll(), HttpStatus.OK);
    }
}
