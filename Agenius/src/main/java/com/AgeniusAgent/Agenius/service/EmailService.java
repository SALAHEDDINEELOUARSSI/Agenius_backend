package com.AgeniusAgent.Agenius.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;
   
    public void sendSuccessEmail(String toEmail,String jobName, String datesheduled) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Succès du test QCM");
        message.setText("Félicitations ! Vous avez réussi le test QCM pour le poste de " + jobName + ".\n" +
                        "La date prévue pour l'entretien est le " + datesheduled + ".");
        javaMailSender.send(message);
    }
    /*public void sendPendingEmail(String toEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("A propos du test QCM");
        message.setText("Cher Candidat ! Vous etes dans la liste d'attente .");
        javaMailSender.send(message);
    }*/
}