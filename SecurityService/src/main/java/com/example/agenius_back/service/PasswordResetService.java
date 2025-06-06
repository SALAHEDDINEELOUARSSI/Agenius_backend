package com.example.agenius_back.service;

import com.example.agenius_back.entity.AppUser;
import com.example.agenius_back.repository.AppUserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private JavaMailSender mailSender; // Service d'envoi d'email

    // 1. Générer un token de reset et envoyer l'email
    public String createPasswordResetToken(String username) {
        System.out.println("Recherche de l'utilisateur avec username: " + username);

        AppUser user = userRepository.findByUsername(username);
        if (user == null) return null;

        // Générer un token de réinitialisation unique
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setTokenExpiration(new Date(System.currentTimeMillis() + 3600 * 1000)); // 1h
        userRepository.save(user);

        // Envoie de l'email avec le lien de réinitialisation
        sendPasswordResetEmail(user.getUsername(), token);

        return token;
    }

    // Méthode pour envoyer l'email de réinitialisation
    private void sendPasswordResetEmail(String username, String token) {
        String resetLink = "http://localhost:3000/reset-password?token=" + token;

        String subject = "Réinitialisation de votre mot de passe";
        String htmlMessage = "<p>Bonjour " + username + ",</p>" +
                "<p>Vous avez demandé la réinitialisation de votre mot de passe.</p>" +
                "<p>Veuillez cliquer sur le lien ci-dessous pour réinitialiser votre mot de passe :</p>" +
                "<p><a href=\"" + resetLink + "\">Réinitialiser mon mot de passe</a></p>" +
                "<p>Si vous n'avez pas demandé cette réinitialisation, veuillez ignorer cet email.</p>";

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(username); // L'email de l'utilisateur
            helper.setSubject(subject);
            helper.setText(htmlMessage, true); // true => contenu HTML

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
            // Log ou gestion d'erreur ici
        }
    }


    // 2. Vérifier si le token est valide
    public boolean isResetTokenValid(String token) {
        Optional<AppUser> userOpt = userRepository.findByResetToken(token);
        if (userOpt.isEmpty()) return false;

        AppUser user = userOpt.get();
        return user.getTokenExpiration().after(new Date());
    }

    // 3. Réinitialiser le mot de passe
    public boolean resetPassword(String token, String newPassword) {
        Optional<AppUser> userOpt = userRepository.findByResetToken(token);
        if (userOpt.isEmpty()) return false;

        AppUser user = userOpt.get();
        if (user.getTokenExpiration() == null || user.getTokenExpiration().before(new Date())) {
            return false;
        }

        user.setPassword(bCryptPasswordEncoder.encode(newPassword)); // 🔐 Encodage du mot de passe
        user.setResetToken(null); // 🔄 Nettoyage du token
        user.setTokenExpiration(null);
        userRepository.save(user);

        return true;
    }
}