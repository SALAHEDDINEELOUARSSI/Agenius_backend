package com.example.jobofferservice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

@Component
public class JwtUtil {

    // Remplace ceci par ta propre clé secrète sécurisée (au moins 256 bits si HS256)
    private static final String SECRET_KEY = "ma_cle_secrete_qui_doit_etre_tres_longue_et_secrete_123456789";

    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    // Extrait toutes les informations du token
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY.getBytes())
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();
    }


    // Extrait le nom d'utilisateur depuis le token
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Optionnel : Vérifie si le token est valide (expiration, signature, etc.)
    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
