package com.amouri_coding.FitGear;

import com.amouri_coding.FitGear.role.UserRole;
import com.amouri_coding.FitGear.role.UserRoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableAsync
public class FitGearApplication {

	public static void main(String[] args) {
		SpringApplication.run(FitGearApplication.class, args);

	}

	@Bean
	public CommandLineRunner runner(UserRoleRepository roleRepository) {
		return args -> {
			if (roleRepository.findByName("COACH").isEmpty()) {
				roleRepository.save(UserRole.builder().name("COACH").build());
			}

			if (roleRepository.findByName("CLIENT").isEmpty()) {
				roleRepository.save(UserRole.builder().name("CLIENT").build());
			}
		};
	}

}
