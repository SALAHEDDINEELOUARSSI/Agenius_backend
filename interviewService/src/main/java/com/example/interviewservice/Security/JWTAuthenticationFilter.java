package com.example.interviewservice.Security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private AuthenticationManager authenticationManager;
    private String secretKey = "your-secret-key";

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            String token = request.getHeader("Authorization");

            if (token == null || !token.startsWith("Bearer ")) {
                throw new RuntimeException("Token manquant ou invalide");
            }

            token = token.substring(7); // Supprime "Bearer " du token

            Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .parseClaimsJws(token);

            // Ici, tu peux extraire les informations du token si nécessaire

            return null; // Retourne null car il n'y a pas d'authentification classique (username/password)
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Échec de l'authentification par token", e);
        }
    }


    private byte[] getSigningKey() {
        return secretKey.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException, ServletException {
        User user=(User)authResult.getPrincipal();
        List<String> roles=new ArrayList<>();
        authResult.getAuthorities().forEach(a->{
            roles.add(a.getAuthority());
        });
        String jwt= JWT.create()
                .withIssuer(request.getRequestURI())
                .withSubject(user.getUsername())
                .withArrayClaim("roles",roles.toArray(new String[roles.size()]))
                .withExpiresAt(new Date(System.currentTimeMillis()+SecurityParams.EXPIRATION))
                .sign(Algorithm.HMAC256(SecurityParams.SECRET));
        response.addHeader(SecurityParams.JWT_HEADER_NAME,jwt);
    }

}
