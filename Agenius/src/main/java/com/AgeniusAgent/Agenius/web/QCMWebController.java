//Ce contrôleur s'occupe de la logique de rendu HTML. Il utilise Thymeleaf pour afficher les questions sur une page HTML lorsque l'utilisateur demande la page via le lien /questions?poste=<poste>.
package com.AgeniusAgent.Agenius.web;

import com.AgeniusAgent.Agenius.service.GeminiQCMService;
import com.AgeniusAgent.Agenius.service.QuestionCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.AgeniusAgent.Agenius.entity.Question;

import java.io.IOException;
import java.util.List;

@Controller
public class QCMWebController {

    @Autowired
    private GeminiQCMService geminiService;
    private final QuestionCacheService questionCacheService;

    @Autowired
    public QCMWebController (QuestionCacheService questionCacheService) {
        this.questionCacheService = questionCacheService;
    }

//si tu veux tester :http://localhost:8070/questions?poste=poste_choisi

    @GetMapping("/questions")
    public String getQuestions(@RequestParam("poste") String poste,
                               @RequestParam("email") String email,
                               @RequestParam("phone") String phone,
                               Model model) throws IOException {
        List<Question> questions;

        if (questionCacheService.contains(poste)) {
            questions = questionCacheService.get(poste);
        } else {
            questions = geminiService.generateQuestions(poste);
            questionCacheService.save(poste, questions);
        }

        model.addAttribute("questions", questions);
        model.addAttribute("poste", poste);
        model.addAttribute("email", email);
        model.addAttribute("phone", phone); // Ajout du numéro de téléphone au modèle

        return "questions"; // Vue Thymeleaf : questions.html
    }

}