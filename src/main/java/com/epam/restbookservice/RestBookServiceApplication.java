package com.epam.restbookservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RestBookServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestBookServiceApplication.class, args);
	}

}
