package com.ai.chat.controllers;

import java.security.Principal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ai.chat.models.AppUser;
import com.ai.chat.repository.UserRepository;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository user_repo;

    private final BCryptPasswordEncoder encoder;

    public AuthController(BCryptPasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @PostMapping("/register")
    public String register(@RequestBody Map<String, String> req) {

        AppUser user = new AppUser();
        user.setUsername(req.get("username"));
        user.setPassword(encoder.encode(req.get("password")));

        user_repo.save(user);

        return "Registration Successful";
    }

    @GetMapping("/me")
    public Object me(Principal principal) {

        if (principal == null) {
            return null;
        }

        return Map.of(
                "username", principal.getName()
        );
    }
}