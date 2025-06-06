package com.example.interviewservice.Config;

import com.example.interviewservice.intercepteur.JwtRestTemplateInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;


@Configuration

public class AppConfig {
    @Autowired
    private JwtRestTemplateInterceptor jwtInterceptor;


    @Bean
    @LoadBalanced
    public RestTemplate loadBalancedRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }

    // Pour les appels vers les APIs externes (ex. Google)
    @Bean(name = "externalRestTemplate")
    public RestTemplate externalRestTemplate() {
        return new RestTemplate();
    }
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

