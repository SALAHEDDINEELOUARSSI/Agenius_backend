package com.AgeniusAgent.Agenius.service;



import com.AgeniusAgent.Agenius.entity.CandidatsSelected;
import com.AgeniusAgent.Agenius.repository.CandidatsSelectedRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.util.List;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.AgeniusAgent.Agenius.entity.Qcm;

@Service
public class QcmEmailService {

    private final JavaMailSender javaMailSender;
    private final CandidatsSelectedRepository candidatsSelectedRepository;
    private final GeminiQCMService geminiService;

    public QcmEmailService(JavaMailSender javaMailSender, CandidatsSelectedRepository candidatsSelectedRepository, GeminiQCMService geminiService) {
        this.javaMailSender = javaMailSender;
        this.candidatsSelectedRepository = candidatsSelectedRepository;
        this.geminiService = geminiService;
    }

    // Cette méthode envoie un email à tous les candidats
    public void sendQcmToAllCandidates(String jobTitle) throws MessagingException {
        // Récupérer tous les candidats
        List<CandidatsSelected> candidates = candidatsSelectedRepository.findByOffresName(jobTitle);
        if (candidates.isEmpty()) {
            System.out.println(" Aucun candidat trouvé en base de données !");
            return;
        }
        System.out.println("Liste des candidats :");
        for (CandidatsSelected candidate : candidates) {
            System.out.println("" + candidate.getEmail());
        }
        // Pour chaque candidat, générer un QCM et envoyer l'email
        for (CandidatsSelected candidate : candidates) {
            // Générer le QCM pour ce candidat
            Qcm qcm = geminiService.generateQcmForCandidate(candidate.getId(), jobTitle);

            // Envoyer l'email
            sendQcmEmail(candidate, qcm, jobTitle);
        }
    }
    public void sendQcmEmail(CandidatsSelected candidate, Qcm qcm, String jobTitle) throws MessagingException {
        System.out.println("Envoi de l'email à : " + candidate.getEmail());
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(candidate.getEmail());
        helper.setSubject("Technical Test for the " + jobTitle);
        String ngrokUrl = " https://85ad-196-120-8-216.ngrok-free.app";
        String testLink = ngrokUrl + "/questions?poste="+jobTitle+"&email=" + candidate.getEmail()+"&phone="+candidate.getPhone();

        String emailContent = "<p>Hello " + candidate.getName() + ",</p>"
                + "<p>You are invited to take a technical test for the <strong>" + jobTitle + "</strong> position.</p>"
                + "<p>Please click the link below to access the test:</p>"
                + "<p><a href='" + testLink+ "' style='color: #277913; font-weight: bold;'>Take the Test</a></p>"
                + "<p>Best regards,<br>The HR Team</p>";

        helper.setText(emailContent, true); // true pour HTML

        System.out.println(" Contenu de l'email : " + emailContent);

        javaMailSender.send(message);
        System.out.println(" Email envoyé à : " + candidate.getEmail());
    }
}