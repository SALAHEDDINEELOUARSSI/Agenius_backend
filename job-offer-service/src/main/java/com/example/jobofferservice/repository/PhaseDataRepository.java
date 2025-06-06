package com.example.jobofferservice.repository;

import com.example.jobofferservice.model.PhaseData;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PhaseDataRepository extends MongoRepository<PhaseData, String> {
    int getPhaseStatusByOfferName(String offerName);
    PhaseData findByOfferName(String offerName);

    Boolean existsByOfferName(String offerName);
}

