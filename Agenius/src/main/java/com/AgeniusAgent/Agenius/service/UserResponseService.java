package com.AgeniusAgent.Agenius.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.AgeniusAgent.Agenius.Model.Offres;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.AgeniusAgent.Agenius.entity.UserResponse;
import com.AgeniusAgent.Agenius.repository.UserResponseRepository;
import org.springframework.web.server.ResponseStatusException;


@Service
public class UserResponseService {
    @Autowired
    private UserResponseRepository userResponseRepository;
    @Autowired
    private OffresService offresService;
    @Autowired
    private UserResponseRepository repository;
    public void saveUserResponses(String email, List<String> questionIds, List<String> userAnswers, Map<String, String> correctAnswers,String offreName,String jobId) {
        Map<String, Integer> responses = new HashMap<>();
        for (int i = 0; i < questionIds.size(); i++) {
            String questionId = questionIds.get(i);
            String userAnswer = userAnswers.get(i);
            String correctAnswer = correctAnswers.get(questionId);
            // Vérifier si la réponse est correcte
            int score = userAnswer.equals(correctAnswer) ? 10 : 0;
            responses.put(questionId, score);
        }
        // Sauvegarde dans MongoDB
        UserResponse userResponse = new UserResponse(email, responses);
        userResponse.setOffrename(offreName);
        
        userResponseRepository.save(userResponse);

    }
    public double calculateAverageScoreByJobId(String jobId) {
        // Récupérer l'offre par ID via le service
        Offres offre = offresService.findOffreById(jobId);

        if (offre == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Offre introuvable pour l'ID : " + jobId);
        }

        // Récupérer le nom de l'offre
        String jobName = offre.getName(); // ou getTitle(), selon ton objet Offres

        List<UserResponse> candidats = repository.findByOffrename(jobName);
        int totalScore = 0;
        int count = 0;

        for (UserResponse response : candidats) {
            totalScore += response.getScore();
            count++;
        }

        return count == 0 ? 0 : (double) totalScore / count;
    }
}
