package com.amouri_coding.FitGear;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FitGearApplication {

	public static void main(String[] args) {
		SpringApplication.run(FitGearApplication.class, args);
	}

}
