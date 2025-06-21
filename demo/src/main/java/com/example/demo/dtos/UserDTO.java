package com.example.demo.dtos;

import java.util.Set;

public record UserDTO(
        Long id,
        String username,
        String email
) {
}
