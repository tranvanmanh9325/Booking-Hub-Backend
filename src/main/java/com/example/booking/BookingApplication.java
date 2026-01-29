package com.example.booking;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableCaching
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class BookingApplication {

	public static void main(String[] args) {
		// Bypass check for Java 25 bytecode (version 69)
		System.setProperty("spring.classformat.ignore", "true");

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