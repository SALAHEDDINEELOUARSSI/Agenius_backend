package com.AgeniusAgent.Agenius.repository;

import com.AgeniusAgent.Agenius.entity.CandidatQCM;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidatQCMRepository extends MongoRepository<CandidatQCM, String> {
    List<CandidatQCM> findByScoreGreaterThan(int score);

}
