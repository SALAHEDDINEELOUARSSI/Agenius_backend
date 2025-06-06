package com.example.jobofferservice.repository;

import com.example.jobofferservice.model.Job;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface JobRepository extends MongoRepository<Job, String> {
public Job getJobByName(String name);
    List<Job> findByCreatedBy(String createdBy);
    Job findByName(String name);
}

