package com.example.jobofferservice;

import com.example.jobofferservice.repository.JobRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
@EnableDiscoveryClient

@SpringBootApplication
@EnableScheduling
public class JobOfferServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobOfferServiceApplication.class, args);
	}
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	//@Bean




}

