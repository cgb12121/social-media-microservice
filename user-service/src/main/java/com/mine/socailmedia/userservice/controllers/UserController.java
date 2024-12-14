package com.mine.socailmedia.userservice.controllers;


import com.mine.socailmedia.userservice.entities.User;
import com.mine.socailmedia.userservice.services.impl.UserService;
import com.mine.socialmedia.common.dtos.request.RegisterRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable UUID id) {
        return userService.getUserById(id);
    }
}
