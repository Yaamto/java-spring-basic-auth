package com.example.demo.controller;

import com.example.demo.dtos.LoginDTO;
import com.example.demo.dtos.RegisterDTO;
import com.example.demo.dtos.UserDTO;
import com.example.demo.model.UserModel;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AuthService;
import com.example.demo.service.UserMapperService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserRepository userRepository;
    private final UserMapperService userMapperService;

    @PostMapping("/register")
    public ResponseEntity<UserModel> register(@Valid @RequestBody RegisterDTO request) {
            UserModel user = authService.register(request.username(), request.email(), request.password(), request.roles());
            return ResponseEntity.ok(user);
}



    @PostMapping("/login")
    public String login(@RequestBody LoginDTO request) {
        return authService.login(request.username(), request.password());
    }

    @GetMapping("/me")
    public Optional<UserDTO> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String username = authentication.getName();
            return Optional.of(userRepository.findByUsername(username))
                    .map(userMapperService::toDto);
        }
        return Optional.empty();
    }
}
