package com.AgeniusAgent.Agenius.repository;

import com.AgeniusAgent.Agenius.entity.CandidatsSelected;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface CandidatsSelectedRepository extends MongoRepository<CandidatsSelected,String> {
     public CandidatsSelected findCandidatsSelectedById(String id);
     public CandidatsSelected findByEmail(String email);
     public CandidatsSelected findByName(String name);
     public  List<CandidatsSelected> findByOffresName(String offresName);
     void deleteByOffresNameAndOffresCreatedBy(String offrename, String createdBy);



     public String getNameByEmail(String email);

}


