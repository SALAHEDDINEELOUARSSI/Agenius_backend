package com.example.interviewservice.Service;

import com.example.interviewservice.Model.CandidatsAccepted;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
@Service
public class CandidasAcceptedService {



    private final RestTemplate restTemplate;

    @Autowired
    public CandidasAcceptedService(RestTemplate loadBalancedRestTemplate) {
        this.restTemplate = loadBalancedRestTemplate;
    }


    public List<CandidatsAccepted> findAll(String offrename) {
        ResponseEntity<CandidatsAccepted[]> response = restTemplate.exchange(
                "http://agentia/user/api/getCandidatsAccepted/"+offrename,
                HttpMethod.GET,
                null,
                CandidatsAccepted[].class
        );
        return Arrays.asList(response.getBody());
    }
}
