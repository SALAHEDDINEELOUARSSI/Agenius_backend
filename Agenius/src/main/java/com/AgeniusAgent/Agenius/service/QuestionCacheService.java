package com.AgeniusAgent.Agenius.service;

import com.AgeniusAgent.Agenius.entity.Question;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QuestionCacheService {

    private final Map<String, List<Question>> cache = new HashMap<>();

    // Vérifie si des questions sont déjà mises en cache pour un poste donné
    public boolean contains(String poste) {
        return cache.containsKey(poste);
    }

    // Récupère les questions mises en cache pour un poste donné
    public List<Question> get(String poste) {
        return cache.get(poste);
    }

    // Sauvegarde les questions en cache pour un poste donné
    public void save(String poste, List<Question> questions) {
        cache.put(poste, questions);
    }
}