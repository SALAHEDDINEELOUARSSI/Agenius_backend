package com.example.agenius_back.web;


import com.example.agenius_back.service.PasswordResetService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/password-reset")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    // Endpoint pour créer un token de réinitialisation et envoyer l'email
    @PostMapping("/request")
    public ResponseEntity<String> requestPasswordReset(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        System.out.println("username controller"+username);
        String token = passwordResetService.createPasswordResetToken(username);
        if (token == null) {
            return ResponseEntity.badRequest().body("Utilisateur non trouvé");
        }
        return ResponseEntity.ok("Email de réinitialisation envoyé avec succès.");
    }


    // Endpoint pour vérifier la validité du token
    @GetMapping("/validate/{token}")
    public ResponseEntity<String> validateResetToken(@PathVariable String token) {
        if (passwordResetService.isResetTokenValid(token)) {
            return ResponseEntity.ok("Le token est valide.");
        } else {
            return ResponseEntity.badRequest().body("Le token n'est pas valide ou a expiré.");
        }
    }

    // Endpoint pour réinitialiser le mot de passe
    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestParam("token") String token,
                                                @RequestParam("newPassword") String newPassword) {
        if (passwordResetService.resetPassword(token, newPassword)) {
            return ResponseEntity.ok("Mot de passe réinitialisé avec succès.");
        } else {
            return ResponseEntity.badRequest().body("Échec de la réinitialisation du mot de passe. Le token peut être expiré.");
        }
    }
}