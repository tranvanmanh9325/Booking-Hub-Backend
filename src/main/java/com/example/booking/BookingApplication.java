package com.example.booking;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BookingApplication {

	public static void main(String[] args) {
		// Load .env file BEFORE Spring Boot starts
		try {
			Dotenv dotenv = Dotenv.configure()
					.ignoreIfMissing()
					.load();
			
			// Set system properties from .env file
			dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
		} catch (Exception e) {
			System.err.println("Warning: Could not load .env file: " + e.getMessage());
			System.err.println("Continuing with system environment variables and application.properties defaults...");
		}
		
		SpringApplication.run(BookingApplication.class, args);
	}

}