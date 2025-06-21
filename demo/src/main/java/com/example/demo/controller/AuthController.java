package com.example.demo.controller;

import com.example.demo.dtos.*;
import com.example.demo.model.PropertyModel;
import com.example.demo.model.UserModel;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AuthService;
import com.example.demo.service.UserMapperService;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserRepository userRepository;
    private final UserMapperService userMapperService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody RegisterDTO request) {
        UserModel user = authService.register(request.username(), request.email(), request.password());
        return ResponseEntity.ok(userMapperService.toDto(user));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginDTO request) {
        return ResponseEntity.ok(authService.login(request.email(), request.password()));
    }

    @GetMapping("/me")
    public Optional<UserDTO> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String username = authentication.getName();
            if (username == null || username.isEmpty()) {
                return Optional.empty();
            }
            return userRepository.findByUsername(username)
                    .map(userMapperService::toDto);
        }
        return Optional.empty();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody PasswordResetDTO request) {
        userService.initiatePasswordReset(request.getEmail());
        return ResponseEntity.ok("Un email de réinitialisation a été envoyé");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetDTO request) {
        userService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok("Mot de passe réinitialisé avec succès");
    }

    @PostMapping("/check-token")
    public ResponseEntity<Boolean> checkToken(@RequestBody TokenDTO tokenDto) {
        return ResponseEntity.ok(userService.checkIfTokenIsValid(tokenDto.getToken()));
    }

    @PostMapping("/favorites/{propertyId}")
    public ResponseEntity<?> addToFavorites(@PathVariable Long propertyId) {
        userService.addToFavorites(propertyId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/favorites/{propertyId}")
    public ResponseEntity<?> removeFromFavorites(@PathVariable Long propertyId) {
        userService.removeFromFavorites(propertyId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/favorites")
    public ResponseEntity<Set<PropertyDTO>> getUserFavorites() {
        Set<PropertyDTO> favorites = userService.getUserFavorites();
        return ResponseEntity.ok(favorites);
    }
}
