package com.example.learning_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Map;

@SpringBootApplication
@EnableAsync
public class LearningApiApplication {

	public static void main(String[] args) {

		SpringApplication.run(LearningApiApplication.class, args);
		System.out.println("Hello World");
	}

}