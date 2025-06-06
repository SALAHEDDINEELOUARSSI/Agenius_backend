package com.AgeniusAgent.Agenius.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.AgeniusAgent.Agenius.entity.UserResponse;
import com.AgeniusAgent.Agenius.repository.UserResponseRepository;

@Service
public class QcmSubmissionService {

    @Autowired
    private UserResponseRepository userResponseRepository;

    public UserResponse processSubmission(String email, String phone, Map<String, String> answers, String offreName)
    {
        Map<String, Integer> userResponses = new HashMap<>();
        int totalScore = 0;

        for (Map.Entry<String, String> entry : answers.entrySet()) {
            if (entry.getKey().startsWith("question_")) {
                String questionNumber = entry.getKey().replace("question_", "");
                try {
                    Integer selectedResult = Integer.parseInt(entry.getValue());
                    userResponses.put("q" + questionNumber, selectedResult);
                    totalScore += selectedResult;
                } catch (NumberFormatException e) {
                    // En cas de problème de parsing, on enregistre la réponse à 0
                    userResponses.put("q" + questionNumber, 0);
                }
            }
        }

        UserResponse userResponse = new UserResponse(email, userResponses);
        userResponse.setPhone(phone);
        userResponse.setOffrename(offreName);
        userResponse.setScore(totalScore);

        return userResponseRepository.save(userResponse);
    }

}