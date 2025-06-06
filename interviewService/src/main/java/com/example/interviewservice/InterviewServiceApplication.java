package com.example.interviewservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient

@SpringBootApplication
public class InterviewServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InterviewServiceApplication.class, args);
    }

}
