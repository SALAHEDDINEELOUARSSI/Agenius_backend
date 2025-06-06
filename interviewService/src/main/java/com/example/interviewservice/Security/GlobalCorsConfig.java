package com.example.interviewservice.Security;/*package com.AgeniusAgent.Agenius.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class GlobalCorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000")
                        .allowedMethods("*")
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .exposedHeaders("Authorization", "Content-Type", "X-Requested-With","Origin", "Accept", "Access-Control-Request-Method", 
                        "Access-Control-Request-Headers","Access-Control-Allow-Origin", "Access-Control-Allow-Credentials");
            }
        };
    }
}
*/
