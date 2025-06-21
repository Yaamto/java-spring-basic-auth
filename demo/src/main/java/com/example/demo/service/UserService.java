package com.example.demo.service;

import com.example.demo.dtos.PropertyDTO;
import com.example.demo.exception.GenericException;
import com.example.demo.model.PropertyModel;
import com.example.demo.model.UserModel;
import com.example.demo.repository.PropertyRepository;
import com.example.demo.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private  PropertyMapperService propertyMapperService;

    @Value("${app.secret-key}")
    private String secretKey;
    @Autowired
    private PropertyRepository propertyRepository;

    public void initiatePasswordReset(String email) {
        try{
            UserModel user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new GenericException("Utilisateur non trouvé"));

            String token = generatePasswordResetToken(user.getEmail());
            user.setPasswordResetToken(token);
            userRepository.save(user);

            sendPasswordResetEmail(user.getEmail(), token);
        }catch(Exception e){
          e.printStackTrace();
        }

    }

    private String generatePasswordResetToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 15 * 60 * 1000); // 15 minutes

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim("purpose", "password_reset")
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    private void sendPasswordResetEmail(String email, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Réinitialisation de votre mot de passe");
        message.setText("Pour réinitialiser votre mot de passe, cliquez sur le lien suivant : " +
                "http://localhost:4200/reset-password?token=" + token + "\n\n" +
                "Ce lien expirera dans 15 minutes.");

        mailSender.send(message);
    }

    private Claims validatePasswordResetToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();

            // Vérifie que c'est bien un token de réinitialisation
            if (!"password_reset".equals(claims.get("purpose"))) {
                throw new GenericException("Token invalide");
            }

            return claims;
        } catch (ExpiredJwtException e) {
            throw new GenericException("Le token a expiré");
        } catch (Exception e) {
            throw new GenericException("Token invalide");
        }
    }

    public void resetPassword(String token, String newPassword) {
        // Valide le token et récupère les informations
        Claims claims = validatePasswordResetToken(token);
        String email = claims.getSubject();

        // Vérifie que le token correspond bien à l'utilisateur
        UserModel user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GenericException("Utilisateur non trouvé"));

        if (!token.equals(user.getPasswordResetToken())) {
            throw new GenericException("Token invalide");
        }

        // Met à jour le mot de passe et supprime le token
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        userRepository.save(user);
    }
    public boolean checkIfTokenIsValid(String token) {
        try {
            Claims claims = validatePasswordResetToken(token);
            String email = claims.getSubject();

            // Vérifie que le token correspond bien à un utilisateur
            UserModel user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new GenericException("Utilisateur non trouvé"));

            return token.equals(user.getPasswordResetToken());
        } catch (Exception e) {
            return false;
        }
    }

    public void addToFavorites(Long propertyId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserModel user = userRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        PropertyModel property = propertyRepository.findById(propertyId)
            .orElseThrow(() -> new RuntimeException("Propriété non trouvée"));

        if (user.getFavorites() == null) {
            user.setFavorites(new HashSet<>());
        }
        user.getFavorites().add(property);
        userRepository.save(user);
    }

    public void removeFromFavorites( Long propertyId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserModel user = userRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        PropertyModel property = propertyRepository.findById(propertyId)
            .orElseThrow(() -> new RuntimeException("Propriété non trouvée"));

        if (user.getFavorites() != null) {
            user.getFavorites().remove(property);
            userRepository.save(user);
        }
    }

    public Set<PropertyDTO> getUserFavorites() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserModel user = userRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return user.getFavorites() != null ? user.getFavorites().stream().map(propertyMapperService::toDto).collect(Collectors.toSet()) : new HashSet<>();
    }
}
