package com.AgeniusAgent.Agenius.repository;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.AgeniusAgent.Agenius.entity.UserResponse;

import java.util.List;
import java.util.Map;


public interface UserResponseRepository extends MongoRepository<UserResponse, String> {
    List<UserResponse> findByScoreGreaterThan(int score);
    List<UserResponse> findByScoreGreaterThanAndOffrename(int score, String offrename);

    List<UserResponse> findAllByOrderByScoreDesc();

    List<UserResponse> findByOffrename(String jobName);
    List<UserResponse> findByOffrenameAndEmail(String jobName,String email);
    @Aggregation(pipeline = {
            "{ '$match': { 'offrename': ?0 } }",
            "{ '$group': { '_id': null, 'averageScore': { '$avg': '$score' } } }"
    })
    Double findAverageScoreByOffreName(String offreName);

}