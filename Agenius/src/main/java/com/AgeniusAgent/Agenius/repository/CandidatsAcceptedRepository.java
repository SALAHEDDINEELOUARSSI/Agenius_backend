package com.AgeniusAgent.Agenius.repository;

import com.AgeniusAgent.Agenius.entity.CandidatsAccepted;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;


public interface CandidatsAcceptedRepository extends MongoRepository<CandidatsAccepted,String> {
    public CandidatsAccepted findCandidatsSelectedAfterTestById(String id);
    public CandidatsAccepted findByEmail(String email);
    long countByJobName(String jobName);
    void deleteByjobNameAndCreatedBy(String offrename, String createdBy);

    List<CandidatsAccepted> findByJobName(String jobName);
}
