package com.example.agenius_back;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.example.agenius_back.service.AccountService;
import com.example.agenius_back.entity.AppRole;

import java.util.stream.Stream;

@SpringBootApplication
@EnableDiscoveryClient
public class AgeniusBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgeniusBackApplication.class, args);
    }
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    //@Bean
    // ✅ Méthode CommandLineRunner pour initialiser les rôles et les utilisateurs

    //@Bean
    CommandLineRunner start(AccountService accountService) {
        return args -> {
            // Ajouter les rôles
            accountService.save(new AppRole(null, "USER"));
            accountService.save(new AppRole(null, "ADMIN"));

            // Ajouter les utilisateurs
            Stream.of("user1@gmail.com", "user2@gmail.com", "user3@gmail.com", "admin@gmail.com").forEach(username -> {
                accountService.saveUser(username, "1234abcD$", "1234abcD$");
            });

            // Associer le rôle ADMIN à l'utilisateur admin
            accountService.addRoleToUser("admin", "ADMIN");
        };
    }


}
