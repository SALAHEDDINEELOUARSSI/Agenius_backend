package com.AgeniusAgent.Agenius.web;

import com.AgeniusAgent.Agenius.entity.UserResponse;

import com.AgeniusAgent.Agenius.repository.UserResponseRepository;
import com.AgeniusAgent.Agenius.service.GeminiQCMService;
import com.AgeniusAgent.Agenius.service.QcmSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class AfterSubmetController {

    @Autowired
    private UserResponseRepository userResponseRepository;

    @Autowired
    private GeminiQCMService geminiService;

    @Autowired
    private QcmSubmissionService qcmSubmissionService;

    @PostMapping("/submit-answers/{offreName}")
    public String submitAnswers(@RequestParam String email,
                                @RequestParam String phone,
                                @RequestParam Map<String, String> answers,
                                @PathVariable String offreName,
                                Model model) {

        UserResponse userResponse = qcmSubmissionService.processSubmission(email, phone, answers, offreName);

        model.addAttribute("responses", userResponse.getResponses());
        return "result";
    }



}