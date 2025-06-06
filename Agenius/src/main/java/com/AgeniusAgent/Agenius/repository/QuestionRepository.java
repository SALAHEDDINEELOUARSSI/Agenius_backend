package com.AgeniusAgent.Agenius.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.AgeniusAgent.Agenius.entity.Question;

public interface QuestionRepository extends MongoRepository<Question, Long> {
    // Tu peux ajouter des méthodes personnalisées si nécessaire
}
