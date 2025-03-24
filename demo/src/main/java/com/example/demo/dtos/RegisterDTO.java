package com.example.demo.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public record RegisterDTO(
        String username,
        @NotBlank(message = "L'email ne peut pas être vide")
        @Email(message="L'email doit être valide")
        String email,
        String password,
        Set<String> roles
) {
}
