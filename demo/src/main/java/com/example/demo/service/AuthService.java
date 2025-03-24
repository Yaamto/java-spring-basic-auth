package com.example.demo.service;

import com.example.demo.dtos.UserDTO;
import com.example.demo.exception.GenericException;
import com.example.demo.exception.GlobalExceptionHandler;
import com.example.demo.model.UserModel;
import com.example.demo.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Value("${app.secret-key}")
    private String secretKey;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public UserModel register(String username, String email, String password, Set<String> roles) {
        if(userRepository.findByUsername(username) != null) {
            throw new GenericException("Username is already in use");
        }


        UserModel user = new UserModel(null, username, email, passwordEncoder.encode(password), roles);
        userRepository.save(user);
        return user;
    }

    public String login(String username, String password) {
        UserModel userOpt = userRepository.findByUsername(username);
        if(userOpt == null || !passwordEncoder.matches(password, userOpt.getPassword())) {
            throw new GenericException("User not found");
        }

        return generateToken(userOpt);
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
