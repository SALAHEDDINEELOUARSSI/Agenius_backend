package com.AgeniusAgent.Agenius;

import com.AgeniusAgent.Agenius.entity.UserResponse;
import com.AgeniusAgent.Agenius.repository.UserResponseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@EnableDiscoveryClient

@SpringBootApplication
@EnableRabbit
public class AgeniusApplication {

	public static void main(String[] args) {
		SpringApplication.run(AgeniusApplication.class, args);
	}
	@Bean
	public CommandLineRunner seedUserResponses(UserResponseRepository repository) {
		return args -> {
			repository.deleteAll(); // Nettoyer la collection

			String[] fakeNames = {
					"alice", "bob", "carol", "dave", "eve",
					"frank", "grace", "heidi", "ivan", "judy"
			};

			Random random = new Random();

			for (int i = 0; i < 10; i++) {
				UserResponse user = new UserResponse();
				user.setEmail(fakeNames[i] + "@example.com");
				user.setPhone("+212628520449" );
				user.setOffrename("Software Engineer");

				Map<String, Integer> responses = new HashMap<>();
				int score = 0;

				for (int q = 1; q <= 10; q++) {
					int result = random.nextBoolean() ? 10 : 0;
					responses.put("q" + q, result);
					score += result;
				}

				user.setResponses(responses);
				user.setScore(score);

				repository.save(user);
			}

			System.out.println("✅ 10 utilisateurs avec réponses QCM enregistrés.");
		};

}


	}

