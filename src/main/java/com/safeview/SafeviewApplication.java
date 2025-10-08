package com.safeview;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

// 생략

@EnableJpaAuditing
@SpringBootApplication
public class SafeviewApplication {

	public static void main(String[] args) {

		SpringApplication.run(SafeviewApplication.class, args);
	}
}