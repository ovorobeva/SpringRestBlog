package com.github.ovorobeva.controller;

import com.github.ovorobeva.dao.BlogRepository;
import com.github.ovorobeva.dao.UserRepository;
import com.github.ovorobeva.model.CustomUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    BlogRepository blogRepository;

    @PostMapping("/user/create")
    public ResponseEntity<CustomUser> createUser(@Valid @RequestBody Map<String, String> body) {
        CustomUser newUser = new CustomUser();
        newUser.setUsername(body.get("login"));
        newUser.setPassword(body.get("password"));
        newUser.setRole("USER");
        return ResponseEntity.status(201).body(userRepository.save(newUser));
    }
}
