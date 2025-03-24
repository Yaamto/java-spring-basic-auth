package com.example.demo.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Value("${app.secret-key}")
    private String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Retire "Bearer " du début

            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey) // Clé secrète utilisée pour signer le JWT
                    .parseClaimsJws(token)
                    .getBody();

            // Extraire les rôles du JWT et les transformer en autorités
            List<String> roles = claims.get("roles", List.class);
            List<GrantedAuthority> authorities = roles.stream()
                    .map(role -> new SimpleGrantedAuthority(role))
                    .collect(Collectors.toList());

            // Authentifier l'utilisateur avec ses rôles
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    claims.getSubject(), null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication); // Authentifie l'utilisateur
        }

        chain.doFilter(request, response); // Poursuit le traitement de la requête
    }
}
