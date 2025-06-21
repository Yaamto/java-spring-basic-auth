package com.example.demo.service;

import com.example.demo.dtos.LoginResponseDTO;
import com.example.demo.dtos.UserDTO;
import com.example.demo.exception.GenericException;
import com.example.demo.model.UserModel;
import com.example.demo.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapperService userMapperService;

    @Value("${app.secret-key}")
    private String secretKey;

    public UserModel register(String username, String email, String password) {
        if(userRepository.findByEmail(email).isPresent()) {
            throw new GenericException("Email is already in use");
        }
        Set<String> userRoles = Set.of("ROLE_USER");
        UserModel user = new UserModel();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(userRoles);

        return userRepository.save(user);
    }

    public LoginResponseDTO login(String email, String password) {
        UserModel user = userRepository.findByEmail(email)
            .orElseThrow(() -> new GenericException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new GenericException("Invalid password");
        }

        String token = generateToken(user);
        UserDTO userDto = userMapperService.toDto(user);

        return new LoginResponseDTO(token, userDto);
    }

    private String generateToken(UserModel user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("roles", user.getRoles())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 jour
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
}
